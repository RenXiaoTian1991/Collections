package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.QQUser;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.bean.WeiBoUser;
import com.edaoyou.collections.engine.EMManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class FastRegiesterActivity extends BaseActivity implements OnClickListener {

	private Button fast_regiester_email;
	private Button fast_regiester_qq;
	private Button fast_regiester_weibo;
	private TextView login_now_tv;
	private Tencent mTencent;// QQapi
	private SsoHandler mSsoHandler;// 新浪微博api

	private String mQQOpenid;
	private String mQQAccessToken;
	private long mQQExpiresIn;// QQtoken过期时间

	private String mNick;// 授权用户名
	private String mAvatar;// 授权用户头像
	private String mGender;// 授权用户性别
	private String mLocation;// 授权用户城市

	private String mWeiBoToken;
	private String mWeiBoUid;
	private long mExpiresTime;// 新浪微博token过期时间

	private String mOpenLoginUrl;
	private int mCurrentAccountType;
	private AuthInfo mAuthInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_fast_regiester;
	}

	@Override
	protected void findViews() {
		fast_regiester_email = (Button) findViewById(R.id.fast_regiester_email);
		fast_regiester_qq = (Button) findViewById(R.id.fast_regiester_qq);
		fast_regiester_weibo = (Button) findViewById(R.id.fast_regiester_weibo);
		login_now_tv = (TextView) findViewById(R.id.login_now_tv);
	}

	@Override
	protected void setListensers() {
		fast_regiester_email.setOnClickListener(this);
		fast_regiester_qq.setOnClickListener(this);
		fast_regiester_weibo.setOnClickListener(this);
		login_now_tv.setOnClickListener(this);
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

	private void gotoLoginActivity() {
		Intent intent = new Intent(FastRegiesterActivity.this, LoginActivity.class);
		FastRegiesterActivity.this.startActivity(intent);
	}

	private void gotoEmailRegiester() {
		Intent intent = new Intent(FastRegiesterActivity.this, EmailRegiesterActivity.class);
		FastRegiesterActivity.this.startActivity(intent);
		overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
	}

	private void gotoMainActivity() {
		Intent intent = new Intent(FastRegiesterActivity.this, MainActivity.class);
		startActivity(intent);
		FastRegiesterActivity.this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			gotoMainActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.fast_regiester_email:
			gotoEmailRegiester();
			break;
		case R.id.fast_regiester_qq:
			loginForQQ();
			break;
		case R.id.fast_regiester_weibo:
			loginWeiBo();
			break;
		case R.id.login_now_tv:
			gotoLoginActivity();
			break;
		default:
			break;
		}
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

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (url.equals(mOpenLoginUrl)) {// 登录成功
			try {
				JSONObject object = new JSONObject(result);
				int ret = object.optInt("ret");
				if (ret == 0) {// 登录成功
					saveUser(result);
					setUserLoginState();
					EMManager.getInstance().login(new MyEMCallBack());
					gotoMainActivity();
				} else if (ret == -1) {// 登录失败
					Toast.makeText(this, "用户名或密码错误.", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

	/**
	 * 授权QQ登录
	 */
	private void loginForQQ() {
		mCurrentAccountType = GlobalParams.LOGIN_TYPE_QQ;
		mTencent = Tencent.createInstance(GlobalParams.QQ_APP_ID, this);
		mTencent.login(this, "all", new MyGetQQTokenListener());
	}

	/**
	 * 获取qq授权用户个人资料
	 */
	private void getQQUserInfo() {
		UserInfo mInfo = new UserInfo(this, mTencent.getQQToken());
		mInfo.getUserInfo(new MyGetUserInfoQQListener());
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
	 * 授权微博登录
	 */
	private void loginWeiBo() {
		mCurrentAccountType = GlobalParams.LOGIN_TYPE_WEIBO;
		mAuthInfo = new AuthInfo(this, GlobalParams.WEI_BO_APP_KEY, GlobalParams.WEI_BO_REDIRECT_URL, null);
		mSsoHandler = new SsoHandler(this, mAuthInfo);
		mSsoHandler.authorize(new MyWeiboAuthListener());
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
	 * 获取微博授权用户的个人资料
	 */
	private void getWeiBoUserInfo() {
		String url = "https://api.weibo.com/2/users/show.json";
		WeiboParameters params = new WeiboParameters(GlobalParams.WEI_BO_APP_KEY);
		params.put("uid", mWeiBoUid);
		params.put("access_token", mWeiBoToken);
		new AsyncWeiboRunner(mContext).requestAsync(url, params, "GET", new MyRequestListener());
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

	private class MyGetQQTokenListener implements IUiListener {

		@Override
		public void onComplete(Object obj) {
			try {
				JSONObject jsonObj = new JSONObject(obj.toString());
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
				Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(mContext, "取消新浪微博授权", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(mContext, "新浪微博授权失败", Toast.LENGTH_SHORT).show();
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
