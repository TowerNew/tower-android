package com.qcast.tower;

import com.qcast.tower.business.Logic;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.config.Configuration;
import com.slfuture.pretty.im.Module;

import android.app.Application;
import android.util.Log;

/**
 * 应用类
 */
public class Program extends Application {
	/**
	 * 版本号
	 */
	public final static String VERSION = "2.0.1";
	/**
	 * 程序引用
	 */
	public static Application application = null;


	/**
	 * 构建回调
	 */
	@Override
    public void onCreate() {
		Log.i("TOWER", "Program.onCreate() execute");
		super.onCreate();
		application = this;
		// 初始化配置系统
		Configuration.initialize(application);
		// 初始化IM组件
		Module.context = this;
		Module.initialize();
		// 初始化网络
		Host.initialize();
    }

	/**
	 * 销毁回调
	 */
	@Override
	public void onTerminate() {
		Log.i("TOWER", "Program.onTerminate() execute");
		super.onTerminate();
		// 关闭IM组件
		Module.terminate();
		// 关闭配置系统
		Configuration.terminate();
		// 关闭网络
		Host.terminate();
	}
}
