package com.qcast.tower.business.structure;

import java.io.Serializable;

import com.slfuture.carrie.base.json.JSONVisitor;

/**
 * 即时通信
 */
public class IM implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 即时通信种类
	 */
	public final static String TYPE_PHONE = "phone";
	public final static String TYPE_BOX = "tv";
	
	/**
	 * 标题
	 */
	public String title;
	/**
	 * 种类
	 */
	public String type;
	/**
	 * IM标志符
	 */
	public String imId;


	/**
	 * 解析数据生成对象
	 * 
	 * @param visitor 数据
	 * @return 解析结果
	 */
	public boolean parse(JSONVisitor visitor) {
		title = visitor.getString("title");
		type = visitor.getString("type");
		imId = visitor.getString("imUsername");
		return true;
	}
}
