package com.qcast.tower.business.structure;

import java.io.File;
import java.io.Serializable;

import android.graphics.Bitmap;

import com.qcast.tower.framework.Storage;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pretty.im.Module;

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
	public boolean parse(JSONVisitor visitor) {
		id = visitor.getString("userGlobalId");
		name = visitor.getString("name");
		photoUrl = visitor.getString("photo");
		department = visitor.getString("department");
		resume = visitor.getString("resume");
		title = visitor.getString("title");
		description = visitor.getString("description");
		imId = visitor.getString("imUsername");
		return true;
	}

	/**
	 * 获取头像位图
	 * 
	 * @return 头像位图
	 */
	public Bitmap photo() {
		File file = Storage.getImageFileByUrl(photoUrl);
		if(!file.exists()) {
			return null;
		}
		return GraphicsHelper.decodeFile(file, 200, 200);
	}
	
	/**
	 * 获取未读消息个数
	 */
	public int unreadMessageCount() {
		return Module.getUnreadMessageCount(imId);
	}
}
