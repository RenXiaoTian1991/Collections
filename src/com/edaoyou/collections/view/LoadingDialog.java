package com.edaoyou.collections.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import android.widget.TextView;

import com.edaoyou.collections.R;
/**
 * 
 * @Description: 加载提示框
 * @author rongwei.mei mayroewe@live.cn 
 * @date 2014-11-17 下午3:09:45  
 * Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class LoadingDialog extends Dialog{
	private Context mContext;
	private LayoutInflater inflater;
	private LayoutParams lp;
	private TextView loadtext;

	public LoadingDialog(Context context) {
		super(context, R.style.Dialog);
		
		this.mContext = context;
		
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.loadingdialog, null);
		loadtext = (TextView) layout.findViewById(R.id.loading_text);
		setContentView(layout);
		
		// 设置window属性
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
//		lp.dimAmount = 0; // 去背景遮盖
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);

	}

	public void setLoadText(String content){
		loadtext.setText(content);
	}
	public void hide(){
		if(this != null && this.isShowing()){
			this.dismiss();
		}
	}
	
}