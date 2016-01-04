package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.SelectInterestActivity;
import com.edaoyou.collections.bean.Bean.Subscribe;
import com.lidroid.xutils.BitmapUtils;

public class SelectInterestAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;
	private BitmapUtils mBitmapUtils;
	private Handler mHandler;
	private List<Subscribe> mSubscribe;

	public SelectInterestAdapter(Context context, BitmapUtils bitmapUtils, Handler handler, List<Subscribe> subscribe) {
		this.mContext = context;
		this.mBitmapUtils = bitmapUtils;
		this.mHandler = handler;
		this.mSubscribe = subscribe;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<Subscribe> subscribe) {
		this.mSubscribe = subscribe;
	}

	@Override
	public int getCount() {
		return mSubscribe.size();
	}

	@Override
	public Object getItem(int position) {
		return mSubscribe.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_select_interest, null);
			mViewHolder.select_interest_bg_iv = (ImageView) convertView.findViewById(R.id.select_interest_bg_iv);
			mViewHolder.select_interest_like_iv = (ImageView) convertView.findViewById(R.id.select_interest_like_iv);
			mViewHolder.select_interest_txt = (TextView) convertView.findViewById(R.id.select_interest_txt);
			mViewHolder.select_interest_title_name_tv = (TextView) convertView.findViewById(R.id.select_interest_title_name_tv);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		Subscribe subscribe = mSubscribe.get(position);
		String title = subscribe.title;
		String txt = subscribe.txt;
		String cover = subscribe.cover;
		String is_followed = subscribe.is_followed;
		mViewHolder.select_interest_title_name_tv.setText(title);
		mViewHolder.select_interest_txt.setText(txt);
		if ("1".equals(is_followed)) {
			mViewHolder.select_interest_like_iv.setBackground(mContext.getResources().getDrawable(R.drawable.select_interest_follow));
		} else {
			mViewHolder.select_interest_like_iv.setBackground(mContext.getResources().getDrawable(R.drawable.select_interest_unfollow));
		}
		mBitmapUtils.display(mViewHolder.select_interest_bg_iv, cover);

		mViewHolder.select_interest_like_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mHandler.removeMessages(SelectInterestActivity.MSG_ONCLICK_INTEREST);
				Message msg = Message.obtain();
				msg.what = SelectInterestActivity.MSG_ONCLICK_INTEREST;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		});

		return convertView;
	}

	private class ViewHolder {
		public ImageView select_interest_bg_iv;
		public ImageView select_interest_like_iv;
		public TextView select_interest_title_name_tv;
		public TextView select_interest_txt;
	}
}
