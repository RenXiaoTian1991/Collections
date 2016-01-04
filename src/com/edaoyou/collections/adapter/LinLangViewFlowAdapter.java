package com.edaoyou.collections.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.LinLangActivity;
import com.edaoyou.collections.bean.Guide.Response.Data;
import com.edaoyou.collections.utils.Util;
import com.lidroid.xutils.BitmapUtils;

public class LinLangViewFlowAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;
	private BitmapUtils mBitmapUtils;
	private Handler mHandler;
	private ArrayList<Data> mDatas;
	private int mScreenHeight; // 屏幕的高度
	private int mScreenWidth;// 屏幕的宽度

	public LinLangViewFlowAdapter(Context context, BitmapUtils bitmapUtils, Handler handler, ArrayList<Data> datas) {
		this.mDatas = datas;
		this.mBitmapUtils = bitmapUtils;
		this.mHandler = handler;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mScreenHeight = Util.getDisplayHeight(context);
		mScreenWidth = Util.getDisplayWidth(context);
	}

	public void setData(ArrayList<Data> datas) {
		this.mDatas = datas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_linlang, null);
			mViewHolder = new ViewHolder();
			mViewHolder.linlang_iv = (ImageView) convertView.findViewById(R.id.linlang_iv);
			mViewHolder.linlang_like_cb = (CheckBox) convertView.findViewById(R.id.linlang_like_cb);
			mViewHolder.linlang_like_count_tv = (TextView) convertView.findViewById(R.id.linlang_like_count_tv);
			mViewHolder.linlang_share_iv = (ImageView) convertView.findViewById(R.id.linlang_share_iv);
			mViewHolder.linlang_description_iv = (ImageView) convertView.findViewById(R.id.linlang_description_iv);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		setLinlangLayoutParams(mViewHolder.linlang_iv);
		setDescriptionLayoutParams(mViewHolder.linlang_description_iv);
		Data data = mDatas.get(position);
		mBitmapUtils.display(mViewHolder.linlang_iv, data.getUrl1());
		mBitmapUtils.display(mViewHolder.linlang_description_iv, data.getUrl2());

		String likes = data.getLikes();
		mViewHolder.linlang_like_count_tv.setText(likes);

		String is_like = data.getIs_like();
		mViewHolder.linlang_like_cb.setChecked("1".equals(is_like));
		mViewHolder.linlang_like_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mHandler.removeMessages(LinLangActivity.MSG_ONCLICK_LIKE);
				Message msg = Message.obtain();
				msg.what = LinLangActivity.MSG_ONCLICK_LIKE;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		});
		mViewHolder.linlang_share_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mHandler.removeMessages(LinLangActivity.MSG_ONCLICK_SHARE);
				Message msg = Message.obtain();
				msg.what = LinLangActivity.MSG_ONCLICK_SHARE;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		});
		return convertView;
	}

	private void setLinlangLayoutParams(ImageView iv) {
		LayoutParams layoutParams = iv.getLayoutParams();
		layoutParams.width = mScreenWidth;
		layoutParams.height = mScreenHeight * 3 / 7;
		iv.setLayoutParams(layoutParams);
	}

	private void setDescriptionLayoutParams(ImageView iv) {
		LayoutParams layoutParams = iv.getLayoutParams();
		layoutParams.width = mScreenWidth * 4 / 7;
		layoutParams.height = mScreenHeight * 1 / 3 - mScreenHeight * 1 / 13;
		iv.setLayoutParams(layoutParams);
	}

	private class ViewHolder {
		public ImageView linlang_iv;
		public CheckBox linlang_like_cb;
		public TextView linlang_like_count_tv;
		public ImageView linlang_share_iv;
		public ImageView linlang_description_iv;
	}
}
