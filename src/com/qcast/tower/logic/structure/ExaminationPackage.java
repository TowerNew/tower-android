package com.qcast.tower.logic.structure;

import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.type.List;

/**
 * 检查套餐
 */
public class ExaminationPackage {
	/**
	 * 主键
	 */
	public int id = 0;
	/**
	 * 标题
	 */
	public String name = null;
	/**
	 * 描述内容
	 */
	public String detail = null;
	/**
	 * 时间段
	 */
	public List<TimePeriod> periods = new List<TimePeriod>();
	

	/**
	 * 构建
	 * 
	 * @param json 对象
	 */
	public static ExaminationPackage build(JSONObject json) {
		ExaminationPackage result = new ExaminationPackage();
		if(null != json.get("id")) {
			result.id = ((JSONNumber) json.get("id")).intValue();
		}
		if(null != json.get("name")) {
			result.name = ((JSONString) json.get("name")).getValue();
		}
		if(null != json.get("detail")) {
			result.detail = ((JSONString) json.get("detail")).getValue();
		}
		if(null != json.get("times")) {
			for(IJSON time : (JSONArray) json.get("times")) {
				result.periods.add(TimePeriod.build((JSONObject) time));
			}
		}
		return result;
	}
}
