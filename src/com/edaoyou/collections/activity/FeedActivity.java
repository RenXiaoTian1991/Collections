package com.edaoyou.collections.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.FeedCommentAdapter;
import com.edaoyou.collections.adapter.FeedPagerAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Comment;
import com.edaoyou.collections.bean.Bean.CommentList;
import com.edaoyou.collections.bean.Bean.IsSuccess;
import com.edaoyou.collections.bean.Bean.Like;
import com.edaoyou.collections.bean.Bean.Photo;
import com.edaoyou.collections.bean.Feed;
import com.edaoyou.collections.bean.TimelineList;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.QSLog;
import com.edaoyou.collections.utils.RotateAnimation;
import com.edaoyou.collections.utils.RotateAnimation.InterpolatedTimeListener;
import com.edaoyou.collections.utils.TimeUitl;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.view.CirclePortrait;
import com.edaoyou.collections.view.ShareView;
import com.etsy.XListView;
import com.etsy.XListView.IXListViewListener;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

public class FeedActivity extends BaseActivity implements InterpolatedTimeListener, OnClickListener {
	private static final int LOAD_FEED_DATA = 0; // 更新所有数据
	private static final int LOAD_LIKE_DATA = 1; // 更新数据
	private static final int LOAD_NEW_CREATE_DATA = 2;// 更新新添加的评论
	private static final int LOAD_OLD_CREATE_DATA = 3;// 更新老的评论
	private static final int MSG_STOP_REFRESH = 1; // 停止刷新
	private View mHeaderView;// ListView的HeaderView
	private ViewPager feed_vp;// 存放图片的ViewPager
	private TextView feed_vp_item_index_tv;// 图片的索引
	private TextView feed_vp_item_sum_tv;// 图片的总数
	private TextView feed_time_tv;// Feed创建时间
	private ImageButton feed_share_ib;// 分享图标
	private ImageView feed_chat_iv;// 发起聊天的图标
	private XListView feed_comment_list_lv;// 显示评论的 Listview
	private CirclePortrait feed_user_icon_iv;// 用户的头像
	private RelativeLayout feed_header_like_rl;// 添加／取消喜欢的按钮
	private ImageView feed_header_like_iv;// 显示是否喜欢的图标
	private EditText feed_publish_et;// 添加评论的填写框
	private GridView feed_like_gv;// 显示喜欢Feed的相关人信息的GtidView
	private TextView feed_text_tv;// Feed文字内容
	private TextSwitcher feed_header_like_count_ts;// 显示喜欢的总人数
	private String mFid;// Feed的fid
	private String mFeedUrl;// 拉取Feed信息的url
	private String mLikeFeedUrl;// 添加喜欢Feed人信息的url
	private String mUnLikeFeedUrl;// 取消喜欢Feed人信息的url
	private String mCreateUrl;// 添加评论的url
	private String mCommentListUrl;// 拉取评论列表的url
	private String mDeleteCommentUrl;// 删除评论的url
	private List<Photo> mPhotos;
	private List<View> mViews;// 为ViewPager添加数据的list
	private FeedPagerAdapter mPagerAdapter;// ViewPager的Adapter
	private RotateAnimation mRotateAnimation;// ViewPager中内容切换时候，索引的动画
	private FeedCommentAdapter mCommentAdapter;// 评论列表的Adapter
	private SimpleAdapter mSimpleAdapter;// 喜欢人的信息的Adapter
	private int mCurrentIndex;// 记录当前ViewPager内容的索引
	private Feed mFeed;// Feed对象
	private int mLoadType = LOAD_FEED_DATA;// 刷新布局的类型
	private String mDeleteCommentId; // 删除评了的id号
	private int mDeleteCommentIndex;// 点击删除评了的索引，方便在listView移除
	private AlertDialog mAlertDialog;// 删除评论的dialog
	private ShareView mShareView; // 分享view
	private SsoHandler mSsoHandler;// 新浪微博api
	private AuthInfo mAuthInfo;

	private String mReprotUrl;
	private int mReprotStyle;// 举报的类型
	private int mReprotSatae;// 举报的
	private static final int DAOTU_STATE = 1;// 盗图
	private static final int BADPIC_STATE = 2;// 不良图片
	private static final int GARBAGEMSG_STATE = 3;// 垃圾信息
	private static final int BADTALK_STATE = 4;// 不良言论
	private static final int COPYRIGHT_STATE = 5;// 版权纠纷

