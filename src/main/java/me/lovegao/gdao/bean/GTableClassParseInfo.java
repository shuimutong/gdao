package me.lovegao.gdao.bean;

import java.lang.reflect.Field;

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
	/**声明了表字段的变量名列表**/
	private Field[] tableFields;
	/**声明的字段名列表，和tableFields顺序对应**/
	private String[] fieldNames;
	
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
	public Field[] getTableFields() {
		return tableFields;
	}
	public void setTableFields(Field[] tableFields) {
		this.tableFields = tableFields;
	}
	public String[] getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
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
	
}
