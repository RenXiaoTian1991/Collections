package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.engine.EMManager;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;

public class RegiesterStep3Activity extends BaseActivity implements OnClickListener {

	private EditText regiester_input_sms_et;
	private Button regiester_step3_back_bt;
	private Button again_send_sms_bt;
	private Button regiester_ok_bt;

	private String mSendSmsUrl;
	private String mCheckSmsUrl;
	private String mRegiesterUrl;
	private String mLoginUrl;

	private String mUsername;
	private String mPhoneNumber;
	private String mPassword;

	private static final int mMsmTime = 60;
	private int mCurrentMsmTiem = mMsmTime;

	private static final int MSG_REGIESTER_FAILURE = -1;// 注册失败
	private static final int MSG_MSM_TIME = 1;// 倒计时
	private static final int MSG_REGIESTER_SUCCESS = 2;// 注册成功

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RegiesterStep3Activity.MSG_MSM_TIME:
				if (mCurrentMsmTiem == 0) {
					mCurrentMsmTiem = mMsmTime;
					again_send_sms_bt.setText("重新发送验证码");
					again_send_sms_bt.setEnabled(true);
				} else {
					again_send_sms_bt.setText("重新发送验证码" + "(" + mCurrentMsmTiem + ")");
					Message timeMsg = Message.obtain();
					timeMsg.what = MSG_MSM_TIME;
					mHandler.sendMessageDelayed(timeMsg, 1000);
					mCurrentMsmTiem--;
				}
				break;
			case RegiesterStep3Activity.MSG_REGIESTER_SUCCESS:
				login();
				break;
			case RegiesterStep3Activity.MSG_REGIESTER_FAILURE:
				Toast.makeText(RegiesterStep3Activity.this, "注册失败，请稍后在试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getUserInfo();
		getUrls();
		sendSms();
	}

	/**
	 * 得到上个页面传过来的用户信息
	 */
	private void getUserInfo() {
		Intent intent = getIntent();
		mUsername = intent.getStringExtra("username");
		mPhoneNumber = intent.getStringExtra("phoneNumber");
		mPassword = intent.getStringExtra("password");
	}

	private void getUrls() {
		mSendSmsUrl = ConstantValue.COMMONURI + ConstantValue.SEND_SMS;
		mCheckSmsUrl = ConstantValue.COMMONURI + ConstantValue.CHECK_SMS;
		mRegiesterUrl = ConstantValue.COMMONURI + ConstantValue.REGISTER;
		mLoginUrl = ConstantValue.COMMONURI + ConstantValue.LOGIN;
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_regiester_step3;
	}

	@Override
	protected void findViews() {
		regiester_input_sms_et = (EditText) findViewById(R.id.regiester_input_sms_et);
		regiester_step3_back_bt = (Button) findViewById(R.id.regiester_step3_back_bt);
		again_send_sms_bt = (Button) findViewById(R.id.again_send_sms_bt);
		regiester_ok_bt = (Button) findViewById(R.id.regiester_ok_bt);
	}

	@Override
	protected void setListensers() {
		regiester_step3_back_bt.setOnClickListener(this);
		again_send_sms_bt.setOnClickListener(this);
		regiester_ok_bt.setOnClickListener(this);
		regiester_input_sms_et.addTextChangedListener(new MyMsmTextWatcher());
	}

	/**
	 * 发送验证码
	 */
	private void sendSms() {
		again_send_sms_bt.setEnabled(false);
		mHandler.sendEmptyMessage(RegiesterStep3Activity.MSG_MSM_TIME);
		JSONObject smsJSONObject = getSendSmsJsonObject();
		initData(mSendSmsUrl, smsJSONObject);
	}

	/**
	 * 检查验证码
	 */
	private void checkSms() {
		JSONObject checkSmsJsonObject = getCheckSmsJsonObject();
		initData(mCheckSmsUrl, checkSmsJsonObject);
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
		JSONObject loginJson = getLoginJson();
		initData(mLoginUrl, loginJson);
	}

