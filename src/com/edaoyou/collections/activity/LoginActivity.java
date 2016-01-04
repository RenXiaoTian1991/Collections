package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.engine.EMManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.Util;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private Button login_back_bt;
	private Button login_bt;
	private EditText input_name_et;
	private EditText input_pwd_et;
	private TextView forget_pwd_tv;

	private static final int LOGIN_TYPE_EMAILADDRESS = 0;// 表示邮箱登陆
	private static final int LOGIN_TYPE_MOBILE = 1;// 表示手机号登录

	private int mCurrentLoginType = LOGIN_TYPE_MOBILE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_login;
	}

	@Override
	protected void findViews() {
		login_back_bt = (Button) findViewById(R.id.login_back_bt);
		login_bt = (Button) findViewById(R.id.login_bt);
		input_name_et = (EditText) findViewById(R.id.input_name_et);
		input_pwd_et = (EditText) findViewById(R.id.input_pwd_et);
		forget_pwd_tv = (TextView) findViewById(R.id.forget_pwd_tv);
	}

	@Override
	protected void setListensers() {
		login_back_bt.setOnClickListener(this);
		login_bt.setOnClickListener(this);
		forget_pwd_tv.setOnClickListener(this);
		input_pwd_et.addTextChangedListener(new MyPwdTextWatcher());
	}

	private void login() {
		String loginUrl = ConstantValue.COMMONURI + ConstantValue.LOGIN;
		String name = input_name_et.getText().toString().trim();
		String pwd = input_pwd_et.getText().toString().trim();
		String MD5pwd = Util.md5(pwd);
		if (!checkNameAndPwdOK(name, pwd)) {
			return;
		}
		JSONObject loginJson = getLoginJson(name, MD5pwd);
		initData(loginUrl, loginJson);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		try {
			JSONObject object = new JSONObject(result);
			int ret = object.optInt("ret");
			if (ret == 0) {// 登录成功
				saveUser(result);
				notifyPreActivity(true);
				setUserLoginState();
				EMManager.getInstance().login(new MyEMCallBack());
				gotoMainActivity();
			} else if (ret == -1) {// 登录失败
				Toast.makeText(LoginActivity.this, "用户名或密码错误.", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveUser(String result) {
		User user = GsonUtils.json2bean(result, User.class);
		mMyApplication.setUser(user);
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(LoginActivity.this);
		sharedPreferencesUtils.saveString(GlobalParams.USER, result);
		sharedPreferencesUtils.saveString(GlobalParams.USER_UID, user.response.uid);
		sharedPreferencesUtils.saveString(GlobalParams.USER_SID, user.response.sid);
		GlobalParams.EM_NAME = GlobalParams.EM_NAME_PRE + user.response.uid;
	}

	/**
	 * 交互协议 密码要MD5加密 json={"uid":"","sid":"","ver":"1", "request":{
	 * "account_type":0,
	 * "account_info":{"email":"iamgray@gmail.com","mobile":"",
	 * "password":"46f94c8de14fb36680850768ff1b7f2a"},
	 * "summery":{"device":"iPhone 4G"
	 * ,"osver":"iOS 4.3.2","appver":"1.0beta","uuid":"0123456789ABCDEF"} } }
	 */
	private JSONObject getLoginJson(String name, String MD5pwd) {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		JSONObject account_info = new JSONObject();
		try {
			account_info.put("password", MD5pwd);
			if (mCurrentLoginType == LOGIN_TYPE_MOBILE) {
				account_info.put("mobile", name);
			} else {
				account_info.put("email", name);
			}
			request.put("account_info", account_info);
			request.put("account_type", mCurrentLoginType);
			json.put("ver", GlobalParams.ver); // TODO 版本号暂时写死
			json.put("request", request);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkNameAndPwdOK(String name, String pwd) {
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {// 检查用户名或者密码是否是空
			Toast.makeText(LoginActivity.this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (Util.isMobile(name)) {// 检查用户名是否是手机号
			mCurrentLoginType = LoginActivity.LOGIN_TYPE_MOBILE;
			return true;
		}
		if (Util.isEmail(name)) {// 检查用户名是否是邮箱
			mCurrentLoginType = LoginActivity.LOGIN_TYPE_EMAILADDRESS;
			return true;
		}
		Toast.makeText(LoginActivity.this, "请检查用户名", Toast.LENGTH_SHORT).show();
		return false;
	}

	private void notifyPreActivity(boolean isLoginSuccess) {
		Intent intent = getIntent();
		intent.putExtra(LoginAndRegiesterActivity.KEY_LOGIN, isLoginSuccess);
		setResult(0, intent);
	}

	private void gotoMainActivity() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		LoginActivity.this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	private void setUserLoginState() {
		SharedPreferencesUtils.getInstance(LoginActivity.this).saveBoolean(GlobalParams.IS_FIRST_LOGIN, true);
		SharedPreferencesUtils.getInstance(LoginActivity.this).saveBoolean(GlobalParams.IS_FIRST_REGIESTER_LOGIN, false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			notifyPreActivity(false);
			LoginActivity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.login_back_bt:
			notifyPreActivity(false);
			LoginActivity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			break;
		case R.id.login_bt:
			login();
			break;
		case R.id.forget_pwd_tv:
			gotoResetPasswordStep1Activity();
			break;
		default:
			break;
		}
	}

	private void gotoResetPasswordStep1Activity() {
		Intent intent = new Intent(LoginActivity.this, ResetPasswordStep1Activity.class);
		startActivity(intent);
		LoginActivity.this.finish();
		overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
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
			if (temp.length() > 7) { // 当用户输入密码大于7位才让点击登录按钮
				login_bt.setEnabled(true);
			} else {
				login_bt.setEnabled(false);
			}
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
