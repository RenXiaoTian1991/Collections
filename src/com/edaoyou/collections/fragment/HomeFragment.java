package com.edaoyou.collections.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.ChatAndNotificationActivity;
import com.edaoyou.collections.activity.FastRegiesterActivity;
import com.edaoyou.collections.activity.SearchActivity;
import com.edaoyou.collections.adapter.HomePagerAdapter;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.etsy.XViewPager;

public class HomeFragment extends BaseFragment implements OnClickListener {

	private TextView choiceness_tv; // 精选集
	private TextView collection_friends_tv; // 藏友圈
	private TextView home_unread_msg_number; // 新消息
	private ImageView home_message_iv;
	private ImageView home_search_iv;
	private XViewPager home_vp;
	private HomePagerAdapter mAdapter;

	private ArrayList<BaseFragment> mFragments = new ArrayList<BaseFragment>();
	//是否切换到新的fragment
	private boolean mIsNewPage;
	//当前的fragment位置
	private int mCurrentPage;
    //检测是否有新消息提醒
	private String mCheckNoticeUrl;
    //因为藏友圈和精选共用一个fragment，所以在构造的时候要进行标记显示不同的数据
	public static final String mIsChoiceness = "isChoiceness_Key";// 往ChoicenessFragment里面传的标记，如果是精选集为true,藏友圈为false
	private RelativeLayout home_message_rl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//当fragment附到activity的时候就进行消息检查，如果用户登陆，则进行网络操作查看，根据结果更新消息提示的UI
		mCheckNoticeUrl = ConstantValue.COMMONURI + ConstantValue.CHECK_NOTICE;
		if (GlobalParams.LOGIN_TYPE_NULL != UserUtil.checkUserIsLogin(mContext)) {// 判断用户是否登录
			checkNotice();
		}
	}

	@Override
	protected void initVariable() {
		initFragments();
	}

	@Override
	protected int setContentView() {
		return R.layout.fragment_home;
	}

	@Override
	protected void findViews(View rootView) {
		choiceness_tv = (TextView) rootView.findViewById(R.id.home_choiceness_tv);
		collection_friends_tv = (TextView) rootView.findViewById(R.id.home_collection_friends_tv);
		home_message_rl = (RelativeLayout) rootView.findViewById(R.id.home_message_rl);
		home_message_iv = (ImageView) rootView.findViewById(R.id.home_message_iv);
		home_unread_msg_number = (TextView) rootView.findViewById(R.id.home_unread_msg_number);
		home_search_iv = (ImageView) rootView.findViewById(R.id.home_search_iv);
		home_vp = (XViewPager) rootView.findViewById(R.id.home_vp);
	}

	@Override
	protected void setListensers() {
		choiceness_tv.setOnClickListener(this);
		collection_friends_tv.setOnClickListener(this);
		home_message_rl.setOnClickListener(this);
		home_search_iv.setOnClickListener(this);
		home_vp.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	protected String getLevel() {
		return GlobalParams.LEVEL_1;
	}

	/**
	 * 检查未读消息
	 */
	private void checkNotice() {
		JSONObject checkNoticeObject = GsonUtils.getJSONObjectForUSer(getActivity(), new JSONObject());
		initData(mCheckNoticeUrl, checkNoticeObject);
	}

	@Override
	protected void initDataOnSucess(String result, String url) {
		super.initDataOnSucess(result, url);
		if (mCheckNoticeUrl.equals(url)) {
			//根据结果进行显示
			isShowMsgNumber(result);
		}
	}

	private void isShowMsgNumber(String result) {
		try {
			JSONObject json = new JSONObject(result);
			JSONObject optJSONObject = json.optJSONObject("response");
			int count = optJSONObject.optInt("count");
			if (count > 0) {
				refreshUI();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    // 共用一个fragment，根据
	private void initFragments() {
		Bundle choicenessBundle = new Bundle();
		choicenessBundle.putBoolean(HomeFragment.mIsChoiceness, true);
		mFragments.add((BaseFragment) Fragment.instantiate(mContext, ChoicenessAndFriendsFragment.class.getName(), choicenessBundle));

		Bundle friendsBundle = new Bundle();
		friendsBundle.putBoolean(HomeFragment.mIsChoiceness, false);
		mFragments.add((BaseFragment) Fragment.instantiate(mContext, ChoicenessAndFriendsFragment.class.getName(), friendsBundle));

		mAdapter = new HomePagerAdapter(getChildFragmentManager(), mFragments);
		home_vp.setOffscreenPageLimit(mFragments.size());
		home_vp.setAdapter(mAdapter);
		home_vp.setCurrentItem(0);
	}

	private void changeTab() {
		if (mCurrentPage == 0) {
			choiceness_tv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nav_highlighted));
			collection_friends_tv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nav_normal));
			choiceness_tv.setTextColor(mContext.getResources().getColor(R.color.white));
			collection_friends_tv.setTextColor(mContext.getResources().getColor(R.color.red));
		} else {
			choiceness_tv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nav_normal));
			collection_friends_tv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nav_highlighted));
			choiceness_tv.setTextColor(mContext.getResources().getColor(R.color.red));
			collection_friends_tv.setTextColor(mContext.getResources().getColor(R.color.white));
		}
	}

	private void loadData(int currentPage) {
		ChoicenessAndFriendsFragment fragment = (ChoicenessAndFriendsFragment) mAdapter.getItem(currentPage);
		if (currentPage == 0) {
			fragment.getChoicenessData();
		} else {
			fragment.getFriendsData();
		}
	}

	private void gotoChatAndNotificationActivity() {
		Intent intent = new Intent(mContext, ChatAndNotificationActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.fade_out);
	}

	private void gotoSearchActivity() {
		Intent intent = new Intent(mContext, SearchActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
	}

	private void gotoFastRegiesterActivity() {
		Intent intent = new Intent(mContext, FastRegiesterActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
	}

	public void refreshUI() {
		home_unread_msg_number.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.home_choiceness_tv:
			home_vp.setCurrentItem(0);
			break;
		case R.id.home_collection_friends_tv:
			home_vp.setCurrentItem(1);
			break;
		case R.id.home_message_rl:
			if (GlobalParams.LOGIN_TYPE_NULL == UserUtil.checkUserIsLogin(mContext)) {
				gotoFastRegiesterActivity();
			} else {
				gotoChatAndNotificationActivity();
			}
			break;
		case R.id.home_search_iv:
			gotoSearchActivity();
			break;
		default:
			break;
		}
	}

	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			mIsNewPage = true;
			mCurrentPage = position;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (getActivity() == null) {
				return;
			}
			switch (arg0) {
			case ViewPager.SCROLL_STATE_IDLE:
				if (mIsNewPage) {
					mIsNewPage = false;
					loadData(mCurrentPage);
					changeTab();
				}
				break;
			case ViewPager.SCROLL_STATE_DRAGGING:
				break;
			case ViewPager.SCROLL_STATE_SETTLING:
				break;
			default:
				break;
			}
		}
	}
}