package com.edaoyou.collections.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.FeedActivity;
import com.edaoyou.collections.adapter.LikeListAdapter;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.bean.Feed;
import com.edaoyou.collections.bean.TimelineList;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.view.PLA_AdapterView;
import com.edaoyou.collections.view.PLA_AdapterView.OnItemClickListener;
import com.edaoyou.collections.view.XGridView;
import com.lemon.android.animation.LemonAnimationUtils;
import com.lemon.android.animation.LemonAnimationUtils.DoingAnimationListener;

@SuppressLint("ValidFragment")
public class LikeListFragment extends BaseFragment implements com.edaoyou.collections.view.XGridView.IXListViewListener,OnItemClickListener{
	 private XGridView like_list_xlv;
	  private static final int REQUEST_COUNT = 10;// 每次访问的条数
	  private static final int PULL_FLAG_NEW = 0; // 首次读取
	  private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	  private static final int PULL_FLAG_DOWN = 2; // 下拉刷新
	  private int mCurrentFlag = PULL_FLAG_NEW;
	  private String mLastFid = "";
	  private String mUid;
	  private String mSid;
	  private String mLike_List; // 首页list的url
	  private int mIsMoreData = 1; // 是否有更多数据
	  private List<Feed> mFeeds;// 列表每次加载更多获得的新的数据
	private LikeListAdapter mlikeListAdapter;
	private List<Feed> mAllFeeds = new ArrayList<Feed>(); // 列表全部数据
	private static final int MSG_STOP_REFRESH = 1; // 停止刷新
	
	@SuppressLint("ValidFragment")
	public LikeListFragment(String mUid){
		this.mUid = mUid;
	}
	@Override
	protected int setContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_like_list;
	}

	@Override
	protected void findViews(View rootView) {
		// TODO Auto-generated method stub
		like_list_xlv = (XGridView) rootView.findViewById(R.id.like_list_xgrid);
	    
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getLikeListData();
	}

	@Override
	protected void activityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.activityCreated(savedInstanceState);
	}

	@Override
	protected void setListensers() {
		// TODO Auto-generated method stub
		like_list_xlv.setSelector(android.R.color.transparent);
		like_list_xlv.setPullLoadEnable(true);
		like_list_xlv.setXListViewListener(this);
		like_list_xlv.setOnItemClickListener(this);
	}

	@Override
	protected String getLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initVariable() {
		// TODO Auto-generated method stub
		mLike_List = ConstantValue.COMMONURI + ConstantValue.LIKE_LIST;
		mSid = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_SID);
	}


	/**
	 * 获取首页列表数据
	 */
	private void getLikeListData() {
		JSONObject likelistjsonObject = getLikeListjsonObject();
		initData(mLike_List, likelistjsonObject);
	}


	private JSONObject getLikeListjsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("count", REQUEST_COUNT);
			request.put("flag", mCurrentFlag);
			request.put("last_fid", mLastFid);
			if (TextUtils.isEmpty(mUid)) {
				json.put("uid", 0); // 以游客身份获取数据
				request.put("uid", 0);
			} else {
				json.put("uid", mUid);
				request.put("uid", mUid);
			}
			if (TextUtils.isEmpty(mSid)) {
				json.put("sid", ""); // 以游客身份获取数据
			} else {
				json.put("sid", mSid);
			}
			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	private void refreshListData(String result, String url) {
		TimelineList jsonBean = GsonUtils.json2bean(result, TimelineList.class);
		if (jsonBean.ret != 0) {
			return;
		}
		if (mCurrentFlag == PULL_FLAG_UP) {
			mIsMoreData = jsonBean.response.more;
		}
		mFeeds = jsonBean.response.feeds;
		if (mFeeds == null || mFeeds.size() == 0) {// 下拉刷新时候，数据有可能是0
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 500);
			return;
		}
		if (mCurrentFlag == PULL_FLAG_DOWN) {// 下拉刷新有新数据时候，清除以前数据
			mAllFeeds.clear();
		}
		mAllFeeds.addAll(mFeeds);
		if (mlikeListAdapter == null) {
			mlikeListAdapter = new LikeListAdapter(mContext, mBitmapUtils, mHandler, mFeeds);
			like_list_xlv.setAdapter(mlikeListAdapter);
		
		} else {
			mlikeListAdapter.setData(mAllFeeds);
			mlikeListAdapter.notifyDataSetChanged();
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 0);
		}
		DataManager.getInstance().setLoadedDataState(url, true);
	}
	
	@Override
	protected void initDataOnSucess(String result, String url) {
		// TODO Auto-generated method stub
		super.initDataOnSucess(result, url);
		if (!isAdded()) {
			return;
		}
		refreshListData(result,url);
	}

	@Override
	protected void initDataOnFailure(String url) {
		// TODO Auto-generated method stub
		super.initDataOnFailure(url);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_STOP_REFRESH:
				
				break;
			default:
				break;
			}
		}
	};
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mCurrentFlag = PULL_FLAG_DOWN;
		mIsMoreData = 1;
		setParameter();
		getLikeListData();
		like_list_xlv.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		like_list_xlv.setRefreshTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mCurrentFlag = PULL_FLAG_UP;
		if (mIsMoreData != 1) {
			like_list_xlv.stopLoadMore();
			Toast.makeText(getActivity(), "没有数据了, 亲!", Toast.LENGTH_SHORT).show();
			return;
		}
		setParameter();
		getLikeListData();
		like_list_xlv.stopLoadMore();
	}
	private void setParameter() {
		if (mAllFeeds == null || mAllFeeds.size() == 0) {
			return;
		}
		Feed feed = null;
		switch (mCurrentFlag) {
		case PULL_FLAG_DOWN:
			feed = mAllFeeds.get(0);
			break;
		case PULL_FLAG_UP:
			feed = mAllFeeds.get(mAllFeeds.size() - 1);
			break;
		default:
			break;
		}
		mLastFid = feed.fid;
	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, final int position,
			long id) {
		// TODO Auto-generated method stub
		LemonAnimationUtils.doingClickAnimation(view, new DoingAnimationListener() {

			@Override
			public void onDoingAnimationEnd() {
				Intent intent = new Intent(getActivity(), FeedActivity.class);
				intent.putExtra(GlobalParams.FEED_FID, mAllFeeds.get(position-1).fid);
				startActivity(intent);
			}
		});
	}
}