package com.edaoyou.collections.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ListView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.MyTopicsAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.TopicListBen;
import com.edaoyou.collections.bean.TopicListBen.News;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;

/**
 * 订阅
 */
public class MyTopicsActivity extends BaseActivity {

	private String mTopdicListUrl;
	private String mUid;
	private String mSid;
	private String mTopicId;
	private ListView topic_lv;
	private MyTopicsAdapter mMyTopicsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initVariable();
		getTopicsData();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_my_topics;
	}

	@Override
	protected void findViews() {
		topic_lv = (ListView) findViewById(R.id.topic_lv);
	}

	@Override
	protected void setListensers() {

	}

	private void initVariable() {
		mTopicId = getIntent().getStringExtra(GlobalParams.TOPIC_ID);
		mTopdicListUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_LIST;
		mUid = UserUtil.getUserUid(mContext);
		mSid = UserUtil.getUserSid(mContext);
	}

	private void getTopicsData() {
		initData(mTopdicListUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getTopicJsonObject()));
	}

	/**
	 * 得到获取标签数据的JSONObject对象
	 * 
	 * @return
	 */
	private JSONObject getTopicJsonObject() {
		JSONObject request = new JSONObject();
		try {
			request.put("count", 10);
			request.put("last_fid", "0");
			request.put("flag", 0);
			request.put("topic_id", mTopicId);
			return request;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		onTopDataSucess(result);
	}

	private void onTopDataSucess(String result) {
		TopicListBen topicListBen = GsonUtils.json2bean(result, TopicListBen.class);
		List<News> news = topicListBen.response.news;
		if (mMyTopicsAdapter == null) {
			mMyTopicsAdapter = new MyTopicsAdapter(mContext, news, mBitmapUtils);
			topic_lv.setAdapter(mMyTopicsAdapter);
		} else {
			mMyTopicsAdapter.setData(news);
			mMyTopicsAdapter.notifyDataSetChanged();
		}
	}

}
