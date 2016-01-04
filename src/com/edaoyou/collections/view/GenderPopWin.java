package com.edaoyou.collections.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.edaoyou.collections.R;

public class GenderPopWin extends PopupWindow {
	private View genderView;
	private RadioGroup rg_gender;
	private RadioButton rb_female, rb_male;
	private Button cancel, submit;
	private String gender;

	public GenderPopWin(Activity context) {
		createPopWindow(context);
	}

	public void createPopWindow(Activity context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		genderView = inflater.inflate(R.layout.genderselect, null);
		cancel = (Button) genderView.findViewById(R.id.btn_back);
		submit = (Button) genderView.findViewById(R.id.btn_submit);
		rg_gender = (RadioGroup) genderView.findViewById(R.id.rg_gender);
		rb_female = (RadioButton) genderView.findViewById(R.id.rb_female);
		rb_male = (RadioButton) genderView.findViewById(R.id.rb_male);

		this.setContentView(genderView);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		// 实例化一个ColorDrawable的颜色为半透明
		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
		// 设置SelectPicPopupWindow的弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener的监听器判断获取触屏位置如果在选择框外面就销毁弹出框
		genderView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = genderView.findViewById(R.id.pop_genderlayout)
						.getTop();
				// 得到触摸位置的距离底部的高
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
		rg_gender.check(R.id.rb_female);

	}

	public void setPopOnClick(OnClickListener clickListener) {
		cancel.setOnClickListener(clickListener);
		submit.setOnClickListener(clickListener);
	}

	public String getGender() {
		return gender;
	}

	public RadioGroup getRg_gender() {
		return rg_gender;
	}

	public RadioButton getRb_female() {
		return rb_female;
	}

	public RadioButton getRb_male() {
		return rb_male;
	}

}
