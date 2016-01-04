package com.edaoyou.collections.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CameraTeachGalleryAdapter extends BaseAdapter {
	private Context mContext;
	private TypedArray mImgs;

	public CameraTeachGalleryAdapter(Context context, TypedArray imgs) {
		this.mContext = context;
		this.mImgs = imgs;
	}

	public int getCount() {
		if (mImgs.length() == 0) {
			return 0;
		}
		return mImgs.length();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout linearLayout = new LinearLayout(mContext);
		ImageView iv = new ImageView(mContext);
		iv.setImageDrawable(mImgs.getDrawable(position));
		iv.setAdjustViewBounds(true);
		if (0 == position) {
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 0);
			iv.setLayoutParams(layoutParams);
		} else {
			iv.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		linearLayout.addView(iv);
		return linearLayout;
	}

}
