package me.lovegao.gdao.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.bean.annotation.GColumn;
import me.lovegao.gdao.bean.annotation.GId;
import me.lovegao.gdao.bean.annotation.GTable;

public class GDaoCommonUtil {
	public static void checkNullException(Object...objects ) throws NullPointerException {
		if(checkCollectionEmpty(objects)) {
			throw new NullPointerException();
		} else {
			for(Object obj : objects) {
				if(obj == null) {
					throw new NullPointerException();
				}
			}
		}
		
	}
	
	public static <T> boolean checkCollectionEmpty(T[] objs) {
		boolean empty = false;
		if(objs == null || objs.length == 0) {
			empty = true;
		}
		return empty;
	}
	
	public static <T> boolean checkCollectionEmpty(Collection<T> objs) {
		boolean empty = false;
		if(objs == null || objs.size() == 0) {
			empty = true;
		}
		return empty;
	}
	
	/**
	 * 解析被表注解的类
	 * @param clazz
	 * @return
	 */
	public static GTableClassParseInfo parseClass(Class<?> clazz) {
		GTable table = clazz.getAnnotation(GTable.class);
		if(table == null) {
			throw new NullPointerException("类没有声明GTable注解");
		}
		String tableName = table.value();
		Field[] fields = clazz.getDeclaredFields();
		List<Field> fieldList = new ArrayList();
		List<String> fieldNames = new ArrayList();
		Map<String, Field> allColumnFieldMap = new HashMap();
		String pkName = "";
		Field pkField = null;
		boolean pkAutoGenerate = false;
		for(Field field : fields) {
			if(field.isAnnotationPresent(GColumn.class)) {
				GColumn column = field.getAnnotation(GColumn.class);
				//主键声明
				if(field.isAnnotationPresent(GId.class)) {
					if(pkField != null) {
						throw new RuntimeException("=====错误：一个数据库对象做多只能定义一个主键======");
					}
					GId pkColumn = field.getAnnotation(GId.class);
					pkName = column.name();
					pkField = field;
					pkAutoGenerate = pkColumn.isAutoIncrease();
				} else {
					fieldList.add(field);
					fieldNames.add(column.name());
				}
				allColumnFieldMap.put(column.name(), field);
			}
		}
		GTableClassParseInfo tableInfo = new GTableClassParseInfo();
		tableInfo.setClazz(clazz);
		tableInfo.setTableName(tableName);
		tableInfo.setPkName(pkName);
		tableInfo.setPkField(pkField);
		tableInfo.setPkAutoGenerate(pkAutoGenerate);
		tableInfo.setFields(fieldList.toArray(new Field[0]));
		tableInfo.setTableColumnNames(fieldNames.toArray(new String[0]));
		tableInfo.setAllColumnFieldMap(allColumnFieldMap);
		return tableInfo;
	}
}
