package com.edaoyou.collections.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.ImageView;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.CameraTeachGalleryAdapter;

/**
 * 非自由模式拍照教学页面
 */
public class CameraPatternActivity extends Activity implements OnClickListener {

	private Gallery gallery;
	private ImageView photo_close_iv;
	private ImageView goto_camera_iv;
	private CameraTeachGalleryAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_teaching);
		findViews();
		initDate();
		setListener();
	}

	private void findViews() {
		gallery = (Gallery) findViewById(R.id.gallery);
		goto_camera_iv = (ImageView) findViewById(R.id.goto_camera_iv);
		photo_close_iv = (ImageView) findViewById(R.id.photo_close_iv);
	}

	@SuppressLint("Recycle")
	private void initDate() {
		TypedArray imgs = null;
		switch (GlobalParams.CURRENT_CAMERA_MODE) {
		case GlobalParams.CAMERA_MODE_CHAIRS:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.chairs_drawable);
			break;
		case GlobalParams.CAMERA_MODE_PICTURES:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.pictures_drawable);
			break;
		case GlobalParams.CAMERA_MODE_CHINAS:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.chinas_drawable);
			break;
		case GlobalParams.CAMERA_MODE_JADES:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.jades_drawable);
			break;
		case GlobalParams.CAMERA_MODE_MONEYS:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.moneys_drawable);
			break;
		case GlobalParams.CAMERA_MODE_GOLDS:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.golds_drawable);
			break;
		case GlobalParams.CAMERA_MODE_BUDDHAS:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.buddhas_drawable);
			break;
		case GlobalParams.CAMERA_MODE_ZHUCHUANS:
			imgs = CameraPatternActivity.this.getResources().obtainTypedArray(R.array.zhuchuans_drawable);
			break;
		default:
			break;
		}
		adapter = new CameraTeachGalleryAdapter(CameraPatternActivity.this, imgs);
		gallery.setAdapter(adapter);
	}

	private void setListener() {
		goto_camera_iv.setOnClickListener(this);
		photo_close_iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_close_iv:
			finish();
			break;
		case R.id.goto_camera_iv:
			Intent intent = new Intent(this, CustomCameraActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
