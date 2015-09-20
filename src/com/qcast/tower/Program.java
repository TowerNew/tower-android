package com.qcast.tower;

import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.slfuture.pluto.config.Configuration;

import android.app.Application;
import android.util.Log;

/**
 * 应用类
 */
public class Program extends Application {
	/**
	 * 构建回调
	 */
	@Override
    public void onCreate() {
		Log.i("TOWER", "Program.onCreate() execute");
		super.onCreate();
		//
		Logic.application = this;
		// 初始化配置系统
		Configuration.initialize(Logic.application);
		// 初始化网络
		Host.initialize();
		Logic.initialize();
    }

	/**
	 * 销毁回调
	 */
	@Override
	public void onTerminate() {
		Log.i("TOWER", "Program.onTerminate() execute");
		super.onTerminate();
		// 关闭配置系统
		Configuration.terminate();
		// 关闭网络
		Host.terminate();
	}
}
