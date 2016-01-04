package com.edaoyou.collections.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.edaoyou.collections.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

/**
 * 拍照模块 拍完照后图片的工具类
 * 
 */
public class BimpUtil {
	public static int max = 0;
	public static boolean act_bool = true;
	// 存放拍照或从相册选择的图片，图片最多为4张。
	public static LinkedHashMap<Integer, Bitmap> bmp = new LinkedHashMap<Integer, Bitmap>();
	// 存放拍照或从相册选择的图片，图片最多为4张。
	public static LinkedHashMap<Integer, byte[]> bm = new LinkedHashMap<Integer, byte[]>();

	public static Bitmap revitionImageSize(String path) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			int i = 0;
			Bitmap bitmap = null;
			while (true) {
				if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
					in = new BufferedInputStream(new FileInputStream(new File(path)));
					options.inSampleSize = (int) Math.pow(2.0D, i);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, options);
					break;
				}
				i += 1;
			}
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 图片按比例大小压缩方法（根据屏幕宽高得到Bitmap的缩略图） 将图片从本地读到内存时,进行压缩
	 * ,即图片从File形式变为Bitmap形式。特点: 通过设置采样率, 减少图片的像素, 达到对内存中的Bitmap进行压缩
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressByteByScreen(Context context, byte[] image) {
		ByteArrayInputStream bais = new ByteArrayInputStream(image);
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;// 图片的宽高
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float hh = (float) metrics.heightPixels;
		float ww = (float) metrics.widthPixels;// 屏幕宽度
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bais = new ByteArrayInputStream(image);
		bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int clipSize = width <= height ? width : height;
		Matrix matrix = new Matrix();
		matrix.postScale((float) ww / clipSize, (float) ww / clipSize); // 长和宽放大缩小的比例
		Bitmap clipedBitmap = Bitmap.createBitmap(bitmap, 0, 0, clipSize, clipSize, matrix, true);
		return clipedBitmap;
	}

	/**
	 * 图片按比例大小压缩方法（根据屏幕宽高得到Bitmap的缩略图） 将图片从本地读到内存时,进行压缩
	 * ,即图片从File形式变为Bitmap形式。特点: 通过设置采样率, 减少图片的像素, 达到对内存中的Bitmap进行压缩
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressByteByTarget(Context context, byte[] image, int targetWidth, int targetHeight) {
		ByteArrayInputStream bais = new ByteArrayInputStream(image);
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;// 图片的宽高
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > targetWidth) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / targetWidth);
		} else if (w < h && h > targetHeight) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / targetHeight);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bais = new ByteArrayInputStream(image);
		bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int clipSize = width <= height ? width : height;
		Matrix matrix = new Matrix();
		matrix.postScale((float) targetWidth / clipSize, (float) targetWidth / clipSize); // 长和宽放大缩小的比例
		Bitmap clipedBitmap = Bitmap.createBitmap(bitmap, 0, 0, clipSize, clipSize, matrix, true);
		return clipedBitmap;
	}

	/**
	 * 自动旋转图片角度
	 * 
	 * @param bm
	 * @param iv
	 * @param uri
	 * @param path
	 * @return
	 */
	public static Bitmap autoFixOrientation(Bitmap bm, Uri uri, String path) {
		int deg = 0;
		try {
			ExifInterface exif = null;
			if (uri == null) {
				exif = new ExifInterface(path);
			} else if (path == null) {
				exif = new ExifInterface(uri.getPath());
			}

			if (exif == null) {
				return bm;
			}

			String rotate = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			int rotateValue = Integer.parseInt(rotate);
			switch (rotateValue) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				deg = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				deg = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				deg = 270;
				break;
			default:
				deg = 0;
				break;
			}
		} catch (Exception e) {

			return bm;
		}
		Matrix m = new Matrix();
		m.preRotate(deg);
		bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
		return bm;
	}

	/**
	 * 按图片长度较短的一边截取图片成正方形，并缩放到指定宽高
	 * 
	 * @param bitmap
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	public static Bitmap clip(Bitmap bitmap, int targetWidth, int targetHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int clipSize = width <= height ? width : height;
		Matrix matrix = new Matrix();
		matrix.postScale((float) targetWidth / clipSize, (float) targetHeight / clipSize); // 长和宽放大缩小的比例
		Bitmap clipedBitmap = Bitmap.createBitmap(bitmap, 0, 0, clipSize, clipSize, matrix, true);
		return clipedBitmap;
	}

	/**
	 * 保存压缩后的图片，存储到SD卡
	 * 
	 * @param data
	 * @throws IOException
	 */
	public static File saveCompressedBitmap(Context context, Bitmap bitmap) throws IOException {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()); // 格式化时间
		String filename = format.format(date) + ".jpg";
		String saveDir = Util.getCachePath(context);// 路径
		File jpgFile = new File(saveDir, filename);
		FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
		bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);// 将压缩Bitmap写入到指定路径，等待上传到服务器
		outputStream.close(); // 关闭输出流
		return jpgFile;
	}

	/**
	 * 获取从本地图库返回来的时候的URI解析出来的文件路径
	 * 
	 * @return
	 */
	public static String getPhotoPathByLocalUri(Context context, Intent data) {
		if (data == null) {
			return null;
		}
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return picturePath;
	}

	/**
	 * 对Bitmap压缩返回byte[]
	 */
	public static byte[] bmpToByteArray(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);// 百分之二十压缩一下
		int options = 20;
		while (baos.toByteArray().length / 1024 > 32) { // 循环判断如果压缩后图片是否大于32kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 2;// 每次都减少2
			if (options <= 0) {
				bmp.compress(Bitmap.CompressFormat.JPEG, 2, baos);
				break;
			}
		}
		return baos.toByteArray();
	}

	/**
	 * 对Bitmap压缩
	 */
	public static Bitmap compressImage(Bitmap bmp) {
		byte[] bmpToByteArray = bmpToByteArray(bmp);
		ByteArrayInputStream isBm = new ByteArrayInputStream(bmpToByteArray);
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	/**
	 * Stores an image on the storage
	 * 
	 * @param image
	 *            the image to store.
	 * @param pictureFile
	 *            the file in which it must be stored
	 */
	public static void storeImage(Bitmap image, File pictureFile) {
		if (pictureFile == null) {
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Get the screen height.
	 * 
	 * @param context
	 * @return the screen height
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenHeight(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.y;
		}
		return display.getHeight();
	}

	/**
	 * Get the screen width.
	 * 
	 * @param context
	 * @return the screen width
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}

	public static String getLauncherPath(Context context) {
		File f = new File(Util.getCachePath(context) + "/ic_launcher.png");
		if (f != null && f.exists()) {
			return Util.getCachePath(context) + "/ic_launcher.png";
		}
		Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		try {
			FileOutputStream fos = new FileOutputStream(Util.getCachePath(context) + "/ic_launcher.png");
			thumb.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return Util.getCachePath(context) + "/ic_launcher.png";
	}

	@SuppressLint("NewApi")
	public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {

		if (VERSION.SDK_INT > 16) {
			Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

			final RenderScript rs = RenderScript.create(context);
			final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
			final Allocation output = Allocation.createTyped(rs, input.getType());
			final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			script.setRadius(radius /* e.g. 3.f */);
			script.setInput(input);
			script.forEach(output);
			output.copyTo(bitmap);
			return bitmap;
		}

		// Stack Blur v1.0 from
		// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
		//
		// Java Author: Mario Klingemann <mario at quasimondo.com>
		// http://incubator.quasimondo.com
		// created Feburary 29, 2004
		// Android port : Yahel Bouaziz <yahel at kayenko.com>
		// http://www.kayenko.com
		// ported april 5th, 2012

		// This is a compromise between Gaussian Blur and Box blur
		// It creates much better looking blurs than Box Blur, but is
		// 7x faster than my Gaussian Blur implementation.
		//
		// I called it Stack Blur because this describes best how this
		// filter works internally: it creates a kind of moving stack
		// of colors whilst scanning through the image. Thereby it
		// just has to add one new block of color to the right side
		// of the stack and remove the leftmost color. The remaining
		// colors on the topmost layer of the stack are either added on
		// or reduced by one, depending on if they are on the right or
		// on the left side of the stack.
		//
		// If you are using this algorithm in your code please add
		// the following line:
		//
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		Log.e("pix", w + " " + h + " " + pix.length);
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}
}
