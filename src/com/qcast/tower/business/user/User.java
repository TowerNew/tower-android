package com.qcast.tower.business.user;

import java.io.Serializable;
import java.text.ParseException;

import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.time.Date;

/**
 * 用户类
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 性别
	 */
	public final static int GENDER_UNKNOWN = 0;
	public final static int GENDER_MALE = 1;
	public final static int GENDER_FEMALE = 2;

	/**
	 * 用户ID
	 */
	public String id;
	/**
	 * 手机号码
	 */
	public String phone;
	/**
	 * 昵称
	 */
	public String nickname;
	/**
	 * 头像
	 */
	public String photo;
	/**
	 * 出生年月
	 */
	public Date birthday;
	/**
	 * 性别
	 */
	public int gender = GENDER_UNKNOWN;
	/**
	 * 通信ID
	 */
	public String imId;


	/**
	 * 解析数据生成用户对象
	 * 
	 * @param visitor 数据
	 * @return 解析结果
	 */
	public boolean parse(JSONVisitor visitor) {
		id = visitor.getString("userGlobalId");
		phone = visitor.getString("phone");
		nickname = visitor.getString("nickname");
		photo = visitor.getString("photo");
		if(null != visitor.getString("birthday")) {
			try {
				birthday = Date.parse(visitor.getString("birthday"));
			}
			catch (ParseException e) { }
		}
		gender = visitor.getInteger("gender", 0);
		imId = visitor.getString("imUsername");
		return true;
	}
}
