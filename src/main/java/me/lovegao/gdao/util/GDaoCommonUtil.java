package me.lovegao.gdao.util;

import java.util.Collection;

public class GDaoCommonUtil {
	
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
}
