package com.edaoyou.collections.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.edaoyou.collections.GlobalParams;
import com.google.gson.Gson;

public class GsonUtils {
	private static boolean isJson(String json) {
		if (TextUtils.isEmpty(json)) {
			return false;
		}
		try {
			new JSONObject(json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static <T> T json2bean(String result, Class<T> clazz) {
		if (isJson(result)) {
			Gson gson = new Gson();
			T t = gson.fromJson(result, clazz);
			return t;
		}
		return null;
	}

	public static JSONObject getJSONObjectForUSer(Context context, JSONObject request) {
		JSONObject json = new JSONObject();
		String uId = UserUtil.getUserUid(context);
		try {
			if (TextUtils.isEmpty(uId)) {
				json.put("uid", "0");
			} else {
				json.put("uid", uId);
			}
			json.put("sid", UserUtil.getUserSid(context));
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
}
