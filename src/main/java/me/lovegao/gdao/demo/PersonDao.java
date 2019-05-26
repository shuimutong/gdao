package me.lovegao.gdao.demo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PersonDao {
	private DBUtil util = new DBUtil();
	
	public List<Person> pageQuery(int start, int end) {
		String sql = "select * from (select p.*, rownum rn from (select * from person order by age) p where rownum<=?) where rn>?";
		ResultSet rs = null;
		List<Object> param = new ArrayList<Object>();
		param.add(end);
		param.add(start);
		List<Person> pList = new ArrayList<Person>();
		util.getConnection();
		rs = util.query(sql, param);
		try {
			while(rs.next()) {
				Person p = new Person();
				p.setId(rs.getString("id"));
				p.setName(rs.getString("name"));
				p.setGender(rs.getString("gender"));
				p.setAge(rs.getInt("age"));
				pList.add(p);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			util.close();
		}
		return pList;
	}
	
	public int getRecordCount() {
		String sql = "select count(*) c from person";
		ResultSet rs = null;
		int recordCount = 0;
		util.getConnection();
		rs = util.query(sql, null);
		try {
			while(rs.next()) {
				recordCount = rs.getInt("c");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			util.close();
		}
		
		return recordCount;
	}
}
