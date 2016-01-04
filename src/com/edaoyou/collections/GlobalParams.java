package com.edaoyou.collections;

public class GlobalParams {

	// 用户信息
	public static final String USER_NAME = "username"; // 用户名
	public static final String USER_PASSWORD = "password";// 密码
	public static final String USER = "user";
	public static final String USER_UID = "uid";
	public static final String USER_SID = "sid";

	// 在主页面判断是否自动弹出来琳琅园或者兴趣爱好
	public static final String IS_FIRST_LOGIN = "is_first_login"; // 用户是否第一次登录
	public static final String IS_FIRST_REGIESTER_LOGIN = "is_first_regiester_login";// 用户是否刚注册成功第一次登录

	// 用户登录信息
	public static final int LOGIN_TYPE_NULL = -1; // 没有登录(以游客身份登录)
	public static final int LOGIN_TYPE_COLLECTIONS = 0; // 以藏家账号登录
	public static final int LOGIN_TYPE_WEIBO = 1; // 以新浪微博账号登录
	public static final int LOGIN_TYPE_WEIXIN = 2; // 以微信账号登录
	public static final int LOGIN_TYPE_QQ = 3; // 以QQ账号登录

	// 环信相关 start
	public static String EM_NAME;// 环信用户名
	public static final String EM_NAME_PRE = "cuser";// 环信用户名前缀
	public static final String EM_PASSWORD = "123456";// 环信密码
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";
	// 环信相关 end

	// 微信相关 start
	public static final String WEI_XIN_APP_ID = "wxbe417af44ee8de66";
	public static final String WEI_XIN_APP_SECRET = "007c06399c687e17ffd742b7e3079710";
	public static String WEI_XIN_CODE_KEY = "weixin_code";
	public static String WEI_XIN_ACCESS_TOKEN_KEY = "weixin_access_token";
	public static String WEI_XIN_REFRESH_TOKEN_KEY = "weixin_refresh_token";
	public static String WEI_XIN_OPENID_KEY = "weixin_openid";
	// 微信相关 end

	// QQ相关 start
	public static final String QQ_APP_ID = "1103949258";
	public static final String QQ_OPENID_KEY = "qq_openid";
	public static final String QQ_ACCESS_TOKEN_KEY = "qq_access_token";
	public static final String QQ_EXPIRES_TIME_KEY = "qq_expires_time";
	// QQ相关 end

	// 新浪微博相关 start
	public static final String WEI_BO_APP_KEY = "1492037044";
	public static final String WEI_BO_SECRET = "c3a730adb89803037d9ea8c031d9e36b";
	public static final String WEI_BO_REDIRECT_URL = "http://www.cangjia.com";
	public static final String WEI_BO_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog," + "invitation_write";
	public static final String WEI_BO_LOGIN = "weibo_login";// 否判断用户是否用微博账号登录
	public static final String WEI_BO_TOKEN_KEY = "weibo_token";
	public static final String WEI_BO_UID_KEY = "weibo_uid";
	public static final String WEI_BO_EXPIRES_TIME_KEY = "weibo_expires_time";
	// 新浪微博相关 end

	// fragment级别
	public static final String LEVEL_1 = "level_1";
	public static final String LEVEL_2_HOME = "level_2_home";
	public static final String LEVEL_2_CHAT_NOTIFICATION = "level_2_Chat_Notification";

	// Feed fid
	public static final String FEED_FID = "fid";

	// 跳转到webActivity
	public static final String WEB_URL = "url";

	// 应用版本信息
	public static final String ver = "1";

	// 结束MainActivity的值
	public static final String KILL_MAIN = "android.kill_mainactivity";
	
	//版本更新的ver值，作为和ios的区别
	public static final String UPDATA_VER = "2";
	//版本更新的build值，纪录上线的svn值
	public static final int UPDATA_BUILD = 810;
	
	// 拍照模式相关
	public static final int CAMERA_MODE_FREE = 0; // 自由模式
	public static final int CAMERA_MODE_CHAIRS = 1; // 家具
	public static final int CAMERA_MODE_PICTURES = 2; // 书画
	public static final int CAMERA_MODE_CHINAS = 3; // 陶瓷
	public static final int CAMERA_MODE_JADES = 4; // 玉石
	public static final int CAMERA_MODE_MONEYS = 5; // 钱币
	public static final int CAMERA_MODE_GOLDS = 6;// 金铜
	public static final int CAMERA_MODE_BUDDHAS = 7;// 佛像
	public static final int CAMERA_MODE_ZHUCHUANS = 8;// 珠串
	public static int CURRENT_CAMERA_MODE = CAMERA_MODE_FREE;// 当前的拍照模式

	// 照相的tag值
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	// 从相册取相片的tag值
	public static final int GET_PICTURE_FROM_XIANGCE_CODE = 200;
	// 取得裁剪后的照片
	public static final int GET_CUT_PICTURE_CODE = 300;

	// 搜索
	public static final String SEARCH_MORE_TYPE = "type";
	public static final String SEARCH_KEY = "search_key";
	public static final String TOPIC_ID = "topic_id";

	// 活动页分享
	public static final String WEB_TITLE = "title";
	public static final String WEB_CONTENT = "content";
	public static final String WEB_PHOTO = "photo";
}
