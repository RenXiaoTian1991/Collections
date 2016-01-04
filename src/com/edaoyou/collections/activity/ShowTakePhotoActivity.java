package com.edaoyou.collections.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.Util;

/**
 * 
 * @Description: 拍照模块显示照片
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-10-21 下午5:24:34 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class ShowTakePhotoActivity extends Activity implements OnClickListener {
	private ImageView show_photo_iv;
	private TextView delete_tv;
	private TextView complete_tv;
	private Bitmap mBitmap;
	private Intent mIntent;

	private int mFrameIndex;
	private static final int RESULT_CODE_DELETE = 0;
	private static final int RESULT_CODE_COMPLETE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_take_photo);
		findViews();
		initDate();
		setListener();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete_tv:
			BimpUtil.bmp.put(mFrameIndex, null);
			setResult(ShowTakePhotoActivity.RESULT_CODE_DELETE, mIntent);
			break;
		case R.id.complete_tv:
			setResult(ShowTakePhotoActivity.RESULT_CODE_COMPLETE, mIntent);
			break;
		default:
			break;
		}
		recycleBitmap();
		finish();
	}

	@Override
	public void onBackPressed() {
		if (mIntent != null) {
			setResult(ShowTakePhotoActivity.RESULT_CODE_COMPLETE, mIntent);
		}
		recycleBitmap();
		finish();
	}

	private void findViews() {
		show_photo_iv = (ImageView) findViewById(R.id.show_photo_iv);
		delete_tv = (TextView) findViewById(R.id.delete_tv);
		complete_tv = (TextView) findViewById(R.id.complete_tv);
	}

	private void initDate() {
		
		mIntent = getIntent();
		mFrameIndex = mIntent.getIntExtra("position", 0);
		mBitmap = BimpUtil.compressByteByTarget(this, BimpUtil.bm.get(mFrameIndex), 540, 540);// 处理成宽高540*540，并裁减成正方形。
		BitmapDrawable background = new BitmapDrawable(getResources(), mBitmap);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(Util.getDisplayWidth(this),
				Util.getDisplayWidth(this));
		show_photo_iv.setLayoutParams(p);
		show_photo_iv.setBackground(background);
	}

	private void setListener() {
		delete_tv.setOnClickListener(this);
		complete_tv.setOnClickListener(this);
	}

	private void recycleBitmap() {
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

}
