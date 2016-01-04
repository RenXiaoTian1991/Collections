package com.edaoyou.collections.bean;

import java.util.List;

import com.edaoyou.collections.bean.Bean.ChatListData;

public class ChatList {
	private String ret;
	private Response response;

	public class Response {
		private int more;
		private int count;
		private List<ChatListData> list;

		public int getMore() {
			return more;
		}

		public void setMore(int more) {
			this.more = more;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public List<ChatListData> getList() {
			return list;
		}

		public void setList(List<ChatListData> list) {
			this.list = list;
		}

	}

	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
