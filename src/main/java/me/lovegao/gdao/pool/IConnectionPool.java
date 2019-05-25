package me.lovegao.gdao.pool;

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
}
