package me.lovegao.gdao.orm;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.bean.TwoTuple;
import me.lovegao.gdao.sqlexecute.ISqlExecutor;
import me.lovegao.gdao.util.GDaoCommonUtil;
import me.lovegao.gdao.util.GDaoOrmUtil;
import me.lovegao.gdao.util.GenerateSqlUtil;

/**
 * 操作对象的dao
 * @author simple
 *
 * @param <T>
 * @param <PK>
 */
public class BaseDao<T, PK extends Serializable> {
    private Class<T> entityClass;
    private GTableClassParseInfo entityClassParseInfo;
    private ISqlExecutor sqlExecutor;
	
    @SuppressWarnings("unchecked")
	protected BaseDao(ISqlExecutor sqlExecutor) {
    		entityClass = (Class<T>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    		this.sqlExecutor = sqlExecutor;
    		entityClassParseInfo = GDaoCommonUtil.parseClass(entityClass);
	}
    
    /**
     * 新增
     * @param entity
     * @return
     * @throws Exception
     */
    public PK add(T entity) throws Exception {
    		PK id = null;
    		if(entity != null) {
    			TwoTuple<String, Object[]> sqlResult = GenerateSqlUtil.addSql(entityClassParseInfo, entity);
    			id = sqlExecutor.insert(sqlResult.a, sqlResult.b);
    		}
    		return id;
    }
    
    /**
     * 批量新增
     * @param list
     * @throws Exception
     */
    public void addBatch(List<T> list) throws Exception {
    		if(!GDaoCommonUtil.checkCollectionEmpty(list)) {
    			TwoTuple<String, List<Object[]>> sqlList = GenerateSqlUtil.addBatchSql(entityClassParseInfo, list);
    			sqlExecutor.insertOrUpdateBatch(sqlList.a, sqlList.b);
    		}
    }
    
    /**
     * 根据主键删除数据
     * @param id
     * @throws Exception
     */
    public void deleteByPK(PK id) throws Exception {
    		TwoTuple<String, PK> sqlIdEntity = GenerateSqlUtil.deleteByPKSql(entityClassParseInfo, id);
    		sqlExecutor.update(sqlIdEntity.a, new Object[] {sqlIdEntity.b});
    }
    
    /**
     * 更新
     * @param entity
     * @throws Exception
     */
    public void update(T entity) throws Exception {
    		TwoTuple<String, Object[]> tuple = GenerateSqlUtil.updateSql(entityClassParseInfo, entity);
    		sqlExecutor.update(tuple.a, tuple.b);
    }
    
    /**
     * 根据主键查找
     * @param id
     * @return
     * @throws Exception
     */
    public T queryByPK(PK id) throws Exception {
    		TwoTuple<String, Object[]> tuple = GenerateSqlUtil.queryByPKSql(entityClassParseInfo, id);
    		TwoTuple<List<Object[]>, String[]> resultTuple = sqlExecutor.queryValueAndColumn(tuple.a, tuple.b);
    		List<T> list = GDaoOrmUtil.convertObject2T(resultTuple.a, resultTuple.b, entityClassParseInfo);
    		if(!GDaoCommonUtil.checkCollectionEmpty(list)) {
    			return list.get(0);
    		}
    		return null;
    }
    
    /**
     * 查询对象列表
     * @param sql 查询sql
     * @param replaceValues sql中对应?的值
     * @return 包装类列表
     * @throws Exception
     */
    public List<T> list(String sql, Object... replaceValues) throws Exception {
    		TwoTuple<List<Object[]>, String[]> resultTuple = sqlExecutor.queryValueAndColumn(sql, replaceValues);
		List<T> list = GDaoOrmUtil.convertObject2T(resultTuple.a, resultTuple.b, entityClassParseInfo);
    		return list;
    }
    
    /**
     * 普通查询，结果需要自己转义
     * @param sql 查询sql
     * @param replaceValues sql中对应?的值
     * @return List<{列1, 列2}>
     * @throws Exception
     */
    public List<Object[]> normalList(String sql, Object... replaceValues) throws Exception {
    		List<Object[]> list = sqlExecutor.query(sql, replaceValues);
    		return list;
    }
    
    /**
     * 统计个数
     * @param sql 有且仅有count()的sql
     * @param replaceValues sql中对应?的值
     * @return
     * @throws Exception
     */
    public long count(String sql, Object... replaceValues) throws Exception {
    		List<Object[]> list = sqlExecutor.query(sql, replaceValues);
    		if(!GDaoCommonUtil.checkCollectionEmpty(list)) {
    			return (long) list.get(0)[0];
    		}
    		return 0;
    }
    
    /**
     * 获取sql执行
     * @return
     */
    public ISqlExecutor getSqlExecutor() {
    	return sqlExecutor;
    }
}
