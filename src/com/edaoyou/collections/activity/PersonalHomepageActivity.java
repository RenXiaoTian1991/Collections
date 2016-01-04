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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.PersonHomepageAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.IsSuccess;
import com.edaoyou.collections.bean.Feed;
import com.edaoyou.collections.bean.TimelineList;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.bean.User.Response;
import com.edaoyou.collections.fragment.AddressBookFragment;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.view.CircleImageView;
import com.edaoyou.collections.view.PLA_AdapterView;
import com.edaoyou.collections.view.PLA_AdapterView.OnItemClickListener;
import com.edaoyou.collections.view.XGridView;
import com.lemon.android.animation.LemonAnimationUtils;
import com.lemon.android.animation.LemonAnimationUtils.DoingAnimationListener;

public class PersonalHomepageActivity extends BaseActivity implements OnClickListener, com.edaoyou.collections.view.XGridView.IXListViewListener,
		OnItemClickListener {

	private String mUid;
	private String mHomePageUrl;
	private String mUserInFoUrl;
	private String mFollowUrl;
	private String mUnFollowUrl;
	private String mDiscussUrl;
	private String mBlockUrl;
	private String mLastFid = "0";
	private String mBackGround;
	private String mSlStatus;
	private String mType = "1";

	private int mIsMoreData; // 是否有更多数据
	private int mModle = 2; // 信息流模式
	private int mCurrentFlag = PULL_FLAG_NEW;
	private static final int REQUEST_COUNT = 10;// 每次访问的条数
	private static final int PULL_FLAG_NEW = 0; // 首次读取
	private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	private static final int PULL_FLAG_DOWN = 2; // 下拉刷新
	private static final int STOP_REFRESH = 4; //
	public static final int ADPATER_CLICKED = 5;

	private View headView;
	private XGridView home_Xlistview;
	private TextView self_name_tv;
	private TextView self_neir_count_tv;
	private TextView self_like_count_tv;
	private TextView self_attention_count_tv;
	private TextView self_myfans_count_tv;
	private TextView self_bio_tv;
	private TextView home_sl_tv;
	private TextView home_visibility_tv;
	private LinearLayout self_home_more;
	private ImageButton setting_bnt;
	private ImageButton add_user_ib;
	private PersonHomepageAdapter mPersonHomepageAdapter;
	private ImageView self_sex_iv;
	private RelativeLayout self_user_info_rl;
	private CircleImageView self_header_iv;
	private AlertDialog mAlertDialog;

	private Feed mFeed;
	private List<Feed> mFeedList;
	private List<Feed> mAllFeedList = new ArrayList<Feed>(); // 列表全部数据
	private Map<String, Feed> mMapFeeds = new LinkedHashMap<String, Feed>();
	private boolean mIsFirst = false;

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
	private boolean mIsShowDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUid = getIntent().getStringExtra(GlobalParams.USER_UID);

		if (!isSelft()) {
			setting_bnt.setBackgroundResource(R.drawable.chat);
			add_user_ib.setVisibility(View.VISIBLE);
		}

		if (isSelft()) {
			self_home_more.setVisibility(View.GONE);
		} else {
			self_home_more.setVisibility(View.VISIBLE);
		}
		// 默认情况只出来个人信息
		home_Xlistview.setAdapter(null);
		self_bio_tv.setText("签名：暂无简介!");

		if (!mIsFirst) {
			setUserInfoNet();
			mIsFirst = true;
		}
		setDataFromNet();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mIsFirst) {
			setUserInfoNet();
			mIsFirst = true;
		}
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_personal_homepage_lv;
	}

	@Override
	protected void findViews() {
		home_Xlistview = (XGridView) findViewById(R.id.home_Xlistview);

		headView = View.inflate(this, R.layout.self_home_title, null);

		self_neir_count_tv = (TextView) headView.findViewById(R.id.self_neir_count_tv);
		self_like_count_tv = (TextView) headView.findViewById(R.id.self_like_count_tv);
		self_myfans_count_tv = (TextView) headView.findViewById(R.id.self_myfans_count_tv);
		self_attention_count_tv = (TextView) headView.findViewById(R.id.self_attention_count_tv);
		self_name_tv = (TextView) headView.findViewById(R.id.self_name_tv);
		self_bio_tv = (TextView) headView.findViewById(R.id.self_bio_tv);
		self_sex_iv = (ImageView) headView.findViewById(R.id.self_sex_iv);
		setting_bnt = (ImageButton) headView.findViewById(R.id.setting_bnt);
		add_user_ib = (ImageButton) headView.findViewById(R.id.add_user_ib);
		self_user_info_rl = (RelativeLayout) headView.findViewById(R.id.self_user_info_rl);
		self_header_iv = (CircleImageView) headView.findViewById(R.id.self_header_iv);
		self_home_more = (LinearLayout) headView.findViewById(R.id.self_home_more);
		home_visibility_tv = (TextView) headView.findViewById(R.id.home_visibility_tv);

		home_Xlistview.addHeaderView(headView);
	}

	@Override
	protected void setListensers() {

		home_Xlistview.setPullLoadEnable(true);
		home_Xlistview.setXListViewListener(this);
		home_Xlistview.setOnItemClickListener(this);
		home_Xlistview.setSelector(android.R.color.transparent);

		setting_bnt.setOnClickListener(this);
		self_user_info_rl.setOnClickListener(this);
		self_myfans_count_tv.setOnClickListener(this);
		self_attention_count_tv.setOnClickListener(this);
		add_user_ib.setOnClickListener(this);
		self_like_count_tv.setOnClickListener(this);
		self_home_more.setOnClickListener(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		switch (requestCode) {
		case AddressBookFragment.ACTIVITY_RESULT_LOGOUT:
			boolean isLogout = intent.getBooleanExtra(AddressBookFragment.IS_LOGOUT, false);
			if (isLogout) {
				notifyPreActivity(true);
				finish();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 通知上一个Activity结果
	 */
	private void notifyPreActivity(boolean isLogout) {
		Intent intent = getIntent();
		intent.putExtra(AddressBookFragment.IS_LOGOUT, isLogout);
		setResult(0, intent);
	}

	private void setUserInfoNet() {
		mUserInFoUrl = ConstantValue.COMMONURI + ConstantValue.PROFILE;
		initData(mUserInFoUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getJSONObjectForUSer()));
	}

	private void setDataFromNet() {
		mHomePageUrl = ConstantValue.COMMONURI + ConstantValue.PRIVATE_LIST;
		initData(mHomePageUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getJSONObject()));
	}

	private void setDataForFollow() {
		mFollowUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
		initData(mFollowUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getJsonObjectForFollow()));
	}

	private void setDataForUnFollow() {
		mUnFollowUrl = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
		initData(mUnFollowUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getJsonObjectForFollow()));
	}

	private JSONObject getJsonObjectForFollow() {
		JSONObject request = new JSONObject();
		try {
			request.put("follow_uid", mUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	private JSONObject getJSONObjectForUSer() {
		JSONObject request = new JSONObject();
		try {
			request.put("uid", mUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	private JSONObject getJSONObject() {
		JSONObject request = new JSONObject();
		try {
			request.put("count", REQUEST_COUNT);
			request.put("last_fid", mLastFid);
			request.put("flag", mCurrentFlag);
			request.put("modle", mModle);
			request.put("uid", mUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (url.equals(mHomePageUrl)) {
			getDataFromNet(result);
		} else if (url.equals(mUserInFoUrl)) {
			getUserDataFromNet(result);
		} else if (url.equals(mFollowUrl) || url.equals(mUnFollowUrl)) {
			if (isSuccess(result)) {
				setUserInfoNet();
			}
		} else if (url.equals(mDiscussUrl)) {
			getDiscussDataFromNet(result);
		} else if (url.equals(mBlockUrl)) {
			getBolckDataFromNet(result);
		}
	}

	@Override
	protected void initDataOnFailure(String url) {
		super.initDataOnFailure(url);
		home_Xlistview.setAdapter(null);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.setting_bnt:
			if (GlobalParams.LOGIN_TYPE_NULL == UserUtil.checkUserIsLogin(mContext)) {
				gotoFastRegiesterActivity();
			} else {
				if (isSelft()) {
					intent = new Intent(PersonalHomepageActivity.this, SettingActivity.class);
					startActivityForResult(intent, AddressBookFragment.ACTIVITY_RESULT_LOGOUT);
				} else {
					intent = new Intent(this, ChatActivity.class);
					intent.putExtra(GlobalParams.USER_UID, mUid);
					intent.putExtra(GlobalParams.USER_NAME, self_name_tv.getText());
					startActivity(intent);
				}
			}
			break;
		case R.id.self_user_info_rl:
			if (isSelft()) {
				showBackGroundDialog();
			}
			break;
		case R.id.self_myfans_count_tv:
			intent = new Intent(PersonalHomepageActivity.this, PersonalMyfansActivity.class);
			intent.putExtra(GlobalParams.USER_UID, mUid);
			startActivity(intent);

			break;
		case R.id.self_attention_count_tv:
			intent = new Intent(PersonalHomepageActivity.this, PersonalAttentionActivity.class);
			intent.putExtra(GlobalParams.USER_UID, mUid);
			startActivity(intent);
			break;
		case R.id.add_user_ib:
			if (GlobalParams.LOGIN_TYPE_NULL == UserUtil.checkUserIsLogin(mContext)) {
				gotoFastRegiesterActivity();
			} else {
				if (isSelft()) {
					setDataForUnFollow();
				} else {
					setDataForFollow();
				}
			}
			break;
		case R.id.self_like_count_tv:
			intent = new Intent(PersonalHomepageActivity.this, LikeActivity.class);
			intent.putExtra(GlobalParams.USER_UID, mUid);
			startActivity(intent);
			break;
		case R.id.self_home_more:// ...
			setHomeDiscussStatus();

			break;
		default:
			break;
		}
	}

	private void gotoFastRegiesterActivity() {
		Intent intent = new Intent(this, FastRegiesterActivity.class);
		startActivity(intent);
	}

	private void getDataFromNet(String result) {

		TimelineList jsonBean = GsonUtils.json2bean(result, TimelineList.class);
		mFeedList = jsonBean.response.feeds;

		if (mFeedList != null && mFeedList.size() != 0) {
			for (int i = 0; i < mFeedList.size(); i++) {
				mFeed = mFeedList.get(i);
				String url = mFeedList.get(i).data.photo.get(0).url;
				mMapFeeds.put(url, mFeed);
			}

			mAllFeedList.clear();

			for (Entry<String, Feed> mapItem : mMapFeeds.entrySet()) {
				mAllFeedList.add(mapItem.getValue());
			}
		}

		if (mCurrentFlag == PULL_FLAG_UP) {
			mIsMoreData = jsonBean.response.more;
		}

		if (mAllFeedList.size() > 0 && mAllFeedList != null) {
			home_visibility_tv.setVisibility(View.GONE);
		}

		if (mPersonHomepageAdapter == null) {
			mPersonHomepageAdapter = new PersonHomepageAdapter(mContext, mBitmapUtils, mAllFeedList);
			home_Xlistview.setAdapter(mPersonHomepageAdapter);
		} else {
			mPersonHomepageAdapter.setData(mAllFeedList);
			mPersonHomepageAdapter.notifyDataSetChanged();
		}
		mHandler.sendEmptyMessageDelayed(STOP_REFRESH, 500);

	}

	private void getUserDataFromNet(String result) {
		User jsonBean = GsonUtils.json2bean(result, User.class);
		mBackGround = jsonBean.response.cover;
		setUserInFo(jsonBean.response);
	}

	private void setUserInFo(Response response) {

		self_name_tv.setText(response.username);
		self_neir_count_tv.setText("内容  " + response.feeds);
		self_like_count_tv.setText("喜欢  " + response.likes);
		self_myfans_count_tv.setText("粉丝  " + response.fans);
		self_attention_count_tv.setText("关注  " + response.follows);
		if (!TextUtils.isEmpty(response.bio)) {
			self_bio_tv.setText("签名：" + response.bio);
		}
		if ("1".equals(response.relations)) {
			add_user_ib.setBackgroundResource(R.drawable.ok_user);
		} else if ("3".equals(response.relations)) {
			add_user_ib.setBackgroundResource(R.drawable.mutual_user);
		} else {
			add_user_ib.setBackgroundResource(R.drawable.add_user);
		}
		mBitmapUtils.display(self_header_iv, response.avatar);
		mBitmapUtils.display(self_user_info_rl, response.cover);
		if ("0".equals(response.gender)) {
			self_sex_iv.setBackgroundResource(R.drawable.nv_header);
		} else {
			self_sex_iv.setBackgroundResource(R.drawable.nan);
		}
	}

	private void getDiscussDataFromNet(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				mSlStatus = "不允许TA和你私聊";
				mType = "1";
			} else {
				mSlStatus = "取消不允许TA和你私聊";
				mType = "0";
			}
			showHomeMoreDiaLog();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getBolckDataFromNet(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				ToastUtils.showToast(mContext, "设置成功");
			} else {
				ToastUtils.showToast(mContext, "设置失败");
			}
			mAlertDialog.dismiss();
		} catch (JSONException e) {
			e.printStackTrace();
			mAlertDialog.dismiss();
		}
	}

	private boolean isSuccess(String result) {
		IsSuccess isSuccess = GsonUtils.json2bean(result, IsSuccess.class);
		return "1".equals(isSuccess.response.status);
	}

	private boolean isSelft() {
		return mUid.equals(UserUtil.getUserUid(getApplicationContext()));
	}

	private void isMoreData() {
		if (mIsMoreData != 1) {
			ToastUtils.showToast(mContext, "没有数据了, 亲!");
		}
	}

	private void showBackGroundDialog() {
		// 暂时这样写，以后想更好的方法
		mIsShowDialog = false;
		new Thread(new Runnable() {

			@Override
			public void run() {
				SystemClock.sleep(700);
				mIsShowDialog = true;
			}
		}).start();

		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(true);
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.CENTER);
		window.setWindowAnimations(R.style.HeadDialogAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		int screenHeight = Util.getDisplayHeight(mContext);
		window.setLayout(screenWidth, screenHeight);
		window.setContentView(R.layout.dialog_background);

		ImageView dialog_background_iv = (ImageView) window.findViewById(R.id.dialog_background_iv);
		mBitmapUtils.display(dialog_background_iv, mBackGround);
		Button dialog_change_background_ib = (Button) window.findViewById(R.id.dialog_change_background_ib);
		dialog_change_background_ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(mContext, SetBackGroundActivity.class);
				startActivity(intent);
				mAlertDialog.dismiss();
			}
		});

		RelativeLayout dialog_all_rl = (RelativeLayout) window.findViewById(R.id.dialog_all_rl);
		dialog_all_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsShowDialog) {
					mAlertDialog.dismiss();
				}
			}
		});
	}

	private void showHomeMoreDiaLog() {
		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(true);
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		int screenHeight = Util.getDisplayHeight(mContext);
		window.setLayout(screenWidth, screenHeight / 5);
		window.setContentView(R.layout.dialog_home_more);

		home_sl_tv = (TextView) window.findViewById(R.id.home_sl_tv);
		home_sl_tv.setText(mSlStatus);
		home_sl_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBlockUrl = ConstantValue.COMMONURI + ConstantValue.BLOCK;
				JSONObject jsonObject = getSlJSONObject();
				initData(mBlockUrl, jsonObject);
			}
		});

		TextView home_no_tv = (TextView) window.findViewById(R.id.home_no_tv);
		home_no_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAlertDialog.dismiss();
			}
		});

	}

	protected JSONObject getSlJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			json.put("uid", UserUtil.getUserUid(mContext));
			json.put("sid", UserUtil.getUserSid(mContext));
			json.put("ver", GlobalParams.ver);
			request.put("uid", mUid);
			request.put("type", mType);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	protected void setHomeDiscussStatus() {
		mDiscussUrl = ConstantValue.COMMONURI + ConstantValue.DISCUSS;
		JSONObject jsonObject = getJSONObjectFroDiscuss();
		initData(mDiscussUrl, jsonObject);
	}

	private JSONObject getJSONObjectFroDiscuss() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			json.put("uid", UserUtil.getUserUid(mContext));
			json.put("sid", UserUtil.getUserSid(mContext));
			json.put("ver", GlobalParams.ver);
			request.put("uid", mUid);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private void setParameter() {
		if (mFeedList == null || mFeedList.size() == 0) {
			return;
		}
		Feed feed = null;
		switch (mCurrentFlag) {
		case PULL_FLAG_DOWN:
			feed = mFeedList.get(0);
			break;
		case PULL_FLAG_UP:
			feed = mFeedList.get(mFeedList.size() - 1);
			break;
		default:
			break;
		}
		mLastFid = feed.fid;
	}

	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		home_Xlistview.stopLoadMore();
		home_Xlistview.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		home_Xlistview.setRefreshTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
	}

	@Override
	public void onRefresh() {
		mCurrentFlag = PULL_FLAG_DOWN;
		mIsMoreData = 1;
		isMoreData();
		setParameter();
		setDataFromNet();
	}

	@Override
	public void onLoadMore() {
		mCurrentFlag = PULL_FLAG_UP;
		isMoreData();
		setParameter();
		setDataFromNet();
	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, final int position, long id) {
		LemonAnimationUtils.doingClickAnimation(view, new DoingAnimationListener() {

			@Override
			public void onDoingAnimationEnd() {
				Intent intent = new Intent(mContext, FeedActivity.class);
				intent.putExtra(GlobalParams.FEED_FID, mAllFeedList.get(position - 2).fid);
				startActivity(intent);
			}
		});
	}
}
