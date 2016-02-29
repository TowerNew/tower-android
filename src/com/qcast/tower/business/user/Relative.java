package com.qcast.tower.business.user;

import java.io.Serializable;
import java.text.ParseException;

import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.time.Date;

/**
 * 非注册亲戚
 */
public class Relative implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	public String id;
	/**
	 * 出生年月
	 */
	public Date birthday;
	/**
	 * 性别
	 */
	public int gender = User.GENDER_UNKNOWN;
	/**
	 * 姓名
	 */
	public String name = null;
	/**
	 * 身份证号
	 */
	public String idNumber = null;
	/**
	 * 身份证截图
	 */
	public String snapshot = null;
	/**
	 * 关系&备注
	 */
	public String relation;


	public boolean 


	/**
	 * 解析数据生成用户对象
	 * 
	 * @param visitor 数据
	 * @return 解析结果
	 */
	public boolean parse(JSONVisitor visitor) {
		id = visitor.getString("userGlobalId");
		if(null != visitor.getString("birthday")) {
			try {
				birthday = Date.parse(visitor.getString("birthday"));
			}
			catch (ParseException e) { }
		}
		gender = visitor.getInteger("gender", 0);
		name = visitor.getString("name");
		idNumber = visitor.getString("idnumber");
		relation = visitor.getString("relation");
		return true;
	}

	/**
	 * 获取有效称呼
	 * 
	 * @return 有效称呼
	 */
	public String nickname() {
		if(null != relation) {
			return relation;
		}
		return name;
	}
}
