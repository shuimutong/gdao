package me.lovegao.gdao.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.lovegao.gdao.util.GDaoCommonUtil;

public class SimpleSqlExecutor implements ISqlExecutor {
	private IConnectionPool connectionPool;
	
	public SimpleSqlExecutor(IConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
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
			conn = connectionPool.getConnection();
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
			if(ps != null) {
				ps.close();
			}
			if(conn != null) {
				connectionPool.returnConnection(conn);
			}
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
				rs.close();
				return localList;
			}
		});
		return list;
	}

	@Override
	public long insert(String sql, Object[] params) throws Exception {
		long pk = -1L;
		pk = generalSqlExecute(sql, params, true, new IPreparedStatementResolve<Long>() {
			@Override
			public Long solvePreparedStatement(PreparedStatement ps) throws Exception {
				long id = -1L;
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					id = rs.getLong(1);
				}
				rs.close();
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
		try {
			conn = connectionPool.getConnection();
			conn.setAutoCommit(false);
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
			conn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if(ps != null) {
				ps.close();
			}
			if(conn != null) {
				conn.setAutoCommit(true);
				connectionPool.returnConnection(conn);
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

}
