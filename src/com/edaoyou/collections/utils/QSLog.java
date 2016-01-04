package com.edaoyou.collections.utils;

import android.util.Log;

public class QSLog {

	private static final String TAG = "Collections";
	private static final boolean ISON = true;

	public static void e(String tag, String msg) {
		if (ISON) {
			Log.e(TAG, tag + " -- " + msg);
		}
	}

	public static void d(String tag, String msg) {
		if (ISON) {
			Log.d(TAG, tag + " -- " + msg);
		}
	}

	public static void i(String tag, String msg) {
		if (ISON) {
			Log.i(TAG, tag + " -- " + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (ISON) {
			Log.w(TAG, tag + " -- " + msg);
		}
	}

	public static void v(String tag, String msg) {
		if (ISON) {
			Log.v(TAG, tag + " -- " + msg);
		}
	}

	public static void e(String msg) {
		if (ISON) {
			Log.e(TAG, msg);
		}
	}

	public static void d(String msg) {
		if (ISON) {
			Log.d(TAG, msg);
		}
	}

	public void i(String msg) {
		if (ISON) {
			Log.i(TAG, msg);
		}
	}

	public void w(String msg) {
		if (ISON) {
			Log.w(TAG, msg);
		}
	}

	public void v(String msg) {
		if (ISON) {
			Log.v(TAG, msg);
		}
	}
}
