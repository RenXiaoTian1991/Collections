package com.edaoyou.collections.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.PersomalAttentionAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Follow;
import com.edaoyou.collections.bean.FollowList;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.XGridView;

public class PersonalAttentionActivity extends BaseActivity implements com.edaoyou.collections.view.XGridView.IXListViewListener {
	private TextView attention_hint_tv;
	private TextView attention_num_tv;
	private XGridView attention_personal_lv;

	private String mSid;
	private String mVer;
	private String mUid;
	private String mConTaCtsUrl;
	private String mLastId = "0";

	private int mCount = 10;
	private int mCurrentFlag = PULL_FLAG_NEW;
	private static final int PULL_FLAG_NEW = 0; // 首次读取
	private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	private static final int PULL_FLAG_DOWN = 2; // 下拉刷新
	private static final int STOP_REFRESH = 4; // 停止刷新

	private Follow mFollow;
	private ArrayList<Follow> mFollowList;
	private List<Follow> mAllFollowList = new ArrayList<Follow>();
	private Map<String, Follow> mFollowMap = new LinkedHashMap<String, Follow>();
	private PersomalAttentionAdapter mPersomalAttentionAdapter;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case STOP_REFRESH:
				stopRefresh();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSid = UserUtil.getUserSid(mContext);
		mUid = getIntent().getStringExtra(GlobalParams.USER_UID);
		mVer = GlobalParams.ver;
		mConTaCtsUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW_LIST;

		if (!mUid.equals(UserUtil.getUserUid(this))) {
			attention_num_tv.setText("TA关注的人");
		}

		setDataFromContacts();

	}

	private void setDataFromContacts() {
		JSONObject jsonObject = getContactsListJSONObject();
		initData(mConTaCtsUrl, jsonObject);
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_personal_attention;
	}

	@Override
	protected void findViews() {
		attention_personal_lv = (XGridView) findViewById(R.id.attention_personal_lv);
		attention_hint_tv = (TextView) findViewById(R.id.attention_hint_tv);
		attention_num_tv = (TextView) findViewById(R.id.attention_num_tv);
	}

	@Override
	protected void setListensers() {
		attention_personal_lv.setPullLoadEnable(true);
		attention_personal_lv.setXListViewListener(this);
	}

	private JSONObject getContactsListJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			request.put("uid", mUid);
			request.put("count", mCount);
			request.put("last_id", mLastId);
			request.put("flag", mCurrentFlag);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (url.equals(mConTaCtsUrl)) {
			sucessForContacts(result);
		}
	}

	private void sucessForContacts(String result) {
		try {
			FollowList jsonBean = GsonUtils.json2bean(result, FollowList.class);
			mFollowList = (ArrayList<Follow>) jsonBean.response.list;

			if (mFollowList.size() > 0 && mFollowList != null) {
				attention_hint_tv.setVisibility(View.GONE);

				for (int i = 0; i < mFollowList.size(); i++) {
					mFollow = mFollowList.get(i);
					String uid = mFollowList.get(i).uid;
					mFollowMap.put(uid, mFollow);
				}

				mAllFollowList.clear();
				for (Entry<String, Follow> mapItem : mFollowMap.entrySet()) {
					mAllFollowList.add(mapItem.getValue());
				}
			} else {
				mHandler.sendEmptyMessageDelayed(STOP_REFRESH, 1000);
			}

			if (mPersomalAttentionAdapter == null) {
				mPersomalAttentionAdapter = new PersomalAttentionAdapter(this, mAllFollowList, mBitmapUtils);
				attention_personal_lv.setAdapter(mPersomalAttentionAdapter);
			} else {
				mPersomalAttentionAdapter.setData(mAllFollowList);
				mPersomalAttentionAdapter.notifyDataSetChanged();
			}
			mHandler.sendEmptyMessageDelayed(STOP_REFRESH, 1000);

		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessageDelayed(STOP_REFRESH, 1000);
		}
	}

	private void setParameter() {
		if (mFollowList.isEmpty()) {
			mHandler.sendEmptyMessageDelayed(STOP_REFRESH, 1000);
			return;
		}
		switch (mCurrentFlag) {
		case PULL_FLAG_NEW:
			mCount = mFollowList.size();
			break;
		case PULL_FLAG_UP:
			mCount = mFollowList.size();
			mLastId = mFollowList.get(mFollowList.size() - 1).getId();
			break;
		default:
			break;
		}
	}

	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		attention_personal_lv.stopLoadMore();
		attention_personal_lv.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		attention_personal_lv.setRefreshTime(getString(R.string.xlistview_header_last_time,
				simpleDateFormat.format(new Date(System.currentTimeMillis()))));
	}

	@Override
	public void onRefresh() {
		mCurrentFlag = PULL_FLAG_DOWN;
		setParameter();
		setDataFromContacts();
	}

	@Override
	public void onLoadMore() {
		mCurrentFlag = PULL_FLAG_UP;
		setParameter();
		setDataFromContacts();
	}
}
