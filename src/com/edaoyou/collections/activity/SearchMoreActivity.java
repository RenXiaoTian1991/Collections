package com.edaoyou.collections.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.SearchMoreAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Article;
import com.edaoyou.collections.bean.Bean.ChatItem;
import com.edaoyou.collections.bean.Bean.Like;
import com.edaoyou.collections.bean.SearchMoreResult.SearchArticleResult;
import com.edaoyou.collections.bean.SearchMoreResult.SearchChatResult;
import com.edaoyou.collections.bean.SearchMoreResult.SearchLikeResult;
import com.edaoyou.collections.bean.SearchMoreResult.SearchUserResult;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.utils.DialogUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.QSLog;
import com.edaoyou.collections.utils.UserUtil;
import com.etsy.XListView;
import com.etsy.XListView.IXListViewListener;

public class SearchMoreActivity extends BaseActivity implements OnClickListener {
	public static final int SEARCH_FOLLOW = 1;// 关注的人类型
	public static final int SEARCH_CHAT = 2;// 聊天类行
	public static final int SEARCH_LIKE = 3;// 喜欢类型
	public static final int SEARCH_USER = 5;// 相关用户类型
	public static final int SEARCH_ARTICLE = 6;// 相关文章
	private static final int SEARCH_FIRST = 0;// 第一次和下拉加载
	private static final int SEARCH_LOAD = 1;// 上拉加载

	private Button search_more_exit_bt;// 退出按钮
	private XListView search_more_list;// 存放数据的listView
	private EditText search_et;// 搜索框
	private TextView search_item_top_tv;// 显示搜索结果的类型

