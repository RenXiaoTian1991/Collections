package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Feed;
import com.edaoyou.collections.fragment.ChoicenessAndFriendsFragment;
import com.edaoyou.collections.view.CirclePortrait;
import com.edaoyou.collections.view.CustomImageButton;
import com.edaoyou.collections.view.FilletLayout;
import com.lidroid.xutils.BitmapUtils;

public class LikeListAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder mViewHolder;
	private BitmapUtils mBitmapUtils;
	private Handler mHandler;
	private List<Feed> lFeeds;
	public LikeListAdapter(Context context, BitmapUtils bitmapUtils, Handler handler, List<Feed> feeds) {
		super();
		this.mContext = context;
		this.mBitmapUtils = bitmapUtils;
		this.mHandler = handler;
		this.lFeeds = feeds;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lFeeds.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lFeeds.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public void setData(List<Feed> feeds) {
		this.lFeeds = feeds;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.likelist_xgridview_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.home_personnick_tv = (TextView) convertView.findViewById(R.id.home_personnick_tv);
			mViewHolder.home_thingtopic_tv = (TextView) convertView.findViewById(R.id.home_thingtopic_tv);
			mViewHolder.home_thing_feed_tv = (TextView) convertView.findViewById(R.id.home_thing_feed_tv);
			mViewHolder.home_person_head_iv = (CirclePortrait) convertView.findViewById(R.id.home_person_head_iv);
			mViewHolder.home_choiceness_thing_iv = (FilletLayout) convertView.findViewById(R.id.home_choiceness_thing_iv);
			mViewHolder.home_choiceness_detail_ll = (LinearLayout) convertView.findViewById(R.id.home_choiceness_detail_ll);
			mViewHolder.home_choiceness_totality_customIB = (CustomImageButton) convertView.findViewById(R.id.home_choiceness_totality_customIB);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		final Feed feed = lFeeds.get(position);
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

		mViewHolder.home_person_head_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Message msg = Message.obtain();
				msg.what = ChoicenessAndFriendsFragment.MSG_USER_HEAD_CLICK;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		});

		return convertView;
	}
	public class ViewHolder {
		public TextView home_thing_feed_tv;
		public TextView home_personnick_tv;
		public TextView home_thingtopic_tv;
		public CirclePortrait home_person_head_iv;
		public FilletLayout home_choiceness_thing_iv;
		public LinearLayout home_choiceness_detail_ll;
		public CustomImageButton home_choiceness_totality_customIB;
	}
}
