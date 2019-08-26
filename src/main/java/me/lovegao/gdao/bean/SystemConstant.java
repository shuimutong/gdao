package me.lovegao.gdao.bean;

/**
 * 常量
 * @author simple
 *
 */
public class SystemConstant {
	/**驱动名称**/
	public final static String STR_DRIVER_NAME = "driverName";
	/**连接url**/
	public final static String STR_CONNECTION_URL = "connectionUrl";
	/**用户名**/
	public final static String STR_USER_NAME = "userName";
	/**密码**/
	public final static String STR_USER_PASSWORD = "userPassword";
	/**连接池初始连接数量**/
	public final static String STR_INIT_CONNECTION_NUM = "initConnectionNum";
	/**连接池最大连接数量**/
	public final static String STR_MAX_CONNECTION_NUM = "maxConnectionNum";
	/**查询超时时间-秒**/
	public final static String STR_QUERY_TIME = "maxQueryTime";
	/**归还连接时检测连接**/
	public final static String STR_CHECK_CONNECTION_WHEN_RETURN = "checkConnectionWhenReturn";
	/**连接检测语句**/
	public final static String STR_CHECK_CONNECTION_VALIDATION_QUERY = "checkConnectionValidationQuery";
	/**定时检测连接间隔时长（秒）**/
	public final static String STR_PERIOD_CHECK_CONNECTION_TIME = "periodCheckConnectionTime";
}
