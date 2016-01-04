package com.edaoyou.collections.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.engine.EMManager;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.CollectionBitmapUtils;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class RegiesterStep2Activity extends BaseActivity implements OnClickListener {

	private ImageView regiester_head_iv;// 头像
	private ImageView regiester_nickname_iv;// 昵称对号
	private ImageView regiester_name_iv;// 用户名对号
	private ImageView regiester_pwd_iv;// 密码对号

	private Button regiester_bt; // 注册按钮

	private EditText regiester_input_nickname_et;// 昵称
	private EditText regiester_input_name_et;// 用户名(手机号或者邮箱)
	private EditText regiester_input_pwd_et;// 密码

	private String mCheckUrl; // 检查昵称是否使用的URL
	private String mRegiesterUrl;// 注册的URL
	private String mLoginUrl;// 登录的URL
	private String mImgHerderUrl;// 设置头像的URL

	private Bitmap mBitMapHeader;// 头像的bitmap对象
	private File mPhotoFile;// 头像的路径

	private static final int ACTIVITY_RESULT_STEP3 = 1;

	private static final int MSG_REGIESTER_FAILURE = -1;// 注册失败
	private static final int MSG_CHECK_NAME = 1;// 昵称
	private static final int MSG_CHECK_MOBILE = 2;// 手机号
	private static final int MSG_CHECK_EMAIL = 3;// 邮箱
	private static final int MSG_CHECK_PWD = 4;// 密码
	private static final int MSG_REGIESTER_SUCCESS = 5;// 注册成功

	private boolean mIsNicknameOk; // 最终检查昵称是否可用
	private boolean mIsNameOk; // 最终检查用户名(手机号或者邮箱)是否可用
	private boolean mIsPwdOk; // 最终检查密码是否可用
	private boolean mIsMobileRegiester;// 是否是以手机号的方式注册

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CHECK_NAME:
				mIsNicknameOk = (Boolean) msg.obj;
				if (mIsNicknameOk) {
					regiester_nickname_iv.setVisibility(View.VISIBLE);
				} else {
					regiester_nickname_iv.setVisibility(View.INVISIBLE);
				}
				break;
			case MSG_CHECK_MOBILE:
				mIsNameOk = (Boolean) msg.obj;
				if (mIsNameOk) {
					regiester_name_iv.setVisibility(View.VISIBLE);
				} else {
					regiester_name_iv.setVisibility(View.INVISIBLE);
				}
				break;
			case MSG_CHECK_EMAIL:
				mIsNameOk = (Boolean) msg.obj;
				if (mIsNameOk) {
					regiester_name_iv.setVisibility(View.VISIBLE);
				} else {
					regiester_name_iv.setVisibility(View.INVISIBLE);
				}
				break;
			case MSG_CHECK_PWD:
				mIsPwdOk = (Boolean) msg.obj;
				if (mIsPwdOk) {
					regiester_pwd_iv.setVisibility(View.VISIBLE);
				} else {
					regiester_pwd_iv.setVisibility(View.INVISIBLE);
				}
				break;
			case MSG_REGIESTER_SUCCESS:
				login();
				break;
			case MSG_REGIESTER_FAILURE:
				Toast.makeText(RegiesterStep2Activity.this, "注册失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			isCanRegiester();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initdata();
		initUrl();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_regiester_step2;
	}

	@Override
	protected void findViews() {
		regiester_head_iv = (ImageView) findViewById(R.id.regiester_head_iv);
		regiester_nickname_iv = (ImageView) findViewById(R.id.regiester_nickname_iv);
		regiester_name_iv = (ImageView) findViewById(R.id.regiester_name_iv);
		regiester_pwd_iv = (ImageView) findViewById(R.id.regiester_pwd_iv);

		regiester_input_nickname_et = (EditText) findViewById(R.id.regiester_input_nickname_et);
		regiester_input_name_et = (EditText) findViewById(R.id.regiester_input_name_et);
		regiester_input_pwd_et = (EditText) findViewById(R.id.regiester_input_pwd_et);

		regiester_bt = (Button) findViewById(R.id.regiester_bt);
	}

	@Override
	protected void setListensers() {
		regiester_bt.setOnClickListener(this);
		regiester_input_nickname_et.addTextChangedListener(new MyNickNameTextWatcher());
		regiester_input_name_et.addTextChangedListener(new MyNameTextWatcher());
		regiester_input_pwd_et.addTextChangedListener(new MyPwdTextWatcher());
	}

	/**
	 * 得到上一个页面传过来的图片资源，设置头像.
	 */
	private void initdata() {
		mBitMapHeader = getIntent().getParcelableExtra("data");
		regiester_head_iv.setImageBitmap(CollectionBitmapUtils.getRoundedCornerBitmap(mBitMapHeader));
	}

	private void initUrl() {
		mCheckUrl = ConstantValue.COMMONURI + ConstantValue.VALIDATE;
		mRegiesterUrl = ConstantValue.COMMONURI + ConstantValue.REGISTER;
		mLoginUrl = ConstantValue.COMMONURI + ConstantValue.LOGIN;
	}

	private class MyNickNameTextWatcher implements TextWatcher {
		private CharSequence temp;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			temp = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			sendCheckedMsg(false, RegiesterStep2Activity.MSG_CHECK_NAME);
			String nickName = temp.toString().trim();
			checkInfoIsOk(nickName, RegiesterStep2Activity.MSG_CHECK_NAME);
		}
	}

	private class MyNameTextWatcher implements TextWatcher {
		private CharSequence temp;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			temp = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String name = temp.toString().trim();

			sendCheckedMsg(false, RegiesterStep2Activity.MSG_CHECK_MOBILE);
			sendCheckedMsg(false, RegiesterStep2Activity.MSG_CHECK_EMAIL);

			if (Util.isMobile(name)) {
				mIsMobileRegiester = true;
				checkInfoIsOk(name, RegiesterStep2Activity.MSG_CHECK_MOBILE);
			} else if (Util.isEmail(name)) {
				mIsMobileRegiester = false;
				checkInfoIsOk(name, RegiesterStep2Activity.MSG_CHECK_EMAIL);
			}
		}
	}

	private class MyPwdTextWatcher implements TextWatcher {
		private CharSequence temp;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			temp = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			sendCheckedMsg(false, RegiesterStep2Activity.MSG_CHECK_PWD);
			if (temp.length() > 7) { // 当用户输入密码大于7位才让点击注册按钮
				sendCheckedMsg(true, RegiesterStep2Activity.MSG_CHECK_PWD);
			} else {
				sendCheckedMsg(false, RegiesterStep2Activity.MSG_CHECK_PWD);
			}
		}
	}

	/**
	 * 检查昵称或者用户名是否被注册过
	 */
	private void checkInfoIsOk(String info, final int type) {
		JSONObject checkJson = getCheckJson(info, type);
		initData(mCheckUrl, checkJson, type);
	}

	/**
	 * 注册
	 */
	private void regiester() {
		JSONObject regiesterJson = getRegiesterJson();
		initData(mRegiesterUrl, regiesterJson);
	}

	/**
	 * 登录
	 */
	private void login() {
		String email = regiester_input_name_et.getText().toString().trim();
		String password = regiester_input_pwd_et.getText().toString().trim();
		String passwordMD5 = Util.md5(password);
		JSONObject loginJson = getLoginJson(email, passwordMD5);
		initData(mLoginUrl, loginJson);
	}

	private JSONObject getCheckJson(String info, int type) {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			switch (type) {
			case RegiesterStep2Activity.MSG_CHECK_NAME:
				request.put("username", info);
				break;
			case RegiesterStep2Activity.MSG_CHECK_MOBILE:
				request.put("mobile", info);
				break;
			case RegiesterStep2Activity.MSG_CHECK_EMAIL:
				request.put("email", info);
				break;
			default:
				break;
			}
			json.put("uid", "");
			json.put("sid", "");
			json.put("ver", GlobalParams.ver); // TODO 版本号暂时写死
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 用户注册 接口 user/register
	 * 
	 * 请求数据 json={"uid":"","sid":"","ver":"1", "request":{ "username":"aixue",
	 * "password":"e10adc3949ba59abbe56e057f20f883e", "mobile":"13512345431",
	 * "email":"zhangs@gmail.com", "device":"", "uuid":"", "location":"北京",
	 * //所在地 "gender":"1", //性别 1男 0女 } }
	 */
	private JSONObject getRegiesterJson() {
		String username = regiester_input_nickname_et.getText().toString().trim();
		String email = regiester_input_name_et.getText().toString().trim();
		String password = regiester_input_pwd_et.getText().toString().trim();
		String passwordMD5 = Util.md5(password);

		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("username", username);
			request.put("password", passwordMD5);
			request.put("email", email);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getLoginJson(String name, String MD5pwd) {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		JSONObject account_info = new JSONObject();
		try {
			account_info.put("password", MD5pwd);
			account_info.put("email", name);
			request.put("account_info", account_info);
			request.put("account_type", "0");
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
		if (mCheckUrl.equals(url)) {
			OnCheckSucess(result, type);
		} else if (mRegiesterUrl.equals(url)) {
			onRegiesterSuccess(result);
		} else if (mLoginUrl.equals(url)) {
			onLoginSucess(result);
		}
	}

	private void OnCheckSucess(String responseData, int type) {
		try {
			JSONObject object = new JSONObject(responseData);
			int ret = object.optInt("ret");
			if (ret == 0) {// 有数据返回
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				if (status == 1) { // 可注册
					sendCheckedMsg(true, type);
				} else {// 不可注册
					showToast(type);
					sendCheckedMsg(false, type);
				}
			} else {// 没有数据返回
				sendCheckedMsg(false, type);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void onRegiesterSuccess(String responseData) {
		try {
			JSONObject object = new JSONObject(responseData);
			int ret = object.optInt("ret");
			if (ret == 0) {// 有数据返回
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				if (status == 1) { // 注册成功
					mHandler.sendEmptyMessage(RegiesterStep2Activity.MSG_REGIESTER_SUCCESS);
				} else {// 不可注册
					mHandler.sendEmptyMessage(RegiesterStep2Activity.MSG_REGIESTER_FAILURE);
				}
			} else {// 没有数据返回
				mHandler.sendEmptyMessage(RegiesterStep2Activity.MSG_REGIESTER_FAILURE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void onLoginSucess(String responseData) {
		try {
			JSONObject object = new JSONObject(responseData);
			int ret = object.optInt("ret");
			if (ret == 0) {// 登录成功
				saveUser(responseData);
				setUserLoginState();
				setUserHeader();
				notifyPreActivity(true);
				EMManager.getInstance().login(new MyEMCallBack());
				gotoMainActivity();
			}
		} catch (JSONException e) {
			e.printStackTrace();
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

	private void showToast(int type) {
		switch (type) {
		case RegiesterStep2Activity.MSG_CHECK_NAME:
			Toast.makeText(RegiesterStep2Activity.this, "该昵称已注册", Toast.LENGTH_SHORT).show();
			break;
		case RegiesterStep2Activity.MSG_CHECK_MOBILE:
			Toast.makeText(RegiesterStep2Activity.this, "该手机号已注册", Toast.LENGTH_SHORT).show();
			break;
		case RegiesterStep2Activity.MSG_CHECK_EMAIL:
			Toast.makeText(RegiesterStep2Activity.this, "该邮箱已注册", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	private void sendCheckedMsg(boolean isSuccess, int type) {
		Message msg = Message.obtain();
		msg.what = type;
		msg.obj = isSuccess;
		mHandler.removeMessages(type);
		mHandler.sendMessage(msg);
	}

	/**
	 * 检查是否可以登录
	 */
	private void isCanRegiester() {
		if (mIsNicknameOk && mIsNameOk && mIsPwdOk) {
			regiester_bt.setEnabled(true);
		} else {
			regiester_bt.setEnabled(false);
		}
	}

	/**
	 * 通知上一个Activity结果
	 */
	private void notifyPreActivity(boolean isRegiesterSuccess) {
		Intent intent = getIntent();
		intent.putExtra(LoginAndRegiesterActivity.KEY_REGIESTER, isRegiesterSuccess);
		setResult(0, intent);
	}

	private void gotoMainActivity() {
		Intent intent = new Intent(RegiesterStep2Activity.this, MainActivity.class);
		startActivity(intent);
		RegiesterStep2Activity.this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	private void gotoRegiesterStep3Activity() {
		Intent intent = new Intent(RegiesterStep2Activity.this, RegiesterStep3Activity.class);
		intent.putExtra("username", regiester_input_nickname_et.getText().toString().trim());
		intent.putExtra("phoneNumber", regiester_input_name_et.getText().toString().trim());
		intent.putExtra("password", regiester_input_pwd_et.getText().toString().trim());
		startActivityForResult(intent, RegiesterStep2Activity.ACTIVITY_RESULT_STEP3);
		overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
	}

	private void setUserLoginState() {
		SharedPreferencesUtils.getInstance(RegiesterStep2Activity.this).saveBoolean(GlobalParams.IS_FIRST_LOGIN, false);
		SharedPreferencesUtils.getInstance(RegiesterStep2Activity.this).saveBoolean(GlobalParams.IS_FIRST_REGIESTER_LOGIN, true);
	}

	// 此方法上传头像
	private void setUserHeader() {
		try {
			if (mBitMapHeader == null) {
				return;
			}
			mPhotoFile = BimpUtil.saveCompressedBitmap(mContext, mBitMapHeader);
			mImgHerderUrl = ConstantValue.COMMONURI + ConstantValue.CHANGE_AVATAR;
		} catch (IOException e) {
			e.printStackTrace();
		}
		getJSONObjectHeader();
	}

	private void getJSONObjectHeader() {
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(mContext);
		mHttpUtils = xUtilsManager.getHttpUtils();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("uid", UserUtil.getUserUid(mContext));
			jsonObject.put("sid", UserUtil.getUserSid(mContext));
			jsonObject.put("ver", GlobalParams.ver);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("json", jsonObject.toString());
		try {
			requestParams.addBodyParameter("file", new FileInputStream(mPhotoFile), mPhotoFile.length(), mPhotoFile.getName(),
					"application/octet-stream");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mHttpUtils.send(HttpMethod.POST, mImgHerderUrl, requestParams, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				initDataOnStart(mImgHerderUrl);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				ToastUtils.showToast(mContext, "获取服务器数据失败");
				initDataOnFailure(mImgHerderUrl);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case RegiesterStep2Activity.ACTIVITY_RESULT_STEP3:
			boolean isRegiesterSuccess = intent.getBooleanExtra(LoginAndRegiesterActivity.KEY_REGIESTER, false);
			if (isRegiesterSuccess) {
				setUserHeader();
				notifyPreActivity(true);
				RegiesterStep2Activity.this.finish();
			} else {
				notifyPreActivity(false);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.regiester_bt:
			if (mIsMobileRegiester) {
				gotoRegiesterStep3Activity();
			} else {
				regiester();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			notifyPreActivity(false);
			RegiesterStep2Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
