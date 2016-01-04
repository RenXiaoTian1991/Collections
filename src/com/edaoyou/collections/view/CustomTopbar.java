package com.edaoyou.collections.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;

public class CustomTopbar extends RelativeLayout {

	private RelativeLayout topBar_relativelayout;
	private ImageView topBar_pre_iv;
	private TextView topBar_pre_textview;
	private TextView topBar_middle_textview;
	private LinearLayout topBar_middle_linearlayout;
	private ImageView topBar_suffix_imageview;
	private TextView topBar_suffix_textview;
	private TextView middle_numerator_tv;
	private TextView middle_denominator_tv;

	public CustomTopbar(Context context) {
		super(context);
		init();
	}

	public CustomTopbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomTopbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化操作
	 */
	private void init() {
		View view = View.inflate(getContext(), R.layout.selfdefineview, this);

		topBar_relativelayout = (RelativeLayout) view
				.findViewById(R.id.topBar_relativelayout);
		topBar_pre_iv = (ImageView) view.findViewById(R.id.topBar_pre_iv);
		topBar_pre_textview = (TextView) view
				.findViewById(R.id.topBar_pre_textview);
		// topbar_middle_image = (ImageView) view
		// .findViewById(R.id.topbar_middle_image);
		topBar_middle_textview = (TextView) view
				.findViewById(R.id.topBar_middle_textview);
		topBar_middle_linearlayout = (LinearLayout) view
				.findViewById(R.id.topBar_middle_linearlayout);
		middle_numerator_tv = (TextView) findViewById(R.id.middle_numerator_tv);
		middle_denominator_tv = (TextView) findViewById(R.id.middle_denominator_tv);

		topBar_suffix_textview = (TextView) view
				.findViewById(R.id.topBar_suffix_textview);
		topBar_suffix_imageview = (ImageView) view
				.findViewById(R.id.topBar_suffix_iv);

	}

	/**
	 * 设置顶部栏的背景
	 * 
	 * @param color
	 */
	public void setTopbarBackground(int color) {
		topBar_relativelayout.setBackgroundColor(color);
	}

	/**
	 * 在返回键的位置（左侧位置），如果是TextView的话， 并设置TextView的监听器
	 * 
	 * @param listener
	 *            监听器
	 */
	public void setPreTVOnclick(OnClickListener listener) {
		topBar_pre_iv.setVisibility(View.GONE);
		topBar_pre_textview.setOnClickListener(listener);
	}

	/**
	 * 在返回键的位置（左侧位置），如果是TextView的话， 并设置TextView的文本内容
	 * 
	 * @param preString
	 *            要设置的textview的文本内容
	 */
	public void setPreTVString(String preString) {
		topBar_pre_iv.setVisibility(View.GONE);
		topBar_pre_textview.setText(preString);
	}

	/**
	 * 在返回键的位置（左侧位置），如果是ImageView的话，并设置ImageView的监听器
	 * 
	 * @param listener
	 *            监听器
	 */
	public void setPreIVOnclick(OnClickListener listener) {
		topBar_pre_textview.setVisibility(View.GONE);
		topBar_pre_iv.setOnClickListener(listener);
	}

	/**
	 * 在返回键的位置（左侧位置），如果是ImageView的话，并设置ImageView的Background
	 * 
	 * @param resId
	 *            背景图片的资源id
	 */
	public void setPreIVBackground(int resId) {
		topBar_pre_textview.setVisibility(View.GONE);
		topBar_pre_iv.setImageResource(resId);
	}

	/**
	 * 在返回键的位置（左侧位置），如果不存在子View的话， 并设置此处的所有子View不可见
	 */
	public void setPreVisiable() {
		topBar_pre_iv.setVisibility(View.GONE);
		topBar_pre_textview.setVisibility(View.GONE);
	}

	/**
	 * 在标题位置（中间位置），如果是TextView的话，并设置TextView的文本内容
	 * 
	 * @param preString
	 *            要设置的textview的文本内容
	 */
	public void setMiddleTVString(String middleString) {
		topBar_middle_linearlayout.setVisibility(View.GONE);
		topBar_middle_textview.setText(middleString);
	}

