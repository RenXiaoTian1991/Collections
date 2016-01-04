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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.edaoyou.collections.R;

public class DeleteCommentPopWin extends PopupWindow {
	private View genderView;
	private TextView deletecomment, canceldeletecomment;

	public DeleteCommentPopWin(Activity context) {
		createPopWindow(context);
	}

	public void createPopWindow(Activity context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		genderView = inflater.inflate(R.layout.deletecommentlayout, null);
		deletecomment = (TextView) genderView.findViewById(R.id.deletecomment_tv);
		canceldeletecomment = (TextView) genderView
				.findViewById(R.id.canceldeletecomment);

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
		canceldeletecomment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeleteCommentPopWin.this.dismiss();
			}
		});
	}

	public void setPopOnClick(OnClickListener clickListener) {
		deletecomment.setOnClickListener(clickListener);
	}
}
