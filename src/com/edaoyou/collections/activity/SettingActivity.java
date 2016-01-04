package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.PersonalUserProfile;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.engine.EMManager;
import com.edaoyou.collections.fragment.AddressBookFragment;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.view.CircleImageView;
import com.edaoyou.collections.view.CustomDialog;
import com.edaoyou.collections.view.CustomSwitchButton;
import com.edaoyou.collections.view.ShareView;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

public class SettingActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {
	private TextView quit_login_tv;
	private TextView setting_user_name_tv;
	private CircleImageView setting_user_img_iv;

	private LinearLayout setting_username_ll;
	private LinearLayout setting_mytab_ll;
	private LinearLayout setting_pushmsg_ll;
	private LinearLayout setting_aboutcollections_ll;
	private LinearLayout setting_clearcache_ll;
	private LinearLayout setting_opinionback_ll;
	private LinearLayout setting_introducetofriends_ll;
	private LinearLayout setting_ptotocol_ll;

	private CustomSwitchButton setting_setchat_csbnt;
	private CustomSwitchButton setting_savepicture_csbnt;

	private AlertDialog mAlertDialog;
	private PersonalUserProfile mPupBean;

	private String mType = "";
	private String mVer;
	private String mSid;
	private String mUid;
	private String mLogoutUrl;
	private String mProfileUrl;
	private String mChatLimitUrl;

	private SharedPreferencesUtils mSp;
	private boolean mFirst;
	private boolean mSavePic;// 保存图片的boolean值
	private boolean mSaveChat;// 保存图片的boolean值
	private boolean mChatFlag;

	private ShareView mShareView;
	private SsoHandler mSsoHandler;// 新浪微博api
	private AuthInfo mAuthInfo;

	@Override
	protected int setContentView() {
		return R.layout.activity_setting;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFirst = false;
		mSp = SharedPreferencesUtils.getInstance(mContext);

		mVer = GlobalParams.ver;
		mSid = UserUtil.getUserSid(this);
		mUid = UserUtil.getUserUid(this);

		mLogoutUrl = ConstantValue.COMMONURI + ConstantValue.LOGOUT;
		mProfileUrl = ConstantValue.COMMONURI + ConstantValue.PROFILE;
		mChatLimitUrl = ConstantValue.COMMONURI + ConstantValue.CHAT_LIMIT;

		// 保存图片的sp
		mSavePic = mSp.getBoolean(UserUtil.getUserUid(mContext) + "pic");
		setting_savepicture_csbnt.setChecked(mSavePic);

		// 保存图片的sp
		mSaveChat = mSp.getBoolean(UserUtil.getUserUid(mContext) + "chat");
		setting_setchat_csbnt.setChecked(mSaveChat);

		JSONObject jsonObject = getJSONObjectForList();
		initData(mProfileUrl, jsonObject);

	}

	@Override
	protected void findViews() {
		quit_login_tv = (TextView) findViewById(R.id.quit_login_tv);
		setting_user_name_tv = (TextView) findViewById(R.id.setting_user_name_tv);
		setting_user_img_iv = (CircleImageView) findViewById(R.id.setting_user_img_iv);

		setting_username_ll = (LinearLayout) findViewById(R.id.setting_username_ll);
		setting_mytab_ll = (LinearLayout) findViewById(R.id.setting_mytab_ll);
		setting_pushmsg_ll = (LinearLayout) findViewById(R.id.setting_pushmsg_ll);
		setting_aboutcollections_ll = (LinearLayout) findViewById(R.id.setting_aboutcollections_ll);
		setting_clearcache_ll = (LinearLayout) findViewById(R.id.setting_clearcache_ll);
		setting_opinionback_ll = (LinearLayout) findViewById(R.id.setting_opinionback_ll);
		setting_introducetofriends_ll = (LinearLayout) findViewById(R.id.setting_introducetofriends_ll);
		setting_ptotocol_ll = (LinearLayout) findViewById(R.id.setting_ptotocol_ll);

		setting_setchat_csbnt = (CustomSwitchButton) findViewById(R.id.setting_setchat_csbnt);
		setting_savepicture_csbnt = (CustomSwitchButton) findViewById(R.id.setting_savepicture_csbnt);
	}

