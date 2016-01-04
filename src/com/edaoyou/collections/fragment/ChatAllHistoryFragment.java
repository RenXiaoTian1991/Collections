package com.edaoyou.collections.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.ChatActivity;
import com.edaoyou.collections.adapter.ChatAllHistoryAdapter;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.bean.Bean.ChatUserListData;
import com.edaoyou.collections.bean.ChatUserList;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.Util;
import com.etsy.XListView;
import com.etsy.XListView.IXListViewListener;

public class ChatAllHistoryFragment extends BaseFragment {
	private XListView chat_all_history_XListView;
	private ChatAllHistoryAdapter mChatAllHistoryAdapter;
	private List<ChatUserListData> mChatHistoryListDatas;
	private List<ChatUserListData> mAllChatHistoryListDatas = new ArrayList<ChatUserListData>();
	private TextView chat_empty;
	private AlertDialog mAlertDialog;
	private String mChatListUrl; // 私聊列表url
	private String mDeleteChatListUrl; // 删除私聊列表url
	private String mReadChatListUrl; // 以读消息
	private String mSetTopChatUrl; // 以读消息
	private String mCancelTopChatUrl; // 以读消息
	private String mUid;
	private String mSid;
	private String mLastFid = "";
	private int mGetCount = 0;
	private int mIsMore = 1; // 是否有更多数据
	private int mCount = 10;// 每次访问的条数

	private int mClickChatPosition;// 点击某个条目的position
	private int mMorePosition;// 点击更多的position

	private boolean isDialogSetRead; // 是否是从dialog中点击的设置已读

	private static final int PULL_FLAG_NEW = 0; // 首次读取,和下拉刷新
	private static final int PULL_FLAG_UP = 1; // 上拉刷新,加载更多
	private int mCurrentFlag = PULL_FLAG_NEW;

	public static final int MSG_STOP_REFRESH = 1; // 停止刷新
	public static final int MSG_DELETE_CHAT = 2;// 删除聊天
	public static final int MSG_MORE = 3;// 更多

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_STOP_REFRESH:
				stopRefresh();
				break;
			case MSG_DELETE_CHAT:
				int deletePosition = (Integer) msg.obj;
				onDeleteChatClick(deletePosition);
				break;
			case MSG_MORE:
				mMorePosition = (Integer) msg.obj;
				onMoreClick();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		mCurrentFlag = PULL_FLAG_NEW;
		if (mChatAllHistoryAdapter != null) {
			mChatAllHistoryAdapter.setData(null);
			mChatAllHistoryAdapter.notifyDataSetChanged();
		}
		getChatListData();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		DataManager.getInstance().setLoadedDataState(mChatListUrl, false);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	protected void initVariable() {
		mChatListUrl = ConstantValue.COMMONURI + ConstantValue.DISCUSS_LIST_BY_USER;
		mDeleteChatListUrl = ConstantValue.COMMONURI + ConstantValue.DELETE_DISCUSS;
		mReadChatListUrl = ConstantValue.COMMONURI + ConstantValue.DISCUSS_SET_READ;
		mSetTopChatUrl = ConstantValue.COMMONURI + ConstantValue.DISCUSS_SET_TOP;
		mCancelTopChatUrl = ConstantValue.COMMONURI + ConstantValue.DISCUSS_CANCEL_TOP;
		mUid = UserUtil.getUserUid(mContext);
		mSid = UserUtil.getUserSid(mContext);
	}

	@Override
	protected int setContentView() {
		return R.layout.fragment_chat_all_history;
	}

	@Override
	protected void findViews(View rootView) {
		chat_all_history_XListView = (XListView) rootView.findViewById(R.id.chat_all_history_XListView);
		chat_empty = (TextView) rootView.findViewById(R.id.chat_empty);
	}

	@Override
	protected void setListensers() {
		chat_all_history_XListView.setPullRefreshEnable(true);
		chat_all_history_XListView.setPullLoadEnable(true);
		chat_all_history_XListView.setXListViewListener(new MyIXListViewListener());
		chat_all_history_XListView.setOnItemClickListener(new MyOnItemClickListener());
		chat_all_history_XListView.setEmptyView(chat_empty);
	}

	@Override
	protected String getLevel() {
		return GlobalParams.LEVEL_2_CHAT_NOTIFICATION;
	}

	public void refreshUI() {
		getChatListData();
	}

	public void getChatListData() {
		if (mIsMore != 1) {
			chat_all_history_XListView.hideFooterView();
			return;
		}
		JSONObject chatListJsonObject = getChatListJsonObject();
		initData(mChatListUrl, chatListJsonObject);
	}

