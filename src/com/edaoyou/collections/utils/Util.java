package com.edaoyou.collections.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Util {

	/**
	 * 判断程序是否重复启动
	 */
	public static boolean isApplicationRepeat(Context applicationContext) {
		int pid = android.os.Process.myPid();
		String processName = null;
		ActivityManager am = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = applicationContext.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pid) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					processName = info.processName;
				}
			} catch (Exception e) {
			}
		}
		String packageName = applicationContext.getPackageName();
		if (processName == null || !processName.equalsIgnoreCase("com.edaoyou.collections")) {
			return true;
		}
		return false;
	}

	/**
	 * 过滤特殊字符
	 */
	public static String stringFilter(String str) throws PatternSyntaxException {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * ＊时间转化成时间戳
	 * 
	 * @param time
	 * @return
	 */
	public static long Date2Timestamp(String time) {
		// Date或者String转化为时间戳
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime() / 1000;
	}

	/**
	 * 得到给定时间戳距离现在的时间
	 * 
	 * @param publicTimestamp
	 *            以前的时间戳
	 * @return
	 */
	public static String getPublicTime(long publicTimestamp) {
		long currentTimestamp = Date2Timestamp(getStringDate());
		// 得到时间差
		long timeGap = currentTimestamp - publicTimestamp;
		// 年
		if ((timeGap / (60 * 60 * 24 * 30 * 12)) > 0) {
			int years = (int) timeGap / (60 * 60 * 24 * 12);
			return years + "年前";
		} else if ((timeGap / (60 * 60 * 24 * 30)) > 0) {// 月
			int months = (int) timeGap / (60 * 60 * 24 * 30);
			return months + "个月前";
		} else if ((timeGap / (60 * 60 * 24)) > 0) {// 天
			int days = (int) timeGap / (60 * 60 * 24);
			return days + "天前";
		} else if ((timeGap / (60 * 60) > 0)) {// 时
			int hours = (int) timeGap / (60 * 60);
			return hours + "小时前";
		} else if ((timeGap / 60) > 0) {// 分
			int minutes = (int) timeGap / 60;
			return minutes + "分钟前";
		} else if (timeGap < 60) {// 秒 小于一分钟当作一秒前发布
			return "刚刚发布";
		}
		return "----";
	}

	public static String getStrTime(Long cc_time) {
		String re_StrTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");
		// 例如：cc_time=1291778220
		re_StrTime = sdf.format(new Date(cc_time * 1000L));

		return re_StrTime;

	}

	/**
	 * 获得当前手机屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisplayHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获得当前手机屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisplayWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 判断手机号码格式是否正确
	 */
	public static boolean isMobile(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 判断email格式是否正确
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 
	 * 密码加密
	 * 
	 * @param paramString
	 * @return
	 */
	public static String md5(String paramString) {
		String returnStr;
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			returnStr = byteToHexString(localMessageDigest.digest());
			return returnStr;
		} catch (Exception e) {
			return paramString;
		}
	}

	/**
	 * 对字符串进行逆序
	 * 
	 * @param str
	 *            被逆转的string
	 * @return str的逆序string
	 */
	public static String reverseString(String str) {
		StringBuilder sb = new StringBuilder(str);
		return sb.reverse().toString();
	}

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd HH:mm:ss
	 */

	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public static void hideSoftInput(Activity activity) {
		View view = activity.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static void showSoftInput(Context context, View view) {
		((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 获取今天是星期几
	 * 
	 * @return 1~7 对应星期一到星期天
	 */
	public static int getWeekDay() {
		Calendar now = Calendar.getInstance();
		// 一周第一天是否为星期天
		boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
		// 获取周几
		int weekDay = now.get(Calendar.DAY_OF_WEEK);
		// 若一周第一天为星期天，则-1
		if (isFirstSunday) {
			weekDay = weekDay - 1;
			if (weekDay == 0) {
				weekDay = 7;
			}
		}
		return weekDay;
	}

	/**
	 * 弹出一个仅有一个按钮的dialog提示框
	 * 
	 * @param context
	 * @param title
	 *            提示框标题
	 * @param message
	 *            提示框内容
	 * @param PositiveButtonStr
	 *            按钮内容
	 * @return
	 */
	public static Builder showDialog(Context context, String title, String message, String PositiveButtonStr) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message).setPositiveButton(PositiveButtonStr, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder;
	}

	/**
	 * 检查sd卡
	 * 
	 */
	public static boolean existSDcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据手机是否加载sd卡，配置对应缓存路径 .../mnt/sdcard/Android/data/包名/cache
	 * 
	 * @param context
	 * @return
	 */
	public static String getCachePath(Context context) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File file = context.getExternalCacheDir();
			if (file != null) {
				return file.getPath(); // cachePath:
										// sdcard/Android/data/data/{packageName}/cache/
			} else {
				return Environment.getExternalStorageDirectory().getPath() + "/";// cachePath:/sdcard/
			}
		} else {
			return context.getCacheDir().getPath();
		}
	}

	/**
	 * 删除某个文件夹下的所有文件夹和文件
	 * 
	 * @param delpath
	 *            String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefile(String delpath) {
		boolean flag = false;

		File file = new File(delpath);
		// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
		if (!file.isDirectory()) {// 是一个文件
			file.delete();
		} else {
			String[] filelist = file.list();
			if (filelist.length == 0) {
				flag = false;
			} else {
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deletefile(delpath + "/" + filelist[i]);
					}
				}
				flag = true;
			}
		}
		return flag;
	}
}
