package me.lovegao.gdao.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCUtil {
	private final static Logger log = LoggerFactory.getLogger(JDBCUtil.class);
	public static void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("closeResultSetException", e);
			}
		}
	}
	
	public static void closePreparedStatement(PreparedStatement ps) {
		if(ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				log.error("closePreparedStatementException", e);
			}
		}
	}
}
