package com.edaoyou.collections.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.SetBackGroundAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.BackGroundImg;
import com.edaoyou.collections.bean.BackGroundImg.Response.Cover_icon;
import com.edaoyou.collections.bean.BackGroundImg.Response.Cover_url;
import com.edaoyou.collections.bean.ChangeAvatarBean;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.BimpUtil;
import com.edaoyou.collections.utils.CollectionBitmapUtils;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.LocalCacheUtils;
import com.edaoyou.collections.utils.NetCacheUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SetBackGroundActivity extends BaseActivity implements OnClickListener {
	private ImageView background_position;
	private LinearLayout made_photo_ll;
	private LinearLayout made_picture_ll;
	private RelativeLayout self_user_info_rl;
	private RelativeLayout prgress_bar_rl;
	private GridView default_background_gv;
	private SetBackGroundAdapter mSetBackGroundAdapter;
	private LocalCacheUtils mLocalCacheUtils;

	public int mPosition = -1;
	public int mOldPosition = -1;
	public static final int ADAPTER_BACK_IMG = 1;
	private String mSid;
	private String mUid;
	private String mVer;
	private String mCoverUrl;
	private String mImgBackUrl;
	private String mBackGroundUrl;

	private Uri mFileUri; // 相机拍照路径
	private File mImageFilePath; // 图片路径
	private File mPhotoFile;

	private List<Cover_icon> mCoverIconList;
	private List<Cover_url> mCoverUrlList;
	private SharedPreferencesUtils mSharePf;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SetBackGroundActivity.ADAPTER_BACK_IMG:
				mBackGroundUrl = (String) msg.obj;
				mPosition = msg.arg1;
				background_position.setVisibility(View.VISIBLE);

				break;
			case NetCacheUtils.SUCCESS_BACKGROUND:
				try {
					Bitmap bitmapFromLocal = mLocalCacheUtils.getBitmapFromLocal(mBackGroundUrl);
					if (bitmapFromLocal == null) {
						ToastUtils.showToast(mContext, "更换失败!");
						return;
					}
					mPhotoFile = BimpUtil.saveCompressedBitmap(SetBackGroundActivity.this, bitmapFromLocal);

				} catch (IOException e) {
					e.printStackTrace();
				}

				mSharePf.saveInt(mUid, mPosition);

				mImgBackUrl = ConstantValue.COMMONURI + ConstantValue.CHANGE_COVER;
				getJSONObjectBack();

				break;
			case NetCacheUtils.FAILED_BACKGROUND:
				prgress_bar_rl.setVisibility(View.GONE);
				ToastUtils.showToast(mContext, "更换失败!");

				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSid = UserUtil.getUserSid(this);
		mUid = UserUtil.getUserUid(this);
		mVer = GlobalParams.ver;

		mLocalCacheUtils = new LocalCacheUtils(mContext);
		mSharePf = SharedPreferencesUtils.getInstance(mContext);
		mOldPosition = mSharePf.getInt_1(mUid);

		backGroundImgName();
		setBackGroundFromNet();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mPhotoFile != null) {
			getJSONObjectBack();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 拍照获取图片
		case GlobalParams.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			// 设置文件保存路径
			if (resultCode == Activity.RESULT_OK && null != mFileUri) {
				CollectionBitmapUtils.cropImageUri(this, mFileUri);

			} else {
				ToastUtils.showToast(mContext, "取消拍照");
			}
			break;

		// 从相册获取图片
		case GlobalParams.GET_PICTURE_FROM_XIANGCE_CODE:
			if (resultCode == Activity.RESULT_OK) {
				CollectionBitmapUtils.cropImageLocal(this, mFileUri);

			} else {
				ToastUtils.showToast(mContext, "取消获取背景图片");
			}
			break;

		// 取得裁剪后的图片
		case GlobalParams.GET_CUT_PICTURE_CODE:
			// 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			if (data != null) {
				try {
					Bitmap bitmap = decodeUriAsBitmap(mFileUri);
					mPhotoFile = BimpUtil.saveCompressedBitmap(this, bitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}

				prgress_bar_rl.setVisibility(View.VISIBLE);
				mSharePf.saveInt(mUid, -1);
				mImgBackUrl = ConstantValue.COMMONURI + ConstantValue.CHANGE_COVER;
				getJSONObjectBack();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_set_background;
	}

	@Override
	protected void findViews() {
		made_photo_ll = (LinearLayout) findViewById(R.id.made_photo_ll);
		made_picture_ll = (LinearLayout) findViewById(R.id.made_picture_ll);
		default_background_gv = (GridView) findViewById(R.id.default_background_gv);
		prgress_bar_rl = (RelativeLayout) findViewById(R.id.prgress_bar_rl);

		View view = View.inflate(mContext, R.layout.self_home_title, null);
		self_user_info_rl = (RelativeLayout) view.findViewById(R.id.self_user_info_rl);

		View view1 = View.inflate(mContext, R.layout.adapter_background_img, null);
		background_position = (ImageView) view1.findViewById(R.id.background_position);
	}

	@Override
	protected void setListensers() {
		made_photo_ll.setOnClickListener(this);
		made_picture_ll.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.made_photo_ll:
			takePhoto();// 照相
			break;
		case R.id.made_picture_ll:
			getPhotoFromSystem();// 从相册里面获取图片
			break;

		default:
			break;
		}
	}

	/**
	 * 以下是请求网络
	 */
	private void setBackGroundFromNet() {
		mCoverUrl = ConstantValue.COMMONURI + ConstantValue.DEFAULT_COVER;
		JSONObject jsonObject = getJSONObjectImg();
		initData(mCoverUrl, jsonObject);
	}

	private JSONObject getJSONObjectImg() {
		JSONObject json = new JSONObject();
		try {
			json.put("uid", mUid);
			json.put("sid", mSid);
			json.put("ver", mVer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private void getJSONObjectBack() {
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
		mHttpUtils.send(HttpMethod.POST, mImgBackUrl, requestParams, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				initDataOnStart(mImgBackUrl);
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
						mBitmapUtils.display(self_user_info_rl, photoUrl);
						prgress_bar_rl.setVisibility(View.GONE);
						ToastUtils.showToast(mContext, "更换成功");
						finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				ToastUtils.showToast(mContext, "获取服务器数据失败");
				initDataOnFailure(mImgBackUrl);
				prgress_bar_rl.setVisibility(View.GONE);
				mSharePf.saveInt(mUid, mOldPosition);
			}
		});
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		super.initDataOnSucess(result, url, type);
		BackGroundImg jsonbean = GsonUtils.json2bean(result, BackGroundImg.class);
		mCoverIconList = jsonbean.response.cover_icon;
		mCoverUrlList = jsonbean.response.cover_url;

		mSetBackGroundAdapter = new SetBackGroundAdapter(mContext, mCoverIconList, mCoverUrlList, mBitmapUtils, mHandler, prgress_bar_rl, mUid);
		default_background_gv.setAdapter(mSetBackGroundAdapter);

	}

	// 生成文件名称
	private void backGroundImgName() {
		String saveDir = Util.getCachePath(mContext);
		// 新建目录
		File dir = new File(saveDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String fileName = "QS_USER_BACKGROUND.jpg";// 照片命名
		mImageFilePath = new File(saveDir, fileName);
		mFileUri = Uri.fromFile(mImageFilePath);
	}

	/**
	 * 照相机
	 */
	private void takePhoto() {
		if (Util.existSDcard()) {
			// 获得sd卡的根目录
			Intent intent_tack = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
	 * 把uri变为bitmap
	 */
	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
