package com.qcast.tower.logic.response;

/**
 * 通用反馈类
 */
public abstract class CommonResponse<T> extends Response {
	/**
	 * 附属信息
	 */
	public Object tag = null;
	
	
	/**
	 * 构造函数
	 */
	public CommonResponse() {}

	/**
	 * 构造函数
	 * 
	 * @param tag 附属信息
	 */
	public CommonResponse(Object tag) {
		this.tag = tag;
	}

	/**
	 * 结束回调
	 * 
	 * @param content 回执内容
	 */
	public abstract void onFinished(T content);
}
