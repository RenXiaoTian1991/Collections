package com.edaoyou.collections.bean;

import java.io.Serializable;
import java.util.List;

import com.edaoyou.collections.bean.Bean.Pois;
import com.edaoyou.collections.bean.Bean.Result;
import com.edaoyou.collections.bean.Bean.Results;

/**
 * 
 * @Description: 拍照模块位置location返回的JavaBean
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-9-30 上午11:55:36 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class PhotographLocation implements Serializable {
	/**
	 * 用于序列化和反序列化前后保持ID一致和唯一
	 */
	private static final long serialVersionUID = 1L;
	public int ret;
	public Response response;

	public class Response implements Serializable {
		/**
		 * 用于序列化和反序列化前后保持ID一致和唯一
		 */
		private static final long serialVersionUID = 1L;
		public int status;
		public String address;
		public List<Pois> pois;
		public String message;
		public List<Results> results;
		public List<Result> result;

	}
}
