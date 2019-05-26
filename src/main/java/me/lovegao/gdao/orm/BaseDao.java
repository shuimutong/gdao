package me.lovegao.gdao.orm;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import me.lovegao.gdao.sqlexecute.ISqlExecutor;

public class BaseDao<T, PK extends Serializable> {
    private Class<T> entityClass;
    private ISqlExecutor sqlExecutor;
	
    @SuppressWarnings("unchecked")
	protected BaseDao(ISqlExecutor sqlExecutor) {
    		entityClass = (Class<T>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    		this.sqlExecutor = sqlExecutor;
	}
    
    /**
     * 添加
     * @param entity
     * @return
     * @throws Exception
     */
    public PK add(T entity) throws Exception {
    		return null;
    }
    
    /**
     * 批量添加
     * @param list
     * @throws Exception
     */
    public void addBatch(List<T> list) throws Exception {
    		
    }
    
    /**
     * 根据主键删除
     * @param id
     * @throws Exception
     */
    public void deleteByPK(PK id) throws Exception {
    }
    
    /**
     * 更新
     * @param entity
     * @throws Exception
     */
    public void update(T entity) throws Exception {
    		
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
