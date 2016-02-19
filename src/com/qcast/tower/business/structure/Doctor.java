package com.qcast.tower.business.structure;

import java.io.Serializable;

/**
 * 医生信息
 */
public class Doctor implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	public String id;
	/**
	 * 姓名
	 */
	public String name;
	/**
	 * 头衔
	 */
	public String title;
	/**
	 * 简历
	 */
	public String resume;
	/**
	 * 自我介绍
	 */
	public String description;
	/**
	 * 通信ID
	 */
	public String imId;
}
