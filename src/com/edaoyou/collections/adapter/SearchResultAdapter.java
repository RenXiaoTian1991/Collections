package com.edaoyou.collections.adapter;

import java.util.ArrayList;
import java.util.List;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Bean.Article;
import com.edaoyou.collections.bean.Bean.ChatItem;
import com.edaoyou.collections.bean.Bean.Like;
import com.edaoyou.collections.bean.SearchResult;
import com.edaoyou.collections.bean.Bean.Tag;
import com.edaoyou.collections.bean.SearchResult.Response.SearchBase;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.utils.SearchUitl;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultAdapter extends BaseAdapter {

	public static final int SEARCH_ITEM_FOLLOW_TOP = 0;
	public static final int SEARCH_ITEM_CHAT_TOP = 1;
	public static final int SEARCH_ITEM_LIKE_TOP = 2;
	public static final int SEARCH_ITEM_TAG_TOP = 3;
	public static final int SEARCH_ITEM_USER_TOP = 4;
	public static final int SEARCH_ITEM_ARTICLES_TOP = 5;
	public static final int SEARCH_ITEM_FOLLOW_MORE = 6;
	public static final int SEARCH_ITEM_CHAT_MORE = 7;
	public static final int SEARCH_ITEM_LIKE_MORE = 8;
	public static final int SEARCH_ITEM_TAG_MORE = 9;
	public static final int SEARCH_ITEM_USER_MORE = 10;
	public static final int SEARCH_ITEM_ARTICLES_MORE = 11;
	public static final int SEARCH_ITEM_FOLLOWS = 12;
	public static final int SEARCH_ITEM_TAGS = 13;
	public static final int SEARCH_ITEM_USERS = 14;
	public static final int SEARCH_ITEM_ARTICLES = 15;
	public static final int SEARCH_ITEM_CHATS = 16;
	public static final int SEARCH_ITEM_LIKES = 17;
	private SearchResult mSearchResult;
	private Context mContext;
	private BitmapUtils mBitmapUtils;
	private int followsCount;
	private int chatsCount;
	private int likesCount;
	private int tagsCount;
	private int userCount;
	private int articlesCount;
	private String searchKey;

	public SearchResultAdapter(Context context, BitmapUtils bitmapUtils, SearchResult result, String searchKey) {
		this.mContext = context;
		this.mSearchResult = result;
		this.mBitmapUtils = bitmapUtils;
		this.searchKey = searchKey;
	}

	@Override
	public int getCount() {
		if (mSearchResult != null) {
			if (!reultIsNull(mSearchResult.response.follows)) {
				followsCount = getResultCount(mSearchResult.response.follows);
			}
			if (!reultIsNull(mSearchResult.response.chats)) {
				chatsCount = getResultCount(mSearchResult.response.chats);
			}
			if (!reultIsNull(mSearchResult.response.likes)) {
				likesCount = getResultCount(mSearchResult.response.likes);
			}
			if (!reultIsNull(mSearchResult.response.tags)) {
				tagsCount = getResultCount(mSearchResult.response.tags);
			}
			if (!reultIsNull(mSearchResult.response.user)) {
				userCount = getResultCount(mSearchResult.response.user);
			}
			if (!reultIsNull(mSearchResult.response.articles)) {
				articlesCount = getResultCount(mSearchResult.response.articles);
			}
		}
		return followsCount + chatsCount + likesCount + tagsCount + userCount + articlesCount;
	}

	/**
	 * 获取搜索结果返回的个数
	 * 
	 * @param base
	 * @param count
	 * @return
	 */
	private int getResultCount(SearchBase base) {
		int count = 0;
		if (base != null) {
			if (base.count <= 3) {
				count += (base.count + 1);
			} else {
				count += 5;
			}
		}
		return count;
	}

	/**
	 * 判断结果是否为null
	 * 
	 * @param base
	 * @return
	 */
	private boolean reultIsNull(SearchBase base) {
		return base == null || base.count <= 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		if (followsCount > 0) {
			if (position == 0) {
				return SEARCH_ITEM_FOLLOW_TOP;
			} else if (position > 0 && position < followsCount - 1) {
				return SEARCH_ITEM_FOLLOWS;
			}
		}
		if (position == followsCount - 1) {
			if (followsCount == 5) {
				return SEARCH_ITEM_FOLLOW_MORE;
			} else {
				return SEARCH_ITEM_FOLLOWS;
			}
		}
		if (chatsCount > 0) {
			if (position == followsCount) {
				return SEARCH_ITEM_CHAT_TOP;
			} else if (position > followsCount && position < followsCount + chatsCount - 1) {
				return SEARCH_ITEM_CHATS;
			}
			if (position == followsCount + chatsCount - 1) {
				if (chatsCount == 5) {
					return SEARCH_ITEM_CHAT_MORE;
				} else {
					return SEARCH_ITEM_CHATS;
				}
			}
		}
		if (likesCount > 0) {
			if (position == followsCount + chatsCount) {
				return SEARCH_ITEM_LIKE_TOP;
			} else if (position > followsCount + chatsCount && position < followsCount + chatsCount + likesCount - 1) {
				return SEARCH_ITEM_LIKES;
			}
			if (position == followsCount + chatsCount + likesCount - 1) {
				if (likesCount == 5) {
					return SEARCH_ITEM_LIKE_MORE;
				} else {
					return SEARCH_ITEM_LIKES;
				}
			}
		}
		if (tagsCount > 0) {
			if (position == followsCount + chatsCount + likesCount) {
				return SEARCH_ITEM_TAG_TOP;
			} else if (position > followsCount + chatsCount + likesCount && position < followsCount + chatsCount + likesCount + tagsCount) {
				return SEARCH_ITEM_TAGS;
			}
			if (position == followsCount + chatsCount + likesCount + tagsCount - 1) {
				if (tagsCount == 5) {
					return SEARCH_ITEM_TAG_MORE;
				} else {
					return SEARCH_ITEM_TAGS;
				}
			}
		}
		if (userCount > 0) {
			if (position == followsCount + chatsCount + likesCount + tagsCount) {
				return SEARCH_ITEM_USER_TOP;
			} else if (position > followsCount + chatsCount + likesCount + tagsCount
					&& position < followsCount + chatsCount + likesCount + tagsCount + userCount - 1) {
				return SEARCH_ITEM_USERS;
			}
			if (position == followsCount + chatsCount + likesCount + tagsCount + userCount - 1) {
				if (userCount == 5) {
					return SEARCH_ITEM_USER_MORE;
				} else {
					return SEARCH_ITEM_USERS;
				}
			}
		}
		if (articlesCount > 0) {
			if (position == followsCount + chatsCount + likesCount + tagsCount + userCount) {
				return SEARCH_ITEM_ARTICLES_TOP;
			} else if (position > followsCount + chatsCount + likesCount + tagsCount + userCount && position < getCount() - 1) {
				return SEARCH_ITEM_ARTICLES;
			}
			if (position == getCount() - 1) {
				if (articlesCount == 5) {
					return SEARCH_ITEM_ARTICLES_MORE;
				} else {
					return SEARCH_ITEM_ARTICLES;
				}
			}
		}
		return super.getItemViewType(position);
	}

	private View initSearchTop(SearchTopHolder searchTopHolder) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_item_top, null);
		searchTopHolder.search_item_top_tv = (TextView) convertView.findViewById(R.id.search_item_top_tv);
		searchTopHolder.search_item_line = convertView.findViewById(R.id.search_item_line);
		convertView.setTag(searchTopHolder);
		return convertView;
	}

	private View initSearchMore(SearchMoreHolder searchMoreHolder) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_more, null);
		searchMoreHolder.search_more_tv = (TextView) convertView.findViewById(R.id.search_more_tv);
		convertView.setTag(searchMoreHolder);
		return convertView;
	}

	private View initSearchUsers(SearchUsersHolder searchUsersHolder) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_follows, null);
		searchUsersHolder.search_user_icon_iv = (CirclePortrait) convertView.findViewById(R.id.search_user_icon_iv);
		searchUsersHolder.search_user_name_tv = (TextView) convertView.findViewById(R.id.search_user_name_tv);
		searchUsersHolder.search_item_line = convertView.findViewById(R.id.search_item_line);
		convertView.setTag(searchUsersHolder);
		return convertView;
	}

	private View initSearchTags(SearchTagHolder searchTagHolder) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_tags, null);
		searchTagHolder.search_tage_icon_bg_iv = (ImageView) convertView.findViewById(R.id.search_tage_icon_bg_iv);
		searchTagHolder.search_tag_icon_iv = (ImageView) convertView.findViewById(R.id.search_tag_icon_iv);
		searchTagHolder.search_tag_title_tv = (TextView) convertView.findViewById(R.id.search_tag_title_tv);
		searchTagHolder.search_tag_txt_tv = (TextView) convertView.findViewById(R.id.search_tag_txt_tv);
		searchTagHolder.search_item_line = convertView.findViewById(R.id.search_item_line);
		convertView.setTag(searchTagHolder);
		return convertView;
	}

	private View initSearchArticle(SearchArticlesHolder searchArticleSHolder) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_articles, null);
		searchArticleSHolder.search_articles_title_tv = (TextView) convertView.findViewById(R.id.search_articles_title_tv);
		searchArticleSHolder.search_articles_photo_iv = (ImageView) convertView.findViewById(R.id.search_articles_photo_iv);
		searchArticleSHolder.search_articles_txt_tv = (TextView) convertView.findViewById(R.id.search_articles_txt_tv);
		searchArticleSHolder.search_articles_time_tv = (TextView) convertView.findViewById(R.id.search_articles_time_tv);
		searchArticleSHolder.search_item_line = convertView.findViewById(R.id.search_item_line);
		convertView.setTag(searchArticleSHolder);
		return convertView;
	}

	private View initSearchLike(SearchLikesHolder searchLikesHolder) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_likes, null);
		searchLikesHolder.search_like_txt_tv = (TextView) convertView.findViewById(R.id.search_like_txt_tv);
		searchLikesHolder.search_like_photo_iv = (ImageView) convertView.findViewById(R.id.search_like_photo_iv);
		searchLikesHolder.search_likes_nick_tv = (TextView) convertView.findViewById(R.id.search_like_nick_tv);
		searchLikesHolder.search_item_line = convertView.findViewById(R.id.search_item_line);
		convertView.setTag(searchLikesHolder);
		return convertView;
	}

	private View initSearchChat(SearchChatsHolder searchChatsHolder) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_chats, null);
		searchChatsHolder.search_chat_icon_iv = (CirclePortrait) convertView.findViewById(R.id.search_chat_icon_iv);
		searchChatsHolder.search_chat_nick_tv = (TextView) convertView.findViewById(R.id.search_chat_nick_tv);
		searchChatsHolder.search_chat_txt_tv = (TextView) convertView.findViewById(R.id.search_chat_txt_tv);
		searchChatsHolder.search_item_line = convertView.findViewById(R.id.search_item_line);
		convertView.setTag(searchChatsHolder);
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchUsersHolder searchUsersHolder = null;
		SearchTopHolder searchTopHolder = null;
		SearchMoreHolder searchMoreHolder = null;
		SearchTagHolder searchTagHolder = null;
		SearchArticlesHolder searchArticleSHolder = null;
		SearchChatsHolder searchChatsHolder = null;
		SearchLikesHolder searchLikesHolder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case SEARCH_ITEM_FOLLOW_TOP:
			case SEARCH_ITEM_CHAT_TOP:
			case SEARCH_ITEM_LIKE_TOP:
			case SEARCH_ITEM_TAG_TOP:
			case SEARCH_ITEM_USER_TOP:
			case SEARCH_ITEM_ARTICLES_TOP:
				searchTopHolder = new SearchTopHolder();
				convertView = initSearchTop(searchTopHolder);
				break;
			case SEARCH_ITEM_FOLLOW_MORE:
			case SEARCH_ITEM_CHAT_MORE:
			case SEARCH_ITEM_LIKE_MORE:
			case SEARCH_ITEM_TAG_MORE:
			case SEARCH_ITEM_USER_MORE:
			case SEARCH_ITEM_ARTICLES_MORE:
				searchMoreHolder = new SearchMoreHolder();
				convertView = initSearchMore(searchMoreHolder);
				break;
			case SEARCH_ITEM_FOLLOWS:
			case SEARCH_ITEM_USERS:
				searchUsersHolder = new SearchUsersHolder();
				convertView = initSearchUsers(searchUsersHolder);
				break;
			case SEARCH_ITEM_TAGS:
				searchTagHolder = new SearchTagHolder();
				convertView = initSearchTags(searchTagHolder);
				break;
			case SEARCH_ITEM_ARTICLES:
				searchArticleSHolder = new SearchArticlesHolder();
				convertView = initSearchArticle(searchArticleSHolder);
				break;
			case SEARCH_ITEM_CHATS:
				searchChatsHolder = new SearchChatsHolder();
				convertView = initSearchChat(searchChatsHolder);
				break;
			case SEARCH_ITEM_LIKES:
				searchLikesHolder = new SearchLikesHolder();
				convertView = initSearchLike(searchLikesHolder);
				break;
			default:
				break;
			}
		} else {
			switch (type) {
			case SEARCH_ITEM_FOLLOW_TOP:
			case SEARCH_ITEM_CHAT_TOP:
			case SEARCH_ITEM_LIKE_TOP:
			case SEARCH_ITEM_TAG_TOP:
			case SEARCH_ITEM_USER_TOP:
			case SEARCH_ITEM_ARTICLES_TOP:
				if (convertView.getTag() instanceof SearchTopHolder) {
					searchTopHolder = (SearchTopHolder) convertView.getTag();
				} else {
					searchTopHolder = new SearchTopHolder();
					convertView = initSearchTop(searchTopHolder);
				}
				break;
			case SEARCH_ITEM_FOLLOW_MORE:
			case SEARCH_ITEM_CHAT_MORE:
			case SEARCH_ITEM_LIKE_MORE:
			case SEARCH_ITEM_TAG_MORE:
			case SEARCH_ITEM_USER_MORE:
			case SEARCH_ITEM_ARTICLES_MORE:
				if (!(convertView.getTag() instanceof SearchMoreHolder)) {
					searchMoreHolder = new SearchMoreHolder();
					convertView = initSearchMore(searchMoreHolder);
				} else {
					searchMoreHolder = (SearchMoreHolder) convertView.getTag();
				}
				break;
			case SEARCH_ITEM_FOLLOWS:
			case SEARCH_ITEM_USERS:
				if (convertView.getTag() instanceof SearchUsersHolder) {
					searchUsersHolder = (SearchUsersHolder) convertView.getTag();
				} else {
					searchUsersHolder = new SearchUsersHolder();
					convertView = initSearchUsers(searchUsersHolder);
				}
				break;
			case SEARCH_ITEM_TAGS:
				if (convertView.getTag() instanceof SearchTagHolder) {
					searchTagHolder = (SearchTagHolder) convertView.getTag();
				} else {
					searchTagHolder = new SearchTagHolder();
					convertView = initSearchTags(searchTagHolder);
				}
				break;
			case SEARCH_ITEM_ARTICLES:
				if (convertView.getTag() instanceof SearchArticlesHolder) {
					searchArticleSHolder = (SearchArticlesHolder) convertView.getTag();
				} else {
					searchArticleSHolder = new SearchArticlesHolder();
					convertView = initSearchArticle(searchArticleSHolder);
				}
				break;
			case SEARCH_ITEM_CHATS:
				if (convertView.getTag() instanceof SearchChatsHolder) {
					searchChatsHolder = (SearchChatsHolder) convertView.getTag();
				} else {
					searchChatsHolder = new SearchChatsHolder();
					convertView = initSearchChat(searchChatsHolder);
				}
				break;
			case SEARCH_ITEM_LIKES:
				if (convertView.getTag() instanceof SearchLikesHolder) {
					searchLikesHolder = (SearchLikesHolder) convertView.getTag();
				} else {
					searchLikesHolder = new SearchLikesHolder();
					convertView = initSearchLike(searchLikesHolder);
				}
				break;
			default:
				break;
			}
		}
		switch (type) {
		case SEARCH_ITEM_FOLLOW_TOP:
			searchTopHolder.search_item_top_tv.setText(mContext.getString(R.string.search_follow));
			break;
		case SEARCH_ITEM_CHAT_TOP:
			searchTopHolder.search_item_top_tv.setText(mContext.getString(R.string.search_chat));
			break;
		case SEARCH_ITEM_LIKE_TOP:
			searchTopHolder.search_item_top_tv.setText(mContext.getString(R.string.search_like));
			break;
		case SEARCH_ITEM_TAG_TOP:
			searchTopHolder.search_item_top_tv.setText(mContext.getString(R.string.search_tag));
			break;
		case SEARCH_ITEM_USER_TOP:
			searchTopHolder.search_item_top_tv.setText(mContext.getString(R.string.search_user));
			break;
		case SEARCH_ITEM_ARTICLES_TOP:
			searchTopHolder.search_item_top_tv.setText(mContext.getString(R.string.search_articles));
			break;
		case SEARCH_ITEM_FOLLOW_MORE:
			searchMoreHolder.search_more_tv.setText(mContext.getString(R.string.search_more_follow));
			break;
		case SEARCH_ITEM_CHAT_MORE:
			searchMoreHolder.search_more_tv.setText(mContext.getString(R.string.search_more_chat));
			break;
		case SEARCH_ITEM_LIKE_MORE:
			searchMoreHolder.search_more_tv.setText(mContext.getString(R.string.search_more_like));
			break;
		case SEARCH_ITEM_TAG_MORE:
			searchMoreHolder.search_more_tv.setText(mContext.getString(R.string.search_more_tag));
			break;
		case SEARCH_ITEM_USER_MORE:
			searchMoreHolder.search_more_tv.setText(mContext.getString(R.string.search_more_user));
			break;
		case SEARCH_ITEM_ARTICLES_MORE:
			searchMoreHolder.search_more_tv.setText(mContext.getString(R.string.search_more_articles));
			break;
		case SEARCH_ITEM_FOLLOWS:
			User followUser = mSearchResult.response.follows.list.get(position - 1);
			searchUsersHolder.search_user_name_tv.setText(SearchUitl.getBuilder(mContext, followUser.nick, searchKey));
			searchUsersHolder.UID = followUser.uid;
			mBitmapUtils.display(searchUsersHolder.search_user_icon_iv, followUser.avatar);
			if (position == followsCount - 1) {
				searchUsersHolder.search_item_line.setVisibility(View.GONE);
			} else {
				searchUsersHolder.search_item_line.setVisibility(View.VISIBLE);
			}
			break;
		case SEARCH_ITEM_TAGS:
			Tag tag = mSearchResult.response.tags.list.get(position - followsCount - likesCount - chatsCount - 1);
			mBitmapUtils.display(searchTagHolder.search_tage_icon_bg_iv, tag.cover);
			mBitmapUtils.display(searchTagHolder.search_tag_icon_iv, tag.avatar);
			searchTagHolder.search_tag_title_tv.setText(SearchUitl.getBuilder(mContext, tag.title, searchKey));
			searchTagHolder.search_tag_txt_tv.setText(tag.txt);
			if (position == followsCount + likesCount + chatsCount + tagsCount - 1) {
				searchTagHolder.search_item_line.setVisibility(View.GONE);
			} else {
				searchTagHolder.search_item_line.setVisibility(View.VISIBLE);
			}
			break;
		case SEARCH_ITEM_USERS:
			User user = mSearchResult.response.user.list.get(position - followsCount - likesCount - chatsCount - tagsCount - 1);
			searchUsersHolder.search_user_name_tv.setText(SearchUitl.getBuilder(mContext, user.nick, searchKey));
			searchUsersHolder.UID = user.uid;
			mBitmapUtils.display(searchUsersHolder.search_user_icon_iv, user.avatar);
			if (position == followsCount + likesCount + chatsCount + tagsCount + userCount - 1) {
				searchUsersHolder.search_item_line.setVisibility(View.GONE);
			} else {
				searchUsersHolder.search_item_line.setVisibility(View.VISIBLE);
			}
			break;
		case SEARCH_ITEM_ARTICLES:
			Article article = mSearchResult.response.articles.list.get(position - followsCount - likesCount - chatsCount - tagsCount - userCount - 1);
			searchArticleSHolder.search_articles_title_tv.setText(SearchUitl.getBuilder(mContext, article.title, searchKey));
			searchArticleSHolder.search_articles_txt_tv.setText(SearchUitl.getBuilder(mContext, article.txt, searchKey));
			searchArticleSHolder.search_articles_time_tv.setText(String.format(mContext.getString(R.string.search_come_form), article.time));
			searchArticleSHolder.URL = article.url;
			searchArticleSHolder.content = article.txt;
			searchArticleSHolder.title = article.title;
			searchArticleSHolder.photoUrl = article.photo;
			mBitmapUtils.display(searchArticleSHolder.search_articles_photo_iv, article.photo);
			if (position == getCount() - 1) {
				searchArticleSHolder.search_item_line.setVisibility(View.GONE);
			} else {
				searchArticleSHolder.search_item_line.setVisibility(View.VISIBLE);
			}
			break;
		case SEARCH_ITEM_CHATS:
			ChatItem chatItem = mSearchResult.response.chats.list.get(position - followsCount - 1);
			mBitmapUtils.display(searchChatsHolder.search_chat_icon_iv, chatItem.avatar);
			searchChatsHolder.search_chat_nick_tv.setText(chatItem.nick);
			searchChatsHolder.search_chat_txt_tv.setText(SearchUitl.getBuilder(mContext, chatItem.txt, searchKey));
			searchChatsHolder.UID = chatItem.to;
			searchChatsHolder.userName = chatItem.nick;
			if (position == followsCount + chatsCount - 1) {
				searchChatsHolder.search_item_line.setVisibility(View.GONE);
			} else {
				searchChatsHolder.search_item_line.setVisibility(View.VISIBLE);
			}
			break;
		case SEARCH_ITEM_LIKES:
			Like like = mSearchResult.response.likes.list.get(position - followsCount - chatsCount - 1);
			mBitmapUtils.display(searchLikesHolder.search_like_photo_iv, like.photo);
			searchLikesHolder.search_like_txt_tv.setText(SearchUitl.getBuilder(mContext, like.txt, searchKey));
			searchLikesHolder.search_likes_nick_tv.setText(String.format(mContext.getString(R.string.search_come_form), like.nick));
			searchLikesHolder.FID = like.fid;
			if (position == followsCount + chatsCount + likesCount - 1) {
				searchLikesHolder.search_item_line.setVisibility(View.GONE);
			} else {
				searchLikesHolder.search_item_line.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
		return convertView;
	}

	public static class SearchLikesHolder extends ViewHolder {
		public String FID;
		public ImageView search_like_photo_iv;
		public TextView search_like_txt_tv;
		public TextView search_likes_nick_tv;
	}

	public static class SearchChatsHolder extends ViewHolder {
		public String UID;
		public String userName;
		public CirclePortrait search_chat_icon_iv;
		public TextView search_chat_nick_tv;
		public TextView search_chat_txt_tv;
	}

	public static class SearchArticlesHolder extends ViewHolder {
		public ImageView search_articles_photo_iv;
		public TextView search_articles_title_tv;
		public TextView search_articles_txt_tv;
		public TextView search_articles_time_tv;
		public String URL;
		public String content;
		public String title;
		public String photoUrl;
	}

	public static class SearchTagHolder extends ViewHolder {
		private ImageView search_tage_icon_bg_iv;
		public ImageView search_tag_icon_iv;
		public TextView search_tag_title_tv;
		public TextView search_tag_txt_tv;
	}

	public static class SearchMoreHolder {
		public TextView search_more_tv;
	}

	public static class SearchTopHolder extends ViewHolder {
		public TextView search_item_top_tv;
	}

	public static class SearchUsersHolder extends ViewHolder {
		public CirclePortrait search_user_icon_iv;
		public TextView search_user_name_tv;
		public String UID;
	}

	public static class ViewHolder {
		public View search_item_line;
	}
}
