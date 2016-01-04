package com.edaoyou.collections.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.SearchResult;
import com.edaoyou.collections.fragment.SearchInitFragment;
import com.edaoyou.collections.fragment.SearchResultFragment;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.QSLog;
import com.edaoyou.collections.utils.SharedPreferencesUtils;

public class SearchActivity extends BaseActivity implements OnClickListener {
	private static final int SEARCH_INIT = 0;
	private static final int SEARCH_RESULT = 1;

	private String UID;
	private String SID;
	private String mSearchKey = null;

	private EditText search_et = null;
	private TextView search_cancel_tv = null;
	private RelativeLayout search_load_rl;

	private List<Fragment> mFragments = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UID = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_UID);
		SID = SharedPreferencesUtils.getInstance(mContext).getString(GlobalParams.USER_SID);
		mFragments.add(new SearchInitFragment());
		mFragments.add(new SearchResultFragment());

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (!mFragments.get(SEARCH_INIT).isAdded()) {
			ft.add(R.id.search_main_fl, mFragments.get(SEARCH_INIT));
		}
		if (!mFragments.get(SEARCH_RESULT).isAdded()) {
			ft.add(R.id.search_main_fl, mFragments.get(SEARCH_RESULT));
		}
		ft.commit();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_search;
	}

	@Override
	protected void findViews() {
		search_et = (EditText) findViewById(R.id.search_et);
		search_cancel_tv = (TextView) findViewById(R.id.search_cancel_tv);
		search_load_rl = (RelativeLayout) findViewById(R.id.search_load_rl);
	}

	@Override
	protected void setListensers() {
		search_cancel_tv.setOnClickListener(this);
		search_et.setOnEditorActionListener(new SearchListener());
	}

	private JSONObject getSearchResultJson(String searchKey) {
		JSONObject request = new JSONObject();
		try {
			request.put("kw", searchKey);
			return GsonUtils.getJSONObjectForUSer(getApplicationContext(), request);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(search_et.getWindowToken(), 0);
			}
		}
	}

	private void searchResult(String searchKey) {
		hideKeyboard();
		search_load_rl.setVisibility(View.VISIBLE);
		String mSearchResultUrl = ConstantValue.COMMONURI + ConstantValue.SEARCH_RESULT;
		JSONObject mSearchResultJsonObject = getSearchResultJson(searchKey);
		initData(mSearchResultUrl, mSearchResultJsonObject);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (search_load_rl.isShown()) {
				search_load_rl.setVisibility(View.GONE);
				cancelLoad();
			} else {
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.push_right_out);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void initDataOnSucess(String result, String url, int type) {
		QSLog.d(result);
		SearchResult searchResult = GsonUtils.json2bean(result, SearchResult.class);
		search_load_rl.setVisibility(View.GONE);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (!mFragments.get(SEARCH_RESULT).isAdded()) {
			ft.add(R.id.search_main_fl, mFragments.get(SEARCH_RESULT));
		}
		ft.hide(mFragments.get(SEARCH_INIT));
		SearchResultFragment resultFragment = (SearchResultFragment) mFragments.get(SEARCH_RESULT);
		resultFragment.setSearchResult(searchResult, search_et.getText().toString());
		ft.show(resultFragment);
		ft.commit();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.search_cancel_tv:
			finish();
			break;

		default:
			break;
		}
	}

	private class SearchListener implements TextView.OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			mSearchKey = search_et.getText().toString();
			if (mSearchKey != null && !mSearchKey.equals("")) {
				searchResult(mSearchKey);
			} else {
				Toast.makeText(SearchActivity.this, SearchActivity.this.getString(R.string.search_key_null), Toast.LENGTH_SHORT).show();
			}

			return true;
		}
	}
}