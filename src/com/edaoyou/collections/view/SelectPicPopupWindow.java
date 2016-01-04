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

public class SelectPicPopupWindow extends PopupWindow {

	private TextView btn_take_photo, btn_pick_photo, btn_cancel;
	private View mMenuView;

	public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.add_head_picture, null);

		btn_take_photo = (TextView) mMenuView.findViewById(R.id.take_photo_bt);
		btn_pick_photo = (TextView) mMenuView.findViewById(R.id.pick_photo_bt);
		btn_cancel = (TextView) mMenuView.findViewById(R.id.cancel_bt);

		// 取消按钮
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// 设置监听按钮
		btn_take_photo.setOnClickListener(itemsOnClick);
		btn_pick_photo.setOnClickListener(itemsOnClick);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的高和宽
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setWidth(LayoutParams.MATCH_PARENT);

		// 设置SelectPicPopupWindow的弹出窗体可悲点击
		this.setFocusable(true);

		// 设置SelectPicPopupWindow的弹出窗体的动画效果
		// this.setAnimationStyle(R.style.animBottom);

		// 实例化一个ColorDrawable的颜色为半透明
		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
		// 设置SelectPicPopupWindow的弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener的监听器判断获取触屏位置如果在选择框外面就销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_linearlayout).getTop();
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
	}
}
