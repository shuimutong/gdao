package me.lovegao.gdao.sqlexecute;

import java.util.List;

import me.lovegao.gdao.bean.TwoTuple;

/**
 * sql执行接口
 * @author simple
 *
 */
public interface ISqlExecutor {
	/**
	 * 查询
	 * @param sql sql语句
	 * @param params 参数值
	 * @return 值列表
	 */
	List<Object[]> query(String sql, Object[] params) throws Exception;
	
	/**
	 * 查询值，和对应的数据库列名
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	TwoTuple<List<Object[]>, String[]> queryValueAndColumn(String sql, Object[] params) throws Exception;
	
	/**
	 * 插入数据
	 * @param sql 插入sql
	 * @param params 参数值
	 * @return 插入数据的id
	 */
	<T> T insert(String sql, Object[] params) throws Exception;
	
	/**
	 * 批量插入
	 * @param sql
	 * @param paramsList
	 * @return
	 */
	int[] insertOrUpdateBatch(String sql, List<Object[]> paramsList) throws Exception;
	
	/**
	 * 更新数据
	 * @param sql
	 * @param params
	 * @return
	 */
	int update(String sql, Object[] params) throws Exception;
	
}
