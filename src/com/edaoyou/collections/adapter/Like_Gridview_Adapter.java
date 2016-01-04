package com.edaoyou.collections.adapter;

import java.util.List;

import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.LikeListAdapter.ViewHolder;
import com.edaoyou.collections.view.CirclePortrait;
import com.edaoyou.collections.view.CustomImageButton;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Like_Gridview_Adapter extends BaseAdapter {
	
	List<String> like_gridview_list;
	private ViewHolder lViewHolder;
	private Context lContext;
	private BitmapUtils lBitmapUtils;
	public Like_Gridview_Adapter(Context lContext,BitmapUtils lBitmapUtils,List<String> like_gridview_list){
		this.like_gridview_list = like_gridview_list;
		this.lContext = lContext;
		this.lBitmapUtils = lBitmapUtils;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return like_gridview_list.size();
	}

	@Override
	public Object getItem(int postion) {
		// TODO Auto-generated method stub
		return like_gridview_list.get(postion);
	}

	@Override
	public long getItemId(int postion) {
		// TODO Auto-generated method stub
		return postion;
	}

	@Override
	public View getView(int postion, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(lContext, R.layout.fragment_like_gridview_item, null);
			lViewHolder = new ViewHolder();
			lViewHolder.like_juzhen_item_iv = (ImageView)convertView.findViewById(R.id.like_juzhen_item_iv);
			convertView.setTag(lViewHolder);
		}else{
			lViewHolder = (ViewHolder) convertView.getTag();
		}
		lBitmapUtils.display(lViewHolder.like_juzhen_item_iv, like_gridview_list.get(postion));
		return convertView;
	}
	public class ViewHolder {
		public ImageView like_juzhen_item_iv;
	}
}
