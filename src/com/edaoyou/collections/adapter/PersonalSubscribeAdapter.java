package com.edaoyou.collections.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.PersonalSubscribeActivity;
import com.edaoyou.collections.bean.Bean.Subscribe;
import com.lidroid.xutils.BitmapUtils;

public class PersonalSubscribeAdapter extends BaseAdapter {
	private Activity mContext;
	private List<Subscribe> mSubscribeList;
	private BitmapUtils mBitmapUtils;

	private final String NOATTENTION = "0";
	private Handler mHandler;

	public PersonalSubscribeAdapter(Context context, List<Subscribe> list, BitmapUtils bitmapUtils, Handler mHandler) {
		this.mSubscribeList = list;
		this.mBitmapUtils = bitmapUtils;
		this.mContext = (Activity) context;
		this.mHandler = mHandler;
	}

	public void setData(List<Subscribe> subscribeList) {
		this.mSubscribeList = subscribeList;
	}

	@Override
	public int getCount() {
		return mSubscribeList.size();
	}

	@Override
	public Object getItem(int position) {
		return mSubscribeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_personal_famous, null);
		}

		viewHolder = getHolder(convertView);

		final Subscribe subscribe = mSubscribeList.get(position);

		if (subscribe != null) {
			viewHolder.famous_header_iv.setBackgroundColor(Color.TRANSPARENT);
			viewHolder.famous_attention_ib.setBackgroundResource(R.drawable.label_ok_icon);
			mBitmapUtils.display(viewHolder.famous_header_iv, subscribe.avatar);

			viewHolder.famous_name_tv.setText(subscribe.topic);
			viewHolder.famous_detail_tv.setText(subscribe.txt);

			String is_followed = subscribe.is_followed;

			if (NOATTENTION.equals(is_followed)) {
				viewHolder.famous_attention_ib.setBackgroundResource(R.drawable.label_add_icon);
			} else {
				viewHolder.famous_attention_ib.setBackgroundResource(R.drawable.label_ok_icon);
			}

			viewHolder.famous_attention_ib.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Message msg = Message.obtain();
					msg.obj = position;
					msg.what = PersonalSubscribeActivity.ADAPTER_MSG;
					mHandler.sendMessage(msg);
				}
			});

			viewHolder.famous_item_ll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO
				}
			});
		}

		return convertView;
	}

	private ViewHolder getHolder(View convertView) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}
		return viewHolder;
	}

	public class ViewHolder {
		TextView famous_detail_tv;
		TextView famous_name_tv;
		ImageView famous_header_iv;
		ImageButton famous_attention_ib;
		LinearLayout famous_item_ll;

		public ViewHolder(View convertView) {
			famous_name_tv = (TextView) convertView.findViewById(R.id.famous_name_tv);
			famous_detail_tv = (TextView) convertView.findViewById(R.id.famous_detail_tv);
			famous_header_iv = (ImageView) convertView.findViewById(R.id.famous_header_iv);
			famous_attention_ib = (ImageButton) convertView.findViewById(R.id.famous_attention_ib);
			famous_item_ll = (LinearLayout) convertView.findViewById(R.id.famous_item_ll);
		}
	}
}
