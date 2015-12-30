package com.qcast.tower.form;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

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
            		// startActivity(new Intent(LoadActivity.this, VoiceActivity.class));
//            		Intent intent = new Intent(LoadActivity.this, VideoActivity.class);
//            		intent.putExtra("userName", "t2");
//            		intent.putExtra("mode", true);
//            		startActivity(intent);
            		startActivity(new Intent(LoadActivity.this, MainActivity.class));
            		//Intent intent = new Intent(LoadActivity.this, GroupChatActivity.class);
            		//intent.putExtra("localId", "appuser_1003");
            		//intent.putExtra("groupId", "140368589411582504");
            		//intent.putExtra("remoteId", "appdoctor_1");
            		//intent.putExtra("localPhoto", "https://www.baidu.com/img/bd_logo1.png");
            		//intent.putExtra("remotePhoto", "https://www.baidu.com/img/bd_logo1.png");
            		//startActivity(intent);
            		
            		// startActivity(new Intent(LoadActivity.this, GroupChatActivity.class));
            		
//            		Intent intent = new Intent(LoadActivity.this, ChatActivity.class);
//            		intent.putExtra("remoteId", "t2");
//            		intent.putExtra("remoteNickName", "凯瑞");
//            		startActivity(intent);
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
		String currentImage = Storage.user("startupImage", String.class);
		if(null != currentImage) {
			ImageView view = (ImageView) this.findViewById(R.id.load_image_ad);
			try {
				view.setImageBitmap(Storage.getImage(currentImage));
			}
			catch(Exception ex) {}
		}
		Host.doCommand("startupImage", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					return;
				}
				JSONString json = (JSONString) resultObject.get("data");
				if(null == json) {
					return;
				}
				if(Storage.existImage(json.getValue())) {
					return;
				}
				String imageName = Storage.getImageName(json.getValue());
				Host.doImage("image", new ImageResponse(imageName, imageName) {
					@Override
					public void onFinished(Bitmap content) {
						try {
							Storage.saveFile(content, (String) tag);
							Storage.setUser("startupImage", (String) tag);
						}
						catch (IOException e) { }
					}
		        }, json.getValue());
			}
		});
		//
		if(null == Logic.imUsername || null == Logic.phone) {
			Logic.phone = null;
			return;
		}
		EMChatManager.getInstance().login(Logic.imUsername, Logic.phone, new EMCallBack() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						Log.d("main", "登陆聊天服务器成功！");		
					}
				});
			}
		 
			@Override
			public void onProgress(int progress, String status) {
		 
			}
		 
			@Override
			public void onError(int code, String message) {
				Log.d("main", "登陆聊天服务器失败！");
			}
		});
	}
}
