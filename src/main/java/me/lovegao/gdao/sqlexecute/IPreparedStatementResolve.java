package me.lovegao.gdao.sqlexecute;

import java.sql.PreparedStatement;

public interface IPreparedStatementResolve<T> {
	T solvePreparedStatement(PreparedStatement ps) throws Exception;
}
