package com.edaoyou.collections.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class AsyncImageLoader {

	public static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();// sd卡的根目录
	// 为了加快速度，在内存中开启缓存（主要应用于重复图片较多时，或者同一个图片要多次被访问，比如在ListView时来回滚动）
	public Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
	private final Handler handler = new Handler();

	/**
	 * @param imageUrl图像url地址
	 * @param callback回调接口
	 * @return 返回内存中缓存的图像，第一次加载返回null
	 */
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback, final String specPath) {
		// 如果缓存过就从缓存中取出数据
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {
				return softReference.get();
			}
		} else if (useTheImage(imageUrl, specPath) != null) {// 缓存中没有图像则使用SD卡上的图像
			return useTheImage(imageUrl, specPath);
		}
		// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
		executorService.submit(new Runnable() {
			public void run() {
				try {
					final Drawable drawable = getDrawableForImgUrl(imageUrl);

					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
					handler.post(new Runnable() {
						public void run() {
							callback.imageLoaded(drawable);
						}
					});
					saveFile(drawable, imageUrl, specPath);// 保存文件
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		return null;
	}

	public Drawable getDrawableForImgUrl(String img) {
		Drawable drawable = null;
		try {
			drawable = Drawable.createFromStream(new URL(img).openStream(), "image.jpg");
			if (drawable == null) {// 没有对应的图片
				// drawable = Drawable.createFromPath(Image.DefaultImg);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}

	// 从网络上取数据方法
	public Drawable loadImageFromUrl(String imageUrl) {
		try {
			return Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 对外界开放的回调接口
	public interface ImageCallback {
		// 注意 此方法是用来设置目标对象的图像资源
		public void imageLoaded(Drawable imageDrawable);
	}

	// 引入线程池，并引入内存缓存功能,并对外部调用封装了接口，简化调用过程
	public void LoadImage(final String url, final ImageView iv, String specPath) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		Drawable cacheImage = loadDrawable(url, new AsyncImageLoader.ImageCallback() {
			// 如果第一次加载url时下面方法会执行
			public void imageLoaded(Drawable imageDrawable) {
				iv.setImageDrawable(imageDrawable);
			}
		}, specPath);

		if (cacheImage != null) {
			iv.setImageDrawable(cacheImage);
		} else {
			// iv.setBackgroundResource(R.drawable.empty_photo);
		}
	}

	/**
	 * 保存图片到SD卡上
	 * 
	 * @param bm
	 * @param fileName
	 * 
	 */
	public void saveFile(Drawable dw, String url, String specPath) {
		try {
			if (dw == null) {
				return;
			}
			BitmapDrawable bd = (BitmapDrawable) dw;
			Bitmap bm = bd.getBitmap();
			// 获得文件名字
			final String fileNa = url.substring(url.lastIndexOf("/") + 1, url.length());
			File file = new File(mSdRootPath + specPath + "/" + fileNa);
			// 创建图片缓存文件夹
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

			if (sdCardExist) {
				File parentFile = new File(mSdRootPath + specPath + "/");
				// 如果文件夹不存在
				if (!parentFile.exists()) {
					// 按照指定的路径创建文件夹
					parentFile.mkdirs();
					// 如果文件夹不存在
				}
				// 检查图片是否存在
				if (!file.exists()) {
					file.createNewFile();
				}
			}
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bm.compress(Bitmap.CompressFormat.JPEG, 15, bos);// 压缩率85%图片
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Log.e("========", e.getMessage());
		}
	}

	/**
	 * 使用SD卡上的图片
	 */
	public Drawable useTheImage(String imageUrl, String specPath) {
		Bitmap bmpDefaultPic = null;
		// 获得图片文件路径
		String imageSDCardPath = mSdRootPath + specPath + "/" + Util.md5(imageUrl) + ".jpg";
		File file = new File(imageSDCardPath);
		// 检查图片是否存在
		if (!file.exists()) {
			return null;
		}
		bmpDefaultPic = BitmapFactory.decodeFile(imageSDCardPath, null);
		if (bmpDefaultPic != null || bmpDefaultPic.toString().length() > 3) {
			Drawable drawable = new BitmapDrawable(bmpDefaultPic);
			return drawable;
		} else {
			return null;
		}
	}
}