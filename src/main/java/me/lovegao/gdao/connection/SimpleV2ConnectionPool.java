package me.lovegao.gdao.connection;

import java.sql.Connection;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
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
	// 定时检测连接的时间-秒
	private int periodCheckConnectionTime;
	/** 待检测连接 **/
	private Queue<Connection> TO_CHECK_CONNECTION_POOL;
	private final int QUERY_TIMEOUT_SECONDS;

	public SimpleV2ConnectionPool(Properties properties) throws Exception {
		super(properties);
		QUERY_TIMEOUT_SECONDS = super.getQueryTimeoutSecond();
		if (properties.containsKey(SystemConstant.STR_CHECK_CONNECTION_VALIDATION_QUERY)) {
			checkConnectionValidationQuery = properties
					.getProperty(SystemConstant.STR_CHECK_CONNECTION_VALIDATION_QUERY);
			String checkWhenReturn = properties.getProperty(SystemConstant.STR_CHECK_CONNECTION_WHEN_RETURN);
			if (checkWhenReturn.toLowerCase().equals("true")) {
				checkConnectionWhenReturn = true;
			}
			if (properties.containsKey(SystemConstant.STR_PERIOD_CHECK_CONNECTION_TIME)) {
				String periodTimeStr = properties.getProperty(SystemConstant.STR_PERIOD_CHECK_CONNECTION_TIME);
				if (StringUtils.isNumeric(periodTimeStr)) {
					periodCheckConnectionTime = Integer.parseInt(periodTimeStr);
				}
			}
			initCheck();
		}
	}

	@Override
	public Connection getConnection() throws Exception {
		return super.getConnection();
	}

	@Override
	public void returnConnection(Connection conn) {
		if (checkConnectionWhenReturn) {
			TO_CHECK_CONNECTION_POOL.add(conn);
			notifyAll();
		} else {
			super.returnConnection(conn);
		}
	}

	private void superReturnConnection(Connection conn) {
		super.returnConnection(conn);
	}

	// 初始化检查
	private void initCheck() {
		if (checkConnectionWhenReturn || periodCheckConnectionTime > 0) {
			ES = Executors.newFixedThreadPool(2);
			TO_CHECK_CONNECTION_POOL = new ConcurrentLinkedQueue();
			if (checkConnectionWhenReturn) {
				checkReturnConnection();
			}
			if (periodCheckConnectionTime > 0) {
				periodCheckConnection();
			}
		}
	}

	// 检查归还连接
	private void checkReturnConnection() {
		ES.execute(new Runnable() {

			@Override
			public void run() {
				while (true) {
					Connection toCheckConn = TO_CHECK_CONNECTION_POOL.poll();
					if (toCheckConn != null) {
						boolean canUse = ConnectionUtil.isValidConnection(toCheckConn, checkConnectionValidationQuery,
								QUERY_TIMEOUT_SECONDS);
						returnConnection(toCheckConn);
					} else {
						try {
							wait();
						} catch (InterruptedException e) {
							log.error("checkReturnConnectionWaitException", e);
						}
					}
				}

			}
		});
	}

	private void periodCheckConnection() {
		ES.execute(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(periodCheckConnectionTime * 1000);
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
		});
	}
}
