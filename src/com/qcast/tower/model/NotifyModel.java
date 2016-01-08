package com.qcast.tower.model;

import java.io.Serializable;

/**
 * 通知数据
 */
public class NotifyModel implements Serializable{
	private static final long serialVersionUID = 407956137132437798L;
	
	// 添加好友请求
	public final static int TYPE_1 = 1;
	// 好友接受通知
	public final static int TYPE_2 = 2;
	// 好友拒绝通知
	public final static int TYPE_3 = 3;
	// 系统公告通知
	public final static int TYPE_4 = 4;
	// 医生会话消息
	public final static int TYPE_5 = 5;
	// 亲友会话消息
	public final static int TYPE_6 = 6;


	public int id;
    public boolean hasRead;
    public String title;
    public String time;
    public String description;
    public int type;
    public String name;
    public String phone;
    public String requestId;
    public String relation;
    public String url;
}
