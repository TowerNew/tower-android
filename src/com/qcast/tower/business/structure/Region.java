package com.qcast.tower.business.structure;

import java.io.Serializable;

/**
 * 小区类
 */
public class Region implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 小区ID
	 */
	public int id;
	/**
	 * 城市名称
	 */
	public String name;
	/**
	 * 所在城市ID
	 */
	public int cityId;
}
