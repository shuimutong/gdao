package me.lovegao.gdao.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.lovegao.gdao.bean.SystemConstant;
import me.lovegao.gdao.util.ConnectionUtil;

/**
 * 第二版简易连接池实现<br/>
 * 主要增加连接有效性检测，包括归还连接时检测，定时检测连接
 * 
 * @author simple
 *
 */
public class SimpleV2ConnectionPool extends SimpleConnectionPool {
	private final static Logger log = LoggerFactory.getLogger(SimpleV2ConnectionPool.class);
	private ExecutorService ES;
	// 归还连接时检测连接，这步最好做成异步的，避免影响归还速度
	private boolean checkConnectionWhenReturn = false;
	// 连接检测语句
	private String checkConnectionValidationQuery;
	// 定时检测连接的时间（分钟）
	private int periodCheckConnectionTimeMin;
	/** 待检测连接 **/
	private Queue<Connection> TO_CHECK_CONNECTION_POOL;
	//查询超时时间
	private final int QUERY_TIMEOUT_SECONDS;
	//连接泄露检测
	private boolean checkConnectionLeak = false;
	//连接泄露检测间隔时长-分钟
	private int checkConnectionLeakPeriodTimeMin = 30;
	//强制归还连接时长（小时）
	private double forceReturnConnectionTimeHour;
	//连接最大空闲时长（小时）
//	private double connectionMaxIdleTimeHour;
	/**连接最后借出时间**/
	private final Map<Integer, Long> CONNECTION_OUT_TIME_MAP_POOL = new ConcurrentHashMap();

	public SimpleV2ConnectionPool(Properties properties) throws Exception {
		super(properties);
		QUERY_TIMEOUT_SECONDS = super.getQueryTimeoutSecond();
		//连接有效性检测配置
		if (properties.containsKey(SystemConstant.STR_CHECK_CONNECTION_VALIDATION_QUERY)) {
			checkConnectionValidationQuery = properties
					.getProperty(SystemConstant.STR_CHECK_CONNECTION_VALIDATION_QUERY);
			String checkWhenReturn = properties.getProperty(SystemConstant.STR_CHECK_CONNECTION_WHEN_RETURN);
			if (checkWhenReturn.toLowerCase().equals("true")) {
				checkConnectionWhenReturn = true;
			}
			if (properties.containsKey(SystemConstant.STR_PERIOD_CHECK_CONNECTION_TIME_MIN)) {
				String periodTimeStr = properties.getProperty(SystemConstant.STR_PERIOD_CHECK_CONNECTION_TIME_MIN);
				if (StringUtils.isNumeric(periodTimeStr)) {
					periodCheckConnectionTimeMin = Integer.parseInt(periodTimeStr);
				}
			}
		}
		//连接泄露检测配置
		if (properties.containsKey(SystemConstant.STR_CONNECTION_LEAK_CHECK)) {
			String leakCheckStr = properties.getProperty(SystemConstant.STR_CONNECTION_LEAK_CHECK);
			if (leakCheckStr.toLowerCase().equals("true")) {
				String leakCheckPeriodTimeStr = properties.getProperty(SystemConstant.STR_CONNECTION_LEAK_CHECK_PERIOD_TIME_MIN);
				if (StringUtils.isNumeric(leakCheckPeriodTimeStr)) {
					checkConnectionLeakPeriodTimeMin = Integer.parseInt(leakCheckPeriodTimeStr);
				}
				String forceReturnTimeStr = properties.getProperty(SystemConstant.STR_FORCE_RETURN_CONNECTION_TIME_HOUR);
				if (NumberUtils.isNumber(forceReturnTimeStr)) {
					forceReturnConnectionTimeHour = Double.parseDouble(forceReturnTimeStr);
					if(forceReturnConnectionTimeHour > 0) {
						checkConnectionLeak = true;
					}
				}
				//最大空闲检测，功能上和定时检测连接重复，暂时不开发。
//				String connectionMaxIdleTimeStr = properties.getProperty(SystemConstant.STR_CONNECTION_MAX_IDLE_TIME_HOUR);
//				if (NumberUtils.isNumber(connectionMaxIdleTimeStr)) {
//					connectionMaxIdleTimeHour = Double.parseDouble(connectionMaxIdleTimeStr);
//				}
				//需要同时配置（强制归还时间）才能检测连接泄露
				if(forceReturnConnectionTimeHour > 0) {
					checkConnectionLeak = true;
				}
			}
		}
		StringBuilder infoSb = new StringBuilder();
		infoSb.append("SimpleV2ConnectionPoolInitDone------")
			.append(",checkConnectionValidationQuery:").append(checkConnectionValidationQuery)
			.append(",checkConnectionWhenReturn:").append(checkConnectionWhenReturn)
			.append(",periodCheckConnectionTimeMin:").append(periodCheckConnectionTimeMin)
			.append(",checkConnectionLeak:").append(checkConnectionLeak)
			.append(",checkConnectionLeakPeriodTimeMin:").append(checkConnectionLeakPeriodTimeMin)
			.append(",forceReturnConnectionTimeHour:").append(forceReturnConnectionTimeHour);
		System.out.println(infoSb.toString());
		initCheck();
	}

