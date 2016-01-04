package com.edaoyou.collections.adapter;

import java.util.List;

import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.SearchMoreActivity;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.bean.Bean.Article;
import com.edaoyou.collections.bean.Bean.ChatItem;
import com.edaoyou.collections.bean.Bean.Like;
import com.edaoyou.collections.utils.SearchUitl;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchMoreAdapter extends BaseAdapter {
	private Context mContext;
	@SuppressWarnings("rawtypes")
	private List mDataList;
	private int mSearchType;
	private BitmapUtils mBitmapUtils;
	private String mSearchKey;

	@SuppressWarnings("rawtypes")
	public SearchMoreAdapter(Context context, List dataList, int searchType, BitmapUtils bitmapUtils) {
		this.mContext = context;
		this.mDataList = dataList;
		this.mSearchType = searchType;
		this.mBitmapUtils = bitmapUtils;
	}

	public SearchMoreAdapter(Context context, int searchType, BitmapUtils bitmapUtils) {
		this.mContext = context;
		this.mSearchType = searchType;
		this.mBitmapUtils = bitmapUtils;
	}

	public void setSearchKey(String searchKey) {
		this.mSearchKey = searchKey;
	}

	@SuppressWarnings("rawtypes")
	public void setDataList(List dataList) {
		this.mDataList = dataList;
	}

	@SuppressWarnings("rawtypes")
	public List getDataList() {
		return mDataList;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position != 0) {
			return mDataList.get(position - 1);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		switch (mSearchType) {
		case SearchMoreActivity.SEARCH_USER:
		case SearchMoreActivity.SEARCH_FOLLOW:
			ViewUserHolder userHolder;
			if (convertView == null) {
				userHolder = new ViewUserHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_follows, null);
				userHolder.search_user_icon_iv = (CirclePortrait) convertView.findViewById(R.id.search_user_icon_iv);
				userHolder.search_user_name_tv = (TextView) convertView.findViewById(R.id.search_user_name_tv);
				convertView.setTag(userHolder);
			} else {
				userHolder = (ViewUserHolder) convertView.getTag();
			}
			User user = (User) mDataList.get(position);
			mBitmapUtils.display(userHolder.search_user_icon_iv, user.avatar);
			userHolder.search_user_name_tv.setText(SearchUitl.getBuilder(mContext, user.nick, mSearchKey));
			break;
		case SearchMoreActivity.SEARCH_ARTICLE:
			ViewAritcleHolder aritcleHoler;
			if (convertView == null) {
				aritcleHoler = new ViewAritcleHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_articles, null);
				aritcleHoler.search_articles_title_tv = (TextView) convertView.findViewById(R.id.search_articles_title_tv);
				aritcleHoler.search_articles_photo_iv = (ImageView) convertView.findViewById(R.id.search_articles_photo_iv);
				aritcleHoler.search_articles_txt_tv = (TextView) convertView.findViewById(R.id.search_articles_txt_tv);
				aritcleHoler.search_articles_time_tv = (TextView) convertView.findViewById(R.id.search_articles_time_tv);
				convertView.setTag(aritcleHoler);
			} else {
				aritcleHoler = (ViewAritcleHolder) convertView.getTag();
			}
			Article article = (Article) mDataList.get(position);
			aritcleHoler.search_articles_title_tv.setText(SearchUitl.getBuilder(mContext, article.title, mSearchKey));
			aritcleHoler.search_articles_txt_tv.setText(SearchUitl.getBuilder(mContext, article.txt, mSearchKey));
			aritcleHoler.search_articles_time_tv.setText(String.format(mContext.getString(R.string.search_come_form), article.time));
			mBitmapUtils.display(aritcleHoler.search_articles_photo_iv, article.photo);
			break;
		case SearchMoreActivity.SEARCH_LIKE:
			ViewLikesHolder likesHolder;
			if (convertView == null) {
				likesHolder = new ViewLikesHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_likes, null);
				likesHolder.search_like_txt_tv = (TextView) convertView.findViewById(R.id.search_like_txt_tv);
				likesHolder.search_like_photo_iv = (ImageView) convertView.findViewById(R.id.search_like_photo_iv);
				likesHolder.search_likes_nick_tv = (TextView) convertView.findViewById(R.id.search_like_nick_tv);
				convertView.setTag(likesHolder);
			} else {
				likesHolder = (ViewLikesHolder) convertView.getTag();
			}
			Like like = (Like) mDataList.get(position);
			mBitmapUtils.display(likesHolder.search_like_photo_iv, like.photo);
			likesHolder.search_like_txt_tv.setText(SearchUitl.getBuilder(mContext, like.txt, mSearchKey));
			likesHolder.search_likes_nick_tv.setText(String.format(mContext.getString(R.string.search_come_form), like.nick));
			break;
		case SearchMoreActivity.SEARCH_CHAT:
			ViewChatsHolder chatHolder;
			if (convertView == null) {
				chatHolder = new ViewChatsHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_chats, null);
				chatHolder.search_chat_icon_iv = (CirclePortrait) convertView.findViewById(R.id.search_chat_icon_iv);
				chatHolder.search_chat_nick_tv = (TextView) convertView.findViewById(R.id.search_chat_nick_tv);
				chatHolder.search_chat_txt_tv = (TextView) convertView.findViewById(R.id.search_chat_txt_tv);
				convertView.setTag(chatHolder);
			} else {
				chatHolder = (ViewChatsHolder) convertView.getTag();
			}
			ChatItem chatItem = (ChatItem) mDataList.get(position);
			mBitmapUtils.display(chatHolder.search_chat_icon_iv, chatItem.avatar);
			chatHolder.search_chat_nick_tv.setText(chatItem.nick);
			chatHolder.search_chat_txt_tv.setText(SearchUitl.getBuilder(mContext, chatItem.txt, mSearchKey));
			break;

		default:
			break;
		}
		return convertView;
	}

	public static class ViewChatsHolder {
		public CirclePortrait search_chat_icon_iv;
		public TextView search_chat_nick_tv;
		public TextView search_chat_txt_tv;
	}

	public static class ViewLikesHolder {
		public ImageView search_like_photo_iv;
		public TextView search_like_txt_tv;
		public TextView search_likes_nick_tv;
	}

	public static class ViewUserHolder {
		public CirclePortrait search_user_icon_iv;
		public TextView search_user_name_tv;
	}

	public static class ViewAritcleHolder {
		public ImageView search_articles_photo_iv;
		public TextView search_articles_title_tv;
		public TextView search_articles_txt_tv;
		public TextView search_articles_time_tv;
	}
}
