package com.edaoyou.collections.bean;

import java.util.List;

import com.edaoyou.collections.bean.Bean.Category;
// TODO 删除警告

/**
 * 
 * @Description:话题标签/ 搜索模块进入标签列表timeline/topic_list
 * @date 2014-9-30 下午3:18:06 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class HomeTopics {
	public int ret;
	public Response response;

	public class Response {
		public List<Category> topic_category;
	}
}