	private void getChatListDataAgain() {
		if (mIsMore != 1) {
			chat_all_history_XListView.hideFooterView();
			return;
		}
		JSONObject chatListJsonObject = getChatListJsonObject();
		initData(mChatListUrl, chatListJsonObject);
	}

	private void setParameter() {
		if (mAllChatHistoryListDatas.isEmpty()) {
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 1000);
			return;
		}
		switch (mCurrentFlag) {
		case PULL_FLAG_NEW:
			mIsMore = 1;
			mGetCount = mAllChatHistoryListDatas.size();
			break;
		case PULL_FLAG_UP:
			mIsMore = 1;
			mGetCount = mAllChatHistoryListDatas.size();
			mLastFid = mAllChatHistoryListDatas.get(mAllChatHistoryListDatas.size() - 1).getId();
			break;
		default:
			break;
		}
	}

	/**
	 * json={"uid":"1","sid":"0123456789ABCDEF0123456789ABCDEF","ver":"1",
	 * "request":{"count":3,"last_id":"12","flag":1,"get_count":"10"} } 备注 flag
	 * 0首次读取 1拉旧数据(上拉刷新) 2拉新数据(下拉刷新) count 每次读取条数
	 * last_id(flag=1时,需传get_count:当前已拉取的条数, flag=2时last_id为最新一条数据的ID)
	 * //此处是按cid排序，非id排序 flag=2时，一次返回所有数据
	 */
	private JSONObject getChatListJsonObject() {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("last_id", mLastFid);
			request.put("count", mCount);
			request.put("get_count", mGetCount);
			request.put("flag", mCurrentFlag);

			json.put("sid", mSid);
			json.put("uid", mUid);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void initDataOnSucess(String result, String url) {
		super.initDataOnSucess(result, url);
		if (!isAdded()) {
			return;
		}
		if (url.equals(mChatListUrl)) {
			refreshList(url, result);
		} else if (url.equals(mDeleteChatListUrl)) {
			getChatListData();
		} else if (url.equals(mReadChatListUrl)) {
			if (!isDialogSetRead) {
				gotoChatActivity(mClickChatPosition);
			}
		} else if (url.equals(mSetTopChatUrl)) {
			onSetTopSucess(result);
		} else if (url.equals(mCancelTopChatUrl)) {
			onCancelTopSucess(result);
		}
	}

	/**
	 * 聊天顶置结果返回
	 */
	private void onSetTopSucess(String result) {
		try {
			JSONObject object = new JSONObject(result);
			Log.i("abc", "object--->" + object.toString());
			int ret = object.optInt("ret");
			if (ret == 0) {// 成功
				Log.i("abc", "set--get--new");
				getChatListData();
			} else if (ret == -1) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消聊天顶置结果返回
	 */
	private void onCancelTopSucess(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int ret = object.optInt("ret");
			if (ret == 0) {// 成功
				Log.i("abc", "Cancel--get--new");
				getChatListData();
			} else if (ret == -1) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initDataOnFailure(String url) {
		super.initDataOnFailure(url);
		mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 500);
	}

	private void refreshList(String url, String result) {
		Log.i("abc", "result-->" + result);
		ChatUserList mChatHistoryList = null;
		try {
			mChatHistoryList = GsonUtils.json2bean(result, ChatUserList.class);
			if (mCurrentFlag == PULL_FLAG_UP) {
				mIsMore = mChatHistoryList.getResponse().getMore();
			}
			mChatHistoryListDatas = mChatHistoryList.getResponse().getList();
			if (mChatHistoryListDatas == null) {
				mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 1000);
				return;
			}
			if (mCurrentFlag == PULL_FLAG_NEW) {
				mAllChatHistoryListDatas.clear();
			}
			mAllChatHistoryListDatas.addAll(mChatHistoryListDatas);
			if (mChatAllHistoryAdapter == null) {
				mChatAllHistoryAdapter = new ChatAllHistoryAdapter(mContext, mBitmapUtils, mChatHistoryListDatas, mHandler);
				chat_all_history_XListView.setAdapter(mChatAllHistoryAdapter);
			} else {
				mChatAllHistoryAdapter.setData(mAllChatHistoryListDatas);
				mChatAllHistoryAdapter.notifyDataSetChanged();
			}
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 0);
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 0);
		}
		DataManager.getInstance().setLoadedDataState(url, true);
	}

	/**
	 * 停止刷新
	 */
	private void stopRefresh() {
		if (!isAdded()) {
			return;
		}
		if (mIsMore != 1) {
			Toast.makeText(mContext, "没有更多数据了，亲", Toast.LENGTH_SHORT).show();
		}
		chat_all_history_XListView.hideFooterView();
		chat_all_history_XListView.stopLoadMore();
		chat_all_history_XListView.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		chat_all_history_XListView.setRefreshTime(getString(R.string.xlistview_header_last_time,
				simpleDateFormat.format(new Date(System.currentTimeMillis()))));
	}

	/**
	 * 点击删除
	 */
	private void onDeleteChatClick(int position) {
		JSONObject deleteChatListJsonObject = getDeleteOrReadJsonObject(position);
		initData(mDeleteChatListUrl, deleteChatListJsonObject);
	}

	/**
	 * json={"uid":"1","sid":"0123456789ABCDEF0123456789ABCDEF","ver":"1",
	 * "request":{"id":"6","uid":"19"} }
	 */
	private JSONObject getDeleteOrReadJsonObject(int position) {
		ChatUserListData chatUserListData = mAllChatHistoryListDatas.get(position);
		String cid = chatUserListData.getCid();
		String uid = chatUserListData.getUid();
		Log.i("abc", "cid--->" + cid);
		Log.i("abc", "uid--->" + uid);
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("id", cid);
			request.put("uid", uid);

			json.put("sid", mSid);
			json.put("uid", mUid);
			json.put("ver", GlobalParams.ver);
			json.put("request", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private void readChat(int position) {
		JSONObject deleteChatListJsonObject = getDeleteOrReadJsonObject(position);
		initData(mReadChatListUrl, deleteChatListJsonObject);
	}

	/**
	 * 聊天顶置
	 */
	private void setTopChat() {
		JSONObject deleteChatListJsonObject = getDeleteOrReadJsonObject(mMorePosition);
		initData(mSetTopChatUrl, deleteChatListJsonObject);
	}

	/**
	 * 取消聊天顶置
	 */
	private void cancelTopChat() {
		JSONObject deleteChatListJsonObject = getDeleteOrReadJsonObject(mMorePosition);
		initData(mCancelTopChatUrl, deleteChatListJsonObject);
	}

	/**
	 * 点击更多
	 */
	private void onMoreClick() {
		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.popupWindowAnimation);
		int screenWidth = Util.getDisplayWidth(mContext);
		window.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_chat_more, null);
		window.setContentView(contentView);
		TextView dialog_chat_read_tv = (TextView) window.findViewById(R.id.dialog_chat_read_tv);
		TextView dialog_top_chat_tv = (TextView) window.findViewById(R.id.dialog_top_chat_tv);
		TextView dialog_chat_cancel = (TextView) window.findViewById(R.id.dialog_chat_cancel);
		dialog_chat_read_tv.setOnClickListener(mDialogClickListener);
		dialog_top_chat_tv.setOnClickListener(mDialogClickListener);
		dialog_chat_cancel.setOnClickListener(mDialogClickListener);

		ChatUserListData chatUserListData = mAllChatHistoryListDatas.get(mMorePosition);
		String isTop = chatUserListData.getTop();
		if ("1".equals(isTop)) {
			dialog_top_chat_tv.setText("取消置顶");
		} else {
			dialog_top_chat_tv.setText("置顶聊天");
		}
	}

	/**
	 * 跳转到聊天界面
	 */
	private void gotoChatActivity(int position) {
		Intent intent = new Intent(mContext, ChatActivity.class);
		String toUid = mAllChatHistoryListDatas.get(position).getUid();
		String toName = mAllChatHistoryListDatas.get(position).getNick();
		intent.putExtra(GlobalParams.USER_UID, toUid);
		intent.putExtra(GlobalParams.USER_NAME, toName);
		startActivity(intent);
	}

	private class MyIXListViewListener implements IXListViewListener {

		@Override
		public void onRefresh() {
			mCurrentFlag = PULL_FLAG_NEW;
			setParameter();
			getChatListDataAgain();
		}

		@Override
		public void onLoadMore() {
			mCurrentFlag = PULL_FLAG_UP;
			setParameter();
			getChatListDataAgain();
		}

		@Override
		public void onRightSlip() {

		}

		@Override
		public void onLeftSlip() {

		}
	}

	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			position--;
			mClickChatPosition = position;
			isDialogSetRead = false;
			readChat(position);
		}
	}

	private OnClickListener mDialogClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.dialog_chat_read_tv:
				isDialogSetRead = true;
				readChat(mMorePosition);
				break;
			case R.id.dialog_top_chat_tv:
				ChatUserListData chatUserListData = mAllChatHistoryListDatas.get(mMorePosition);
				String isTop = chatUserListData.getTop();
				Log.i("abc", "mMorePosition-->" + mMorePosition);
				if ("1".equals(isTop)) {
					Log.i("abc", "qu-xiao-->");
					cancelTopChat();
				} else {
					Log.i("abc", "set-->");
					setTopChat();
				}
				break;
			case R.id.dialog_chat_cancel:
				break;
			default:
				break;
			}
			mAlertDialog.cancel();
		}
	};

}
