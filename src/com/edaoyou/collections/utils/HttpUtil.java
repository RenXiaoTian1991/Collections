package com.edaoyou.collections.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
 * @Description: 客户端和服务器端数据交互的工具类
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-8-26 下午5:12:01 Copyright © 北京易道游网络有限公司 版权所有
 */
public class HttpUtil {

	// 初始化client，如果是wap方式联网，需要设置代理信息
	// public HttpUtil() {
	// client = new DefaultHttpClient();
	// if (GlobalParams.isWap) {
	// HttpHost host = new HttpHost(android.net.Proxy.getDefaultHost(),
	// android.net.Proxy.getDefaultPort());
	// HttpParams params = client.getParams();
	// params.setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
	// }
	// }

	/**
	 * 发送POST请求，携带json得到服务器返回的结果（json格式），必须要开启子线程。否则得不到数据。
	 * 
	 * @param url
	 *            请求地址
	 * @param json
	 *            请求体中封装的json字符串
	 * @return
	 */
	// public static String sendPost(String url, String json) {
	// HttpUtils http = new HttpUtils();
	// RequestParams params = new RequestParams();
	// params.addBodyParameter("json", json);
	// String s = null;
	// // 同步方法，获取服务器端返回的流
	// ResponseStream responseStream = null;
	// try {
	// responseStream = http.sendSync(HttpMethod.POST, url, params);
	// s = responseStream.readString();
	// } catch (HttpException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// if (responseStream != null)
	// responseStream.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return s;
	// }

	/**
	 * 发送POST请求，携带json和多个文件参数得到服务器返回的结果（json格式）
	 * 
	 * @param url
	 *            请求地址
	 * @param json
	 *            请求体中封装的json字符串
	 * @param List
	 *            <File> 上传多个文件
	 * @return
	 */
	public static String sendPost(String url, String json, List<File> files) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("json", json);
		if (files.size() == 1) {
			try {
				params.addBodyParameter("file", new FileInputStream(files.get(0)), files.get(0).length(), files.get(0).getName(),
						"application/octet-stream");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < files.size(); i++) {
				if (files.get(i) != null) {
					try {
						params.addBodyParameter("file" + i, new FileInputStream(files.get(i)), files.get(i).length(), files.get(i).getName(),
								"application/octet-stream");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		String s = null;
		ResponseStream responseStream = null;
		try {
			// 同步方法，获取服务器端返回的流
			responseStream = http.sendSync(HttpMethod.POST, url, params);
			s = responseStream.readString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (responseStream != null)
					responseStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	/**
	 * 发送POST请求，携带json和多个文件参数得到服务器返回的结果（json格式）
	 * 
	 * @param url
	 *            请求地址
	 * @param json
	 *            请求体中封装的json字符串
	 * @param byte[] 上传文件二进制数据
	 * @param String
	 *            上传文件文件名
	 * @return
	 */
	public static String sendPost(String url, String json, byte[] file, String fileName) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("json", json);
		try {
			params.addBodyParameter("file", new ByteArrayInputStream(file), file.length, fileName, "application/octet-stream");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String s = null;
		ResponseStream responseStream = null;
		try {
			// 同步方法，获取服务器端返回的流
			responseStream = http.sendSync(HttpMethod.POST, url, params);
			s = responseStream.readString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (responseStream != null)
					responseStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

}
