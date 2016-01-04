package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edaoyou.collections.R;

public class TagAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mItems;
	private ViewHolder mViewHolder;

	public TagAdapter(Context context, List<String> items) {
		this.mContext = context;
		this.mItems = items;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.category_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		mViewHolder.tv_item_name.setText(mItems.get(position));
		return convertView;
	}

	public class ViewHolder {
		public TextView tv_item_name;
	}
}
