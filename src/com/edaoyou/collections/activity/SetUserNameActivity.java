package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;

public class SetUserNameActivity extends BaseActivity implements OnClickListener {
	private TextView save_user_name_tv;
	private TextView save_user_bio_tv;
	private EditText set_user_name_et;
	private EditText user_bio_et;
	private LinearLayout set_name_delete_ll;

	private String oldUserName;
	private String newUserName;
	private String userBio;

	private int mFlag;
	private String mVer;
	private String mSid;
	private String mUid;
	private String mChangeUserUrl;

	private int NAME = SettingUserActivity.NAME;

	@Override
	protected int setContentView() {
		if (mFlag == NAME) {
			return R.layout.activity_set_user_name;
		} else {
			return R.layout.activity_user_bio;
		}

	}

	@Override
	protected void findViews() {
		save_user_name_tv = (TextView) findViewById(R.id.save_user_name_tv);
		set_user_name_et = (EditText) findViewById(R.id.set_user_name_et);
		user_bio_et = (EditText) findViewById(R.id.user_bio_et);
		save_user_bio_tv = (TextView) findViewById(R.id.save_user_bio_tv);
		set_name_delete_ll = (LinearLayout) findViewById(R.id.set_name_delete_ll);
	}

	@Override
	protected void setListensers() {
		if (mFlag == NAME) {
			save_user_name_tv.setOnClickListener(this);
			set_name_delete_ll.setOnClickListener(this);
		} else {
			save_user_bio_tv.setOnClickListener(this);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void setNameGiveData() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			if (mFlag == NAME) {
				request.put("username", newUserName);
			} else {
				request.put("bio", userBio);
			}
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		initData(mChangeUserUrl, json);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		mFlag = intent.getFlags();
		super.onCreate(savedInstanceState);
		if (mFlag == NAME) {
			oldUserName = intent.getStringExtra("name");
			set_user_name_et.setText(oldUserName);
		}
		mVer = GlobalParams.ver;
		mSid = (String) UserUtil.getUserSid(this);
		mUid = (String) UserUtil.getUserUid(this);
		mChangeUserUrl = ConstantValue.COMMONURI + ConstantValue.CHANGE_PROFILE;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		try {
			JSONObject jsonObject = new JSONObject(result);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");

			if (ret == 0 && status == 1) {
				ToastUtils.showToast(mContext, "修改成功");
				finish();
			} else {
				ToastUtils.showToast(mContext, "网络异常");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_user_name_tv:
			newUserName = set_user_name_et.getText().toString().trim();
			if (TextUtils.isEmpty(newUserName)) {
				ToastUtils.showToast(mContext, "昵称不能为空");
				return;
			}
			setNameGiveData();
			break;
		case R.id.save_user_bio_tv:
			userBio = user_bio_et.getText().toString().trim();
			setNameGiveData();
			break;
		case R.id.set_name_delete_ll:
			set_user_name_et.setText("");
			break;
		default:
			break;
		}
	}
}
