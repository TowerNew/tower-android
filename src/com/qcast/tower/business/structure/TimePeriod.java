package com.qcast.tower.business.structure;

import java.text.ParseException;

import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.time.Date;

/**
 * 时间段
 */
public class TimePeriod {
	/**
	 * 时间段：上午
	 */
	public final static String TIME_AM = "AM";
	/**
	 * 时间段：下午
	 */
	public final static String TIME_PM = "PM";


	/**
	 * 日期
	 */
	public Date date = null;
	/**
	 * 时间段
	 */
	public String span = null;
	
	
	/**
	 * 转化为字符串
	 * 
	 * @return 字符串
	 */
	@Override
	public String toString() {
		if(TIME_AM.equalsIgnoreCase(span)) {
			return date.toString() + " 上午";
		}
		else if(TIME_PM.equalsIgnoreCase(span)) {
			return date.toString() + " 下午";
		}
		else {
			return date.toString();
		}
	}

	/**
	 * 构建
	 * 
	 * @param json 对象
	 */
	public static TimePeriod build(JSONObject json) {
		TimePeriod result = new TimePeriod();
		if(null != json.get("date")) {
			try {
				result.date = Date.parse(((JSONString) json.get("date")).getValue());
			}
			catch (ParseException e) { }
		}
		if(null != json.get("span")) {
			result.span = ((JSONString) json.get("span")).getValue();
		}
		return result;
	}
}
