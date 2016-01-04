package com.edaoyou.collections.bean;

import java.util.List;

public class TopicListBen {

	public int ret;
	public Response response;
	public int more;

	public class Response {
		public int type;
		public Topic topic;
		public List<User> user;
		public int count;
		public List<News> news;
	}

	public class Topic {
		public String topic_id;
		public String topic_type;
		public String topic;
		public String title;
		public String cover;
		public String avatar;
		public String txt;
		public String is_followed;
	}

	public class News {
		public int id;
		public String title;
		public String txt;
		public String photo;
		public String time;
		public String url;
	}
}
