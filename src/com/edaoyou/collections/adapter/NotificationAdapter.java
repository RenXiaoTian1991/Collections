package com.edaoyou.collections.adapter;

import java.util.List;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Bean.NotificationData;
import com.edaoyou.collections.fragment.NotificationFragment;
import com.edaoyou.collections.utils.TimeUitl;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotificationAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder viewHolder;
	private List<NotificationData> mChatHistoryListDatas;
	private BitmapUtils mBitmapUtils;
	private Handler mHandler;

	public NotificationAdapter(Context context, List<NotificationData> notificationDatas, BitmapUtils mBitmapUtils, Handler mHandler) {
		super();
		this.mContext = context;
		this.mChatHistoryListDatas = notificationDatas;
		this.mBitmapUtils = mBitmapUtils;
		this.mHandler = mHandler;
	}

	public void setData(List<NotificationData> notificationDatas) {
		this.mChatHistoryListDatas = notificationDatas;
	}

	@Override
	public int getCount() {
		return mChatHistoryListDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mChatHistoryListDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_notification_item, null);
			viewHolder = new ViewHolder();
			viewHolder.chat_history_avatar_iv = (CirclePortrait) convertView.findViewById(R.id.chat_history_avatar_iv);
			viewHolder.notification_name_tv = (TextView) convertView.findViewById(R.id.notification_name_tv);
			viewHolder.notification_txt_tv = (TextView) convertView.findViewById(R.id.notification_txt_tv);
			viewHolder.notification_time_tv = (TextView) convertView.findViewById(R.id.notification_time_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		NotificationData notificationData = mChatHistoryListDatas.get(position);
		String nick = notificationData.getNick();
		String time = notificationData.getTime();
		String txt = notificationData.getTxt();
		viewHolder.notification_name_tv.setText(nick);
		viewHolder.notification_txt_tv.setText(txt);
		String diffTime = TimeUitl.getDiffTime(mContext, time);
		viewHolder.notification_time_tv.setText(diffTime);
		mBitmapUtils.display(viewHolder.chat_history_avatar_iv, notificationData.getAvatar());
		viewHolder.chat_history_avatar_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Message msg = Message.obtain();
				msg.what = NotificationFragment.MSG_GOTO_PERSONALHOMEPAGE;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		});
		return convertView;
	}

	public class ViewHolder {
		public CirclePortrait chat_history_avatar_iv;
		public TextView notification_name_tv;
		public TextView notification_txt_tv;
		public TextView notification_time_tv;
	}

}
