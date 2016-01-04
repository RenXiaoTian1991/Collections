package com.edaoyou.collections.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Bean.Comment;
import com.edaoyou.collections.utils.Util;
import com.lidroid.xutils.BitmapUtils;

public class CommentsAdapter extends BaseAdapter {

	private List<Comment> mCommentLists;
	private Activity mContext;
	private BitmapUtils mBitmapUtils;

	public CommentsAdapter(Context context, BitmapUtils bitmapUtils, List<Comment> lists) {
		this.mCommentLists = new ArrayList<Comment>();
		this.mBitmapUtils = bitmapUtils;
		if (lists != null && lists.size() > 0) {
			addLists(lists);
		}
		mContext = (Activity) context;
	}

	public void addListItem(Comment listItem) {
		mCommentLists.add(listItem);
	}

	public void deleteListItem(int position) {
		mCommentLists.remove(position);
	}

	public void addLists(List<Comment> comments) {
		mCommentLists.addAll(comments);
	}

	@Override
	public int getCount() {
		return mCommentLists.size();
	}

	@Override
	public Object getItem(int position) {
		return mCommentLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(mContext, R.layout.home_userinfodisplayitem, null);
			findViewByIdAll(convertView, holder);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final Comment comment = mCommentLists.get(position);
		holder.head_iv.setTag(comment.avatar);
		mBitmapUtils.display(holder.head_iv, comment.avatar);

		holder.user_nick_tv.setText(comment.nick);
		holder.commentds_contents_tv.setText(comment.txt);
		holder.comment_time_tv.setText(Util.getPublicTime(comment.time));
		convertView.setPadding(0, 10, 0, 10);

		return convertView;
	}

	private void findViewByIdAll(View convertView, final Holder holder) {
		holder.head_iv = (ImageView) convertView.findViewById(R.id.head_iv);
		holder.user_nick_tv = (TextView) convertView.findViewById(R.id.user_nick_tv);
		holder.commentds_contents_tv = (TextView) convertView.findViewById(R.id.commentds_contents_tv);
		holder.comment_time_tv = (TextView) convertView.findViewById(R.id.comment_time_tv);
	}

	static class Holder {
		ImageView head_iv;
		TextView user_nick_tv;
		TextView commentds_contents_tv;
		TextView comment_time_tv;
	}
}