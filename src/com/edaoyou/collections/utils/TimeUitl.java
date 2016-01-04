package com.edaoyou.collections.utils;

import java.util.Calendar;

import com.edaoyou.collections.R;

import android.content.Context;

public class TimeUitl {
	private static final long sMINMS = 1000 * 60; // 一分钟等于多少毫秒
	private static final long sHourms = 1000 * 60 * 60; // 一小时等于多少毫秒
	private static final long sDayms = 1000 * 60 * 60 * 24; // 一天等于多少毫秒

	/**
	 * 换算与当前的时间差
	 * 
	 * @param context
	 * @param 原始时间
	 * @return 天数或者小时或者分钟
	 */
	public static String getDiffTime(Context context, String timeStr) {
		long loc_time = Long.valueOf(timeStr) * 1000L;
		Calendar calendar = Calendar.getInstance();// 初始化日历控件
		long currentTime = calendar.getTimeInMillis();// 得到当前时区的时间戳
		long time = currentTime - loc_time;
		long diffDay = time / sDayms; // 得到天数
		long diffHour = 0;
		long diffMin = 0;
		if (diffDay > 0) { // 大于1天
			long hour = time - diffDay * sDayms; // 多少个小时的毫秒
			diffHour = hour / sHourms;
			if (diffHour > 0) { // 大于一个小时
				long m = hour - diffHour * sHourms; // 多少分钟的毫秒
				diffMin = m / sMINMS;
			} else {
				// 不到一个小时
				diffMin = hour / sMINMS;
			}
		} else { // 小于1天
			long hours = time / sHourms; // 多少个小时
			if (hours > 0) { // 大于1小时
				diffHour = hours;
				long mins = time - hours * sHourms; // 多少个分钟的毫秒
				diffMin = mins / sMINMS;
			} else {// 小于1小时
				diffMin = time / sMINMS; // 多少分钟
			}
		}
		if (diffDay != 0) {
			return String.format(context.getString(R.string.diff_time_day), diffDay);
		} else if (diffHour != 0) {
			return String.format(context.getString(R.string.diff_time_hour), diffHour);
		} else if (diffMin != 0) {
			return String.format(context.getString(R.string.diff_time_min), diffMin);
		} else {
			return context.getString(R.string.diff_time_just);
		}
	}
}
