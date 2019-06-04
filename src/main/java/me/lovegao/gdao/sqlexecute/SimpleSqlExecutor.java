package me.lovegao.gdao.sqlexecute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.lovegao.gdao.connection.IConnectionPool;
import me.lovegao.gdao.util.GDaoCommonUtil;
import me.lovegao.gdao.util.JDBCUtil;

public class SimpleSqlExecutor implements ISqlExecutor, IManulTransactionSqlExecutor {
	private static ThreadLocal<Connection> CONNECTION_THREAD_LOCAL = new ThreadLocal();
	private IConnectionPool connectionPool;
	/**默认事务隔离级别**/
	private static int DEFAULT_TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
	
	public SimpleSqlExecutor(IConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}
	
	//获取连接
	private Connection getConnection() throws Exception {
		Connection conn = CONNECTION_THREAD_LOCAL.get();
		if(conn == null) {
			conn = connectionPool.getConnection();
			conn.setTransactionIsolation(DEFAULT_TRANSACTION_ISOLATION_LEVEL);
			conn.setAutoCommit(true);
			CONNECTION_THREAD_LOCAL.set(conn);
		}
		return conn;
	}
	
	//释放连接
	private void releaseConnection() {
		Connection conn = CONNECTION_THREAD_LOCAL.get();
		if(conn != null) {
			CONNECTION_THREAD_LOCAL.remove();
			connectionPool.returnConnection(conn);
		}
	}
	
	/**
	 * 通用sql执行
	 * @param sql
	 * @param params
	 * @param preparedStatementResolve
	 * @return
	 * @throws Exception
	 */
	private <T> T generalSqlExecute(String sql, Object[] params, boolean returnKeys,
			IPreparedStatementResolve<T> preparedStatementResolve) throws Exception {
		T t = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			if(returnKeys) {
				ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				ps = conn.prepareStatement(sql);
			}
			if(!GDaoCommonUtil.checkCollectionEmpty(params)) {
				for(int i=0; i<params.length; i++) {
					ps.setObject(i+1, params[i]);
				}
			}
			t = preparedStatementResolve.solvePreparedStatement(ps);
		} catch (Exception e) {
			throw e;
		} finally {
			JDBCUtil.closePreparedStatement(ps);
			releaseConnection();
		}
		return t;
	}

	@Override
	public List<Object[]> query(String sql, Object[] params) throws Exception {
		List<Object[]> list = new ArrayList();
		list = generalSqlExecute(sql, params, false, new IPreparedStatementResolve<List<Object[]>>() {
			@Override
			public List<Object[]> solvePreparedStatement(PreparedStatement ps) throws Exception {
				List<Object[]> localList = new ArrayList();
				ResultSet rs = ps.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				while(rs.next()) {
					Object[] rowData = new Object[columnCount];
					for(int i=1; i<=columnCount; i++) {
						rowData[i-1] = rs.getObject(i);
					}
					localList.add(rowData);
				}
				JDBCUtil.closeResultSet(rs);
				return localList;
			}
		});
		return list;
	}

	@Override
	public <T> T insert(String sql, Object[] params) throws Exception {
		T pk = null;
		pk = generalSqlExecute(sql, params, true, new IPreparedStatementResolve<T>() {
			@Override
			public T solvePreparedStatement(PreparedStatement ps) throws Exception {
				T id = null;
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					id = (T) rs.getObject(1);
				}
				JDBCUtil.closeResultSet(rs);
				return id;
			}
		});
		return pk;
	}

	@Override
	public int[] insertOrUpdateBatch(String sql, List<Object[]> paramsList) throws Exception {
		int[] res = null;
		Connection conn = null;
		PreparedStatement ps = null;
		//是否改变了自动提交，防止此事务外还有其他事务
		//1、如果默认是手动提交，说明外界修改了自动提交，即外界要自己控制提交、回滚操作，则后续的提交、回滚操作不主动触发。
		//2、如果默认是自动提交，后续则触发提交、回滚操作
		boolean changeAutoCommit = false;
		try {
			conn = getConnection();
			//默认是主动提交
			if(changeAutoCommit = conn.getAutoCommit()) {
				//改为非自动提交
				conn.setAutoCommit(false);
			}
			ps = conn.prepareStatement(sql);
			if(!GDaoCommonUtil.checkCollectionEmpty(paramsList)) {
				for(Object[] params : paramsList) {
					for(int i=0; i<params.length; i++) {
						ps.setObject(i+1, params[i]);
					}
					ps.addBatch();
				}
			}
			res = ps.executeBatch();
			if(changeAutoCommit) {
				conn.commit();
			}
		} catch (Exception e) {
			if(changeAutoCommit) {
				conn.rollback();
			}
			throw e;
		} finally {
			if(ps != null) {
				ps.close();
			}
			if(conn != null && changeAutoCommit) {
				conn.setAutoCommit(true);
				releaseConnection();
			}
		}
		return res;
	}

	@Override
	public int update(String sql, Object[] params) throws Exception {
		int tmpRes = generalSqlExecute(sql, params, false, new IPreparedStatementResolve<Integer>() {
			@Override
			public Integer solvePreparedStatement(PreparedStatement ps) throws Exception {
				int tmpRes = ps.executeUpdate();
				return tmpRes;
			}
		});
		return tmpRes;
	}

	@Override
	public void beginTransaction() throws Exception {
		beginTransaction(DEFAULT_TRANSACTION_ISOLATION_LEVEL);
	}

	@Override
	public void beginTransaction(int transactionIsolationLevel) throws Exception {
		Connection conn = getConnection();
		conn.setAutoCommit(false);
		conn.setTransactionIsolation(transactionIsolationLevel);
	}

	@Override
	public void commitTransaction() throws Exception {
		Connection conn = getConnection();
		conn.commit();
		conn.setAutoCommit(true);
		releaseConnection();
	}

	@Override
	public void rollbackTransaction() throws Exception {
		Connection conn = getConnection();
		conn.rollback();
		conn.setAutoCommit(true);
		releaseConnection();
	}
}
