package me.lovegao.gdao.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import me.lovegao.gdao.bean.SystemConstant;

public class ConnectionGetor {

	public static Connection createConnection(Properties properties) throws Exception {
		Connection conn = null;
		String driverName = properties.getProperty(SystemConstant.STR_DRIVER_NAME);
		String url = properties.getProperty(SystemConstant.STR_CONNECTION_URL);
		String userName = properties.getProperty(SystemConstant.STR_USER_NAME);
		String passwd = properties.getProperty(SystemConstant.STR_USER_PASSWORD);
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(url, userName, passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

}