	private int mType;// 搜索类型
	private String mSearchKey;// 搜索的索引
	private SearchMoreAdapter mSearchMoreAdapter;// 显示数据家的adapter
	private int flag = SEARCH_FIRST;// 加载数据的方式 flag 0首次读取 1拉旧数据(上拉刷新)
									// 2拉新数据(下拉刷新)
	private int mIsMoreData = 1; // 是否有更多数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mType = intent.getIntExtra(GlobalParams.SEARCH_MORE_TYPE, 0);
		mSearchKey = intent.getStringExtra(GlobalParams.SEARCH_KEY);
		search_et.setText(mSearchKey);
		switch (mType) {
		case SEARCH_USER:
			search_item_top_tv.setText(getString(R.string.search_user));
			break;
		case SEARCH_FOLLOW:
			search_item_top_tv.setText(getString(R.string.search_follow));
			break;
		case SEARCH_ARTICLE:
			search_item_top_tv.setText(getString(R.string.search_articles));
			break;
		case SEARCH_LIKE:
			search_item_top_tv.setText(getString(R.string.search_like));
			break;
		case SEARCH_CHAT:
			search_item_top_tv.setText(getString(R.string.search_chat));
			break;
		default:
			break;
		}
		getMoreData(10, "", 0);
	}

	/**
	 * 访问网络获取更多数据
	 * 
	 * @param count
	 *            加载多少条数据
	 * @param lastId
	 *            (flag=1时last_fid为最后一条数据的FeedID, flag=2时last_fid为最新一条数据的FeedID)
	 * @param flag
	 *            0首次读取 1拉旧数据(上拉刷新) 2拉新数据(下拉刷新)
	 */
	private void getMoreData(int count, String lastId, int flag) {
		String url = ConstantValue.COMMONURI + ConstantValue.SEARCH_MORE;
		initData(url, GsonUtils.getJSONObjectForUSer(getApplicationContext(), getMoreJsonObject(count, lastId, flag)));
	}

	/**
	 * 获得获取更多数据JSONObject 对象
	 * 
	 * @param count
	 *            加载多少条数据
	 * @param lastId
	 *            (flag=1时last_fid为最后一条数据的FeedID, flag=2时last_fid为最新一条数据的FeedID)
	 * @param flag
	 *            0首次读取 1拉旧数据(上拉刷新) 2拉新数据(下拉刷新)
	 */
	public JSONObject getMoreJsonObject(int count, String lastId, int flag) {
		JSONObject request = new JSONObject();
		try {
			request.put("type", mType);
			request.put("kw", mSearchKey);
			request.put("count", count);
			request.put("last_id", lastId);
			request.put("flag", flag);
			return request;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		QSLog.d(result);
		setData(result);
		super.initDataOnSucess(result, url, type);
	}

	/**
	 * 停止下拉刷新
	 */
	private void stopRefresh() {
		search_more_list.stopRefresh();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		search_more_list
				.setRefreshTime(getString(R.string.xlistview_header_last_time, simpleDateFormat.format(new Date(System.currentTimeMillis()))));
	}

	/**
	 * 停止上拉加载
	 */
	private void stopLoadMore() {
		search_more_list.stopLoadMore();
		search_more_list.hideFooterView();
	}

	/**
	 * 显示对应的数据
	 * 
	 * @param result
	 */
	private void setData(String result) {
		if (mSearchMoreAdapter == null) {
			mSearchMoreAdapter = new SearchMoreAdapter(getApplicationContext(), mType, mBitmapUtils);
		}
		mSearchMoreAdapter.setSearchKey(mSearchKey);
		switch (mType) {
		case SEARCH_USER:
		case SEARCH_FOLLOW:
			setUserData(result);
			break;
		case SEARCH_ARTICLE:
			setArticleData(result);
			break;
		case SEARCH_LIKE:
			setLikeData(result);
			break;
		case SEARCH_CHAT:
			setChatData(result);
			break;
		default:
			break;
		}
		if (flag == SEARCH_FIRST) {
			stopRefresh();
			QSLog.d("mIsMoreData = " + mIsMoreData);
			if (mIsMoreData != 1) {
				stopLoadMore();
			}
		} else {
			stopLoadMore();
		}
		mSearchMoreAdapter.notifyDataSetChanged();
		search_more_list.setAdapter(mSearchMoreAdapter);
	}

	/**
	 * 显示聊天记录的数据
	 * 
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void setChatData(String result) {
		SearchChatResult chatResult = GsonUtils.json2bean(result, SearchChatResult.class);
		mIsMoreData = chatResult.response.more;
		if (chatResult.ret != 0) {
			stopRefresh();
			Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
			return;
		}
		switch (flag) {
		case SEARCH_FIRST:
			mSearchMoreAdapter.setDataList(chatResult.response.list);
			break;
		case SEARCH_LOAD:
			mSearchMoreAdapter.getDataList().addAll(chatResult.response.list);
			break;
		default:
			break;
		}
	}

	/**
	 * 显示赞过的数据
	 * 
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void setLikeData(String result) {
		SearchLikeResult likeResult = GsonUtils.json2bean(result, SearchLikeResult.class);
		mIsMoreData = likeResult.response.more;
		if (likeResult.ret != 0) {
			stopRefresh();
			Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
			return;
		}
		switch (flag) {
		case SEARCH_FIRST:
			mSearchMoreAdapter.setDataList(likeResult.response.list);
			break;
		case SEARCH_LOAD:
			mSearchMoreAdapter.getDataList().addAll(likeResult.response.list);
			break;
		default:
			break;
		}

	}

	/**
	 * 显示相关文章
	 * 
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void setArticleData(String result) {
		SearchArticleResult articleResult = GsonUtils.json2bean(result, SearchArticleResult.class);
		mIsMoreData = articleResult.response.more;
		if (articleResult.ret != 0) {
			stopRefresh();
			Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
			return;
		}
		switch (flag) {
		case SEARCH_FIRST:
			mSearchMoreAdapter.setDataList(articleResult.response.list);
			break;
		case SEARCH_LOAD:
			mSearchMoreAdapter.getDataList().addAll(articleResult.response.list);
			break;
		default:
			break;
		}
	}

	/**
	 * 显示相关用户
	 * 
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void setUserData(String result) {
		SearchUserResult searchUserResult = GsonUtils.json2bean(result, SearchUserResult.class);
		mIsMoreData = searchUserResult.response.more;
		if (searchUserResult.ret != 0) {
			stopRefresh();
			Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
			return;
		}
		switch (flag) {
		case SEARCH_FIRST:
			mSearchMoreAdapter.setDataList(searchUserResult.response.list);
			break;
		case SEARCH_LOAD:
			mSearchMoreAdapter.getDataList().addAll(searchUserResult.response.list);
			break;
		default:
			break;
		}
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_search_more;
	}

	@Override
	protected void findViews() {
		search_more_exit_bt = (Button) findViewById(R.id.search_more_exit_bt);
		search_et = (EditText) findViewById(R.id.search_et);
		search_more_list = (XListView) findViewById(R.id.search_more_list);
		initHeadView();
	}

	private void initHeadView() {
		View headView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.adapter_search_item_top, null);
		search_item_top_tv = (TextView) headView.findViewById(R.id.search_item_top_tv);
		search_more_list.addHeaderView(headView);
	}

	@Override
	protected void setListensers() {
		search_more_list.setPullLoadEnable(true);
		search_more_list.setPullRefreshEnable(true);
		search_more_exit_bt.setOnClickListener(this);
		search_more_list.setXListViewListener(mListViewListener);
		search_more_list.setOnItemClickListener(mItemClickListener);
		search_more_list.setEmptyView(findViewById(R.id.search_empty_tv));
		search_et.setOnEditorActionListener(mEditorActionListener);
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(search_et.getWindowToken(), 0);
			}
		}
	}

	// list字条目的点击的监听
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (mType) {
			case SEARCH_USER:
			case SEARCH_FOLLOW:
				if (position > 1) {
					User user = (User) mSearchMoreAdapter.getItem(position - 1);
					if (user != null && !TextUtils.isEmpty(user.uid)) {
						Intent intent = new Intent(SearchMoreActivity.this, PersonalHomepageActivity.class);
						intent.putExtra(GlobalParams.USER_UID, user.uid);
						startActivity(intent);
					}
				}
				break;
			case SEARCH_ARTICLE:
				if (position > 1) {
					Article article = (Article) mSearchMoreAdapter.getItem(position - 1);
					if (article != null && !TextUtils.isEmpty(article.url)) {
						Intent intent = new Intent(mContext, WebActivity.class);
						intent.putExtra(GlobalParams.WEB_URL, article.url);
						startActivity(intent);
					}
				}
				break;
			case SEARCH_LIKE:
				if (position > 1) {
					Like like = (Like) mSearchMoreAdapter.getItem(position - 1);
					Intent intent = new Intent(mContext, FeedActivity.class);
					intent.putExtra(GlobalParams.FEED_FID, like.fid);
					startActivity(intent);
				}
				break;
			case SEARCH_CHAT:
				if (position > 1) {
					ChatItem chatItem = (ChatItem) mSearchMoreAdapter.getItem(position - 1);
					if (chatItem.to.equals(UserUtil.getUserUid(getApplicationContext()))) {
						DialogUtil.showChaHintDialog(SearchMoreActivity.this,R.string.chat_hint_txt);
						return;
					} else if (TextUtils.isEmpty(UserUtil.getUserUid(getApplicationContext()))) {
						DialogUtil.showChaHintDialog(SearchMoreActivity.this,R.string.chat_hint_txt_no_login);
						return;
					}
					Intent intent = new Intent(mContext, ChatActivity.class);
					intent.putExtra(GlobalParams.USER_UID, chatItem.to);
					intent.putExtra(GlobalParams.USER_NAME, chatItem.nick);
					startActivity(intent);
				}
				break;

			default:
				break;
			}
		}
	};
	// 对Editor动作对监听
	private OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			mSearchKey = search_et.getText().toString();
			if (!TextUtils.isEmpty(mSearchKey)) {
				flag = SEARCH_FIRST;
				getMoreData(10, "", flag);
				hideKeyboard();
			} else {
				Toast.makeText(SearchMoreActivity.this, getString(R.string.search_key_null), Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	};
	// 对list的滑动的监听
	private IXListViewListener mListViewListener = new IXListViewListener() {

		@Override
		public void onRightSlip() {

		}

		@Override
		public void onRefresh() {
			flag = SEARCH_FIRST;
			getMoreData(search_more_list.getCount(), "", flag);

		}

		@Override
		public void onLoadMore() {
			flag = SEARCH_LOAD;
			if (mIsMoreData != 1) {
				stopLoadMore();
				Toast.makeText(SearchMoreActivity.this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
				return;
			}
			String lastId = "";
			switch (mType) {
			case SEARCH_USER:
				User user = (User) mSearchMoreAdapter.getItem(mSearchMoreAdapter.getCount());
				if (user != null) {
					lastId = user.uid;
				}
				break;
			case SEARCH_ARTICLE:
				Article article = (Article) mSearchMoreAdapter.getItem(mSearchMoreAdapter.getCount());
				if (article != null) {
					lastId = Integer.valueOf(article.id).toString();
				}
				break;
			case SEARCH_LIKE:
				Like like = (Like) mSearchMoreAdapter.getItem(mSearchMoreAdapter.getCount());
				if (like != null) {
					lastId = Integer.valueOf(like.fid).toString();
				}
				break;
			case SEARCH_CHAT:
				ChatItem chat = (ChatItem) mSearchMoreAdapter.getItem(mSearchMoreAdapter.getCount());
				if (chat != null) {
					lastId = chat.id;
				}
				break;

			default:
				break;
			}
			getMoreData(10, lastId, flag);
		}

		@Override
		public void onLeftSlip() {

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_more_exit_bt:
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
			break;

		default:
			break;
		}
	}

}
