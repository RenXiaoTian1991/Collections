package com.edaoyou.collections.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.view.ScrollableImageView;

public class EmailRegiesterActivity extends BaseActivity implements OnClickListener {

	private ImageView mBlurredImage;
	private ImageView mNormalImage;
	private ScrollableImageView mBlurredImageHeader;
	private static final String BLURRED_IMG_PATH = "blurred_image.png";
	private EditText regiester_email_et;
	private EditText regiester_name_et;
	private EditText regiester_input_pwd_et;
	private Button regiester_and_login_bt;

	private String mCheckUrl; // 检查昵称是否使用的URL
	private String mRegiesterUrl;// 注册的URL
	private String mLoginUrl;// 登录的URL

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUrls();
		initMaoBoli();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_email_regiester;
	}

	@Override
	protected void findViews() {
		mBlurredImage = (ImageView) findViewById(R.id.blurred_image);
		mNormalImage = (ImageView) findViewById(R.id.normal_image);
		mBlurredImageHeader = (ScrollableImageView) findViewById(R.id.blurred_image_header);

		regiester_email_et = (EditText) findViewById(R.id.regiester_email_et);
		regiester_name_et = (EditText) findViewById(R.id.regiester_name_et);
		regiester_input_pwd_et = (EditText) findViewById(R.id.regiester_input_pwd_et);
		regiester_and_login_bt = (Button) findViewById(R.id.regiester_and_login_bt);
	}

	@Override
	protected void setListensers() {
		regiester_and_login_bt.setOnClickListener(this);
	}

	private void initUrls() {
		mCheckUrl = ConstantValue.COMMONURI + ConstantValue.VALIDATE;
		mRegiesterUrl = ConstantValue.COMMONURI + ConstantValue.REGISTER;
		mLoginUrl = ConstantValue.COMMONURI + ConstantValue.LOGIN;
	}

	/**
	 * 初始化毛玻璃效果
	 */
	private void initMaoBoli() {
		final int screenWidth = BimpUtil.getScreenWidth(this);
		mBlurredImageHeader.setScreenWidth(screenWidth);
		final File blurredImage = new File(getFilesDir() + BLURRED_IMG_PATH);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.fast_regiester_bg, options);
		Bitmap newImg = BimpUtil.fastblur(EmailRegiesterActivity.this, image, 12);
		BimpUtil.storeImage(newImg, blurredImage);
		updateView(screenWidth);
		mBlurredImage.setAlpha(0.99f);
	}

	private void updateView(final int screenWidth) {
		Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir() + BLURRED_IMG_PATH);
		bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, screenWidth,
				(int) (bmpBlurred.getHeight() * ((float) screenWidth) / (float) bmpBlurred.getWidth()), false);
		mBlurredImage.setImageBitmap(bmpBlurred);
		mBlurredImageHeader.setoriginalImage(bmpBlurred);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.regiester_and_login_bt:
			String email = regiester_email_et.getText().toString().trim();
			String name = regiester_name_et.getText().toString().trim();
			String pwd = regiester_input_pwd_et.getText().toString().trim();
			if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
				Toast.makeText(mContext, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			checkInfoIsOk();
			break;
		default:
			break;
		}
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (mCheckUrl.equals(url)) {
			onCheckSucess(result);
		} else if (mRegiesterUrl.equals(url)) {
			onRegiesterSuccess(result);
		} else if (mLoginUrl.equals(url)) {
			onLoginSucess(result);
		}
	}

	/**
	 * 检查昵称或者用户名是否被注册过
	 */
	private void checkInfoIsOk() {
		JSONObject checkJson = getCheckJson();
		initData(mCheckUrl, checkJson);
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
		String email = regiester_email_et.getText().toString().trim();
		String password = regiester_input_pwd_et.getText().toString().trim();
		String passwordMD5 = Util.md5(password);
		JSONObject loginJson = getLoginJson(email, passwordMD5);
		initData(mLoginUrl, loginJson);
	}

	private JSONObject getCheckJson() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("username", regiester_name_et.getText().toString().trim());
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
		String username = regiester_name_et.getText().toString().trim();
		String email = regiester_email_et.getText().toString().trim();
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

	private void onCheckSucess(String responseData) {
		try {
			JSONObject object = new JSONObject(responseData);
			int ret = object.optInt("ret");
			if (ret == 0) {// 有数据返回
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				if (status == 1) { // 可注册
					regiester();
				} else {
					Toast.makeText(mContext, "用户名重复", Toast.LENGTH_SHORT).show();
				}
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
					login();
				} else {// 不可注册
					Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
				}
			} else {// 没有数据返回
				Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
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
				EMManager.getInstance().login(new MyEMCallBack());
				gotoMainActivity();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void gotoMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
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
