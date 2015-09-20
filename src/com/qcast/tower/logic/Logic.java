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
	 * 用户ID
	 */
	public static String userId = null;
	/**
	 * 用户名称
	 */
	public static String name = null;
	/**
	 * 身份证号码
	 */
	public static String idNumber = null;
	/**
	 * 性别
	 */
	public static int gender = 0;
	/**
	 * 出生年月
	 */
	public static String birthday = null;
	/**
	 * 用户头像
	 */
	public static String photo = null;
	/**
	 * 区域
	 */
	public static Table<Integer, String> regions = null;
	/**
	 * 当前区域ID
	 */
	public static int regionId = 1;
	/**
	 * 当前区域名称
	 */
	public static String regionName;
	/**
	 * 家庭成员
	 */
	public static Table<String, FamilyMember> familys = new Table<String, FamilyMember>();
	/**
	 * 银行卡号
	 */
	public static String bankNumber;
	/**
	 * 正面照片
	 */
	public static String frontImage;
	/**
	 * 背面照片
	 */
	public static String backImage;


	/**
	 * 初始化
	 */
	public static boolean initialize() {
		phone = Storage.user("phone", String.class);
		token = Storage.user("token", String.class);
		userId = Storage.user("userId", String.class);
		name = Storage.user("name", String.class);
		if(null != Storage.user("regionId", Integer.class)) {
			regionId = (int) (double) (Double) Storage.user("regionId", Double.class);
		}
		regionName = Storage.user("regionName", String.class);
		return true;
	}
}
