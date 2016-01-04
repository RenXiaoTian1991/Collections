package com.edaoyou.collections.adapter;

import java.util.List;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edaoyou.collections.view.TouchImageView;

public class BigViewPagerAdapter extends PagerAdapter {
	private List<Bitmap> photobitmaps;

	public BigViewPagerAdapter(List<Bitmap> photobitmaps) {
		this.photobitmaps = photobitmaps;
	}

	@Override
	public int getCount() {
		return photobitmaps.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		TouchImageView view = new TouchImageView(container.getContext());
		view.setImageBitmap(photobitmaps.get(position));
		container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		return view;
	}
}
