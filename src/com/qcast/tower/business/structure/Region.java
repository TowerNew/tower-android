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
	 * 构造函数
	 */
	public Region() {}
	/**
	 * 构造函数
	 */
	public Region(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * 获取小区的短名称
	 * 
	 * @return 短名称
	 */
	public String getShortName() {
		if(name.length() <= 4) {
			return name;
		}
		return name.substring(name.length() - 4);
	}
}
