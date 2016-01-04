package com.edaoyou.collections.bean;

public class CheckVersionBean {

	public int ret;
	public Response response;

	public class Response {
		public String update;
		public String build;
		public String ver;
		public String txt;
		public String type;
		public String url;
	}
}
