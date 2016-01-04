package com.edaoyou.collections.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * user对象的实体类，注意要根据服务器反回来的数据是否有多了一层Response,来取数据.
 */
public class User {
	public int ret;
	public Response response;

	public String id;
	public String uid;// 用户id
	public String sid;// token
	public String nick; // 昵称
	public String username;// 用户名
	public String location;// 所在地
	public String gender;// 性别 0女 1男
	public String bio;// 签名
	public String[] queries;
	public String avatar;// 头像
	public String lang;// app版本 zh-cn简体中文 zh-tw繁体中文 en英文
	public String cover;// 封面
	public String is_followed;// 我是否关注 0未关注 1已关注 2自己
	public String relations; // 0无关系 1我关注了他(已关注) 2他关注了我(我的粉丝) 3互粉
	public String validtime;// token有效期
	public String verified;// 是否认证
	public String chat_limit;// 只允许我关注的人和我私聊(1开 0关)
	public String feeds;// 内容数
	public String likes;// 被赞数
	public String follows;// 关注数
	public String fans;// 粉丝数
	public List<Tag> tag;
	public ArrayList<String> thumb;// 我的相册缩略图

	public static class Response {

		public String id;
		public String uid;// 用户id
		public String sid;// token
		public String nick; // 昵称
		public String username;// 用户名
		public String location;// 所在地
		public String gender;// 性别 0女 1男
		public String bio;// 签名
		public String[] queries;
		public String avatar;// 头像
		public String lang;// app版本 zh-cn简体中文 zh-tw繁体中文 en英文
		public String cover;// 封面
		public String is_followed;// 我是否关注 0未关注 1已关注 2自己
		public String relations; // 0无关系 1我关注了他(已关注) 2他关注了我(我的粉丝) 3互粉
		public String validtime;// token有效期
		public String verified;// 是否认证
		public String chat_limit;// 只允许我关注的人和我私聊(1开 0关)
		public String feeds;// 内容数
		public String likes;// 被赞数
		public String follows;// 关注数
		public String fans;// 粉丝数
		public List<Tag> tag;
		public ArrayList<String> thumb;// 我的相册缩略图

	}

	public static class Tag {
		public String topic;
		public String topic_id;
	}
}
