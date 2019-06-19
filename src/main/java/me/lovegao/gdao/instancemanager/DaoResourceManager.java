package me.lovegao.gdao.instancemanager;

import java.sql.Connection;
import java.util.Properties;

import me.lovegao.gdao.connection.ConnectionGetor;
import me.lovegao.gdao.connection.IConnectionGetor;
import me.lovegao.gdao.connection.IConnectionPool;
import me.lovegao.gdao.connection.SimpleConnectionPool;
import me.lovegao.gdao.sqlexecute.IManulTransactionSqlExecutor;
import me.lovegao.gdao.sqlexecute.SimpleSqlExecutor;

/**
 * dao资源初始化
 * @author simple
 *
 */
public class DaoResourceManager {
	private IConnectionPool connectionPool;
	private IManulTransactionSqlExecutor sqlExecutor;
	
	/**
	 * 构造方法（一个数据库创建一个实例即可）
	 * @param prop 数据库配置（示例配置：/src/main/resources/DatabaseDemo.properties）
	 * @throws Exception
	 */
	public DaoResourceManager(Properties prop) throws Exception {
		connectionPool = new SimpleConnectionPool(prop);
		sqlExecutor = new SimpleSqlExecutor(connectionPool);
	}
	
	/**
	 * 获取sql执行器
	 * @return
	 */
	public IManulTransactionSqlExecutor getSqlExecutor() {
		return sqlExecutor;
	}
	
	/**
	 * 获取连接池
	 * @return
	 */
	public IConnectionPool getConnectionPool() {
		return connectionPool;
	}
}
