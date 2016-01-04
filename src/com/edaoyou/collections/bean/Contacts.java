package com.edaoyou.collections.bean;

import java.util.List;

import com.edaoyou.collections.bean.Bean.Contact;

public class Contacts {
	public int ret;
	public Response response;

	public class Response {
		public int count;
		public List<Contact> list;
		public int more;
	}

}
