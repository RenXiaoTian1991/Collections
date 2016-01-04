package com.edaoyou.collections.bean;

import java.util.List;

public class ProvinceBean {
	private String name;
	private List<CityBean> cityList;
	
	public ProvinceBean() {
		super();
	}

	public ProvinceBean(String name, List<CityBean> cityList) {
		super();
		this.name = name;
		this.cityList = cityList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CityBean> getCityList() {
		return cityList;
	}

	public void setCityList(List<CityBean> cityList) {
		this.cityList = cityList;
	}

	@Override
	public String toString() {
		return "ProvinceModel [name=" + name + ", cityList=" + cityList + "]";
	}
	
}
