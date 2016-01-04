package com.edaoyou.collections.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.SearchFriendActivity;
import com.edaoyou.collections.bean.Bean.Contact;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;

public class SearchFriendAdapter extends BaseAdapter {

	private Context mContext;
	private List<Contact> mContacts;
	private ViewHolder viewHolder;
	private BitmapUtils mBitmapUtils;
	private Handler mHandler;
	private static final int STATE_ADD = 0;// 无关系
	private static final int STATE_I_LIKE_HE = 1;// 我关注了他(已关注)
	private static final int STATE_HE_LIKE_ME = 2;// 2他关注了我(我的粉丝)
	private static final int STATE_MUTUAL = 3;// 互相关注

	public SearchFriendAdapter(Context context, BitmapUtils bitmapUtils, Handler handler, List<Contact> contacts) {
		this.mContext = context;
		this.mContacts = contacts;
		this.mHandler = handler;
		this.mBitmapUtils = bitmapUtils;
	}

	public void setdata(List<Contact> contacts) {
		this.mContacts = contacts;
	}

	@Override
	public int getCount() {
		return mContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return mContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_search_friends, null);
			viewHolder = new ViewHolder();
			viewHolder.adapter_search_friend_iv = (CirclePortrait) convertView.findViewById(R.id.adapter_search_friend_iv);
			viewHolder.adapter_search_friend_tv = (TextView) convertView.findViewById(R.id.adapter_search_friend_tv);
			viewHolder.adapter_search_friend_like = (ImageButton) convertView.findViewById(R.id.adapter_search_friend_like);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Contact contact = mContacts.get(position);
		String avatar = contact.avatar;
		String nick = contact.nick;
		int relations = contact.relations;
		mBitmapUtils.display(viewHolder.adapter_search_friend_iv, avatar);
		viewHolder.adapter_search_friend_tv.setText(nick);
		switch (relations) {
		case STATE_ADD:
			viewHolder.adapter_search_friend_like.setBackgroundResource(R.drawable.personal_add);
			break;
		case STATE_I_LIKE_HE:
			viewHolder.adapter_search_friend_like.setBackgroundResource(R.drawable.personal_ok);
			break;
		case STATE_HE_LIKE_ME:
			viewHolder.adapter_search_friend_like.setBackgroundResource(R.drawable.personal_add);
			break;
		case STATE_MUTUAL:
			viewHolder.adapter_search_friend_like.setBackgroundResource(R.drawable.personal_mutual);
			break;
		default:
			break;
		}

		viewHolder.adapter_search_friend_like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Message msg = Message.obtain();
				msg.what = SearchFriendActivity.MSG_LIKE;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		});
		return convertView;
	}

	class ViewHolder {
		CirclePortrait adapter_search_friend_iv;
		TextView adapter_search_friend_tv;
		ImageButton adapter_search_friend_like;
	}

}
