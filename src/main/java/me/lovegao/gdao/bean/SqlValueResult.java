package me.lovegao.gdao.bean;

import java.util.List;

/**
 * sql和对应字段值
 * @author simple
 *
 */
public class SqlValueResult {
	private String sql;
	private List<Object> paramList;
	
	public SqlValueResult(String sql, List<Object> paramList) {
		super();
		this.sql = sql;
		this.paramList = paramList;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<Object> getParamList() {
		return paramList;
	}
	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}
	
}
