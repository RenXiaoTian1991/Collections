package com.edaoyou.collections.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

/**
 * @author andong 网络缓存工具类
 */
public class NetCacheUtils {

	public static final int SUCCESS_BACKGROUND = 0;
	public static final int FAILED_BACKGROUND = 3;

	private Handler mHandler;
	private ExecutorService mExecutorService;
	private LocalCacheUtils localCacheUtils; // 本地缓存对象

	public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils) {
		this.mHandler = handler;
		this.localCacheUtils = localCacheUtils;

		// 创建一个线程池
		mExecutorService = Executors.newFixedThreadPool(10);
	}

	/**
	 * 使用子线程去请求网络, 把图片抓取回来, 再发送给主线程
	 * 
	 * @param imageUrl
	 */
	public void getBitmapFromNet(String imageUrl) {
		// new Thread(new InternalRunnable(imageUrl, position)).start();

		// 让线程池执行一个任务.
		mExecutorService.execute(new InternalRunnable(imageUrl));
	}

	class InternalRunnable implements Runnable {

		private String imageUrl;

		public InternalRunnable(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(imageUrl).openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.connect();

				int responseCode = conn.getResponseCode();
				if (responseCode == 200) {
					InputStream is = conn.getInputStream();
					Bitmap bm = BitmapFactory.decodeStream(is);

					// 向本地存一份
					localCacheUtils.putBitmap2Local(imageUrl, bm);

					Message msg = Message.obtain();
					msg.what = SUCCESS_BACKGROUND;
					mHandler.sendMessage(msg);

				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = Message.obtain();
				msg.what = FAILED_BACKGROUND;
				mHandler.sendMessage(msg);
			} finally {
				if (conn != null) {
					conn.disconnect(); // 断开连接
				}
			}
		}
	}
}
