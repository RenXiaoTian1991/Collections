package com.edaoyou.collections.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @Description: 拍照模块app配置client/boot返回的JavaBean
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-9-25 下午5:17:39 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class PhotoGraphTagClientBoot implements Serializable {
	/**
	 * 用于序列化和反序列化前后保持ID一致和唯一
	 */
	private static final long serialVersionUID = 1L;
	public int ret;
	public Response response;

	public static class Response implements Serializable {
		/**
		 * 用于序列化和反序列化前后保持ID一致和唯一
		 */
		private static final long serialVersionUID = 1L;
		public int update;
		public int ver;
		public List<TagCategory> tag_category;
		public String shar_url;
		public String agreement_url;

		public static class TagCategory implements Serializable {
			/**
			 * 用于序列化和反序列化前后保持ID一致和唯一
			 */
			private static final long serialVersionUID = 1L;
			public String tag;
			public List<Detail> detail;

			public static class Detail implements Serializable {
				/**
				 * 用于序列化和反序列化前后保持ID一致和唯一
				 */
				private static final long serialVersionUID = 1L;
				public String category;
				public List<String> tag;
			}
		}
	}
}
