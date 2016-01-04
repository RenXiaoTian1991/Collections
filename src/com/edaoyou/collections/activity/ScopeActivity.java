package com.edaoyou.collections.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.edaoyou.collections.R;
import com.edaoyou.collections.view.CustomTopbar;

public class ScopeActivity extends Activity {
	private RadioGroup scope_radiogroup;
	private CustomTopbar customTopbar;
	private RadioButton public_rb, private_rb;

	private String result = null;
	private int scope = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photographscope);

		findAllViewsById();
		setCustomTopBar();
		scope = getIntent().getIntExtra("fanwei", 0);
		setRedioGroup();
	}

	public void findAllViewsById() {
		customTopbar = (CustomTopbar) findViewById(R.id.customTopbar);
		scope_radiogroup = (RadioGroup) findViewById(R.id.scope_radiogroup);
		public_rb = (RadioButton) findViewById(R.id.public_rb);
		private_rb = (RadioButton) findViewById(R.id.private_rb);
	}

	public void setCustomTopBar() {
		customTopbar.setMiddleTVString("选择可见范围");
		customTopbar.setSuffixVisiable();
		customTopbar.setMiddleTVColor(Color.BLACK);
		customTopbar.setPreIVBackground(R.drawable.black_ruturn);
		customTopbar.setPreIVOnclick(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if ("公开".equals(result)) {
					scope = 0;
				} else if ("私密".equals(result)) {
					scope = 1;
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("fanwei", scope);
				intent.putExtras(bundle);
				setResult(0, intent);
				finish();
			}
		});
	}

	public void setRedioGroup() {
		if (scope == 0) {
			scope_radiogroup.check(R.id.public_rb);
		} else {
			scope_radiogroup.check(R.id.private_rb);
		}
		scope_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int pisition) {
				if (pisition == public_rb.getId()) {
					result = public_rb.getText().toString();
				} else if (pisition == private_rb.getId()) {
					result = private_rb.getText().toString();
				}
			}
		});
	}
}
