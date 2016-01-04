package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.TopicListBen.News;
import com.lidroid.xutils.BitmapUtils;

public class MyTopicsAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder viewHolder;
	private List<News> mNews;
	private BitmapUtils mBitmapUtils;

	public MyTopicsAdapter(Context context, List<News> news, BitmapUtils bitmapUtils) {
		super();
		this.mContext = context;
		this.mNews = news;
		this.mBitmapUtils = bitmapUtils;
	}

	public void setData(List<News> news) {
		this.mNews = news;
	}

	@Override
	public int getCount() {
		return mNews.size();
	}

	@Override
	public Object getItem(int position) {
		return mNews.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_my_topics_item, null);
			viewHolder = new ViewHolder();
			viewHolder.my_topics_photo_iv = (ImageView) convertView.findViewById(R.id.my_topics_photo_iv);
			viewHolder.my_topics_title_tv = (TextView) convertView.findViewById(R.id.my_topics_title_tv);
			viewHolder.my_topics_txt_tv = (TextView) convertView.findViewById(R.id.my_topics_txt_tv);
			viewHolder.my_topics_time_tv = (TextView) convertView.findViewById(R.id.my_topics_time_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		News news = mNews.get(position);
		String photo = news.photo;
		String title = news.title;
		String txt = news.txt;
		String time = news.time;
		mBitmapUtils.display(viewHolder.my_topics_photo_iv, photo);
		viewHolder.my_topics_title_tv.setText(title);
		viewHolder.my_topics_txt_tv.setText(txt);
		viewHolder.my_topics_time_tv.setText(time);
		return convertView;
	}

	public class ViewHolder {
		public ImageView my_topics_photo_iv;
		public TextView my_topics_title_tv;
		public TextView my_topics_txt_tv;
		public TextView my_topics_time_tv;
	}

}
