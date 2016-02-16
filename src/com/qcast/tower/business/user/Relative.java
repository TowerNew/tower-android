package com.qcast.tower.business.user;

import java.io.Serializable;

/**
 * 非注册亲戚
 */
public class Relative implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	public String id;
	/**
	 * 称呼
	 */
	public String nickName = null;
	/**
	 * 姓名
	 */
	public String name = null;
	/**
	 * 身份证号
	 */
	public String idNumber = null;
}
