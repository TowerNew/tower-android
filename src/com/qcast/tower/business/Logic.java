package com.qcast.tower.business;

import com.qcast.tower.business.structure.FamilyMember;
import com.slfuture.carrie.base.type.Set;
import com.slfuture.carrie.base.type.safe.Table;

/**
 * 当前运行时
 */
public class Logic {
	/**
	 * 是否有未读消息
	 */
	public static boolean hasUnreadMessage = false;
	
	
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
	 * 用户头像
	 */
	public static String photo = null;
	/**
	 * 区域
	 */
	public static Table<Integer, String> regions = null;

	/**
	 * 当前城市ID
	 */
	public static int cityId = 1;
	/**
	 * 当前城市名称
	 */
	public static String cityName;

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
	 * 是否有未读消息
	 */
	public static Set<String> messageFamily = new Set<String>();
	/**
	 * 用户名
	 */
	public static String imUsername = null;
	/**
	 * 是否存在未读消息
	 */
	public static boolean hasMessage = false;
}
