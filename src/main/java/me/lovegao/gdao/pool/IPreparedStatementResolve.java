package me.lovegao.gdao.pool;

import java.sql.PreparedStatement;

public interface IPreparedStatementResolve<T> {
	T solvePreparedStatement(PreparedStatement ps) throws Exception;
}
