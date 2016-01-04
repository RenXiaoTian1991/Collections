package com.edaoyou.collections.bean;

import java.util.List;

import com.edaoyou.collections.bean.Bean.Article;
import com.edaoyou.collections.bean.Bean.ChatItem;
import com.edaoyou.collections.bean.Bean.Like;
import com.edaoyou.collections.bean.Bean.Tag;
// TODO 下面这是啥意思
;

/**
 * 
 * @Description: 搜索模块进入标签列表timeline/topic_list资讯返回的数据
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-9-30 下午3:18:06 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class SearchResult {
	public int ret;
	public Response response;

	public class Response {
		public SearchFollow follows;
		public SearchChat chats;
		public SearchLike likes;
		public SearchTag tags;
		public SearchUser user;
		public SearcHarticle articles;

		public class SearchBase {
			public int type;
			public int count;
			public int more;
		}

		public class SearchFollow extends SearchBase {
			public List<User> list;
		}

		public class SearchChat extends SearchBase {
			public List<ChatItem> list;
		}

		public class SearchLike extends SearchBase {
			public List<Like> list;
		}

		public class SearchTag extends SearchBase {
			public List<Tag> list;
		}

		public class SearchUser extends SearchBase {
			public List<User> list;
		}

		public class SearcHarticle extends SearchBase {
			public List<Article> list;
		}

	}
}
