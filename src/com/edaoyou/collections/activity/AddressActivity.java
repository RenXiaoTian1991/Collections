package com.edaoyou.collections.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.PhotographLocationAdapter;
import com.edaoyou.collections.adapter.PhotographLocationSerachAdapter;
import com.edaoyou.collections.bean.Bean.Pois;
import com.edaoyou.collections.bean.Bean.Results;
import com.edaoyou.collections.bean.CollectionsAddress;
import com.edaoyou.collections.bean.PhotographLocation;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.view.CustomClearEditText;
import com.edaoyou.collections.view.CustomTopbar;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@SuppressLint("ShowToast")
public class AddressActivity extends Activity implements OnClickListener,
		OnEditorActionListener, OnItemClickListener {
	private static final int MIN_TIME = 2000;
	private static final int MIN_DISTENCE = 10;

	public static final String POIS = "Pois";
	public static final String RESULTS = "Results";
	public static final String COLLECTIONSADDRESS = "COLLECTIONSADDRESS";

	public static final String UNSHOWLOCATIONSTR = "Hidden";// 选择不显示位置获取数据
	public static final String HISTORY = "History";// 选择历史记录获取数据
	public static final String SEARCH = "Search";// 选择搜索结果获取数据
	public static final String ITEM = "Item";// 选择item获取数据
	private String ver;
	private String uid;
	private String sid;
	private String longitude, latitude, selectedFlag;

	private CustomTopbar customTitlebar;
	private CustomClearEditText add_description_et;
	private TextView cancel_tv;
	private ListView address_lv;
	private ImageView selected_iv;
	private LinearLayout listHearderView, unShow_location, history_layout,
			search_linearlayout;

	private PhotographLocationSerachAdapter locationSerachAdapter;
	private PhotographLocationAdapter locationAdapter;
	private PhotographLocation locationPGBean;

	private boolean choose = true;// 打钩

	private List<Pois> poisList;// 返回了一组包含地址的数据集
	private List<Results> resultssList;
	private List<CollectionsAddress> historyList;

	private Pois pois;
	private Results results = null;

	private LocationManager locationManager;
	private Location location = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address);

		findAllViewById();

		setAllListeners();

		customTitlebar.setPreIVBackground(R.drawable.black_ruturn);
		customTitlebar.setMiddleTVString("所在位置");
		customTitlebar.setMiddleTVColor(Color.BLACK);
		customTitlebar.setSuffixVisiable();

		ver = GlobalParams.ver;
		uid = UserUtil.getUserUid(this);
		sid = UserUtil.getUserSid(this);

		add_description_et.clearFocus();

		address_lv.addHeaderView(listHearderView);

		poisList = new ArrayList<Pois>();
		boolean GPS_status = false;
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		try {
			GPS_status = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (GPS_status) {
				init();
			} else {
				startActivityForResult(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
						0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			new AlertDialog.Builder(this).setMessage("定位权限被禁止，请到应用程序许可中打开!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					finish();
				}
			}).show();
			e.printStackTrace();
		}
		
		// boolean networkStatus =
		// locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		init();
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(locationListener);
	}

	public void init() {
		getLocationDegree();

		String url = ConstantValue.COMMONURI + ConstantValue.LOCATION_GET;
		getDataFromServer(url, sid, uid, ver, latitude, longitude);
	}

	public void setAllListeners() {
		cancel_tv.setOnClickListener(this);
		add_description_et.setOnClickListener(this);
		unShow_location.setOnClickListener(this);
		history_layout.setOnClickListener(this);
		add_description_et.setOnEditorActionListener(this);
		add_description_et.setFocusable(false);
		add_description_et.setFocusableInTouchMode(false);
		address_lv.setOnItemClickListener(this);
		customTitlebar.setPreIVOnclick(this);
	}

	public void findAllViewById() {
		customTitlebar = (CustomTopbar) findViewById(R.id.customTitlebar);
		add_description_et = (CustomClearEditText) findViewById(R.id.add_description_et);

		search_linearlayout = (LinearLayout) findViewById(R.id.search_linearlayout);
		listHearderView = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.address_head, null);
		unShow_location = (LinearLayout) listHearderView
				.findViewById(R.id.unShow_ll);
		history_layout = (LinearLayout) listHearderView
				.findViewById(R.id.history_ll);

		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		selected_iv = (ImageView) listHearderView
				.findViewById(R.id.selected_iv);
		address_lv = (ListView) findViewById(R.id.address_list);

	}

	public void getLocationDegree() {
		boolean networkStatus = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (networkStatus) {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			if (location != null) {
				setLocationDegree();
			}
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTENCE,
					locationListener);

		} else {
			startActivity(new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	}

	public void setLocationDegree() {
		latitude = location.getLatitude() + "";
		longitude = location.getLongitude() + "";
	}

	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
			if (location != null) {
				setLocationDegree();
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	private void getDataFromServer(String url, String sid, String uid,
			String ver, String lat, String lng) {
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(this);
		HttpUtils httpUtils = xUtilsManager.getHttpUtils();
		RequestParams requestParams = new RequestParams();
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("lat", lat);
			request.put("lng", lng);

			json.put("uid", uid);
			json.put("sid", sid);
			json.put("ver", ver);
			json.put("request", request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());

		httpUtils.send(HttpMethod.POST, url, requestParams,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(AddressActivity.this, "请检查网络",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (responseInfo != null) {
							String responseData = responseInfo.result;
							updataUI(responseData);
						}
					}
				});
	}

	private void updataUI(String responseData) {
		locationPGBean = GsonUtils.json2bean(responseData,
				PhotographLocation.class);
		selectedFlag = AddressActivity.ITEM;
		poisList = locationPGBean.response.pois;

		setSelectIvVisible();

		// TODO 这里的代码，内层的需要优化
		if (AddressActivity.ITEM.equals(getIntent().getStringExtra("sign"))) {
			pois = (Pois) getIntent()
					.getSerializableExtra(AddressActivity.POIS);
			locationAdapter = new PhotographLocationAdapter(poisList, pois,
					null, choose);
		}

		if (AddressActivity.SEARCH.equals(getIntent().getStringExtra("sign"))) {
			results = (Results) getIntent().getSerializableExtra(
					AddressActivity.RESULTS);
			locationAdapter = new PhotographLocationAdapter(poisList, null,
					results, choose);
		}
		address_lv.setAdapter(locationAdapter);
	}

	private void setSelectIvVisible() {
		String unShowLocationStr = getIntent().getStringExtra(
				AddressActivity.UNSHOWLOCATIONSTR);
		Pois pois = (Pois) getIntent().getSerializableExtra(
				AddressActivity.POIS);
		if (pois != null && !unShowLocationStr.equals("不显示位置")) {
			choose = false;
			selected_iv.setVisibility(View.INVISIBLE);
		} else {
			choose = true;
			selected_iv.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.topBar_pre_iv:
			finish();
			break;

		case R.id.cancel_tv:
			stopSearch();
			break;
		case R.id.add_description_et:
			startSearch();
			break;

		case R.id.unShow_ll:
			clickedUnShowLocation();
			break;

		case R.id.history_ll:
			updateHistoryRecodeUI();
			break;

		default:
			break;
		}
	}

	public void updateHistoryRecodeUI() {
		search_linearlayout.setVisibility(View.GONE);

		try {
			DbUtils db = DbUtils.create(this);
			db.configAllowTransaction(true);
			db.configDebug(true);
			historyList = db.findAll(Selector.from(CollectionsAddress.class));
			if (null == historyList || 0 == historyList.size()) {
				Toast.makeText(this, "没有历史记录！", Toast.LENGTH_SHORT).show();
				return;
			}
			selectedFlag = AddressActivity.HISTORY;
			int headerViewsCount = address_lv.getHeaderViewsCount();

			while (headerViewsCount != 0) {
				address_lv.removeHeaderView(listHearderView);
				headerViewsCount--;
			}

			// TODO 每次点击的时候都new一个，可以优化吗
			locationSerachAdapter = new PhotographLocationSerachAdapter(
					historyList);

			address_lv.setAdapter(locationSerachAdapter);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void clickedUnShowLocation() {
		Intent hiddenIntent = new Intent();
		Bundle hiddenBundle = new Bundle();
		hiddenBundle.putString("sign", AddressActivity.UNSHOWLOCATIONSTR);
		hiddenIntent.putExtras(hiddenBundle);
		setResult(PublishPhotoActivity.ACTIVITY_RESULT_ADDRESS, hiddenIntent);
		finish();
	}

	private void startSearch() {
		add_description_et.setFocusable(true);
		add_description_et.setFocusableInTouchMode(true);
		add_description_et.requestFocus();
		// 弹出软键盘
		Util.showSoftInput(this, add_description_et);

		cancel_tv.setVisibility(View.VISIBLE);
	}

	private void stopSearch() {
		cancel_tv.setVisibility(View.GONE);
		add_description_et.setFocusable(false);
		add_description_et.setFocusableInTouchMode(false);
		add_description_et.clearFocus();
		add_description_et.setText("");
		// 隐藏软键盘
		Util.hideSoftInput(this);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			// 点击软键盘搜索图标，进入搜索
			if (Util.isNetWorkConnected(this)) {
				String url = ConstantValue.COMMONURI
						+ ConstantValue.LOCATION_SEARCH;
				String kw = add_description_et.getText().toString();
				getSerachResultFromServer(url, sid, uid, ver, latitude,
						longitude, kw);

				// 隐藏软键盘
				Util.hideSoftInput(this);
			} else {
				Toast.makeText(this, "网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	private void getSerachResultFromServer(String url, String sid, String uid,
			String ver, String lat, String lng, String kw) {
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(this);
		HttpUtils httpUtils = xUtilsManager.getHttpUtils();
		RequestParams requestParams = new RequestParams();
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("lat", lat);
			request.put("lng", lng);
			request.put("kw", kw);

			json.put("uid", uid);
			json.put("sid", sid);
			json.put("ver", ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());
		httpUtils.send(HttpMethod.POST, url, requestParams,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(AddressActivity.this, "请检查网络",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (responseInfo != null) {
							String responseData = responseInfo.result;
							updataSearchUI(responseData);
						}
					}
				});
	}

	private void updataSearchUI(String responseData) {
		locationPGBean = GsonUtils.json2bean(responseData,
				PhotographLocation.class);
		selectedFlag = AddressActivity.SEARCH;
		resultssList = locationPGBean.response.results;
		address_lv.removeHeaderView(listHearderView);
		locationSerachAdapter = new PhotographLocationSerachAdapter(
				resultssList);
		address_lv.setAdapter(locationSerachAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		if (selectedFlag.equals(AddressActivity.ITEM)) {

			pois = locationAdapter.getmPoiss().get(position - 1);
			bundle.putSerializable(AddressActivity.POIS, pois);
			saveHistoryRecord(pois);
		} else if (selectedFlag.equals(AddressActivity.SEARCH)) {

			results = resultssList.get(position);
			bundle.putSerializable(AddressActivity.RESULTS, results);
			saveHistoryRecord(results);
		} else {

			CollectionsAddress collectionsAddress = historyList.get(position);
			bundle.putSerializable(AddressActivity.COLLECTIONSADDRESS,
					collectionsAddress);
			saveHistoryRecord(collectionsAddress);
		}
		bundle.putString("sign", selectedFlag);
		intent.putExtras(bundle);
		setResult(PublishPhotoActivity.ACTIVITY_RESULT_ADDRESS, intent);
		finish();
	}

	public void saveHistoryRecord(CollectionsAddress address) {
		try {
			DbUtils db = DbUtils.create(this);
			db.configAllowTransaction(true);
			db.configDebug(true);

			CollectionsAddress hasHistoryItem = db.findFirst(Selector.from(
					CollectionsAddress.class).where("name", "=", address.name));

			if (hasHistoryItem == null) {
				hasHistoryItem = new CollectionsAddress();
				hasHistoryItem.name = address.name;

				if (address instanceof Pois) {
					hasHistoryItem.addr = address.addr;
				} else if (address instanceof Results) {
					hasHistoryItem.address = address.address;
				} else {
					hasHistoryItem.address = address.address;
					hasHistoryItem.addr = address.addr;
				}
				db.save(hasHistoryItem);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
}
