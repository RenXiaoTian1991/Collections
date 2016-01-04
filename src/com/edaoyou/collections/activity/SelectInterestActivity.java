package com.edaoyou.collections.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.SelectInterestAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Subscribe;
import com.edaoyou.collections.bean.SubscribeBean;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.etsy.XListView;

/**
 * 选择兴趣
 */
public class SelectInterestActivity extends BaseActivity implements OnClickListener {

	private XListView select_interest_Xlistview;
	private SelectInterestAdapter mSelectInterestAdapter;
	private TextView select_interest_size_tv;
	private TextView select_interest_finish_tv;

	private List<Subscribe> mSubscribes;
	private String mTopicsUrl;
	private String mTopicsFollowUrl;
	private String mTopicsUnFollowUrl;

	private String mUid;
	private String mSid;

	private int mClickLikePosition;

	public static final int MSG_ONCLICK_INTEREST = 1;// 点击了选择兴趣
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SelectInterestActivity.MSG_ONCLICK_INTEREST:
				mClickLikePosition = (Integer) msg.obj;
				checkFollowOrCancelFollow();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTopicsUrl = ConstantValue.COMMONURI + ConstantValue.TOPICS;
		mTopicsFollowUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_FOLLOW;
		mTopicsUnFollowUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_UNFOLLOW;
		mUid = (String) UserUtil.getUserUid(this);
		mSid = (String) UserUtil.getUserSid(this);
		getTopicsData();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_select_interest;
	}

	@Override
	protected void findViews() {
		select_interest_Xlistview = (XListView) findViewById(R.id.select_interest_Xlistview);
		select_interest_size_tv = (TextView) findViewById(R.id.select_interest_size_tv);
		select_interest_finish_tv = (TextView) findViewById(R.id.select_interest_finish_tv);
	}

	@Override
	protected void setListensers() {
		select_interest_Xlistview.setPullRefreshEnable(false);
		select_interest_Xlistview.setPullLoadEnable(false);
		select_interest_finish_tv.setOnClickListener(this);
		select_interest_Xlistview.setOnItemClickListener(new MyOnItemClickListener());
	}

	private void gotoMainActivity() {
		setUserLoginState();
		Intent intent = new Intent(SelectInterestActivity.this, MainActivity.class);
		startActivity(intent);
		SelectInterestActivity.this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	private void setUserLoginState() {
		SharedPreferencesUtils.getInstance(SelectInterestActivity.this).saveBoolean(GlobalParams.IS_FIRST_LOGIN, false);
		SharedPreferencesUtils.getInstance(SelectInterestActivity.this).saveBoolean(GlobalParams.IS_FIRST_REGIESTER_LOGIN, false);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (mTopicsUrl.equals(url)) {
			refreshListData(result);
		} else if (mTopicsFollowUrl.equals(url)) {
			paserFollowJson(result, url);
		} else if (mTopicsUnFollowUrl.equals(url)) {
			paserFollowJson(result, url);
		}
	}

	private void refreshListData(String result) {
		SubscribeBean subscribe = GsonUtils.json2bean(result, SubscribeBean.class);
		mSubscribes = subscribe.response.topic_category;
		int followSize = getFollowSize();
		select_interest_size_tv.setText("选择兴趣" + "(" + followSize + ")");
		if (mSelectInterestAdapter == null) {
			mSelectInterestAdapter = new SelectInterestAdapter(mContext, mBitmapUtils, mHandler, mSubscribes);
			select_interest_Xlistview.setAdapter(mSelectInterestAdapter);
		} else {
			mSelectInterestAdapter.setData(mSubscribes);
			mSelectInterestAdapter.notifyDataSetChanged();
		}
	}

	private void checkFollowOrCancelFollow() {
		String is_followed = mSubscribes.get(mClickLikePosition).is_followed;
		if ("1".equals(is_followed)) {
			cancelLike();
		} else {
			doFollowed();
		}
	}

	private void getTopicsData() {
		JSONObject topicsJsonObject = getTopicsObject();
		initData(mTopicsUrl, topicsJsonObject);
	}

	private void cancelLike() {
		JSONObject followObject = getFollowObject();
		initData(mTopicsUnFollowUrl, followObject);
	}

	private void doFollowed() {
		JSONObject followObject = getFollowObject();
		initData(mTopicsFollowUrl, followObject);
	}

	private void paserFollowJson(String result, String url) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONObject responseJSONObject = jsonObject.optJSONObject("response");
			int status = responseJSONObject.optInt("status");
			if (status == 1) {
				getTopicsData();
			} else {
				Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
		}
	}

	private JSONObject getTopicsObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", GlobalParams.ver);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getFollowObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();

		try {
			request.put("topic_id", mSubscribes.get(mClickLikePosition).topic_id);
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 得到点赞的个数
	 */
	private int getFollowSize() {
		int size = 0;
		for (int i = 0; i < mSubscribes.size(); i++) {
			String is_followed = mSubscribes.get(i).is_followed;
			if ("1".equals(is_followed)) {
				size++;
			}
		}
		return size;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			gotoMainActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.select_interest_finish_tv:
			gotoMainActivity();
			break;

		default:
			break;
		}
	}

	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i("abc", "position-->" + position);
		}
	}

}
