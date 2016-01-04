package com.edaoyou.collections.utils;

import com.edaoyou.collections.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DialogUtil {
	private static AlertDialog alertDialog;

	/**
	 * 显示dialog
	 * 
	 * @param context
	 * @param view
	 * @param width
	 * @param height
	 */
	public static void showDialog(Context context, View view, int width, int height) {
		if(alertDialog ==null){
			alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.show();
			alertDialog.setCanceledOnTouchOutside(false);
			Window window = alertDialog.getWindow();
			window.setGravity(Gravity.CENTER);
			window.setLayout(width, height);
			window.setContentView(view);
		}
		
	}

	/**
	 * 关闭dialog
	 */
	public static void dismiss() {
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
			alertDialog = null;
		}
	}

	/**
	 * 显示聊天提示的dialog
	 * 
	 * @param context
	 */
	public static void showChaHintDialog(Context context,int resouId) {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_chat_hint, null);
		view.findViewById(R.id.chat_hint_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		TextView textView = (TextView) view.findViewById(R.id.chat_hint_txt_tv);
		textView.setText(resouId);
		int width = (int) context.getResources().getDimension(R.dimen.search_chat_hint_widht);
		int height = (int) context.getResources().getDimension(R.dimen.search_chat_hint_height);
		showDialog(context, view, width, height);
	}
}
