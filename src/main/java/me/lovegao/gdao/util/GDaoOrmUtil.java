package me.lovegao.gdao.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.lovegao.gdao.bean.GTableClassParseInfo;

/**
 * orm工具
 * @author simple
 *
 */
public class GDaoOrmUtil {
	/**
	 * 将数据库中查询到的数据转换为对应对象
	 * @param queryValuesList
	 * @param columnNames
	 * @param entityClassParseInfo
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> convertObject2T(List<Object[]> queryValuesList, 
			String[] columnNames, GTableClassParseInfo entityClassParseInfo) throws Exception {
		List<T> list = new ArrayList();
		GDaoCommonUtil.checkNullException(entityClassParseInfo);
		if(GDaoCommonUtil.checkCollectionEmpty(queryValuesList) 
				|| GDaoCommonUtil.checkCollectionEmpty(columnNames)) {
			return list;
		}
		Map<String, Field> columnFieldMap = entityClassParseInfo.getAllColumnFieldMap();
		Constructor tConstructor = entityClassParseInfo.getClazz().getDeclaredConstructor();
		boolean setAccessible = false;
		for(Object[] objs : queryValuesList) {
			T t = (T) tConstructor.newInstance();
			for(int i=0; i<columnNames.length; i++) {
				String columnName = columnNames[i];
				Field field = columnFieldMap.get(columnName);
				if(!setAccessible) {
					field.setAccessible(true);
				}
				Object tmpObj = objs[i];
				if(tmpObj != null 
						&& (field.getType() == Long.class || field.getType() == long.class) 
						&& tmpObj instanceof BigInteger) {
					field.set(t, ((BigInteger)tmpObj).longValue());
				} else {
					field.set(t, objs[i]);
				}
			}
			setAccessible = true;
			list.add(t);
		}
		return list;
	}
}
