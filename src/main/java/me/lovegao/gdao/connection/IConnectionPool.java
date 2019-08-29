package me.lovegao.gdao.connection;

import java.sql.Connection;

/**
 * 连接池
 * @author simple
 *
 */
public interface IConnectionPool {
	/**
	 * 获取连接
	 * @return
	 */
	Connection getConnection() throws Exception;
	/**
	 * 归还连接
	 * @param conn
	 */
	void returnConnection(Connection conn);
	
	/**
	 * 获取查询超时时间
	 * @return
	 */
	int getQueryTimeoutSecond();
	
	/**
	 * 关闭连接池
	 */
	void closeConnectionPool();
}
