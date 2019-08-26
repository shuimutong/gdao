package me.lovegao.gdao.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 连接util
 * @author simple
 *
 */
public class ConnectionUtil {
	//检测连接是否正常
	public static boolean isValidConnection(Connection conn, String querySql, int queryTimeoutSeconds) {
		Statement pingStatement = null;
		try {
			pingStatement = conn.createStatement();
			pingStatement.setQueryTimeout(queryTimeoutSeconds);
			pingStatement.executeQuery(querySql).close();
			return true;
		} catch (SQLException sqlEx) {
			return false;
		} finally {
			if (pingStatement != null) {
				JDBCUtil.closeStatement(pingStatement);
			}
		}
	}
}
