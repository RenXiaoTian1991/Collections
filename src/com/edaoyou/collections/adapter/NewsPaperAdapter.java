package com.edaoyou.collections.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.topic.BaseViewPagerFragment;
import com.edaoyou.collections.topic.ListViewFragment;

public class NewsPaperAdapter extends FragmentPagerAdapter {
	private List<BaseViewPagerFragment> fragments;
	private String mTopicId;
	private String[] mTabName = new String[]{"热门","最新","咨询"};
	public NewsPaperAdapter(FragmentManager fm,String mTopicId) {
		super(fm);
		this.mTopicId = mTopicId;
	}

	public void setFragments(List<BaseViewPagerFragment> fragments) {
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		
		return mTabName[position];
	}

	@Override
	public Fragment getItem(int position) {
		return ListViewFragment.newInstance(position,mTopicId);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
	}

}
