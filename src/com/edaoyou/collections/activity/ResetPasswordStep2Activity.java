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
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.utils.SharedPreferencesUtils;

public class ResetPasswordStep2Activity extends BaseActivity implements OnClickListener {

	private String resetPwdUrl;
	private Button reset_step2_title_back_bt;
	private TextView finish_tv;
	private EditText reset_step2_input_sms_et;
	private EditText reset_step2_input_new_pwd_et;

	private boolean mIsMsmTextOK;
	private boolean mIsPwdOK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getUrl();
	}

	private void getUrl() {
		resetPwdUrl = ConstantValue.COMMONURI + ConstantValue.RESET_PWD;
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_reset_password_step2;
	}

	@Override
	protected void findViews() {
		reset_step2_title_back_bt = (Button) findViewById(R.id.reset_step2_title_back_bt);
		finish_tv = (TextView) findViewById(R.id.finish_tv);
		reset_step2_input_sms_et = (EditText) findViewById(R.id.reset_step2_input_sms_et);
		reset_step2_input_new_pwd_et = (EditText) findViewById(R.id.reset_step2_input_new_pwd_et);
	}

	@Override
	protected void setListensers() {
		reset_step2_title_back_bt.setOnClickListener(this);
		finish_tv.setOnClickListener(this);
		reset_step2_input_sms_et.addTextChangedListener(new MyMsmTextWatcher());
		reset_step2_input_new_pwd_et.addTextChangedListener(new MyNewPwdWatcher());
	}

	private void resetPassword() {
		JSONObject resetPasswordJson = getResetPasswordJson();
		initData(resetPwdUrl, resetPasswordJson);
	}

	/**
	 * 接口 user/reset_pwd
	 * 
	 * 请求数据 json={"uid":"","sid":"","ver":"1",
	 * "request":{"uid":32,"code":"73812"
	 * ,"newpassword":"e10adc3949ba59abbe56e057f20f883e"
	 * ,"key":"46f94c8de14fb36680850768ff1b7f2a"}}
	 * 
	 * 返回数据 {"ret":0,"response":{"status":1}} //1修改成功 0修改失败
	 * 
	 * 备注 发送：key 反转code
	 */
	private JSONObject getResetPasswordJson() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(ResetPasswordStep2Activity.this);
		String uid = sharedPreferencesUtils.getString(GlobalParams.USER_UID);
		String code = reset_step2_input_sms_et.getText().toString().trim();
		String pwd = reset_step2_input_new_pwd_et.getText().toString().trim();
		String passwordMD5 = Util.md5(pwd);

		String reverseCode = Util.reverseString(code); // 1.反转字符串
		String MD5key = Util.md5(reverseCode).toLowerCase();// 2.md5加密
		String finalKey = Util.reverseString(MD5key).toLowerCase();// 3.反转md5

		try {
			request.put("uid", uid);
			request.put("code", code);
			request.put("newpassword", passwordMD5);
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
			if (ret == 0) {// 有数据返回
				JSONObject response = object.optJSONObject("response");
				int status = response.optInt("status");
				if (status == 1) {
					Toast.makeText(ResetPasswordStep2Activity.this, "重置密码成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ResetPasswordStep2Activity.this, "重置密码失败", Toast.LENGTH_SHORT).show();
				}
				gotoLoginAndRegiesterActivity();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void gotoLoginAndRegiesterActivity() {
		Intent intent = new Intent(ResetPasswordStep2Activity.this, LoginAndRegiesterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ResetPasswordStep2Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.reset_step2_title_back_bt:
			ResetPasswordStep2Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			break;
		case R.id.finish_tv:
			resetPassword();
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
				mIsMsmTextOK = true;
			} else {
				mIsMsmTextOK = false;
			}
			if (isCanResetPwd()) {
				finish_tv.setEnabled(true);
			} else {
				finish_tv.setEnabled(false);
			}
		}
	}

	private class MyNewPwdWatcher implements TextWatcher {
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
			if (length > 7) {
				mIsPwdOK = true;
			} else {
				mIsPwdOK = false;
			}
			if (isCanResetPwd()) {
				finish_tv.setEnabled(true);
			} else {
				finish_tv.setEnabled(false);
			}
		}
	}

	private boolean isCanResetPwd() {
		return mIsMsmTextOK && mIsPwdOK;
	}
}
