package com.edaoyou.collections.engine;

import android.content.Context;

import com.edaoyou.collections.utils.Util;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;

public class XUtilsManager {
	private static XUtilsManager mXutilsManager;
	private BitmapUtils mBitmapUtils;
	private HttpUtils mHttpUtils;

	private XUtilsManager(Context context) {
		mHttpUtils = new HttpUtils();
		mBitmapUtils = BitmapUtils.create(context);
		mBitmapUtils.configDiskCachePath(Util.getCachePath(context));
	}

	public synchronized static XUtilsManager getInstance(Context context) {
		if (mXutilsManager == null) {
			mXutilsManager = new XUtilsManager(context);
		}
		return mXutilsManager;
	}

	public BitmapUtils getBitmapUtils() {
		return mBitmapUtils;
	}

	public HttpUtils getHttpUtils() {
		return mHttpUtils;
	}
}