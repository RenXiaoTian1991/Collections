package com.edaoyou.collections;

public interface ConstantValue {

	String ENCODING = "UTF-8";
	/**
	 * 公共连接 http://182.92.195.162:8089/
	 */
	String COMMONURI = "http://123.56.100.57:8089/index.php?r=api/";
	/**
	 * 分享
	 */
	String SHARE = "http://123.56.100.57:8089/wap.php?r=wap/share/index";
	/**
	 * 分享app地址
	 */
	String SHARE_APP = "http://www.tcollections.com/wechat/publicity";
	/**
	 * app配置
	 */
	String BOOT = "client/boot";
	/**
	 * 检查是否有未读消息 接口
	 */
	String CHECK_NOTICE = "client/check_notice";
	/**
	 * 检查 用户名/邮箱/手机号 是否可注册
	 */
	String VALIDATE = "user/validate";
	/**
	 * 用户注册
	 */
	String REGISTER = "user/register";
	/**
	 * 发送手机验证码
	 */
	String SEND_SMS = "user/send_sms";
	/**
	 * 重置密码发送手机验证码
	 */
	String RESET_SEND_SMS = "user/reset_send_sms";
	/**
	 * 重置密码发送邮箱验证码
	 */
	String RESET_SEND_EMAIL = "user/reset_send_email";
	/**
	 * 重置密码
	 */
	String RESET_PWD = "user/reset_pwd";

	/**
	 * 短信验证码验证
	 */
	String CHECK_SMS = "user/check_sms";
	/**
	 * 用户登录
	 */
	String LOGIN = "user/login";

	/**
	 * 第三方登录
	 */
	String OPEN_LOGIN = "user/open_login";

	/**
	 * 获取用户资料
	 */
	String PROFILE = "user/profile";
	/**
	 * 换头像
	 */
	String CHANGE_AVATAR = "user/change_avatar";
	/**
	 * 换封面
	 */
	String CHANGE_COVER = "user/change_cover";
	/**
	 * 关注某人
	 */
	String FOLLOW = "user/follow";
	/**
	 * 取消对某人关注
	 */
	String UNFOLLOW = "user/unfollow";
	/**
	 * 关注列表
	 */
	String FOLLOW_LIST = "user/follow_list";
	/**
	 * 粉丝列表
	 */
	String FANS_LIST = "user/fans_list";
	/**
	 * 发布Feed
	 */
	String CREATE = "timeline/create";
	/**
	 * 刷新Feed详情
	 */
	String GET_FEED = "timeline/get_feed";
	/**
	 * 首页精选timeline
	 */
	String HOT_LIST = "timeline/hot_list";
	/**
	 * 个人主页timeline
	 */
	String PRIVATE_LIST = "timeline/private_list";
	/**
	 * 我关注的timeline
	 */
	String PUBLIC_LIST = "timeline/public_list";
	/**
	 * 话题标签的timeline
	 */
	String TOPIC_LIST = "timeline/topic_list";
	/**
	 * 赞
	 */
	String LIKE = "timeline/like";
	/**
	 * 取消赞
	 */
	String UNLIKE = "timeline/unlike";
	/**
	 * 发布评论
	 */
	String COMMENT = "comment/create";
	/**
	 * Feed评论列表
	 */
	String COMMENT_LIST = "comment/lists";
	/**
	 * 删除评论
	 */
	String DELETECOMMENT = "comment/delete";
	/**
	 * 消息-私聊列表
	 */
	String DISCUSS_LIST_BY_USER = "message/discuss_list_by_user";
	/**
	 * 删除聊天条目
	 */
	String DELETE_DISCUSS = "message/delete_discuss";
	/**
	 * 删除聊天条目
	 */
	String DISCUSS_SET_READ = "message/discuss_set_read";
	/**
	 * 聊天条目置顶
	 */
	String DISCUSS_SET_TOP = "message/discuss_set_top";
	/**
	 * 取消聊天条目置顶
	 */
	String DISCUSS_CANCEL_TOP = "message/discuss_cancel_top";
	/**
	 * 消息-通知列表
	 */
	String NOTICE_LIST = "message/notice_list";
	/**
	 * 喜欢列表
	 */
	String LIKE_LIST = "timeline/like_list";
	/**
	 * 位置-根据经纬度获取附近地点
	 */
	String LOCATION_GET = "location/get";
	/**
	 * 位置-地点搜索
	 */
	String LOCATION_SEARCH = "location/search";
	/**
	 * 位置-搜索建议
	 */
	String LOCATION_SUGGESTION = "location/suggestion";
	/**
	 * 搜索-活动
	 */
	String ACTIVITY_LIST = "search/activity_list";
	/**
	 * 搜索-活动wap
	 */
	String ACTIVITY_WAP_LIST = "search/activity_wap_list";
	/**
	 * 搜索-名家
	 */
	String USER_LIST = "search/user_list";
	/**
	 * 搜索结果(首页)
	 */
	String SEARCH_RESULT = "search/search_result";
	/**
	 * 搜索结果-更多
	 */
	String SEARCH_MORE = "search/search_more";
	/**
	 * 搜索好友
	 */
	String SEARCH_USER = "search/user";
	/**
	 * 搜索结果-更多
	 */
	String CHATLIAT = "discuss/lists";

	/**
	 * 话题标签取消关注
	 */
	String TOPIC_UNFOLLOW = "timeline/topic_unfollow";
	/**
	 * 
	 话题标签关注
	 */
	String TOPIC_FOLLOW = "timeline/topic_follow";

	/**
	 * 聊天发送
	 */
	String LIAOTIAN_SEND = "discuss/create";
	/**
	 * 聊天记录列表
	 */
	String LIAOTIAN_LIST = "discuss/lists_by_user";
	/**
	 * 话题标签列表
	 */
	String TOPICS = "timeline/topics";
	/**
	 * 举报
	 */
	String REPORT = "timeline/report";
	/**
	 * 订阅号－标签列表 
	 */
	String MY_TOPICS = "timeline/my_topics";
	/**
	 * 通讯录列表
	 */
	String CONTACTS_LIST = "user/contacts_list";
	/**
	 * 检查手机通讯录
	 */
	String CHECK_CONTACTS = "search/check_contacts";
	/**
	 * 用户退出
	 */
	String LOGOUT = "user/logout";
	/**
	 * 用户反馈
	 */
	String FEEDBACK = "user/feedback";
	/**
	 * 更新用户资料
	 */
	String CHANGE_PROFILE = "user/change_profile";

	/**
	 * 获取图鉴引导页
	 */
	String CLIENT_GUIDE = "client/guide";
	/**
	 * 引导页点赞
	 */
	String CLIENT_GUIDE_LIKE = "client/guide_like";
	/**
	 * 引导取消点赞
	 */
	String CLIENT_GUIDE_UNLIKE = "client/guide_unlike";
	/**
	 * 客户端内置默认背景图
	 */
	String DEFAULT_COVER = "client/default_cover";
	/**
	 * 是否可以私聊
	 */
	String DISCUSS = "discuss/validate";
	/**
	 * 设置私聊限制开关
	 */
	String CHAT_LIMIT = "user/setting_chat_limit";
	/**
	 * 拉黑
	 */
	String BLOCK = "discuss/block";
	/**
	 * 绑定设备device_token
	 */
	String UPDATE_TOKEN = "client/update_token";
	/**
	 * 版本检测
	 */
	String CHECK_VERSION = "client/check_version";

}
