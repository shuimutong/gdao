package me.lovegao.gdao.util;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.test.TUser;

public class GDaoCommonUtilTest {
	@Test
	public void parseClass() {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		System.out.println(JSONObject.toJSONString(classParseInfo));
		Assert.assertTrue(classParseInfo != null);
		Assert.assertTrue(classParseInfo.getPkName().equals("id"));
		Assert.assertTrue(classParseInfo.getTableColumnNames().length == classParseInfo.getFields().length);
		
	}
	
	//测试setAccessible是不是一直生效的
	@Test
	public void accessableTest() throws Exception {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		TUser user = new TUser();
		Field pkField = classParseInfo.getPkField();
		pkField.setAccessible(true);
		pkField.set(user, 1L);
		System.out.println("user,id:" + user.getId());
		
		TUser user1 = new TUser();
		pkField.set(user1, 2L);
		System.out.println("user1,id:" + user1.getId());
		
		TUser user2 = new TUser();
		pkField.set(user2, 3L);
		System.out.println("user2,id:" + user2.getId());
	}
}
