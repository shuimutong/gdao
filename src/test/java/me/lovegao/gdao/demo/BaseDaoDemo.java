package me.lovegao.gdao.demo;

import java.io.Serializable;
import java.util.Properties;

import me.lovegao.gdao.connection.IConnectionPool;
import me.lovegao.gdao.connection.SimpleConnectionPool;
import me.lovegao.gdao.orm.AbstractBaseDao;
import me.lovegao.gdao.sqlexecute.ISqlExecutor;
import me.lovegao.gdao.sqlexecute.SimpleSqlExecutor;

public class BaseDaoDemo<T, PK extends Serializable> extends AbstractBaseDao<T, PK> {

	@Override
	public ISqlExecutor getSqlExecutor() {
		ISqlExecutor sqlExecutor = null;
		Properties prop = new Properties();
		String confPath = "/mysql.properties";
		try {
			prop.load(UserDaoTest.class.getResourceAsStream(confPath));
			IConnectionPool connectionPool = new SimpleConnectionPool(prop);
			sqlExecutor = new SimpleSqlExecutor(connectionPool);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlExecutor;
	}
	
}
