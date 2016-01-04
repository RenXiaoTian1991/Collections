package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.QQUser;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.bean.WeiBoUser;
import com.edaoyou.collections.bean.WeiXinUser;
import com.edaoyou.collections.engine.EMManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.view.FloatingActionButton;
import com.igexin.sdk.PushManager;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.connect.UserInfo;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginAndRegiesterActivity extends BaseActivity implements OnClickListener {

	private Button login_bt;
	private Button regiester_bt;
	private TextView other_login_tv;
	private RelativeLayout free_rl;
	private FloatingActionButton other_login_weixin;
	private FloatingActionButton other_login_qq;
	private FloatingActionButton other_login_weibo;

	private String mOpenLoginUrl;

	private IWXAPI mWeiXinApi; // 微信api
	private Tencent mTencent;// QQapi
	private SsoHandler mSsoHandler;// 新浪微博api

	private String mGetWeixinTokenUrl;// 获取授权微信token的URL
	private String mGetAuthorizeUserInfoUrl;// 获取授权微信个人资料的URL
	private String mUpdateTokenUrl;// 获取绑定设备device_token的URl
	private String mWeiXinToken;
	private String mWeiXinRefreshToken;
	private String mWeiXinOpenid;

	private String mQQOpenid;
	private String mQQAccessToken;
	private long mQQExpiresIn;// QQtoken过期时间

	private String mWeiBoToken;
	private String mWeiBoUid;
	private long mExpiresTime;// 新浪微博token过期时间

	private String mNick;// 授权用户名
	private String mAvatar;// 授权用户头像
	private String mGender;// 授权用户性别
	private String mLocation;// 授权用户城市
	private String mCid;// 用户推送消息的cid
	private String mUid;
	private String mSid;

	private static final int ACTIVITY_RESULT_LOGIN = 1;
	private static final int ACTIVITY_RESULT_REGIESTER = 2;

	public static final String KEY_LOGIN = "isLoginSuccess";
	public static final String KEY_REGIESTER = "isRegiesterSuccess";

	private int mCurrentAccountType;

	private static final int ANIM_TIME = 150;

	private static final int MSG_SHOW_WEIXIN_BT = 1;// 显示微信登录按钮
	private static final int MSG_SHOW_QQ_BT = 2;// 显示QQ登录按钮
	private static final int MSG_SHOW_WEIBO_BT = 3;// 显示微博登录按钮

	private static final int MSG_HIDE_WEIXIN_BT = 4;// 隐藏微信登录按钮
	private static final int MSG_HIDE_QQ_BT = 5;// 隐藏QQ登录按钮
	private static final int MSG_HIDE_WEIBO_BT = 6;// 隐藏微博登录按钮
	private static final int MSG_SHOW_OLD_LOGIN_BT = 7;// 显示之前的登录按钮
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LoginAndRegiesterActivity.MSG_SHOW_WEIXIN_BT:
				login_bt.setVisibility(View.GONE);
				regiester_bt.setVisibility(View.GONE);
				other_login_tv.setVisibility(View.GONE);
				other_login_weixin.show();
				mHandler.sendEmptyMessageDelayed(MSG_SHOW_QQ_BT, ANIM_TIME);
				break;
			case LoginAndRegiesterActivity.MSG_SHOW_QQ_BT:
				other_login_qq.show();
				mHandler.sendEmptyMessageDelayed(MSG_SHOW_WEIBO_BT, ANIM_TIME);
				break;
			case LoginAndRegiesterActivity.MSG_SHOW_WEIBO_BT:
				other_login_weibo.show();
				break;
			case LoginAndRegiesterActivity.MSG_HIDE_WEIXIN_BT:
				other_login_weixin.hide();
				mHandler.sendEmptyMessageDelayed(MSG_SHOW_OLD_LOGIN_BT, ANIM_TIME);
				break;
			case LoginAndRegiesterActivity.MSG_HIDE_QQ_BT:
				other_login_qq.hide();
				mHandler.sendEmptyMessageDelayed(MSG_HIDE_WEIXIN_BT, ANIM_TIME);
				break;
			case LoginAndRegiesterActivity.MSG_HIDE_WEIBO_BT:
				other_login_weibo.hide();
				mHandler.sendEmptyMessageDelayed(MSG_HIDE_QQ_BT, ANIM_TIME);
				break;
			case LoginAndRegiesterActivity.MSG_SHOW_OLD_LOGIN_BT:
				login_bt.setVisibility(View.VISIBLE);
				regiester_bt.setVisibility(View.VISIBLE);
				other_login_tv.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWeiXin();
		initQQ();
		initWeiBo();
		mCid = PushManager.getInstance().getClientid(this);
		initPpdateToken();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCurrentAccountType == GlobalParams.LOGIN_TYPE_WEIXIN) {
			getWeiXinToKen();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_login_and_regiester;
	}

	@Override
	protected void findViews() {
		login_bt = (Button) findViewById(R.id.login_bt);
		regiester_bt = (Button) findViewById(R.id.regiester_bt);
		other_login_tv = (TextView) findViewById(R.id.other_login_tv);
		free_rl = (RelativeLayout) findViewById(R.id.free_rl);
		other_login_weixin = (FloatingActionButton) findViewById(R.id.other_login_weixin);
		other_login_qq = (FloatingActionButton) findViewById(R.id.other_login_qq);
		other_login_weibo = (FloatingActionButton) findViewById(R.id.other_login_weibo);
	}

	@Override
	protected void setListensers() {
		login_bt.setOnClickListener(this);
		regiester_bt.setOnClickListener(this);
		other_login_tv.setOnClickListener(this);
		free_rl.setOnClickListener(this);
		other_login_weixin.setOnClickListener(this);
		other_login_qq.setOnClickListener(this);
		other_login_weibo.setOnClickListener(this);
	}

	private void initWeiXin() {
		mWeiXinApi = WXAPIFactory.createWXAPI(this, GlobalParams.WEI_XIN_APP_ID);
		mWeiXinApi.registerApp(GlobalParams.WEI_XIN_APP_ID);
	}

	private void initQQ() {
		mTencent = Tencent.createInstance(GlobalParams.QQ_APP_ID, this);
	}

	private void initWeiBo() {
		AuthInfo mAuthInfo = new AuthInfo(this, GlobalParams.WEI_BO_APP_KEY, GlobalParams.WEI_BO_REDIRECT_URL, null);
		mSsoHandler = new SsoHandler(this, mAuthInfo);
	}

	private void initPpdateToken() {
		mUid = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_UID);
		mSid = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_SID);
		mUpdateTokenUrl = ConstantValue.COMMONURI + ConstantValue.UPDATE_TOKEN;
		JSONObject jsonObject = getUpdateTokenJSONObeject();
		initData(mUpdateTokenUrl, jsonObject);
	}

	private JSONObject getUpdateTokenJSONObeject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("device_token", mCid);
			if (TextUtils.isEmpty(mUid)) {
				json.put("uid", 0); // 以游客身份获取数据
			} else {
				json.put("uid", mUid);
			}
			if (TextUtils.isEmpty(mSid)) {
				json.put("sid", ""); // 以游客身份获取数据
			} else {
				json.put("sid", mSid);
			}
			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.login_bt:
			gotoLoginActivity();
			break;
		case R.id.regiester_bt:
			gotoRegiesterActivity();
			break;
		case R.id.other_login_tv:
			showOtherLogin();
			break;
		case R.id.free_rl:
			gotoMainActivity();
			break;
		case R.id.other_login_weixin:
			loginForWeiXin();
			hideOtherLogin();
			break;
		case R.id.other_login_qq:
			loginForQQ();
			hideOtherLogin();
			break;
		case R.id.other_login_weibo:
			loginForWeiBo();
			hideOtherLogin();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (other_login_weixin.isShow()) {
				hideOtherLogin();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		switch (requestCode) {
		case LoginAndRegiesterActivity.ACTIVITY_RESULT_LOGIN: // 登录结束返回
			boolean isLoginSuccess = intent.getBooleanExtra(LoginAndRegiesterActivity.KEY_LOGIN, false);
			if (isLoginSuccess) {
				finish();
			}
			break;
		case LoginAndRegiesterActivity.ACTIVITY_RESULT_REGIESTER: // 注册结束返回
			boolean isRegiesterSuccess = intent.getBooleanExtra(LoginAndRegiesterActivity.KEY_REGIESTER, false);
			if (isRegiesterSuccess) {
				finish();
			}
			break;
		default:
			break;
		}
		if (mSsoHandler != null) {// 新浪微博 SSO 登录授权时，需要加上
			mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
		}
	}

	/**
	 * 跳转到登录页面
	 */
	private void gotoLoginActivity() {
		Intent intent = new Intent(LoginAndRegiesterActivity.this, LoginActivity.class);
		startActivityForResult(intent, LoginAndRegiesterActivity.ACTIVITY_RESULT_LOGIN);
		overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
	}

	/**
	 * 跳转到注册页面
	 */
	private void gotoRegiesterActivity() {
		Intent intent = new Intent(LoginAndRegiesterActivity.this, RegiesterStep1Activity.class);
		startActivityForResult(intent, LoginAndRegiesterActivity.ACTIVITY_RESULT_REGIESTER);
		overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
	}

	/**
	 * 显示第三方登录
	 */
	private void showOtherLogin() {
		mHandler.removeMessages(LoginAndRegiesterActivity.MSG_SHOW_WEIXIN_BT);
		mHandler.sendEmptyMessage(LoginAndRegiesterActivity.MSG_SHOW_WEIXIN_BT);
	}

	/**
	 * 隐藏第三方登录
	 */
	private void hideOtherLogin() {
		mHandler.removeMessages(LoginAndRegiesterActivity.MSG_HIDE_WEIBO_BT);
		mHandler.sendEmptyMessage(LoginAndRegiesterActivity.MSG_HIDE_WEIBO_BT);
	}

	/**
	 * 授权新浪微博登录
	 */
	private void loginForWeiBo() {
		mCurrentAccountType = GlobalParams.LOGIN_TYPE_WEIBO;
		mSsoHandler.authorize(new MyWeiboAuthListener());
	}

	/**
	 * 授权微信登录
	 */
	private void loginForWeiXin() {
		mCurrentAccountType = GlobalParams.LOGIN_TYPE_WEIXIN;
		SendAuth.Req req = new SendAuth.Req();
		req.openId = GlobalParams.WEI_XIN_APP_ID;
		req.scope = "snsapi_userinfo";
		req.state = "collections";
		mWeiXinApi.sendReq(req);
	}

	/**
	 * 授权QQ登录
	 */
	private void loginForQQ() {
		mCurrentAccountType = GlobalParams.LOGIN_TYPE_QQ;
		mTencent.login(this, "all", new MyGetQQTokenListener());
	}

	/**
	 * 获取微信token
	 */
	private void getWeiXinToKen() {
		String weixin_code = SharedPreferencesUtils.getInstance(this).getString(GlobalParams.WEI_XIN_CODE_KEY);
		if (TextUtils.isEmpty(weixin_code)) {
			ToastUtils.showToast(mContext, "微信授权失败，稍后在试.");
			return;
		}
		mGetWeixinTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + GlobalParams.WEI_XIN_APP_ID + "&secret="
				+ GlobalParams.WEI_XIN_APP_SECRET + "&code=" + weixin_code + "&grant_type=authorization_code";
		initDataGet(mGetWeixinTokenUrl);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (url.equals(mGetWeixinTokenUrl)) {// 获取微信token成功
			try {
				JSONObject obj = new JSONObject(result);
				mWeiXinToken = obj.optString("access_token");
				mWeiXinRefreshToken = obj.optString("refresh_token");
				mWeiXinOpenid = obj.optString("openid");
				saveWeiXinToken();
				getWeiXinUserInfo();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (url.equals(mGetAuthorizeUserInfoUrl)) {// 获取授权微信用户信息成功
			WeiXinUser mWeiXinUser = GsonUtils.json2bean(result, WeiXinUser.class);
			mNick = mWeiXinUser.getNickname();
			mAvatar = mWeiXinUser.getHeadimgurl();
			mGender = mWeiXinUser.getSex();
			mLocation = mWeiXinUser.getProvince();
			login();
		} else if (url.equals(mOpenLoginUrl)) {// 登录成功
			try {
				JSONObject object = new JSONObject(result);
				int ret = object.optInt("ret");
				if (ret == 0) {// 登录成功
					saveUser(result);
					setUserLoginState();
					EMManager.getInstance().login(new MyEMCallBack());
					gotoMainActivity();
				} else if (ret == -1) {// 登录失败
					ToastUtils.showToast(mContext, "用户名或密码错误.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (url.equals(mUpdateTokenUrl)) {
			try {
				JSONObject object = new JSONObject(result);
				int ret = object.getInt("ret");
				int status = object.getJSONObject("response").getInt("status");
				if (ret == 0 && status == 1) {// 更新成功
				} else {// 无更新
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void initDataOnFailure(String url) {
		super.initDataOnFailure(url);
	}

	/**
	 * 存储微信授权用户token相关信息
	 */
	private void saveWeiXinToken() {
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(mContext);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_XIN_ACCESS_TOKEN_KEY, mWeiXinToken);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_XIN_REFRESH_TOKEN_KEY, mWeiXinRefreshToken);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_XIN_OPENID_KEY, mWeiXinOpenid);
	}

	/**
	 * 存储QQ授权用户token相关信息
	 */
	private void saveQQToken() {
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(mContext);
		sharedPreferencesUtils.saveString(GlobalParams.QQ_OPENID_KEY, mQQOpenid);
		sharedPreferencesUtils.saveString(GlobalParams.QQ_ACCESS_TOKEN_KEY, mQQAccessToken);
		sharedPreferencesUtils.saveLong(GlobalParams.QQ_EXPIRES_TIME_KEY, mQQExpiresIn);
	}

	/**
	 * 存储微博授权用户token相关信息
	 */
	private void saveWeiBoToken() {
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(mContext);
		sharedPreferencesUtils.saveBoolean(GlobalParams.WEI_BO_LOGIN, true);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_BO_TOKEN_KEY, mWeiBoToken);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_BO_UID_KEY, mWeiBoUid);
		sharedPreferencesUtils.saveLong(GlobalParams.WEI_BO_EXPIRES_TIME_KEY, mExpiresTime);
	}

	/**
	 * 获取微信授权用户的个人资料
	 */
	private void getWeiXinUserInfo() {
		mGetAuthorizeUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + mWeiXinToken + "&openid=" + mWeiXinOpenid;
		initDataGet(mGetAuthorizeUserInfoUrl);
	}

	/**
	 * 获取qq授权用户个人资料
	 */
	private void getQQUserInfo() {
		UserInfo mInfo = new UserInfo(this, mTencent.getQQToken());
		mInfo.getUserInfo(new MyGetUserInfoQQListener());
	}

	/**
	 * 获取微博授权用户的个人资料
	 */
	private void getWeiBoUserInfo() {
		String url = "https://api.weibo.com/2/users/show.json";
		WeiboParameters params = new WeiboParameters(GlobalParams.WEI_BO_APP_KEY);
		params.put("uid", mWeiBoUid);
		params.put("access_token", mWeiBoToken);
		new AsyncWeiboRunner(mContext).requestAsync(url, params, "GET", new MyRequestListener());
	}

	private void login() {
		mOpenLoginUrl = ConstantValue.COMMONURI + ConstantValue.OPEN_LOGIN;
		JSONObject openLoginJson = getOpenLoginJson();
		initData(mOpenLoginUrl, openLoginJson);
	}

	/**
	 * 请求数据 json={"uid":"","sid":"","ver":"1", "request":{ "account_type":1,
	 * "account_info"
	 * :{"openid":"fo0imtsj9k5ealkrdllc6uo6r3","nick":"大大","avatar"
	 * :"http://tp4.sinaimg.cn/1642909335/50/22867541466/0"
	 * ,"gender":"1","location":"北京"},
	 * "summery":{"device":"iPhone 4G","osver":"iOS 4.3.2"
	 * ,"appver":"1.0beta","uuid":"0123456789ABCDEF"} } }
	 */
	private JSONObject getOpenLoginJson() {

		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		JSONObject account_info = new JSONObject();
		try {

			switch (mCurrentAccountType) {
			case GlobalParams.LOGIN_TYPE_WEIXIN:
				account_info.put("openid", mWeiXinOpenid);
				break;
			case GlobalParams.LOGIN_TYPE_QQ:
				account_info.put("openid", mQQOpenid);
				break;
			case GlobalParams.LOGIN_TYPE_WEIBO:
				account_info.put("openid", mWeiBoUid);
				break;
			}
			account_info.put("nick", mNick);
			account_info.put("gender", mGender);
			account_info.put("location", mLocation);
			account_info.put("avatar", mAvatar);
			request.put("account_info", account_info);
			request.put("account_type", mCurrentAccountType);
			json.put("ver", GlobalParams.ver); // TODO 版本号暂时写死
			json.put("request", request);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void saveUser(String result) {
		User user = GsonUtils.json2bean(result, User.class);
		mMyApplication.setUser(user);
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
		sharedPreferencesUtils.saveString(GlobalParams.USER, result);
		sharedPreferencesUtils.saveString(GlobalParams.USER_UID, user.response.uid);
		sharedPreferencesUtils.saveString(GlobalParams.USER_SID, user.response.sid);
		GlobalParams.EM_NAME = GlobalParams.EM_NAME_PRE + user.response.uid;
	}

	private void setUserLoginState() {
		SharedPreferencesUtils.getInstance(this).saveBoolean(GlobalParams.IS_FIRST_LOGIN, false);
		SharedPreferencesUtils.getInstance(this).saveBoolean(GlobalParams.IS_FIRST_REGIESTER_LOGIN, true);
	}

	private void gotoMainActivity() {
		Intent intent = new Intent(LoginAndRegiesterActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	private class MyGetQQTokenListener implements IUiListener {

		@Override
		public void onComplete(Object obj) {
			try {
				JSONObject jsonObj = new JSONObject(obj.toString());
				mQQExpiresIn = jsonObj.optLong("expires_in", 0l);
				getQQUserInfo();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onError(UiError uiError) {
		}
	}

	private class MyGetUserInfoQQListener implements IUiListener {

		@Override
		public void onComplete(Object obj) {
			QQUser user = GsonUtils.json2bean(obj.toString(), QQUser.class);
			mNick = user.getNickname();
			mLocation = user.getProvince();
			mAvatar = user.getFigureurl_qq_2();
			String gender = user.getGender();
			if ("男".equals(gender)) {
				mGender = "1";
			} else {
				mGender = "0";
			}
			mQQOpenid = mTencent.getOpenId();
			mQQAccessToken = mTencent.getAccessToken();
			saveQQToken();
			login();
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onError(UiError uiError) {
		}
	}

	private class MyWeiboAuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				mWeiBoToken = mAccessToken.getToken();
				mWeiBoUid = mAccessToken.getUid();
				mExpiresTime = mAccessToken.getExpiresTime();
				saveWeiBoToken();
				getWeiBoUserInfo();
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = getString(R.string.weibosdk_demo_toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				ToastUtils.showToast(mContext, message, ToastUtils.LONG);
			}
		}

		@Override
		public void onCancel() {
			ToastUtils.showToast(mContext, "取消新浪微博授权");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			ToastUtils.showToast(mContext, "新浪微博授权失败");
		}
	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private class MyRequestListener implements RequestListener {
		@Override
		public void onComplete(String response) {
			WeiBoUser user = WeiBoUser.parse(response);
			mAvatar = user.profile_image_url;
			mNick = user.screen_name;
			mLocation = user.location;
			if ("m".equals(user.gender)) {
				mGender = "1";
			} else {
				mGender = "0";
			}
			login();
		}

		@Override
		public void onWeiboException(WeiboException e) {
		}
	}

	private class MyEMCallBack implements EMCallBack {
		@Override
		public void onProgress(int arg0, String arg1) {

		}

		@Override
		public void onSuccess() {

		}

		@Override
		public void onError(int arg0, String arg1) {

		}
	}

}
