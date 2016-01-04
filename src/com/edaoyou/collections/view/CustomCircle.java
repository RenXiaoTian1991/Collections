package com.edaoyou.collections.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public abstract class CustomCircle extends ImageView {
	private static final Xfermode MASK_XFERMODE;
	private Bitmap mask;
	private Paint paint;

	static {
		PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;
		MASK_XFERMODE = new PorterDuffXfermode(localMode);
	}

	public CustomCircle(Context paramContext) {
		super(paramContext);
	}

	public CustomCircle(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CustomCircle(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public abstract Bitmap createMask();

	protected void onDraw(Canvas paramCanvas) {
		/*
		 * 声明一个可画的对象，并从ImageView对象中 获取一个具体的对象（没有即为null），即实例化
		 */
		Drawable localDrawable = getDrawable();
		// 如果我们要画的图片为空，那么就没有继续画圆环的必要了，返回
		if (localDrawable == null)
			return;
		// 如果我们要画的图片的宽度或高度不符合要求，也没有画的必要
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		/*
		 * 同时应该注意到9Patch的图片也是有边框的，那么我们要加边框的 图片的应用类是继承了 NinePathcDrawable
		 * 的话，那么人家本身 有边框，我们也就没有加圆形边框的必要了
		 */
		if (localDrawable.getClass() == NinePatchDrawable.class) {
			return;
		}
		/*
		 * 参考Google 官方文档，Android 画一个view 要经历两个过程 第一个是计算measure ,即计算这个view
		 * 所需要多大的尺寸空间 第二个是布局layout ,即要根据开发者的要求来放在哪个位置 所以，要先测量
		 */
		try {
			if (this.paint == null) {
				Paint localPaint1 = new Paint();
				this.paint = localPaint1;
				this.paint.setFilterBitmap(false);
				Paint localPaint2 = this.paint;
				Xfermode localXfermode1 = MASK_XFERMODE;
				localPaint2.setXfermode(localXfermode1);
			}
			float f1 = getWidth();
			float f2 = getHeight();
			int i = paramCanvas.saveLayer(0.0F, 0.0F, f1, f2, null, 31);
			int j = getWidth();
			int k = getHeight();
			localDrawable.setBounds(0, 0, j, k);
			localDrawable.draw(paramCanvas);
			if ((this.mask == null) || (this.mask.isRecycled())) {
				Bitmap localBitmap1 = createMask();
				this.mask = localBitmap1;
			}
			Bitmap localBitmap2 = this.mask;
			Paint localPaint3 = this.paint;
			paramCanvas.drawBitmap(localBitmap2, 0.0F, 0.0F, localPaint3);
			paramCanvas.restoreToCount(i);
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
}