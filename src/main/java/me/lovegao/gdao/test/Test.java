package me.lovegao.gdao.test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ThreadLocal<Person> t1 = new ThreadLocal();
		Person p1 = new Person();
		p1.setAge(19);
		t1.set(p1);
		WeakReference weakReference = new WeakReference(p1);
		p1 = null;
        System.gc();
		Thread.sleep(2000);
        System.out.println("手动触发GC:" + weakReference.get());
		
		
		Person p2 = t1.get();
		System.out.println(p2.getAge());
		
		System.out.println("before gc----");
		List<byte[]> list = new ArrayList();
		for(int i=0; i<2; i++) {
			byte[] arr = new byte[1*1024*1024];
			list.add(arr);
		}
		System.gc();
		System.gc();
		Thread.sleep(2000);
		System.out.println("after gc----");
		
		Person t3 = t1.get();
		System.out.println(t3.getAge());
	}
	
	public static void add() {
		DBUtil util = new DBUtil();
		String sql = "insert into person values(sys_guid(), ?,?,?)";
		List<Object> param = new ArrayList<Object>();
		for(int i=0;i<200;i++) {
			util.getConnection();
			param.add("Name" + i);
			if(i%2==0) {
				param.add("男");
			}else {
				param.add("女");
			}
			param.add(i);
			util.update(sql, param);
			util.close();
			param.clear();
		}
	}

}
