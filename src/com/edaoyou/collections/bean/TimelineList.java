package com.edaoyou.collections.bean;

import java.util.List;


/**
 * 
 * @Description: 首页精选timeline/hot_list或我关注的timeline/public_list或timeline/tag_list或
 * 我赞过的timeline/like_list或刷新Feed详情timeline/get_feed或个人主页timeline/private_list(图片模式、信息流模式、标签分类模式)
 * 或搜索模块标签、活动列表timeline/topic_list热门或最新返回的数据
 * @author rongwei.mei mayroewe@live.cn 
 * @date 2014-9-30 下午12:53:29  
 * Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class TimelineList {
	public int ret;
	public Response response;
	
	public static class Response{
		public int count;
		public int more;
		public List<Feed> feeds;
	}
}
