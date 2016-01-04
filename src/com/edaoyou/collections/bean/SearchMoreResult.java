package com.edaoyou.collections.bean;

import java.util.List;

import com.edaoyou.collections.bean.Bean.Article;
import com.edaoyou.collections.bean.Bean.ChatItem;
import com.edaoyou.collections.bean.Bean.Like;

public class SearchMoreResult {

	public class SearchUserResult extends SearchResult {

		public Response response;

		public class Response extends SearchResponse {
			public List<User> list;
		}
	}

	public class SearchArticleResult extends SearchResult {
		public Response response;

		public class Response extends SearchResponse {
			public List<Article> list;
		}
	}

	public class SearchLikeResult extends SearchResult {
		public Response response;

		public class Response extends SearchResponse {
			public List<Like> list;
		}
	}

	public class SearchChatResult extends SearchResult {
		public Response response;

		public class Response extends SearchResponse {
			public List<ChatItem> list;
		}
	}

	public class SearchResult {
		public int ret;

		public class SearchResponse {
			public int count;
			public int more;
		}
	}
}
