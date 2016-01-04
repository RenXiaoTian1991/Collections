package com.edaoyou.collections.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * 本地缓存工具类
 */
public class LocalCacheUtils {
	private String CACHE_DIR;

	public LocalCacheUtils(Context context) {
		CACHE_DIR = Util.getCachePath(context);
	}

	/**
	 * 根据Url存储当前图片
	 * 
	 * @param imageUrl
	 * @param bm
	 */
	public void putBitmap2Local(String imageUrl, Bitmap bm) {
		try {
			String fileName = MD5Encoder.encode(imageUrl) + ".png";
			File file = new File(CACHE_DIR, fileName); // /mnt/sdcard/collections/aslkjdlhk124312423lkj
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(file);
			// 把图片通过流写到本地
			bm.compress(CompressFormat.PNG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从本地中根据url取出图片
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getBitmapFromLocal(String imageUrl) {
		try {
			String fileName = MD5Encoder.encode(imageUrl) + ".png";
			File file = new File(CACHE_DIR, fileName); // /mnt/sdcard/collections/aslkjdlhk124312423lkj
			if (file.exists()) {
				// 文件存在, 有缓存, 读取出来, 并返回

				FileInputStream is = new FileInputStream(file);
				Bitmap bm = BitmapFactory.decodeStream(is);
				return bm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
