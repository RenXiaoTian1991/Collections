package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.ShareView;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

public class PersonalAddfriendActivity extends BaseActivity implements OnClickListener {
	private EditText search_friend_et;
	private TextView addfriend_myname_tv;

	private LinearLayout addfriend_from_phone_ll;
	private LinearLayout addfriend_from_qq_ll;
	private LinearLayout addfriend_from_wx_ll;

	private String mUserInFoUrl;
	private String mUid;

	private ShareView mShareView;
	private SsoHandler mSsoHandler;// 新浪微博api
	private AuthInfo mAuthInfo;

	@Override
	protected int setContentView() {
		return R.layout.activity_personal_addfriend;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUid = UserUtil.getUserUid(mContext);

		mUserInFoUrl = ConstantValue.COMMONURI + ConstantValue.PROFILE;
		JSONObject jsonObject = getUserJsonData();
		initData(mUserInFoUrl, GsonUtils.getJSONObjectForUSer(getApplicationContext(), jsonObject));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (mSsoHandler != null) {// 新浪微博 SSO 登录授权时，需要加上
			mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
		}
	}

	private JSONObject getUserJsonData() {
		JSONObject request = new JSONObject();
		try {
			request.put("uid", mUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	@Override
	protected void findViews() {
		search_friend_et = (EditText) findViewById(R.id.search_friend_et);
		addfriend_from_phone_ll = (LinearLayout) findViewById(R.id.addfriend_from_phone_ll);
		addfriend_from_qq_ll = (LinearLayout) findViewById(R.id.addfriend_from_qq_ll);
		addfriend_from_wx_ll = (LinearLayout) findViewById(R.id.addfriend_from_wx_ll);
		addfriend_myname_tv = (TextView) findViewById(R.id.addfriend_myname_tv);
	}

	@Override
	protected void setListensers() {
		search_friend_et.setOnClickListener(this);
		addfriend_from_phone_ll.setOnClickListener(this);
		addfriend_from_qq_ll.setOnClickListener(this);
		addfriend_from_wx_ll.setOnClickListener(this);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		User jsonBean = GsonUtils.json2bean(result, User.class);
		String userName = jsonBean.response.username;
		addfriend_myname_tv.setText("我的昵称：" + userName);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.addfriend_from_phone_ll:
			intent = new Intent(PersonalAddfriendActivity.this, AddfriendPhoneActivity.class);
			startActivity(intent);
			break;
		case R.id.addfriend_from_qq_ll:
			initShare();
			mShareView.setQQData(getShareQQData());
			mShareView.shareToQQ();
			break;
		case R.id.addfriend_from_wx_ll:
			initShare();
			mShareView.setWeiXinData(getShareWeiXinData());
			mShareView.shareToWeiXin();
			break;
		case R.id.search_friend_et:
			intent = new Intent(PersonalAddfriendActivity.this, SearchFriendActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void initShare() {
		if (mShareView == null) {
			mShareView = new ShareView(this);
			mAuthInfo = new AuthInfo(this, GlobalParams.WEI_BO_APP_KEY, GlobalParams.WEI_BO_REDIRECT_URL, GlobalParams.WEI_BO_SCOPE);
			mSsoHandler = new SsoHandler(this, mAuthInfo);
		}
	}
	
	private Bundle getShareQQData() {
		Bundle bundle = new Bundle();
		bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "邀请您加入“藏·家”中国权威收藏艺术品交流交易平台" + getString(R.string.share_app2));
		bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "藏家");
		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, ConstantValue.SHARE_APP);
		bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, BimpUtil.getLauncherPath(this));
		bundle.putString(QQShare.SHARE_TO_QQ_KEY_TYPE, "藏家" + GlobalParams.QQ_APP_ID);
		return bundle;
	}

	private WXMediaMessage getShareWeiXinData() {
		WXMediaMessage msg = new WXMediaMessage();
		Bitmap bitmapFromDiskCache = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		byte[] arr = BimpUtil.bmpToByteArray(bitmapFromDiskCache);
		msg.thumbData = arr;
		msg.title = "邀请您加入“藏·家”中国权威收藏艺术品交流交易平台";
		msg.description = getString(R.string.share_app2);
		WXWebpageObject web = new WXWebpageObject();
		String shareUrl = ConstantValue.SHARE_APP;
		web.webpageUrl = shareUrl;
		msg.mediaObject = web;
		return msg;
	}
}
