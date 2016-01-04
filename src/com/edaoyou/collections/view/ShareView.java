package com.edaoyou.collections.view;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.Util;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

/**
 * 分享
 */
public class ShareView {
	private Activity mActivity;
	private AlertDialog mAlertDialog;

	private IWXAPI mWeiXinApi; // 微信api
	private Tencent mTencent;// QQapi
	private SsoHandler mSsoHandler;// 新浪微博api

	private String mWeiBoToken;
	private String mWeiBoUid;
	private AuthInfo mAuthInfo;
	private IWeiboShareAPI mWeiboShareAPI;
	private long mExpiresTime;// 新浪微博token过期时间

	private WXMediaMessage mWXMediaMessage;// 分享微信的载体
	private WeiboMultiMessage mWeiboMultiMessage;// 分享微博的载体
	private Bundle mBundle;// 分享qq的载体
	private Handler mHandler;

	public static final int NO_REPROT = 1;
	public static final int REPROT_MSG = 4;

	public ShareView(Activity activity) {
		mActivity = activity;
	}

	public void setHandler(Handler handle) {
		this.mHandler = handle;
	}

	/**
	 * 举报的状态，false默认Gone;;是否举报, 0 举报
	 * 
	 * @param isShowReport
	 *            是否举报,false是不举报，true是举报
	 * @param reportState
	 *            举报状态 (不举报时传NO_REPROT)
	 */
	public void show(boolean isShowReport, int reportState) {// 举报的状态，false默认Gone;;是否举报
																// 0已举报
		mAlertDialog = new AlertDialog.Builder(mActivity).create();
		mAlertDialog.show();
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mActivity);
		window.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_linlang_share, null);
		window.setContentView(contentView);

		TextView linlang_dialog_share_cancel = (TextView) window.findViewById(R.id.dialog_cancel);
		ImageView share_weixin = (ImageView) window.findViewById(R.id.linlang_share_weixin);
		ImageView share_friend = (ImageView) window.findViewById(R.id.linlang_share_friend);
		ImageView share_weibo = (ImageView) window.findViewById(R.id.linlang_share_weibo);
		ImageView share_qq = (ImageView) window.findViewById(R.id.linlang_share_qq);

		LinearLayout linlang_report_ll = (LinearLayout) window.findViewById(R.id.linlang_report_ll);
		TextView linlang_report_tv = (TextView) window.findViewById(R.id.linlang_report_tv);
		if (isShowReport) {
			linlang_report_ll.setVisibility(View.VISIBLE);

			if (0 == reportState) {
				linlang_report_tv.setText("举报");
				linlang_report_ll.setOnClickListener(mDialogClickListener);
			} else {
				linlang_report_tv.setText("已举报");
				linlang_report_tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mAlertDialog.dismiss();
					}
				});
			}

		} else {
			linlang_report_tv.setText("");
		}

		share_weixin.setOnClickListener(mDialogClickListener);
		share_friend.setOnClickListener(mDialogClickListener);
		share_weibo.setOnClickListener(mDialogClickListener);
		share_qq.setOnClickListener(mDialogClickListener);
		linlang_dialog_share_cancel.setOnClickListener(mDialogClickListener);
	}

	/**
	 * 设置分享微信内容
	 */
	public void setWeiXinData(WXMediaMessage msg) {
		this.mWXMediaMessage = msg;
	}

	/**
	 * 设置分享微博内容
	 */
	public void setWeiboData(SsoHandler ssoHandler, AuthInfo authInfo, WeiboMultiMessage msg) {
		if (mSsoHandler == null) {
			mSsoHandler = ssoHandler;
		}
		if (mAuthInfo == null) {
			mAuthInfo = authInfo;
		}
		this.mWeiboMultiMessage = msg;
	}

	public void shareToQQ() {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(GlobalParams.QQ_APP_ID, mActivity);
		}
		mTencent.shareToQQ(mActivity, mBundle, null);
	}

	public void setQQData(Bundle bundle) {
		this.mBundle = bundle;
	}

	public void shareToWeiXin() {
		shareWeiXin(false);
	}

	private void shareToFriend() {
		shareWeiXin(true);
	}

	private void shareWeiXin(boolean isToFriend) {
		if (mWeiXinApi == null) {
			mWeiXinApi = WXAPIFactory.createWXAPI(mActivity, GlobalParams.WEI_XIN_APP_ID);
			mWeiXinApi.registerApp(GlobalParams.WEI_XIN_APP_ID);
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		if (isToFriend) {
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		} else {
			req.scene = SendMessageToWX.Req.WXSceneSession;
		}
		req.transaction = buildTransaction("webpage");
		req.message = mWXMediaMessage;
		mWeiXinApi.sendReq(req);
	}

	private void checkShareToWeiBo() {
		mWeiBoToken = SharedPreferencesUtils.getInstance(mActivity).getString(GlobalParams.WEI_BO_TOKEN_KEY);
		if (TextUtils.isEmpty(mWeiBoToken)) {
			mSsoHandler.authorize(new MyWeiboGetTokenListener());
		} else {
			shareToWeiBo();
		}
	}

	private void shareToWeiBo() {
		if (mWeiboShareAPI == null) {
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mActivity, GlobalParams.WEI_BO_APP_KEY);
			mWeiboShareAPI.registerApp();
		}
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = mWeiboMultiMessage;
		mWeiboShareAPI.sendRequest(mActivity, request, mAuthInfo, mWeiBoToken, null);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	private void saveWeiBoToken() {
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(mActivity);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_BO_TOKEN_KEY, mWeiBoToken);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_BO_UID_KEY, mWeiBoUid);
		sharedPreferencesUtils.saveLong(GlobalParams.WEI_BO_EXPIRES_TIME_KEY, mExpiresTime);
	}

	private OnClickListener mDialogClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.linlang_share_weixin:
				shareToWeiXin();
				break;
			case R.id.linlang_share_friend:
				shareToFriend();
				break;
			case R.id.linlang_share_weibo:
				checkShareToWeiBo();
				break;
			case R.id.linlang_share_qq:
				shareToQQ();
				break;
			case R.id.dialog_cancel:
				break;
			case R.id.linlang_report_ll:
				Message msg = Message.obtain();
				msg.what = REPROT_MSG;
				mHandler.sendMessage(msg);
				break;
			default:
				break;
			}
			mAlertDialog.cancel();
		}
	};

	private class MyWeiboGetTokenListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				mWeiBoToken = mAccessToken.getToken();
				mWeiBoUid = mAccessToken.getUid();
				mExpiresTime = mAccessToken.getExpiresTime();
				saveWeiBoToken();
				shareToWeiBo();
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = mActivity.getString(R.string.weibosdk_demo_toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				ToastUtils.showToast(mActivity, message, ToastUtils.LONG);
			}
		}

		@Override
		public void onCancel() {
			ToastUtils.showToast(mActivity, "取消新浪微博授权");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			ToastUtils.showToast(mActivity, "新浪微博授权失败");
		}
	}
}
