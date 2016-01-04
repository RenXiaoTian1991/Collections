package com.edaoyou.collections.bean;

import java.util.List;

public class BackGroundImg {
	public int ret;
	public Response response;

	public class Response {
		public int ver;
		public List<Cover_url> cover_url;
		public List<Cover_icon> cover_icon;
		
		public class Cover_url{
			public String url;
		}
		
		public class Cover_icon{
			public String url;
		}
	}
}
