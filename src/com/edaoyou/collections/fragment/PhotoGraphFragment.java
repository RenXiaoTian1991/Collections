package com.edaoyou.collections.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.CameraPatternActivity;
import com.edaoyou.collections.activity.CustomCameraActivity;
import com.edaoyou.collections.adapter.PhotoGraphModelAdapter;

public class PhotoGraphFragment extends Fragment {

	private GridView grid_view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photograph, null);
		grid_view = (GridView) view.findViewById(R.id.grid_view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		grid_view.setOnItemClickListener(new MyOnItemClickListener());
		grid_view.setSelector(new ColorDrawable(Color.TRANSPARENT));
		grid_view.setAdapter(new PhotoGraphModelAdapter(getActivity()));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (getView() != null) {
			getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
		}
	}

	private class MyOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = null;
			if (position == GlobalParams.CAMERA_MODE_FREE) {// 如果是自由模式，直接拍照
				GlobalParams.CURRENT_CAMERA_MODE = GlobalParams.CAMERA_MODE_FREE;
				intent = new Intent(getActivity(), CustomCameraActivity.class);
				startActivity(intent);
			} else {// 如果是非自由模式，先跳转到拍摄教学页面
				GlobalParams.CURRENT_CAMERA_MODE = position;
				intent = new Intent(getActivity(), CameraPatternActivity.class);
				startActivity(intent);
			}
		}
	}
}
