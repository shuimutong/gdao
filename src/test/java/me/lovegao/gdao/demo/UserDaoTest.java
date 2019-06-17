package me.lovegao.gdao.demo;

import java.util.Properties;

import me.lovegao.gdao.connection.IConnectionPool;
import me.lovegao.gdao.connection.SimpleConnectionPool;
import me.lovegao.gdao.sqlexecute.ISqlExecutor;
import me.lovegao.gdao.sqlexecute.SimpleSqlExecutor;

/**
 * Hello world!
 *
 */
public class UserDaoTest 
{
    public static void main( String[] args ) throws Exception {
    		Properties prop = new Properties();
    		String confPath = "/mysql.properties";
    		prop.load(UserDaoTest.class.getResourceAsStream(confPath));
    		IConnectionPool connectionPool = new SimpleConnectionPool(prop);
    		ISqlExecutor sqlExecutor = new SimpleSqlExecutor(connectionPool);
    		UserDao userDao = new UserDao(sqlExecutor);
    		UserDo user = new UserDo();
    		user.setAge(12);
    		user.setName("HelloUser");
    		long uid = userDao.add(user);
    		System.out.println(uid);
    }
}
