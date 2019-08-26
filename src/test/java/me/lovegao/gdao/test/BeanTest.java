package me.lovegao.gdao.test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.alibaba.fastjson.JSONObject;

import me.lovegao.gdao.connection.IConnectionPool;
import me.lovegao.gdao.connection.SimpleV2ConnectionPool;

public class BeanTest {

	public static void main(String[] args) throws Exception {
		Person p = new Person();
		PropertyDescriptor pd = new PropertyDescriptor("name", Person.class);
		Method m1 = pd.getWriteMethod();
		m1.invoke(p, "jack");
		
		Method m2 = pd.getReadMethod();
		String name = (String) m2.invoke(p);
		System.out.println(name);
		
		JSONObject.parseObject("t", Person.class);
	}
	
	public static void extendTest() throws Exception {
		IConnectionPool pool = new SimpleV2ConnectionPool(null);
	}

}