	@Override
	protected void setListensers() {
		setting_username_ll.setOnClickListener(this);
		setting_mytab_ll.setOnClickListener(this);
		setting_pushmsg_ll.setOnClickListener(this);
		setting_aboutcollections_ll.setOnClickListener(this);
		setting_clearcache_ll.setOnClickListener(this);
		setting_opinionback_ll.setOnClickListener(this);
		setting_introducetofriends_ll.setOnClickListener(this);
		setting_ptotocol_ll.setOnClickListener(this);
		quit_login_tv.setOnClickListener(this);

		setting_setchat_csbnt.setOnCheckedChangeListener(this);
		setting_savepicture_csbnt.setOnCheckedChangeListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (mSsoHandler != null) {// 新浪微博 SSO 登录授权时，需要加上
			mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.setting_username_ll:
			intent = new Intent(this, SettingUserActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			break;
		case R.id.setting_mytab_ll:
			intent = new Intent(this, SettingMyAttentionActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			break;
		case R.id.setting_pushmsg_ll:
			intent = new Intent(this, SettingPushMsgActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			break;
		case R.id.setting_aboutcollections_ll:
			intent = new Intent(this, SettingAboutCollActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			break;
		case R.id.setting_clearcache_ll:
			showClearCacheDialog();
			break;
		case R.id.setting_opinionback_ll:
			intent = new Intent(this, SettingFeedMindActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			break;
		case R.id.setting_introducetofriends_ll:
			showShareDialog();
			break;
		case R.id.setting_ptotocol_ll:
			intent = new Intent(this, SettingPtotocolActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			break;
		case R.id.quit_login_tv:
			showQuitLoginDialog();

			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.setting_setchat_csbnt:// 私聊
			if (mFirst) {
				chatLimitSetState(isChecked);
			} else {
				mFirst = true;
			}
			break;
		case R.id.setting_savepicture_csbnt:// 保存图片
			mSp.saveBoolean(UserUtil.getUserUid(mContext) + "pic", isChecked);
			break;

		default:
			break;
		}
	}

	private void chatLimitSetState(boolean isChecked) {
		if (isChecked) {
			mType = "1";
		} else {
			mType = "0";
		}
		mChatFlag = isChecked;

		JSONObject jsonObject = getJSONObjectChat();
		initData(mChatLimitUrl, jsonObject);
	}

	private JSONObject getJSONObjectChat() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("type", mType);
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getJSONObjectForList() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);
			request.put("uid", mUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 展示dialog
	 */

	private void showClearCacheDialog() {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage("根据缓存文件大小，清理时间从几秒到几分钟不等，请耐心等待...");
		builder.setTitle("清除缓存");
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("清除", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String saveDir = Util.getCachePath(mContext);
				boolean flag = Util.deletefile(saveDir);

				if (flag) {
					ToastUtils.showToast(mContext, "清除完成!");
					dialog.dismiss();
				} else {
					ToastUtils.showToast(mContext, "没有多余缓存!");
					dialog.dismiss();
				}
			}
		});

		builder.create().show();

	}

	private void showQuitLoginDialog() {
		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(true);
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		int screenHeight = Util.getDisplayHeight(mContext);
		window.setLayout(screenWidth, screenHeight * 1 / 3);
		View quit_user = View.inflate(mContext, R.layout.setting_quit_user, null);
		window.setContentView(quit_user);
		final TextView quit_ok = (TextView) quit_user.findViewById(R.id.quit_ok);
		quit_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				quit_ok.setEnabled(false);
				quitUserLoginByData();
			}
		});
		TextView quit_cancle = (TextView) quit_user.findViewById(R.id.quit_cancle);
		quit_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAlertDialog.dismiss();
			}
		});
	}

	/**
	 * 展示分享dialog
	 */
	private void showShareDialog() {
		if (mShareView == null) {
			mShareView = new ShareView(this);
			mAuthInfo = new AuthInfo(this, GlobalParams.WEI_BO_APP_KEY, GlobalParams.WEI_BO_REDIRECT_URL, GlobalParams.WEI_BO_SCOPE);
			mSsoHandler = new SsoHandler(this, mAuthInfo);
		}
		mShareView.setQQData(getShareQQData());
		mShareView.setWeiXinData(getShareWeiXinData());
		mShareView.setWeiboData(mSsoHandler, mAuthInfo, getShareWeiBoData());
		mShareView.show(false, ShareView.NO_REPROT);
	}

	private WeiboMultiMessage getShareWeiBoData() {
		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		// 文字
		TextObject textObject = new TextObject();
		String shareUrl = ConstantValue.SHARE_APP;
		String shareText = "藏家" + getString(R.string.share_app);
		textObject.text = shareText + shareUrl;
		weiboMessage.textObject = textObject;
		// 图片
		ImageObject imageObject = new ImageObject();
		Bitmap bm = BimpUtil.compressImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		imageObject.setImageObject(bm);
		weiboMessage.imageObject = imageObject;
		return weiboMessage;
	}

	private WXMediaMessage getShareWeiXinData() {
		WXMediaMessage msg = new WXMediaMessage();
		Bitmap bitmapFromDiskCache = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		byte[] arr = BimpUtil.bmpToByteArray(bitmapFromDiskCache);
		msg.thumbData = arr;
		msg.description = getString(R.string.share_app);
		WXWebpageObject web = new WXWebpageObject();
		String shareUrl = ConstantValue.SHARE_APP;
		web.webpageUrl = shareUrl;
		msg.mediaObject = web;
		return msg;
	}

	private Bundle getShareQQData() {
		Bundle bundle = new Bundle();
		bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "藏家");
		bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "藏家");
		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, ConstantValue.SHARE_APP);
		bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, BimpUtil.getLauncherPath(this));
		bundle.putString(QQShare.SHARE_TO_QQ_KEY_TYPE, "藏家" + GlobalParams.QQ_APP_ID);
		return bundle;
	}

	private void quitUserLoginByData() {
		JSONObject json = new JSONObject();
		try {
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		initData(mLogoutUrl, json);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);

		if (url.equals(mProfileUrl)) {
			mPupBean = GsonUtils.json2bean(result, PersonalUserProfile.class);
			String avatar = mPupBean.response.avatar;
			String username = mPupBean.response.username;
			setting_user_name_tv.setText(username);
			mBitmapUtils.display(setting_user_img_iv, avatar);
		}

		if (url.equals(mLogoutUrl)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				int ret = jsonObject.getInt("ret");
				int status = jsonObject.getInt("status");
				if (ret == 0 && status == 1) {
					logout();
				} else {
					ToastUtils.showToast(mContext, "网络异常");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (url.equals(mChatLimitUrl)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				int ret = jsonObject.getInt("ret");
				int status = jsonObject.getJSONObject("response").getInt("status");
				if (ret == 0 && status == 1) {
					setting_setchat_csbnt.setChecked(mChatFlag);
					mSp.saveBoolean(UserUtil.getUserUid(mContext) + "chat", mChatFlag);
					ToastUtils.showToast(mContext, "更改成功！");
				} else {
					ToastUtils.showToast(mContext, "更改失败！");
					setting_setchat_csbnt.setChecked(!mChatFlag);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void logout() {
		EMManager.getInstance().logout();
		UserUtil.logout(this);
		notifyPreActivity(true);

		DataManager.getInstance().clearDataStateMap();
		DataManager.getInstance().clearHasExecuteSet();

		// 退出登陆，给Main发送的广播
		Intent intent = new Intent();
		intent.setAction(GlobalParams.KILL_MAIN);
		SettingActivity.this.sendBroadcast(intent);

		Intent intentLog = new Intent(this, LoginAndRegiesterActivity.class);
		startActivity(intentLog);
		finish();
	}

	/**
	 * 通知上一个Activity结果
	 */
	private void notifyPreActivity(boolean isLogout) {
		Intent intent = getIntent();
		intent.putExtra(AddressBookFragment.IS_LOGOUT, isLogout);
		setResult(0, intent);
	}
}
