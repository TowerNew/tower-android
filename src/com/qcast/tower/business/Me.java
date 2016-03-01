package com.qcast.tower.business;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;

import com.qcast.tower.Program;
import com.qcast.tower.R;
import com.qcast.tower.business.core.IMeListener;
import com.qcast.tower.business.structure.Doctor;
import com.qcast.tower.business.structure.IM;
import com.qcast.tower.business.structure.Notify;
import com.qcast.tower.business.user.Friend;
import com.qcast.tower.business.user.Relative;
import com.qcast.tower.business.user.User;
import com.qcast.tower.framework.Storage;
import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.time.Date;
import com.slfuture.carrie.base.type.List;
import com.slfuture.carrie.base.type.safe.Table;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.etc.Version;
import com.slfuture.pluto.framework.Broadcaster;
import com.slfuture.pluto.sensor.Reminder;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.core.IReactor;
import com.slfuture.pretty.im.view.form.SingleChatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

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
		
		public Contact(String photo, String name) {
			this.photo = photo;
			this.name = name;
		}
	}


	private static final long serialVersionUID = 1L;

	/**
	 * 手机号码
	 */
	public String phone;
	/**
	 * 口令
	 */
	public String token = null;
	/**
	 * 安全密码
	 */
	public String password = null;
	/**
	 * 地址
	 */
	public String address = null;
	/**
	 * 姓名
	 */
	public String name = null;
	/**
	 * 身份证
	 */
	public String idNumber = null;
	/**
	 * 身份证截图
	 */
	public String snapshot = null;
	/**
	 * 是否认证
	 */
	public boolean isAuthenticated;
	/**
	 * 私人医生
	 */
	public Doctor doctor = null;
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
	public static Me instance = null;


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
				if(content.getInteger("code", 0) <= 0) {
					if(null != callback) {
						callback.on(false);
					}
					return;
				}
				content = content.getVisitor("data");
				if(null == content) {
					if(null != callback) {
						callback.on(false);
					}
					return;
				}
				Me me = new Me();
				me.parse(content);
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
							if(0 < content.getInteger("code", 0)) {
								if(instance.parse(content.getVisitor("data"))) {
									try {
										instance.save();
									}
									catch (IOException e) {
										throw new RuntimeException("存储用户信息失败", e);
									}
								}
								Module.reactor = instance;
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
		Module.logout(null);
		instance = null;
		delete();
	}

	/**
	 * 刷新私人成员
	 * 
	 * @param context 上下文 
	 * @param callback 结果
	 */
	public void refreshDoctor(Context context, IEventable<Boolean> callback) {
		Host.doCommand("privateDoctor", new JSONResponse(context, callback) {
			@SuppressWarnings("unchecked")
			@Override
			public void onFinished(JSONVisitor content) {
				 IEventable<Boolean> callback = (IEventable<Boolean>) tag;
				 if(null == content || content.getInteger("code", 1) < 0) {
					 callback.on(false);
					 return;
				 }
				 if(null == content.getVisitor("data")) {
					 return;
				 }
				 doctor = new Doctor();
				 doctor.parse(content.getVisitor("data"));
				 try {
					save();
				 }
				 catch (IOException e) {}
				 callback.on(true);
			}
		}, token);
	}

	/**
	 * 刷新成员
	 * 
	 * @param context 上下文 
	 * @param callback 结果
	 */
	public void refreshMember(Context context, IEventable<Boolean> callback) {
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
						if(friend.parse(visitor)) {
							friends.add(friend);
						}
					}
					else {
						// 非注册亲戚
						Relative relative = new Relative();
						if(relative.parse(visitor)) {
							relatives.add(relative);
						}
					}
				 }
				 try {
					save();
				 }
				 catch (IOException e) {}
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
		intent.putExtra("selfId", fetchIMId(IM.TYPE_PHONE));
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
			contact = new Contact(photo, name);
			contacts.put(imId, contact);
		}
	}
	
	/**
	 * 通过通信ID获取好友
	 * 
	 * @param imId 通信ID
	 * @return 好友对象
	 */
	public Friend fetchFriendByIM(String imId) {
		for(Friend friend : friends) {
			if(null != friend.fetchTypeByIM(imId)) {
				return friend;
			}
		}
		return null;
	}

	/**
	 * 通过用户ID获取好友
	 * 
	 * @param userId 用户ID
	 * @return 好友对象
	 */
	public Friend fetchFriendById(String userId) {
		for(Friend friend : friends) {
			if(friend.id.equals(userId)) {
				return friend;
			}
		}
		return null;
	}

	/**
	 * 通过用户ID获取亲人
	 * 
	 * @param userId 用户ID
	 * @return 亲人对象
	 */
	public Relative fetchRelativeById(String userId) {
		for(Relative relative : relatives) {
			if(relative.id.equals(userId)) {
				return relative;
			}
		}
		return null;
	}

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
		phone = visitor.getString("username");
		address = visitor.getString("address");
		token = visitor.getString("token");
		password = visitor.getString("password");
		idNumber = visitor.getString("idnumber");
		snapshot = visitor.getString("idcardfront");
		if(3 == visitor.getInteger("type", 1)) {
			isAuthenticated = true;
		}
		else {
			isAuthenticated = false;
		}
		relatives.clear();
		for(JSONVisitor item : visitor.getVisitors("userList")) {
			if(id.equals(item.getString("userGlobalId"))) {
				try {
					if(null != item.getString("birthday")) {
						birthday = Date.parse(item.getString("birthday"));
					}
				}
				catch (ParseException e) { }
				gender = item.getInteger("gender", 0);
				name = item.getString("name");
				idNumber = item.getString("idnumber");
				continue;
			}
			// 非注册亲戚
			Relative relative = new Relative();
			if(relative.parse(item)) {
				relatives.add(relative);
			}
		}
		friends.clear();
		for(JSONVisitor item : visitor.getVisitors("familyList")) {
			// 注册好友
			Friend friend = new Friend();
			if(friend.parse(item)) {
				friends.add(friend);
			}
		}
		this.doctor = null;
		if(null != visitor.getVisitor("privateDoctor")) {
			Doctor doctor = new Doctor();
			if(doctor.parse(visitor.getVisitor("privateDoctor"))) {
				this.doctor = doctor;
			}
		}
		return true;
	}

	/**
	 * 获取存储文件
	 * 
	 * @return 存储文件
	 */
	public static File file() {
		return new File(Storage.dataFolder() + "me." + Version.fetchVersion(Program.application).toString() + ".dat");
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
		if(null != doctor && userId.equals(doctor.imId)) {
			return GraphicsHelper.decodeResource(Program.application, R.drawable.icon_doctor_default);
		}
		return GraphicsHelper.decodeFile(new File(contact.photo));
	}

	@Override
	public String getName(String userId) {
		for(IM item : im) {
			if(userId.equals(item.imId)) {
				return item.title;
			}
		}
		for(Friend friend : friends) {
			for(IM item : friend.im) {
				if(userId.equals(item.imId)) {
					return item.title;
				}
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
		return fetchIMId(IM.TYPE_PHONE);
	}

	@Override
	public String getPassword() {
		return phone;
	}

	@Override
	public void onConflict() {
		logout();
		Toast.makeText(Program.application, "账号在其他设备上登录", Toast.LENGTH_LONG).show();
		Broadcaster.<IMeListener>broadcast(Program.application, IMeListener.class).onConflict();
	}

	@Override
	public void onCommand(final String from, final String action, final com.slfuture.carrie.base.type.Table<String, Object> data) {
		Reminder.ringtone(Program.application);
		Integer type = (Integer) data.get("type");
		if(null != type && (Notify.TYPE_5 == type || Notify.TYPE_9 == type)) {
			Me.instance.refreshMember(Program.application, new IEventable<Boolean>() {
				@Override
				public void on(Boolean result) {
					if(!result) {
						return;
					}
					Broadcaster.<IMeListener>broadcast(Program.application, IMeListener.class).onCommand(from, action, data);
				}
			});
			Reminder.vibrate(Program.application);
			return;
		}
		if("message".equals(action)) {
			Logic.hasUnreadMessage = true;
		}
		Broadcaster.<IMeListener>broadcast(Program.application, IMeListener.class).onCommand(from, action, data);
	}
}
