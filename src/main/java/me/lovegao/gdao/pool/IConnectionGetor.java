package me.lovegao.gdao.pool;

import java.sql.Connection;
import java.util.Properties;

/**
 * 连接获取接口
 * @author simple
 *
 */
public interface IConnectionGetor {
	/**
	 * 创建连接
	 * @return
	 */
	Connection createConnection(Properties properties) throws Exception;
}
