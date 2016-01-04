package com.edaoyou.collections.utils;

import android.content.Context;
import android.text.TextUtils;
import com.edaoyou.collections.GlobalParams;

public class UserUtil {

	/**
	 * 判断用户是否曾经登录,并且返回用户登录的类型
	 */
	public static int checkUserIsLogin(Context context) {
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(context);
		String isWeiXinLogin = sharedPreferencesUtils.getString(GlobalParams.WEI_XIN_CODE_KEY);
		if (!TextUtils.isEmpty(isWeiXinLogin)) {
			return GlobalParams.LOGIN_TYPE_WEIXIN;
		}
		String isQQLogin = sharedPreferencesUtils.getString(GlobalParams.QQ_ACCESS_TOKEN_KEY);
		if (!TextUtils.isEmpty(isQQLogin)) {
			return GlobalParams.LOGIN_TYPE_QQ;
		}
		String isCollectionsLogin = sharedPreferencesUtils.getString(GlobalParams.USER_UID);
		if (!TextUtils.isEmpty(isCollectionsLogin)) {
			return GlobalParams.LOGIN_TYPE_COLLECTIONS;
		}
		boolean isWeiBoLogin = sharedPreferencesUtils.getBoolean(GlobalParams.WEI_BO_LOGIN);
		if (isWeiBoLogin) {
			return GlobalParams.LOGIN_TYPE_WEIBO;
		}
		return GlobalParams.LOGIN_TYPE_NULL;
	}

	/**
	 * 得到用户登录后的uid
	 */
	public static String getUserUid(Context context) {
		return SharedPreferencesUtils.getInstance(context).getString(GlobalParams.USER_UID);
	}

	/**
	 * 得到用户登录后的sid
	 */
	public static String getUserSid(Context context) {
		return SharedPreferencesUtils.getInstance(context).getString(GlobalParams.USER_SID);
	}

	/**
	 * 退出登录,清空数据
	 */
	public static void logout(Context context) {
		SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(context);
		// 清除藏家用户信息
		sharedPreferencesUtils.saveString(GlobalParams.USER, null);
		sharedPreferencesUtils.saveString(GlobalParams.USER_UID, null);
		sharedPreferencesUtils.saveString(GlobalParams.USER_SID, null);

		// 清除微信用户信息
		sharedPreferencesUtils.saveString(GlobalParams.WEI_XIN_CODE_KEY, null);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_XIN_ACCESS_TOKEN_KEY, null);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_XIN_REFRESH_TOKEN_KEY, null);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_XIN_OPENID_KEY, null);

		// 清除微博用户信息
		sharedPreferencesUtils.saveBoolean(GlobalParams.WEI_BO_LOGIN, false);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_BO_TOKEN_KEY, null);
		sharedPreferencesUtils.saveString(GlobalParams.WEI_BO_UID_KEY, null);
		sharedPreferencesUtils.saveLong(GlobalParams.WEI_BO_EXPIRES_TIME_KEY, 0l);

		// 清除QQ用户信息
		sharedPreferencesUtils.saveString(GlobalParams.QQ_OPENID_KEY, null);
		sharedPreferencesUtils.saveString(GlobalParams.QQ_ACCESS_TOKEN_KEY, null);
		sharedPreferencesUtils.saveLong(GlobalParams.QQ_EXPIRES_TIME_KEY, 0l);
	}

}