	@Override
	public Connection getConnection() throws Exception {
		Connection conn = super.getConnection();
		if(checkConnectionLeak) {
			int connHashCode = conn.hashCode();
			CONNECTION_OUT_TIME_MAP_POOL.put(connHashCode, System.currentTimeMillis());
		}
		return conn;
	}

	@Override
	public void returnConnection(Connection conn) {
		if(checkConnectionLeak) {
			CONNECTION_OUT_TIME_MAP_POOL.remove(conn.hashCode());
		}
		if (checkConnectionWhenReturn) {
			TO_CHECK_CONNECTION_POOL.add(conn);
			notifyAll();
		} else {
			superReturnConnection(conn);
		}
	}

	private void superReturnConnection(Connection conn) {
		super.returnConnection(conn);
	}
	
	private Connection superGetByConnectionHashCode(int hashCode) {
		return super.getByConnectionHashCode(hashCode);
	}

	// 初始化检查
	private void initCheck() {
		int threadPoolSize = 0;
		if(checkConnectionWhenReturn) {
			threadPoolSize += 2;
		}
		if(periodCheckConnectionTimeMin > 0) {
			threadPoolSize++;
		}
		//需要同时配置（强制归还时间、最大空闲时间）才能检测连接泄露
		if(checkConnectionLeak) {
			threadPoolSize++;
		}
		if(threadPoolSize > 0) {
			ES = Executors.newFixedThreadPool(threadPoolSize);
			// 检查归还连接
			if(checkConnectionWhenReturn) {
				//启两个线程同时检测
				for(int i=0; i<2; i++) {
					ES.execute(new ReturnConnectionCheck());
				}
			}
			//定时检测连接
			if (periodCheckConnectionTimeMin > 0) {
				ES.execute(new ConnectionPeriodCheck());
			}
			//连接泄露检测
			if(checkConnectionLeak) {
				ES.execute(new ConnectionLeakCheck());
			}
		}
	}

	/**
	 * 连接泄露定时检测
	 * @author simple
	 *
	 */
	class ConnectionLeakCheck implements Runnable {
		int sleepTimeMs = checkConnectionLeakPeriodTimeMin * 60 * 1000;
		@Override
		public void run() {
			Set<Integer> preConnHashCodeSet = new HashSet();
			while (true) {
				try {
					Thread.sleep(sleepTimeMs);
				} catch (Exception e) {
					log.error("ConnectionLeakCheckSleepException", e);
				}
				try {
					checkConnectionLeak(preConnHashCodeSet);
				} catch (Exception e) {
					log.error("ConnectionLeakCheckException", e);
				}
			}
		}
	}
	
