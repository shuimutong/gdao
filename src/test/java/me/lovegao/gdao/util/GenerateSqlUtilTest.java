package me.lovegao.gdao.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.bean.SqlValueResult;
import me.lovegao.gdao.bean.TwoTuple;
import me.lovegao.gdao.test.TUser;
import me.lovegao.gdao.test.TUser2;

public class GenerateSqlUtilTest {
	@Test
	public void addSql() throws Exception {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		TUser user = new TUser();
		user.setId(1L);
		user.setAge(18);
		user.setName("jack");
		SqlValueResult sqlResult = GenerateSqlUtil.addSql1(classParseInfo, user);
		System.out.println(JSONObject.toJSONString(sqlResult));
		Assert.assertTrue(sqlResult.getParamList().size() == 2);
		Assert.assertTrue(sqlResult.getSql() != null && sqlResult.getSql().length() > 20);
		
	}
	
	@Test
	public void addBatchSql() throws Exception {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		TUser user = new TUser();
		user.setId(1L);
		user.setAge(18);
		user.setName("jack");
		List<TUser> tList = new ArrayList();
		tList.add(user);
		TwoTuple<String, List<Object[]>> res = GenerateSqlUtil.addBatchSql(classParseInfo, tList);
		System.out.println(JSONObject.toJSONString(res));
		Assert.assertTrue(res.a.length() > 20);
		Assert.assertTrue(res.b.size() == 1 && res.b.get(0).length == 2);
		
		
		System.out.println("------------");
		
		classParseInfo = GDaoCommonUtil.parseClass(TUser2.class);
		TUser2 user2 = new TUser2();
		user2.setId(1L);
		user2.setAge(18);
		user2.setName("jack");
		List<TUser2> tList2 = new ArrayList();
		tList2.add(user2);
		res = GenerateSqlUtil.addBatchSql(classParseInfo, tList2);
		System.out.println(JSONObject.toJSONString(res));
		Assert.assertTrue(res.a.length() > 20);
		Assert.assertTrue(res.b.size() == 1 && res.b.get(0).length == 3);
		
	}
	
	@Test
	public void deleteByPKSql() throws Exception {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		TUser user = new TUser();
		user.setId(1L);
		user.setAge(18);
		user.setName("jack");
		
		TwoTuple<String, Long> res = GenerateSqlUtil.deleteByPKSql(classParseInfo, user.getId());
		System.out.println(JSONObject.toJSONString(res));
		Assert.assertTrue(res.a.length() > 10);
		Assert.assertTrue(res.b == user.getId());
	}
	
	@Test
	public void updateSql() throws Exception {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		TUser user = new TUser();
		user.setId(1L);
		user.setAge(18);
		user.setName("jack");
		
		TwoTuple<String, Object[]> res = GenerateSqlUtil.updateSql(classParseInfo, user);
		System.out.println(JSONObject.toJSONString(res));
		Assert.assertTrue(res.a.length() > 10);
		Assert.assertTrue(res.b.length == 3);
		Assert.assertTrue((Long)res.b[2] == user.getId());
		
	}
}
