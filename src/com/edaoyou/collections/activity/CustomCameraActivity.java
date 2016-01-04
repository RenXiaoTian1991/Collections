package com.edaoyou.collections.activity;

import java.io.IOException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.CollectionBitmapUtils;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.view.CustomPicFrame;

/**
 * 
 * @Description: 自定义照相机Activity，自由模式拍照
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-9-23 下午4:19:30 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class CustomCameraActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback {
	
	private static final int SHOW_TAKE_PHOTO = 3;
	private int offset = 0;// 指示条偏移量
	private int bmpW;// 红色相框宽度
	private int currIndex = 0;// 当前选中相框位置
	private int emptyIndex = 0;// 空白相框位置
	private int mCuttentFlash = 1;// 当前闪光灯模式
	private static final int FLASH_AUTO = 1;// 自动闪光，
	private static final int FLASH_OPEN = 2;// 打开闪光灯
	private static final int FLASH_CLOSE = 3;// 关闭闪光灯
	private boolean[] isPlusImageShowed = new boolean[] { true, false, false,
			false };

	private String[] tvString = new String[4];

	private SurfaceView surfaceView;
	private ImageView ivLocalPhotos;
	private ImageView ivCamereCenterPhoto;
	private ImageView ivRadFrame;// 红色相框
	private ImageView ivTipCircle;
	private TextView tv1, tv2, tv3, tv4, tvNextStep, tvCancel, tvSpark;
	private TextView[] tvArray = new TextView[4];
	private TextView tvTipPictureIndex;

	private CustomPicFrame customPicFrame2, customPicFrame3, customPicFrame4;

	private AlphaAnimation alphaAnimation;
	private CustomPicFrame currentCustomPicFrame;
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private Camera.Parameters parameters = null;

	private LinearLayout linearLayoutMapFrameSet; // 存储已拍摄的图片的方框的布局
	private LinearLayout linearLayoutTipCirCleSet; // 四个提示的小圆圈布局
	private LinearLayout linearLayoutTipSet; // 整个提示的布局，包含图索引和四个提示小圆圈的布局
	private RelativeLayout relativeLayoutTakephoto; // 照相图标的布局

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		findAllViewById();
		initTvArray();
		if (GlobalParams.CURRENT_CAMERA_MODE == GlobalParams.CAMERA_MODE_FREE) {
			customPicFrame2.setVisibility(View.INVISIBLE);
			customPicFrame3.setVisibility(View.INVISIBLE);
			customPicFrame4.setVisibility(View.INVISIBLE);
			tv2.setVisibility(View.INVISIBLE);
			tv3.setVisibility(View.INVISIBLE);
			tv4.setVisibility(View.INVISIBLE);
		}

		getFrameStringIndex();

		getPictureTexts();

		setSurfaceViewAttribute();

		setListener();

		setDisplayBarAlphaAnimation();
		linearLayoutTipSet.startAnimation(alphaAnimation);

		// 设置红色相框滑动初始位置
		initRedFramePosition();

		initTakePhotos();
		showTipInfo(0);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvCancel:
			finish();
			break;
		case R.id.tvSpark:
			// 打开闪光灯
			sparkCamera();
			break;
		case R.id.ivLocalPhotos:
			// 从相册中去获取
			getPictureFromLocation();
			break;
		case R.id.relativeLayoutTakephoto:
			takePhotoOperate();
			break;
		case R.id.tvNextStep:
			goToNextActivity();
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 从相册获取图片
		case GlobalParams.GET_PICTURE_FROM_XIANGCE_CODE:
			String path = BimpUtil.getPhotoPathByLocalUri(this, data);
			if (path != null) {
				Bitmap bitmap = BimpUtil.revitionImageSize(path);
				int width = Util.getDisplayWidth(CustomCameraActivity.this);
				bitmap = BimpUtil.clip(bitmap, width, width);
				bitmap = BimpUtil.autoFixOrientation(bitmap, null, path);
				BimpUtil.bmp.put(currIndex, bitmap);

				setBgfromPicture(bitmap);
				emptyIndex = getNoPicFrame();
				startAnimation(ivRadFrame, currIndex, emptyIndex);
				currentCustomPicFrame
						.setRelativeBackground(R.drawable.photograph_yet);

				tvNextStep.setVisibility(TextView.VISIBLE);

				if (currIndex == emptyIndex) {
					tvNextStep.setEnabled(true);
				}
				currIndex = emptyIndex;
				showTipInfo(currIndex);
				linearLayoutTipSet.startAnimation(alphaAnimation);
			}
			break;
		case CustomCameraActivity.SHOW_TAKE_PHOTO:
			if (data != null) {
				if (resultCode == 0) {// 删除操作
					if (GlobalParams.CURRENT_CAMERA_MODE == GlobalParams.CAMERA_MODE_FREE) {// 自由拍照模式
						setNextTVEnable(true);
					} else if (resultCode == 0) {// 非自由拍照模式
						setNextTVEnable(false);
					}
					currentCustomPicFrame.setImageVisibility(ImageView.GONE);
				}
				
				currentCustomPicFrame
				.setRelativeBackground(R.drawable.photograph_yet);
				currentCustomPicFrame.setClickable(true);

			}
			break;
		default:
			break;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(); // 打开摄像头
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		parameters = camera.getParameters(); // 获取相机参数
		if (Build.VERSION.SDK_INT >= 8) {
			setDisplayOrientation(camera, 90);
		} else {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				parameters.set("orientation", "portrait");
			} else {
				parameters.set("orientation", "landscape");
			}
			parameters.set("rotation", 90);
		}
		parameters.setPictureFormat(ImageFormat.JPEG); // 设置图片格式
		parameters
				.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
		parameters.setJpegQuality(100); // 设置照片质量
		camera.cancelAutoFocus();
		camera.setParameters(parameters);
		camera.setDisplayOrientation(getPreviewDegree(CustomCameraActivity.this));
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 设置用于显示拍照影像的SurfaceHolder对象
		camera.startPreview(); // 开始预览

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.stopPreview();
			// 释放相机资源
			camera.release();
			camera = null;
		}
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void findAllViewById() {
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

		customPicFrame2 = (CustomPicFrame) findViewById(R.id.customPicFrame2);
		customPicFrame3 = (CustomPicFrame) findViewById(R.id.customPicFrame3);
		customPicFrame4 = (CustomPicFrame) findViewById(R.id.customPicFrame4);

		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		tv4 = (TextView) findViewById(R.id.tv4);
		tvCancel = (TextView) findViewById(R.id.tvCancel);
		tvSpark = (TextView) findViewById(R.id.tvSpark);
		tvTipPictureIndex = (TextView) findViewById(R.id.tvTipPictureIndex);
		tvNextStep = (TextView) findViewById(R.id.tvNextStep);

		ivCamereCenterPhoto = (ImageView) findViewById(R.id.ivCamereCenterPhoto);
		ivLocalPhotos = (ImageView) findViewById(R.id.ivLocalPhotos);
		ivRadFrame = (ImageView) findViewById(R.id.ivRadFrame);

		relativeLayoutTakephoto = (RelativeLayout) findViewById(R.id.relativeLayoutTakephoto);
		linearLayoutTipSet = (LinearLayout) findViewById(R.id.linearLayoutTipSet);
		linearLayoutTipCirCleSet = (LinearLayout) findViewById(R.id.linearLayoutTipCirCleSet);
		linearLayoutMapFrameSet = (LinearLayout) findViewById(R.id.linearLayoutMapSet);
	}

	private void initTvArray() {
		tvArray[0] = tv1;
		tvArray[1] = tv2;
		tvArray[2] = tv3;
		tvArray[3] = tv4;
	}

	@SuppressLint("Recycle")
	private void getFrameStringIndex() {
		TypedArray currentModesArray = null;
		switch (GlobalParams.CURRENT_CAMERA_MODE) {
		case GlobalParams.CAMERA_MODE_CHAIRS: // 家具
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.chairs_str);
			break;
		case GlobalParams.CAMERA_MODE_PICTURES: // 书画
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.pictures_str);
			break;
		case GlobalParams.CAMERA_MODE_CHINAS: // 陶瓷
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.chinas_str);
			break;
		case GlobalParams.CAMERA_MODE_JADES: // 玉石
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.jades_str);
			break;
		case GlobalParams.CAMERA_MODE_MONEYS: // 钱币
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.moneys_str);
			break;
		case GlobalParams.CAMERA_MODE_GOLDS:// 金铜
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.golds_str);
			break;
		case GlobalParams.CAMERA_MODE_BUDDHAS:// 佛像
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.buddhas_str);
			break;
		case GlobalParams.CAMERA_MODE_ZHUCHUANS:// 珠串
			currentModesArray = CustomCameraActivity.this.getResources()
					.obtainTypedArray(R.array.zhuchuans_str);
			break;
		default:
			break;
		}
		if (currentModesArray != null) {
			tv1.setText(currentModesArray.getString(0));
			tv2.setText(currentModesArray.getString(1));
			tv3.setText(currentModesArray.getString(2));
			tv4.setText(currentModesArray.getString(3));
		}
	}

	private class TakePictureAnimationListener implements AnimationListener {
		public void onAnimationEnd(Animation animation) {
			relativeLayoutTakephoto.setEnabled(true);
		}

		public void onAnimationRepeat(Animation animation) {

		}

		public void onAnimationStart(Animation animation) {
			// 自定义拍照
			camera.takePicture(null, null, new TakePhotoCallback());
			relativeLayoutTakephoto.setEnabled(false);
		}

	}

	private void takePhotoOperate() {
		// 以下代码是拍照按钮的动画，拍照功能在动画监听器的回调方法中
		ScaleAnimation animation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(1000);
		animation.setAnimationListener(new TakePictureAnimationListener());
		ivCamereCenterPhoto.startAnimation(animation);
	}

	private void goToNextActivity() {
		Intent publishPhotoIntent = new Intent(this, PublishPhotoActivity.class);
		startActivity(publishPhotoIntent);
	}

	private void getPictureFromLocation() {
		try {
			Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
			pickIntent.setType("image/*");
			startActivityForResult(pickIntent,
					GlobalParams.GET_PICTURE_FROM_XIANGCE_CODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initTakePhotos() {
		for (int i = 0; i < linearLayoutMapFrameSet.getChildCount(); i++) {
			if (i == 0) {
				currentCustomPicFrame = (CustomPicFrame) linearLayoutMapFrameSet
						.getChildAt(0);
			}
			initMapKey(i);
			// 添加相框事件监听
			linearLayoutMapFrameSet.getChildAt(i).setOnClickListener(
					new MyOnClickListener(i));
		}
	}

	private void initMapKey(int i) {
		BimpUtil.bmp.put(i, null);
	}

	private void setDisplayBarAlphaAnimation() {
		// 指示条展示动画，显示1秒后消失
		alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(1000);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				linearLayoutTipSet.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {
				linearLayoutTipSet.setVisibility(View.VISIBLE);
			}

		});
	}

	private void setListener() {
		tvCancel.setOnClickListener(this);
		tvSpark.setOnClickListener(this);
		ivLocalPhotos.setOnClickListener(this);
		relativeLayoutTakephoto.setOnClickListener(this);
		tvNextStep.setOnClickListener(this);
	}

	private void getPictureTexts() {
		tvString[0] = tv1.getText().toString();
		tvString[1] = tv2.getText().toString();
		tvString[2] = tv3.getText().toString();
		tvString[3] = tv4.getText().toString();
	}

	private void setSurfaceViewAttribute() {
		surfaceHolder = surfaceView.getHolder();
		// .setFixedSize(176, 144); // 设置Surface分辨率
		surfaceHolder.setKeepScreenOn(true);// 屏幕常亮
		surfaceHolder.addCallback(this);// 为SurfaceView的句柄添加一个回调函数
		surfaceView.setFocusable(true);// 自动对焦
	}

	/**
	 * 展示圈和文字指示条
	 */
	private void showTipInfo(int currIndex) {
		tvTipPictureIndex.setText(tvString[currIndex]);
		for (int i = 0; i < linearLayoutTipCirCleSet.getChildCount(); i++) {

			ivTipCircle = (ImageView) linearLayoutTipCirCleSet
					.getChildAt(currIndex);
			if (currIndex != i) {
				ivTipCircle = (ImageView) linearLayoutTipCirCleSet
						.getChildAt(i);
			}
			ivTipCircle.setImageResource(R.drawable.camera_page_others);

		}
	}

	/**
	 * 相框事件监听
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			if (BimpUtil.bmp.get(currIndex) != null && currIndex == index) {
				clickPosHasPicture();
			} else {
				clickPosNoPicture();
			}

			linearLayoutTipSet.startAnimation(alphaAnimation);
		}

		private void clickPosHasPicture() {
			Bundle bundle = new Bundle();
			bundle.putInt("position", currIndex);
			Intent intent = new Intent(CustomCameraActivity.this,
					ShowTakePhotoActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, CustomCameraActivity.SHOW_TAKE_PHOTO);
			currentCustomPicFrame.setClickable(false);
		}

		private void clickPosNoPicture() {
			// 开始动画
			startAnimation(ivRadFrame, currIndex, index);
			// 动画完成之后当前位置
			currIndex = index;
			showTipInfo(currIndex);
		}
	}

	/**
	 * 设置红色相框滑动初始位置
	 */
	private void initRedFramePosition() {
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.photograph_frame).getWidth();// 获取图片宽度
		int screenW = Util.getDisplayWidth(this);
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();// 矩阵原理
		matrix.postTranslate(offset, 0);
		ivRadFrame.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 相框移动动画
	 * 
	 * @param imageFrame
	 * @param currIndex
	 *            动画前位置
	 * @param index
	 *            动画后位置
	 */
	private void startAnimation(ImageView imageFrame, int currIndex, int index) {
		int one = offset * 2 + bmpW;
		currentCustomPicFrame = (CustomPicFrame) linearLayoutMapFrameSet
				.getChildAt(index);
		Animation animation = new TranslateAnimation(one * currIndex, one
				* index, 0, 0);
		currIndex = index;
		animation.setFillAfter(true);
		animation.setDuration(300);
		imageFrame.startAnimation(animation);
	}

	private final class TakePhotoCallback implements PictureCallback {
		private Bitmap bitmap;

		// 拍完照片后的回调函数
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Configuration config = getResources().getConfiguration();
				Matrix matrix = new Matrix();
				matrix.reset();
				if (config.orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖拍
					matrix.postRotate(90);
				} else {
					matrix.postRotate(180);
				}
				bitmap = BimpUtil.compressByteByScreen(
						CustomCameraActivity.this, data);// 处理成宽高自适应屏幕，并裁减成正方形。
				// bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
				BimpUtil.bmp.put(currIndex, bitmap);
				BimpUtil.bm.put(currIndex,
						CollectionBitmapUtils.Bitmap2Bytes(bitmap));

				setBgfromPicture(bitmap);

				emptyIndex = getNoPicFrame();

				updateFrameAndNextStep();

				currentCustomPicFrame.setVisibility(View.VISIBLE);
				currentCustomPicFrame
						.setRelativeBackground(R.drawable.photograph_freedom_add);

				startAnimation(ivRadFrame, currIndex, emptyIndex);
				currIndex = emptyIndex;
				showTipInfo(emptyIndex);
				linearLayoutTipSet.startAnimation(alphaAnimation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			camera.startPreview(); // 拍完照后，重新开始预览
		}

		private void updateFrameAndNextStep() {

			if (GlobalParams.CURRENT_CAMERA_MODE == GlobalParams.CAMERA_MODE_FREE) {
				if (currIndex != 3 && !isPlusImageShowed[currIndex + 1]) {
					currentCustomPicFrame = (CustomPicFrame) linearLayoutMapFrameSet
							.getChildAt(currIndex + 1);
					isPlusImageShowed[currIndex + 1] = true;
					tvNextStep.setEnabled(true);
					tvArray[currIndex + 1].setVisibility(View.VISIBLE);
				}
				setNextTVEnable(true);
			} else {
				boolean isAllPositionHaveImage = true;
				for (int i = 0; i < BimpUtil.bmp.size(); i++) {
					isAllPositionHaveImage &= (BimpUtil.bmp.get(i) != null);
				}
				if (isAllPositionHaveImage) {
					setNextTVEnable(true);
				} else {
					setNextTVEnable(false);
				}
			}
		}
	}

	private void setNextTVEnable(boolean enable) {
		if (BimpUtil.bmp.get(0) != null) {
			tvNextStep.setEnabled(enable);
		} else {
			tvNextStep.setEnabled(false);
		}
	}

	private void setBgfromPicture(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(getResources(), bitmap);
		currentCustomPicFrame.setImageBackgroundResource(drawable);
		currentCustomPicFrame.setImageVisibility(ImageView.VISIBLE);
	}

	private int getNoPicFrame() {
		int tempIndex = 0;
		// 自动跳转到下一个没有数据的相框
		for (int i = 0; i < 4; i++) {
			if (BimpUtil.bmp.get(i) == null) {
				tempIndex = i;
				break;
			} else {
				tempIndex = currIndex;
			}
		}
		return tempIndex;
	}

	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	public static int getPreviewDegree(Activity activity) {
		// 获得手机的方向
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		default:
			break;
		}
		return degree;
	}

	/**
	 * 打开反光灯
	 */
	private void sparkCamera() {
		switch (mCuttentFlash) {
		case FLASH_AUTO:
			cameraParameters(Parameters.FLASH_MODE_ON, "打开");
			mCuttentFlash = CustomCameraActivity.FLASH_OPEN;
			break;
		case FLASH_OPEN:
			cameraParameters(Parameters.FLASH_MODE_OFF, "关闭");
			mCuttentFlash = CustomCameraActivity.FLASH_CLOSE;
			break;
		case FLASH_CLOSE:
			cameraParameters(Parameters.FLASH_MODE_AUTO, "自动");
			mCuttentFlash = CustomCameraActivity.FLASH_AUTO;
			break;
		default:
			break;
		}
		camera.startPreview();
	}

	private void cameraParameters(String mode, String text) {
		Parameters parameters = camera.getParameters();
		parameters.setFlashMode(mode);
		camera.setParameters(parameters);
		tvSpark.setText(text);
	}

}
