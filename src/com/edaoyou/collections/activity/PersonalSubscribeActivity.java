package com.edaoyou.collections.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.PersonalSubscribeAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Subscribe;
import com.edaoyou.collections.bean.SubscribeBean;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.GsonUtils;

public class PersonalSubscribeActivity extends BaseActivity {
	private String VER = "";
	private String SID = "";
	private String UID = "";
	private String mSubscribeUrl;
	private String mFollowUrl;
	private final String NOATTENTION = "0";
	private final String ATTENTIONED = "1";

	private int mCount = 0;
	private int mPosition;

	private TextView subscribe_num_tv;
	private ListView subscribe_item_lv;

	private Subscribe mSubscribe;
	private PersonalSubscribeAdapter mSubscribeAdapter;
	public static final int ADAPTER_MSG = 0;
	private List<Subscribe> mSubscribeList;

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ADAPTER_MSG:
				mPosition = (Integer) msg.obj;
				setDataGiveAdapter();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected int setContentView() {
		return R.layout.activity_personal_subscribe;
	}

	@Override
	protected void findViews() {
		subscribe_item_lv = (ListView) findViewById(R.id.subscribe_item_lv);
		subscribe_num_tv = (TextView) findViewById(R.id.subscribe_num_tv);
	}

	@Override
	protected void setListensers() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromNet();
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		if (url.equals(mSubscribeUrl)) {
			subscrubeForData(result);
		} else {
			setFollowImgBnt(result, url);
		}
	}

	private void subscrubeForData(String result) {
		SubscribeBean jsonBean = GsonUtils.json2bean(result, SubscribeBean.class);
		mSubscribeList = jsonBean.response.topic_category;
		mCount = 0;
		if (mSubscribeList != null && mSubscribeList.size() > 0) {
			for (int i = 0; i < mSubscribeList.size(); i++) {
				if (mSubscribeList.get(i).is_followed.equals(ATTENTIONED)) {
					mCount++;
				}
			}
			subscribe_num_tv.setText("订阅(" + mCount + ")");
			if (mSubscribeAdapter == null) {
				mSubscribeAdapter = new PersonalSubscribeAdapter(PersonalSubscribeActivity.this, mSubscribeList, mBitmapUtils, mHandler);
				subscribe_item_lv.setAdapter(mSubscribeAdapter);
			} else {
				mSubscribeAdapter.setData(mSubscribeList);
				mSubscribeAdapter.notifyDataSetChanged();
			}
		}
	}

	private void setFollowImgBnt(String result, String url) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int ret = jsonObject.getInt("ret");
			int status = jsonObject.getJSONObject("response").getInt("status");
			if (ret == 0 && status == 1) {
				getDataFromNet();
			} else {
				ToastUtils.showToast(mContext, "访问数据失败");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ToastUtils.showToast(mContext, "访问数据失败");
		}
	}

	private void getDataFromNet() {
		VER = GlobalParams.ver;
		SID = (String) UserUtil.getUserSid(this);
		UID = (String) UserUtil.getUserUid(this);
		mSubscribeUrl = ConstantValue.COMMONURI + ConstantValue.TOPICS;

		JSONObject jsonObject = getJSONObject();
		initData(mSubscribeUrl, jsonObject);

	}

	private void setDataGiveAdapter() {
		mSubscribe = mSubscribeList.get(mPosition);
		String isFollowed = mSubscribe.is_followed;

		if (NOATTENTION.equals(isFollowed)) {
			mFollowUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_FOLLOW;
		} else {
			mFollowUrl = ConstantValue.COMMONURI + ConstantValue.TOPIC_UNFOLLOW;
		}

		JSONObject JSONObject = getJSONObjectFromAdapter();
		initData(mFollowUrl, JSONObject);

	}

	private JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("uid", UID);
			json.put("sid", SID);
			json.put("ver", VER);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getJSONObjectFromAdapter() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();

		try {
			request.put("topic_id", mSubscribe.topic_id);
			json.put("uid", (String) UserUtil.getUserUid(mContext));
			json.put("sid", (String) UserUtil.getUserSid(mContext));
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
}
