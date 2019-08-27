package me.lovegao.gdao.connection;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.lovegao.gdao.bean.SystemConstant;

public class SimpleConnectionPool implements IConnectionPool {
	private final static Logger log = LoggerFactory.getLogger(SimpleConnectionPool.class);
	/**配置**/
	private Properties properties;
	/**保存连接的map**/
	private final Map<Integer, Connection> CONNECTION_MAP_POOL = new ConcurrentHashMap();
	/**连接池的连接索引**/
	private final Queue<Integer> CONNECTION_KEY_POOL = new ConcurrentLinkedQueue();
	/**连接池初始连接数量**/
	private int POOL_INIT_NUM;
	/**连接池最大连接数量**/
	private int POOL_MAX_NUM;
	/**已创建连接数量**/
	private AtomicInteger POOL_CREATE_NUM = new AtomicInteger(0);
	/**查询超时时间-秒**/
	private int QUERY_TIMEOUT_SECONDS;
	
	public SimpleConnectionPool(Properties properties) throws Exception {
		this.properties = properties;
		this.POOL_INIT_NUM = Integer.parseInt(properties.getProperty(SystemConstant.STR_INIT_CONNECTION_NUM));
		this.POOL_MAX_NUM = Integer.parseInt(properties.getProperty(SystemConstant.STR_MAX_CONNECTION_NUM));
		this.QUERY_TIMEOUT_SECONDS = Integer.parseInt(properties.getProperty(SystemConstant.STR_QUERY_TIME));
		for(int i=0; i<POOL_INIT_NUM; i++) {
			POOL_CREATE_NUM.incrementAndGet();
			Connection conn = ConnectionGetor.createConnection(properties);
			CONNECTION_MAP_POOL.put(conn.hashCode(), conn);
			CONNECTION_KEY_POOL.add(conn.hashCode());
		}
	}
	
	@Override
	public Connection getConnection() throws Exception {
		Connection conn = null;
		Integer connKey = CONNECTION_KEY_POOL.poll();
		if(connKey == null) {
			if(POOL_CREATE_NUM.intValue() < POOL_MAX_NUM) {
				int poolNum = POOL_CREATE_NUM.incrementAndGet();
				if(poolNum <= POOL_MAX_NUM) {
					conn = ConnectionGetor.createConnection(properties);
					CONNECTION_MAP_POOL.put(conn.hashCode(), conn);
				} else {
					POOL_CREATE_NUM.decrementAndGet();
				}
			}
		} else {
			conn = CONNECTION_MAP_POOL.get(connKey);
		}
		//没有获取到连接
		if(conn == null) {
			throw new NullPointerException("连接池连接用完");
		}
		return conn;
	}
	
	@Override
	public void returnConnection(Connection conn) {
		if(conn != null) {
			try {
				if(conn.isClosed()) {
					CONNECTION_MAP_POOL.remove(conn.hashCode());
					POOL_CREATE_NUM.decrementAndGet();
				} else {
					CONNECTION_KEY_POOL.add(conn.hashCode());
				}
			} catch (Exception e) {
				log.error("returnConnectionException", e);
			}
		}
	}

	@Override
	public int getQueryTimeoutSecond() {
		return QUERY_TIMEOUT_SECONDS;
	}

	Connection getByConnectionHashCode(int hashCode) {
		return CONNECTION_MAP_POOL.get(hashCode);
	}
}