	/**
	 * 在标题位置（中间位置），如果是TextView的话，并设置TextView的文本内容
	 * 
	 * @param resId
	 *            要设置的textview的文本内容的资源id
	 */
	public void setMiddleTVResIdString(int resId) {
		topBar_middle_linearlayout.setVisibility(View.GONE);
		topBar_middle_textview.setText(resId);
	}

	/**
	 * 在标题位置（中间位置），如果是TextView的话，并设置TextView的文本颜色
	 * 
	 * @param preString
	 *            要设置的textview的文本颜色
	 */
	public void setMiddleTVColor(int color) {
		topBar_middle_linearlayout.setVisibility(View.GONE);
		topBar_middle_textview.setTextColor(color);
	}

	/**
	 * 在标题位置（中间位置），如果是LinearLayout的话，并设置LinearLayout的子布局middle_numerator（
	 * 分子Numerator）文本内容
	 * 
	 * @param middleNumerator
	 *            LinearLayout的子布局middle_numerator（分子Numerator）文本内容
	 */
	public void setMiddleLLNumerator(String middleNumerator) {
		topBar_middle_textview.setVisibility(View.GONE);
		middle_numerator_tv.setText(middleNumerator);
	}

	/**
	 * 在标题位置（中间位置），如果是LinearLayout的话，并设置LinearLayout的子布局middle_denominator（
	 * 分母Denominator）文本内容
	 * 
	 * @param preString
	 *            LinearLayout的子布局middle_denominator（分母Denominator）文本内容
	 */
	public void setMiddleLLDenominator(String middleDenominator) {
		topBar_middle_textview.setVisibility(View.GONE);
		middle_denominator_tv.setText(middleDenominator);
	}

	/**
	 * 在标题位置（中间位置），如果不存在子View的话， 并设置此处的所有子View不可见
	 */
	public void setMiddleVisiable() {
		topBar_middle_textview.setVisibility(View.GONE);
		middle_denominator_tv.setVisibility(View.GONE);
	}

	/**
	 * 在完成位置（右侧位置），如果是TextView的话，设置TextView的监听器
	 * 
	 * @param listener
	 *            监听器
	 */
	public void setSuffixTVOnclick(OnClickListener listener) {
		topBar_suffix_imageview.setVisibility(View.GONE);
		topBar_suffix_textview.setOnClickListener(listener);
	}

	/**
	 * 在完成位置（右侧位置），如果是TextView的话，设置TextView的文本内容
	 * 
	 * @param suffixString
	 *            TextView的文本内容
	 */
	public void setSuffixTVString(String suffixString) {
		topBar_suffix_imageview.setVisibility(View.GONE);
		topBar_suffix_textview.setText(suffixString);
	}

	/**
	 * 在完成位置（右侧位置），如果是TextView的话，设置TextView的文本颜色
	 * 
	 * @param suffixString
	 *            TextView的文本颜色
	 */
	public void setSuffixTVColor(int color) {
		topBar_suffix_imageview.setVisibility(View.GONE);
		topBar_suffix_textview.setTextColor(color);
	}

	/**
	 * 在完成位置（右侧位置），如果是Imageview的话，设置ImageView的监听器
	 * 
	 * @param listener
	 *            监听器
	 */
	public void setSuffixIVOnclick(OnClickListener listener) {
		topBar_suffix_textview.setVisibility(View.GONE);
		topBar_suffix_imageview.setOnClickListener(listener);
	}

	/**
	 * 在完成位置（右侧位置），如果是Imageview的话，设置ImageView的Background
	 * 
	 * @param listener
	 *            监听器
	 */
	public void setSuffixIVBackground(int resId) {
		topBar_suffix_textview.setVisibility(View.GONE);
		topBar_suffix_imageview.setImageResource(resId);
	}

	/**
	 * 在标题位置（中间位置），如果不存在子View的话， 并设置此处的所有子View不可见
	 */
	public void setSuffixVisiable() {
		topBar_suffix_textview.setVisibility(View.GONE);
		topBar_suffix_imageview.setVisibility(View.GONE);
	}
}
