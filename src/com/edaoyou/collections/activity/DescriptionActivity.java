package com.edaoyou.collections.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.edaoyou.collections.R;
import com.edaoyou.collections.view.CustomTopbar;

public class DescriptionActivity extends Activity implements OnClickListener {
	private EditText add_description_tv;
	private CustomTopbar customTopbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_description);

		findAllViewsById();
		setCustomTopBar();
		setClickListeners();

		if (getIntent().getStringExtra("desc") != null) {
			add_description_tv.setText(getIntent().getStringExtra("desc")
					.toString());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topBar_pre_textview:
			finish();
			break;
		case R.id.topBar_suffix_textview:

			if (TextUtils.isEmpty(add_description_tv.getText())) {
				Toast.makeText(DescriptionActivity.this, "您还没有输入任何内容！",
						Toast.LENGTH_SHORT).show();
				
				return;
			}
			goToNextActivity();
			break;
		default:
			break;
		}
	}

	public void findAllViewsById() {
		add_description_tv = (EditText) findViewById(R.id.add_description_et);
		customTopbar = (CustomTopbar) findViewById(R.id.customTopbar);
	}

	public void setCustomTopBar() {
		customTopbar.setPreTVString("取消");
		customTopbar.setMiddleTVString("添加描述");
		customTopbar.setMiddleTVColor(Color.BLACK);
		customTopbar.setSuffixTVString("完成");
	}

	public void setClickListeners() {
		customTopbar.setPreTVOnclick(this);
		customTopbar.setSuffixTVOnclick(this);
	}

	public void goToNextActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("desc", add_description_tv.getText().toString());
		intent.putExtras(bundle);
		setResult(0, intent);
		finish();
	}
}
