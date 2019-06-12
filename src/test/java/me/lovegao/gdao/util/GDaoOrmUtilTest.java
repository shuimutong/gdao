package me.lovegao.gdao.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.demo.TUser;

public class GDaoOrmUtilTest {
	@Test
	public void convertObject2T() throws Exception {
		List<Object[]> queryValuesList = new ArrayList();
		queryValuesList.add(new Object[] {1, "a", 19});
		queryValuesList.add(new Object[] {2, "bc", 129});
		TUser user3 = new TUser();
		user3.setId(3);
		user3.setName("jack");
		user3.setAge(90);
		Object[] obj3 = {user3.getId(), user3.getName(), user3.getAge()};
		queryValuesList.add(obj3);
		String[] columnNames = {"id","name","age"};
		GTableClassParseInfo entityClassParseInfo = GDaoCommonUtil.parseClass(TUser.class);
		List<TUser> list = GDaoOrmUtil.convertObject2T(queryValuesList, columnNames, entityClassParseInfo);
		System.out.println(JSONObject.toJSONString(list));
		Assert.assertTrue(list.size() == 3);
		Assert.assertTrue(list.get(2).getId() == user3.getId());
		Assert.assertTrue(list.get(2).getName().equals(user3.getName()));
		Assert.assertTrue(list.get(2).getAge() == user3.getAge());
		
		double num = 10;
		long t1 = System.currentTimeMillis();
		for(int i=0; i<num; i++) {
			list = GDaoOrmUtil.convertObject2T(queryValuesList, columnNames, entityClassParseInfo);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("totalUseTime:" + (t2-t1) + "ms,avgUseTime:" + (t2-t1)/num + "ms");
	}
}
