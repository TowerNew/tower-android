package com.qcast.tower.logic.response.core;

/**
 * 网络调用反馈接口
 */
public interface IResponse {
	/**
	 * 结果代码
	 */
	public final static int CODE_SUCCESS = 0;
	public final static int CODE_ERROR = 1;
	public final static int CODE_TIMEOUT = 2;


	/**
	 * 获取结果代码
	 * 
	 * @return 结果代码
	 */
	public int code();
}
