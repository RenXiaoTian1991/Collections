package com.edaoyou.collections.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.ArrayWheelAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.ChangeAvatarBean;
import com.edaoyou.collections.bean.CityBean;
import com.edaoyou.collections.bean.PersonalUserProfile;
import com.edaoyou.collections.bean.ProvinceBean;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.CollectionBitmapUtils;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.edaoyou.collections.utils.XmlParserHandler;
import com.edaoyou.collections.view.CircleImageView;
import com.edaoyou.collections.view.SelectPicPopupWindow;
import com.edaoyou.collections.view.WheelView;
import com.edaoyou.collections.view.WheelView.OnWheelChangedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 个人主页模块设置界面修改昵称
 */

public class SettingUserActivity extends BaseActivity implements OnClickListener, OnWheelChangedListener {
	private SelectPicPopupWindow mSelectPicPopupWindow;
	private CircleImageView head_iv;

	private RelativeLayout head_rl;
	private RelativeLayout name_rl;
	private RelativeLayout gender_rl;
	private RelativeLayout address_rl;
	private RelativeLayout introduce_rl;

	private TextView name_tv;
	private TextView gender_tv;
	private TextView address_tv;
	private TextView introduce_tv;

	private View mCityView;
	private View mSexView;
	private Uri mFileUri; // 相机拍照路径
	private File mImageFilePath; // 图片路径
	private File mPhotoFile;

	public static int NAME = 98;
	public static int BIO = 99;
	public int mType = 11;// 这个是同一接口中，区分不同的内容--地址

	private PersonalUserProfile mPersonalUserProfile;
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewSex;
	private AlertDialog mAlertDialog;
	private Button mBtnSex;
	private Button mBtnConfirm;

	private String[] mProvinceDatas;// 所有省
	// key-省 value-市
	private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	// key-市 values-区
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	private String mCurrentProviceName;// 当前省的名称
	private String mCurrentCityName;// 当前市的名称
	private String mSexName;

	private String mVer;
	private String mSid;
	private String mUid;
	private String mMan;
	private String mWoman;
	private String mProfileUrl;
	private String mChangeUserUrl;
	private String mImgHerderUrl;

	// 用户的属性
	private String username;
	private String location;
	private String bio;
	private String gender;
	private String avatar;
	private String[] sexItems;

	@Override
	protected int setContentView() {
		return R.layout.setting_user;
	}

	@Override
	protected void findViews() {
		head_rl = (RelativeLayout) findViewById(R.id.head_rl);
		name_rl = (RelativeLayout) findViewById(R.id.name_rl);
		gender_rl = (RelativeLayout) findViewById(R.id.gender_rl);
		address_rl = (RelativeLayout) findViewById(R.id.address_rl);
		introduce_rl = (RelativeLayout) findViewById(R.id.introduce_rl);

		head_iv = (CircleImageView) findViewById(R.id.head_iv);

		name_tv = (TextView) findViewById(R.id.name_tv);
		gender_tv = (TextView) findViewById(R.id.gender_tv);
		address_tv = (TextView) findViewById(R.id.address_tv);
		introduce_tv = (TextView) findViewById(R.id.introduce_tv);

	}

	@Override
	protected void setListensers() {
		head_rl.setOnClickListener(this);
		name_rl.setOnClickListener(this);
		gender_rl.setOnClickListener(this);
		address_rl.setOnClickListener(this);
		introduce_rl.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mVer = GlobalParams.ver;
		mSid = (String) UserUtil.getUserSid(this);
		mUid = (String) UserUtil.getUserUid(this);
		mProfileUrl = ConstantValue.COMMONURI + ConstantValue.PROFILE;

		String[] gender = getResources().getStringArray(R.array.gender);
		mWoman = gender[0];
		mMan = gender[1];

		initPicPopupWindow();

	}

	@Override
	protected void onStart() {
		super.onStart();
		setDataFromNet();
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);

		if (url.equals(mProfileUrl)) {
			Gson gson = new Gson();
			Type mtype = new TypeToken<PersonalUserProfile>() {
			}.getType();
			mPersonalUserProfile = gson.fromJson(result, mtype);
			updateUI();
		}