	//检测连接泄露
	private void checkConnectionLeak(Set<Integer> preConnHashCodeSet) throws Exception {
		if(CONNECTION_OUT_TIME_MAP_POOL.size() < 1) {
			preConnHashCodeSet = new HashSet();
		} else {
			Iterator<Entry<Integer, Long>> connHashCodeIt = CONNECTION_OUT_TIME_MAP_POOL.entrySet().iterator();
			//先对比前后两次的连接，如果有相同的，再检测相同的连接
			if(preConnHashCodeSet.size() == 0) {
				while(connHashCodeIt.hasNext()) {
					preConnHashCodeSet.add(connHashCodeIt.next().getKey());
				}
			} else {
				StringBuilder logSb = new StringBuilder();
				long timeFlag = (long) (System.currentTimeMillis() - forceReturnConnectionTimeHour * 3600 * 1000);
				logSb.append("ConnectionLeakCheck---")
					.append(",timeFlag:").append(timeFlag)
					.append(",forceReturnConnectionTimeHour:").append(forceReturnConnectionTimeHour);
				List<Integer> toCloseConnectionHashCodeList = new ArrayList();
				logSb.append(",toCloseConn,{");
				while(connHashCodeIt.hasNext()) {
					Entry<Integer, Long> connEntry = connHashCodeIt.next();
					int connHashCode = connEntry.getKey();
					if(preConnHashCodeSet.contains(connHashCode) && connEntry.getValue() < timeFlag) {
						toCloseConnectionHashCodeList.add(connHashCode);
						logSb.append(connHashCode).append(":").append(timeFlag).append(",");
					}
				}
				logSb.append("}");
				if(toCloseConnectionHashCodeList.size() > 0) {
					for(Integer connHashCode : toCloseConnectionHashCodeList) {
						Connection conn = superGetByConnectionHashCode(connHashCode);
						if(conn != null) {
							try {
								conn.close();
							} catch (SQLException e) {
								log.error("closeConnectionException", e);
							}
							superReturnConnection(conn);
						}
					}
				}
				log.info(logSb.toString());
				//进行过一次检测之后，对之前存储的进行初始化
				preConnHashCodeSet = new HashSet();
			}
		}
	}
	
	/**
	 * 归还连接检测
	 * @author simple
	 *
	 */
	class ReturnConnectionCheck implements Runnable {
		@Override
		public void run() {
			while (true) {
				Connection toCheckConn = TO_CHECK_CONNECTION_POOL.poll();
				if (toCheckConn == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						log.error("checkReturnConnectionWaitException", e);
					}
				} else {
					boolean canUse = ConnectionUtil.isValidConnection(toCheckConn, checkConnectionValidationQuery,
							QUERY_TIMEOUT_SECONDS);
					if (!canUse) {
						try {
							toCheckConn.close();
						} catch (SQLException e) {
							log.error("checkReturnConnectionCloseConnException", e);
						}
					}
					superReturnConnection(toCheckConn);
				}
			}
		}
	}
	
	/**
	 * 连接定时检测
	 * @author simple
	 *
	 */
	class ConnectionPeriodCheck implements Runnable {
		int sleepTimeMs = periodCheckConnectionTimeMin * 60 * 1000;
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(sleepTimeMs);
				} catch (Exception e) {
					log.error("checkReturnConnectionSleepException", e);
				}
				Connection toCheckConn = null;
				try {
					toCheckConn = getConnection();
					if (toCheckConn != null) {
						boolean canUse = ConnectionUtil.isValidConnection(toCheckConn,
								checkConnectionValidationQuery, QUERY_TIMEOUT_SECONDS);
						if (!canUse) {
							toCheckConn.close();
						}
					}
				} catch (Exception e) {
					log.error("checkReturnConnectionWaitException", e);
				} finally {
					if (toCheckConn != null) {
						superReturnConnection(toCheckConn);
					}
				}
			}
		}
	}
}
