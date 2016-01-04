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
import com.edaoyou.collections.bean.FamosBean.Famos;
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

public class PersonalFamousAdapter extends BaseAdapter {

	private List<Famos> mFamousList;
	private Activity mContext;
	private BitmapUtils mBitmapUtils;
	private String mUid;
	private String mUrl;

	private final String NO_ATTENTION = "0";
	private final String I_ATTENTION_H = "1";
	private final String H_ATTENTION_I = "2";
	private final String EACH_ATTENTION = "3";

	public PersonalFamousAdapter(Context context, BitmapUtils bitmapUtils, List<Famos> mFamousList, String mUid) {
		this.mBitmapUtils = bitmapUtils;
		this.mContext = (Activity) context;
		this.mFamousList = mFamousList;
		this.mUid = mUid;
	}

	@Override
	public int getCount() {
		return mFamousList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFamousList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final TextView textView = new TextView(mContext);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.adapter_personal_famous, null);
		}

		viewHolder = getHolder(convertView);

		final Famos famos = mFamousList.get(position);

		if (famos != null) {
			viewHolder.famous_header_iv.setBackgroundColor(Color.TRANSPARENT);
			mBitmapUtils.display(viewHolder.famous_header_iv, famos.avatar);

			viewHolder.famous_name_tv.setText(famos.nick);
			viewHolder.famous_detail_tv.setText(famos.bio);

			String relations = famos.relations;
			if (NO_ATTENTION.equals(relations) || H_ATTENTION_I.equals(relations)) {
				viewHolder.famous_attention_ib.setBackgroundResource(R.drawable.personal_add);
			} else if (I_ATTENTION_H.equals(relations)) {
				viewHolder.famous_attention_ib.setBackgroundResource(R.drawable.personal_ok);
			} else {
				viewHolder.famous_attention_ib.setBackgroundResource(R.drawable.personal_mutual);
			}
			textView.setTag(relations);

			viewHolder.famous_attention_ib.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String flag = (String) textView.getTag();
					if (NO_ATTENTION.equals(flag) || H_ATTENTION_I.equals(flag)) {
						mUrl = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
					} else {
						mUrl = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
					}
					postFollowOrNoData(mUrl, famos.uid, textView, flag, viewHolder.famous_attention_ib);
				}
			});

			viewHolder.famous_item_ll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, PersonalHomepageActivity.class);
					intent.putExtra(GlobalParams.USER_UID, famos.uid);
					mContext.startActivity(intent);
				}
			});
		}

		return convertView;
	}

	protected void postFollowOrNoData(final String mUrl, String uid, final TextView textView, final String flag, final ImageButton famous_attention_ib) {
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
			request.put("follow_uid", uid);
			json.put("uid", mUid);
			json.put("sid", UserUtil.getUserSid(mContext));
			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());
		httpUtils.send(HttpMethod.POST, mUrl, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ToastUtils.showToast(mContext, "请检查网络!");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					String responseData = responseInfo.result;
					JSONObject jsonObject = new JSONObject(responseData);
					int ret = jsonObject.getInt("ret");
					int status = jsonObject.getJSONObject("response").getInt("status");
					if (ret == 0 && status == 1) {
						if (mUrl.equals(ConstantValue.COMMONURI + ConstantValue.FOLLOW)) {
							if (NO_ATTENTION.equals(flag)) {
								famous_attention_ib.setBackgroundResource(R.drawable.personal_ok);
								textView.setTag(I_ATTENTION_H);
							}
							if (H_ATTENTION_I.equals(flag)) {
								famous_attention_ib.setBackgroundResource(R.drawable.personal_mutual);
								textView.setTag(EACH_ATTENTION);
							}
						}
						if (mUrl.equals(ConstantValue.COMMONURI + ConstantValue.UNFOLLOW)) {
							if (I_ATTENTION_H.equals(flag)) {
								famous_attention_ib.setBackgroundResource(R.drawable.personal_add);
								// 我取消关注
								textView.setTag(NO_ATTENTION);
							} else {
								famous_attention_ib.setBackgroundResource(R.drawable.personal_add);
								// 取消我对他的关注
								textView.setTag(H_ATTENTION_I);
							}
						}
					} else {
						ToastUtils.showToast(mContext, "访问数据失败");
					}
				} catch (JSONException e) {
					ToastUtils.showToast(mContext, "请检查网络!");
				}
			}
		});
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
		TextView famous_detail_tv;
		TextView famous_name_tv;
		CirclePortrait famous_header_iv;
		ImageButton famous_attention_ib;
		LinearLayout famous_item_ll;

		public ViewHolder(View convertView) {
			famous_name_tv = (TextView) convertView.findViewById(R.id.famous_name_tv);
			famous_detail_tv = (TextView) convertView.findViewById(R.id.famous_detail_tv);
			famous_header_iv = (CirclePortrait) convertView.findViewById(R.id.famous_header_iv);
			famous_attention_ib = (ImageButton) convertView.findViewById(R.id.famous_attention_ib);
			famous_item_ll = (LinearLayout) convertView.findViewById(R.id.famous_item_ll);
		}
	}
}