		if (url.equals(mChangeUserUrl)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				int ret = jsonObject.getInt("ret");
				int status = jsonObject.getJSONObject("response").getInt("status");

				if (ret == 0 && status == 1) {
					url = mProfileUrl;
					setDataFromNet();
					ToastUtils.showToast(mContext, "修改成功");
					mAlertDialog.dismiss();
				} else {
					ToastUtils.showToast(mContext, "网络异常");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 拍照获取头像
		case GlobalParams.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			// 设置文件保存路径
			if (resultCode == Activity.RESULT_OK && null != mFileUri) {
				// 因为在开启相机时，设置了保存路径所以（data会为null）,因此无法用intent传递数据
				// 直接使用开启相机时设置的保存路径就好 (fileUri就是uri,,对应的图片路径是imageFilePath)
				CollectionBitmapUtils.startPhotoZoom(this, mFileUri);
			} else {
				ToastUtils.showToast(mContext, "取消拍照");
			}
			break;
		// 从相册获取头像
		case GlobalParams.GET_PICTURE_FROM_XIANGCE_CODE:
			if (resultCode == Activity.RESULT_OK) {
				CollectionBitmapUtils.startPhotoZoom(this, data.getData());
			} else {
				ToastUtils.showToast(mContext, "取消裁剪头像");
			}
			break;
		// 取得裁剪后的图片
		case GlobalParams.GET_CUT_PICTURE_CODE:
			// 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			if (data != null) {
				try {
					Bitmap bitmap = CollectionBitmapUtils.setPicToView(data);
					mPhotoFile = BimpUtil.saveCompressedBitmap(this, bitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mImgHerderUrl = ConstantValue.COMMONURI + ConstantValue.CHANGE_AVATAR;
				getJSONObjectHeader();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.push_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.head_rl:
			mSelectPicPopupWindow.showAtLocation(findViewById(R.id.setting_user), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.name_rl:
			intent = new Intent();
			intent.putExtra("name", username);
			intent.setFlags(NAME);
			intent.setClass(this, SetUserNameActivity.class);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			startActivity(intent);
			break;
		case R.id.gender_rl:
			mType = 22;
			showDialogForSex();
			break;
		case R.id.address_rl:
			mType = 11;
			showDialog();
			break;
		case R.id.introduce_rl:
			intent = new Intent();
			intent.setFlags(BIO);
			intent.setClass(this, SetUserNameActivity.class);
			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
			startActivity(intent);
			break;
		case R.id.set_city_btn:
			setUserInfo();
			break;
		case R.id.set_sex_btn:
			setUserInfo();
			break;
		default:
			break;
		}
	}

	private void initPicPopupWindow() {
		mSelectPicPopupWindow = new SelectPicPopupWindow(SettingUserActivity.this, new selectPicPopupWindowClickListener());
		mSelectPicPopupWindow.setAnimationStyle(R.style.popupWindowAnimation);
		mSelectPicPopupWindow.update();
	}

	private void setDataFromNet() {
		JSONObject jsonObject = getJSONObject();
		initData(mProfileUrl, jsonObject);
	}

	private void setUserInfo() {
		mChangeUserUrl = ConstantValue.COMMONURI + ConstantValue.CHANGE_PROFILE;
		JSONObject jsonObject = getJSONObjectForCity();
		initData(mChangeUserUrl, jsonObject);
	}

	private JSONObject getJSONObjectForCity() {
		String location = mCurrentProviceName + " " + mCurrentCityName;
		String gender;
		if (mMan.equals(mSexName)) {
			gender = "1";
		} else {
			gender = "0";
		}
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			if (mType == 11) {
				request.put("location", location);
			} else if (mType == 22) {
				request.put("gender", gender);
			}
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
			json.put("request", request);
			request.put("uid", mUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private void getJSONObjectHeader() {
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(mContext);
		mHttpUtils = xUtilsManager.getHttpUtils();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("uid", mUid);
			jsonObject.put("sid", mSid);
			jsonObject.put("ver", mVer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("json", jsonObject.toString());
		try {
			requestParams.addBodyParameter("file", new FileInputStream(mPhotoFile), mPhotoFile.length(), mPhotoFile.getName(),
					"application/octet-stream");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mHttpUtils.send(HttpMethod.POST, mImgHerderUrl, requestParams, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				initDataOnStart(mImgHerderUrl);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					String responseData = responseInfo.result;
					ChangeAvatarBean jsonBean = GsonUtils.json2bean(responseData, ChangeAvatarBean.class);
					int ret = jsonBean.ret;
					int status = jsonBean.response.status;
					if (ret == 0 && status == 1) {
						String photoUrl = jsonBean.response.url;
						mBitmapUtils.display(head_iv, photoUrl);
						ToastUtils.showToast(mContext, "更换成功");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				ToastUtils.showToast(mContext, "获取服务器数据失败");
				initDataOnFailure(mImgHerderUrl);
			}
		});
	}

	private void updateUI() {
		avatar = mPersonalUserProfile.response.avatar;
		gender = mPersonalUserProfile.response.gender;
		username = mPersonalUserProfile.response.username;
		location = mPersonalUserProfile.response.location;
		bio = mPersonalUserProfile.response.bio;

		mBitmapUtils.display(head_iv, avatar);
		name_tv.setText(username + "");
		if ("1".equals(gender)) {
			gender_tv.setText(mMan);
		} else if ("0".equals(gender)) {
			gender_tv.setText(mWoman);
		}
		address_tv.setText(location);
		introduce_tv.setText(bio);
	}

	private class selectPicPopupWindowClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.take_photo_bt:
				takePhoto();
				mSelectPicPopupWindow.dismiss();
				break;
			case R.id.pick_photo_bt:
				getPhotoFromSystem();
				mSelectPicPopupWindow.dismiss();
			default:
				break;
			}
		}
	}

	private void takePhoto() {
		if (Util.existSDcard()) {
			// 获得sd卡的根目录
			Intent intent_tack = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String saveDir = Util.getCachePath(mContext);
			// 新建目录
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 生成文件名称
			String timeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
			String fileName = "QS" + timeFormat + ".jpg";// 照片命名
			mImageFilePath = new File(saveDir, fileName);
			mFileUri = Uri.fromFile(mImageFilePath);
			// set the image file name
			intent_tack.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
			startActivityForResult(intent_tack, GlobalParams.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
	}

	/**
	 * 从相册里面获取图片。
	 */
	private void getPhotoFromSystem() {
		Intent intent_pick = new Intent(Intent.ACTION_PICK, null);
		intent_pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CollectionBitmapUtils.IMAGE_UNSPECIFIED);
		startActivityForResult(intent_pick, GlobalParams.GET_PICTURE_FROM_XIANGCE_CODE);
	}

	/**
	 * 展示dialog
	 */
	private void showDialog() {
		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(true);
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		int screenHeight = Util.getDisplayHeight(mContext);
		window.setLayout(screenWidth, screenHeight / 2);
		mCityView = View.inflate(mContext, R.layout.activity_set_user_city, null);
		window.setContentView(mCityView);
		setUpViews();
		setUpListener();
		setUpData();
	}

	/**
	 * 一下是对选择省市做的东西
	 */
	private void setUpViews() {
		mViewProvince = (WheelView) mCityView.findViewById(R.id.id_province);
		mViewCity = (WheelView) mCityView.findViewById(R.id.id_city);
		mBtnConfirm = (Button) mCityView.findViewById(R.id.set_city_btn);
	}

	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加onclick事件
		mBtnConfirm.setOnClickListener(this);
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		updateCities();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewSex) {
			int pCurrent = mViewSex.getCurrentItem();
			mSexName = sexItems[pCurrent];
		}
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
	}

	/**
	 * 解析省市区的XML数据
	 */

	protected void initProvinceDatas() {
		List<ProvinceBean> provinceList = null;
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */ 初始化默认选中的省、市、区
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityBean> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
				}
			}

			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityBean> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j).getName();
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改性别
	 */
	private void showDialogForSex() {
		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(true);
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		int screenHeight = Util.getDisplayHeight(mContext);
		window.setLayout(screenWidth, screenHeight / 2);
		mSexView = View.inflate(mContext, R.layout.activity_set_user_sex, null);
		window.setContentView(mSexView);
		setSexView();
		setSexListener();
		setSexData();
	}

	private void setSexView() {
		mViewSex = (WheelView) mSexView.findViewById(R.id.set_sex_wv);
		mBtnSex = (Button) mSexView.findViewById(R.id.set_sex_btn);
	}

	private void setSexListener() {
		// 添加change事件
		mViewSex.addChangingListener(this);
		// 添加onclick事件
		mBtnSex.setOnClickListener(this);
	}

	private void setSexData() {
		sexItems = new String[] { mMan, mWoman };
		mViewSex.setViewAdapter(new ArrayWheelAdapter<String>(this, sexItems));
		int pCurrent = mViewSex.getCurrentItem();
		mSexName = sexItems[pCurrent];
		// 设置可见条目数量
		mViewSex.setVisibleItems(7);
	}
}
