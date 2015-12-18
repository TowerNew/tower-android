package com.qcast.tower.model;

import com.slfuture.carrie.base.time.DateTime;

import android.graphics.Bitmap;

/**
 * 聊天消息
 */
public class ChatMessage {
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 头像
	 */
	private Bitmap photo;
	/**
	 * 消息内容
	 */
	private String message;
	/**
	 * 消息内容
	 */
	private Bitmap image;
	/**
	 * 是否本人投递的消息
	 */
	private boolean isSelf;
	/**
	 * 投递时间
	 */
	private DateTime time;
	
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSelf() {
		return isSelf;
	}
	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}
	public DateTime getTime() {
		return time;
	}
	public void setTime(DateTime time) {
		this.time = time;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
}
