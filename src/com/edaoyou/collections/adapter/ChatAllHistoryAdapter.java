package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Bean.ChatUserListData;
import com.edaoyou.collections.fragment.ChatAllHistoryFragment;
import com.edaoyou.collections.utils.SmileUtils;
import com.edaoyou.collections.utils.TimeUitl;
import com.etsy.BaseSwipeAdapter;
import com.etsy.SwipeLayout;
import com.lidroid.xutils.BitmapUtils;

public class ChatAllHistoryAdapter extends BaseSwipeAdapter {

	private Context mContext;
	private BitmapUtils mBitmapUtils;
	private List<ChatUserListData> mChatHistoryListDatas;
	private Handler mHandler;

	public ChatAllHistoryAdapter(Context context, BitmapUtils bitmapUtils, List<ChatUserListData> chatHistoryListDatas, Handler handler) {
		this.mContext = context;
		this.mBitmapUtils = bitmapUtils;
		this.mChatHistoryListDatas = chatHistoryListDatas;
		this.mHandler = handler;
	}

	public void setData(List<ChatUserListData> chatHistoryListDatas) {
		this.mChatHistoryListDatas = chatHistoryListDatas;
	}

	@Override
	public int getCount() {
		if (mChatHistoryListDatas == null) {
			return 0;
		}
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

	/**
	 * SwipeLayout的布局id
	 */
	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.chat_swipe;
	}

	/**
	 * 对控件的填值操作独立出来了，我们可以在这个方法里面进行item的数据赋值
	 */
	@Override
	public void fillValues(int position, View convertView) {
		ImageView chat_history_avatar = (ImageView) convertView.findViewById(R.id.chat_history_avatar);
		TextView chat_history_unread_msg_number = (TextView) convertView.findViewById(R.id.chat_history_unread_msg_number);
		TextView chat_history_name = (TextView) convertView.findViewById(R.id.chat_history_name);
		TextView chat_history_message = (TextView) convertView.findViewById(R.id.chat_history_message);
		TextView chat_history_name_time = (TextView) convertView.findViewById(R.id.chat_history_name_time);

		ChatUserListData chatHistoryListData = mChatHistoryListDatas.get(position);
		String nick = chatHistoryListData.getNick();
		String historyMessage = chatHistoryListData.getTxt();
		String time = chatHistoryListData.getTime();
		String new_message = chatHistoryListData.getNew_message();
		if ("1".equals(new_message)) {
			chat_history_unread_msg_number.setVisibility(View.VISIBLE);
		} else {
			chat_history_unread_msg_number.setVisibility(View.GONE);
		}
		chat_history_name.setText(nick);

		Spannable span = SmileUtils.getSmiledText(mContext, historyMessage);
		// 设置内容
		chat_history_message.setText(span, BufferType.SPANNABLE);
		String diffTime = TimeUitl.getDiffTime(mContext, time);
		chat_history_name_time.setText(diffTime);
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_user_item, parent, false);
		final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
		// 点击更多
		view.findViewById(R.id.chat_more_tv).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Message msg = Message.obtain();
				msg.what = ChatAllHistoryFragment.MSG_MORE;
				msg.obj = position;
				mHandler.sendMessage(msg);
				swipeLayout.close();
			}
		});
		// 点击删除
		view.findViewById(R.id.chat_delete_tv).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Message msg = Message.obtain();
				msg.what = ChatAllHistoryFragment.MSG_DELETE_CHAT;
				msg.obj = position;
				mHandler.sendMessage(msg);
				swipeLayout.close();
			}
		});
		return view;
	}
}
