package com.qcast.tower.logic.response;

import com.qcast.tower.logic.response.core.IResponse;

/**
 * 返回结果
 */
public class Response implements IResponse {
	/**
	 * 代码
	 */
	private int code;


	/**
	 * 获取结果代码
	 * 
	 * @return 结果代码
	 */
	@Override
	public int code() {
		return this.code;
	}

	/**
	 * 设置结果代码
	 * 
	 * @param code 结果代码
	 */
	public void setCode(int code) {
		this.code = code;
	}
}
