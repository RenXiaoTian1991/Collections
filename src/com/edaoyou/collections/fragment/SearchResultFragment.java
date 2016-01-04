package com.edaoyou.collections.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.activity.ChatActivity;
import com.edaoyou.collections.activity.FeedActivity;
import com.edaoyou.collections.activity.PersonalHomepageActivity;
import com.edaoyou.collections.activity.SearchMoreActivity;
import com.edaoyou.collections.activity.WebActivity;
import com.edaoyou.collections.adapter.SearchResultAdapter;
import com.edaoyou.collections.adapter.SearchResultAdapter.SearchArticlesHolder;
import com.edaoyou.collections.adapter.SearchResultAdapter.SearchChatsHolder;
import com.edaoyou.collections.adapter.SearchResultAdapter.SearchLikesHolder;
import com.edaoyou.collections.adapter.SearchResultAdapter.SearchTagHolder;
import com.edaoyou.collections.adapter.SearchResultAdapter.SearchUsersHolder;
import com.edaoyou.collections.base.BaseFragment;
import com.edaoyou.collections.bean.SearchResult;
import com.edaoyou.collections.utils.DialogUtil;
import com.edaoyou.collections.utils.UserUtil;

public class SearchResultFragment extends BaseFragment {
	private ListView mResultListView;
	private SearchResultAdapter mResultAdapter;
	private String mSearchKey;

	public void setSearchResult(SearchResult result, String searchKey) {
		mSearchKey = searchKey;
		mResultAdapter = new SearchResultAdapter(getActivity(), mBitmapUtils, result, mSearchKey);
		mResultListView.setAdapter(mResultAdapter);
		mResultAdapter.notifyDataSetChanged();
	}

	public ListView getListView() {
		return mResultListView;
	}

	@Override
	protected int setContentView() {
		return R.layout.fragment_search_result;
	}

	@Override
	protected void findViews(View rootView) {
		mResultListView = (ListView) rootView.findViewById(R.id.search_result_lv);
	}

	@Override
	protected void setListensers() {
		mResultListView.setOnItemClickListener(mItemClickListener);
	}

	@Override
	protected String getLevel() {
		return null;
	}

	@Override
	protected void initVariable() {

	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (mResultAdapter.getItemViewType(position)) {
			case SearchResultAdapter.SEARCH_ITEM_FOLLOWS:
			case SearchResultAdapter.SEARCH_ITEM_USERS:
				if (view.getTag() instanceof SearchUsersHolder) {
					SearchUsersHolder usersHolder = (SearchUsersHolder) view.getTag();
					Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
					intent.putExtra(GlobalParams.USER_UID, usersHolder.UID);
					startActivity(intent);
				}
				break;
			case SearchResultAdapter.SEARCH_ITEM_CHATS:
				if (view.getTag() instanceof SearchChatsHolder) {
					SearchChatsHolder chatsHolder = (SearchChatsHolder) view.getTag();
					if (chatsHolder.UID.equals(UserUtil.getUserUid(getActivity()))) {
						DialogUtil.showChaHintDialog(getActivity(), R.string.chat_hint_txt);
						return;
					} else if (TextUtils.isEmpty(UserUtil.getUserUid(getActivity()))) {
						DialogUtil.showChaHintDialog(getActivity(), R.string.chat_hint_txt_no_login);
						return;
					}
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					intent.putExtra(GlobalParams.USER_UID, chatsHolder.UID);
					intent.putExtra(GlobalParams.USER_NAME, chatsHolder.userName);
					startActivity(intent);
				}
				break;
			case SearchResultAdapter.SEARCH_ITEM_LIKES:
				if (view.getTag() instanceof SearchLikesHolder) {
					SearchLikesHolder likesHolder = (SearchLikesHolder) view.getTag();
					Intent intent = new Intent(getActivity(), FeedActivity.class);
					intent.putExtra(GlobalParams.FEED_FID, likesHolder.FID);
					startActivity(intent);
				}
				break;
			case SearchResultAdapter.SEARCH_ITEM_TAGS:
				Toast.makeText(mContext, "相关标签被点击", Toast.LENGTH_SHORT).show();
				if (view.getTag() instanceof SearchTagHolder) {
				}
				break;
			case SearchResultAdapter.SEARCH_ITEM_ARTICLES:
				if (view.getTag() instanceof SearchArticlesHolder) {
					SearchArticlesHolder articlesHolder = (SearchArticlesHolder) view.getTag();
					Intent intent = new Intent(mContext, WebActivity.class);
					intent.putExtra(GlobalParams.WEB_URL, articlesHolder.URL);
					intent.putExtra(GlobalParams.WEB_CONTENT, articlesHolder.content);
					intent.putExtra(GlobalParams.WEB_TITLE, articlesHolder.title);
					intent.putExtra(GlobalParams.WEB_PHOTO, articlesHolder.photoUrl);
					startActivity(intent);
				}
				break;
			case SearchResultAdapter.SEARCH_ITEM_USER_MORE:
				startSearchMoreActivity(SearchMoreActivity.SEARCH_USER);
				break;
			case SearchResultAdapter.SEARCH_ITEM_FOLLOW_MORE:
				startSearchMoreActivity(SearchMoreActivity.SEARCH_FOLLOW);
				break;
			case SearchResultAdapter.SEARCH_ITEM_ARTICLES_MORE:
				startSearchMoreActivity(SearchMoreActivity.SEARCH_ARTICLE);
				break;
			case SearchResultAdapter.SEARCH_ITEM_LIKE_MORE:
				startSearchMoreActivity(SearchMoreActivity.SEARCH_LIKE);
				break;
			case SearchResultAdapter.SEARCH_ITEM_CHAT_MORE:
				startSearchMoreActivity(SearchMoreActivity.SEARCH_CHAT);
				break;
			default:
				break;
			}
		}

		private void startSearchMoreActivity(int type) {
			Intent intent = new Intent(mContext, SearchMoreActivity.class);
			intent.putExtra(GlobalParams.SEARCH_KEY, mSearchKey);
			intent.putExtra(GlobalParams.SEARCH_MORE_TYPE, type);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		}
	};
}
