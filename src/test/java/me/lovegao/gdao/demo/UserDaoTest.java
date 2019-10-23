package me.lovegao.gdao.demo;

/**
 * Hello world!
 *
 */
public class UserDaoTest 
{
    public static void main( String[] args ) throws Exception {
    		UserDao userDao = new UserDao();
    		UserDo user = new UserDo();
    		user.setAge(12);
    		user.setName("HelloUser");
    		long uid = userDao.add(user);
    		System.out.println(uid);
    }
}
