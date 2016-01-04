package com.edaoyou.collections.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.utils.CollectionBitmapUtils;

public class RegiesterStep1Activity extends BaseActivity implements OnClickListener {

	private FrameLayout set_head_fl;
	private Button regiester_step1_back_bt;
	private Button regiester_camera_bt;
	private Button regiester_photo_bt;

	private Uri mCameraUri;
	private static final int ACTIVITY_RESULT_STEP2 = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_regiester_step1;
	}

	@Override
	protected void findViews() {
		set_head_fl = (FrameLayout) findViewById(R.id.set_head_fl);
		regiester_step1_back_bt = (Button) findViewById(R.id.regiester_step1_back_bt);
		regiester_camera_bt = (Button) findViewById(R.id.regiester_camera_bt);
		regiester_photo_bt = (Button) findViewById(R.id.regiester_photo_bt);
	}

	@Override
	protected void setListensers() {
		set_head_fl.setOnClickListener(this);
		regiester_step1_back_bt.setOnClickListener(this);
		regiester_camera_bt.setOnClickListener(this);
		regiester_photo_bt.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case GlobalParams.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: // 系统相机
			if (resultCode == Activity.RESULT_OK) {
				if (intent == null && mCameraUri != null) {
					CollectionBitmapUtils.startPhotoZoom(this, mCameraUri);
				} else {
					CollectionBitmapUtils.startPhotoZoom(this, intent.getData());
				}
			}
			break;
		case GlobalParams.GET_PICTURE_FROM_XIANGCE_CODE: // 系统相册回调
			if (resultCode == Activity.RESULT_OK) {
				CollectionBitmapUtils.startPhotoZoom(this, intent.getData());
			}
			break;
		case GlobalParams.GET_CUT_PICTURE_CODE: // 剪后图片之后回调
			if (resultCode == Activity.RESULT_OK) {
				gotoStep2Activity(intent);
			}
			break;
		case RegiesterStep1Activity.ACTIVITY_RESULT_STEP2:
			boolean isRegiesterSuccess = intent.getBooleanExtra(LoginAndRegiesterActivity.KEY_REGIESTER, false);
			if (isRegiesterSuccess) {
				notifyPreActivity(true);
				RegiesterStep1Activity.this.finish();
			} else {
				notifyPreActivity(false);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			notifyPreActivity(false);
			RegiesterStep1Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void gotoStep2Activity(Intent intent) {
		intent.setClass(RegiesterStep1Activity.this, RegiesterStep2Activity.class);
		startActivityForResult(intent, RegiesterStep1Activity.ACTIVITY_RESULT_STEP2);
		overridePendingTransition(R.anim.fade_in, R.anim.welcome_out);
	}

	/**
	 * 通知上一个Activity结果
	 */
	private void notifyPreActivity(boolean isRegiesterSuccess) {
		Intent intent = getIntent();
		intent.putExtra(LoginAndRegiesterActivity.KEY_REGIESTER, isRegiesterSuccess);
		setResult(0, intent);
	}

	/**
	 * 从相册里面获取图片。
	 */
	private void getoSystemPhoto() {
		Intent intent_pick = new Intent(Intent.ACTION_PICK, null);
		intent_pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CollectionBitmapUtils.IMAGE_UNSPECIFIED);
		startActivityForResult(intent_pick, GlobalParams.GET_PICTURE_FROM_XIANGCE_CODE);
	}

	/**
	 * 调用系统的照相机
	 */
	private void getoSystemCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String filename = timeStampFormat.format(new Date());
		ContentValues values = new ContentValues();
		values.put(Media.TITLE, filename);
		mCameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
		startActivityForResult(intent, GlobalParams.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.set_head_fl:
			getoSystemCamera();
			break;
		case R.id.regiester_step1_back_bt:
			notifyPreActivity(false);
			RegiesterStep1Activity.this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_down_out);
			break;
		case R.id.regiester_camera_bt:
			getoSystemCamera();
			break;
		case R.id.regiester_photo_bt:
			getoSystemPhoto();
			break;
		default:
			break;
		}
	}
}
