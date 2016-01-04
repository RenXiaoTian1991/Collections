package com.edaoyou.collections.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.SearchFriendAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Contact;
import com.edaoyou.collections.bean.Contacts;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.etsy.XListView;
import com.etsy.XListView.IXListViewListener;

public class SearchFriendActivity extends BaseActivity implements OnClickListener {

	private TextView search_fragment_cancel_tv;
	private EditText search_fragment_et;
	private XListView search_fragment_Xlistview;
	private SearchFriendAdapter searchFriendAdapter;

	private String mWord;// 搜索的关键字
	private String mUid;
	private String mSid;
	private String mSearchUserUrl;// 搜索URL
	private String mFollowUrl;// 关注URL
	private String mUnfollowUrl;// 取消关注URL

	private static final int PULL_FLAG_NEW = 0; // 首次读取
	private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	private static final int PULL_FLAG_DOWN = 2; // 下拉刷新
	private int mCurrentFlag = PULL_FLAG_NEW;

	private static final int STATE_ADD = 0;// 无关系
	private static final int STATE_I_LIKE_HE = 1;// 我关注了他(已关注)
	private static final int STATE_HE_LIKE_ME = 2;// 2他关注了我(我的粉丝)
	private static final int STATE_MUTUAL = 3;// 互相关注

	private String mLastFid;
	private int mCount = 10;// 每次访问的条数
	private int mPosition;
	private int mIsMore = 1;
	private List<Contact> mAllContacts = new ArrayList<Contact>();
	private List<Contact> mContacts;
	private Contact mContact;