	// 显示喜欢人总个数的动画
	private Animation mAnimationUpIn;
	private Animation mAnimationUpOut;
	private Animation mAnimationDownIn;
	private Animation mAnimationDownOut;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_STOP_REFRESH:
				stopRefresh();
				break;

			case ShareView.REPROT_MSG:
				showReprotDialog();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFid = getIntent().getStringExtra(GlobalParams.FEED_FID);
		mViews = new ArrayList<View>();
		feed_publish_et.clearFocus();
		initFeedData();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_feed;
	}

	@Override
	protected void findViews() {
		feed_vp = (ViewPager) findViewById(R.id.feed_vp);
		feed_vp_item_index_tv = (TextView) findViewById(R.id.feed_vp_item_index_tv);
		feed_vp_item_sum_tv = (TextView) findViewById(R.id.feed_vp_item_sum_tv);
		feed_share_ib = (ImageButton) findViewById(R.id.feed_share_ib);
		feed_chat_iv = (ImageView) findViewById(R.id.feed_chat_iv);
		feed_user_icon_iv = (CirclePortrait) findViewById(R.id.feed_user_icon_iv);
		feed_publish_et = (EditText) findViewById(R.id.feed_publish_et);
		feed_time_tv = (TextView) findViewById(R.id.feed_time_tv);
		feed_comment_list_lv = (XListView) findViewById(R.id.feed_comment_list_lv);
		mAnimationUpIn = AnimationUtils.loadAnimation(this, R.anim.up_slip_in);
		mAnimationUpOut = AnimationUtils.loadAnimation(this, R.anim.up_slip_out);
		mAnimationDownIn = AnimationUtils.loadAnimation(this, R.anim.down_slip_in);
		mAnimationDownOut = AnimationUtils.loadAnimation(this, R.anim.down_slip_out);
		initHeaderView();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (mSsoHandler != null) {// 新浪微博 SSO 登录授权时，需要加上
			mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
		}
	}

	/**
	 * 初始化HeaderView
	 */
	private void initHeaderView() {
		mHeaderView = LayoutInflater.from(this).inflate(R.layout.feed_xlistview_header, null);
		feed_like_gv = (GridView) mHeaderView.findViewById(R.id.feed_like_gv);
		feed_header_like_rl = (RelativeLayout) mHeaderView.findViewById(R.id.feed_header_like_rl);
		feed_header_like_iv = (ImageView) mHeaderView.findViewById(R.id.feed_header_like_iv);
		feed_text_tv = (TextView) mHeaderView.findViewById(R.id.feed_text_tv);
		feed_header_like_count_ts = (TextSwitcher) mHeaderView.findViewById(R.id.feed_header_like_count_ts);
		feed_comment_list_lv.addHeaderView(mHeaderView);
	}

	/**
	 * 获取喜欢人的数据
	 * 
	 * @return 喜欢人的数据
	 */
	private List<Map<String, String>> getLikeData() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<Like> likes = mFeed.data.likes;
		int count = likes.size() > 9 ? 9 : likes.size();
		for (int i = 0; i < count; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("avatar", likes.get(i).avatar);
			list.add(map);
		}
		return list;
	}

	@Override
	protected void setListensers() {
		feed_comment_list_lv.setPullRefreshEnable(false);
		feed_vp.setOnPageChangeListener(mPageChangeListener);
		feed_share_ib.setOnClickListener(mClickListener);
		feed_chat_iv.setOnClickListener(mClickListener);
		feed_header_like_rl.setOnClickListener(mClickListener);
		feed_user_icon_iv.setOnClickListener(mClickListener);
		feed_like_gv.setOnItemClickListener(mGridViewItemClickListener);
		feed_comment_list_lv.setXListViewListener(mListViewListener);
		feed_publish_et.setOnEditorActionListener(mEditorActionListener);
		feed_comment_list_lv.setOnItemClickListener(mItemClickListener);
		feed_header_like_count_ts.setFactory(mViewFactory);
	}

	// TextSwitch创建TextView对象的监听
	private ViewFactory mViewFactory = new ViewFactory() {

		@Override
		public View makeView() {
			TextView tv = new TextView(FeedActivity.this);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			tv.setLayoutParams(params);
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.WHITE);
			return tv;
		}
	};
	// 评论条目的点击监听
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			Comment comment = (Comment) mCommentAdapter.getItem(position - 2);
			if (position > 0) {
				if (!TextUtils.isEmpty(comment.uid) && comment.uid.equals(UserUtil.getUserUid(mContext))) {
					mDeleteCommentId = comment.id + "";
					mDeleteCommentIndex = position - 2;
					showDeleteCommenyDialog();
				}
			}
		}
	};
	// EditText接收Action的监听
	private OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
			if (GlobalParams.LOGIN_TYPE_NULL == UserUtil.checkUserIsLogin(mContext)) {
				Toast.makeText(mContext, "亲，还没登录哦.", Toast.LENGTH_SHORT).show();
			} else {
				String publish = feed_publish_et.getText().toString().trim();
				QSLog.d(publish);
				if (TextUtils.isEmpty(publish)) {

				} else {
					initCreateData();
				}
			}
			return true;
		}
	};
	// ListView加载更多的监听
	private IXListViewListener mListViewListener = new IXListViewListener() {

		@Override
		public void onRightSlip() {

		}

		@Override
		public void onRefresh() {

		}

		@Override
		public void onLoadMore() {
			mLoadType = LOAD_OLD_CREATE_DATA;
			initCommentListData(10, mFeed.data.comments.get(mCommentAdapter.getCount() - 1).id, 1);
		}

		@Override
		public void onLeftSlip() {

		}
	};
	// GridView字条目点击的监听
	private OnItemClickListener mGridViewItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Intent intent = new Intent(FeedActivity.this, PersonalHomepageActivity.class);
			intent.putExtra(GlobalParams.USER_UID, mFeed.data.likes.get(arg2).uid);
			startActivity(intent);
		}
	};

	// View点击的监听
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.feed_share_ib:
				showShareDialog();
				break;
			case R.id.feed_chat_iv:
				if (TextUtils.isEmpty(UserUtil.getUserUid(getApplicationContext()))) {
					Intent intent = new Intent(FeedActivity.this, FastRegiesterActivity.class);
					startActivity(intent);
				} else if (mFeed != null) {
					Intent intent = new Intent(FeedActivity.this, ChatActivity.class);
					intent.putExtra(GlobalParams.USER_UID, mFeed.uid);
					intent.putExtra(GlobalParams.USER_NAME, mFeed.nick);
					startActivity(intent);
				}
				break;
			case R.id.feed_header_like_rl:
				if (TextUtils.isEmpty(UserUtil.getUserUid(getApplicationContext()))) {
					Intent intent = new Intent(FeedActivity.this, FastRegiesterActivity.class);
					startActivity(intent);
				} else if (mFeed != null) {
					if (isLike()) {
						initUnLikeFeedData();
						feed_header_like_count_ts.setInAnimation(mAnimationUpIn);
						feed_header_like_count_ts.setOutAnimation(mAnimationDownOut);
					} else {
						initLikeFeedData();
						feed_header_like_count_ts.setInAnimation(mAnimationDownIn);
						feed_header_like_count_ts.setOutAnimation(mAnimationUpOut);
					}
				}
				break;
			case R.id.feed_delet_comment_bt:
				initDeleComment(mDeleteCommentId);
				break;
			case R.id.dialog_cancel:
				mAlertDialog.dismiss();
				break;
			case R.id.feed_user_icon_iv:
				Intent intent = new Intent(FeedActivity.this, PersonalHomepageActivity.class);
				intent.putExtra(GlobalParams.USER_UID, mFeed.uid);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
	// ViewPager内容改变的监听
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			float cX = feed_vp_item_index_tv.getWidth() / 2.0f;
			float cY = feed_vp_item_index_tv.getHeight() / 2.0f;
			if (arg0 > mCurrentIndex) {
				mRotateAnimation = new RotateAnimation(cX, cY, RotateAnimation.ROTATE_DECREASE);
			} else if (arg0 < mCurrentIndex) {
				mRotateAnimation = new RotateAnimation(cX, cY, RotateAnimation.ROTATE_INCREASE);
			}
			if (mRotateAnimation != null) {
				mRotateAnimation.setInterpolatedTimeListener(FeedActivity.this);
				mRotateAnimation.setFillAfter(true);
				feed_vp_item_index_tv.startAnimation(mRotateAnimation);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			mCurrentIndex = arg0;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	/**
	 * 拉取评论
	 * 
	 * @param count
	 *            拉取数据
	 * @param lastId
	 *            flag=1时last_id为最后一条数据的ID, flag=2时last_id为最新一条数据的ID
	 * @param flag
	 *            0首次读取 1拉旧数据(上拉刷新) 2拉新数据(下拉刷新)
	 */
	private void initCommentListData(int count, int lastId, int flag) {
		mCommentListUrl = ConstantValue.COMMONURI + ConstantValue.COMMENT_LIST;
		initData(mCommentListUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getCommentListJsonObject(count, lastId, flag)));
	}

	/**
	 * 拉取Feed
	 */
	private void initFeedData() {
		mFeedUrl = ConstantValue.COMMONURI + ConstantValue.GET_FEED;
		initData(mFeedUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getFeedJsonObject()));
	}

	/**
	 * 添加喜欢
	 */
	private void initLikeFeedData() {
		mLikeFeedUrl = ConstantValue.COMMONURI + ConstantValue.LIKE;
		initData(mLikeFeedUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getFeedJsonObject()),0,feed_header_like_rl);
	}

	/**
	 * 取消喜欢
	 */
	private void initUnLikeFeedData() {
		mUnLikeFeedUrl = ConstantValue.COMMONURI + ConstantValue.UNLIKE;
		initData(mUnLikeFeedUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getFeedJsonObject()),0, feed_header_like_rl);
	}

	/**
	 * 上传删除评论的信息
	 * 
	 * @param id
	 */
	private void initDeleComment(String id) {
		mDeleteCommentUrl = ConstantValue.COMMONURI + ConstantValue.DELETECOMMENT;
		initData(mDeleteCommentUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getDeleteCommentJsonObject(id)));
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(feed_publish_et.getWindowToken(), 0);
			}
		}
	}

	/**
	 * 添加评论
	 */
	private void initCreateData() {
		mCreateUrl = ConstantValue.COMMONURI + ConstantValue.COMMENT;
		initData(mCreateUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getCreateJsonObject()));
	}

	/**
	 * 获取拉取评论的JsonObject对象
	 * 
	 * @param count
	 *            拉取数据
	 * @param lastId
	 *            flag=1时last_id为最后一条数据的ID, flag=2时last_id为最新一条数据的ID
	 * @param flag
	 *            0首次读取 1拉旧数据(上拉刷新) 2拉新数据(下拉刷新)
	 * @return 拉取评论的JsonObject对象
	 */
	private JSONObject getCommentListJsonObject(int count, int lastId, int flag) {
		JSONObject request = new JSONObject();
		try {
			request.put(GlobalParams.FEED_FID, mFid);
			request.put("count", count);
			request.put("last_id", lastId);
			request.put("flag", flag);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * 获取添加评论的JsonObject对象
	 * 
	 * @return添加评论的JsonObject对象
	 */
	private JSONObject getCreateJsonObject() {
		JSONObject request = new JSONObject();
		try {
			request.put(GlobalParams.FEED_FID, mFid);
			request.put("txt", feed_publish_et.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * 获取Feed信息的JsonObject对象
	 * 
	 * @return添加评论的JsonObject对象
	 */
	private JSONObject getFeedJsonObject() {
		JSONObject request = new JSONObject();
		try {
			request.put(GlobalParams.FEED_FID, mFid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * 获取删除评论的JsonObject对像
	 * 
	 * @param id
	 *            评论对应的ID号
	 * @return 删除评论的JsonObject对像
	 */
	private JSONObject getDeleteCommentJsonObject(String id) {
		JSONObject request = new JSONObject();
		try {
			request.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		QSLog.d(result);
		if (url.equals(mFeedUrl)) {
			refreshFeedData(result);
			switch (mLoadType) {
			case LOAD_FEED_DATA:
				setFeedViewPageData();
				setFeedLikeData();
				break;
			case LOAD_LIKE_DATA:
				setFeedLikeData();
				break;
			case LOAD_NEW_CREATE_DATA:
				initCommentListData(1, mFeed.data.comments.get(0).id, 2);
				break;
			default:
				break;
			}
		} else if (url.equals(mLikeFeedUrl) || url.equals(mUnLikeFeedUrl)) {
			mLoadType = LOAD_LIKE_DATA;
			if (isSuccess(result)) {
				initFeedData();
			}
		} else if (url.equals(mCreateUrl)) {
			mLoadType = LOAD_NEW_CREATE_DATA;
			if (isSuccess(result)) {
				hideKeyboard();
				feed_publish_et.setText("");
				initFeedData();
			}
		} else if (url.equals(mCommentListUrl)) {
			setCommentData(result);
		} else if (url.equals(mDeleteCommentUrl)) {
			if (isSuccess(result)) {
				List<Comment> comments = mCommentAdapter.getComments();
				comments.remove(mDeleteCommentIndex);
				mCommentAdapter.notifyDataSetChanged();
				mAlertDialog.cancel();
			}
		} else if (url.equals(mReprotUrl)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				int ret = jsonObject.getInt("ret");
				int status = jsonObject.getJSONObject("response").getInt("status");
				if (ret == 0 && status == 1) {
					ToastUtils.showToast(mContext, "举报成功");
				} else {
					mReprotSatae = 1;
				}
				mAlertDialog.dismiss();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		feed_comment_list_lv.stopLoadMore();
	}

	/**
	 * 判断操作是否执行成功
	 * 
	 * @param result
	 *            操作以后服务器下发的json数据
	 * @return 操作是否执行成功，成功为true,否则为false
	 */
	private boolean isSuccess(String result) {
		IsSuccess isSuccess = GsonUtils.json2bean(result, IsSuccess.class);
		return "1".equals(isSuccess.response.status);
	}

	/**
	 * 初始化Feed数据
	 * 
	 * @param result
	 */
	private void refreshFeedData(String result) {
		TimelineList feedList = GsonUtils.json2bean(result, TimelineList.class);
		mFeed = feedList.response.feeds.get(0);
	}

	/**
	 * 判断当前用户是否喜欢该Feed
	 * 
	 * @return 喜欢返回true,否则false
	 */
	private boolean isLike() {
		if (mFeed == null) {
			return false;
		}
		return 1 == mFeed.data.is_like;
	}

	/**
	 * 向评论的 Adapter中添加数据
	 * 
	 * @param result
	 */
	private void setCommentData(String result) {
		CommentList commentList = GsonUtils.json2bean(result, CommentList.class);
		List<Comment> comments = commentList.response.list;
		switch (mLoadType) {
		case LOAD_NEW_CREATE_DATA:
			comments.addAll(mFeed.data.comments);
			mCommentAdapter.setComments(comments);
			break;
		case LOAD_OLD_CREATE_DATA:
			if (comments.size() == 0) {
				Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
				mHandler.sendEmptyMessageAtTime(MSG_STOP_REFRESH, 500);
				return;
			}
			mFeed.data.comments.addAll(comments);
			mCommentAdapter.setComments(mFeed.data.comments);
			break;

		default:
			break;
		}
		mHandler.sendEmptyMessage(MSG_STOP_REFRESH);
		mCommentAdapter.notifyDataSetChanged();
	}

	/**
	 * 向喜欢的GridView中添加数据
	 */
	private void setFeedLikeData() {
		if (isLike()) {
			feed_header_like_iv.setBackgroundResource(R.drawable.particulars_like);
		} else {
			feed_header_like_iv.setBackgroundResource(R.drawable.like_icon);
		}
		if (mFeed.data.likes.size() > 8) {
			feed_header_like_count_ts.setText(mFeed.data.likes.size() + "");
			feed_header_like_count_ts.setVisibility(View.VISIBLE);
		} else {
			feed_header_like_count_ts.setVisibility(View.GONE);
		}
		mSimpleAdapter = new SimpleAdapter(this, getLikeData(), R.layout.adapter_feed_like_item, new String[] { "avatar" },
				new int[] { R.id.feed_user_icon_iv });
		mSimpleAdapter.setViewBinder(mViewBinder);
		feed_like_gv.setAdapter(mSimpleAdapter);
	}

	/**
	 * 向Feed的ViewPager中添加数据
	 */
	private void setFeedViewPageData() {
		mPhotos = mFeed.data.photo;
		for (int i = 0; i < mPhotos.size(); i++) {
			ImageView imageView = new ImageView(getApplicationContext());
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			mBitmapUtils.display(imageView, mPhotos.get(i).url);
			mViews.add(imageView);
		}
		mPagerAdapter = new FeedPagerAdapter(mViews);
		feed_vp.setAdapter(mPagerAdapter);
		feed_vp_item_index_tv.setText("1");
		feed_vp_item_sum_tv.setText("/ " + mPagerAdapter.getCount());
		feed_time_tv.setText(TimeUitl.getDiffTime(getApplicationContext(), mFeed.time + ""));
		feed_text_tv.setText(mFeed.data.text);
		mBitmapUtils.display(feed_user_icon_iv, mFeed.avatar);
		mCommentAdapter = new FeedCommentAdapter(getApplicationContext(), mFeed.data.comments);
		if (mCommentAdapter.getCount() < 10) {
			feed_comment_list_lv.setPullLoadEnable(false);
		} else {
			feed_comment_list_lv.setPullLoadEnable(true);
		}
		feed_comment_list_lv.setAdapter(mCommentAdapter);
	}

	@Override
	public void interpolatedTime(float interpolatedTime) {
		if (interpolatedTime > 0.5f) {
			feed_vp_item_index_tv.setText((mCurrentIndex + 1) + "");
		}
	}

	// 对GridView对 View绑定数据的时后调用ViewBinder
	private ViewBinder mViewBinder = new ViewBinder() {

		@Override
		public boolean setViewValue(View arg0, Object arg1, String arg2) {
			if (arg0 instanceof CirclePortrait) {
				CirclePortrait circlePortrait = (CirclePortrait) arg0;
				mBitmapUtils.display(circlePortrait, arg2);
				return true;
			}
			return false;
		}
	};

	/**
	 * 显示删除评论的Dialog
	 */
	private void showDeleteCommenyDialog() {
		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(true);
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		int screenHeight = Util.getDisplayHeight(mContext);
		window.setLayout(screenWidth, screenHeight * 1 / 3);
		View deleteComment = View.inflate(mContext, R.layout.dialog_feed_comment, null);
		window.setContentView(deleteComment);

		Button dialog_cancel = (Button) window.findViewById(R.id.dialog_cancel);
		Button feed_delet_comment_bt = (Button) window.findViewById(R.id.feed_delet_comment_bt);

		dialog_cancel.setOnClickListener(mClickListener);
		feed_delet_comment_bt.setOnClickListener(mClickListener);
	}

	/**
	 * 显示分享的Dialog
	 */
	private void showShareDialog() {
		if (mShareView == null) {
			mShareView = new ShareView(this);
			mAuthInfo = new AuthInfo(this, GlobalParams.WEI_BO_APP_KEY, GlobalParams.WEI_BO_REDIRECT_URL, GlobalParams.WEI_BO_SCOPE);
			mSsoHandler = new SsoHandler(this, mAuthInfo);
		}
		mReprotSatae = mFeed.data.is_report;
		mShareView.setQQData(getShareQQData());
		mShareView.setWeiXinData(getShareWeiXinData());
		mShareView.setWeiboData(mSsoHandler, mAuthInfo, getShareWeiBoData());
		mShareView.setHandler(mHandler);
		mShareView.show(true, mReprotSatae);
	}

	/**
	 * 点击举报的Dialog
	 */
	private void showReprotDialog() {

		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(true);
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		int screenHeight = Util.getDisplayHeight(mContext);
		window.setLayout(screenWidth, screenHeight * 3 / 5);
		View deleteComment = View.inflate(mContext, R.layout.dialog_report, null);
		window.setContentView(deleteComment);

		LinearLayout report_daotu_ll = (LinearLayout) window.findViewById(R.id.report_daotu_ll);
		LinearLayout report_badpic_ll = (LinearLayout) window.findViewById(R.id.report_badpic_ll);
		LinearLayout report_garbagemsg_ll = (LinearLayout) window.findViewById(R.id.report_garbagemsg_ll);
		LinearLayout report_badtalk_ll = (LinearLayout) window.findViewById(R.id.report_badtalk_ll);
		LinearLayout report_copyright_ll = (LinearLayout) window.findViewById(R.id.report_copyright_ll);
		LinearLayout report_cancel_ll = (LinearLayout) window.findViewById(R.id.report_cancel_ll);

		report_daotu_ll.setOnClickListener(this);
		report_badpic_ll.setOnClickListener(this);
		report_garbagemsg_ll.setOnClickListener(this);
		report_badtalk_ll.setOnClickListener(this);
		report_copyright_ll.setOnClickListener(this);
		report_cancel_ll.setOnClickListener(this);
	}

	/**
	 * 获得分享qq内容
	 */
	private Bundle getShareQQData() {
		Bundle bundle = new Bundle();
		String shareUrl = getShareUrl(false);
		// 这条分享消息被好友点击后的跳转URL。
		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
		// 分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_ SUMMARY不能全为空，最少必须有一个是有值的。
		bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "我分享了\"" + mFeed.nick + "\"的一件藏品，快来围观");
		// 分享的图片URL
		String imgUrl = mPhotos.get(feed_vp.getCurrentItem()).url;
		bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
		// 分享的消息摘要，最长50个字
		bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, mFeed.data.text);
		// 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
		bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "藏家");
		// 标识该消息的来源应用，值为应用名称+AppId。
		bundle.putString(QQShare.SHARE_TO_QQ_KEY_TYPE, "藏家" + GlobalParams.QQ_APP_ID);
		return bundle;
	}

	/**
	 * 获得分享微信内容
	 */
	private WXMediaMessage getShareWeiXinData() {
		WXMediaMessage msg = new WXMediaMessage();
		String url = mPhotos.get(feed_vp.getCurrentItem()).url;
		Bitmap bitmapFromDiskCache = mBitmapUtils.getBitmapFromCache(url);
		byte[] arr = BimpUtil.bmpToByteArray(bitmapFromDiskCache);
		msg.thumbData = arr;
		msg.title = "我分享了\"" + mFeed.nick + "\"的一件藏品，快来围观";
		msg.description = mFeed.data.text;
		WXWebpageObject web = new WXWebpageObject();
		String shareUrl = getShareUrl(true);
		web.webpageUrl = shareUrl;
		msg.mediaObject = web;
		return msg;
	}

	/**
	 * 获得分享微博内容
	 */
	private WeiboMultiMessage getShareWeiBoData() {
		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		// 文字
		TextObject textObject = new TextObject();
		String shareUrl = getShareUrl(false);
		String shareText = "我分享了\"" + mFeed.nick + "\"的一件藏品，快来围观!";
		textObject.text = shareText + shareUrl;
		weiboMessage.textObject = textObject;
		// 图片
		ImageObject imageObject = new ImageObject();
		String url = mPhotos.get(feed_vp.getCurrentItem()).url;
		Bitmap bm = BimpUtil.compressImage(mBitmapUtils.getBitmapFromCache(url));
		imageObject.setImageObject(bm);
		weiboMessage.imageObject = imageObject;
		return weiboMessage;
	}

	/**
	 * http://182.92.195.162:8088/wap.php?r=wap/share/index&id=12&type=1&key
	 * =46f94c8de14fb36680850768ff1b7f2a&lang=zh-cn type 1 琳琅 2 feed照片详情 3 资讯 4
	 * 活动 5 App分享 key 反转id
	 */
	private String getShareUrl(boolean isShareToWeiXin) {
		String id = mFeed.fid;
		String reverseId = Util.reverseString(id); // 1.反转字符串
		String MD5key = Util.md5(reverseId).toLowerCase();// 2.md5加密
		String finalKey = Util.reverseString(MD5key).toLowerCase();// 3.反转md5
		String url;
		if (isShareToWeiXin) {
			url = ConstantValue.SHARE + "&id=" + id + "&type=2&key=" + finalKey + "&lang=zh-cn&channel=wx";
		} else {
			url = ConstantValue.SHARE + "&id=" + id + "&type=2&key=" + finalKey + "&lang=zh-cn";
		}
		return url.replace("&amp", "&");
	}

	/**
	 * 点击举报后的操作
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.report_daotu_ll) {
			mReprotStyle = DAOTU_STATE;
		} else if (id == R.id.report_badpic_ll) {
			mReprotStyle = BADPIC_STATE;
		} else if (id == R.id.report_garbagemsg_ll) {
			mReprotStyle = GARBAGEMSG_STATE;
		} else if (id == R.id.report_badtalk_ll) {
			mReprotStyle = BADTALK_STATE;
		} else if (id == R.id.report_copyright_ll) {
			mReprotStyle = COPYRIGHT_STATE;
		} else {
			mAlertDialog.dismiss();
			return;
		}

		mReprotUrl = ConstantValue.COMMONURI + ConstantValue.REPORT;
		JSONObject jsonObject = getReportJsonData();
		initData(mReprotUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), jsonObject));

	}

	private JSONObject getReportJsonData() {
		JSONObject request = new JSONObject();
		try {
			request.put(GlobalParams.FEED_FID, mFid);
			request.put("type", mReprotStyle);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}
}
