package com.edaoyou.collections.bean;

import java.util.List;

import com.edaoyou.collections.bean.Bean.Subscribe;

/**
 * 订阅的话题标签列表
 */
public class SubscribeBean {
	public int ret;
	public Response response;
	
	public static class Response{
		public List<Subscribe> topic_category;
	}
}
