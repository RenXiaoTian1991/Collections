package com.edaoyou.collections.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.utils.Util;

public class ReportPopWin extends PopupWindow {
	private View reportPopWin;
	private Context mcontext;
	private TextView report_cancle;
	private TextView report_daotu;
	private TextView report_badPicture;
	private TextView report_rubbishinfo;
	private TextView report_badlanguage;
	private TextView report_copyrightdisputes;

	public ReportPopWin(Activity context) {
		mcontext = context;
		createPopWindow();
	}

	private void createPopWindow() {
		LayoutInflater inflater = (LayoutInflater) mcontext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		reportPopWin = inflater.inflate(R.layout.reportselect, null);
		report_daotu = (TextView) reportPopWin.findViewById(R.id.report_stolen_pic_tv);// 盗图
		report_badPicture = (TextView) reportPopWin
				.findViewById(R.id.report_badPicture_tv);// 不良图片
		report_rubbishinfo = (TextView) reportPopWin
				.findViewById(R.id.report_rubbishinfo_tv);// 垃圾信息
		report_badlanguage = (TextView) reportPopWin
				.findViewById(R.id.report_badlanguage_tv);// 不良言论
		report_copyrightdisputes = (TextView) reportPopWin
				.findViewById(R.id.report_copyright_disputes_tv);// 版权纠纷
		report_cancle = (TextView) reportPopWin
				.findViewById(R.id.report_cancle);// 取消

		// 设置SelectPicPopupWindow的View
		this.setContentView(reportPopWin);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setWidth(LayoutParams.MATCH_PARENT);
		reportPopWin.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = reportPopWin.findViewById(
						R.id.pop_photodetaillayout).getHeight();
				int popHeight = Util
						.getDisplayHeight((Activity) mcontext) - height;
				// 得到触摸位置的距离底部的高
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < popHeight) {
						ReportPopWin.this.dismiss();
					}
				}
				return true;
			}
		});
		report_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReportPopWin.this.dismiss();
			}
		});
	}

	public void setPopOnClick(OnClickListener clickListener) {
		report_daotu.setOnClickListener(clickListener);
		report_badPicture.setOnClickListener(clickListener);
		report_rubbishinfo.setOnClickListener(clickListener);
		report_badlanguage.setOnClickListener(clickListener);
		report_copyrightdisputes.setOnClickListener(clickListener);
	}
}
