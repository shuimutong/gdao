package me.lovegao.gdao.pool;

import java.sql.Connection;
import java.util.Properties;

public class DaoInit {
	private Properties properties;
	
	public DaoInit(Properties properties) {
		this.properties = properties;
	}
	
	ISqlExecutor getSqlExecutor() throws Exception {
//		ISqlExecutor sqlExecutor = new SimpleSqlExecutor();
		return null;
	}
	
}