	/**
	 * 
	 * 请求数据 json={"uid":"","sid":"","ver":"1","request":{"mobile":"13520166164",
	 * "key":"46f94c8de14fb36680850768ff1b7f2a"}}
	 * 
	 * 返回数据 {"ret":0,"response":{"status":1}} //1发送成功 0发送失败
	 * 
	 * 备注 发送：key 反转mobile // 反转规则：1.反转字符串，2.md5加密，3.反转md5加密
	 */
	private JSONObject getSendSmsJsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("mobile", mPhoneNumber);
			String reversePhoneNumber = Util.reverseString(mPhoneNumber); // 1.反转字符串
			String MD5key = Util.md5(reversePhoneNumber).toLowerCase();// 2.md5加密
			String finalKey = Util.reverseString(MD5key).toLowerCase();// 3.反转md5
			request.put("key", finalKey);

			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getCheckSmsJsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("mobile", mPhoneNumber);
			request.put("code", regiester_input_sms_et.getText().toString().trim());
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getRegiesterJson() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		String passwordMD5 = Util.md5(mPassword);
		try {
			request.put("username", mUsername);
			request.put("password", passwordMD5);
			request.put("mobile", mPhoneNumber);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getLoginJson() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		JSONObject account_info = new JSONObject();
		String passwordMD5 = Util.md5(mPassword);
		try {
			account_info.put("password", passwordMD5);
			account_info.put("mobile", mPhoneNumber);
			request.put("account_info", account_info);
			request.put("account_type", "1");
			json.put("ver", GlobalParams.ver);
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
		if (mSendSmsUrl.equals(url)) {
			onSendSmsSucess(result);
		} else if (mCheckSmsUrl.equals(url)) {
			onCheckSmsSucess(result);
		} else if (mRegiesterUrl.equals(url)) {
			onRegiesterSucess(result);
		} else if (mLoginUrl.equals(url)) {
			onLoginSucess(result);
		}
	}

	private void onSendSmsSucess(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int ret = object.optInt("ret");
			if (ret == 0) {
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				switch (status) {
				case 0:
					Toast.makeText(RegiesterStep3Activity.this, "获取验证码失败,请稍后在试", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(RegiesterStep3Activity.this, "正在获取验证码，请注意查收短信", Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(RegiesterStep3Activity.this, "获取验证码失败,请稍后在试", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void onCheckSmsSucess(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int ret = object.optInt("ret");
			if (ret == 0) {
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				switch (status) {
				case 0:
					Toast.makeText(RegiesterStep3Activity.this, "验证失败", Toast.LENGTH_SHORT).show();
					break;
				case 1: // 验证成功
					regiester();
					break;
				default:
					Toast.makeText(RegiesterStep3Activity.this, "验证失败", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void onRegiesterSucess(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int ret = object.optInt("ret");
			if (ret == 0) {// 有数据返回
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				if (status == 1) { // 注册成功
					mHandler.sendEmptyMessage(RegiesterStep3Activity.MSG_REGIESTER_SUCCESS);
				} else {// 不可注册
					mHandler.sendEmptyMessage(RegiesterStep3Activity.MSG_REGIESTER_FAILURE);
				}
			} else {// 没有数据返回
				mHandler.sendEmptyMessage(RegiesterStep3Activity.MSG_REGIESTER_FAILURE);
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

	/**
	 * 通知上一个Activity结果
	 */
	private void notifyPreActivity(boolean isRegiesterSuccess) {
		Intent intent = getIntent();
		intent.putExtra(LoginAndRegiesterActivity.KEY_REGIESTER, isRegiesterSuccess);
		setResult(0, intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			notifyPreActivity(false);
			RegiesterStep3Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void gotoMainActivity() {
		Intent intent = new Intent(RegiesterStep3Activity.this, MainActivity.class);
		startActivity(intent);
		RegiesterStep3Activity.this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	private void setUserLoginState() {
		SharedPreferencesUtils.getInstance(RegiesterStep3Activity.this).saveBoolean(GlobalParams.IS_FIRST_LOGIN, false);
		SharedPreferencesUtils.getInstance(RegiesterStep3Activity.this).saveBoolean(GlobalParams.IS_FIRST_REGIESTER_LOGIN, true);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.regiester_step3_back_bt:
			notifyPreActivity(false);
			RegiesterStep3Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			break;
		case R.id.again_send_sms_bt:
			sendSms();
			break;
		case R.id.regiester_ok_bt:
			regiester_ok_bt.setEnabled(false);
			checkSms();
			break;
		default:
			break;
		}
	}

	private class MyMsmTextWatcher implements TextWatcher {
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
			int length = temp.length();
			if (length == 5) {
				regiester_ok_bt.setEnabled(true);
			} else {
				regiester_ok_bt.setEnabled(false);
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
