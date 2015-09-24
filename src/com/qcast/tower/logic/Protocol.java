package com.qcast.tower.logic;

import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.pluto.net.HttpLoader;
import com.slfuture.pluto.net.Option;
import com.slfuture.pluto.net.future.Future;

/**
 * 协议对象
 */
public class Protocol {
	/**
	 * URL模板
	 */
	private String urlTemplate = null;
	/**
	 * 模拟的结果
	 */
	public String mock = null;


	/**
	 * 构建协议对象
	 * 
	 * @param urlTemplate URL模板
	 * @return 协议对象
	 */
	public static Protocol build(String urlTemplate, String mock) {
		Protocol result = new Protocol();
		result.urlTemplate = urlTemplate;
		result.mock = mock;
		return result;
	}
	
	/**
	 * 构建URL
	 * 
	 * @param parameters 参数列表
	 */
	public String buildURL(Object... parameters) {
		// 梳理URL
		String url = urlTemplate;
		for(int i = parameters.length - 1; i >= 0; i--) {
			Object parameter = parameters[i];
			if(null == parameter) {
				url = url.replace("[" + i + "]", "");
			}
			else {
				url = url.replace("[" + i + "]", parameter.toString());
			}
		}
		return url;
	}

	/**
	 * 调用
	 * 
	 * @param loader 网络加载器
	 * @param future 回调对象
	 * @param parameters 参数列表
	 */
	public void invoke(HttpLoader loader, Future future, Object... parameters) {
		String url = urlTemplate;
		// 梳理POST参数
		Table<String, Object> postParameterMap = null;
		int i = url.indexOf("#");
		if(i > 0) {
			String path = url.substring(i + 1);
			url = url.substring(0, i);
			postParameterMap = new Table<String, Object>();
			String[] postParameters = path.split("&");
			for(String postParameter : postParameters) {
				if(Text.isBlank(postParameter)) {
					continue;
				}
				String[] pair = postParameter.split("=");
				if(pair.length != 2) {
					continue;
				}
				String indexString = Text.substring(pair[1], "[", "]");
				if(null == indexString) {
					postParameterMap.put(pair[0], pair[1]);
				}
				else {
					postParameterMap.put(pair[0], parameters[Integer.parseInt(indexString)]);
				}
			}
		}
		else {
			for(int j = parameters.length - 1; j >= 0; j--) {
				Object parameter = parameters[j];
				if(null == parameter) {
					url = url.replace("[" + j + "]", "");
				}
				else {
					url = url.replace("[" + j + "]", parameter.toString());
				}
			}
		}
		// 开始投递
		loader.send(url, postParameterMap, new Option(10000), future);
	}
}
