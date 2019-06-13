package me.lovegao.gdao.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ConnectionTest {

	public static void main(String[] args) {
//		insertPs();
//		queryPs();
		insertAndGetId();
	}
	
	public static void query() {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stat = conn.createStatement();
			String sql = "select id, name from user;";
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()) {
//				System.out.println(rs.getLong(1) + "--" + rs.getString(2));
				System.out.println(rs.getLong("id") + "--" + rs.getString("name"));
			}
			rs.close();
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void queryPs() {
		Connection conn = null;
		try {
			conn = getConnection();
			StringBuilder sql = new StringBuilder("select id, name from user where id in (");
			int[] ids = new int[] {1,2,3,4};
			for(int i=0; i<ids.length; i++) {
				sql.append("?,");
			}
			String tmpSql = sql.subSequence(0, sql.length()-1) + ")";
			System.out.println(tmpSql);
			PreparedStatement ps = conn.prepareStatement(tmpSql);
			for(int i=0; i<ids.length; i++) {
				ps.setObject(i+1, ids[i]);
			}
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			String[] columnNames = new String[columnCount];
			for(int i=0; i<columnCount; i++) {
				columnNames[i] = metaData.getColumnName(i+1);
			}
			List<Map<String, Object>> list = new ArrayList();
			while(rs.next()) {
				Map<String, Object> queryValMap = new HashMap();
				for(int i=0; i<columnNames.length; i++) {
					queryValMap.put(columnNames[i], rs.getObject(columnNames[i]));
				}
				list.add(queryValMap);
			}
			System.out.println(JSONObject.toJSONString(list));
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void insertPs() {
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "insert into user(id,name) value(?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			Object[] params = new Object[] {4, "Hello,ps"};
			for(int i=0; i<params.length; i++) {
				ps.setObject(i+1, params[i]);
			}
			boolean flag = ps.execute();
			System.out.println(flag);
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void insertTransaction() {
		Connection conn = null;
		try {
			conn = getConnection();
			
			conn.setAutoCommit(false);
			conn.rollback();
			conn.commit();
			
			String sql = "insert into user(id,name) value(?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			Object[] params = new Object[] {4, "Hello,ps"};
			for(int i=0; i<params.length; i++) {
				ps.setObject(i+1, params[i]);
			}
			boolean flag = ps.execute();
			System.out.println(flag);
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void insertAndGetId() {
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "insert into t_user(name, age) value(?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			Object[] params = new Object[] {"james", 17};
			for(int i=0; i<params.length; i++) {
				ps.setObject(i+1, params[i]);
			}
			int num = ps.executeUpdate();
			System.out.println("num:" + num);
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()) {
				long id = rs.getLong(1);
				System.out.println("id:" + id);
			}
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void insert() {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stat = conn.createStatement();
			String sql = "insert into user(id,name) value(2, 'hello,mysql');";
			stat.execute(sql);
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Connection getConnection() {
		Connection conn = null;
		String driverName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/simple?useServerPrepStmts=false&rewriteBatchedStatements=true&connectTimeout=1000&useUnicode=true&characterEncoding=utf-8";
		String userName = "simple";
		String passwd = "123456";
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(url, userName, passwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

}
