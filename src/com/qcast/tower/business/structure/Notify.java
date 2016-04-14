package com.qcast.tower.business.structure;

import java.io.Serializable;

/**
 * 通知数据
 */
public class Notify implements Serializable{
	private static final long serialVersionUID = 407956137132437798L;

	// 添加好友通知
	public final static int TYPE_1 = 1;
	// 红包
	public final static int TYPE_2 = 2;
	// 系统公告
	public final static int TYPE_3 = 3;
	// 慈铭报告
	public final static int TYPE_4 = 4;
	// 好友接受通知
	public final static int TYPE_5 = 5;
	// 好友拒绝通知
	public final static int TYPE_6 = 6;
	// 医生会话消息
	public final static int TYPE_7 = 7;
	// 亲友会话消息
	public final static int TYPE_8 = 8;
	// 亲友删除通知
	public final static int TYPE_9 = 9;
    //用户认证通过的通知
    public final static int TYPE_21 = 21;
    //用户认证被驳回的通知
    public final static int TYPE_22 = 22;
    //私人医生被删除的通知
	public static final int TYPE_10 = 10;
    
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
    public String imId;
    public String imGroupId;
    public String remotePhoto;
    public String reason;
}
