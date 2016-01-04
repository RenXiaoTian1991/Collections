package com.edaoyou.collections.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.DoubleExpandableListViewAdapter;
import com.edaoyou.collections.adapter.PhotoDetailViewPager2Adapter;
import com.edaoyou.collections.adapter.PhotographFabuAdapter;
import com.edaoyou.collections.bean.Bean.Pois;
import com.edaoyou.collections.bean.Bean.Results;
import com.edaoyou.collections.bean.CollectionsAddress;
import com.edaoyou.collections.bean.FaBuRequest;
import com.edaoyou.collections.bean.PhotoGraphTagClientBoot;
import com.edaoyou.collections.bean.PhotoGraphTagClientBoot.Response;
import com.edaoyou.collections.bean.PhotoGraphTagClientBoot.Response.TagCategory;
import com.edaoyou.collections.bean.PhotoGraphTagClientBoot.Response.TagCategory.Detail;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.NetUtil;
import com.edaoyou.collections.view.CancelFaBuPopupWindow;
import com.edaoyou.collections.view.CustomTopbar;
import com.edaoyou.collections.view.LoadingDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 拍照完成上传页面
 */
public class PublishPhotoActivity extends Activity implements OnClickListener {
	private CancelFaBuPopupWindow mCancelFaBuPopupWindow;
	private CustomTopbar customTopbar;
	private LoadingDialog mLoadingDialog;
	private ExpandableListView mExpandablelistview;
	private ViewPager vPager;
	private ListView listview;

	private TextView select_label_tv;
	private TextView publish_tv;
	private TextView modify_label_tv;
	private ImageView more_arrow_iv;
	private LinearLayout select_label_ll;
	private DoubleExpandableListViewAdapter mDoubleExpandableListViewAdapter;
	private PhotographFabuAdapter mPhotographFabuAdapter;

	private Pois mPois;
	private Results mResults;
	private CollectionsAddress mCollectionsAddress;
	private FaBuRequest mFaBuRequest = new FaBuRequest(); // 最终发布的时候需要单been
	private Response response; // ExpandableListView所需要的数据

	private List<View> mShowPhotos; // viewPager的数据集合
	private List<File> mUploadPhotos; // 上传服务器的数据集合

	public static final String KEY_LIST = "list";
	public static final String KEY_POSITION = "position";
	private String mUid = UserUtil.getUserUid(this);
	private String mSid = UserUtil.getUserSid(this);
	private String mDesc;
	private String mPlace;
	private String mSelectedGroupName; // 选择组名称
	private String mCameraMode;
	private String mSign = AddressActivity.ITEM;
	private String[] itemNames = { "添加描述", "所在地", "可见范围", "发起咨询" };

	private boolean mIsSelectGroup; // 是否点击了组

	private int[] itemPhotos = { R.drawable.fabu_miaoshu_icon, R.drawable.fabu_suozaidi_normal, R.drawable.fabu_kejianfanwei_normal,
			R.drawable.fabu_zixun_normal };
	private int mGroupPosition;
	private int mSecondGroupPosition;
	private int mCacheTimeout = 1 * 1000;// 发布成功，让dialog多展示一会儿

	public static final int ACTIVITY_RESULT_SELECTCATEGORY = 1; // 类别返回码
	public static final int ACTIVITY_RESULT_DESCRIPTION = 2; // 图片描述返回码
	public static final int ACTIVITY_RESULT_ADDRESS = 3; // 地址返回码
	public static final int ACTIVITY_RESULT_FANWEI = 4; // 可见范围返回码

