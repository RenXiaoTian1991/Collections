package com.edaoyou.collections.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.GridView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.HomeTopicsAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Category;
import com.edaoyou.collections.bean.HomeTopics;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;

public class SettingMyAttentionActivity extends BaseActivity {
	private GridView topicdiaplay_gv;
	private String mTopicsUrl;

	@Override
	protected int setContentView() {
		return R.layout.activity_setting_attention;
	}

	@Override
	protected void findViews() {
		topicdiaplay_gv = (GridView) findViewById(R.id.topicdiaplay_gv);
	}

	@Override
	protected void setListensers() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getDataFromNet();
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);

		HomeTopics jsonBean = GsonUtils.json2bean(result, HomeTopics.class);
		List<Category> topic_category = jsonBean.response.topic_category;
		if (topic_category != null && topic_category.size() > 0) {
			topicdiaplay_gv.setAdapter(new HomeTopicsAdapter(this, topic_category));
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

	private void getDataFromNet() {
		mTopicsUrl = ConstantValue.COMMONURI + ConstantValue.TOPICS;
		String mVer = GlobalParams.ver;
		String mSid = (String) UserUtil.getUserSid(this);
		String mUid = (String) UserUtil.getUserUid(this);

		JSONObject json = getJSONObject(mVer, mSid, mUid);
		initData(mTopicsUrl, json);
	}

	private JSONObject getJSONObject(String mVer, String mSid, String mUid) {
		JSONObject json = new JSONObject();
		try {
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
}
