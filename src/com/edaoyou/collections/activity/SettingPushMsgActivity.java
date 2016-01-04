package com.edaoyou.collections.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.CustomSwitchButton;
import com.igexin.sdk.PushManager;

public class SettingPushMsgActivity extends BaseActivity implements OnCheckedChangeListener {
	private CustomSwitchButton setting_pushmsg_time_csbnt;
	private CustomSwitchButton setting_pushmsg_csbnt;

	private SharedPreferencesUtils mSp;

	private boolean mPushMsgSwicth;
	private boolean mPushMsgTime;
	private boolean mSilentTime;

	@Override
	protected int setContentView() {
		return R.layout.activity_setting_pushmsg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSp = SharedPreferencesUtils.getInstance(mContext);

		// 消息推送的boolean sp
		mPushMsgSwicth = mSp.getBooleanTrue(UserUtil.getUserUid(mContext) + "pushmsg_switch");
		setting_pushmsg_csbnt.setChecked(mPushMsgSwicth);

		// 消息推送防骚扰的boolean sp
		mPushMsgTime = mSp.getBooleanTrue(UserUtil.getUserUid(mContext) + "pushmsg_time");
		setting_pushmsg_time_csbnt.setChecked(mPushMsgTime);

	}

	@Override
	protected void findViews() {
		setting_pushmsg_csbnt = (CustomSwitchButton) findViewById(R.id.setting_pushmsg_csbnt);
		setting_pushmsg_time_csbnt = (CustomSwitchButton) findViewById(R.id.setting_pushmsg_time_csbnt);
	}

	@Override
	protected void setListensers() {
		setting_pushmsg_csbnt.setOnCheckedChangeListener(this);
		setting_pushmsg_time_csbnt.setOnCheckedChangeListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.setting_pushmsg_csbnt:
			if (isChecked) {
				// 重新初始化sdk
				PushManager.getInstance().initialize(this.getApplicationContext());
				ToastUtils.showToast(mContext, "消息推送，打开!");

			} else {
				// 当前为运行状态，停止SDK服务
				PushManager.getInstance().stopService(this.getApplicationContext());
				ToastUtils.showToast(mContext, "消息推送，关闭!");
			}
			mSp.saveBoolean(UserUtil.getUserUid(mContext) + "pushmsg_switch", isChecked);

			break;
		case R.id.setting_pushmsg_time_csbnt:
			mSilentTime = PushManager.getInstance().setSilentTime(mContext, 23, 9);
			if (isChecked) {
				if (mSilentTime) {
					ToastUtils.showToast(mContext, "免打扰模式，打开!");
				} else {
					return;
				}
			} else {
				if (mSilentTime) {
					ToastUtils.showToast(mContext, "免打扰模式，关闭!");
				} else {
					return;
				}
			}
			mSp.saveBoolean(UserUtil.getUserUid(mContext) + "pushmsg_time", isChecked);
			break;

		default:
			break;
		}
	}
}
