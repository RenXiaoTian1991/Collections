package com.edaoyou.collections.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 接收微信返回值的类
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI mWeiXinApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWeiXinApi = WXAPIFactory.createWXAPI(this, GlobalParams.WEI_XIN_APP_ID, false);
		mWeiXinApi.registerApp(GlobalParams.WEI_XIN_APP_ID);
		mWeiXinApi.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mWeiXinApi.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK: // 发送成功
			if (resp instanceof SendAuth.Resp) {// 判断是否是授权登录
				SendAuth.Resp sendAuthResp = (Resp) resp;
				String weiXinCode = sendAuthResp.code;
				saveWeiXinCode(weiXinCode);
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:// 发送取消
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:// 发送被拒绝
			break;
		default:// 发送返回
			break;
		}
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	private void saveWeiXinCode(String weiXinCode) {
		SharedPreferencesUtils.getInstance(this).saveString(GlobalParams.WEI_XIN_CODE_KEY, weiXinCode);
	}
}
