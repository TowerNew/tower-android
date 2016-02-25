package com.qcast.tower.business.user;

import java.io.Serializable;

import com.slfuture.carrie.base.json.JSONVisitor;

/**
 * 注册好友
 */
public class Friend extends User implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 关系&备注
	 */
	public String relation;


	/**
	 * 解析数据生成用户对象
	 * 
	 * @param visitor 数据
	 * @return 解析结果
	 */
	public boolean parse(JSONVisitor visitor) {
		if(!super.parse(visitor)) {
			return false;
		}
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
		return super.nickname();
	}
}
