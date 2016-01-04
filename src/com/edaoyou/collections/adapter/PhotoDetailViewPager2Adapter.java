package com.edaoyou.collections.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class PhotoDetailViewPager2Adapter extends PagerAdapter {
	private List<View> viewLists;

	public PhotoDetailViewPager2Adapter(List<View> lists) {
		viewLists = lists;
	}

	@Override
	public int getCount() {
		return viewLists.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewLists.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(viewLists.get(position), 0);
		return viewLists.get(position);
	}

}
