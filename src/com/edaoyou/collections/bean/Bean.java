package com.edaoyou.collections.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @Description:话题标签/ 搜索模块进入标签列表timeline/topic_list
 * @date 2014-9-30 下午3:18:06 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class Bean {
	public class Like {
		public String fid;
		public String photo;
		public String uid;
		public String avatar;
		public String nick;
		public String txt;
		public String time;

	}

	public class Article { // 相关资讯
		public int id;
		public String title;
		public String txt;
		public String photo;
		public String time;
		public String url;
	}

	public class ChatItem {
		public String id;
		public String fid;
		public String to;
		public String avatar;
		public String nick;
		public String txt;
		public String time;
	}

	public class WebViewBean {
		public int id;
		public String title;
		public String txt;
		public String photo;
		public String time;
		public String url;
	}

	public class Tag {
		public int tag_id;
		public String tag;
		public String topic_id;
		public String topic;
		public String title;
		public String cover;
		public String avatar;
		public String txt;
		public int topic_type;
	}

	public class Topic implements Serializable {
		/**
		 * 用于序列化和反序列化前后保持ID一致和唯一
		 */
		private static final long serialVersionUID = 1L;
		public String topic_id;
		public String topic;
		public int topic_type;
	}

	public class Photo {
		public String url;
		public String preview;
		public int width;
		public int height;
	}

	public class Category {
		public String topic_id;
		public int topic_type;
		public String topic;
		public String title;
		public String cover;
		public String avatar;
		public String avatar1;
		public String txt;
		public String is_followed;
	}

	/**
	 * 评论列表
	 */
	public class CommentList {

		public int ret;
		public Response response;

		public class Response {
			public int count;
			public List<Comment> list;
			public int more;
		}
	}

	public class Comment {
		public int id;
		public String uid;
		public String avatar;
		public String nick;
		public String txt;
		public long time;
	}

	public class Data {
		public int is_report;
		public int is_like;
		public int total_like;
		public List<Like> likes;
		public List<Tag> tags;
		public Topic topic;
		public List<Photo> photo;
		public int hot;
		public int is_hot;
		public String text;
		public int total_comments;
		public List<Comment> comments;
		public String place;
	}

	public class Pois extends CollectionsAddress {
		private static final long serialVersionUID = 1L;
		public String cp;
		public String distance;
		public String poiType;
		public Point point;
		public String tel;
		public String uid;
		public String zip;

		public class Point implements Serializable {
			/**
			 * 用于序列化和反序列化前后保持ID一致和唯一
			 */
			private static final long serialVersionUID = 1L;
			public double x;
			public double y;
		}
	}

	public class Results extends CollectionsAddress {
		private static final long serialVersionUID = 1L;
		public String street_id;
		public String uid;
		public Location location;

		public class Location implements Serializable {
			/**
			 * 用于序列化和反序列化前后保持ID一致和唯一
			 */
			private static final long serialVersionUID = 1L;
			public double lat;
			public double lng;
		}
	}

	public class Result implements Serializable {
		/**
		 * 用于序列化和反序列化前后保持ID一致
		 */
		private static final long serialVersionUID = 1L;
		public String name;
		public String city;
		public String district;
		public String business;
		public String cityid;
	}

	public class Subscribe {
		public String topic_id;
		public int topic_type;
		public String topic;
		public String title;
		public String cover;
		public String avatar;
		public String txt;
		public String is_followed;
		public String time;
		public int news;
	}

	public class Follow {
		public String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String uid;
		public String avatar;
		public String nick;
		public String py;
		public int relations;
	}

	/**
	 * 关注是否成功
	 */
	public class IsSuccess {
		public String ret;
		public Response response;

		public class Response {
			public String status;
		}
	}

	public class Contact {
		public int id;
		public String uid;
		public String avatar;
		public String nick;
		public String mobile;
		public String bio;
		public int relations;
	}

	/**
	 * "聊天历史列表记录
	 */
	public class ChatUserListData {
		private String cid;// 会话ID
		private String id; // 会话中最后一条聊天记录的id(用于排序)
		private String fid; // FeedID
		private String uid;
		private String photo;// http:\/\/192.168.1.182:100\/thumb.php?src=1689518320_1115978641_1921381653.jpg&t=a&w=112&h=112",//物品缩略图
		private String preview;// http:\/\/192.168.1.182:100\/thumb.php?src=1689518320_1115978641_1921381653.jpg&t=a&w=640&h=199",//聊天置顶图
		private String to; // 和谁聊
		private String avatar;// :http://192.168.1.182:100/thumb.php?src=1894491319_612762404_361207340.jpg&t=a&w=112&h=112",
		private String nick;
		private String txt; // 最后聊天内容
		private String time;// 最后聊天日期
		private String top;// 1置顶 0非置顶,
		private String new_message;// 1有新消息 0无新消息

		public String getCid() {
			return cid;
		}

		public void setCid(String cid) {
			this.cid = cid;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getFid() {
			return fid;
		}

		public void setFid(String fid) {
			this.fid = fid;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getPhoto() {
			return photo;
		}

		public void setPhoto(String photo) {
			this.photo = photo;
		}

		public String getPreview() {
			return preview;
		}

		public void setPreview(String preview) {
			this.preview = preview;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public String getNick() {
			return nick;
		}

		public void setNick(String nick) {
			this.nick = nick;
		}

		public String getTxt() {
			return txt;
		}

		public void setTxt(String txt) {
			this.txt = txt;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getTop() {
			return top;
		}

		public void setTop(String top) {
			this.top = top;
		}

		public String getNew_message() {
			return new_message;
		}

		public void setNew_message(String new_message) {
			this.new_message = new_message;
		}

	}

	/**
	 * 私聊聊天记录
	 */
	public class ChatListData {
		private String cid;// "23"
		private String chat_id; // iOS客户端关键字冲突改成chat_id
		private String fid;// FeedID
		private String uid;
		private String preview;// http:\/\/192.168.1.182:100\/thumb.php?src=1689518320_1115978641_1921381653.jpg&t=a&w=640&h=199",Feed缩略图
		private String avatar;// http:\/\/192.168.1.182:100\/thumb.php?src=1689518320_1115978641_1921381653.jpg&t=a&w=112&h=112",头像
		private String nick;// 用户名
		private String txt;// "在啊", //发送内容
		private String type;// 1文字 2语音
		private String sound_id;// 语音文件id
		private String time;// 1407843662 发送时间

		public String getCid() {
			return cid;
		}

		public void setCid(String cid) {
			this.cid = cid;
		}

		public String getChat_id() {
			return chat_id;
		}

		public void setChat_id(String chat_id) {
			this.chat_id = chat_id;
		}

		public String getFid() {
			return fid;
		}

		public void setFid(String fid) {
			this.fid = fid;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getPreview() {
			return preview;
		}

		public void setPreview(String preview) {
			this.preview = preview;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public String getNick() {
			return nick;
		}

		public void setNick(String nick) {
			this.nick = nick;
		}

		public String getTxt() {
			return txt;
		}

		public void setTxt(String txt) {
			this.txt = txt;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSound_id() {
			return sound_id;
		}

		public void setSound_id(String sound_id) {
			this.sound_id = sound_id;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}
	}

	public class NotificationData {
		private String id;
		private String uid;
		private String avatar;
		private String nick;
		private String txt;
		private String time;
		private String type;
		private String fid;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public String getNick() {
			return nick;
		}

		public void setNick(String nick) {
			this.nick = nick;
		}

		public String getTxt() {
			return txt;
		}

		public void setTxt(String txt) {
			this.txt = txt;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getFid() {
			return fid;
		}

		public void setFid(String fid) {
			this.fid = fid;
		}
	}
}
