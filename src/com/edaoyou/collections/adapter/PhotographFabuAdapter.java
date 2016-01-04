package com.edaoyou.collections.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edaoyou.collections.R;

public class PhotographFabuAdapter extends BaseAdapter {
	private Context context;
	private int[] itemPhotos;
	private String[] itemNames;
	private String result = null;

	public PhotographFabuAdapter(Context context, int[] itemPhotos,
			String[] itemNames, String result) {
		this.context = context;
		this.itemPhotos = itemPhotos;
		this.itemNames = itemNames;
		this.result = result;
	}

	public void modifyChild(int index, int itemPhotosRes, String itemNamesRes,
			String result) {
		itemPhotos[index] = itemPhotosRes;
		itemNames[index] = itemNamesRes;
		this.result = result;
	}

	public void modifyChild(int index, int itemPhotosRes, String itemNamesRes) {
		itemPhotos[index] = itemPhotosRes;
		itemNames[index] = itemNamesRes;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView==null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(context, R.layout.fabu_item, null);
			viewHolder.tv_item_name = (TextView) convertView
					.findViewById(R.id.tv_item_name);
			viewHolder.tv_result = (TextView) convertView.findViewById(R.id.tv_result);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position == 2) {
			viewHolder.tv_result.setVisibility(View.VISIBLE);
			viewHolder.tv_result.setText(result);
		}else{
			viewHolder.tv_result.setVisibility(View.GONE);
		}
		
		Drawable drawable = context.getResources().getDrawable(
				itemPhotos[position]);
		viewHolder.tv_item_name.setCompoundDrawablesWithIntrinsicBounds(
				drawable, null, null, null);
		viewHolder.tv_item_name.setText(itemNames[position]);
		return convertView;
	}

	@Override
	public int getCount() {
		return itemNames.length;
	}

	@Override
	public Object getItem(int position) {
		return itemNames[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		private TextView tv_item_name;
		private TextView tv_result;
	}

}
