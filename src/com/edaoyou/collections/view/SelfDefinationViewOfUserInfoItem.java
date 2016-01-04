package com.edaoyou.collections.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;

public class SelfDefinationViewOfUserInfoItem extends RelativeLayout {

	private ImageView head_iv;
	private TextView user_nick_tv;
	private TextView commentds_contents_tv;
	private TextView comment_time_tv;

	public SelfDefinationViewOfUserInfoItem(Context context) {
		super(context);
		init();
	}

	public SelfDefinationViewOfUserInfoItem(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SelfDefinationViewOfUserInfoItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化操作
	 */
	private void init() {
		View view = View.inflate(getContext(),R.layout.home_userinfodisplayitem, this);
		head_iv = (ImageView) view.findViewById(R.id.head_iv);
		user_nick_tv = (TextView) view.findViewById(R.id.user_nick_tv);
		commentds_contents_tv = (TextView) view.findViewById(R.id.commentds_contents_tv);
		comment_time_tv = (TextView) view.findViewById(R.id.comment_time_tv);
	}

	/**
	 * 暴露了方法给上层代码使用
	 * 
	 * @param title
	 */

	public void setHeadimage(Bitmap headimage) {
		this.head_iv.setImageBitmap(headimage);
	}

	public void setUsernick(String usernick) {
		this.user_nick_tv.setText(usernick);
	}

	public void setCommentds_contentsText(String commentds_contents) {
		this.commentds_contents_tv.setText(commentds_contents);
	}

	public void setPublicTimeText(String comment_time) {
		this.comment_time_tv.setText(comment_time);
	}

}
