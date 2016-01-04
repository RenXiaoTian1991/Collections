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

public class CancelFaBuPopupWindow extends PopupWindow {

	private TextView cancelpublish_bt, cancel_bt;
	private View mMenuView;

	public CancelFaBuPopupWindow(Activity context, OnClickListener itemsOnClick) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.cancel_fabu, null);

		cancelpublish_bt = (TextView) mMenuView
				.findViewById(R.id.cancelpublish_bt);
		cancel_bt = (TextView) mMenuView.findViewById(R.id.cancel_bt);

		// 取消按钮
		cancel_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// 设置监听按钮
		cancelpublish_bt.setOnClickListener(itemsOnClick);
		cancel_bt.setOnClickListener(itemsOnClick);
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
				int height = mMenuView.findViewById(R.id.pop_linearlayout)
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

	}

}
