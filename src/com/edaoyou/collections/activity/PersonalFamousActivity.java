package com.edaoyou.collections.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ListView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.PersonalFamousAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.FamosBean;
import com.edaoyou.collections.bean.FamosBean.Famos;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.lidroid.xutils.BitmapUtils;

public class PersonalFamousActivity extends BaseActivity {

	private ListView famous_item_lv;

	private PersonalFamousAdapter mFamousAdapter;
	private BitmapUtils mBitmapUtils;

	private int flag = 0; // 0代表初始请求 1代表上拉加载 2代表下拉刷新
	private int count = 10; // 请求的的数据条数

	private String mFamousUrl;
	private String last_id = "0"; // 当前页面加载过得最后一条数据的id
	private String mVer = GlobalParams.ver;
	private String mSid = UserUtil.getUserSid(this);
	private String mUid = UserUtil.getUserUid(this);

	private List<Famos> mFamousList;
	private XUtilsManager mXUtilsManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_personal_famous;
	}

	@Override
	protected void findViews() {
		famous_item_lv = (ListView) findViewById(R.id.famous_item_lv);

	}

	@Override
	protected void setListensers() {
	}

	private void initData() {
		mFamousUrl = ConstantValue.COMMONURI + ConstantValue.USER_LIST;

		mXUtilsManager = XUtilsManager.getInstance(this);
		mBitmapUtils = mXUtilsManager.getBitmapUtils();

		JSONObject jsonObject = getJSONObjectData();
		initData(mFamousUrl, jsonObject);
	}

	private JSONObject getJSONObjectData() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();

		try {
			request.put("count", count);
			request.put("last_id", last_id);
			request.put("flag", flag);

			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		FamosBean jsonBean = GsonUtils.json2bean(result, FamosBean.class);
		mFamousList = jsonBean.response.list;
		famous_item_lv.setDivider(null);// 分割线

		mFamousAdapter = new PersonalFamousAdapter(this, mBitmapUtils, mFamousList, mUid);
		famous_item_lv.setAdapter(mFamousAdapter);

		mFamousAdapter.notifyDataSetChanged();
	}

}
