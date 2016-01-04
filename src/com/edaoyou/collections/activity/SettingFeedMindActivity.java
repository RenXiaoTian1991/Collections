package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;

public class SettingFeedMindActivity extends BaseActivity {
	private TextView save_mind_tv;
	private EditText edit_mind_et;
	private EditText edit_email_et;

	private String mVer;
	private String mSid;
	private String mUid;
	private String mFeedUrl;
	private String mMind = "";
	private String mEmail = "";

	@Override
	protected int setContentView() {
		return R.layout.activity_mind_feedback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mVer = GlobalParams.ver;
		mSid = (String) UserUtil.getUserSid(this);
		mUid = (String) UserUtil.getUserUid(this);
		mFeedUrl = ConstantValue.COMMONURI + ConstantValue.FEEDBACK;
	}

	@Override
	protected void findViews() {
		save_mind_tv = (TextView) findViewById(R.id.save_mind_tv);
		edit_mind_et = (EditText) findViewById(R.id.edit_mind_et);
		edit_email_et = (EditText) findViewById(R.id.edit_email_et);
	}

	@Override
	protected void setListensers() {
		save_mind_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mindFeedBackGiveNet();
			}
		});
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

	private void mindFeedBackGiveNet() {
		mMind = edit_mind_et.getText().toString().trim();
		mEmail = edit_email_et.getText().toString().trim();

		JSONObject jsonObject = getJSONObjectData();
		initData(mFeedUrl, jsonObject);

	}

	private org.json.JSONObject getJSONObjectData() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("email", mEmail);
			request.put("txt", mMind);
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);

		if (TextUtils.isEmpty(mMind)) {
			ToastUtils.showToast(mContext, "内容为空");
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(result);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				ToastUtils.showToast(mContext, "发送成功");
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ToastUtils.showToast(mContext, "请检查网络");
		}
	}
}
