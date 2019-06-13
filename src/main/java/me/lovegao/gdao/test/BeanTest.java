package me.lovegao.gdao.test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.alibaba.fastjson.JSONObject;

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

}
