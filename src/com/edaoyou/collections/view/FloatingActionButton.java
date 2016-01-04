package com.edaoyou.collections.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

public class FloatingActionButton extends Button {
	private int mDuration = 200;
	private boolean mVisible = true;
	private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

	public FloatingActionButton(Context context) {
		this(context, null);
	}

	public FloatingActionButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		hide(false);
	}

	private int getMarginBottom() {
		int marginBottom = 0;
		final ViewGroup.LayoutParams layoutParams = getLayoutParams();
		if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
			marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
		}
		return marginBottom;
	}

	public void setFloatingActionText(String text) {
		setText(text);
	}

	public boolean isShow() {
		return mVisible;
	}

	public void show() {
		show(true);
	}

	public void hide() {
		hide(true);
	}

	public void setDuration(int duration) {
		this.mDuration = duration;
	}

	public void show(boolean animate) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			setVisibility(View.VISIBLE);
		} else {
			toggle(true, animate, false);
		}
	}

	public void hide(boolean animate) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			setVisibility(View.GONE);
		} else {
			toggle(false, animate, false);
		}
	}

	@SuppressLint("NewApi")
	private void toggle(final boolean visible, final boolean animate, boolean force) {
		if (mVisible != visible || force) {
			mVisible = visible;
			int height = getHeight();
			if (height == 0 && !force) {
				ViewTreeObserver vto = getViewTreeObserver();
				if (vto.isAlive()) {
					vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
						@Override
						public boolean onPreDraw() {
							ViewTreeObserver currentVto = getViewTreeObserver();
							if (currentVto.isAlive()) {
								currentVto.removeOnPreDrawListener(this);
							}
							toggle(visible, animate, true);
							return true;
						}
					});
					return;
				}
			}
			int translationY = visible ? 0 : height + getMarginBottom();
			if (animate) {
				animate().setInterpolator(mInterpolator).setDuration(mDuration).translationY(translationY);
			} else {
				setTranslationY(translationY);
			}
		}
	}
}
