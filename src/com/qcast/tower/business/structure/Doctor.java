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
	public static Doctor build(JSONVisitor visitor) {
		Doctor result = new Doctor();
		result.id = visitor.getString("userGlobalId");
		result.name = visitor.getString("name");
		result.photoUrl = visitor.getString("photo");
		result.department = visitor.getString("department");
		result.resume = visitor.getString("resume");
		result.title = visitor.getString("title");
		result.description = visitor.getString("description");
		result.imId = visitor.getString("imUsername");
		return result;
	}
}
