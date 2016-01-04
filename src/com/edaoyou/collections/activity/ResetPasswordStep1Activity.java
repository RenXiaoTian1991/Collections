package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.utils.SharedPreferencesUtils;

public class ResetPasswordStep1Activity extends BaseActivity implements OnClickListener {

	private EditText regiester_input_pwd_et;
	private Button send_sms_bt;

	private boolean mIsMobileReset; // 是否以手机号方式重置密码

	private String mResetSendSmsUrl; // 以手机号方式重置密码的url
	private String mResetSendEmailUrl; // 以邮箱方式重置密码的url

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getUrls();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_reset_password_step1;
	}

	@Override
	protected void findViews() {
		regiester_input_pwd_et = (EditText) findViewById(R.id.regiester_input_pwd_et);
		send_sms_bt = (Button) findViewById(R.id.send_sms_bt);
	}

	@Override
	protected void setListensers() {
		send_sms_bt.setOnClickListener(this);
		regiester_input_pwd_et.addTextChangedListener(new MyNameTextWatcher());
	}

	private void getUrls() {
		mResetSendSmsUrl = ConstantValue.COMMONURI + ConstantValue.RESET_SEND_SMS;
		mResetSendEmailUrl = ConstantValue.COMMONURI + ConstantValue.RESET_SEND_EMAIL;
	}

	/**
	 * 发送验证码
	 */
	private void sendSms() {
		JSONObject smsJSONObject = getSendSmsJsonObject();
		if (mIsMobileReset) {
			initData(mResetSendSmsUrl, smsJSONObject);
		} else {
			initData(mResetSendEmailUrl, smsJSONObject);
		}
	}

	private JSONObject getSendSmsJsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		String name = regiester_input_pwd_et.getText().toString().trim();
		try {
			if (mIsMobileReset) {
				request.put("mobile", name);
			} else {
				request.put("email", name);
			}
			String reverseName = Util.reverseString(name); // 1.反转字符串
			String MD5key = Util.md5(reverseName).toLowerCase();// 2.md5加密
			String finalKey = Util.reverseString(MD5key).toLowerCase();// 3.反转md5
			request.put("key", finalKey);

			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		try {
			JSONObject object = new JSONObject(result);
			int ret = object.optInt("ret");
			if (ret == 0) {
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				switch (status) {
				case 0:
					Toast.makeText(ResetPasswordStep1Activity.this, "获取验证码失败,请稍后在试", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					String uid = response.optString("uid");
					SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(ResetPasswordStep1Activity.this);
					sharedPreferencesUtils.saveString(GlobalParams.USER_UID, uid);
					if (mIsMobileReset) {
						gotoResetPasswordStep2Activity();
						Toast.makeText(ResetPasswordStep1Activity.this, "正在获取验证码，请注意查收短信", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ResetPasswordStep1Activity.this, "重置密码链接已发送您邮箱，请注意查收", Toast.LENGTH_LONG).show();
						ResetPasswordStep1Activity.this.finish();
						overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
					}
					break;
				default:
					Toast.makeText(ResetPasswordStep1Activity.this, "获取验证码失败,请稍后在试", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void gotoResetPasswordStep2Activity() {
		Intent intent = new Intent(ResetPasswordStep1Activity.this, ResetPasswordStep2Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.send_sms_bt:
			sendSms();
			send_sms_bt.setEnabled(false);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ResetPasswordStep1Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
			if (Util.isMobile(name)) {
				mIsMobileReset = true;
				send_sms_bt.setEnabled(true);
			} else if (Util.isEmail(name)) {
				mIsMobileReset = false;
				send_sms_bt.setEnabled(true);
			} else {
				send_sms_bt.setEnabled(false);
			}
		}
	}
}
