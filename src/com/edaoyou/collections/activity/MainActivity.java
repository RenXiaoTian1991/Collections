package com.edaoyou.collections.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.fragment.AddressBookFragment;
import com.edaoyou.collections.fragment.HomeFragment;
import com.edaoyou.collections.fragment.PhotoGraphFragment;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.FloatingActionButton;

public class MainActivity extends BaseActivity implements OnClickListener {

	private ImageButton camera_ib;
	private RadioGroup main_rg;
	private RadioButton home_rb;
	private RadioButton address_book_rb;
	private FloatingActionButton main_toast;

	private Animation mHomeCameraAnimationIn;
	private Animation mHomeCameraAnimationOut;

	private NewMessageReceiver mNewMessageReceiver;
	private KillMainBroadcastReceiver mKillMainBroadcastReceiver;

	private List<Fragment> mFragments = new ArrayList<Fragment>();
	private int mCurrentTab; // 当前的Fragment的index
	private long mExitTime;
	private boolean mIsShowCameraFragment; // 是否显示CameraFragment
	private boolean mIsHomeFramgentBelow; // 是否是HomeFramgent在下面

	private static final long TOAST_WAIT_TIME = (long) (2.5 * 1000); // 程序进入主页面等2.5秒钟弹出Toast
	private static final long TOAST_SHOW_TIME = 5 * 1000; // Toast显示的时长

	private static final long SHOW_OTHER_WAIT_TIME = (long) (1.5 * 1000); // 程序进入主页面等1.5秒检查是否跳转其他界面

