package com.edaoyou.collections.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.FeedActivity;
import com.edaoyou.collections.activity.LinLangActivity;
import com.edaoyou.collections.activity.PersonalHomepageActivity;
import com.edaoyou.collections.activity.WebActivity;
import com.edaoyou.collections.adapter.HomeViewFlowAdapter;
import com.edaoyou.collections.adapter.HomeXGridViewAdapter;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.bean.ActivityWapList;
import com.edaoyou.collections.bean.Bean.IsSuccess;
import com.edaoyou.collections.bean.Feed;
import com.edaoyou.collections.bean.Guide;
import com.edaoyou.collections.bean.Guide.Response;
import com.edaoyou.collections.bean.Guide.Response.Data;
import com.edaoyou.collections.bean.TimelineList;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.bean.User.Tag;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.topic.TopicActivity;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.view.CirclePortrait;
import com.edaoyou.collections.view.FilletLayout;
import com.edaoyou.collections.view.PLA_AdapterView;
import com.edaoyou.collections.view.PLA_AdapterView.OnItemClickListener;
import com.edaoyou.collections.view.XGridView;
import com.edaoyou.collections.view.XGridView.IXListViewListener;
import com.etsy.RectFlowIndicator;
import com.etsy.ViewFlow;
import com.etsy.ViewFlow.ViewSwitchListener;
import com.igexin.sdk.PushManager;
import com.lemon.android.animation.LemonAnimationUtils;
import com.lemon.android.animation.LemonAnimationUtils.DoingAnimationListener;

/**
 * 精选集,藏友圈
 */
public class ChoicenessAndFriendsFragment extends BaseFragment implements OnItemClickListener {

	private ViewFlow mViewFlow;
	private XGridView home_Xlistview;
	private LinearLayout mViewflowindiclay;
	private HomeViewFlowAdapter mHomeViewFlowAdapter;
	private HomeXGridViewAdapter mHomeXGridViewAdapter;
	private Window mWindow;
	private CirclePortrait user_profile_icon_iv;// 用户头像
	private TextView user_profile_name_tv;// 用户名
	private TextView user_gender_tv;// 用户性别
	private FilletLayout user_cover;// 用户封面
	private Button user_home_bt;// 进入用户主页的按钮
	private AlertDialog mAlertDialog;// 显示用户信息大dialog
	private LinearLayout user_profile_tag1_ll;// 存放tag的第一个layout
	private LinearLayout user_profile_tag2_ll;// 存放tag的第二个layout
	private RelativeLayout user_profile_followed_ok_rl;// 关注的layout
	private RelativeLayout user_profile_followed_no_rl;// 取消关注的layout
	private TextView user_profile_followed_ok_tv;// 显示关注
	private TextView user_profile_followed_no_tv;// 显示取消关注

	private List<Feed> mAllFeeds = new ArrayList<Feed>(); // 列表全部数据
	private ArrayList<Data> mAllHeaderDatas = new ArrayList<Data>(); // Header全部数据
	private List<Feed> mFeeds;// 列表每次加载更多获得的新的数据
	private ArrayList<Data> mLinLangdatas;// header琳琅园
	private Data mTodayData;// 今天显示的琳琅园

	private String mHotListUrl; // 首页list的url
	private String mGuideUrl; // 引导的url
	private String mActivityUrl; // 活动的url
	private String mPublicListUrl;// 藏友圈的url
	private String mProfileUrl;// 获取用户信息的url
	private String mUpdateTokenUrl;// 获取绑定设备device_token的URl
	private String mUid;
	private String mSid;
	private String mCid;
	private String mLastFid = "";
	private String mFollowUrl;
	private String mUnFollowUrl;
	private String mUserProfileUid;

	private boolean mIsChoiceness; // 是否是精品阁

