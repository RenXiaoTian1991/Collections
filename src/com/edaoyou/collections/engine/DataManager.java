package com.edaoyou.collections.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataManager {

	private static DataManager mDataManager;
	// 访问网络状态信息的存储
	private Map<String, Boolean> mDataStateMap;
	// 已经执行过的方法
	private Set<String> mHasExecuteMethodNames;

	private DataManager() {
		mDataStateMap = new HashMap<String, Boolean>();
		mHasExecuteMethodNames = new HashSet<String>();
	}

	public synchronized static DataManager getInstance() {
		if (mDataManager == null) {
			mDataManager = new DataManager();
		}
		return mDataManager;
	}

	/**
	 * 设置该URL数据展示状态
	 */
	public void setLoadedDataState(String url, boolean state) {
		mDataStateMap.put(url, state);
	}

	/**
	 * 得到该URL数据展示状态
	 */
	public boolean getLoadedDataState(String url) {
		if (mDataStateMap.get(url) == null) {
			return false;
		}
		return mDataStateMap.get(url);
	}

	public void clearDataStateMap() {
		mDataStateMap.clear();
	}

	/**
	 * 方法是否已经执行,没执行添加到已执行集合
	 * 
	 * @param methodName方法名
	 */
	public boolean hasExecute(String methodName) {
		if (mHasExecuteMethodNames.contains(methodName)) {
			return true;
		} else {
			mHasExecuteMethodNames.add(methodName);
			return false;
		}
	}

	public void clearHasExecuteSet() {
		mHasExecuteMethodNames.clear();
	}

}