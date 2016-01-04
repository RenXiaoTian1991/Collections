package com.edaoyou.collections.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.ShareFriendByPhoneActivity;
import com.edaoyou.collections.bean.Bean.Contact;
import com.edaoyou.collections.bean.PhoneFriend;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.NetUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class AddfriendPhoneAdapter extends BaseAdapter {
	private List<Contact> mContactList;// 只有安装应用的用户的详细数据
	private List<PhoneFriend> mPhoneList;// 所有通讯录列表
	private Activity mContext;
	private BitmapUtils mBitmapUtils;
	private int mCount;// 安装应用的用户的个数
	private String mUrl;

	private final int NO_ATTENTION = 0;
	private final int I_ATTENTION_H = 1;
	private final int H_ATTENTION_I = 2;
	private final int EACH_ATTENTION = 3;

	public AddfriendPhoneAdapter(Context context, List<Contact> contactList, List<PhoneFriend> mPhoneList, int count, BitmapUtils mBitmapUtils) {
		this.mContactList = contactList;
		this.mContext = (Activity) context;
		this.mBitmapUtils = mBitmapUtils;
		this.mCount = count;
		this.mPhoneList = mPhoneList;
	}

	@Override
	public int getCount() {
		return mPhoneList.size();
	}

	@Override
	public Object getItem(int position) {
		return mPhoneList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final TextView textView = new TextView(mContext);
		//TODO 需要优化
		convertView = View.inflate(mContext, R.layout.adapter_personal_myfans, null);
		viewHolder = getHolder(convertView);

		if (position < mCount) {
			setLoginUsers(position, viewHolder, textView);

		} else {// 未注册用户
			final String name = mPhoneList.get(position).getName();
			final String number = mPhoneList.get(position).getNumber();
			viewHolder.person_name_tv.setText(name);
			viewHolder.person_name_tv.setTextColor(Color.BLACK);
			viewHolder.person_header_iv.setVisibility(View.GONE);
			viewHolder.person_attention_ib.setVisibility(View.GONE);

			viewHolder.personal_myfaans_ll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, ShareFriendByPhoneActivity.class);
					intent.putExtra("name", name);
					intent.putExtra("number", number);
					mContext.startActivity(intent);
				}
			});
		}

		return convertView;
	}

	private void setLoginUsers(int position, final ViewHolder viewHolder, final TextView textView) {
		final Contact contact = mContactList.get(position);

		// 设置姓名，头像
		viewHolder.person_name_tv.setText(contact.nick);
		viewHolder.person_header_iv.setBackgroundColor(Color.TRANSPARENT);
		mBitmapUtils.display(viewHolder.person_header_iv, contact.avatar);

		// 设置关注状态
		int relations = contact.relations;
		attentionState(viewHolder, relations);
		textView.setTag(relations);

		viewHolder.person_attention_ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int flag = (Integer) textView.getTag();
				if (NO_ATTENTION == flag || H_ATTENTION_I == flag) {
					mUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
				} else {
					mUrl = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
				}
				postFollowOrNoData(mUrl, contact.uid, textView, flag, viewHolder.person_attention_ib);
			}
		});
	}

	protected void postFollowOrNoData(final String mUrl, String uid, final TextView textView, final int flag, final ImageButton person_attention_ib) {
		if (!NetUtil.isNetConnect(mContext)) {
			Toast.makeText(mContext, "请检查网络!", Toast.LENGTH_SHORT).show();
			return;
		}
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(mContext);
		HttpUtils httpUtils = xUtilsManager.getHttpUtils();
		RequestParams requestParams = new RequestParams();
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("follow_uid", uid);
			json.put("uid", (String) UserUtil.getUserUid(mContext));
			json.put("sid", (String) UserUtil.getUserSid(mContext));
			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());
		httpUtils.send(HttpMethod.POST, mUrl, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(mContext, "获取数据失败!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				onSuccessAttention(mUrl, textView, flag, person_attention_ib, responseInfo);
			}
		});
	}

	private void onSuccessAttention(final String mUrl, final TextView textView, final int flag, final ImageButton person_attention_ib,
			ResponseInfo<String> responseInfo) {
		try {
			String responseData = responseInfo.result;
			JSONObject jsonObject = new JSONObject(responseData);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				if (mUrl.equals(ConstantValue.COMMONURI + ConstantValue.FOLLOW)) {
					if (NO_ATTENTION == flag) {
						person_attention_ib.setBackgroundResource(R.drawable.personal_ok);
						textView.setTag(I_ATTENTION_H);
					}
					if (H_ATTENTION_I == flag) {
						person_attention_ib.setBackgroundResource(R.drawable.personal_mutual);
						textView.setTag(EACH_ATTENTION);
					}
				}
				if (mUrl.equals(ConstantValue.COMMONURI + ConstantValue.UNFOLLOW)) {
					if (I_ATTENTION_H == flag) {
						person_attention_ib.setBackgroundResource(R.drawable.personal_add);
						// 我取消关注
						textView.setTag(NO_ATTENTION);
					} else {
						person_attention_ib.setBackgroundResource(R.drawable.personal_add);
						// 取消我对他的关注
						textView.setTag(H_ATTENTION_I);
					}
				}
			} else {
				Toast.makeText(mContext, "解析数据失败", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void attentionState(final ViewHolder viewHolder, int relations) {
		if (NO_ATTENTION == relations || H_ATTENTION_I == relations) {
			viewHolder.person_attention_ib.setBackgroundResource(R.drawable.personal_add);
		} else if (I_ATTENTION_H == (relations)) {
			viewHolder.person_attention_ib.setBackgroundResource(R.drawable.personal_ok);
		} else {
			viewHolder.person_attention_ib.setBackgroundResource(R.drawable.personal_mutual);
		}
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
		TextView person_name_tv;
		ImageView person_header_iv;
		ImageButton person_attention_ib;
		LinearLayout personal_myfaans_ll;

		public ViewHolder(View convertView) {
			person_name_tv = (TextView) convertView.findViewById(R.id.person_name_tv);
			person_header_iv = (ImageView) convertView.findViewById(R.id.person_header_iv);
			person_attention_ib = (ImageButton) convertView.findViewById(R.id.person_attention_ib);
			personal_myfaans_ll = (LinearLayout) convertView.findViewById(R.id.personal_myfaans_ll);
		}
	}
}
