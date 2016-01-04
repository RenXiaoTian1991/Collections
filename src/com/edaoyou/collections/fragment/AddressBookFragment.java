package com.edaoyou.collections.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.PersonalAddfriendActivity;
import com.edaoyou.collections.activity.PersonalFamousActivity;
import com.edaoyou.collections.activity.PersonalHomepageActivity;
import com.edaoyou.collections.activity.PersonalSubscribeActivity;
import com.edaoyou.collections.adapter.AddressBookAdapter;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.bean.Bean.Follow;
import com.edaoyou.collections.bean.FollowList;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.ListViewForScrollView;

public class AddressBookFragment extends BaseFragment {
	private ScrollView personal_scrollview;
	private ListViewForScrollView address_personal_lvScroll;
	private AddressBookAdapter mAddressBookAdapter;
	private MyOnClickListener mOnClickListener;
	private LinearLayout personal_homepage_ll, personal_famous_ll, personal_subscribe_ll;
	private ImageButton home_topic_ib;

	private String mContactsUrl;
	private String mSid;
	private String mVer;
	private String mUid;
	private ArrayList<Follow> mFollowList;

	public static final int ACTIVITY_RESULT_LOGOUT = 1;
	public static final String IS_LOGOUT = "is_logout";

	private boolean isAnimIn;
	private boolean isFirstIn = true;

	@Override
	protected void initVariable() {
		mContactsUrl = ConstantValue.COMMONURI + ConstantValue.CONTACTS_LIST;
		mSid = UserUtil.getUserSid(getActivity());
		mUid = UserUtil.getUserUid(getActivity());
		mVer = GlobalParams.ver;

		personal_scrollview.smoothScrollTo(0, 0);
	}

	@Override
	protected int setContentView() {
		return R.layout.address_book_fragment;
	}

	@Override
	protected void findViews(View rootView) {
		home_topic_ib = (ImageButton) rootView.findViewById(R.id.personal_addfriend_ib);
		address_personal_lvScroll = (ListViewForScrollView) rootView.findViewById(R.id.address_personal_lvScroll);
		personal_scrollview = (ScrollView) rootView.findViewById(R.id.personal_scrollview);
		personal_homepage_ll = (LinearLayout) rootView.findViewById(R.id.personal_homepage_ll);
		personal_famous_ll = (LinearLayout) rootView.findViewById(R.id.personal_famous_ll);
		personal_subscribe_ll = (LinearLayout) rootView.findViewById(R.id.personal_subscribe_ll);
	}

	@Override
	protected void setListensers() {
		mOnClickListener = new MyOnClickListener();
		home_topic_ib.setOnClickListener(mOnClickListener);
		personal_homepage_ll.setOnClickListener(mOnClickListener);
		personal_famous_ll.setOnClickListener(mOnClickListener);
		personal_subscribe_ll.setOnClickListener(mOnClickListener);
	}

	@Override
	protected String getLevel() {
		return GlobalParams.LEVEL_1;
	}

	@Override
	public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
		Animation animation = null;
		try {
			animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
		} catch (NotFoundException e) {
			animation = AnimationUtils.loadAnimation(getActivity(), enter ? R.anim.push_right_in : R.anim.push_right_out);
			e.printStackTrace();
		}
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isAnimIn = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (enter) {
					getContactsListData();
				}
			}
		});
		return animation;

	}

	@Override
	public void onStart() {
		super.onStart();
		if (isFirstIn) {
			isFirstIn = false;
			return;
		}

		if (!isAnimIn) {
			getContactsListData();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		switch (requestCode) {
		case ACTIVITY_RESULT_LOGOUT:
			boolean isLogout = intent.getBooleanExtra(IS_LOGOUT, false);
			if (isLogout) {
				getActivity().finish();
			}
			break;
		default:
			break;
		}
	}

	private void getContactsListData() {
		JSONObject contactsListJSONObject = getContactsListJSONObject();
		initData(mContactsUrl, contactsListJSONObject);
	}

	private JSONObject getContactsListJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("uid", mUid);
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
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (getView() != null) {
			getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	protected void initDataOnSucess(String result, String url) {
		super.initDataOnSucess(result, url);
		if (mContactsUrl.equals(url)) {
			updataUI(result);
		}

		isAnimIn = false;
	}

	private void updataUI(String result) {
		try {
			FollowList jsonBean = GsonUtils.json2bean(result, FollowList.class);
			mFollowList = (ArrayList<Follow>) jsonBean.response.list;
			mAddressBookAdapter = new AddressBookAdapter(getActivity(), mFollowList, mBitmapUtils);
			address_personal_lvScroll.setAdapter(mAddressBookAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyOnClickListener implements OnClickListener {

		private Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.personal_addfriend_ib:
				intent.setClass(getActivity(), PersonalAddfriendActivity.class);
				startActivity(intent);
				break;
			case R.id.personal_homepage_ll:
				intent.setClass(mContext, PersonalHomepageActivity.class);
				intent.putExtra(GlobalParams.USER_UID, UserUtil.getUserUid(getActivity()));
				startActivityForResult(intent, ACTIVITY_RESULT_LOGOUT);
				break;
			case R.id.personal_famous_ll:
				intent.setClass(getActivity(), PersonalFamousActivity.class);
				startActivity(intent);
				break;
			case R.id.personal_subscribe_ll:
				intent.setClass(getActivity(), PersonalSubscribeActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	}
}