	private int mPosition;
	private int mIsMoreData = 1; // 是否有更多数据
	private static final int REQUEST_COUNT = 10;// 每次访问的条数
	private static final int PULL_FLAG_NEW = 0; // 首次读取
	private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	private static final int PULL_FLAG_DOWN = 2; // 下拉刷新
	private int mCurrentFlag = PULL_FLAG_NEW;

	private static final int MSG_STOP_REFRESH = 1; // 停止刷新
	public static final int MSG_HOME_HEADER_CLICK = 2;// Header被点击
	public static final int MSG_USER_HEAD_CLICK = 3;// 用户头像被点击

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ChoicenessAndFriendsFragment.MSG_STOP_REFRESH:
				stopRefresh();
				break;
			case ChoicenessAndFriendsFragment.MSG_HOME_HEADER_CLICK:
				int headerPosition = (Integer) msg.obj;
				if (headerPosition == 0) {
					gotoLinLangActivity();
				} else {
					gotoWebActivity(headerPosition);
				}
				break;
			case ChoicenessAndFriendsFragment.MSG_USER_HEAD_CLICK:
				int listPosition = (Integer) msg.obj;
				mUserProfileUid = mAllFeeds.get(listPosition).uid;
				getProfiletData(false);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCid = PushManager.getInstance().getClientid(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);

	}

	@Override
	protected void initVariable() {
		//此处得到区分类型的常量，初始化url地址，得到token，和用户id
		mIsChoiceness = getArguments().getBoolean(HomeFragment.mIsChoiceness);
		mHotListUrl = ConstantValue.COMMONURI + ConstantValue.HOT_LIST;
		mGuideUrl = ConstantValue.COMMONURI + ConstantValue.CLIENT_GUIDE;
		mActivityUrl = ConstantValue.COMMONURI + ConstantValue.ACTIVITY_WAP_LIST;
		mPublicListUrl = ConstantValue.COMMONURI + ConstantValue.PUBLIC_LIST;
		mUid = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_UID);
		mSid = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_SID);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initXGridView();
		initUpdateToken();
	}

	private void initUpdateToken() {
		mUpdateTokenUrl = ConstantValue.COMMONURI + ConstantValue.UPDATE_TOKEN;
		JSONObject jsonObject = getUpdateTokenJSONObeject();
		initData(mUpdateTokenUrl, jsonObject);
	}

	private JSONObject getUpdateTokenJSONObeject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("device_token", mCid);
			if (TextUtils.isEmpty(mUid)) {
				json.put("uid", 0); // 以游客身份获取数据
			} else {
				json.put("uid", mUid);
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

	@Override
	protected void activityCreated(Bundle savedInstanceState) {
		super.activityCreated(savedInstanceState);
		if (mIsChoiceness) {
			getHotListData(true);
			getGuideData(true);
		}
	}

	private void initXGridView() {
		if (mIsChoiceness) {
			initXGridViewHeader();
		}
	}

	private void initXGridViewHeader() {
		View headView = LayoutInflater.from(mContext).inflate(R.layout.home_xgridview_header, null);
		mViewFlow = (ViewFlow) headView.findViewById(R.id.vfHomeGallery);
		mViewflowindiclay = (LinearLayout) headView.findViewById(R.id.viewflowindiclay);
		home_Xlistview.addHeaderView(headView);
	}

	@Override
	protected int setContentView() {
		return R.layout.fragment_choiceness;
	}

	@Override
	protected void findViews(View rootView) {
		home_Xlistview = (XGridView) rootView.findViewById(R.id.home_Xlistview);
	}

	@Override
	protected void setListensers() {
		// home_Xlistview.setPullRefreshEnable(true);
		home_Xlistview.setPullLoadEnable(true);
		home_Xlistview.setOnItemClickListener(this);
		home_Xlistview.setSelector(android.R.color.transparent);
		home_Xlistview.setXListViewListener(new MyIXListViewListener());
	}

	@Override
	protected String getLevel() {
		return GlobalParams.LEVEL_2_HOME;
	}

	/**
	 * 得到精品集数据,供父layout调用
	 */
	public void getChoicenessData() {
		boolean loadedDataState = DataManager.getInstance().getLoadedDataState(mHotListUrl);
		if (loadedDataState) {
			return;
		}
		getHotListData(true);
		getGuideData(true);
	}

	/**
	 * 得到藏友圈数据,供父layout调用
	 */
	public void getFriendsData() {
		boolean loadedDataState = DataManager.getInstance().getLoadedDataState(mPublicListUrl);
		if (loadedDataState) {
			return;
		}
		getPublicListData();
	}

	/**
	 * 获取首页列表数据
	 */
	private void getHotListData(boolean mShowDialog) {
		JSONObject hotListjsonObject = getHotListjsonObject();
		initData(mHotListUrl, hotListjsonObject, mShowDialog);
	}

	/**
	 * 获取用户信息
	 */
	private void getProfiletData(boolean mShowDialog) {
		mProfileUrl = ConstantValue.COMMONURI + ConstantValue.PROFILE;
		JSONObject hotListjsonObject = GsonUtils.getJSONObjectForUSer(getActivity(), getProfilejsonObject());
		initData(mProfileUrl, hotListjsonObject, mShowDialog);
	}

	/**
	 * 获取Header第一页数据
	 */
	private void getGuideData(boolean mShowDialog) {
		JSONObject guidejsonObject = getGuidejsonObject();
		initData(mGuideUrl, guidejsonObject, mShowDialog);
	}

	/**
	 * 获取Header后面的数据
	 */
	private void getActivityData(boolean mShowDialog) {
		JSONObject activityjsonObject = getActivityjsonObject();
		initData(mActivityUrl, activityjsonObject, mShowDialog);
	}

	/**
	 * 获取藏友圈列表
	 */
	private void getPublicListData() {
		JSONObject hotListjsonObject = getHotListjsonObject();
		initData(mPublicListUrl, hotListjsonObject);
	}

	/**
	 * 发送数据 json={"uid":"1","sid":"0123456789ABCDEF0123456789ABCDEF","ver":"1",
	 * "request":{"count":10,"last_fid":"0","flag":0} } 备注 flag 0首次读取
	 * 1拉旧数据(上拉刷新) 2拉新数据(下拉刷新) count 每次读取条数
	 * last_fid(flag=1时last_fid为最后一条数据的FeedID, flag=2时last_fid为最新一条数据的FeedID)
	 * flag=2时，一次返回所有数据
	 */
	private JSONObject getHotListjsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("count", REQUEST_COUNT);
			request.put("flag", mCurrentFlag);
			request.put("last_fid", mLastFid);
			if (TextUtils.isEmpty(mUid)) {
				json.put("uid", 0); // 以游客身份获取数据
			} else {
				json.put("uid", mUid);
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

	/**
	 * 请求用户信息接口
	 * 
	 * @param uid
	 *            用户的uid
	 * @return json对象
	 */
	private JSONObject getProfilejsonObject() {
		JSONObject request = new JSONObject();
		try {
			request.put("uid", mUserProfileUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * 
	 获取图鉴引导页 接口 client/guide
	 * 
	 * 请求数据 json={"uid":"1","sid":"0123456789ABCDEF0123456789ABCDEF","ver":"1",
	 * "request":{"ver":1,"type":"1"} }
	 */
	private JSONObject getGuidejsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("type", "1");
			request.put("ver", GlobalParams.ver);
			if (TextUtils.isEmpty(mUid)) {
				json.put("uid", 0); // 以游客身份获取数据
			} else {
				json.put("uid", mUid);
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

	/**
	 * 
	 发送数据 json={"uid":"1","sid":"0123456789ABCDEF0123456789ABCDEF","ver":"1",
	 * "request":{} }
	 */
	private JSONObject getActivityjsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			if (TextUtils.isEmpty(mUid)) {
				json.put("uid", 0); // 以游客身份获取数据
			} else {
				json.put("uid", mUid);
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

	@Override
	protected void initDataOnSucess(String result, String url) {
		super.initDataOnSucess(result, url);
		if (!isAdded()) {
			return;
		}
		if (mHotListUrl.equals(url)) {
			refreshListData(result, url);
		} else if (mGuideUrl.equals(url)) {
			getLinLangToday(result);
			getActivityData(false);
		} else if (mActivityUrl.equals(url)) {
			refreshHeaderData(result);
		} else if (mPublicListUrl.equals(url)) {
			refreshListData(result, url);
		} else if (url.equals(mProfileUrl)) {
			showDialog(result);
		} else if (url.equals(mFollowUrl) || url.equals(mUnFollowUrl)) {
			if (isSuccess(result)) {
				getProfiletData(false);
			} else {
				setFollwedTxt(false);
			}
		} else if (url.equals(mUpdateTokenUrl)) {
			getUpdateTokenData(result);
		}
	}

	private void getUpdateTokenData(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int ret = object.getInt("ret");
			int status = object.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {// 更新成功
			} else {// 无更新
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initDataOnFailure(String url) {
		super.initDataOnFailure(url);
		mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 500);
	}

	private void getLinLangToday(String result) {
		Guide jsonBean = GsonUtils.json2bean(result, Guide.class);
		Response response = jsonBean.getResponse();
		mLinLangdatas = response.getList();
		int weekDay = Util.getWeekDay() - 1;
		mTodayData = mLinLangdatas.get(weekDay);
	}

	/**
	 * 展示用户信息
	 * 
	 * @param result
	 *            获取json数据
	 */
	private void refreshProflieData(String result) {
		final User user = GsonUtils.json2bean(result, User.class);
		mBitmapUtils.display(user_profile_icon_iv, user.response.avatar);
		if (!TextUtils.isEmpty(user.response.cover)) {
			mBitmapUtils.display(user_cover, user.response.cover);
		}
		user_profile_name_tv.setText(user.response.username);
		String genders[] = getResources().getStringArray(R.array.gender);
		user_gender_tv.setText(genders[Integer.parseInt(user.response.gender)]);
		user_home_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
				intent.putExtra(GlobalParams.USER_UID, user.response.uid);
				startActivity(intent);
				mAlertDialog.dismiss();
			}
		});
		setTagData(user.response.tag);
		showFollwed(user.response.is_followed);
	}

	private void showFollwed(String followed, boolean isAnimation) {
		if (isAnimation) {
			Animation inTranslateAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, 0,
					Animation.ABSOLUTE, -user_profile_followed_ok_rl.getHeight());
			Animation inAlphaAnim = new AlphaAnimation(1f, 0f);
			AnimationSet inAnimationSet = new AnimationSet(false);
			inAnimationSet.addAnimation(inTranslateAnim);
			inAnimationSet.addAnimation(inAlphaAnim);
			inAnimationSet.setDuration(300);
			inAnimationSet.setFillAfter(true);

			Animation outTranslateAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE,
					user_profile_followed_ok_rl.getHeight(), Animation.ABSOLUTE, 0f);
			Animation outAlphaAnim = new AlphaAnimation(0f, 1f);
			AnimationSet outAnimationSet = new AnimationSet(false);
			outAnimationSet.addAnimation(outTranslateAnim);
			outAnimationSet.addAnimation(outAlphaAnim);
			outAnimationSet.setDuration(300);
			outAnimationSet.setFillAfter(true);
			showFollwed(followed);

			if ("0".equals(followed)) {
				user_profile_followed_no_rl.startAnimation(outAnimationSet);
				user_profile_followed_ok_rl.startAnimation(inAnimationSet);
			} else if ("1".equals(followed)) {
				user_profile_followed_ok_rl.startAnimation(outAnimationSet);
				user_profile_followed_no_rl.startAnimation(inAnimationSet);
			}
		}
	}

	private void showFollwed(String followed) {
		if ("0".equals(followed)) {
			user_profile_followed_no_rl.setVisibility(View.VISIBLE);
			user_profile_followed_ok_rl.setVisibility(View.GONE);
			user_profile_followed_no_rl.setClickable(true);
			user_profile_followed_ok_rl.setClickable(false);
		} else if ("1".equals(followed)) {
			user_profile_followed_ok_rl.setVisibility(View.VISIBLE);
			user_profile_followed_no_rl.setVisibility(View.GONE);
			user_profile_followed_ok_rl.setClickable(true);
			user_profile_followed_no_rl.setClickable(false);
		} else {
			user_profile_followed_no_rl.setVisibility(View.GONE);
			user_profile_followed_ok_rl.setVisibility(View.GONE);
			user_profile_followed_no_rl.setClickable(false);
			user_profile_followed_ok_rl.setClickable(false);
		}
	}

	private void setFollwedTxt(boolean isSuccess) {
		Animation TextOutAlphaAnim = new AlphaAnimation(1f, 0f);
		TextOutAlphaAnim.setDuration(300);
		TextOutAlphaAnim.setStartOffset(600);
		TextOutAlphaAnim.setFillAfter(true);
		if (user_profile_followed_no_rl.isShown()) {
			if (isSuccess) {
				user_profile_followed_no_tv.setText(getResources().getString(R.string.user_follwed_no_success));
			} else {
				user_profile_followed_no_tv.setText(getResources().getString(R.string.user_follwed_ok_failure));
			}
			user_profile_followed_no_tv.setVisibility(View.VISIBLE);
			user_profile_followed_no_tv.startAnimation(TextOutAlphaAnim);
			user_profile_followed_ok_tv.setVisibility(View.GONE);
		} else if (user_profile_followed_ok_rl.isShown()) {
			if (isSuccess) {
				user_profile_followed_ok_tv.setText(getResources().getString(R.string.user_follwed_ok_success));
			} else {
				user_profile_followed_ok_tv.setText(getResources().getString(R.string.user_follwed_no_failure));
			}
			user_profile_followed_ok_tv.setVisibility(View.VISIBLE);
			user_profile_followed_ok_tv.startAnimation(TextOutAlphaAnim);
			user_profile_followed_no_tv.setVisibility(View.GONE);
		}
	}

	/**
	 * 展示tag
	 * 
	 * @param tags
	 *            tag集合
	 */
	private void setTagData(List<Tag> tags) {
		LinearLayout layout = null;
		for (int i = 0; i < tags.size(); i++) {
			final Tag tag = tags.get(i);
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_profile_tag_item, null);
			final TextView textView = (TextView) view.findViewById(R.id.user_profile_tag_item_tv);
			textView.setText(tags.get(i).topic);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), TopicActivity.class);
					intent.putExtra(GlobalParams.TOPIC_ID, tag.topic_id);
					startActivity(intent);
				}
			});
			if (i < 2) {
				user_profile_tag1_ll.addView(view);
			} else if ((i - 2) % 3 == 0) {
				View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_profile_tag2_item, null);
				layout = (LinearLayout) view2.findViewById(R.id.user_profile_tag2_item_ll);
				layout.addView(view);
				user_profile_tag2_ll.addView(view2);
			} else {
				if (layout != null) {
					layout.addView(view);
				}
			}
		}
	}

	private void refreshHeaderData(String result) {
		ActivityWapList jsonBean = GsonUtils.json2bean(result, ActivityWapList.class);
		ArrayList<Data> activity_category = jsonBean.getResponse().getActivity_category();
		if (mTodayData == null || activity_category == null || activity_category.size() == 0) {
			return;
		}

		mAllHeaderDatas.clear();
		mAllHeaderDatas.add(mTodayData);
		mAllHeaderDatas.addAll(activity_category);
		mViewflowindiclay.removeAllViews();
		mHomeViewFlowAdapter = new HomeViewFlowAdapter(mContext, mBitmapUtils, mHandler, mAllHeaderDatas);
		mViewFlow.setAdapter(mHomeViewFlowAdapter);
		mViewFlow.setmSideBuffer(mAllHeaderDatas.size());
		mViewFlow.setTimeSpan(5000);
		mViewFlow.setSelection(mAllHeaderDatas.size() * 1000);
		mViewFlow.stopAutoFlowTimer();
		mViewFlow.startAutoFlowTimer();
		RectFlowIndicator viewflowindic = new RectFlowIndicator(mContext);
		viewflowindic.setFillColor(mContext.getResources().getColor(R.color.white));
		viewflowindic.setProperty(mContext.getResources().getDimension(R.dimen.radius), getResources().getDimension(R.dimen.circleSeparation),
				getResources().getDimension(R.dimen.activeRadius), 0, false);
		mViewflowindiclay.addView(viewflowindic);
		mViewFlow.setFlowIndicator(viewflowindic);
		mViewFlow.setOnViewSwitchListener(new MyViewSwitchListener());
	}

	private void refreshListData(String result, String url) {
		TimelineList jsonBean = GsonUtils.json2bean(result, TimelineList.class);
		if (jsonBean.ret != 0) {
			home_Xlistview.setVisibility(ViewGroup.GONE);
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
		if (mHomeXGridViewAdapter == null) {
			mHomeXGridViewAdapter = new HomeXGridViewAdapter(mContext, mBitmapUtils, mHandler, mFeeds);
			home_Xlistview.setAdapter(mHomeXGridViewAdapter);
		} else {
			mHomeXGridViewAdapter.setData(mAllFeeds);
			mHomeXGridViewAdapter.notifyDataSetChanged();
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 0);
		}
		home_Xlistview.setVisibility(ViewGroup.VISIBLE);
		DataManager.getInstance().setLoadedDataState(url, true);
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

	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		home_Xlistview.stopLoadMore();
		home_Xlistview.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		home_Xlistview.setRefreshTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
	}

	/**
	 * 跳转到琳琅园
	 */
	private void gotoLinLangActivity() {
		Intent intent = new Intent(mContext, LinLangActivity.class);
		startActivity(intent);
	}

	/**
	 * 跳转到活动
	 */
	private void gotoWebActivity(int headerPosition) {
		String url = mAllHeaderDatas.get(headerPosition).getUrl();
		Intent intent = new Intent(mContext, WebActivity.class);
		intent.putExtra(GlobalParams.WEB_URL, url);
		startActivity(intent);
	}

	/**
	 * 展示dialog
	 */
	private void showDialog(String result) {
		if (mAlertDialog == null) {
			mAlertDialog = new AlertDialog.Builder(mContext).create();
			if (!mAlertDialog.isShowing()) {
				mAlertDialog.show();
			}
			mAlertDialog.setCanceledOnTouchOutside(true);
			mWindow = mAlertDialog.getWindow();
			mWindow.setGravity(Gravity.CENTER);
			mWindow.setWindowAnimations(R.style.HeadDialogAnimation);
			int width = (int) getResources().getDimension(R.dimen.user_profile_width);
			int height = (int) getResources().getDimension(R.dimen.user_profile_height);
			mWindow.setLayout(width, height);
			mWindow.setContentView(R.layout.dialog_user_profile);
			mAlertDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					mAlertDialog = null;
					mWindow = null;
				}
			});
			initProfileDialog(result);
		} else {
			User user = GsonUtils.json2bean(result, User.class);
			showFollwed(user.response.is_followed, true);
			setFollwedTxt(true);
		}

	}

	/**
	 * 初始化用户信息的dialog
	 */
	private void initProfileDialog(String result) {
		if (mWindow != null) {
			user_profile_icon_iv = (CirclePortrait) mWindow.findViewById(R.id.user_profile_icon_iv);
			user_profile_name_tv = (TextView) mWindow.findViewById(R.id.user_profile_name_tv);
			user_gender_tv = (TextView) mWindow.findViewById(R.id.user_gender_tv);
			user_cover = (FilletLayout) mWindow.findViewById(R.id.user_cover);
			user_home_bt = (Button) mWindow.findViewById(R.id.user_home_bt);
			user_profile_tag1_ll = (LinearLayout) mWindow.findViewById(R.id.user_profile_tag1_ll);
			user_profile_tag2_ll = (LinearLayout) mWindow.findViewById(R.id.user_profile_tag2_ll);
			user_profile_followed_ok_rl = (RelativeLayout) mWindow.findViewById(R.id.user_profile_followed_ok_rl);
			user_profile_followed_no_rl = (RelativeLayout) mWindow.findViewById(R.id.user_profile_followed_no_rl);
			user_profile_followed_ok_rl.setOnClickListener(mClickListener);
			user_profile_followed_no_rl.setOnClickListener(mClickListener);
			user_profile_followed_ok_tv = (TextView) mWindow.findViewById(R.id.user_profile_followed_ok_tv);
			user_profile_followed_no_tv = (TextView) mWindow.findViewById(R.id.user_profile_followed_no_tv);
			refreshProflieData(result);
		}
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.user_profile_followed_ok_rl:
				setDataForUnFollow();
				break;
			case R.id.user_profile_followed_no_rl:
				setDataForFollow();
				break;

			default:
				break;
			}
		}
	};

	private boolean isSuccess(String result) {
		IsSuccess isSuccess = GsonUtils.json2bean(result, IsSuccess.class);
		return isSuccess != null && "1".equals(isSuccess.response.status);
	}

	private JSONObject getJsonObjectForFollow() {
		JSONObject request = new JSONObject();
		try {
			request.put("follow_uid", mUserProfileUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	private void setDataForFollow() {
		mFollowUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
		initData(mFollowUrl, GsonUtils.getJSONObjectForUSer(getActivity(), getJsonObjectForFollow()));
	}

	private void setDataForUnFollow() {
		mUnFollowUrl = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
		initData(mUnFollowUrl, GsonUtils.getJSONObjectForUSer(getActivity(), getJsonObjectForFollow()));
	}

	private class MyViewSwitchListener implements ViewSwitchListener {

		@Override
		public void onSwitched(View view, int position) {
			try {
				mPosition = position % mAllHeaderDatas.size();
			} catch (Exception e) {
			}
		}
	}

	private class MyIXListViewListener implements IXListViewListener {

		@Override
		public void onRefresh() {
			mCurrentFlag = PULL_FLAG_DOWN;
			mIsMoreData = 1;
			setParameter();
			if (mIsChoiceness) {
				getHotListData(false);
				getGuideData(false);
			} else {
				getPublicListData();
			}
		}

		@Override
		public void onLoadMore() {
			mCurrentFlag = PULL_FLAG_UP;
			if (mIsMoreData != 1) {
				// home_Xlistview.hideFooterView();
				home_Xlistview.stopLoadMore();
				Toast.makeText(getActivity(), "没有数据了, 亲!", Toast.LENGTH_SHORT).show();
				return;
			}
			setParameter();
			if (mIsChoiceness) {
				getHotListData(false);
			} else {
				getPublicListData();
			}
		}

	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, final int position, long id) {
		LemonAnimationUtils.doingClickAnimation(view, new DoingAnimationListener() {

			@Override
			public void onDoingAnimationEnd() {
				if (mIsChoiceness) {
					Intent intent = new Intent(getActivity(), FeedActivity.class);
					intent.putExtra(GlobalParams.FEED_FID, mAllFeeds.get(position - 2).fid);
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(), FeedActivity.class);
					intent.putExtra(GlobalParams.FEED_FID, mAllFeeds.get(position - 1).fid);
					startActivity(intent);
				}

			}
		});
	}

}