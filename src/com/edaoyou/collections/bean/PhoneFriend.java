package com.edaoyou.collections.bean;

import java.io.Serializable;

public class PhoneFriend implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String number;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
}
