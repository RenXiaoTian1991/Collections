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
import com.edaoyou.collections.adapter.PersonalMyfansAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Follow;
import com.edaoyou.collections.bean.FollowList;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.XGridView;
import com.lidroid.xutils.BitmapUtils;

public class PersonalMyfansActivity extends BaseActivity implements com.edaoyou.collections.view.XGridView.IXListViewListener {
	private TextView myfans_hint_tv;
	private TextView myfans_num_tv;
	private XGridView myfans_personal_lv;

	private Follow mFollow;
	private XUtilsManager mXUtilsManager;
	private BitmapUtils mBitmapUtils;
	private PersonalMyfansAdapter mMyfansAdapter;

	private String mUid;
	private String mSid;
	private String mVer;
	private String mLastId = "0";
	private String mMyFansUrl;

	private int mCount = 10;
	private int mCurrentFlag = PULL_FLAG_NEW;
	private static final int PULL_FLAG_NEW = 0; // 首次读取
	private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	private static final int PULL_FLAG_DOWN = 2; // 下拉刷新
	private static final int STOP_REFRESH = 4; // 停止刷新

	private List<Follow> mFollowList;
	private List<Follow> mAllFollowList = new ArrayList<Follow>();
	private Map<String, Follow> mFollowMap = new LinkedHashMap<String, Follow>();

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
		initData();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_personal_myfans;
	}

	@Override
	protected void findViews() {
		myfans_personal_lv = (XGridView) findViewById(R.id.myfans_personal_lv);
		myfans_hint_tv = (TextView) findViewById(R.id.myfans_hint_tv);
		myfans_num_tv = (TextView) findViewById(R.id.myfans_num_tv);
	}

	@Override
	protected void setListensers() {

		myfans_personal_lv.setPullLoadEnable(true);
		myfans_personal_lv.setXListViewListener(this);
	}

	private void initData() {

		mSid = UserUtil.getUserSid(this);
		mVer = GlobalParams.ver;
		mUid = getIntent().getStringExtra(GlobalParams.USER_UID);
		mMyFansUrl = ConstantValue.COMMONURI + ConstantValue.FANS_LIST;

		mXUtilsManager = XUtilsManager.getInstance(this);
		mBitmapUtils = mXUtilsManager.getBitmapUtils();

		if (!mUid.equals(UserUtil.getUserUid(this))) {
			myfans_num_tv.setText("TA的粉丝");
		}

		setDataFromNet();
	}

	private void setDataFromNet() {
		JSONObject jsonObject = getJSONObjectData();
		initData(mMyFansUrl, jsonObject);
	}

	private JSONObject getJSONObjectData() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();

		try {
			request.put("uid", mUid);
			request.put("count", mCount);
			request.put("last_id", mLastId);
			request.put("flag", mCurrentFlag);
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

		FollowList jsonBean = GsonUtils.json2bean(result, FollowList.class);
		mFollowList = jsonBean.response.list;
		if (mFollowList.size() > 0 && mFollowList != null) {
			myfans_hint_tv.setVisibility(View.GONE);

			for (int i = 0; i < mFollowList.size(); i++) {
				mFollow = mFollowList.get(i);
				String uid = mFollowList.get(i).uid;
				mFollowMap.put(uid, mFollow);
			}

			mAllFollowList.clear();
			for (Entry<String, Follow> mapItem : mFollowMap.entrySet()) {
				mAllFollowList.add(mapItem.getValue());
			}
		}

		if (mMyfansAdapter == null) {
			mMyfansAdapter = new PersonalMyfansAdapter(PersonalMyfansActivity.this, mAllFollowList, mBitmapUtils);
			myfans_personal_lv.setAdapter(mMyfansAdapter);
		} else {
			mMyfansAdapter.setData(mAllFollowList);
			mMyfansAdapter.notifyDataSetChanged();
		}
		mHandler.sendEmptyMessageDelayed(STOP_REFRESH, 1000);
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
		myfans_personal_lv.stopLoadMore();
		myfans_personal_lv.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		myfans_personal_lv.setRefreshTime(getString(R.string.xlistview_header_last_time,
				simpleDateFormat.format(new Date(System.currentTimeMillis()))));
	}

	@Override
	public void onRefresh() {
		mCurrentFlag = PULL_FLAG_DOWN;
		setParameter();
		setDataFromNet();

	}

	@Override
	public void onLoadMore() {
		mCurrentFlag = PULL_FLAG_UP;
		setParameter();
		setDataFromNet();
	}
}
