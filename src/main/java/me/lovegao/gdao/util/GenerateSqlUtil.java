package me.lovegao.gdao.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.bean.SqlValueResult;
import me.lovegao.gdao.bean.TwoTuple;

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
	@Deprecated
	public static <T> SqlValueResult addSql1(GTableClassParseInfo classParseInfo, T t) throws Exception {
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
	
	/**
	 * 生成添加sql
	 * @param classParseInfo
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static <T> TwoTuple<String, Object[]> addSql(GTableClassParseInfo classParseInfo, T t) throws Exception {
		List<T> tList = new ArrayList();
		tList.add(t);
		TwoTuple<String, List<Object[]>> tmpTwoTuple = addBatchSql(classParseInfo, tList);
		TwoTuple<String, Object[]> twoTuple = new TwoTuple<String, Object[]>(tmpTwoTuple.a, tmpTwoTuple.b.get(0));
		return twoTuple;
	}
	
	/**
	 * 批量生成添加sql
	 * @param classParseInfo
	 * @param tList
	 * @return
	 * @throws Exception
	 */
	public static <T> TwoTuple<String, List<Object[]>> 
			addBatchSql(GTableClassParseInfo classParseInfo, List<T> tList) throws Exception {
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("insert into ").append(classParseInfo.getTableName());
		String[] fieldNames = classParseInfo.getFieldNames();
		Field[] tableFields = classParseInfo.getTableFields();
		//所有对象的值列表
		List<Object[]> valueList = new ArrayList();
		StringBuilder fieldSb = new StringBuilder();
		StringBuilder dotSb = new StringBuilder();
		//非id字段数量
		int notIdFieldNum = fieldNames.length;
		//主键自增
		boolean pkAutoGenerate = true;
		//主键非自增
		if(!classParseInfo.isPkAutoGenerate()) {
			pkAutoGenerate = false;
			fieldSb.append("`").append(classParseInfo.getPkName()).append("`,");
			dotSb.append("?,");
			classParseInfo.getPkField().setAccessible(true);
			for(T t : tList) {
				Object[] tValues = new Object[notIdFieldNum+1];
				tValues[0] = classParseInfo.getPkField().get(t);
				valueList.add(tValues);
			}
		} else {
			//初始化值列表
			valueList.add(new Object[notIdFieldNum]);
		}
		for(int i=0; i<fieldNames.length; i++) {
			fieldSb.append("`").append(fieldNames[i]).append("`");
			dotSb.append("?");
			if(i != fieldNames.length-1) {
				fieldSb.append(",");
				dotSb.append(",");
			}
			tableFields[i].setAccessible(true);
			int tValueIndex = i;
			if(!pkAutoGenerate) {
				tValueIndex += 1;
			}
			for(int j=0; j<tList.size(); j++) {
				Object currentFileValue = tableFields[i].get(tList.get(j));
				valueList.get(j)[tValueIndex] = currentFileValue;
			}
		}
		sqlSb.append("(").append(fieldSb.toString()).append(") value(").append(dotSb.toString()).append(")");
		return new TwoTuple(sqlSb.toString(), valueList);
	}
}
