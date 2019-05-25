package me.lovegao.gdao.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DBUtil {
	private Connection conn = null;
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:orcl";
	private String user = "sunxun";
	private String password = "123";
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	
	public void getConnection(){
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,user,password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet query(String sql,List<Object> param){
		try {
			ps = conn.prepareStatement(sql);
			if(param!=null && param.size()>0){
				for(int i=0;i<param.size();i++){
					ps.setObject(i+1, param.get(i));
				}
			}
			rs =  ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
		
	}
	
	public void update(String sql, List<Object> param){
		try {
			ps = conn.prepareStatement(sql);
			if(param!=null && param.size()>0){
				for(int i=0;i<param.size();i++){
					ps.setObject(i+1, param.get(i));
				}
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			if(rs != null){
				rs.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(conn != null){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
