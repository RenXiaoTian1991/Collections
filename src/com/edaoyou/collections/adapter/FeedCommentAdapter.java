package com.edaoyou.collections.adapter;

import java.util.List;

import com.easemob.chat.core.c;
import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Bean.Comment;
import com.edaoyou.collections.utils.TimeUitl;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FeedCommentAdapter extends BaseAdapter {

	private Context mContext;
	private List<Comment> mComments;
	private BitmapUtils mBitmapUtils;

	public FeedCommentAdapter(Context context, List<Comment> comments) {
		this.mContext = context;
		this.mComments = comments;
		mBitmapUtils = BitmapUtils.create(mContext);
	}

	public void setComments(List<Comment> comments) {
		mComments = comments;
	}

	public List<Comment> getComments() {
		return mComments;
	}

	@Override
	public int getCount() {
		return mComments.size();
	}

	@Override
	public Object getItem(int position) {
		return mComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_feed_comment_item, null);
			holder = new ViewHolder();
			holder.feed_user_icon_iv = (CirclePortrait) convertView.findViewById(R.id.feed_user_icon_iv);
			holder.feed_user_nick_tv = (TextView) convertView.findViewById(R.id.feed_user_nick_tv);
			holder.feed_comment_txt_tv = (TextView) convertView.findViewById(R.id.feed_comment_txt_tv);
			holder.feed_item_line = convertView.findViewById(R.id.feed_item_line);
			holder.feed_comment_time_tv = (TextView) convertView.findViewById(R.id.feed_comment_time_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Comment comment = mComments.get(position);
		mBitmapUtils.display(holder.feed_user_icon_iv, comment.avatar);
		holder.feed_user_nick_tv.setText(comment.nick);
		holder.feed_comment_txt_tv.setText(comment.txt);
		holder.feed_comment_time_tv.setText(TimeUitl.getDiffTime(mContext, comment.time + ""));
		return convertView;
	}

	public static class ViewHolder {
		public CirclePortrait feed_user_icon_iv;
		public TextView feed_user_nick_tv;
		public TextView feed_comment_txt_tv;
		public TextView feed_comment_time_tv;
		public View feed_item_line;
	}
}
