package com.edaoyou.collections.adapter;

import java.util.List;

import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.NotificationAdapter.ViewHolder;
import com.edaoyou.collections.bean.Bean.NotificationData;
import com.edaoyou.collections.bean.Bean.Subscribe;
import com.edaoyou.collections.fragment.NotificationFragment;
import com.edaoyou.collections.utils.TimeUitl;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyTopicAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder viewHolder;
	private List<Subscribe> mSubscribes;
	private BitmapUtils mBitmapUtils;

	public MyTopicAdapter(Context context, List<Subscribe> subscribes, BitmapUtils bitmapUtils) {
		super();
		this.mContext = context;
		this.mSubscribes = subscribes;
		this.mBitmapUtils = bitmapUtils;
	}

	public void setData(List<Subscribe> subscribes) {
		this.mSubscribes = subscribes;
	}

	@Override
	public int getCount() {
		return mSubscribes.size();
	}

	@Override
	public Object getItem(int position) {
		return mSubscribes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_my_topic_item, null);
			viewHolder = new ViewHolder();
			viewHolder.topic_avatar_iv = (CirclePortrait) convertView.findViewById(R.id.topic_avatar_iv);
			viewHolder.topic_name_tv = (TextView) convertView.findViewById(R.id.topic_name_tv);
			viewHolder.topic_txt_tv = (TextView) convertView.findViewById(R.id.topic_txt_tv);
			viewHolder.topic_time_tv = (TextView) convertView.findViewById(R.id.topic_time_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Subscribe subscribe = mSubscribes.get(position);
		String topic = subscribe.topic;
		String txt = subscribe.txt;
		String time = subscribe.time;
		String avatar = subscribe.avatar;
		int news = subscribe.news;
		String diffTime = TimeUitl.getDiffTime(mContext, time);
		viewHolder.topic_time_tv.setText(diffTime);
		viewHolder.topic_name_tv.setText(topic);
		viewHolder.topic_txt_tv.setText(txt);
		mBitmapUtils.display(viewHolder.topic_avatar_iv, avatar);
		return convertView;
	}

	public class ViewHolder {
		public CirclePortrait topic_avatar_iv;
		public TextView topic_name_tv;
		public TextView topic_txt_tv;
		public TextView topic_time_tv;
	}

}
