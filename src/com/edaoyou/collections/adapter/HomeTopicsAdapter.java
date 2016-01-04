package com.edaoyou.collections.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.Bean.Category;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.UserUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HomeTopicsAdapter extends BaseAdapter {
	private List<Category> mTopicCategoryList;
	private Context mContext;
	private final int[] mResId = { R.drawable.danmolou, R.drawable.guquanhui, R.drawable.yaoqizhai, R.drawable.yucuixuan, R.drawable.banruotang,
			R.drawable.muqizuo, R.drawable.jintongguan, R.drawable.zhenwange, R.drawable.zhuchuanji, R.drawable.jiaoyitai, R.drawable.jianshangyuan };

	private final String[] topics = { "丹墨楼", "古泉汇", "窑器斋", "瑜翠轩", "般若堂", "木器作", "金铜馆", "珍玩阁", "珠串集", "交易台", "鉴赏园" };

	public HomeTopicsAdapter(Context context, List<Category> topic_category) {
		super();
		this.mContext = context;
		this.mTopicCategoryList = topic_category;
	}

	@Override
	public int getCount() {
		return mResId.length;
	}

	@Override
	public Object getItem(int position) {
		return mResId[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		String is_followed = "-1";
		final ViewHolder holder = new ViewHolder();
		final View view = View.inflate(mContext, R.layout.hometopicdisplayitem, null);

		findViewByAll(holder, view);

		holder.topic_diaplayitem_rl.setBackgroundResource(mResId[position]);
		holder.topic_diaplayitem_tv.setText(topics[position]);
		is_followed = mTopicCategoryList.get(position).is_followed;

		setAttentionParams(is_followed, holder);

		view.setTag(is_followed);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击Item的时候反应较慢，建议点击时加入一个加载页面。。。（一闪而过）
				clickedIsTopicFollow(position, holder, view);
			}
		});

		return view;
	}

	private void clickedIsTopicFollow(final int position, final ViewHolder holder, final View view) {
		String tag = (String) view.getTag();
		String url;
		if ("1".equals(tag)) {
			// 取消关注
			url = ConstantValue.COMMONURI + ConstantValue.TOPIC_UNFOLLOW;
		} else {
			// 关注
			url = ConstantValue.COMMONURI + ConstantValue.TOPIC_FOLLOW;
		}
		toFollow(position, url, view, holder.topic_diaplayitem_bnt);
	}

	private void toFollow(final int position, final String url, final View view, final Button guanzhu) {

		XUtilsManager xUtilsManager = XUtilsManager.getInstance(mContext);
		HttpUtils httpUtils = xUtilsManager.getHttpUtils();
		RequestParams requestParams = new RequestParams();
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("topic_id", mTopicCategoryList.get(position).topic_id);
			json.put("uid", UserUtil.getUserUid(mContext));
			json.put("sid", UserUtil.getUserSid(mContext));
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());
		httpUtils.send(HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				successToFollow(url, view, guanzhu, responseInfo);
			}
		});
	}

	private void setAttentionParams(String is_followed, final ViewHolder holder) {
		if ("1".equals(is_followed)) {
			holder.topic_diaplayitem_bnt.setBackgroundResource(R.drawable.unfollow_button);
			holder.topic_diaplayitem_bnt.setText("已关注");
		} else {
			holder.topic_diaplayitem_bnt.setBackgroundResource(R.drawable.follow_button);
			holder.topic_diaplayitem_bnt.setText("关注");
		}
	}

	private static class ViewHolder {
		RelativeLayout topic_diaplayitem_rl;
		TextView topic_diaplayitem_tv;
		Button topic_diaplayitem_bnt;
	}

	private void findViewByAll(final ViewHolder holder, final View view) {
		holder.topic_diaplayitem_rl = (RelativeLayout) view.findViewById(R.id.topic_diaplayitem_rl);
		holder.topic_diaplayitem_tv = (TextView) view.findViewById(R.id.topic_diaplayitem_tv);
		holder.topic_diaplayitem_bnt = (Button) view.findViewById(R.id.topic_diaplayitem_bnt);
	}

	private void successToFollow(final String url, final View view, final Button guanzhu, ResponseInfo<String> responseInfo) {
		try {
			String responseData = responseInfo.result;
			JSONObject jsonObject = new JSONObject(responseData);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				if (url.equals(ConstantValue.COMMONURI + ConstantValue.TOPIC_FOLLOW)) {
					guanzhu.setBackgroundResource(R.drawable.unfollow_button);
					guanzhu.setText("已关注");
					view.setTag("1");
				} else {
					guanzhu.setBackgroundResource(R.drawable.follow_button);
					guanzhu.setText("关注");
					view.setTag("0");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "访问数据失败", Toast.LENGTH_SHORT).show();
		}
	}
}
