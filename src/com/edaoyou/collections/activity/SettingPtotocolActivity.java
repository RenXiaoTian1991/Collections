package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.PhotoGraphTagClientBoot;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.GsonUtils;

public class SettingPtotocolActivity extends BaseActivity {
	private WebView user_agreement_wv;

	private String mVer;
	private String mSid;
	private String mUid;
	private String mBootUrl;

	@Override
	protected int setContentView() {
		return R.layout.activity_setting_ptotocol;
	}

	@Override
	protected void findViews() {
		user_agreement_wv = (WebView) findViewById(R.id.user_agreement_wv);
	}

	@Override
	protected void setListensers() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mVer = GlobalParams.ver;
		mSid = (String) UserUtil.getUserSid(this);
		mUid = (String) UserUtil.getUserUid(this);
		mBootUrl = ConstantValue.COMMONURI + ConstantValue.BOOT;

		JSONObject jsonObject = getJSONObject();
		initData(mBootUrl, jsonObject);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		PhotoGraphTagClientBoot jsonBean = GsonUtils.json2bean(result, PhotoGraphTagClientBoot.class);
		String agreementUrl = jsonBean.response.agreement_url;

		WebSettings ws = user_agreement_wv.getSettings();

		ws.setAllowFileAccess(true); // 允许访问文件
		ws.setBuiltInZoomControls(true); // 设置显示缩放按钮
		ws.setSupportZoom(true); // 支持缩放

		/**
		 * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
		 * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
		 */
		ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		ws.setDefaultTextEncodingName("utf-8"); // 设置文本编码
		ws.setAppCacheEnabled(true);
		ws.setCacheMode(WebSettings.LOAD_DEFAULT);// 设置缓存模式

		// 设置打开的网页
		user_agreement_wv.loadUrl(agreementUrl);

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

	private JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("ver", mVer);
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
}
