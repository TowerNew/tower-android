package com.qcast.tower.business.user;

import java.io.Serializable;

import com.slfuture.carrie.base.time.Date;

/**
 * 用户类
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 性别
	 */
	public final static int GENDER_UNKNOWN = 0;
	public final static int GENDER_MALE = 1;
	public final static int GENDER_FEMALE = 2;

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
	/**
	 * 性别
	 */
	public int gender = GENDER_UNKNOWN;
	/**
	 * 出生年月
	 */
	public Date birthday;
}
