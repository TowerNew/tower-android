package com.qcast.tower.business;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import com.qcast.tower.Program;
import com.qcast.tower.business.core.IMeListener;
import com.qcast.tower.business.user.Friend;
import com.qcast.tower.business.user.Relative;
import com.qcast.tower.business.user.User;
import com.qcast.tower.framework.Storage;
import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.type.List;
import com.slfuture.carrie.base.type.core.ITable;
import com.slfuture.carrie.base.type.safe.Table;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.framework.Broadcaster;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.core.IReactor;
import com.slfuture.pretty.im.view.form.SingleChatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * 当前登录用户类
 */
public class Me extends User implements Serializable, IReactor {
	/**
	 * 最近联系人类
	 */
	private static class Contact implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * 头像路径
		 */
		public String photo = null;
		/**
		 * 姓名
		 */
		public String name = null;
		
		
		public Contact() { }
		public Contact(String photo, String name) {
			this.photo = photo;
			this.name = name;
		}
	}


	private static final long serialVersionUID = 1L;

	/**
	 * 口令
	 */
	public String token = null;
	/**
	 * 注册好友列表
	 */
	public List<Friend> friends = new List<Friend>();
	/**
	 * 非注册亲戚列表
	 */
	public List<Relative> relatives = new List<Relative>();
	/**
	 * 最近联系人
	 */
	public Table<String, Contact> contacts = new Table<String, Contact>();

	/**
	 * 实例
	 */
	public  static Me instance = null;


	/**
	 * 登录
	 * 
	 * @param context 上下文
	 * @param phone 手机号码
	 * @param code 验证码
	 * @param callback 回调函数
	 */
	public static void login(Context context, String phone, String code, IEventable<Boolean> callback) {
		Host.doCommand("login", new JSONResponse(context, callback) {
			@SuppressWarnings("unchecked")
			@Override
			public void onFinished(JSONVisitor content) {
				final IEventable<Boolean> callback = (IEventable<Boolean>) tag;
				if(null == content) {
					if(null != callback) {
						callback.on(false);
					}
					return;
				}
				if(content.getInteger("code", 0) < 0) {
					if(null != callback) {
						callback.on(false);
					}
					return;
				}
				content = content.getVisitor("data");
				Me me = new Me();
				me.id = content.getString("userGlobalId");
				me.phone = content.getString("username");
				me.imId = content.getString("imUsername");
				me.token = content.getString("token");
				for(JSONVisitor visitor : content.getVisitors("userList")) {
					if(1 == visitor.getInteger("type", 1)) {
						// 注册好友
						Friend friend = new Friend();
						friend.id = visitor.getString("userGlobalId");
						friend.phone = visitor.getString("phone");
						friend.imId = visitor.getString("imUsername");
						friend.nickName = visitor.getString("relation");
						if(null == friend.nickName) {
							// 容错
							friend.nickName = visitor.getString("name");
						}
						me.friends.add(friend);
					}
					else {
						// 非注册亲戚
						Relative relative = new Relative();
						relative.id = visitor.getString("userGlobalId");
						relative.name = visitor.getString("name");
						relative.idNumber = visitor.getString("idnumber");
						relative.nickName = visitor.getString("relation");
						if(null == relative.nickName) {
							// 容错
							relative.nickName = visitor.getString("name");
						}
						me.relatives.add(relative);
					}
				}
				for(JSONVisitor visitor : content.getVisitors("tvList")) {
					me.contacts.put(visitor.getString("imUsername"), new Contact(null, visitor.getString("name")));
				}
				instance = me;
				try {
					instance.save();
				}
				catch (IOException e) {
					throw new RuntimeException("存储用户信息失败", e);
				}
				Module.reactor = instance;
				Module.login(new IEventable<Boolean>() {
					@Override
					public void on(Boolean arg0) {
						if(null == callback) {
							return;
						}
						if(arg0) {
							callback.on(true);
						}
						else {
							callback.on(false);
						}
					}
				});
			}
		}, phone, code);
	}

	/**
	 * 自动登录
	 * 
	 * @param context 上下文
	 * @param callback 回调函数
	 */
	public static void autoLogin(Context context, IEventable<Boolean> callback) {
		try {
			Me me = read();
			if(null == me) {
				if(null != callback) {
					callback.on(false);
				}
			}
			else {
				instance = me;
				Host.doCommand("check", new JSONResponse(context, callback) {
					@SuppressWarnings("unchecked")
					@Override
					public void onFinished(JSONVisitor content) {
						final IEventable<Boolean> callback = (IEventable<Boolean>) tag;
						if(null == content) {
							instance = null;
							callback.on(false);
						}
						else {
							if(1 == content.getInteger("code", 0)) {
								Module.login(new IEventable<Boolean>() {
									@Override
									public void on(Boolean arg0) {
										if(arg0) {
											callback.on(true);
										}
										else {
											callback.on(false);
										}
									}
								});
							}
							else {
								instance = null;
								callback.on(false);
							}
						}
					}
				}, me.phone, me.token);
			}
		}
		catch (IOException e) {
			throw new RuntimeException("读取用户信息失败", e);
		}
	}

	/**
	 * 退出登录
	 */
	public void logout() {
		instance = null;
		delete();
	}

	/**
	 * 加载成员
	 * 
	 * @param context 上下文 
	 * @param callback 结果
	 */
	public void refresh(Context context, IEventable<Boolean> callback) {
		Host.doCommand("member", new JSONResponse(context, callback) {
			@SuppressWarnings("unchecked")
			@Override
			public void onFinished(JSONVisitor content) {
				 IEventable<Boolean> callback = (IEventable<Boolean>) tag;
				 if(null == content || content.getInteger("code", 1) < 0) {
					 callback.on(false);
					 return;
				 }
				 friends.clear();
				 relatives.clear();
				 for(JSONVisitor visitor : content.getVisitors("data")) {
					 if(1 == visitor.getInteger("type", 1)) {
						 // 注册好友
						Friend friend = new Friend();
						friend.id = visitor.getString("userGlobalId");
						friend.phone = visitor.getString("phone");
						friend.imId = visitor.getString("imUsername");
						friend.nickName = visitor.getString("relation");
						if(null == friend.nickName) {
							// 容错
							friend.nickName = visitor.getString("name");
						}
						friends.add(friend);
					}
					else {
						// 非注册亲戚
						Relative relative = new Relative();
						relative.id = visitor.getString("userGlobalId");
						relative.name = visitor.getString("name");
						relative.idNumber = visitor.getString("idnumber");
						relative.nickName = visitor.getString("relation");
						if(null == relative.nickName) {
							// 容错
							relative.nickName = visitor.getString("name");
						}
						relatives.add(relative);
					}
				 }
				 callback.on(true);
			}
		}, token);
	}

	/**
	 * 打开聊天对话框
	 * 
	 * @param context 上下文
	 * @param groupId 聊天群组ID
	 * @param remoteId 聊天对方ID
	 */
	public void doChat(Context context, String groupId, String remoteId) {
		Intent intent = new Intent(context, SingleChatActivity.class);
		intent.putExtra("selfId", imId);
		intent.putExtra("groupId", groupId);
		intent.putExtra("remoteId", remoteId);
		context.startActivity(intent);
	}

	/**
	 * 保存
	 */
	public void save() throws IOException {
		Serial.restore(this, file());
	}

	/**
	 * 添加联系人
	 * 
	 * @param imId 即时通信ID
	 * @param photo 头像
	 * @param name 姓名
	 */
	public void addContact(String imId, String photo, String name) {
		Contact contact = contacts.get(imId);
		if(null != contact) {
			contact.photo = photo;
			contact.name = name;
		}
		else {
			contact = new Contact();
			contact.photo = photo;
			contact.name = name;
			contacts.put(imId, contact);
		}
	}

	/**
	 * 获取存储文件
	 * 
	 * @return 存储文件
	 */
	public static File file() {
		return new File(Storage.DATA_ROOT + "me.dat");
	}

	/**
	 * 删除
	 */
	private void delete() {
		file().delete();
	}

	/**
	 * 读取
	 * 
	 * @return 返回存储的对象
	 */
	private static Me read() throws IOException {
		File file = file();
		if(!file.exists()) {
			return null;
		}
		try {
			return Serial.extract(file, Me.class);
		}
		catch (ClassNotFoundException e1) {
			return null;
		}
	}

	@Override
	public Bitmap getPhoto(String userId) {
		Contact contact = contacts.get(userId);
		if(null == contact || null == contact.photo) {
			return null;
		}
		return GraphicsHelper.decodeFile(new File(contact.photo));
	}

	@Override
	public String getName(String userId) {
		if(userId.equals(imId)) {
			return "我";
		}
		for(Friend friend : friends) {
			if(userId.equals(friend.imId)) {
				return friend.nickName;
			}
		}
		Contact contact = contacts.get(userId);
		if(null == contact || null == contact.name) {
			return "未知";
		}
		return contact.name;
	}

	@Override
	public String getUserId() {
		return imId;
	}

	@Override
	public String getPassword() {
		return phone;
	}

	@Override
	public void onConflict() {
		Broadcaster.<IMeListener>broadcast(Program.application, IMeListener.class).onConflict();
	}

	@Override
	public void onCommand(String from, String action, ITable<String, Object> data) {
		Broadcaster.<IMeListener>broadcast(Program.application, IMeListener.class).onCommand(from, action, data);
	}
}
