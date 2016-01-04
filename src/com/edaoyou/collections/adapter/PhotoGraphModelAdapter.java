package com.edaoyou.collections.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;

// TODO 能用科学方式解决警告否
@SuppressLint("Recycle")
public class PhotoGraphModelAdapter extends BaseAdapter {

	private Context mContext;
	private TypedArray mImgsArray;
	private TypedArray mStringArray;

	public PhotoGraphModelAdapter(Context context) {
		super();
		this.mContext = context;
		mImgsArray = context.getResources().obtainTypedArray(R.array.camera_modes);
		mStringArray = context.getResources().obtainTypedArray(R.array.camera_modes_strs);
	}

	@Override
	public int getCount() {
		return mImgsArray.length();
	}

	@Override
	public Object getItem(int position) {
		return mImgsArray.getDrawable(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// TODO 能用科学方式解决警告否
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HodlerView hodlerView = null;
		if (convertView == null) {
			hodlerView = new HodlerView();
			convertView = View.inflate(mContext, R.layout.camera_item, null);
			hodlerView.rl_imageview = (RelativeLayout) convertView.findViewById(R.id.rl_imageview);
			hodlerView.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
			convertView.setTag(hodlerView);
		} else {
			hodlerView = (HodlerView) convertView.getTag();
		}
		hodlerView.rl_imageview.setBackgroundDrawable(mImgsArray.getDrawable(position));
		hodlerView.tv_text.setText(mStringArray.getString(position));
		return convertView;
	}

	class HodlerView {
		private RelativeLayout rl_imageview;
		private TextView tv_text;
	}

}