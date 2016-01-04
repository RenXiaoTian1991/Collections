package com.edaoyou.collections.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.edaoyou.collections.base.BaseFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

	private List<BaseFragment> fragments;

	public HomePagerAdapter(FragmentManager supportFragmentManager, List<BaseFragment> fragments) {
		super(supportFragmentManager);
		this.fragments = fragments;
	}

	public void setFragments(List<BaseFragment> fragments) {
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return null;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
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
		super.destroyItem(container, position, object);
	}
}
