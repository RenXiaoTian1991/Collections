package com.edaoyou.collections.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.PersonalHomepageActivity;
import com.edaoyou.collections.bean.Bean.Follow;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.NetUtil;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class AddressBookAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private Activity mContext;
	private ArrayList<Follow> mFollowList;
	private ArrayList<String> mPyList = new ArrayList<String>();
	private BitmapUtils mBitmapUtils;
	private String mUrl;

	private final int NO_ATTENTION = 0;
	private final int I_ATTENTION_H = 1;
	private final int H_ATTENTION_I = 2;
	private final int EACH_ATTENTION = 3;

	public AddressBookAdapter(Context context, List<Follow> followList, BitmapUtils mBitmapUtils) {
		this.mContext = (Activity) context;
		this.mInflater = LayoutInflater.from(context);
		this.mFollowList = (ArrayList<Follow>) followList;
		this.mBitmapUtils = mBitmapUtils;
	}

	@Override
	public int getCount() {
		return mFollowList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFollowList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TextView textView = new TextView(mContext);
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.address_adapter_item, null);
		}

		viewHolder = getHolder(convertView);

		final Follow follow = mFollowList.get(position);
		String name = follow.nick;
		String py = follow.py.toUpperCase();
		mPyList.add(py);

		indexTvGoneOrVisible(position, viewHolder, py);

		viewHolder.person_name_tv.setText(name);

		int relations = follow.relations;

		viewHolder.person_header_iv.setBackgroundColor(Color.TRANSPARENT);

		attentionState(viewHolder, relations);

		textView.setTag(relations);
		mBitmapUtils.display(viewHolder.person_header_iv, follow.avatar);

		viewHolder.person_attention_ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int flag = (Integer) textView.getTag();
				if (NO_ATTENTION == flag || H_ATTENTION_I == flag) {
					mUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
				} else {
					mUrl = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
				}
				postFollowOrNoData(mUrl, follow.uid, textView, flag, viewHolder.person_attention_ib);
			}
		});
		viewHolder.personal_famous_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(mContext, PersonalHomepageActivity.class);
				intent.putExtra(GlobalParams.USER_UID, mFollowList.get(position).uid);
				mContext.startActivity(intent);
			}
		});

		return convertView;
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

	private void indexTvGoneOrVisible(final int position, final ViewHolder viewHolder, String py) {
		if (position > 0) {
			String lastFirstWord = mPyList.get(position - 1);
			if (py.equals(lastFirstWord)) {
				viewHolder.index_tv.setVisibility(View.GONE);
			} else {
				viewHolder.index_tv.setVisibility(View.VISIBLE);
				viewHolder.index_tv.setText(py);
			}
		} else {
			viewHolder.index_tv.setVisibility(View.VISIBLE);
			viewHolder.index_tv.setText(py);
		}
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
				Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
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
		TextView index_tv;
		TextView person_name_tv;
		CirclePortrait person_header_iv;
		ImageButton person_attention_ib;
		LinearLayout personal_famous_ll;

		public ViewHolder(View convertView) {

			index_tv = (TextView) convertView.findViewById(R.id.index_tv);
			person_name_tv = (TextView) convertView.findViewById(R.id.person_name_tv);
			person_header_iv = (CirclePortrait) convertView.findViewById(R.id.person_header_iv);
			person_attention_ib = (ImageButton) convertView.findViewById(R.id.person_attention_ib);
			personal_famous_ll = (LinearLayout) convertView.findViewById(R.id.personal_famous_ll);
		}
	}
}
