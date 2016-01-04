package com.edaoyou.collections;

import android.app.Application;

import com.easemob.chat.EMChat;
import com.edaoyou.collections.bean.User;
import com.edaoyou.collections.engine.DataManager;
import com.edaoyou.collections.utils.Util;
import com.igexin.sdk.PushManager;

public class MyApplication extends Application {

	private User mUser;

	@Override
	public void onCreate() {
		boolean applicationRepeat = Util.isApplicationRepeat(this);
		if (applicationRepeat) {
			return;
		}
		EMChat.getInstance().init(this);
		EMChat.getInstance().setDebugMode(true);

		PushManager.getInstance().initialize(this.getApplicationContext());
	}

	public User getUser() {
		return mUser;
	}

	public void setUser(User user) {
		this.mUser = user;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		DataManager.getInstance().clearDataStateMap();
	}
}