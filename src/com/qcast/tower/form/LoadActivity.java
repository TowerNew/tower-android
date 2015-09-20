package com.qcast.tower.form;

import java.util.Timer;
import java.util.TimerTask;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.Response;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

/**
 * 引导界面
 */
public class LoadActivity extends Activity {
	/**
	 * 加载完毕消息ID
	 */
	public final static int MESSAGE_LOADFINISHED = 1;


	/**
	 * 加载结束回调句柄
	 */
	public class LoadHandler extends Handler {
		/**
		 * 处理消息
		 * 
		 * @param msg 消息对象
		 */
		public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_LOADFINISHED:
            	Log.i("TOWER", "LoadActivity load finished, start alter to main form");
            	// 页面切换
            	if(0 == Logic.regionId) {
    				
            		
            		
            		startActivity(new Intent(LoadActivity.this, MainActivity.class));
                }
            	else {
            		startActivity(new Intent(LoadActivity.this, MainActivity.class));
            	}
            	LoadActivity.this.finish();
                break;
            }
            super.handleMessage(msg);
        }
	}

	/**
	 * 定时器
	 */
	private class LoadTask extends TimerTask {
		/**
		 * 定时回调
		 */
		@Override
		public void run() {
			// Looper.prepare();
        	Log.i("TOWER", "LoadActivity.LoadTask.run() execute");
			if(null != timer) {
				timer = null;
			}
			Message message = new Message();
			message.what = MESSAGE_LOADFINISHED;
			handler.sendMessage(message);
		}
	}


	/**
	 * 定时器
	 */
	protected Timer timer = null;
	/**
	 * 消息句柄
	 */
	public static LoadHandler handler = null;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "LoadActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_load);
		// 开始计时
		handler = new LoadHandler();
		timer = new Timer();
		timer.schedule(new LoadTask(), 3000);
	}
}
