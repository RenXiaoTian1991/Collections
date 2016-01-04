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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.PersonalHomepageActivity;
import com.edaoyou.collections.bean.Bean.Follow;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.NetUtil;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.view.CirclePortrait;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class PersonalMyfansAdapter extends BaseAdapter {

	private BitmapUtils mBitmapUtils;
	private List<Follow> mFollowList;
	private Activity mContext;

	private String mFollowUrl;

	private final int H_ATTENTION_I = 2;
	private final int EACH_ATTENTION = 3;

	public PersonalMyfansAdapter(Context context, List<Follow> followList, BitmapUtils bitmapUtils) {
		this.mContext = (Activity) context;
		this.mBitmapUtils = bitmapUtils;
		this.mFollowList = followList;
	}

	public void setData(List<Follow> followList) {
		this.mFollowList = followList;
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
			convertView = View.inflate(mContext, R.layout.adapter_personal_myfans, null);
		}
		viewHolder = getHolder(convertView);

		final Follow follow = mFollowList.get(position);

		viewHolder.person_name_tv.setText(follow.nick);

		int relations = follow.relations;

		viewHolder.person_header_iv.setBackgroundColor(Color.TRANSPARENT);
		mBitmapUtils.display(viewHolder.person_header_iv, follow.avatar);

		if (EACH_ATTENTION == relations) {
			viewHolder.person_attention_ib.setBackgroundResource(R.drawable.personal_mutual);
		} else {
			viewHolder.person_attention_ib.setBackgroundResource(R.drawable.personal_add);
		}

		textView.setTag(relations);
		final String followUid = follow.uid;
		viewHolder.person_attention_ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int flag = (Integer) textView.getTag();
				if (H_ATTENTION_I == flag) {
					mFollowUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
				} else {
					mFollowUrl = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
				}

				postFollowOrNoData(mFollowUrl, followUid, textView, flag, viewHolder.person_attention_ib, position);
			}
		});

		viewHolder.personal_myfaans_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PersonalHomepageActivity.class);
				intent.putExtra(GlobalParams.USER_UID, mFollowList.get(position).uid);
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}

	protected void postFollowOrNoData(final String mUrl, String followUid, final TextView textView, final int flag,
			final ImageButton person_attention_ib, final int position) {
		if (!NetUtil.isNetConnect(mContext)) {
			ToastUtils.showToast(mContext, "请检查网络!");
			return;
		}
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(mContext);
		HttpUtils httpUtils = xUtilsManager.getHttpUtils();
		RequestParams requestParams = new RequestParams();
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("follow_uid", followUid);
			json.put("uid", UserUtil.getUserUid(mContext));
			json.put("sid", UserUtil.getUserSid(mContext));
			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());
		httpUtils.send(HttpMethod.POST, mUrl, requestParams, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				onSuccessAttention(mUrl, textView, flag, person_attention_ib, responseInfo, position);
			}

			@Override
			public void onFailure(HttpException error, String msg) {

			}
		});
	}

	protected void onSuccessAttention(String mUrl2, TextView textView, int flag, ImageButton person_attention_ib, ResponseInfo<String> responseInfo,
			int position) {
		try {
			String responseData = responseInfo.result;
			JSONObject jsonObject = new JSONObject(responseData);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				if (mFollowUrl.equals(ConstantValue.COMMONURI + ConstantValue.FOLLOW)) {
					if (H_ATTENTION_I == flag) {
						person_attention_ib.setBackgroundResource(R.drawable.personal_mutual);
						textView.setTag(EACH_ATTENTION);
					}
				} else {
					if (EACH_ATTENTION == flag) {
						person_attention_ib.setBackgroundResource(R.drawable.personal_add);
						textView.setTag(H_ATTENTION_I);
					}
				}
			}
		} catch (JSONException e) {
			ToastUtils.showToast(mContext, "访问数据失败");
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
		CirclePortrait person_header_iv;
		ImageButton person_attention_ib;
		LinearLayout personal_myfaans_ll;

		public ViewHolder(View convertView) {
			person_name_tv = (TextView) convertView.findViewById(R.id.person_name_tv);
			person_header_iv = (CirclePortrait) convertView.findViewById(R.id.person_header_iv);
			person_attention_ib = (ImageButton) convertView.findViewById(R.id.person_attention_ib);
			personal_myfaans_ll = (LinearLayout) convertView.findViewById(R.id.personal_myfaans_ll);
		}
	}
}
