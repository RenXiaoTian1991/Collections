package com.edaoyou.collections.bean;

import java.util.List;

public class FamosBean {
	public int ret;
	public Response response;
	public int more;

	public class Response {
		public int count;
		public List<Famos> list;
	}

	public class Famos {
		public int id;
		public String uid;
		public String avatar;
		public String nick;
		public String bio;
		public String relations;
	}
}
