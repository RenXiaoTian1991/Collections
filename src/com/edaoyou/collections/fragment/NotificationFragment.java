package com.edaoyou.collections.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.MyTopicActivity;
import com.edaoyou.collections.activity.PersonalHomepageActivity;
import com.edaoyou.collections.adapter.NotificationAdapter;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.bean.Bean.NotificationData;
import com.edaoyou.collections.bean.Bean.Subscribe;
import com.edaoyou.collections.bean.NotificationList;
import com.edaoyou.collections.bean.SubscribeBean;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.XGridView;
import com.edaoyou.collections.view.XGridView.IXListViewListener;

public class NotificationFragment extends BaseFragment {

	private XGridView notification_Xlistview;
	private NotificationAdapter mNotificationAdapter;
	private TextView notification_my_topics_txt_tv;
	private TextView notification_unread_msg_number;

	private List<NotificationData> mAllNotificationDatas = new ArrayList<NotificationData>();
	private List<NotificationData> mNotificationDatas;

	private String mNotificationListUrl;// 通知列表
	private String mMyTopicsUrl;// 订阅
	private String mUid;
	private String mSid;
	private String mLastFid = "";
	private int mGetCount = 0;
	private int mIsMore = 1; // 是否有更多数据
	private int mCount = 10;// 每次访问的条数
	private static final int PULL_FLAG_NEW = 0; // 首次读取,和下拉刷新
	private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	private int mCurrentFlag = PULL_FLAG_NEW;