	private static final int MSG_TOAST_SHOW = 1; // 显示Toast
	private static final int MSG_TOAST_HIDE = 2; // 隐藏Toast
	private static final int MSG_GOTO_SELECT_INTEREST_ACTIVITY = 3; // 跳转到选择兴趣爱好Activity
	private static final int MSG_GOTO_LIN_LANG_ACTIVITY = 4; // 跳转到琳琅园Activity
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MainActivity.MSG_TOAST_SHOW:
				main_toast.show();
				mHandler.sendEmptyMessageDelayed(MainActivity.MSG_TOAST_HIDE, MainActivity.TOAST_SHOW_TIME);
				break;
			case MainActivity.MSG_TOAST_HIDE:
				main_toast.hide();
				break;
			case MainActivity.MSG_GOTO_SELECT_INTEREST_ACTIVITY:
				gotoSelectInterestActivity();
				break;
			case MainActivity.MSG_GOTO_LIN_LANG_ACTIVITY:
				gotoLinLangActivity();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFragments();
		initAnimation();
		checkIsShowOtherActivity();
		showMainToast();
		registerNewMessageReceiver();
		killMainBroadcastReceiver();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent == null) {
			return;
		}

		home_rb.performClick();
		checkIsShowOtherActivity();
		if (mNewMessageReceiver == null) {
			registerNewMessageReceiver();
		}
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_main;
	}

	@Override
	protected void findViews() {
		camera_ib = (ImageButton) findViewById(R.id.camera_ib);
		main_rg = (RadioGroup) findViewById(R.id.main_rg);
		home_rb = (RadioButton) findViewById(R.id.home_rb);
		home_rb.performClick();
		address_book_rb = (RadioButton) findViewById(R.id.address_book_rb);
		main_toast = (FloatingActionButton) findViewById(R.id.main_toast);
	}

	@Override
	protected void setListensers() {
		main_rg.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
		camera_ib.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EMChatManager.getInstance().activityResumed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收者
		try {
			unregisterReceiver(mNewMessageReceiver);
			mNewMessageReceiver = null;
			unregisterReceiver(mKillMainBroadcastReceiver);
			mKillMainBroadcastReceiver = null;
		} catch (Exception e) {
		}
	}

	private void initFragments() {
		mFragments.add(new HomeFragment());
		mFragments.add(new PhotoGraphFragment());
		mFragments.add(new AddressBookFragment());
		// 默认显示第一页
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (!mFragments.get(0).isAdded()) {
			ft.add(R.id.tab_content, mFragments.get(0));
		}
		ft.commit();
		mIsHomeFramgentBelow = true;
	}

	private void initAnimation() {
		mHomeCameraAnimationIn = AnimationUtils.loadAnimation(this, R.anim.home_camera_in);
		mHomeCameraAnimationOut = AnimationUtils.loadAnimation(this, R.anim.home_camera_out);
	}

	/**
	 * 切换Fragment
	 */
	private void showTab(int idx, boolean isShowCameraFragment) {
		if (mCurrentTab == idx) {
			return;
		}

		FragmentTransaction ft = obtainFragmentTransaction(idx, mIsShowCameraFragment);
		ft.hide(mFragments.get(mCurrentTab));
		if (mFragments.get(idx).isAdded()) {
			ft.show(mFragments.get(idx));
		} else {
			ft.add(R.id.tab_content, mFragments.get(idx));
		}
		ft.commit();
		mCurrentTab = idx; // 更新目标tab为当前tab
	}

	/**
	 * 获取一个带动画的FragmentTransaction
	 */
	private FragmentTransaction obtainFragmentTransaction(int index, boolean isShowCameraFragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (index == 1) {
			if (mIsShowCameraFragment) {
				ft.setCustomAnimations(R.anim.push_down_out, R.anim.fade_in);
			} else {
				ft.setCustomAnimations(R.anim.push_down_in, R.anim.fade_out);
			}
		} else {
			if (mIsShowCameraFragment) {
				ft.setCustomAnimations(R.anim.fade_in, R.anim.push_down_out);
			} else {
				if (index > mCurrentTab) {
					ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
				} else {
					ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
				}
			}

		}
		return ft;
	}

	/**
	 * 显示或者隐藏CameraFragment
	 */
	private void showOrHideCameraFragment() {
		if (mIsShowCameraFragment) {
			if (mIsHomeFramgentBelow) {
				showTab(0, mIsShowCameraFragment);
			} else {
				showTab(2, mIsShowCameraFragment);
			}
			home_rb.setVisibility(View.VISIBLE);
			address_book_rb.setVisibility(View.VISIBLE);
			camera_ib.startAnimation(mHomeCameraAnimationOut);
		} else {
			showTab(1, mIsShowCameraFragment);
			home_rb.setVisibility(View.GONE);
			address_book_rb.setVisibility(View.GONE);
			camera_ib.startAnimation(mHomeCameraAnimationIn);
		}
		mIsShowCameraFragment = !mIsShowCameraFragment;
	}

	private void showMainToast() {
		main_toast.setDuration(1000);
		mHandler.sendEmptyMessageDelayed(MainActivity.MSG_TOAST_SHOW, MainActivity.TOAST_WAIT_TIME);
	}

	private void gotoFastRegiesterActivity() {
		Intent intent = new Intent(MainActivity.this, FastRegiesterActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
	}

	private void gotoSelectInterestActivity() {
		Intent intent = new Intent(MainActivity.this, SelectInterestActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_up_in, R.anim.fade_out);
	}

	private void gotoLinLangActivity() {
		Intent intent = new Intent(MainActivity.this, LinLangActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_up_in, R.anim.fade_out);
	}

	/**
	 * 检查是否需要弹出来选择兴趣爱好Activity,或者琳琅园Activity.
	 * 用户第一次注册并登录弹出来选择兴趣爱好Actiivty,如果是第一次登录需要弹出来琳琅园Activity.
	 */
	private void checkIsShowOtherActivity() {
		boolean isShowSelectInterestActivity = SharedPreferencesUtils.getInstance(MainActivity.this)
				.getBoolean(GlobalParams.IS_FIRST_REGIESTER_LOGIN);
		boolean isShowLinLangActivity = SharedPreferencesUtils.getInstance(MainActivity.this).getBoolean(GlobalParams.IS_FIRST_LOGIN);
		if (isShowSelectInterestActivity) {
			mHandler.sendEmptyMessageDelayed(MainActivity.MSG_GOTO_SELECT_INTEREST_ACTIVITY, MainActivity.SHOW_OTHER_WAIT_TIME);
		} else if (isShowLinLangActivity) {
			mHandler.sendEmptyMessageDelayed(MainActivity.MSG_GOTO_LIN_LANG_ACTIVITY, MainActivity.SHOW_OTHER_WAIT_TIME);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.camera_ib:
//			if (GlobalParams.LOGIN_TYPE_NULL == UserUtil.checkUserIsLogin(mContext)) {
//				gotoFastRegiesterActivity();
//			} else {
//			}
			showOrHideCameraFragment();
			break;
		case R.id.main_toast:
			main_toast.hide();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				ToastUtils.showToast(mContext, "再按一次返回键退出藏家");
				mExitTime = System.currentTimeMillis();
			} else {
				DataManager.getInstance().clearDataStateMap();
				DataManager.getInstance().clearHasExecuteSet();
				MainActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
			switch (checkedId) {
			case R.id.home_rb:
				showTab(0, mIsShowCameraFragment);
				mIsHomeFramgentBelow = true;
				break;
			case R.id.address_book_rb:
				if (GlobalParams.LOGIN_TYPE_NULL == UserUtil.checkUserIsLogin(mContext)) {
					home_rb.performClick();
					gotoFastRegiesterActivity();
				} else {
					showTab(2, mIsShowCameraFragment);
					mIsHomeFramgentBelow = false;
				}
				break;
			default:
				break;
			}
		}
	}

	private void registerNewMessageReceiver() {
		mNewMessageReceiver = new NewMessageReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(mNewMessageReceiver, intentFilter);
		EMChat.getInstance().setAppInited();
	}

	private void killMainBroadcastReceiver() {
		if (mKillMainBroadcastReceiver == null) {
			mKillMainBroadcastReceiver = new KillMainBroadcastReceiver();
			IntentFilter filter = new IntentFilter(GlobalParams.KILL_MAIN);
			filter.setPriority(5);
			registerReceiver(mKillMainBroadcastReceiver, filter);
		}
	}

	/**
	 * 新消息到来刷新左上角图片
	 */
	private void refreshUI() {
		HomeFragment homeFragment = (HomeFragment) mFragments.get(0);
		homeFragment.refreshUI();
	}

	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			refreshUI();
		}
	}

	/**
	 * 接受退出登陆的广播
	 * 
	 */
	private class KillMainBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			MainActivity.this.finish();
		}
	}
}
