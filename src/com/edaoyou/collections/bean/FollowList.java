package com.edaoyou.collections.bean;

import java.util.List;

import com.edaoyou.collections.bean.Bean.Follow;

/**
 * 关注列表 备注 flag 0首次读取 1拉旧数据(上拉刷新) 2拉新数据(下拉刷新)count 每次读取条数
 * last_id(flag=1时last_id为最后一条数据的ID, flag=2时last_id为最新一条数据的ID) flag=2时，一次返回所有数据
 * 
 */
public class FollowList {
	public int ret;
	public Response response;
	public int count;

	public class Response {
		public List<Follow> list;
	}

	public int more;
}
