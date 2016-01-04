package com.edaoyou.collections.bean;

import java.io.Serializable;

/**
 * Description: 该类用于拍照模块下的定位具体地址的历史记录
 * 
 * @author xin.lv
 * @date 2015-1-9
 */
public class CollectionsAddress implements Serializable {
	/**
	 * 用于序列化和反序列化前后保持ID一致和唯一
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	public String name;
	public String address;
	public String addr;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Override
	public String toString() {
		return "CollectionsAddress [id=" + id + ", name=" + name + ", address="
				+ address + ", addr=" + addr + "]";
	}

}
