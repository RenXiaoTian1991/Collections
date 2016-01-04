package com.edaoyou.collections.bean;

import java.util.List;

/**
 * 
 * @Description: 拍照模块发布时需要发送的json中的request对象
 * @author rongwei.mei mayroewe@live.cn 
 * @date 2014-10-5 下午10:02:56  
 * Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class FaBuRequest {
	private String txt;
	private int lock;
	private List<String> tag;
	private int count;
	private int modle;
	private String lat;
	private String lng;
	private String place;
	public FaBuRequest() {
		super();
	}
	public FaBuRequest(String txt, int lock, List<String> tag, int count,
			int modle, String lat, String lng, String place) {
		super();
		this.txt = txt;
		this.lock = lock;
		this.tag = tag;
		this.count = count;
		this.modle = modle;
		this.lat = lat;
		this.lng = lng;
		this.place = place;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	public int getLock() {
		return lock;
	}
	public void setLock(int lock) {
		this.lock = lock;
	}
	public List<String> getTag() {
		return tag;
	}
	public void setTag(List<String> tag) {
		this.tag = tag;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getModle() {
		return modle;
	}
	public void setModle(int modle) {
		this.modle = modle;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	
}
