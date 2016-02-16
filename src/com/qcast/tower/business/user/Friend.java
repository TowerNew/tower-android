package com.qcast.tower.business.user;

import java.io.Serializable;

/**
 * 注册好友
 */
public class Friend extends User implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 称呼
	 */
	public String nickName = null;
}
