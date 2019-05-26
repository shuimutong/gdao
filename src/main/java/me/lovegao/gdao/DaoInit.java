package me.lovegao.gdao;

import java.util.Properties;

import me.lovegao.gdao.sqlexecute.ISqlExecutor;

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
