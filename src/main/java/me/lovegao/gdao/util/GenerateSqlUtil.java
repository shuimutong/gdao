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
		String[] fieldNames = classParseInfo.getTableColumnNames();
		Field[] tableFields = classParseInfo.getFields();
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
		GDaoCommonUtil.checkNullException(classParseInfo, t);
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
		GDaoCommonUtil.checkNullException(classParseInfo, tList);
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("insert into ").append(classParseInfo.getTableName());
		String[] fieldNames = classParseInfo.getTableColumnNames();
		Field[] tableFields = classParseInfo.getFields();
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

	/**
	 * 生成通过主键删除记录的sql
	 * demo:DELETE FROM Person WHERE LastName = 'Wilson' 
	 * @param classParseInfo
	 * @param id 主键值
	 * @return
	 * @throws Exception
	 */
	public static <PK> TwoTuple<String, PK> deleteByPKSql(GTableClassParseInfo classParseInfo, PK id) throws Exception {
		GDaoCommonUtil.checkNullException(classParseInfo, id);
		StringBuilder sqlSb = new StringBuilder("delete from ");
		sqlSb.append(classParseInfo.getTableName())
			.append(" where ").append(classParseInfo.getPkName())
			.append("=?");
		return new TwoTuple<String, PK>(sqlSb.toString(), id);
	}
	
	/**
	 * 生成根据主键更新对象的sql
	 * @param classParseInfo
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static <T> TwoTuple<String, Object[]> updateSql(GTableClassParseInfo classParseInfo, T t) throws Exception {
		GDaoCommonUtil.checkNullException(classParseInfo, t);
		StringBuilder sqlSb = new StringBuilder("update ");
		sqlSb.append(classParseInfo.getTableName()).append(" set ");
		//UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing'
		//WHERE LastName = 'Wilson'
		String[] tableFieldNames = classParseInfo.getTableColumnNames();
		Field[] fields = classParseInfo.getFields();
		Object[] tValues = new Object[tableFieldNames.length + 1];
		for(int i=0; i<tableFieldNames.length; i++) {
			String fieldName = tableFieldNames[i];
			sqlSb.append("`").append(fieldName).append("`=?");
			if(i != tableFieldNames.length - 1) {
				sqlSb.append(",");
			}
			sqlSb.append(" ");
			fields[i].setAccessible(true);
			tValues[i] = fields[i].get(t);
		}
		sqlSb.append(" where ").append(classParseInfo.getPkName()).append("=?");
		Field pkField = classParseInfo.getPkField();
		pkField.setAccessible(true);
		tValues[tableFieldNames.length] = pkField.get(t);
		return new TwoTuple<String, Object[]>(sqlSb.toString(), tValues);
	}
	
	/**
	 * 生成根据主键查询的sql
	 * @param classParseInfo
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static <PK> TwoTuple<String, Object[]> queryByPKSql(GTableClassParseInfo classParseInfo, PK id) throws Exception {
		GDaoCommonUtil.checkNullException(classParseInfo, id);
		StringBuilder sqlSb = new StringBuilder("select ");
		String[] tableFieldNames = classParseInfo.getTableColumnNames();
		for(int i=0; i<tableFieldNames.length; i++) {
			sqlSb.append("`").append(tableFieldNames[i]).append("`,");
		}
		sqlSb.append("`").append(classParseInfo.getPkName()).append("` from ")
			.append(classParseInfo.getTableName()).append(" where ")
			.append(classParseInfo.getPkName()).append("=?");
		
		return new TwoTuple<String, Object[]>(sqlSb.toString(), new Object[] {id});
	}
}
