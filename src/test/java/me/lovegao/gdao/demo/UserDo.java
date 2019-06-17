package me.lovegao.gdao.demo;

import me.lovegao.gdao.bean.annotation.GColumn;
import me.lovegao.gdao.bean.annotation.GId;
import me.lovegao.gdao.bean.annotation.GTable;

@GTable("t_user")
public class UserDo {
	@GId(isAutoIncrease=true)
	@GColumn(name="id")
	private long id;
	
	@GColumn(name="name")
	private String name;
	
	@GColumn(name="age")
	private int age;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
}
