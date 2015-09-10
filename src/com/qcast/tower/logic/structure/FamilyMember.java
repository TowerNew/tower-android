package com.qcast.tower.logic.structure;

/**
 * 家庭成员结构体
 */
public class FamilyMember {
	/**
	 * 成员关系类型
	 */
	public final static int CATEGORY_RELATIVE = 1;
	public final static int CATEGORY_OWNER = 2;
	/**
	 * 成员确认状态
	 */
	public final static int STATUS_UNCONFIRM = 1;
	public final static int STATUS_CONFIRMED = 2;


	/**
	 * 属性
	 */
	public String userId;
	public int category;
	public int status;
	public String phone;
	public String relation;
	public String name;
	public String idNumber;
	public String birthday;
	
	
	/**
	 * 构造函数
	 */
	public FamilyMember() { }
	
	/**
	 * 构造函数
	 * 
	 * @param id 家庭成员ID
	 * @param phone 手机号码
	 * @param relation 关系类型
	 */
	public FamilyMember(String userId, String phone, String relation) {
		this.userId = userId;
		this.phone = phone;
		this.relation = relation;
	}
}
