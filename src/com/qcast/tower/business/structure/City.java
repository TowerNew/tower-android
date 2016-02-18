package com.qcast.tower.business.structure;

import java.io.Serializable;

import com.slfuture.carrie.base.type.List;

/**
 * 城市类
 */
public class City implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 城市ID
	 */
	public int id;
	/**
	 * 城市名称
	 */
	public String name;
	/**
	 * 包含小区列表
	 */
	public List<Region> regions = new List<Region>();
}
