package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.Like_Gridview_Adapter.ViewHolder;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.bean.TopicListBen.News;
import com.lidroid.xutils.BitmapUtils;

public class NewsAdapter extends BaseAdapter {
	private List<News> mNews;
	private ViewHolder mViewHolder;
	private Context mContext;
	private BitmapUtils mBitmapUtils;

	public NewsAdapter(Context mContext, BitmapUtils mBitmapUtils, List<News> mNews) {
		this.mContext = mContext;
		this.mBitmapUtils = mBitmapUtils;
		this.mNews = mNews;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNews.size();
	}

	@Override
	public Object getItem(int postion) {
		// TODO Auto-generated method stub
		return mNews.get(postion);
	}

	@Override
	public long getItemId(int postion) {
		// TODO Auto-generated method stub
		return postion;
	}

	@Override
	public View getView(int postion, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.fragment_news_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.news_pic = (ImageView) convertView.findViewById(R.id.news_pic);
			mViewHolder.news_title = (TextView) convertView.findViewById(R.id.news_title);
			mViewHolder.news_content = (TextView) convertView.findViewById(R.id.news_content);
			mViewHolder.news_time = (TextView) convertView.findViewById(R.id.news_time);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		mViewHolder.news_title.setText(mNews.get(postion).title);
		mViewHolder.news_content.setText(mNews.get(postion).txt);
		mViewHolder.news_time.setText(mNews.get(postion).time);
		Log.d("url", mNews.get(postion).url);
		mBitmapUtils.display(mViewHolder.news_pic, mNews.get(postion).url);
		return convertView;
	}

	public class ViewHolder {
		public ImageView news_pic;
		public TextView news_title;
		public TextView news_content;
		public TextView news_time;
	}

}
