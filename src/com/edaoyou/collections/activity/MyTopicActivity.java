package com.edaoyou.collections.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.MyTopicAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Subscribe;
import com.edaoyou.collections.bean.SubscribeBean;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;

/**
 * 订阅号
 */
public class MyTopicActivity extends BaseActivity {
	private ListView my_topic_lv;
	private String mMyTopicsUrl;
	private String mUid;
	private String mSid;
	private List<Subscribe> mSubscribe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initVariable();
		getMyTopicsData();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_my_topic;
	}

	@Override
	protected void findViews() {
		my_topic_lv = (ListView) findViewById(R.id.my_topic_lv);
	}

	@Override
	protected void setListensers() {
		my_topic_lv.setOnItemClickListener(new MyOnItemClickListener());
	}

	private void initVariable() {
		mMyTopicsUrl = ConstantValue.COMMONURI + ConstantValue.MY_TOPICS;
		mUid = UserUtil.getUserUid(mContext);
		mSid = UserUtil.getUserSid(mContext);
	}

	private void getMyTopicsData() {
		JSONObject myTopicsJsonObject = getMyTopicsJsonObject();
		initData(mMyTopicsUrl, myTopicsJsonObject);
	}

	private JSONObject getMyTopicsJsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			json.put("sid", mSid);
			json.put("uid", mUid);
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
		if (url.equals(mMyTopicsUrl)) {
			onMyTopicsSucess(result);
		}
	}

	private void onMyTopicsSucess(String result) {
		SubscribeBean jsonBean = GsonUtils.json2bean(result, SubscribeBean.class);
		mSubscribe = jsonBean.response.topic_category;
		MyTopicAdapter mMyTopicAdapter = new MyTopicAdapter(mContext, mSubscribe, mBitmapUtils);
		my_topic_lv.setAdapter(mMyTopicAdapter);
	}

	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(mContext, MyTopicsActivity.class);
			intent.putExtra(GlobalParams.TOPIC_ID, mSubscribe.get(position).topic_id);
			startActivity(intent);
		}

	}
}
