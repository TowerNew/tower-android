package com.qcast.tower.framework;

import com.qcast.tower.view.form.PayActivity;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pretty.general.core.IBrowserHandler;
import com.slfuture.pretty.general.view.form.BrowserActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 帮助类
 */
public class Helper {
	/**
	 * 打开浏览器
	 * 
	 * @param url 页面地址
	 */
	public static void openBrowser(final Context context, final String url) {
		Intent intent = new Intent(context, BrowserActivity.class);
		intent.putExtra("url", url);
		Bundle bundle = new Bundle();
		bundle.putSerializable("1", new IBrowserHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public String name() {
				return "pay";
			}
			@Override
			public void on(String parameter, final IEventable<String> callback) {
				JSONVisitor visitor = new JSONVisitor(JSONObject.convert(parameter));
				String name = visitor.getString("name");
				String description = visitor.getString("description");
				int price = visitor.getInteger("price");
				//
				Controller.doJoin(9527, new IEventable<Integer>() {
					@Override
					public void on(Integer event) {
						if(PayActivity.RESULT_SUCCESS == event) {
							callback.on("{\"result\":true}");
						}
						else {
							callback.on("{\"result\":false}");
						}
					}
				});
				//
				Intent intent = new Intent(context, PayActivity.class);
				intent.putExtra("commandId", 9527);
				intent.putExtra("name", name);
				intent.putExtra("description", description);
				intent.putExtra("price", price);
				context.startActivity(intent);
			}
		});
		context.startActivity(intent);
	}
}
