package com.qcast.tower.business.structure;

/**
 * 一个消息的JavaBean
 * 
 * @author Jose
 * 
 */
public class ChatMsgEntity {
	private String message;//消息内容
	private boolean isComMeg = true;// 是否为收到的消息
	private String speakeId;
	private String time;
	private int messageId;
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

	public String getTime() {
		return time;
	}

	public void setIsComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	public void setSpeakeId(String speakeId) {
		this.speakeId = speakeId;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
}