	public static final int MSG_LIKE = 1;// 跳转到主界面
	public static final int MSG_STOP_REFRESH = 2; // 停止刷新
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LIKE:
				mPosition = (Integer) msg.obj;
				checkFollowOrCancelFollow();
				break;
			case MSG_STOP_REFRESH:
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
		mSearchUserUrl = ConstantValue.COMMONURI + ConstantValue.SEARCH_USER;
		mFollowUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
		mUnfollowUrl = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
		mUid = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_UID);
		mSid = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_SID);
		showSoftInput();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_search_friend;
	}

	@Override
	protected void findViews() {
		search_fragment_cancel_tv = (TextView) findViewById(R.id.search_fragment_cancel_tv);
		search_fragment_et = (EditText) findViewById(R.id.search_fragment_et);
		search_fragment_Xlistview = (XListView) findViewById(R.id.search_fragment_Xlistview);
	}

	@Override
	protected void setListensers() {
		search_fragment_cancel_tv.setOnClickListener(this);
		search_fragment_et.setOnEditorActionListener(new SearchListener());
		search_fragment_Xlistview.setPullRefreshEnable(false);
		search_fragment_Xlistview.setPullLoadEnable(true);
		search_fragment_Xlistview.setXListViewListener(new MyIXListViewListener());
	}

	private void getSearchFriend() {
		JSONObject searchFriendJsonObject = getSearchFriendJsonObject();
		initData(mSearchUserUrl, searchFriendJsonObject);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (mSearchUserUrl.equals(url)) {
			refreshListData(result);
		} else if (mFollowUrl.equals(url)) {
			onFollowSucess(result);
		} else if (mUnfollowUrl.equals(url)) {
			onFollowSucess(result);
		}
	}

	private void onFollowSucess(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				setFollowParameter();
				getSearchFriend();
			} else {
				Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * 检查是关注还是取消关注
	 */
	private void checkFollowOrCancelFollow() {
		int relations = mAllContacts.get(mPosition).relations;
		switch (relations) {
		case STATE_ADD:
			follow();
			break;
		case STATE_I_LIKE_HE:
			canceFollow();
			break;
		case STATE_HE_LIKE_ME:
			follow();
			break;
		case STATE_MUTUAL:
			canceFollow();
			break;
		default:
			break;
		}
	}

	/**
	 * 关注
	 */
	private void follow() {
		JSONObject followJSONObject = getFollowJSONObject();
		initData(mFollowUrl, followJSONObject);
	}

	/**
	 * 取消关注
	 */
	private void canceFollow() {
		JSONObject followJSONObject = getFollowJSONObject();
		initData(mUnfollowUrl, followJSONObject);
	}

	private JSONObject getFollowJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("follow_uid", mAllContacts.get(mPosition).uid);
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private void refreshListData(String result) {
		Contacts jsonBean = GsonUtils.json2bean(result, Contacts.class);
		mIsMore = jsonBean.response.more;
		mContacts = jsonBean.response.list;

		if (mContacts == null || mContacts.size() == 0) {// 下拉刷新时候，数据有可能是0
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 500);
			return;
		}
		if (mCurrentFlag == PULL_FLAG_UP) {
			mAllContacts.addAll(mContacts);
		} else {
			mAllContacts.clear();
			mAllContacts.addAll(mContacts);
		}
		if (searchFriendAdapter == null) {
			searchFriendAdapter = new SearchFriendAdapter(this, mBitmapUtils, mHandler, mContacts);
			search_fragment_Xlistview.setAdapter(searchFriendAdapter);
		} else {
			searchFriendAdapter.setdata(mAllContacts);
			searchFriendAdapter.notifyDataSetChanged();
			mHandler.sendEmptyMessage(MSG_STOP_REFRESH);
		}
		hideSoftInput();
	}

	/**
	 * 
	 发送数据 json={"uid":"1","sid":"0123456789ABCDEF0123456789ABCDEF","ver":"1",
	 * "request":{"kw":"包子","count":3,"last_id":"0","flag":0} } 备注 kw 关键字
	 * 支持昵称、邮箱、手机号
	 */
	private JSONObject getSearchFriendJsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("kw", mWord);
			request.put("count", mCount);
			request.put("flag", mCurrentFlag);
			request.put("last_id", mLastFid);
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private void setLoadMoreParameter() {
		if (mContacts == null || mContacts.size() == 0) {
			return;
		}
		mContact = mAllContacts.get(mAllContacts.size() - 1);
		mCount = 10;
		mLastFid = mContact.uid;
	}

	private void setFollowParameter() {
		mLastFid = "";
		mIsMore = 1;
		mCurrentFlag = PULL_FLAG_NEW;
		mCount = mAllContacts.size();
	}

	/**
	 * 显示键盘
	 */
	private void showSoftInput() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) search_fragment_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(search_fragment_et, 0);
			}
		}, 600);
	}

	/**
	 * 隐藏键盘
	 */
	private void hideSoftInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void clearSearth() {
		mLastFid = "";
		mIsMore = 1;
		mCurrentFlag = PULL_FLAG_NEW;
		mAllContacts.clear();
		if (searchFriendAdapter != null) {
			searchFriendAdapter.setdata(mAllContacts);
			searchFriendAdapter.notifyDataSetChanged();
			search_fragment_Xlistview.setPullLoadEnable(true);
			mHandler.sendEmptyMessage(MSG_STOP_REFRESH);
		}
	}

	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		search_fragment_Xlistview.stopLoadMore();
		search_fragment_Xlistview.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		search_fragment_Xlistview.setRefreshTime(getString(R.string.xlistview_header_last_time,
				simpleDateFormat.format(new Date(System.currentTimeMillis()))));
	}

	private class SearchListener implements TextView.OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			String newWord = search_fragment_et.getText().toString().trim();
			if (newWord.equals("")) {
				Toast.makeText(SearchFriendActivity.this, "搜索内容为空！", Toast.LENGTH_SHORT).show();
				return false;
			}
			newWord = newWord.replaceAll(" ", "");
			newWord = Util.stringFilter(newWord);
			if (!newWord.equals(mWord)) {
				clearSearth();
			}
			mWord = newWord;
			getSearchFriend();
			return true;
		}
	}

	private class MyIXListViewListener implements IXListViewListener {

		@Override
		public void onRefresh() {
		}

		@Override
		public void onLoadMore() {
			if (mIsMore != 1) {
				Toast.makeText(SearchFriendActivity.this, "没有更多数据了, 亲!", Toast.LENGTH_SHORT).show();
				search_fragment_Xlistview.setPullLoadEnable(false);
				return;
			}
			mCurrentFlag = PULL_FLAG_UP;
			setLoadMoreParameter();
			getSearchFriend();
		}

		@Override
		public void onRightSlip() {

		}

		@Override
		public void onLeftSlip() {

		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.search_fragment_cancel_tv:
			finish();
			break;
		default:
			break;
		}
	}

}
