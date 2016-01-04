package com.edaoyou.collections.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {

	private static SharedPreferences mSharedPreferences;
	private static SharedPreferencesUtils mSharedPreferencesUtils;

	private SharedPreferencesUtils(Context context) {
		mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
	}

	public synchronized static SharedPreferencesUtils getInstance(Context context) {
		if (mSharedPreferencesUtils == null) {
			mSharedPreferencesUtils = new SharedPreferencesUtils(context);
		}
		return mSharedPreferencesUtils;
	}

	public String getString(String key) {
		return mSharedPreferences.getString(key, null);
	}

	public void saveString(String key, String value) {
		Editor editor = mSharedPreferences.edit();
		editor.putString(key, value).commit();
	}

	public void saveBoolean(String key, boolean value) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(key, value).commit();
	}

	public boolean getBoolean(String key) {
		return mSharedPreferences.getBoolean(key, false);
	}

	public long getLong(String key) {
		return mSharedPreferences.getLong(key, 0);
	}

	public void saveLong(String key, Long value) {
		Editor editor = mSharedPreferences.edit();
		editor.putLong(key, value).commit();
	}

	public int getInt(String key) {
		return mSharedPreferences.getInt(key, 0);
	}

	public void saveInt(String key, int value) {
		Editor editor = mSharedPreferences.edit();
		editor.putInt(key, value).commit();
	}

	public int getInt_1(String key) {
		return mSharedPreferences.getInt(key, -1);
	}

	public void saveLoginAfterReturnResponse(Context ct, String key, String value) {
		Editor editor = mSharedPreferences.edit();
		editor.putString(key, value).commit();
	}

	public String getLoginAfterReturnResponse(Context ct, String key) {
		return mSharedPreferences.getString(key, null);
	}

	public void remove(String key) {
		Editor editor = mSharedPreferences.edit();
		editor.remove(key).commit();
	}

	public boolean getBooleanTrue(String key) {
		return mSharedPreferences.getBoolean(key, true);
	}
}
