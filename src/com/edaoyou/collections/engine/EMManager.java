package com.edaoyou.collections.engine;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.edaoyou.collections.GlobalParams;

public class EMManager {
	private static EMManager mEMManager;

	private EMManager() {
	}

	public synchronized static EMManager getInstance() {
		if (mEMManager == null) {
			mEMManager = new EMManager();
		}
		return mEMManager;
	}

	/**
	 * 登录
	 */
	public void login(EMCallBack emEMCallBack) {
		EMChatManager.getInstance().login(GlobalParams.EM_NAME, GlobalParams.EM_PASSWORD, emEMCallBack);
	}

	/**
	 * 退出登录
	 */
	public void logout() {
		EMChatManager.getInstance().logout();
	}

	/**
	 * 获取未读消息数
	 * 
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}
}