package com.edaoyou.collections.topic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.FeedActivity;
import com.edaoyou.collections.adapter.NewsAdapter;
import com.edaoyou.collections.bean.Feed;
import com.edaoyou.collections.bean.TopicListBen;
import com.edaoyou.collections.bean.TopicListBen.News;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.view.PLA_AdapterView;
import com.edaoyou.collections.view.PLA_AdapterView.OnItemClickListener;
import com.edaoyou.collections.view.XGridView;
import com.lemon.android.animation.LemonAnimationUtils;
import com.lemon.android.animation.LemonAnimationUtils.DoingAnimationListener;
@SuppressLint("ValidFragment")
public class ListViewFragment extends BaseViewPagerFragment implements
		OnItemClickListener,com.edaoyou.collections.view.XGridView.IXListViewListener {

	private XGridView news_xgrid;
	private AbsListViewDelegate mAbsListViewDelegate = new AbsListViewDelegate();
	  private static final int REQUEST_COUNT = 10;// 每次访问的条数
	  private static final int PULL_FLAG_NEW = 0; // 首次读取
	  private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	  private static final int PULL_FLAG_DOWN = 2; // 下拉刷新
	  private int mCurrentFlag = PULL_FLAG_NEW;
	  private String mLastFid = "";
	  private int mIsMoreData = 1; // 是否有更多数据
	  private List<News> mNews;// 列表每次加载更多获得的新的数据
	private NewsAdapter mNewsAdapter;
	private List<Feed> mAllFeeds = new ArrayList<Feed>(); // 列表全部数据
	private static final int MSG_STOP_REFRESH = 1; // 停止刷新
	public  List<String> mallImgUrl = new ArrayList<String>();//存储所有图片URL
	@SuppressLint("ValidFragment")
	public  List<String> mallImgId = new ArrayList<String>();//存储所有图片对应的ID
	private String mTopdicListUrl;
	private String mTopicId;
	@SuppressLint("ValidFragment")
	public static ListViewFragment newInstance(int index,String mTopicId) {
		ListViewFragment fragment = new ListViewFragment(mTopicId);
		Bundle args = new Bundle();
		args.putInt(BUNDLE_FRAGMENT_INDEX, index);
		fragment.setArguments(args);
		return fragment;
	}

	public ListViewFragment(String mTopicId) {
		this.mTopicId = mTopicId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getTopicData();
	}
	@Override
	public boolean isViewBeingDragged(MotionEvent event) {
		return mAbsListViewDelegate.isViewBeingDragged(event, news_xgrid);
	}

	@Override
	protected int setContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_news_gridview;
	}

	@Override
	protected void findViews(View rootView) {
		// TODO Auto-generated method stub
		news_xgrid = (XGridView) rootView.findViewById(R.id.news_xgrid);
	}

	@Override
	protected void setListensers() {
		// TODO Auto-generated method stub
		news_xgrid.setSelector(android.R.color.transparent);
		news_xgrid.setPullLoadEnable(true);
		news_xgrid.setXListViewListener(this);
		news_xgrid.setOnItemClickListener(this);
	}

	@Override
	protected String getLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initVariable() {
		// TODO Auto-generated method stub
		
		
	}


	/**
	 * 获取首页列表数据
	 */
	private void getTopicData() {
		mTopdicListUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_LIST;
		initData(mTopdicListUrl, GsonUtils.getJSONObjectForUSer(getActivity(), getTopicJsonObject()));
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
			request.put("last_fid", mLastFid);
			request.put("flag", 0);
			request.put("topic_id", mTopicId);
			request.put("type", "");
			return request;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void refreshListData(String result, String url) {
		TopicListBen topicListBen = GsonUtils.json2bean(result, TopicListBen.class);
		if (topicListBen.ret != 0) {
			return;
		}
		if (mCurrentFlag == PULL_FLAG_UP) {
			mIsMoreData = topicListBen.more;
		}
		mNews = topicListBen.response.news;
		if (mNews == null || mNews.size() == 0) {// 下拉刷新时候，数据有可能是0
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 500);
			return;
		}
		if (mCurrentFlag == PULL_FLAG_DOWN) {// 下拉刷新有新数据时候，清除以前数据
			mAllFeeds.clear();
		}
//		mAllFeeds.addAll(mFeeds);
		if (mNewsAdapter == null) {
			mNewsAdapter = new NewsAdapter(mContext, mBitmapUtils,mNews);
			news_xgrid.setAdapter(mNewsAdapter);
		
		} else {
			mNewsAdapter.notifyDataSetChanged();
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
				stopRefresh(); 
				break;
			default:
				break;
			}
		}
	};
	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		news_xgrid.stopLoadMore();
		news_xgrid.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		news_xgrid.setRefreshTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mCurrentFlag = PULL_FLAG_DOWN;
		mIsMoreData = 1;
		setParameter();
		getTopicData();
		news_xgrid.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		news_xgrid.setRefreshTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mCurrentFlag = PULL_FLAG_UP;
		if (mIsMoreData != 1) {
			news_xgrid.stopLoadMore();
			Toast.makeText(getActivity(), "没有数据了, 亲!", Toast.LENGTH_SHORT).show();
			return;
		}
		setParameter();
		getTopicData();
		news_xgrid.stopLoadMore();
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
				intent.putExtra(GlobalParams.FEED_FID, mallImgId.get(position-1));
				startActivity(intent);
			}
		});
	}



}
