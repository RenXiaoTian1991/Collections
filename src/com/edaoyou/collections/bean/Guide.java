package com.edaoyou.collections.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Guide {
	private String ret;
	private Response response;

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

	public class Response {
		private String update;
		private String ver;
		private String type;
		private ArrayList<Data> list;

		public String getUpdate() {
			return update;
		}

		public void setUpdate(String update) {
			this.update = update;
		}

		public String getVer() {
			return ver;
		}

		public void setVer(String ver) {
			this.ver = ver;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public ArrayList<Data> getList() {
			return list;
		}

		public void setList(ArrayList<Data> list) {
			this.list = list;
		}

		public class Data implements Serializable {

			private static final long serialVersionUID = 1L;
			// 琳琅园
			private String id;
			private String txt;
			private String url1;
			private String url2;
			private String url3;
			private String likes;
			private String is_like;
			private String mabstract;

			// 活动
			private String title;
			private String time;
			private String photo;
			private String url;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getTxt() {
				return txt;
			}

			public void setTxt(String txt) {
				this.txt = txt;
			}

			public String getUrl1() {
				return url1;
			}

			public void setUrl1(String url1) {
				this.url1 = url1;
			}

			public String getUrl2() {
				return url2;
			}

			public void setUrl2(String url2) {
				this.url2 = url2;
			}

			public String getUrl3() {
				return url3;
			}

			public void setUrl3(String url3) {
				this.url3 = url3;
			}

			public String getLikes() {
				return likes;
			}

			public void setLikes(String likes) {
				this.likes = likes;
			}

			public String getIs_like() {
				return is_like;
			}

			public void setIs_like(String is_like) {
				this.is_like = is_like;
			}

			public String getMabstract() {
				return mabstract;
			}

			public void setMabstract(String mabstract) {
				this.mabstract = mabstract;
			}

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public String getTime() {
				return time;
			}

			public void setTime(String time) {
				this.time = time;
			}

			public String getPhoto() {
				return photo;
			}

			public void setPhoto(String photo) {
				this.photo = photo;
			}

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

		}
	}
}
