package com.edaoyou.collections.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.utils.Util;

public class SharePopWin extends PopupWindow {
	private ImageView pop_photodetailshare_friend, pop_photodetailshare_sina,
			pop_photodetailshare_weichat;
	private TextView sharePopWin_Report, sharePopWin_cancel;
	private View sharePopWinView;
	private Context mcontext;

	public SharePopWin(Activity context) {
		mcontext = context;
		createPopWindow();
	}

	private void createPopWindow() {
		LayoutInflater inflater = (LayoutInflater) mcontext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		sharePopWinView = inflater.inflate(R.layout.shareselect, null);
		pop_photodetailshare_friend = (ImageView) sharePopWinView
				.findViewById(R.id.pop_photodetailshare_friend);
		pop_photodetailshare_sina = (ImageView) sharePopWinView
				.findViewById(R.id.pop_photodetailshare_sina);
		pop_photodetailshare_weichat = (ImageView) sharePopWinView
				.findViewById(R.id.pop_photodetailshare_weichat);
		sharePopWin_Report = (TextView) sharePopWinView
				.findViewById(R.id.share_Report_tv);
		sharePopWin_cancel = (TextView) sharePopWinView
				.findViewById(R.id.sharePopWin_cancel);
		// 设置SelectPicPopupWindow的View
		this.setContentView(sharePopWinView);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setWidth(LayoutParams.MATCH_PARENT);
		// this.setFocusable(true);
		// // 实例化一个ColorDrawable的颜色为半透明
		// ColorDrawable dw = new ColorDrawable(Color.WHITE);
		// // 设置SelectPicPopupWindow的弹出窗体的背景
		// this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener的监听器判断获取触屏位置如果在选择框外面就销毁弹出框
		sharePopWinView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = sharePopWinView.findViewById(
						R.id.pop_photodetaillayout).getHeight();
				int popHeight = Util
						.getDisplayHeight((Activity) mcontext) - height;
				// 得到触摸位置的距离底部的高
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < popHeight) {
						SharePopWin.this.dismiss();
					}
				}
				return true;
			}
		});
		sharePopWin_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharePopWin.this.dismiss();
			}
		});
	}

	public void setReportVisiable() {
		sharePopWin_Report.setVisibility(View.GONE);
	}

	public void reported() {
		sharePopWin_Report.setText("已举报");
		sharePopWin_Report.setClickable(false);
	}

	public void setPopOnClick(OnClickListener clickListener) {
		if (sharePopWin_Report.getVisibility() == View.VISIBLE) {
			sharePopWin_Report.setOnClickListener(clickListener);
		}
		pop_photodetailshare_friend.setOnClickListener(clickListener);
		pop_photodetailshare_sina.setOnClickListener(clickListener);
		pop_photodetailshare_weichat.setOnClickListener(clickListener);
	}
}
