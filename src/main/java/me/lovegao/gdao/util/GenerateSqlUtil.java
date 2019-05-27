package me.lovegao.gdao.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.bean.SqlValueResult;

/**
 * 生成sql工具
 * @author simple
 *
 */
public class GenerateSqlUtil {
	/**
	 * 生成添加sql
	 * @param classParseInfo
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static <T> SqlValueResult addSql(GTableClassParseInfo classParseInfo, T t) throws Exception {
		//insert into user(`id`,`name`) value(?, ?)
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("insert into ").append(classParseInfo.getTableName());
		String[] fieldNames = classParseInfo.getFieldNames();
		Field[] tableFields = classParseInfo.getTableFields();
		List<Object> valueList = new ArrayList();
		StringBuilder fieldSb = new StringBuilder();
		StringBuilder dotSb = new StringBuilder();
		//主键非自增
		if(!classParseInfo.isPkAutoGenerate()) {
			fieldSb.append("`").append(classParseInfo.getPkName()).append("`,");
			dotSb.append("?,");
			classParseInfo.getPkField().setAccessible(true);
			valueList.add(classParseInfo.getPkField().get(t));
		}
		for(int i=0; i<fieldNames.length; i++) {
			fieldSb.append("`").append(fieldNames[i]).append("`");
			dotSb.append("?");
			if(i != fieldNames.length-1) {
				fieldSb.append(",");
				dotSb.append(",");
			}
			tableFields[i].setAccessible(true);
			Object currentFileValue = tableFields[i].get(t);
			valueList.add(currentFileValue);
		}
		sqlSb.append("(").append(fieldSb.toString()).append(") value(").append(dotSb.toString()).append(")");
		SqlValueResult result = new SqlValueResult(sqlSb.toString(), valueList);
		return result;
	}
}