	private static final int MSG_GOTO_MAIN = 1;// 去主界面
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GOTO_MAIN:
				boolean isPublishSuccess = (Boolean) msg.obj;
				toMainActivity(isPublishSuccess);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_photo);
		findAllViewsById();
		setAllClickListener();
		initView();
		getCategoryDate();
	}

	private void findAllViewsById() {
		customTopbar = (CustomTopbar) findViewById(R.id.customTopbar);
		vPager = (ViewPager) findViewById(R.id.detail_list_item_vp);
		mExpandablelistview = (ExpandableListView) findViewById(R.id.expandablelistview);
		listview = (ListView) findViewById(R.id.listview);
		select_label_ll = (LinearLayout) findViewById(R.id.select_label_ll);
		more_arrow_iv = (ImageView) findViewById(R.id.more_arrow_iv);
		modify_label_tv = (TextView) findViewById(R.id.modify_label_tv);
		select_label_tv = (TextView) findViewById(R.id.select_label_tv);
		publish_tv = (TextView) findViewById(R.id.publish_tv);
	}

	private void setAllClickListener() {
		customTopbar.setSuffixTVOnclick(this);
		customTopbar.setPreIVOnclick(this);
		publish_tv.setOnClickListener(this);
		select_label_ll.setOnClickListener(this);
		mExpandablelistview.setOnGroupClickListener(new MyOnGroupClickListener());
		mExpandablelistview.setOnChildClickListener(new MyOnChildClickListener());
	}

	private void initView() {
		initCustomTopbar();
		initGroupName();
		initViewPager();
		initListview();
	}

	private void initCustomTopbar() {
		customTopbar.setSuffixTVString("取消发布");
		customTopbar.setSuffixTVColor(Color.WHITE);
	}

	private void initListview() {
		mPhotographFabuAdapter = new PhotographFabuAdapter(PublishPhotoActivity.this, itemPhotos, itemNames, "公开");
		listview.setAdapter(mPhotographFabuAdapter);
		listview.setOnItemClickListener(new MyOnItemClickListener());
	}

	/**
	 * 从服务器获得类别数据
	 */
	private void getCategoryDate() {
		boolean isNetworkOk = NetUtil.isNetConnect(PublishPhotoActivity.this);
		if (!isNetworkOk) {
			Toast.makeText(PublishPhotoActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		String url = ConstantValue.COMMONURI + ConstantValue.BOOT;
		String ver = GlobalParams.ver;
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(this);
		HttpUtils httpUtils = xUtilsManager.getHttpUtils();
		RequestParams requestParams = new RequestParams();
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("ver", ver);
			json.put("uid", "");
			json.put("sid", "");
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestParams.addBodyParameter("json", json.toString());
		httpUtils.send(HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String responseData = responseInfo.result;
				PhotoGraphTagClientBoot jsonBean = GsonUtils.json2bean(responseData, PhotoGraphTagClientBoot.class);
				response = jsonBean.response;
			}
		});
	}

	private void initExpandablelistviewFooter() {
		TextView save_tv = new TextView(this);
		save_tv.setText("保存");
		save_tv.setTextSize(29.0f);
		save_tv.setTextColor(0xff6EBD50);
		LayoutParams params = new ExpandableListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		save_tv.setLayoutParams(params);
		save_tv.setGravity(Gravity.CENTER);
		save_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mExpandablelistview.setVisibility(View.GONE);
				select_label_ll.setVisibility(View.VISIBLE);
				select_label_tv.setText(mSelectedGroupName);
				modify_label_tv.setVisibility(View.VISIBLE);
				more_arrow_iv.setVisibility(View.INVISIBLE);
				saveUserSelcetCategory();
			}
		});
		mExpandablelistview.addFooterView(save_tv);
	}

	/**
	 * 保存用户选择的类别标签
	 */
	private void saveUserSelcetCategory() {
		ArrayList<String> userSelcetLList = new ArrayList<String>();
		userSelcetLList.add(mSelectedGroupName);
		TreeMap<Integer, String> userSelcetCategory = mDoubleExpandableListViewAdapter.getUserSelcetCategory();
		Set<Entry<Integer, String>> entrySet = userSelcetCategory.entrySet();
		Iterator<Entry<Integer, String>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<Integer, String> next = iterator.next();
			String value = next.getValue();
			userSelcetLList.add(value);
		}
		mFaBuRequest.setTag(userSelcetLList);
	}

	private void setDescription(Intent data) {
		mDesc = data.getStringExtra("desc").toString();
		mPhotographFabuAdapter.modifyChild(0, R.drawable.fabu_miaoshu_icon, mDesc);
		mPhotographFabuAdapter.notifyDataSetChanged();
		mFaBuRequest.setTxt(mDesc);
		if (mSelectedGroupName != null && mFaBuRequest.getTxt() != null) {
			publish_tv.setBackgroundResource(R.drawable.fabu_button_bg_click);
			publish_tv.setEnabled(true);
		}
	}

	private void setScope(Intent data) {
		int lock = data.getIntExtra("fanwei", 0);
		String result = null;
		if (lock == 0) {
			result = "公开";
		} else {
			result = "私密";
		}
		mPhotographFabuAdapter.modifyChild(2, R.drawable.fabu_kejianfanwei_click, "可见范围", result);
		mPhotographFabuAdapter.notifyDataSetChanged();
		mFaBuRequest.setLock(lock);
	}

	private void showAddress(Intent data) {
		String signSource = data.getStringExtra("sign");

		// arounds item
		if (AddressActivity.ITEM.equals(signSource)) {
			mSign = AddressActivity.ITEM;
			mPois = (Pois) data.getSerializableExtra(AddressActivity.POIS);
			mPlace = mPois.name;
		}

		// search
		if (AddressActivity.SEARCH.equals(signSource)) {
			mSign = AddressActivity.SEARCH;
			mResults = (Results) data.getSerializableExtra(AddressActivity.RESULTS);
			mPlace = mResults.name;
		}

		// unshowlocationstr
		if (AddressActivity.UNSHOWLOCATIONSTR.equals(signSource)) {
			mPlace = "不显示位置";
		}

		// history
		if (AddressActivity.HISTORY.equals(signSource)) {
			mSign = AddressActivity.HISTORY;
			mCollectionsAddress = (CollectionsAddress) data.getSerializableExtra(AddressActivity.COLLECTIONSADDRESS);
			mPlace = mCollectionsAddress.name;
		}

		mPhotographFabuAdapter.modifyChild(1, R.drawable.fabu_suozaidi_click, mPlace);
		mPhotographFabuAdapter.notifyDataSetChanged();
		mFaBuRequest.setPlace(mPlace);
	}

	/**
	 * 跳转到描述页面
	 */
	private void gotoDescriptionActivity() {
		Intent descriptionIntent = new Intent(PublishPhotoActivity.this, DescriptionActivity.class);
		Bundle descriptionBundle = new Bundle();
		descriptionBundle.putString("desc", mFaBuRequest.getTxt());
		descriptionIntent.putExtras(descriptionBundle);
		startActivityForResult(descriptionIntent, ACTIVITY_RESULT_DESCRIPTION);
	}

	/**
	 * 跳转地址页面
	 */
	private void gotoAddressActivity() {
		Intent addressIntent = new Intent(PublishPhotoActivity.this, AddressActivity.class);
		Bundle addressBundle = new Bundle();
		addressBundle.putSerializable(AddressActivity.POIS, mPois);
		addressBundle.putSerializable(AddressActivity.RESULTS, mResults);
		addressBundle.putString(AddressActivity.UNSHOWLOCATIONSTR, mFaBuRequest.getPlace());
		addressBundle.putString("sign", mSign);
		addressIntent.putExtras(addressBundle);
		startActivityForResult(addressIntent, ACTIVITY_RESULT_ADDRESS);
	}

	/**
	 * 跳转到可以范围页面
	 */
	private void gotoScopeActivity() {
		Intent fanWeiIntent = new Intent(PublishPhotoActivity.this, ScopeActivity.class);
		Bundle fanWeiBundle = new Bundle();
		fanWeiBundle.putInt("fanwei", mFaBuRequest.getLock());
		fanWeiIntent.putExtras(fanWeiBundle);
		startActivityForResult(fanWeiIntent, ACTIVITY_RESULT_FANWEI);
	}

	/**
	 * 
	 * 得到一级菜单 如：中国画，当代绘画，西方绘画，书法 等
	 */
	private ArrayList<String> getGroupList() {
		ArrayList<String> groupList = new ArrayList<String>();
		List<TagCategory> tag_category = response.tag_category;
		for (int i = 0; i < tag_category.size(); i++) {
			String groupName = tag_category.get(i).tag;
			groupList.add(groupName);
		}
		return groupList;
	}

	/**
	 * 
	 * 得到具体结果列表 如：古代，南宋，北宋，元代 等
	 */
	private ArrayList<String> getResultList(int groupPosition, int childPosition) {
		ArrayList<String> groupList = new ArrayList<String>();
		TagCategory selectGroup = getSelectGroup(groupPosition);
		List<Detail> details = selectGroup.detail;
		Detail detail = details.get(childPosition);
		for (int i = 0; i < detail.tag.size(); i++) {
			groupList.add(detail.tag.get(i));
		}
		return groupList;
	}

	private TagCategory getSelectGroup(int position) {
		return response.tag_category.get(position);
	}

	@SuppressLint("Recycle")
	private void initGroupName() {
		if (GlobalParams.CURRENT_CAMERA_MODE != GlobalParams.CAMERA_MODE_FREE) {
			TypedArray obtainTypedArray = PublishPhotoActivity.this.getResources().obtainTypedArray(R.array.camera_modes_strs);
			mCameraMode = obtainTypedArray.getString(GlobalParams.CURRENT_CAMERA_MODE);
			select_label_tv.setText(mCameraMode);
		}
	}

	private void initViewPager() {
		mUploadPhotos = new ArrayList<File>();
		mShowPhotos = new ArrayList<View>();
		int photoNum = 0;
		Iterator<Map.Entry<Integer, Bitmap>> bitmapIterator = BimpUtil.bmp.entrySet().iterator();
		while (bitmapIterator.hasNext()) {
			Entry<Integer, Bitmap> entry = bitmapIterator.next();
			Bitmap bitmap = entry.getValue();
			if (bitmap != null) {
				try {
					ImageView imageView = new ImageView(this);
					imageView.setImageResource(R.drawable.photodetail_viewpager_shadow);
					imageView.setScaleType(ScaleType.FIT_XY);
					imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
					File file = BimpUtil.saveCompressedBitmap(this, bitmap);
					mUploadPhotos.add(file);
					mShowPhotos.add(imageView);
				} catch (IOException e) {
					e.printStackTrace();
				}
				photoNum++;
			}
		}
		customTopbar.setMiddleLLDenominator("/" + photoNum);
		vPager.setAdapter(new PhotoDetailViewPager2Adapter(mShowPhotos));
		vPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 上传图片到服务器
	 */
	private void uploadCreatePhotos() {
		boolean isNetworkOk = NetUtil.isNetConnect(PublishPhotoActivity.this);
		if (!isNetworkOk) {
			Toast.makeText(PublishPhotoActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		JSONObject jsonObject = new JSONObject();
		JSONObject requestObject = new JSONObject();
		JSONArray tagArray = new JSONArray(mFaBuRequest.getTag());
		try {
			requestObject.put("txt", mFaBuRequest.getTxt());// 拍卖史上成交价最高的国画，市值10亿--文字内容
			requestObject.put("lock", mFaBuRequest.getLock());// 0--是否公开
			requestObject.put("tag", tagArray);// ["中国画","人物"]--所选的类别信息,SelectCategoryActivity相关
			requestObject.put("file_count", mFaBuRequest.getCount());// 3--图片数量
			requestObject.put("modle", mFaBuRequest.getModle());// 1--拍照模式,
			requestObject.put("lat", mFaBuRequest.getLat()); // "39.977522"--纬度
			requestObject.put("lng", mFaBuRequest.getLng());// "116.385099"--经度
			requestObject.put("place", mFaBuRequest.getPlace());// "金澳国际写字楼"--地点

			jsonObject.put("uid", mUid);
			jsonObject.put("sid", mSid);
			jsonObject.put("ver", GlobalParams.ver);
			jsonObject.put("request", requestObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String url = ConstantValue.COMMONURI + ConstantValue.CREATE;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("json", jsonObject.toString());
		if (mUploadPhotos.size() == 1) {
			try {
				params.addBodyParameter("file", new FileInputStream(mUploadPhotos.get(0)), mUploadPhotos.get(0).length(), mUploadPhotos.get(0)
						.getName(), "application/octet-stream");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < mUploadPhotos.size(); i++) {
				if (mUploadPhotos.get(i) != null) {
					try {
						params.addBodyParameter("file" + i, new FileInputStream(mUploadPhotos.get(i)), mUploadPhotos.get(i).length(), mUploadPhotos
								.get(i).getName(), "application/octet-stream");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}

		mLoadingDialog = new LoadingDialog(PublishPhotoActivity.this);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				mLoadingDialog.show();
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(PublishPhotoActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String responseData = responseInfo.result;
				try {
					JSONObject obj = new JSONObject(responseData);
					int ret = obj.optInt("ret");
					if (ret == 0) {// 上传成功
						mLoadingDialog.setLoadText("发布成功...");
						Message msg = Message.obtain();
						msg.what = PublishPhotoActivity.MSG_GOTO_MAIN;
						msg.obj = true;
						mHandler.sendMessageDelayed(msg, mCacheTimeout);
					} else {
						// 上传失败 TODO 上传失败处理
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void toMainActivity(boolean isPublishSuccess) {
		if (mCancelFaBuPopupWindow != null && mCancelFaBuPopupWindow.isShowing()) {
			mCancelFaBuPopupWindow.dismiss();
		}
		mLoadingDialog.hide();
		Intent intent = new Intent(PublishPhotoActivity.this, MainActivity.class);
		if (isPublishSuccess) {
			intent.putExtra("guanzhu", true);
		}
		startActivity(intent);
	}

	/**
	 * 刷新Expandablelistview
	 */
	private void updateExpandablelistview(Intent intent) {
		if (mIsSelectGroup) {
			mGroupPosition = intent.getIntExtra(PublishPhotoActivity.KEY_POSITION, 0);
			TagCategory selectGroup = getSelectGroup(mGroupPosition);
			mSelectedGroupName = selectGroup.tag;
			if (mDoubleExpandableListViewAdapter == null) {
				mDoubleExpandableListViewAdapter = new DoubleExpandableListViewAdapter(this, selectGroup);
				mDoubleExpandableListViewAdapter.setIsSelectGroup(true);
				mExpandablelistview.setAdapter(mDoubleExpandableListViewAdapter);
				initExpandablelistviewFooter();
			} else {
				mDoubleExpandableListViewAdapter.setDate(selectGroup);
				mDoubleExpandableListViewAdapter.setIsSelectGroup(true);
				mDoubleExpandableListViewAdapter.notifyDataSetChanged();
			}
			mExpandablelistview.expandGroup(0);
		} else {
			int ziPosition = intent.getIntExtra(PublishPhotoActivity.KEY_POSITION, 0);
			mDoubleExpandableListViewAdapter.setIsSelectGroup(false);
			mDoubleExpandableListViewAdapter.setZiPosition(ziPosition);
			mDoubleExpandableListViewAdapter.setSecondGroupPosition(mSecondGroupPosition);
			mDoubleExpandableListViewAdapter.notifyDataSetChanged();
		}
		mExpandablelistview.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		switch (requestCode) {
		case ACTIVITY_RESULT_SELECTCATEGORY:
			updateExpandablelistview(intent);
			break;
		case ACTIVITY_RESULT_DESCRIPTION:
			setDescription(intent);
			break;
		case ACTIVITY_RESULT_ADDRESS:
			// 四种数据来源：1.搜索结果。2.不显示位置。3.历史记录。4.item选择。
			showAddress(intent);
			break;
		case ACTIVITY_RESULT_FANWEI:
			setScope(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topBar_pre_iv:
			onBackPressed();
			break;
		case R.id.publish_tv:
			mFaBuRequest.setCount(mUploadPhotos.size());
			uploadCreatePhotos();
			break;
		case R.id.select_label_ll:
			if (response == null) {
				Toast.makeText(PublishPhotoActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
				return;
			}
			mIsSelectGroup = true;
			Intent intent = new Intent(PublishPhotoActivity.this, SelectCategoryActivity.class);
			ArrayList<String> groupList = getGroupList();
			intent.putStringArrayListExtra(KEY_LIST, groupList);
			startActivityForResult(intent, ACTIVITY_RESULT_SELECTCATEGORY);
			break;
		case R.id.topBar_suffix_textview:
			// 实例化SelectPicPopupWindow
			mCancelFaBuPopupWindow = new CancelFaBuPopupWindow(this, this);
			// 现实窗口，设置layout在PopupWindow中显示的位置
			mCancelFaBuPopupWindow.showAtLocation(findViewById(R.id.publish), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.cancelpublish_bt:
			Message msg = Message.obtain();
			msg.what = PublishPhotoActivity.MSG_GOTO_MAIN;
			msg.obj = false;
			mHandler.sendMessage(msg);
			break;
		case R.id.cancel_bt:
			mCancelFaBuPopupWindow.dismiss();
			break;
		default:
			break;
		}
	}

	private class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			customTopbar.setMiddleLLNumerator(position + 1 + "");
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

	}

	/**
	 * 点击ExpandableListView一级菜单的点击事件
	 */
	private class MyOnGroupClickListener implements OnGroupClickListener {

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			mIsSelectGroup = true;
			Intent intent = new Intent(PublishPhotoActivity.this, SelectCategoryActivity.class);
			ArrayList<String> groupList = getGroupList();
			intent.putStringArrayListExtra(KEY_LIST, groupList);
			startActivityForResult(intent, ACTIVITY_RESULT_SELECTCATEGORY);
			mExpandablelistview.expandGroup(0);
			return false;
		}
	}

	/**
	 * 点击ExpandableListView二级菜单的点击事件
	 */
	private class MyOnChildClickListener implements OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			mIsSelectGroup = false;
			mSecondGroupPosition = childPosition;
			Intent intent = new Intent(PublishPhotoActivity.this, SelectCategoryActivity.class);
			ArrayList<String> groupList = getResultList(mGroupPosition, childPosition);
			intent.putStringArrayListExtra(KEY_LIST, groupList);
			startActivityForResult(intent, ACTIVITY_RESULT_SELECTCATEGORY);
			return false;
		}

	}

	/**
	 * 下面ListView的点击事件
	 */
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0:
				gotoDescriptionActivity();
				break;
			case 1:
				gotoAddressActivity();
				break;
			case 2:
				gotoScopeActivity();
				break;
			case 3:
				Toast.makeText(PublishPhotoActivity.this, "还没开放此功能", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	}
}
