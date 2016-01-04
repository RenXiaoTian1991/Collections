package com.edaoyou.collections.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.WindowManager;

import com.edaoyou.collections.GlobalParams;

/**
 * @Description: 查询图片，并将其解析为Bitmap的工具类
 * @date 2014-9-19 上午10:19:53 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public final class CollectionBitmapUtils {
	/** 图片处理：裁剪. */
	public static final int CUTIMG = 0;

	/** 图片处理：缩放. */
	public static final int SCALEIMG = 1;

	/** 图片处理：不处理. */
	public static final int ORIGINALIMG = 2;

	public static final String IMAGE_UNSPECIFIED = "image/*";

	private static int mWidth;

	private static int mHeight;

	/**
	 * 个人头像裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public static void startPhotoZoom(Activity activity, Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, GlobalParams.GET_CUT_PICTURE_CODE);
	}

	/**
	 * 通过照相 获取裁剪背景图片的方法
	 */
	public static void cropImageUri(Activity activity, Uri uri) {
		getServiceParams(activity);

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 5);
		intent.putExtra("aspectY", 4);
		intent.putExtra("outputX", mWidth);
		intent.putExtra("outputY", mHeight);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		activity.startActivityForResult(intent, GlobalParams.GET_CUT_PICTURE_CODE);
	}

	/**
	 * 通过本地图片 获取裁剪背景图片的方法
	 */
	public static void cropImageLocal(Activity activity, Uri uri) {
		getServiceParams(activity);

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 5);
		intent.putExtra("aspectY", 4);
		intent.putExtra("outputX", mWidth);
		intent.putExtra("outputY", mHeight);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection
		activity.startActivityForResult(intent, GlobalParams.GET_CUT_PICTURE_CODE);
	}

	private static void getServiceParams(Activity activity) {
		WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		mWidth = wm.getDefaultDisplay().getWidth();
		mHeight = wm.getDefaultDisplay().getHeight() / 2;
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	public static Bitmap setPicToView(Intent picdata) {
		if (picdata != null) {
			Bitmap photo = picdata.getParcelableExtra("data");
			Bitmap bitmap = CollectionBitmapUtils.getRoundedCornerBitmap(photo);
			return bitmap;
		}
		return null;
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 */
	public static Bitmap setBackPicToView(Intent picdata) {
		if (picdata != null) {
			Bitmap photo = picdata.getParcelableExtra("data");
			return photo;
		}
		return null;
	}

	/**
	 * bitmap---->byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * Bitmap对象转换Drawable对象.
	 * 
	 * @param bitmap
	 *            要转化的Bitmap对象
	 * @return Drawable 转化完成的Drawable对象
	 */
	public static Drawable bitmapToDrawable(Activity activity, Bitmap bitmap) {
		BitmapDrawable mBitmapDrawable = null;
		try {
			if (bitmap == null) {
				return null;
			}
			mBitmapDrawable = new BitmapDrawable(activity.getResources(), bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmapDrawable;
	}

	/**
	 * 切圆形
	 * 
	 * @param bitmap
	 *            已经切好的方形头像
	 * @return 去掉矩形边角的圆形bitmap
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() / 2 : bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 将bitmap变为byte[] 数组
	 * 
	 * @param
	 * @return
	 */
	public static byte[] byteToBitmap(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
}