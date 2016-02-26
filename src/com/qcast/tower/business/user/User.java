package com.qcast.tower.business.user;

import java.io.Serializable;
import java.text.ParseException;

import com.qcast.tower.business.structure.IM;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.time.Date;
import com.slfuture.carrie.base.type.List;
import com.slfuture.pretty.im.Module;

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
	 * 即时通信集合
	 */
	public List<IM> im = new List<IM>();


	/**
	 * 解析数据生成用户对象
	 * 
	 * @param visitor 数据
	 * @return 解析结果
	 */
	public boolean parse(JSONVisitor visitor) {
		id = visitor.getString("userGlobalId");
		nickname = visitor.getString("nickname");
		photo = visitor.getString("photo");
		if(null != visitor.getString("birthday")) {
			try {
				birthday = Date.parse(visitor.getString("birthday"));
			}
			catch (ParseException e) { }
		}
		gender = visitor.getInteger("gender", 0);
		im.clear();
		if(null != visitor.getVisitors("im")) {
			for(JSONVisitor item : visitor.getVisitors("im")) {
				IM im = new IM();
				if(im.parse(item)) {
					this.im.add(im);
				}
			}
		}
		return true;
	}

	/**
	 * 获取即时通信ID
	 * 
	 * @param type 即时通信类型
	 * @return 即时通信ID
	 */
	public String fetchIMId(String type) {
		for(IM item : im) {
			if(type.equals(item.type)) {
				return item.imId;
			}
		}
		return null;
	}
	
	/**
	 * 获取即时通信ID
	 * 
	 * @param type 即时通信ID
	 * @return 即时通信类型
	 */
	public String fetchTypeByIM(String imId) {
		for(IM item : im) {
			if(imId.equals(item.imId)) {
				return item.type;
			}
		}
		return null;
	}

	/**
	 * 获取未读消息个数
	 */
	public int unreadMessageCount() {
		String imId = fetchIMId(IM.TYPE_PHONE);
		if(null == imId) {
			return 0;
		}
		return Module.getUnreadMessageCount(imId);
	}

	/**
	 * 获取有效称呼
	 * 
	 * @return 有效称呼
	 */
	public String nickname() {
		return nickname;
	}
}
