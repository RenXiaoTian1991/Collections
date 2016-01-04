package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.bean.WeiBoUser;
import com.edaoyou.collections.bean.WeiXinUser;
import com.edaoyou.collections.engine.EMManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

public class WelcomeActivity extends BaseActivity {

	private String mNick;// 授权用户名
	private String mAvatar;// 授权用户头像
	private String mGender;// 授权用户性别
	private String mLocation;// 授权用户城市

	private String mOpenLoginUrl;
	private String mRefreshTokenUrl;// 刷新微信token的url
	private String mGetAuthorizeUserInfoUrl;// 获取授权微信用户资料的url
	private String mWeiXinToken;
	private String mWeiXinOpenid;

	private SsoHandler mSsoHandler;
	private String mWeiBoToken;
	private String mWeiBoUid;
	private long mExpiresTime; // 新浪微博token有效期

	private int mCurrentAccountType;// 当前登录的类型

	private long mAppStartTime;
	private static final int GOTO_DELAYED_TIME = 1500;

	private static final int MSG_GOTO_LOGIN_AND_REGIESTER = 1;// 跳转到注册和登录页面
	private static final int MSG_GOTO_MAIN = 2;// 跳转到主界面
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GOTO_LOGIN_AND_REGIESTER:
				gotoLoginAndRegiesterActivity();
				break;
			case MSG_GOTO_MAIN:
				gotoMainActivity();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppStartTime = System.currentTimeMillis();
		getLoginUrl();
		setUserLoginState();
		checkLogin();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_welcome;
	}

	@Override
	protected void findViews() {

	}

	@Override
	protected void setListensers() {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		if (mSsoHandler != null) {// 新浪微博 SSO 登录授权时，需要加上
			mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
		}
	}

	private void setUserLoginState() {
		SharedPreferencesUtils.getInstance(WelcomeActivity.this).saveBoolean(GlobalParams.IS_FIRST_LOGIN, false);
		SharedPreferencesUtils.getInstance(WelcomeActivity.this).saveBoolean(GlobalParams.IS_FIRST_REGIESTER_LOGIN, false);
	}

	private void getLoginUrl() {
		mOpenLoginUrl = ConstantValue.COMMONURI + ConstantValue.OPEN_LOGIN;
	}

	private void checkLogin() {
		int loginState = UserUtil.checkUserIsLogin(this);
		switch (loginState) {
		case GlobalParams.LOGIN_TYPE_WEIBO:
			mCurrentAccountType = GlobalParams.LOGIN_TYPE_WEIBO;
			loginForWeibo();
			break;
		case GlobalParams.LOGIN_TYPE_WEIXIN:
			mCurrentAccountType = GlobalParams.LOGIN_TYPE_WEIXIN;
			loginForWeiXin();
			break;
		case GlobalParams.LOGIN_TYPE_QQ:
			mCurrentAccountType = GlobalParams.LOGIN_TYPE_QQ;
			saveUser();
			loginForEM();
			mHandler.sendEmptyMessageDelayed(MSG_GOTO_MAIN, GOTO_DELAYED_TIME);
			break;
		case GlobalParams.LOGIN_TYPE_COLLECTIONS:
			saveUser();
			loginForEM();
			mHandler.sendEmptyMessageDelayed(MSG_GOTO_MAIN, GOTO_DELAYED_TIME);
			break;
		case GlobalParams.LOGIN_TYPE_NULL:
			mHandler.sendEmptyMessageDelayed(MSG_GOTO_LOGIN_AND_REGIESTER, GOTO_DELAYED_TIME);
			break;
		default:
			mHandler.sendEmptyMessageDelayed(MSG_GOTO_LOGIN_AND_REGIESTER, GOTO_DELAYED_TIME);
			break;
		}
	}

	/**
	 * 刷新微信的token有效期
	 */
	private void loginForWeiXin() {
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
		String refresh_token = sharedPreferencesUtils.getString(GlobalParams.WEI_XIN_REFRESH_TOKEN_KEY);
		mRefreshTokenUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + GlobalParams.WEI_XIN_APP_ID
				+ "&grant_type=refresh_token&refresh_token=" + refresh_token;
		initDataGet(mRefreshTokenUrl);
	}

	/**
	 * 获取微信授权用户的个人资料
	 */
	private void getWeiXinUserInfo() {
		mGetAuthorizeUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + mWeiXinToken + "&openid=" + mWeiXinOpenid;
		initDataGet(mGetAuthorizeUserInfoUrl);
	}

	/**
	 * 微博登录
	 */
	private void loginForWeibo() {
		long expiresTime = SharedPreferencesUtils.getInstance(mContext).getLong(GlobalParams.WEI_BO_EXPIRES_TIME_KEY);
		long currentTimeMillis = System.currentTimeMillis();
		if (expiresTime > currentTimeMillis) {// 说明token有效，无需授权
			saveUser();
			loginForEM();
			mHandler.sendEmptyMessageDelayed(MSG_GOTO_MAIN, GOTO_DELAYED_TIME);
			return;
		}
		AuthInfo mAuthInfo = new AuthInfo(this, GlobalParams.WEI_BO_APP_KEY, GlobalParams.WEI_BO_REDIRECT_URL, null);
		mSsoHandler = new SsoHandler(this, mAuthInfo);
		mSsoHandler.authorize(new MyWeiboAuthListener());
	}

	/**
	 * 环信登录
	 */
	private void loginForEM() {
		String userUid = UserUtil.getUserUid(this);
		GlobalParams.EM_NAME = GlobalParams.EM_NAME_PRE + userUid;
		EMManager.getInstance().login(new MyEMCallBack());
	}

	/**
	 * 第三方登录藏家
	 */
	private void loginForOther() {
		JSONObject openLoginJson = getOpenLoginJson();
		initData(mOpenLoginUrl, openLoginJson);
	}

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

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (url.equals(mRefreshTokenUrl)) {
			try {
				JSONObject obj = new JSONObject(result);
				mWeiXinToken = obj.optString("access_token");
				mWeiXinOpenid = obj.optString("openid");
				String refresh_token = obj.optString("refresh_token");
				String expires_in = obj.optString("expires_in");// token有效期 2个小时
				SharedPreferencesUtils.getInstance(mContext).saveString(GlobalParams.WEI_XIN_ACCESS_TOKEN_KEY, mWeiXinToken);
				SharedPreferencesUtils.getInstance(mContext).saveString(GlobalParams.WEI_XIN_REFRESH_TOKEN_KEY, refresh_token);
				SharedPreferencesUtils.getInstance(mContext).saveString(GlobalParams.WEI_XIN_OPENID_KEY, mWeiXinOpenid);
				getWeiXinUserInfo();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (url.equals(mGetAuthorizeUserInfoUrl)) {
			WeiXinUser mWeiXinUser = GsonUtils.json2bean(result, WeiXinUser.class);
			mNick = mWeiXinUser.getNickname();
			mAvatar = mWeiXinUser.getHeadimgurl();
			mGender = mWeiXinUser.getSex();
			mLocation = mWeiXinUser.getProvince();
			loginForOther();
		} else if (url.equals(mOpenLoginUrl)) {// 登录成功
			try {
				JSONObject object = new JSONObject(result);
				int ret = object.optInt("ret");
				if (ret == 0) {// 登录成功
					saveUser(result);
					loginForEM();
					sendGotoMainMsg();
				} else if (ret == -1) {// 登录失败
					ToastUtils.showToast(mContext, "用户名或密码错误。");
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

	private void saveUser() {
		String userStr = SharedPreferencesUtils.getInstance(this).getString(GlobalParams.USER);
		User user = GsonUtils.json2bean(userStr, User.class);
		mMyApplication.setUser(user);
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

	private void gotoLoginAndRegiesterActivity() {
		Intent intent = new Intent(WelcomeActivity.this, LoginAndRegiesterActivity.class);
		startActivity(intent);
		WelcomeActivity.this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	private void sendGotoMainMsg() {
		long currentTimeMillis = System.currentTimeMillis();
		long runTime = currentTimeMillis - mAppStartTime;
		if (runTime < GOTO_DELAYED_TIME) {
			long delaydeTime = GOTO_DELAYED_TIME - runTime;
			mHandler.sendEmptyMessageDelayed(MSG_GOTO_MAIN, delaydeTime);
		} else {
			mHandler.sendEmptyMessageDelayed(MSG_GOTO_MAIN, 0);
		}
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

	private void gotoMainActivity() {
		Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
		startActivity(intent);
		WelcomeActivity.this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	/**
	 * 环信回调
	 */
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
			loginForOther();
		}

		@Override
		public void onWeiboException(WeiboException e) {
		}
	}

}
