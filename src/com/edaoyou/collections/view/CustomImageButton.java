package com.edaoyou.collections.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;

/**
 * 
 * @Description: 自定义ImageButton，左边图片，右边文字
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-9-19 下午7:21:54 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class CustomImageButton extends LinearLayout {
	private ImageView iv;
	private TextView tv;

	public CustomImageButton(Context context) {
		this(context, null);
	}

	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.zdy_imagebutton, this,
				true);
		iv = (ImageView) findViewById(R.id.iv);
		tv = (TextView) findViewById(R.id.tv);

	}

	public ImageView getIv() {
		return iv;
	}

	public TextView getTv() {
		return tv;
	}

	/**
	 * 设置图片资源
	 */
	public void setImageResource(int resId) {
		getIv().setImageResource(resId);
	}

	/**
	 * 设置显示的文字
	 */
	public void setTextViewText(String text) {
		getTv().setText(text);
	}

}