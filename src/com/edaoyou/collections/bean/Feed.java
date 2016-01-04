package com.edaoyou.collections.bean;

import java.io.Serializable;

import com.edaoyou.collections.bean.Bean.Data;

/**
 * 
 * @Description: Feed类
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-9-30 下午1:59:26 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class Feed implements Serializable {
	/**
	 * 用于序列化和反序列化前后保持ID一致和唯一
	 */
	private static final long serialVersionUID = 1L;
	public String fid;
	public String uid;
	public int group;
	public String nick;
	public String avatar;
	public long time;
	public int lock;
	public int type;
	public Data data;
}