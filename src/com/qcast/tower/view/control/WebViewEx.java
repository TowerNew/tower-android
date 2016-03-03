package com.qcast.tower.view.control;

import java.util.List;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.js.BridgeHandler;
import com.slfuture.pluto.js.CallBackFunction;
import com.slfuture.pluto.sensor.Location;
import com.slfuture.pluto.sensor.LocationSensor;
import com.slfuture.pluto.sensor.core.ILocationListener;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

/**
 * 拓展WebView
 */
public class WebViewEx extends com.slfuture.pluto.js.BridgeWebView {
	/**
	 * 当前窗口
	 */
	public Activity activity = null;


	public WebViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public WebViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public WebViewEx(Context context) {
		super(context);
	}

	/**
	 * 注册回调函数
	 */
	public void register() {
		registerHandler("closeWindow", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				if(null != activity) {
					activity.finish();
					activity = null;
				}
            }
		});
	}
}
