package com.edaoyou.collections.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.CheckVersionBean;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.CustomDialog;

public class SettingAboutCollActivity extends BaseActivity implements OnClickListener {
	private LinearLayout setting_check_version_ll;
	private LinearLayout setting_emali_ll;

	private String mUid;
	private String mSid;
	private String mCheckVersionUrl;
	private CheckVersionBean mCheckBean;

	@Override
	protected int setContentView() {
		return R.layout.activity_setting_about;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUid = UserUtil.getUserUid(mContext);
		mSid = UserUtil.getUserSid(mContext);
		mCheckVersionUrl = ConstantValue.COMMONURI + ConstantValue.CHECK_VERSION;
	}

	@Override
	protected void findViews() {
		setting_check_version_ll = (LinearLayout) findViewById(R.id.setting_check_version_ll);
		setting_emali_ll = (LinearLayout) findViewById(R.id.setting_emali_ll);
	}

	@Override
	protected void setListensers() {
		setting_check_version_ll.setOnClickListener(this);
		setting_emali_ll.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_check_version_ll:
			setting_check_version_ll.setEnabled(false);
			setting_emali_ll.setEnabled(false);

			JSONObject jsonObject = getJSONObjectData();
			initData(mCheckVersionUrl, jsonObject);

			break;
		case R.id.setting_emali_ll:
			Intent data = new Intent(Intent.ACTION_SENDTO);
			data.setData(Uri.parse("mailto:jia.cang@tcollections.com"));
			data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
			data.putExtra(Intent.EXTRA_TEXT, "这是内容");
			startActivity(data);
			break;

		default:
			break;
		}
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

	private JSONObject getJSONObjectData() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();

		try {
			request.put("build", GlobalParams.UPDATA_BUILD);
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", GlobalParams.UPDATA_VER);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		mCheckBean = GsonUtils.json2bean(result, CheckVersionBean.class);

		int ret = mCheckBean.ret;
		String update = mCheckBean.response.update;

		if (ret == 0 && "1".equals(update)) {
			progressGone();
			showCheckVersionDialog();
		} else {
			progressGone();
			ToastUtils.showToast(mContext, "已是最新版本!");
		}
	}

	@Override
	protected void initDataOnFailure(String url) {
		super.initDataOnFailure(url);
		progressGone();
	}

	/**
	 * 展示dialog
	 */

	private void showCheckVersionDialog() {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(mCheckBean.response.txt);
		builder.setTitle("检查更新");
		builder.setPositiveButton("稍后", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("更新", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Uri uri = Uri.parse(mCheckBean.response.url);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		});

		builder.create().show();
	}

	private void progressGone() {
		setting_check_version_ll.setEnabled(true);
		setting_emali_ll.setEnabled(true);
	}
}
