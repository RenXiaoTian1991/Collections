package com.edaoyou.collections.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.edaoyou.collections.ConstantValue;
import com.edaoyou.collections.GlobalParams;
import com.edaoyou.collections.R;
import com.edaoyou.collections.adapter.AddfriendPhoneAdapter;
import com.edaoyou.collections.base.BaseActivity;
import com.edaoyou.collections.bean.Bean.Contact;
import com.edaoyou.collections.bean.Contacts;
import com.edaoyou.collections.bean.PhoneFriend;
import com.edaoyou.collections.utils.UserUtil;
import com.edaoyou.collections.utils.GsonUtils;
import com.edaoyou.collections.utils.SharedPreferencesUtils;

public class AddfriendPhoneActivity extends BaseActivity implements OnClickListener {
	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	private final String FIRST_GOON = "0";
	private final String NO_FIRST_GOON = "1";
	private final String IS_FIRST_GOON = "IS_FIRST_GOON";

	private RelativeLayout first_addfriend_phone_rl;
	private RelativeLayout show_addfriend_list_rl;
	private Button addfriend_from_phone_bnt;
	private ListView friend_list_lv;
	private JSONArray mJsonArray;

	private List<PhoneFriend> mPhoneList;

	private AddfriendPhoneAdapter mAddfriendPhoneAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isVisibleAddFriend();
		mPhoneList = new ArrayList<PhoneFriend>();
		addFriendFromPhone();
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_addfriend_phone;
	}

	@Override
	protected void findViews() {

		first_addfriend_phone_rl = (RelativeLayout) findViewById(R.id.first_addfriend_phone_rl);
		show_addfriend_list_rl = (RelativeLayout) findViewById(R.id.show_addfriend_list_rl);
		addfriend_from_phone_bnt = (Button) findViewById(R.id.addfriend_from_phone_bnt);
		friend_list_lv = (ListView) findViewById(R.id.friend_list_lv);

	}

	@Override
	protected void setListensers() {
		addfriend_from_phone_bnt.setOnClickListener(this);
	}

	private void addFriendFromPhone() {
		String mUrl = ConstantValue.COMMONURI + ConstantValue.CHECK_CONTACTS;
		String mVer = GlobalParams.ver;
		String mSid = (String) UserUtil.getUserSid(this);
		String mUid = (String) UserUtil.getUserUid(this);
		// 获得手机通讯录
		getPhoneContacts();

		JSONObject json = getJSONObject(mVer, mSid, mUid);
		initData(mUrl, json);
	}

	private JSONObject getJSONObject(String mVer, String mSid, String mUid) {
		JSONObject json = new JSONObject();
		JSONObject request = new JSONObject();
		try {
			request.put("mobile", mJsonArray);
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

		Contacts jsonBean = GsonUtils.json2bean(result, Contacts.class);
		List<Contact> contactList = jsonBean.response.list;
		int count = jsonBean.response.count;
		mAddfriendPhoneAdapter = new AddfriendPhoneAdapter(mContext, contactList, mPhoneList, count, mBitmapUtils);
		friend_list_lv.setAdapter(mAddfriendPhoneAdapter);
		mAddfriendPhoneAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addfriend_from_phone_bnt:
			first_addfriend_phone_rl.setVisibility(View.INVISIBLE);
			show_addfriend_list_rl.setVisibility(View.VISIBLE);
			SharedPreferencesUtils.getInstance(this).saveString(IS_FIRST_GOON, NO_FIRST_GOON);
			break;

		default:
			break;
		}
	}

	private void isVisibleAddFriend() {
		String flag = SharedPreferencesUtils.getInstance(this).getString(IS_FIRST_GOON);
		if (FIRST_GOON == flag || flag == null) {
			first_addfriend_phone_rl.setVisibility(View.VISIBLE);
			show_addfriend_list_rl.setVisibility(View.INVISIBLE);
		} else {
			first_addfriend_phone_rl.setVisibility(View.GONE);
			show_addfriend_list_rl.setVisibility(View.VISIBLE);
		}
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = this.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

		mJsonArray = new JSONArray();

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				PhoneFriend friend = new PhoneFriend();

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(1);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}

				// 得到联系人名称
				String contactName = phoneCursor.getString(0);

				mJsonArray.put(phoneNumber);
				friend.setName(contactName);
				friend.setNumber(phoneNumber);
				mPhoneList.add(friend);
			}
			phoneCursor.close();
		}
	}
}
