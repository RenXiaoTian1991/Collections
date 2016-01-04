package com.edaoyou.collections.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Guide.Response.Data;
import com.edaoyou.collections.fragment.ChoicenessAndFriendsFragment;
import com.edaoyou.collections.view.FilletLayout;
import com.lidroid.xutils.BitmapUtils;

public class HomeViewFlowAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;
	private BitmapUtils mBitmapUtils;
	private Handler mHandler;
	private ArrayList<Data> mDatas;

	public HomeViewFlowAdapter(Context context, BitmapUtils bitmapUtils, Handler handler, ArrayList<Data> datas) {
		this.mDatas = datas;
		this.mBitmapUtils = bitmapUtils;
		this.mHandler = handler;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Data> datas) {
		this.mDatas = datas;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position % (mDatas.size()));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int fPosition = position % mDatas.size();
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_xgirdview_header_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.image = (FilletLayout) convertView.findViewById(R.id.gallery_img);
			mViewHolder.linlang_logo_iv = (ImageView) convertView.findViewById(R.id.linlang_logo_iv);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		Data data = mDatas.get(fPosition);
		String imgUrl = "";
		if (TextUtils.isEmpty(data.getPhoto())) {
			imgUrl = data.getUrl1();
		} else {
			imgUrl = data.getPhoto();
		}
		mBitmapUtils.display(mViewHolder.image, imgUrl);
		
		if (!TextUtils.isEmpty(data.getUrl3())) {
			mViewHolder.linlang_logo_iv.setVisibility(View.VISIBLE);
			mBitmapUtils.display(mViewHolder.linlang_logo_iv, data.getUrl3());
		} else {
			mViewHolder.linlang_logo_iv.setVisibility(View.GONE);
		}
		
		mViewHolder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Message msg = Message.obtain();
				msg.what = ChoicenessAndFriendsFragment.MSG_HOME_HEADER_CLICK;
				msg.obj = fPosition;
				mHandler.sendMessage(msg);
			}
		});
		return convertView;
	}

	public class ViewHolder {
		public FilletLayout image;
		public ImageView linlang_logo_iv;
	}
}
