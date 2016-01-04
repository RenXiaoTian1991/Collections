package com.edaoyou.collections.bean;


public class CityBean {
	private String name;
	
	public CityBean() {
		super();
	}

	public CityBean(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "CityModel [name=" + name + "]";
	}
	
}
