package com.edaoyou.collections.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.LikePagerAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.fragment.LikeGridviewFragment;
import com.edaoyou.collections.fragment.LikeListFragment;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.etsy.XViewPager;

public class LikeActivity extends BaseActivity implements OnClickListener{


	private ArrayList<BaseFragment> mFragments = new ArrayList<BaseFragment>();//fragment集合
	private int mCurrentPage;//当前页
	ImageView like_back_iv,like_list_iv,like_juzhen_iv,like_red_line_iv;//返回按钮,选项卡按钮（瀑布流，网格），红色下划线
	XViewPager like_vp;//滑动切换控件
	TextView like_title_tv;//标题文字
	/**
     * 选项卡下划线长度
     */
    private static int lineWidth;
    
    /**
     * 偏移量
     *         （手机屏幕宽度/3-选项卡长度）/2
     */
    private static int offset = 0;
    
    /**
     * 选项卡总数
     */
    private static final int TAB_COUNT = 2;
    LikePagerAdapter adapter;
    private String mUid;//用户ID
    private String mOtherUid;//对比用的用户ID
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		initFragments();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected int setContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_like;
	}

	@Override
	protected void findViews() {
		// TODO Auto-generated method stub
		mUid = getIntent().getStringExtra(GlobalParams.USER_UID);
		mOtherUid = SharedPreferencesUtils.getInstance(this).getString(GlobalParams.USER_UID);
		like_back_iv = (ImageView)findViewById(R.id.like_back_iv);
		like_list_iv = (ImageView)findViewById(R.id.like_list_iv);
		like_juzhen_iv = (ImageView)findViewById(R.id.like_juzhen_iv);
		like_red_line_iv = (ImageView)findViewById(R.id.like_red_line_iv);
		like_title_tv = (TextView)findViewById(R.id.like_title_tv);
		like_vp = (XViewPager)findViewById(R.id.like_vp);
		if(mUid.equals(mOtherUid)){
			like_title_tv.setText("我喜欢的");
		}else{
			like_title_tv.setText("TA喜欢的");
		}
		initImageView();
	}

	@Override
	protected void setListensers() {
		// TODO Auto-generated method stub
		like_back_iv.setOnClickListener(this);
		like_list_iv.setOnClickListener(this);
		like_juzhen_iv.setOnClickListener(this);
		like_vp.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	private void initFragments() {
		
		mFragments.add(new LikeListFragment(mUid));
		mFragments.add(new LikeGridviewFragment(mUid));
		adapter = new  LikePagerAdapter(getSupportFragmentManager(), mFragments);
		like_vp.setOffscreenPageLimit(mFragments.size());
		like_vp.setAdapter(adapter);
		like_vp.setCurrentItem(0);
	}
	
	private class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + lineWidth;
		@Override
		public void onPageScrollStateChanged(int index) {
			// TODO Auto-generated method stub
			if (this == null) {
				return;
			}
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int postion) {
			// TODO Auto-generated method stub
			mCurrentPage = postion;
			Animation animation = new TranslateAnimation(one*mCurrentPage,one*postion, 0,0);
            animation.setFillAfter(true);
            animation.setDuration(300);
            like_red_line_iv.startAnimation(animation);
            changeTab();
            mCurrentPage = postion;
		}
		
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.like_back_iv:
			finish();
			break;
		case R.id.like_list_iv:
			like_vp.setCurrentItem(0);
			break;
		case R.id.like_juzhen_iv:
			like_vp.setCurrentItem(1);
			break;
		default:
			break;
		}
	}
	 private void initImageView()
	    {
	       
	        //获取图片宽度
//	        lineWidth = BitmapFactory.decodeResource(getResources(),R.drawable.line).getWidth();
		 	
	        DisplayMetrics dm = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
	        //获取屏幕宽度
	        int screenWidth = dm.widthPixels;
	        lineWidth = screenWidth/2;
	        like_red_line_iv.setMinimumWidth(screenWidth/2);
	        Matrix matrix = new Matrix();
	        offset = (int) ((screenWidth/(float)TAB_COUNT - lineWidth)/2);
	        matrix.postTranslate(offset, 0);
	        //设置初始位置
	        like_red_line_iv.setImageMatrix(matrix);
	    }
	 private void changeTab() {
			if (mCurrentPage == 0) {
				like_list_iv.setBackgroundResource(R.drawable.liebiao_highlighted);
				like_juzhen_iv.setBackgroundResource(R.drawable.juzhen_normal);
			} else {
				like_list_iv.setBackgroundResource(R.drawable.liebiao_normal);
				like_juzhen_iv.setBackgroundResource(R.drawable.juzhen_highlighted);
			}
		}

	

	


}
