package com.edaoyou.collections.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.edaoyou.collections.R;

/**
 * 
 * @Description: 自定义拍照模块：相框以及文字
 * @author rongwei.mei mayroewe@live.cn
 * @date 2014-9-24 下午2:57:36 Copyright © 北京易道游网络技术有限公司 版权所有
 */
public class CustomPicFrame extends RelativeLayout {
	private RelativeLayout rl_frame;
	private ImageView iv_pic, iv_frame;

	public CustomPicFrame(Context context) {
		super(context);
	}

	public CustomPicFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.zdy_imageframe, this,
				true);
		rl_frame = (RelativeLayout) findViewById(R.id.rl_frame);
		iv_frame = (ImageView) findViewById(R.id.ivRadFrame);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
	}

	/**
	 * 红色指示框的点击事件
	 * 
	 * @param listener
	 */
	public void setOnMyClicklistener(OnClickListener listener) {
		iv_frame.setOnClickListener(listener);
	};

	/**
	 * 设置红色指示框
	 */
	public void setBackground(int resId) {
		iv_frame.setBackgroundResource(resId);
	}

	/**
	 * 设置红色指示框
	 */
	public void setRelativeBackground(int resId) {
		rl_frame.setBackgroundResource(resId);
	}

	/**
	 * 设置红色指示框位移
	 */
	public void setImageMatrix(Matrix matrix) {
		iv_frame.setImageMatrix(matrix);
	}

	/**
	 * 设置照片
	 */
	@SuppressWarnings("deprecation")
	public void setImageBackgroundResource(Drawable drawable) {
		// 用旧方法适配低版本报错的问题.
		iv_pic.setBackgroundDrawable(drawable);
	}

	/**
	 * 设置照片是否显示
	 */
	public void setImageVisibility(int visibility) {
		iv_pic.setVisibility(visibility);
	}
}
