package com.edaoyou.collections.topic;

import org.json.JSONObject;

import com.edaoyou.collections.MyApplication;
import com.edaoyou.collections.R;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.engine.XUtilsManager;
import com.edaoyou.collections.utils.NetUtil;
import com.edaoyou.collections.utils.ToastUtils;
import com.edaoyou.collections.view.LoadingDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public abstract class BaseViewPagerFragment extends Fragment implements ScrollableListener {

    protected ScrollableFragmentListener mListener;
    protected static final String BUNDLE_FRAGMENT_INDEX = "BaseFragment.BUNDLE_FRAGMENT_INDEX";
    protected int mFragmentIndex;
    protected Context mContext;
	protected HttpUtils mHttpUtils;
	protected BitmapUtils mBitmapUtils;
	protected User mUser;
	protected LoadingDialog mLoadingDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
		mUser = ((MyApplication) mContext.getApplicationContext()).getUser();
		initXUtils();
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.setLoadText("努力加载中...");
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFragmentIndex = bundle.getInt(BUNDLE_FRAGMENT_INDEX, 0);
        }

        if (mListener != null) {
            mListener.onFragmentAttached(this, mFragmentIndex);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ScrollableFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement ScrollableFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        if (mListener != null) {
            mListener.onFragmentDetached(this, mFragmentIndex);
        }

        super.onDetach();
        mListener = null;
    }
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = initParent(inflater, container);
		findViews(rootView);
		setListensers();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initVariable();
		if (!DataManager.getInstance().hasExecute(getLevel())) {
			activityCreated(savedInstanceState);
		}
	}

	private View initParent(LayoutInflater inflater, ViewGroup container) {
		View rootView = inflater.inflate(R.layout.fragment_base_layout, container, false);
		LinearLayout subCententView = (LinearLayout) rootView.findViewById(R.id.base_sub_fragment_layout);
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		View centerView = View.inflate(mContext, setContentView(), null);
		subCententView.addView(centerView, layoutParams);
		return rootView;
	}

	private void initXUtils() {
		XUtilsManager xUtilsManager = XUtilsManager.getInstance(mContext);
		mHttpUtils = xUtilsManager.getHttpUtils();
		mBitmapUtils = xUtilsManager.getBitmapUtils();
	}

	/**
	 * POST方式请求服务器
	 */
	public void initData(final String url, JSONObject jsonObject) {
		if (!NetUtil.isNetConnect(mContext)) {
			ToastUtils.showToast(mContext, "请检查网络");
			return;
		}
		DataManager.getInstance().setLoadedDataState(url, false);
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("json", jsonObject.toString());
		mLoadingDialog.show();
		mHttpUtils.send(HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				initDataOnStart(url);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				mLoadingDialog.hide();
				String responseData = responseInfo.result;
				initDataOnSucess(responseData, url);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mLoadingDialog.hide();
				ToastUtils.showToast(mContext, "获取服务器数据失败");
				initDataOnFailure(url);
			}
		});
	}
	/**
	 * POST方式请求服务器 控制view不重复请求
	 */
	public void initData(final String url, JSONObject jsonObject,final boolean showLoadingDialog) {
		if (!NetUtil.isNetConnect(mContext)) {
			ToastUtils.showToast(mContext, "请检查网络");
			return;
		}
		if(showLoadingDialog){
			mLoadingDialog.show();
		}
		DataManager.getInstance().setLoadedDataState(url, false);
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("json", jsonObject.toString());
		mHttpUtils.send(HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				initDataOnStart(url);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if(showLoadingDialog){
					mLoadingDialog.hide();
				}
				String responseData = responseInfo.result;
				initDataOnSucess(responseData, url);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if(showLoadingDialog){
					mLoadingDialog.hide();
				}
				ToastUtils.showToast(mContext, "获取服务器数据失败");
				initDataOnFailure(url);
			}
		});
	}
	/**
	 * POST方式请求服务器 控制view不重复请求
	 */
	public void initData(final String url, JSONObject jsonObject,final View view) {
		if (!NetUtil.isNetConnect(mContext)) {
			ToastUtils.showToast(mContext, "请检查网络");
			return;
		}
		view.setClickable(false);
		DataManager.getInstance().setLoadedDataState(url, false);
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("json", jsonObject.toString());
		mLoadingDialog.show();
		mHttpUtils.send(HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				initDataOnStart(url);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				mLoadingDialog.hide();
				String responseData = responseInfo.result;
				view.setClickable(true);
				initDataOnSucess(responseData, url);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mLoadingDialog.hide();
				ToastUtils.showToast(mContext, "获取服务器数据失败");
				initDataOnFailure(url);
			}
		});
	}
	/**
	 * GET方式请求服务器
	 */
	protected void initDataGet(final String url) {
		if (!NetUtil.isNetConnect(mContext)) {
			ToastUtils.showToast(mContext, "请检查网络");
			return;
		}
		DataManager.getInstance().setLoadedDataState(url, false);
		mHttpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				initDataOnStart(url);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					String responseData = responseInfo.result;
					initDataOnSucess(responseData, url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				ToastUtils.showToast(mContext, "获取服务器数据失败");
				initDataOnFailure(url);
			}
		});
	}

	/**
	 * 加载子类布局
	 */
	protected abstract int setContentView();

	/**
	 * 加载控件
	 */
	protected abstract void findViews(View rootView);

	/**
	 * 设置监听
	 */
	protected abstract void setListensers();

	/**
	 * 设置层次关系
	 */
	protected abstract String getLevel();

	/**
	 * 初始化子类fragment的变量
	 */
	protected abstract void initVariable();

	/**
	 * 类似onActivityCreated方法，不会多次执行。
	 */
	protected void activityCreated(Bundle savedInstanceState) {
	};

	/**
	 * 请求网络之前
	 */
	protected void initDataOnStart(String url) {
	}

	/**
	 * 请求网络成功
	 */
	protected void initDataOnSucess(String result, String url) {
	}

	/**
	 * 请求网络失败
	 */
	protected void initDataOnFailure(String url) {
	}
}
