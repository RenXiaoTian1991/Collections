package com.edaoyou.collections.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 指定用户资料user/profile
 */
public class PersonalUserProfile implements Serializable{
	/**
	 * 序列化的唯一标示 
	 */
	private static final long serialVersionUID = 1L;
	public int ret;
	public Response response;

	public static class Response {
		public String uid;
		public String username;
		public String location;
		public String gender;
		public String bio;
		public int verified;
		public int chat_limit;
		public int feeds;
		public int likes;
		public int follows;
		public int fans;
		public String avatar;
		public String cover;
		public String is_followed;
		public String relations;
		public List<Tag> tag;
		public List<String> thumb;

		public static class Tag {
			public String topic;
			public String topic_id;
		}
	}
}