	public static final int MSG_STOP_REFRESH = 1; // 停止刷新
	public static final int MSG_GOTO_PERSONALHOMEPAGE = 2; // 跳转到用户主页

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_STOP_REFRESH:
				stopRefresh();
				break;
			case MSG_GOTO_PERSONALHOMEPAGE:
				int position = (Integer) msg.obj;
				gotoPersonalHomepageActivity(position);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initHeader();
	}

	@Override
	protected int setContentView() {
		return R.layout.fragment_notification;
	}

	@Override
	protected void findViews(View rootView) {
		notification_Xlistview = (XGridView) rootView.findViewById(R.id.notification_Xlistview);
	}

	@Override
	protected void setListensers() {
		notification_Xlistview.setPullRefreshEnable(true);
		notification_Xlistview.setPullLoadEnable(true);
		notification_Xlistview.setXListViewListener(new MyIXListViewListener());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		DataManager.getInstance().setLoadedDataState(mNotificationListUrl, false);
		DataManager.getInstance().setLoadedDataState(mMyTopicsUrl, false);
	}

	@Override
	protected String getLevel() {
		return GlobalParams.LEVEL_2_CHAT_NOTIFICATION;
	}

	@Override
	protected void initVariable() {
		mNotificationListUrl = ConstantValue.COMMONURI + ConstantValue.NOTICE_LIST;
		mMyTopicsUrl = ConstantValue.COMMONURI + ConstantValue.MY_TOPICS;
		mUid = UserUtil.getUserUid(mContext);
		mSid = UserUtil.getUserSid(mContext);
	}

	private void initHeader() {
		View headView = LayoutInflater.from(mContext).inflate(R.layout.notification_list_header, null);
		ImageView notification_my_topics_avatar_iv = (ImageView) headView.findViewById(R.id.notification_my_topics_avatar_iv);
		notification_my_topics_txt_tv = (TextView) headView.findViewById(R.id.notification_my_topics_txt_tv);
		notification_unread_msg_number = (TextView) headView.findViewById(R.id.notification_unread_msg_number);
		notification_my_topics_avatar_iv.setOnClickListener(new MyHeaderOnClickListener());
		notification_Xlistview.addHeaderView(headView);
	}

	/**
	 * 供外界调用
	 */
	public void getNotificationData() {
		boolean loadedDataState = DataManager.getInstance().getLoadedDataState(mNotificationListUrl);
		if (loadedDataState) {
			return;
		}
		JSONObject notificationJsonObject = getNotificationJsonObject();
		initData(mNotificationListUrl, notificationJsonObject);

		getMyTopicsData();
	}

	/**
	 * 本类调用
	 */
	private void getNotificationDataAgain() {
		JSONObject notificationJsonObject = getNotificationJsonObject();
		initData(mNotificationListUrl, notificationJsonObject);
	}

	private void getMyTopicsData() {
		JSONObject myTopicsJsonObject = getMyTopicsJsonObject();
		initData(mMyTopicsUrl, myTopicsJsonObject);
	}

	/**
	 * json={"uid":"1","sid":"0123456789ABCDEF0123456789ABCDEF","ver":"1",
	 * "request":{"count":3,"last_id":"12","flag":1} }
	 */
	private JSONObject getNotificationJsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("last_id", mLastFid);
			request.put("count", mCount);
			request.put("get_count", mGetCount);
			request.put("flag", mCurrentFlag);

			json.put("sid", mSid);
			json.put("uid", mUid);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
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
	protected void initDataOnSucess(String result, String url) {
		super.initDataOnSucess(result, url);
		if (url.equals(mNotificationListUrl)) {
			onNotificationSucess(url, result);
		} else if (url.equals(mMyTopicsUrl)) {
			onMyTopicsSucess(result);
		}
	}

	private void onMyTopicsSucess(String result) {
		SubscribeBean jsonBean = GsonUtils.json2bean(result, SubscribeBean.class);
		List<Subscribe> topic_category = jsonBean.response.topic_category;
		int noReadSize = 0;
		for (int i = 0; i < topic_category.size(); i++) {
			int news = topic_category.get(i).news;
			noReadSize += news;
		}
		notification_my_topics_txt_tv.setText(noReadSize + "条新资讯");
		if (noReadSize == 0) {
			notification_unread_msg_number.setVisibility(View.INVISIBLE);
		} else {
			notification_unread_msg_number.setVisibility(View.VISIBLE);
		}
	}

	private void onNotificationSucess(String url, String result) {
		NotificationList mNotificationList = GsonUtils.json2bean(result, NotificationList.class);
		mNotificationDatas = mNotificationList.getResponse().getList();
		if (mCurrentFlag == PULL_FLAG_NEW) {
			mAllNotificationDatas.clear();
		}
		if (mCurrentFlag == PULL_FLAG_UP) {
			mIsMore = mNotificationList.getResponse().getMore();
		}
		mAllNotificationDatas.addAll(mNotificationDatas);
		if (mNotificationAdapter == null) {
			mNotificationAdapter = new NotificationAdapter(mContext, mAllNotificationDatas, mBitmapUtils,mHandler);
			notification_Xlistview.setAdapter(mNotificationAdapter);
		} else {
			mNotificationAdapter.setData(mAllNotificationDatas);
			mNotificationAdapter.notifyDataSetChanged();
		}
		mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 0);
		DataManager.getInstance().setLoadedDataState(url, true);
	}

	private void setParameter() {
		if (mAllNotificationDatas.isEmpty()) {
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 1000);
			return;
		}
		switch (mCurrentFlag) {
		case PULL_FLAG_NEW:
			mIsMore = 1;
			mGetCount = mAllNotificationDatas.size();
			break;
		case PULL_FLAG_UP:
			mIsMore = 1;
			mGetCount = mAllNotificationDatas.size();
			mLastFid = mAllNotificationDatas.get(mAllNotificationDatas.size() - 1).getId();
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到个人主页
	 */
	private void gotoPersonalHomepageActivity(int position) {
		NotificationData notificationData = mAllNotificationDatas.get(position);
		String uid = notificationData.getUid();
		Intent intent = new Intent(mContext, PersonalHomepageActivity.class);
		intent.putExtra(GlobalParams.USER_UID, uid);
		startActivity(intent);
	}

	private void gotoMyTopicActivity() {
		Intent intent = new Intent(mContext, MyTopicActivity.class);
		startActivity(intent);
	}

	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		if (!isAdded()) {
			return;
		}
		if (mIsMore != 1) {
			Toast.makeText(mContext, "没有更多数据了，亲", Toast.LENGTH_SHORT).show();
		}
		notification_Xlistview.stopLoadMore();
		notification_Xlistview.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		notification_Xlistview.setRefreshTime(getString(R.string.xlistview_header_last_time,
				simpleDateFormat.format(new Date(System.currentTimeMillis()))));
	}

	private class MyIXListViewListener implements IXListViewListener {
		@Override
		public void onRefresh() {
			mCurrentFlag = PULL_FLAG_NEW;
			if (mIsMore != 1) {
				mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 500);
				return;
			}
			setParameter();
			getNotificationDataAgain();
		}

		@Override
		public void onLoadMore() {
			mCurrentFlag = PULL_FLAG_UP;
			if (mIsMore != 1) {
				mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 500);
				return;
			}
			setParameter();
			getNotificationDataAgain();
		}
	}

	private class MyHeaderOnClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			gotoMyTopicActivity();
		}
	}
}
