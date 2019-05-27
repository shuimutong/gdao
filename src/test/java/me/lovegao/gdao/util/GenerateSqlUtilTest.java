package me.lovegao.gdao.util;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.bean.SqlValueResult;
import me.lovegao.gdao.demo.TUser;

public class GenerateSqlUtilTest {
	@Test
	public void addSql() throws Exception {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		TUser user = new TUser();
		user.setId(1L);
		user.setAge(18);
		user.setName("jack");
		SqlValueResult sqlResult = GenerateSqlUtil.addSql(classParseInfo, user);
		System.out.println(JSONObject.toJSONString(sqlResult));
		Assert.assertTrue(sqlResult.getParamList().size() == 2);
		Assert.assertTrue(sqlResult.getSql() != null && sqlResult.getSql().length() > 20);
		
	}
}
