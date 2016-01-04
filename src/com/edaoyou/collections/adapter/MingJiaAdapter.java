package com.edaoyou.collections.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.UserUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MingJiaAdapter extends BaseAdapter {

	private List<User> lists;
	private Activity mcontext;
	private String url;
	private BitmapUtils mBitmapUtils;

	public MingJiaAdapter(Context context, BitmapUtils bitmapUtils) {
		this.mcontext = (Activity) context;
		this.mBitmapUtils = bitmapUtils;
		lists = new ArrayList<User>();
	}

	public void addList(List<User> lists) {
		this.lists.addAll(lists);
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final TextView textView = new TextView(mcontext);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mcontext, R.layout.message_chat_layout, null);
			holder.tou_xiang = (ImageView) convertView.findViewById(R.id.tou_xiang);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.mingjia_guanzhu = (ImageView) convertView.findViewById(R.id.mingjia_guanzhu);
			holder.time.setVisibility(View.GONE);
			holder.mingjia_guanzhu.setVisibility(View.VISIBLE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final User user = lists.get(position);
		if (user != null) {
			mBitmapUtils.display(holder.tou_xiang, user.response.avatar);
			holder.name.setTextColor(Color.BLUE);
			holder.name.setText(user.response.nick);
			holder.content.setText(user.response.bio);
			holder.content.setTextSize(12);

			String relations = user.response.relations;
			if ("0".equals(relations) || "2".equals(relations)) {
				holder.mingjia_guanzhu.setBackgroundResource(R.drawable.mingjia_guanzhu1);
			} else if ("1".equals(relations)) {
				holder.mingjia_guanzhu.setBackgroundResource(R.drawable.mingjia_guanzhu2);
			} else {
				holder.mingjia_guanzhu.setBackgroundResource(R.drawable.mingjia_guanzhu3);
			}
			textView.setTag(relations);
			holder.mingjia_guanzhu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String flag = (String) textView.getTag();
					if ("0".equals(flag) || "2".equals(flag)) {
						url = ConstantValue.COMMONURI + ConstantValue.FOLLOW;
						postFollowOrNoData(url, user.response.uid, textView, flag, holder.mingjia_guanzhu);
					} else {
						url = ConstantValue.COMMONURI + ConstantValue.UNFOLLOW;
						postFollowOrNoData(url, user.response.uid, textView, flag, holder.mingjia_guanzhu);
					}
				}
			});
		}
		return convertView;
	}

	public void postFollowOrNoData(final String url, String follow_uid, final TextView tv, final String flag, final ImageView iv) {

		XUtilsManager xUtilsManager = XUtilsManager.getInstance(mcontext);
		HttpUtils httpUtils = xUtilsManager.getHttpUtils();
		RequestParams requestParams = new RequestParams();
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("follow_uid", follow_uid);
			json.put("uid", UserUtil.getUserUid(mcontext));
			json.put("sid", UserUtil.getUserSid(mcontext));
			json.put("ver", GlobalParams.ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());
		httpUtils.send(HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// 访问网络失败
				Toast.makeText(mcontext, "获取数据失败!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String responseData = responseInfo.result;
				try {
					JSONObject jsonObject = new JSONObject(responseData);
					int ret = jsonObject.getInt("ret");
					int status = jsonObject.getJSONObject("response").getInt("status");
					if (ret == 0 && status == 1) {
						if (url.equals(ConstantValue.COMMONURI + ConstantValue.FOLLOW)) {
							// 我关注
							if ("0".equals(flag)) {
								iv.setBackgroundResource(R.drawable.mingjia_guanzhu2);
								tv.setTag("1");
							}
							if ("2".equals(flag)) {
								iv.setBackgroundResource(R.drawable.mingjia_guanzhu3);
								tv.setTag("3");
							}
						}
						if (url.equals(ConstantValue.COMMONURI + ConstantValue.UNFOLLOW)) {
							if ("1".equals(flag)) {
								iv.setBackgroundResource(R.drawable.mingjia_guanzhu1);
								// 我取消关注
								tv.setTag("0");
							} else {
								iv.setBackgroundResource(R.drawable.mingjia_guanzhu1);
								// 取消我对他的关注
								tv.setTag("2");
							}
						}
					} else {
						Toast.makeText(mcontext, "访问数据失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	static class ViewHolder {
		ImageView tou_xiang;
		ImageView mingjia_guanzhu;
		TextView name;
		TextView content;
		TextView time;
	}

}
