package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Feed;
import com.edaoyou.collections.view.CirclePortrait;
import com.edaoyou.collections.view.CustomImageButton;
import com.edaoyou.collections.view.FilletLayout;
import com.lidroid.xutils.BitmapUtils;

public class PersonHomepageAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder mViewHolder;
	private BitmapUtils mBitmapUtils;
	private List<Feed> mFeedList;

	public PersonHomepageAdapter(Context mContext, BitmapUtils mBitmapUtils, List<Feed> mFeedList) {
		this.mContext = mContext;
		this.mBitmapUtils = mBitmapUtils;
		this.mFeedList = mFeedList;
	}

	public void setData(List<Feed> feeds) {
		this.mFeedList = feeds;
	}

	@Override
	public Object getItem(int position) {
		return mFeedList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private ViewHolder getHolder(View convertView) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}
		return viewHolder;
	}

	@Override
	public int getCount() {
		return mFeedList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_home_item, null);
		}
		mViewHolder = getHolder(convertView);
		final Feed feed = mFeedList.get(position);
		int total_like = feed.data.total_like;
		String headImageUrl = feed.avatar;
		String nick = feed.nick;
		String text = feed.data.text;
		String thingurl = feed.data.photo.get(0).url;
		String topic;
		if (feed.data.topic.topic != null) {
			topic = feed.data.topic.topic.toString();
		} else {
			topic = null;
		}

		mViewHolder.home_choiceness_tv.setVisibility(View.GONE);
		mViewHolder.home_choiceness_thing_iv.setTag(thingurl);// 设置文玩图片
		mViewHolder.home_personnick_tv.setText(nick);// 设置昵称
		mViewHolder.home_thing_feed_tv.setText(text);// 设置feed文字内容
		mViewHolder.home_thingtopic_tv.setText(topic);// 分类标签
		mViewHolder.home_choiceness_thing_iv.setTag(thingurl);// 设置文玩图片
		// 设置喜欢的总数
		mViewHolder.home_choiceness_totality_customIB.setTextViewText(total_like + "");
		mViewHolder.home_choiceness_totality_customIB.setPadding(10, 0, 0, 10);

		mBitmapUtils.display(mViewHolder.home_choiceness_thing_iv, thingurl);
		mBitmapUtils.display(mViewHolder.home_person_head_iv, headImageUrl);

		return convertView;
	}

	public class ViewHolder {
		TextView home_thing_feed_tv;
		TextView home_personnick_tv;
		TextView home_thingtopic_tv;
		TextView home_choiceness_tv;
		CirclePortrait home_person_head_iv;
		FilletLayout home_choiceness_thing_iv;
		CustomImageButton home_choiceness_totality_customIB;

		public ViewHolder(View convertView) {
			home_personnick_tv = (TextView) convertView.findViewById(R.id.home_personnick_tv);
			home_thingtopic_tv = (TextView) convertView.findViewById(R.id.home_thingtopic_tv);
			home_thing_feed_tv = (TextView) convertView.findViewById(R.id.home_thing_feed_tv);
			home_choiceness_tv = (TextView) convertView.findViewById(R.id.home_choiceness_tv);
			home_person_head_iv = (CirclePortrait) convertView.findViewById(R.id.home_person_head_iv);
			home_choiceness_thing_iv = (FilletLayout) convertView.findViewById(R.id.home_choiceness_thing_iv);
			home_choiceness_totality_customIB = (CustomImageButton) convertView.findViewById(R.id.home_choiceness_totality_customIB);
		}
	}
}
