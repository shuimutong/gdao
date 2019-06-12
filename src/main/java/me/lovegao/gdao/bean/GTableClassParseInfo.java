package me.lovegao.gdao.bean;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 被GTable注解的类解析后的信息
 * @author simple
 *
 */
public class GTableClassParseInfo {
	/**类**/
	private Class<?> clazz;
	/**表名**/
	private String tableName;
	/**主键名称**/
	private String pkName;
	/**主键的变量**/
	private Field pkField;
	/**主键自动生成**/
	private boolean pkAutoGenerate;
	/**声明了表字段的变量名列表，不含主键**/
	private Field[] fields;
	/**声明的数据库表字段名列表，和fields顺序对应，不含主键**/
	private String[] tableColumnNames;
	/**数据库字段和field对应关系，包含主键和非主键<columnName, Field>**/
	private Map<String, Field> allColumnFieldMap;
	
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Field[] getFields() {
		return fields;
	}
	public void setFields(Field[] fields) {
		this.fields = fields;
	}
	public String[] getTableColumnNames() {
		return tableColumnNames;
	}
	public void setTableColumnNames(String[] tableColumnNames) {
		this.tableColumnNames = tableColumnNames;
	}
	public String getPkName() {
		return pkName;
	}
	public void setPkName(String pkName) {
		this.pkName = pkName;
	}
	public boolean isPkAutoGenerate() {
		return pkAutoGenerate;
	}
	public void setPkAutoGenerate(boolean pkAutoGenerate) {
		this.pkAutoGenerate = pkAutoGenerate;
	}
	public Field getPkField() {
		return pkField;
	}
	public void setPkField(Field pkField) {
		this.pkField = pkField;
	}
	public Map<String, Field> getAllColumnFieldMap() {
		return allColumnFieldMap;
	}
	public void setAllColumnFieldMap(Map<String, Field> allColumnFieldMap) {
		this.allColumnFieldMap = allColumnFieldMap;
	}
	
}
