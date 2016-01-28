package com.qcast.tower.logic;

import com.qcast.tower.logic.structure.FamilyMember;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.type.Set;
import com.slfuture.carrie.base.type.safe.Table;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;

import android.app.Application;
import android.widget.Toast;

/**
 * 当前运行时
 */
public class Logic {
	/**
	 * 电视信息
	 */
	public static class TVInfo {
		/**
		 * 称呼
		 */
		public String name;
		/**
		 * 环信ID
		 */
		public String imUsername;
	}

	/**
	 * 程序引用
	 */
	public static Application application = null;


	// 运行时数据
	/**
	 * 用户手机号码
	 */
	public static String phone = null;
	/**
	 * 用户口令
	 */
	public static String token = null;
	/**
	 * 用户ID
	 */
	public static String userId = null;
	/**
	 * 用户名称
	 */
	public static String name = null;
	/**
	 * 身份证号码
	 */
	public static String idNumber = null;
	/**
	 * 用户头像
	 */
	public static String photo = null;
	/**
	 * 区域
	 */
	public static Table<Integer, String> regions = null;

	/**
	 * 当前城市ID
	 */
	public static int cityId = 1;
	/**
	 * 当前城市名称
	 */
	public static String cityName;

	/**
	 * 当前区域ID
	 */
	public static int regionId = 1;
	/**
	 * 当前区域名称
	 */
	public static String regionName;
	/**
	 * 家庭成员
	 */
	public static Table<String, FamilyMember> familys = new Table<String, FamilyMember>();
	/**
	 * 银行卡号
	 */
	public static String bankNumber;
	/**
	 * 正面照片
	 */
	public static String frontImage;
	/**
	 * 背面照片
	 */
	public static String backImage;
	/**
	 * 是否有未读消息
	 */
	public static Set<String> messageFamily = new Set<String>();
	/**
	 * TV通信列表
	 */
	public static Table<String, TVInfo> tvMap = new Table<String, TVInfo>();
	/**
	 * 用户名
	 */
	public static String imUsername = null;
	/**
	 * 是否存在未读消息
	 */
	public static boolean hasMessage = false;


	/**
	 * 初始化
	 */
	public static boolean initialize() {
		phone = Storage.user("phone", String.class);
		token = Storage.user("token", String.class);
		userId = Storage.user("userId", String.class);
		name = Storage.user("name", String.class);
		imUsername = Storage.user("imUsername", String.class);
		if(null != Storage.user("cityId", Integer.class)) {
			cityId = (int) (double) (Double) Storage.user("cityId", Double.class);
		}
		cityName = Storage.user("cityName", String.class);
		if(null != Storage.user("regionId", Integer.class)) {
			regionId = (int) (double) (Double) Storage.user("regionId", Double.class);
		}
		regionName = Storage.user("regionName", String.class);
		return true;
	}
	
	/**
	 * 增加TV信息
	 * 
	 * @param name 昵称
	 * @param imUsername 通信ID
	 */
	public static void addTVInfo(String name, String imUsername) {
		TVInfo tv = new TVInfo();
		tv.name = name;
		tv.imUsername = imUsername;
		tvMap.put(imUsername, tv);
	}

	/**
	 * 加载成员
	 * 
	 * @param result 结果
	 */
	public static void loadMember(final IEventable<Boolean> callback) {
		Host.doCommand("member", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(application, "加载家庭成员失败", Toast.LENGTH_LONG).show();
					if(null != callback) {
						callback.on(false);
					}
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(application, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					if(null != callback) {
						callback.on(false);
					}
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				Logic.familys.clear();
				for(IJSON item : result) {
					JSONObject newJSONObject = (JSONObject) item;
					FamilyMember member = new FamilyMember();
					if(null != newJSONObject.get("userGlobalId")) {
						member.userId = ((JSONString) newJSONObject.get("userGlobalId")).getValue();
					}
					member.category = ((JSONNumber) newJSONObject.get("category")).intValue();
					member.status = ((JSONNumber) newJSONObject.get("status")).intValue();
					if(null != newJSONObject.get("phone")) {
						member.phone = ((JSONString) newJSONObject.get("phone")).getValue();
					}
					if(null != newJSONObject.get("relation")) {
						member.relation = ((JSONString) newJSONObject.get("relation")).getValue();
					}
					member.name = ((JSONString) newJSONObject.get("name")).getValue();
					if(null != newJSONObject.get("idnumber")) {
						member.idNumber = ((JSONString) newJSONObject.get("idnumber")).getValue();
					}
					if(null != newJSONObject.get("imUsername")) {
						member.imUsername = ((JSONString) newJSONObject.get("imUsername")).getValue();
					}
					if(null != member.userId) {
						Logic.familys.put(member.userId, member);
					}
				}
				if(null != callback) {
					callback.on(true);
				}
			}
		}, Logic.token);
	}
}
