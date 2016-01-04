package com.edaoyou.collections.adapter;

import java.util.List;
import java.util.TreeMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.PhotoGraphTagClientBoot.Response.TagCategory;
import com.edaoyou.collections.bean.PhotoGraphTagClientBoot.Response.TagCategory.Detail;

public class DoubleExpandableListViewAdapter extends BaseExpandableListAdapter {

	private TagCategory mTagCategory;
	private LayoutInflater mInflater;

	private TreeMap<Integer, String> mResults = new TreeMap<Integer, String>();
	private int mZiPosition;
	private int mSecondGroupPosition;

	private boolean mIsSelectGroup;

	public DoubleExpandableListViewAdapter(Context context, TagCategory tagCategory) {
		this.mInflater = LayoutInflater.from(context);
		this.mTagCategory = tagCategory;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.fabu_item, parent, false);
		TextView tv = (TextView) view.findViewById(R.id.tv_item_name);
		String groupName = mTagCategory.tag;
		tv.setText(groupName);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.fabu_child_item, parent, false);
		TextView name = (TextView) view.findViewById(R.id.tv_item_name);
		TextView result = (TextView) view.findViewById(R.id.tv_result);
		List<Detail> details = mTagCategory.detail;
		Detail detail = details.get(childPosition);
		String secondName = detail.category + "(可选)";
		name.setText(secondName);
		if (mIsSelectGroup) {
			mResults.clear();
			result.setVisibility(View.GONE);
		} else {
			if (mSecondGroupPosition == childPosition) {
				List<String> tag = detail.tag;
				String resultStr = tag.get(mZiPosition);
				result.setText(resultStr);
				mResults.put(childPosition, resultStr);
			}
			if (mResults.containsKey(childPosition)) {
				String resultStr = mResults.get(childPosition);
				result.setText(resultStr);
			}
			result.setVisibility(View.VISIBLE);
		}
		return view;
	}

	@Override
	public int getGroupCount() {
		return 1;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int childrenCount = mTagCategory.detail.size();
		return childrenCount;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setDate(TagCategory tagCategory) {
		this.mTagCategory = tagCategory;
	}

	public void setZiPosition(int ziPosition) {
		this.mZiPosition = ziPosition;
	}

	public void setIsSelectGroup(boolean isSelectGroup) {
		this.mIsSelectGroup = isSelectGroup;
	}

	public void setSecondGroupPosition(int secondGroupPosition) {
		this.mSecondGroupPosition = secondGroupPosition;
	}

	public TreeMap<Integer, String> getUserSelcetCategory() {
		return mResults;
	}
}
