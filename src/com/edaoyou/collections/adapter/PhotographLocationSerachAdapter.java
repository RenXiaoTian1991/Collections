package com.edaoyou.collections.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.CollectionsAddress;

public class PhotographLocationSerachAdapter extends BaseAdapter {

	private List<? extends CollectionsAddress> addressList;

	public PhotographLocationSerachAdapter(
			List<? extends CollectionsAddress> address) {
		this.addressList = address;
	}

	public List<? extends CollectionsAddress> getResultss() {
		return addressList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(parent.getContext(),
					R.layout.fabu_location_item, null);
			viewHolder.tv_location = (TextView) convertView
					.findViewById(R.id.tv_location);
			viewHolder.tv_address = (TextView) convertView
					.findViewById(R.id.tv_address);
			viewHolder.iv_location_choose2 = (ImageView) convertView
					.findViewById(R.id.iv_location_choose2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.iv_location_choose2.setVisibility(View.INVISIBLE);

		CollectionsAddress collectionsAddress = addressList.get(position);
		if (collectionsAddress.address != null) {
			viewHolder.tv_location.setText(collectionsAddress.name);
			viewHolder.tv_address.setText(collectionsAddress.address);
		} else {
			viewHolder.tv_location.setText(collectionsAddress.name);
			viewHolder.tv_address.setText(collectionsAddress.addr);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return addressList.size();
	}

	@Override
	public Object getItem(int position) {
		return addressList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		private TextView tv_location;
		private TextView tv_address;
		private ImageView iv_location_choose2;
	}

}
