package com.edaoyou.collections.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.edaoyou.collections.R;
import com.edaoyou.collections.base.BaseActivity;

public class ShareFriendByPhoneActivity extends BaseActivity implements OnClickListener {
	private TextView friend_name_tv;
	private TextView friend_number_tv;
	private TextView set_name_tv;
	private TextView call_phone_friend_tv;
	private Bundle extras;
	private String userNumber;
	private String userName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		extras = this.getIntent().getExtras();
		userName = extras.getString("name");
		userNumber = extras.getString("number");
		
		friend_name_tv.setText(userName);
		friend_number_tv.setText(userNumber);
		set_name_tv.setText(userName+"还未开通藏家账号");
		
	}

	@Override
	protected int setContentView() {
		return R.layout.activity_share_friend_phone;
	}

	@Override
	protected void findViews() {
		friend_name_tv = (TextView) findViewById(R.id.friend_name_tv);
		friend_number_tv = (TextView) findViewById(R.id.friend_number_tv);
		set_name_tv = (TextView) findViewById(R.id.set_name_tv);
		call_phone_friend_tv = (TextView) findViewById(R.id.call_phone_friend_tv);
		
	}

	@Override
	protected void setListensers() {
		call_phone_friend_tv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		 Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://"+userNumber));    
         startActivity(intent); 
	}

}
