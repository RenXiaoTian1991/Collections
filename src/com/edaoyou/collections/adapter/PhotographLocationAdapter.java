package com.edaoyou.collections.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Bean;
import com.edaoyou.collections.bean.Bean.Pois;
import com.edaoyou.collections.bean.Bean.Results;

public class PhotographLocationAdapter extends BaseAdapter {
	private List<Pois> mPoiss;
	private Pois mPois;
	private Results mResults;
	private boolean choose;

	public PhotographLocationAdapter(List<Pois> poiss, Pois pois,
			Results results, boolean choose) {
		mPoiss = poiss;
		mPois = pois;
		this.mResults = results;
		this.choose = choose;

		List<Pois> newPois = new ArrayList<Pois>();
		if (mPois != null) {
			for (int i = 0; i < mPoiss.size(); i++) {
				if (mPoiss.get(i).name.equals(mPois.name)) {
					newPois.add(mPoiss.remove(i));
					break;
				}
			}
			if (newPois.size() == 0) {
				newPois.add(mPois);
			}
		}
		if (mResults != null) {
			Pois p = new Bean().new Pois();
			p.name = mResults.name;
			p.addr = mResults.address;
			if (mPoiss.get(0).name.equals(mResults.name)) {
				newPois.add(mPoiss.remove(0));
			}
			if (newPois.size() == 0) {
				newPois.add(p);
			}
		}
		newPois.addAll(mPoiss);
		mPoiss = newPois;
	}

	public List<Pois> getmPoiss() {
		return mPoiss;
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
		if (position == 0 && !choose) {
			viewHolder.iv_location_choose2.setVisibility(View.VISIBLE);
		} else {
			viewHolder.iv_location_choose2.setVisibility(View.INVISIBLE);
		}
		viewHolder.tv_location.setText(mPoiss.get(position).name);
		viewHolder.tv_address.setText(mPoiss.get(position).addr);
		return convertView;
	}

	@Override
	public int getCount() {
		return mPoiss.size();
	}

	@Override
	public Object getItem(int position) {
		return mPoiss.get(position);
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
