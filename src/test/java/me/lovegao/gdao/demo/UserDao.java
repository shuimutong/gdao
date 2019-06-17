package me.lovegao.gdao.demo;

import me.lovegao.gdao.orm.BaseDao;
import me.lovegao.gdao.sqlexecute.ISqlExecutor;

public class UserDao extends BaseDao<UserDo, Long> {
	public UserDao(ISqlExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
}
