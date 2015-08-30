package com.qcast.tower.logic;

import com.qcast.tower.logic.structure.FamilyMember;
import com.slfuture.carrie.base.type.safe.Table;

import android.app.Application;

/**
 * 当前运行时
 */
public class Logic {
	/**
	 * 程序引用
	 */
	public static Application application = null;


	// 运行时数据
	/**
	 * 用户手机号码
	 */
	public static String phone = null;
	/**
	 * 用户口令
	 */
	public static String token = null;
	/**
	 * 用户名称
	 */
	public static String name = null;
	/**
	 * 用户头像
	 */
	public static String photo = null;
	/**
	 * 当前区域ID
	 */
	public static int regionId;
	/**
	 * 当前区域名称
	 */
	public static String regionName;
	/**
	 * 家庭成员
	 */
	public static Table<Integer, FamilyMember> familys = new Table<Integer, FamilyMember>();
}
