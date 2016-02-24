package com.qcast.tower.business.structure;

import java.io.Serializable;

import com.slfuture.carrie.base.json.JSONVisitor;

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
	 * 头像下载路径
	 */
	public String photoUrl;
	/**
	 * 头衔
	 */
	public String title;
	/**
	 * 科室
	 */
	public String department;
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


	/**
	 * 构建医生
	 * 
	 * @param visitor
	 */
	public boolean parse(JSONVisitor visitor) {
		id = visitor.getString("userGlobalId");
		name = visitor.getString("name");
		photoUrl = visitor.getString("photo");
		department = visitor.getString("department");
		resume = visitor.getString("resume");
		title = visitor.getString("title");
		description = visitor.getString("description");
		imId = visitor.getString("imUsername");
		return true;
	}
}
