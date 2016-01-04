package com.edaoyou.collections.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.SetBackGroundActivity;
import com.edaoyou.collections.bean.BackGroundImg.Response.Cover_icon;
import com.edaoyou.collections.bean.BackGroundImg.Response.Cover_url;
import com.edaoyou.collections.utils.LocalCacheUtils;
import com.edaoyou.collections.utils.NetCacheUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.lidroid.xutils.BitmapUtils;

public class SetBackGroundAdapter extends BaseAdapter {

	private Activity mContext;
	private List<Cover_icon> mCoverIconList;
	private List<Cover_url> mCoverUrlList;
	private BitmapUtils mBitmapUtils;
	private String mUid;
	private Handler mHandler;
	private RelativeLayout prgress_bar_rl;

	private NetCacheUtils mNetCacheUtils; // 网络缓存对象
	private LocalCacheUtils mLocalCacheUtils; // 本地缓存对象
	private SharedPreferencesUtils sp = SharedPreferencesUtils.getInstance(mContext);

	public SetBackGroundAdapter(Context context, List<Cover_icon> mCoverIconList, List<Cover_url> mCoverUrlList, BitmapUtils mBitmapUtils,
			Handler handler, RelativeLayout prgress_bar_rl, String mUid) {
		this.mContext = (Activity) context;
		this.mCoverIconList = mCoverIconList;
		this.mBitmapUtils = mBitmapUtils;
		this.mHandler = handler;
		this.mCoverUrlList = mCoverUrlList;
		this.prgress_bar_rl = prgress_bar_rl;
		this.mUid = mUid;

		this.mLocalCacheUtils = new LocalCacheUtils(mContext);
		this.mNetCacheUtils = new NetCacheUtils(mHandler, mLocalCacheUtils);
	}

	@Override
	public int getCount() {
		return mCoverIconList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCoverIconList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_background_img, null);
		}

		viewHolder = getHolder(convertView);
		mBitmapUtils.display(viewHolder.background_iv, mCoverIconList.get(position).url);
		final String backUrl = mCoverUrlList.get(position).url;

		int index = sp.getInt_1(mUid);
		if (position == index) {
			viewHolder.background_position.setVisibility(View.VISIBLE);
		}

		viewHolder.background_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewHolder.background_position.setVisibility(View.GONE);
				prgress_bar_rl.setVisibility(View.VISIBLE);
				mNetCacheUtils.getBitmapFromNet(backUrl);

				Message msg = Message.obtain();
				msg.what = SetBackGroundActivity.ADAPTER_BACK_IMG;
				msg.obj = backUrl;
				msg.arg1 = position;
				mHandler.sendMessage(msg);
			}
		});

		return convertView;
	}

	private ViewHolder getHolder(View convertView) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}
		return viewHolder;
	}

	public class ViewHolder {
		ImageView background_iv;
		ImageView background_position;

		public ViewHolder(View convertView) {
			background_iv = (ImageView) convertView.findViewById(R.id.background_iv);
			background_position = (ImageView) convertView.findViewById(R.id.background_position);
		}
	}
}
