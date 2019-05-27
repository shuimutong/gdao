package me.lovegao.gdao.util;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.demo.TUser;

public class GDaoCommonUtilTest {
	@Test
	public void parseClass() {
		GTableClassParseInfo classParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		System.out.println(JSONObject.toJSONString(classParseInfo));
		Assert.assertTrue(classParseInfo != null);
		Assert.assertTrue(classParseInfo.getPkName().equals("id"));
		Assert.assertTrue(classParseInfo.getFieldNames().length == classParseInfo.getTableFields().length);
		
	}
}
