package me.lovegao.gdao.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;

import me.lovegao.gdao.connection.IConnectionPool;
import me.lovegao.gdao.connection.SimpleConnectionPool;
import me.lovegao.gdao.sqlexecute.ISqlExecutor;
import me.lovegao.gdao.sqlexecute.SimpleSqlExecutor;

public class SqlExecutorTest {

	public static void main(String[] args) throws Exception {
		Properties prop = new Properties();
		String dbFileName = "/DatabasePropertiesDemo.properties";
		prop.load(SqlExecutorTest.class.getResourceAsStream(dbFileName));
		System.out.println(JSONObject.toJSONString(prop));
		IConnectionPool connectionPool = new SimpleConnectionPool(prop);
		
		ISqlExecutor sqlExecutor = new SimpleSqlExecutor(connectionPool);
		
		String insertSql = "insert into t_user(name, age) value(?, ?)";
		Object[] params = {"a", 1};
		long addReturn = sqlExecutor.insert(insertSql, params);
		System.out.println("addReturn:" + addReturn);
		
		String updateSql = "update t_user set age=? where name=?";
		Object[] params1 = {2, "a"};
		int updateReturn = sqlExecutor.update(updateSql, params1);
		System.out.println("updateReturn:" + updateReturn);
		
		List<Object[]> batchAddList = new ArrayList();
		batchAddList.add(new Object[] {"b", 2});
		batchAddList.add(new Object[] {"c", 3});
		batchAddList.add(new Object[] {"d", 4});
		int[] batchReturn = sqlExecutor.insertOrUpdateBatch(insertSql, batchAddList);
		System.out.println("batchReturn:" + JSONObject.toJSONString(batchReturn));
		
	}

}
