package com.qcast.tower.business.user;

import java.io.Serializable;

/**
 * 用户类
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	public String id;
	/**
	 * 手机号码
	 */
	public String phone;
	/**
	 * 通信ID
	 */
	public String imId;
}
