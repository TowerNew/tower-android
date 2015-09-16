package com.qcast.tower.model;

/**
 * 一个消息的JavaBean
 * 
 * @author way
 * 
 */
public class ChatMsgEntity {
	private String message;//消息内容
	private boolean isComMeg = true;// 是否为收到的消息
	private String speakeId;
	private long time;
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getMsgType() {
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMeg = isComMsg;
	}

	public ChatMsgEntity() {
	}

	public ChatMsgEntity(String text, boolean isComMsg) {
		super();
		this.message = text;
		this.isComMeg = isComMsg;
	}

	public String getSpeakeId() {
		return speakeId;
	}

	public boolean isComMeg() {
		return isComMeg;
	}

	public long getTime() {
		return time;
	}

	public void setIsComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	public void setSpeakeId(String speakeId) {
		this.speakeId = speakeId;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
