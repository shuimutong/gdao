package me.lovegao.gdao.orm;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import me.lovegao.gdao.bean.GTableClassParseInfo;
import me.lovegao.gdao.bean.TwoTuple;
import me.lovegao.gdao.sqlexecute.ISqlExecutor;
import me.lovegao.gdao.util.GDaoCommonUtil;
import me.lovegao.gdao.util.GenerateSqlUtil;

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
     * 添加
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
     * 批量添加
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
     * 根据主键删除
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
    		return null;
    }
    
    /**
     * 查询对象列表
     * @param sql
     * @param params
     * @return 包装类列表
     * @throws Exception
     */
    public List<T> list(String sql, Object... params) throws Exception {
    		return null;
    }
    
    /**
     * 普通查询，结果需要自己转义
     * @param sql
     * @param params
     * @return List<{列1, 列2}>
     * @throws Exception
     */
    public List<Object[]> normalList(String sql, Object... params) throws Exception {
    		return null;
    }
    
    /**
     * 统计个数
     * @param sql 包含count()的sql
     * @param params
     * @return
     * @throws Exception
     */
    public int count(String sql, Object... params) throws Exception {
    		return 0;
    }
    
}
