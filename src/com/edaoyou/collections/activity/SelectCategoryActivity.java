package com.edaoyou.collections.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.CategoryAdapter;
import com.edaoyou.collections.view.CustomTopbar;

/**
 * 
 * @Description: 拍照模块选择藏品类别
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-10-21 下午5:24:34 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class SelectCategoryActivity extends Activity {

	private CustomTopbar customTopbar;
	private ListView category_lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_category);
		findViews();
		initCustomTopbar();
		initData();
		setListeners();
	}

	private void findViews() {
		customTopbar = (CustomTopbar) findViewById(R.id.customTopbar);
		category_lv = (ListView) findViewById(R.id.category_lv);
	}

	private void initCustomTopbar() {
		customTopbar.setMiddleTVResIdString(R.string.collection_type);
		customTopbar.setSuffixVisiable();
		customTopbar.setMiddleTVColor(Color.BLACK);
		customTopbar.setPreIVBackground(R.drawable.black_ruturn);
		customTopbar.setPreIVOnclick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {
		ArrayList<String> items = getIntent().getStringArrayListExtra(PublishPhotoActivity.KEY_LIST);
		category_lv.setAdapter(new CategoryAdapter(SelectCategoryActivity.this, items));
	}

	private void setListeners() {
		category_lv.setOnItemClickListener(new MyOnItemClickListener());
	}

	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent();
			intent.putExtra(PublishPhotoActivity.KEY_POSITION, position);
			setResult(0, intent);
			SelectCategoryActivity.this.finish();
		}
	}

}
