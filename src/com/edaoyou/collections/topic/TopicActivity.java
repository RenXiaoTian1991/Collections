package com.edaoyou.collections.topic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.PersonalHomepageActivity;
import com.edaoyou.collections.adapter.NewsPaperAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.IsSuccess;
import com.edaoyou.collections.bean.TopicListBen;
import com.edaoyou.collections.bean.TopicListBen.Topic;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.QSLog;
import com.edaoyou.collections.utils.UserUtil;

public class TopicActivity extends BaseActivity implements TouchCallbackLayout.TouchEventListener, ScrollableFragmentListener,
		ViewPagerHeaderHelper.OnViewPagerTouchListener {

	private static final long DEFAULT_DURATION = 300L;
	private static final float DEFAULT_DAMPING = 1.5f;
	private static final int FIRST_LOAD = -1;
	private static final int FOLLOWED = 1;
	private static final int NO_FOLLOWED = 0;
	private RelativeLayout topic_header_center_rl;
	private Button tag_exit_bt;// 退出按钮
	private TextView tag_topic_tv;// 显示标签内容的TextView
	private RelativeLayout tag_lable_home_add_rl;// 添加关注这个标签的layout
	private ImageView tag_lable_home_add_iv;// 添加关注标签的ImageView
	private TextView tag_lable_home_add_tv;// 显示添加是否成功的TextView
	private RelativeLayout tag_lable_home_ok_rl;// 取消关注这个标签的layout
	private ImageView tag_lable_home_ok_iv;// 显示关注标签的ImageView
	private TextView tag_lable_home_ok_tv;// 显示取消关注是否成功的TextView
	private ImageView tag_avatar_iv;// 存放标签类型图片的imageView
	private TextView tag_topic_txt_tv;// 显示标签内容的TextView
	private LinearLayout tag_user_ll;// 显示关注这个标签的人信息
	private TextSwitcher tag_header_like_count_ts;// 显示关注这个标签的人数
	private ListView tag_new_lv;
	private LinearLayout tag_lable_ll;

	private SparseArrayCompat<ScrollableListener> mScrollableListenerArrays = new SparseArrayCompat<ScrollableListener>();
	private ViewPager mViewPager;
	private View mHeaderLayoutView;
	private ViewPagerHeaderHelper mViewPagerHeaderHelper;
	private PagerSlidingTabStrip mTabStrips;
	private PullToRefreshView mPullToRefreshView;
	private String mTopicId;
	private String mTopdicListUrl;
	private String mFollowUrl;// 关注的url
	private String mUnFollowUrl;// 取消关注的url
	private int mLoadType = FIRST_LOAD;
	private int mSelftIndex = -1;// 当前用户在关注标签人中的位置
	private Animation mAnimationUpIn;
	private Animation mAnimationUpOut;
	private Animation mAnimationDownIn;
	private Animation mAnimationDownOut;
	private int mTouchSlop;
	private int mTabHeight;
	private int mHeaderHeight;
	private List<BaseViewPagerFragment> mFragments = new ArrayList<BaseViewPagerFragment>();//fragment集合
	private Interpolator mInterpolator = new DecelerateInterpolator();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTopicId = getIntent().getStringExtra(GlobalParams.TOPIC_ID);
		// 出发移动事件的最小移动距离
		mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
		mTabHeight = getResources().getDimensionPixelSize(R.dimen.tabs_height);
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.viewpager_header_height);

		mViewPagerHeaderHelper = new ViewPagerHeaderHelper(this, this);

		TouchCallbackLayout touchCallbackLayout = (TouchCallbackLayout) findViewById(R.id.layout);
		touchCallbackLayout.setTouchEventListener(this);

		mHeaderLayoutView = findViewById(R.id.tag_header_rl);

		// slidingTabLayout.setDistributeEvenly(true);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTabStrips = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pullView);
		mPullToRefreshView.setIsPullDown(true);
		getTopicData();
		mFragments.add(new ListViewFragment(mTopicId));
		mFragments.add(new ListViewFragment(mTopicId));
		mFragments.add(new ListViewFragment(mTopicId));
		mFragments.add(new ListViewFragment(mTopicId));
		NewsPaperAdapter mNewsPaperAdapter = new NewsPaperAdapter(getSupportFragmentManager(), mTopicId);
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mNewsPaperAdapter);
		// mViewPager.setOffscreenPageLimit(mFragments.size());
		mViewPager.setCurrentItem(0);
		mTabStrips.setIndicatorColorResource(R.color.tab_indicator);
		mTabStrips.setViewPager(mViewPager);

		ViewCompat.setTranslationY(mViewPager, mHeaderHeight);
	}

	@Override
	public boolean onLayoutInterceptTouchEvent(MotionEvent event) {

		return mViewPagerHeaderHelper.onLayoutInterceptTouchEvent(event, mTabHeight + mHeaderHeight);
	}

	@Override
	public boolean onLayoutTouchEvent(MotionEvent event) {
		return mViewPagerHeaderHelper.onLayoutTouchEvent(event);
	}

	@Override
	public boolean isViewBeingDragged(MotionEvent event) {
		return mScrollableListenerArrays.valueAt(mViewPager.getCurrentItem()).isViewBeingDragged(event);
	}

	@Override
	public void onMoveStarted(float y) {

	}

	@Override
	public void onMove(float y, float yDx) {
		float headerTranslationY = ViewCompat.getTranslationY(mHeaderLayoutView) + yDx;
		if (headerTranslationY >= 0) { // pull end
			headerExpand(0L);
		} else if (headerTranslationY <= -mHeaderHeight) { // push end
			headerFold(0L);
		} else {
			ViewCompat.animate(mHeaderLayoutView).translationY(headerTranslationY).setDuration(0).start();
			ViewCompat.animate(mViewPager).translationY(headerTranslationY + mHeaderHeight).setDuration(0).start();
		}
		if (ViewCompat.getTranslationY(mHeaderLayoutView) >= 0) {
			mPullToRefreshView.setIsPullDown(true);
		} else {
			mPullToRefreshView.setIsPullDown(false);
		}
	}

	@Override
	public void onMoveEnded(boolean isFling, float flingVelocityY) {

		float headerY = ViewCompat.getTranslationY(mHeaderLayoutView); // 0到负数
		Log.d("xxxx", "headerY = " + headerY);
		if (headerY == 0 || headerY == -mHeaderHeight) {
			return;
		}

		if (mViewPagerHeaderHelper.getInitialMotionY() - mViewPagerHeaderHelper.getLastMotionY() < -mTouchSlop) { // pull
																													// >
																													// mTouchSlop
																													// =
																													// expand
			headerExpand(headerMoveDuration(true, headerY, isFling, flingVelocityY));
			mPullToRefreshView.setIsPullDown(true);
		} else if (mViewPagerHeaderHelper.getInitialMotionY() - mViewPagerHeaderHelper.getLastMotionY() > mTouchSlop) { // push
																														// >
																														// mTouchSlop
																														// =
																														// fold
			headerFold(headerMoveDuration(false, headerY, isFling, flingVelocityY));
			mPullToRefreshView.setIsPullDown(false);
		} else {
			if (headerY > -mHeaderHeight / 2f) { // headerY > header/2 = expand
				headerExpand(headerMoveDuration(true, headerY, isFling, flingVelocityY));
			} else { // headerY < header/2= fold
				headerFold(headerMoveDuration(false, headerY, isFling, flingVelocityY));
			}
		}
	}

	private long headerMoveDuration(boolean isExpand, float currentHeaderY, boolean isFling, float velocityY) {

		long defaultDuration = DEFAULT_DURATION;

		if (isFling) {

			float distance = isExpand ? Math.abs(mHeaderHeight) - Math.abs(currentHeaderY) : Math.abs(currentHeaderY);
			velocityY = Math.abs(velocityY) / 1000;

			defaultDuration = (long) (distance / velocityY * DEFAULT_DAMPING);

			defaultDuration = defaultDuration > DEFAULT_DURATION ? DEFAULT_DURATION : defaultDuration;
		}

		return defaultDuration;
	}

	private void headerFold(long duration) {
		ViewCompat.animate(mHeaderLayoutView).translationY(-mHeaderHeight).setDuration(duration).setInterpolator(mInterpolator).start();

		ViewCompat.animate(mViewPager).translationY(0).setDuration(duration).setInterpolator(mInterpolator).start();

		mViewPagerHeaderHelper.setHeaderExpand(false);
	}

	private void headerExpand(long duration) {
		ViewCompat.animate(mHeaderLayoutView).translationY(0).setDuration(duration).setInterpolator(mInterpolator).start();

		ViewCompat.animate(mViewPager).translationY(mHeaderHeight).setDuration(duration).setInterpolator(mInterpolator).start();
		mViewPagerHeaderHelper.setHeaderExpand(true);
	}

	@Override
	public void onFragmentAttached(ScrollableListener listener, int position) {
		mScrollableListenerArrays.put(position, listener);
	}

	@Override
	public void onFragmentDetached(ScrollableListener listener, int position) {
		mScrollableListenerArrays.remove(position);
	}


	@Override
	protected int setContentView() {
		return R.layout.activity_topic;
	}

	@Override
	protected void findViews() {
		topic_header_center_rl = (RelativeLayout) findViewById(R.id.topic_header_center_rl);
		tag_exit_bt = (Button) findViewById(R.id.tag_exit_bt);
		tag_topic_tv = (TextView) findViewById(R.id.tag_topic_tv);
		tag_lable_home_add_rl = (RelativeLayout) findViewById(R.id.tag_lable_home_add_rl);
		tag_lable_home_add_iv = (ImageView) findViewById(R.id.tag_lable_home_add_iv);
		tag_lable_home_add_tv = (TextView) findViewById(R.id.tag_lable_home_add_tv);
		tag_lable_home_ok_rl = (RelativeLayout) findViewById(R.id.tag_lable_home_ok_rl);
		tag_lable_home_ok_iv = (ImageView) findViewById(R.id.tag_lable_home_ok_iv);
		tag_lable_home_ok_tv = (TextView) findViewById(R.id.tag_lable_home_ok_tv);
		tag_avatar_iv = (ImageView) findViewById(R.id.tag_avatar_iv);
		tag_topic_txt_tv = (TextView) findViewById(R.id.tag_topic_txt_tv);
		tag_user_ll = (LinearLayout) findViewById(R.id.tag_user_ll);
		tag_header_like_count_ts = (TextSwitcher) findViewById(R.id.tag_header_like_count_ts);
		tag_lable_home_add_rl = (RelativeLayout) findViewById(R.id.tag_lable_home_add_rl);
		tag_lable_home_ok_rl = (RelativeLayout) findViewById(R.id.tag_lable_home_ok_rl);
		mAnimationUpIn = AnimationUtils.loadAnimation(this, R.anim.up_slip_in);
		mAnimationUpOut = AnimationUtils.loadAnimation(this, R.anim.up_slip_out);
		mAnimationDownIn = AnimationUtils.loadAnimation(this, R.anim.down_slip_in);
		mAnimationDownOut = AnimationUtils.loadAnimation(this, R.anim.down_slip_out);
	}

	@Override
	protected void setListensers() {
		tag_exit_bt.setOnClickListener(mClickListener);
		tag_header_like_count_ts.setFactory(mViewFactory);
		tag_lable_home_add_rl.setOnClickListener(mClickListener);
		tag_lable_home_ok_rl.setOnClickListener(mClickListener);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		QSLog.d(result);
		QSLog.d(url);
		if (url.equals(mTopdicListUrl)) {
			setTopicData(result);
		} else if (url.equals(mFollowUrl) || url.equals(mUnFollowUrl)) {
			if (isSuccess(result)) {
				getTopicData();
			} else {
				setFollwedTxt(false);
			}
		}
		super.initDataOnSucess(result, url, type);
	}

	/**
	 * 显示标签内容
	 * 
	 * @param result
	 */
	private void setTopicData(String result) {
		TopicListBen topicListBen = GsonUtils.json2bean(result, TopicListBen.class);
		Topic topic = topicListBen.response.topic;
		mBitmapUtils.display(topic_header_center_rl, topic.cover);
		tag_topic_tv.setText(topic.topic);
		mBitmapUtils.display(tag_avatar_iv, topic.avatar);
		tag_topic_txt_tv.setText(topic.txt);
		setUserData(topicListBen.response.user);
		showFollwed(topic.is_followed, true);
		if (mLoadType != FIRST_LOAD) {
			setFollwedTxt(true);
		}
	}

	// TextSwitch创建TextView对象的监听
	private ViewFactory mViewFactory = new ViewFactory() {

		@Override
		public View makeView() {
			TextView tv = new TextView(TopicActivity.this);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			tv.setLayoutParams(params);
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.WHITE);
			return tv;
		}
	};

	private void setUserLayoutTransition() {
		if (tag_user_ll.getLayoutAnimation() == null) {
			LayoutTransition transitioner = new LayoutTransition();
			transitioner.setDuration(300);
			transitioner.setDuration(LayoutTransition.DISAPPEARING, 0);
			transitioner.setStartDelay(LayoutTransition.DISAPPEARING, 0);
			transitioner.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
			transitioner.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
			tag_user_ll.setLayoutTransition(transitioner);
		}
	}

	/**
	 * 显示关注这个标签的人信息
	 * 
	 * @param users
	 */
	private void setUserData(List<User> users) {
		if (users.size() > 9) {
			tag_header_like_count_ts.setText(Integer.valueOf(users.size()).toString());
			tag_header_like_count_ts.setVisibility(View.VISIBLE);
		} else {
			tag_header_like_count_ts.setVisibility(View.GONE);
		}
		int count = users.size() > 9 ? 9 : users.size();
		for (int i = 0; i < count; i++) {
			final User user = users.get(i);
			View userView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_topic_user_item, null);
			userView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TopicActivity.this, PersonalHomepageActivity.class);
					intent.putExtra(GlobalParams.USER_UID, user.uid);
					startActivity(intent);
				}
			});
			switch (mLoadType) {
			case FIRST_LOAD:
				mBitmapUtils.display(userView.findViewById(R.id.feed_user_icon_iv), user.avatar);
				tag_user_ll.addView(userView);
				break;
			case FOLLOWED:
				setUserLayoutTransition();
				mBitmapUtils.display(userView.findViewById(R.id.feed_user_icon_iv), mUser.response.avatar);
				tag_user_ll.addView(userView, 0);
				mSelftIndex = 0;
				if (tag_user_ll.getChildCount() > 9) {
					tag_user_ll.removeViewAt(tag_user_ll.getChildCount() - 1);
				}
				return;
			case NO_FOLLOWED:
				setUserLayoutTransition();
				mBitmapUtils.display(userView.findViewById(R.id.feed_user_icon_iv), users.get(8).avatar);
				tag_user_ll.removeViewAt(mSelftIndex);
				tag_user_ll.addView(userView);
				return;

			default:
				break;
			}
			if (GlobalParams.LOGIN_TYPE_NULL != UserUtil.checkUserIsLogin(getApplicationContext()) && users.get(i).uid == mUser.response.uid) {
				mSelftIndex = i;
			}
		}
	}

	/**
	 * 判断执行结果
	 * 
	 * @param result
	 * @return
	 */
	private boolean isSuccess(String result) {
		IsSuccess isSuccess = GsonUtils.json2bean(result, IsSuccess.class);
		return isSuccess != null && "1".equals(isSuccess.response.status);
	}

	/**
	 * 得到标签信息
	 */
	private void getTopicData() {
		mTopdicListUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_LIST;
		initData(mTopdicListUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getTopicJsonObject()));
	}

	/**
	 * 关注标签
	 */
	private void setDataForFollow() {
		mLoadType = FOLLOWED;
		mFollowUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_FOLLOW;
		initData(mFollowUrl, GsonUtils.getJSONObjectForUSer(this, getJsonObjectForFollow()));
	}

	/**
	 * 取消关注
	 */
	private void setDataForUnFollow() {
		mLoadType = NO_FOLLOWED;
		mUnFollowUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_UNFOLLOW;
		initData(mUnFollowUrl, GsonUtils.getJSONObjectForUSer(this, getJsonObjectForFollow()));
	}

	/**
	 * 得到关注和取消关注的JSONObject对象
	 * 
	 * @return
	 */
	private JSONObject getJsonObjectForFollow() {
		JSONObject request = new JSONObject();
		try {
			request.put("topic_id", mTopicId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
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
			request.put("last_fid", "0");
			request.put("flag", 0);
			request.put("topic_id", mTopicId);
			return request;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void showFollwed(String followed) {
		if ("0".equals(followed)) {
			tag_lable_home_add_rl.setVisibility(View.VISIBLE);
			tag_lable_home_ok_rl.setVisibility(View.GONE);
			tag_lable_home_add_rl.setClickable(true);
			tag_lable_home_ok_rl.setClickable(false);
		} else if ("1".equals(followed)) {
			tag_lable_home_ok_rl.setVisibility(View.VISIBLE);
			tag_lable_home_add_rl.setVisibility(View.GONE);
			tag_lable_home_ok_rl.setClickable(true);
			tag_lable_home_add_rl.setClickable(false);
		} else {
			tag_lable_home_add_rl.setVisibility(View.GONE);
			tag_lable_home_ok_rl.setVisibility(View.GONE);
			tag_lable_home_add_rl.setClickable(false);
			tag_lable_home_ok_rl.setClickable(false);
		}
	}

	private void setFollwedTxt(boolean isSuccess) {
		Animation TextOutAlphaAnim = new AlphaAnimation(1f, 0f);
		TextOutAlphaAnim.setDuration(300);
		TextOutAlphaAnim.setStartOffset(600);
		TextOutAlphaAnim.setFillAfter(true);
		if (tag_lable_home_add_rl.isShown()) {
			if (isSuccess) {
				tag_lable_home_add_tv.setText(getResources().getString(R.string.user_follwed_no_success));
			} else {
				tag_lable_home_add_tv.setText(getResources().getString(R.string.user_follwed_ok_failure));
			}
			tag_lable_home_add_tv.setVisibility(View.VISIBLE);
			tag_lable_home_add_tv.startAnimation(TextOutAlphaAnim);
			tag_lable_home_ok_tv.setVisibility(View.GONE);
		} else if (tag_lable_home_ok_rl.isShown()) {
			if (isSuccess) {
				tag_lable_home_ok_tv.setText(getResources().getString(R.string.user_follwed_ok_success));
			} else {
				tag_lable_home_ok_tv.setText(getResources().getString(R.string.user_follwed_no_failure));
			}
			tag_lable_home_ok_tv.setVisibility(View.VISIBLE);
			tag_lable_home_ok_tv.startAnimation(TextOutAlphaAnim);
			tag_lable_home_add_tv.setVisibility(View.GONE);
		}
	}

	private void showFollwed(String followed, boolean isAnimation) {
		if (isAnimation) {
			Animation inTranslateAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, 0,
					Animation.ABSOLUTE, -tag_lable_home_add_rl.getHeight());
			Animation inAlphaAnim = new AlphaAnimation(1f, 0f);
			AnimationSet inAnimationSet = new AnimationSet(false);
			inAnimationSet.addAnimation(inTranslateAnim);
			inAnimationSet.addAnimation(inAlphaAnim);
			inAnimationSet.setDuration(300);
			inAnimationSet.setFillAfter(true);

			Animation outTranslateAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE,
					tag_lable_home_add_rl.getHeight(), Animation.ABSOLUTE, 0f);
			Animation outAlphaAnim = new AlphaAnimation(0f, 1f);
			AnimationSet outAnimationSet = new AnimationSet(false);
			outAnimationSet.addAnimation(outTranslateAnim);
			outAnimationSet.addAnimation(outAlphaAnim);
			outAnimationSet.setDuration(300);
			outAnimationSet.setFillAfter(true);
			showFollwed(followed);

			if ("0".equals(followed)) {
				tag_lable_home_add_rl.startAnimation(outAnimationSet);
				tag_lable_home_ok_rl.startAnimation(inAnimationSet);
			} else if ("1".equals(followed)) {
				tag_lable_home_ok_rl.startAnimation(outAnimationSet);
				tag_lable_home_add_rl.startAnimation(inAnimationSet);
			}
		}
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tag_exit_bt:
				finish();
				break;
			case R.id.tag_lable_home_ok_rl:
				setDataForUnFollow();
				tag_header_like_count_ts.setInAnimation(mAnimationUpIn);
				tag_header_like_count_ts.setOutAnimation(mAnimationDownOut);
				break;
			case R.id.tag_lable_home_add_rl:
				setDataForFollow();
				tag_header_like_count_ts.setInAnimation(mAnimationDownIn);
				tag_header_like_count_ts.setOutAnimation(mAnimationUpOut);
				break;

			default:
				break;
			}
		}
	};
	private class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			
			return ListViewFragment.newInstance(position,mTopicId);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.tab_country);
			case 1:
				return getString(R.string.tab_continent);
			case 2:
				return getString(R.string.tab_city);
			case 3:
				return getString(R.string.tab_scroll_view);
			}

			return "";
		}
	}
}
