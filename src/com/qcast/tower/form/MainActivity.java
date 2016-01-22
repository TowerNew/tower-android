package com.qcast.tower.form;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;
/**
 * 主界面
 */
public class MainActivity extends FragmentActivity {
	/**
	 * 实现ConnectionListener接口
	 */
	private class ConnectionListener implements EMConnectionListener {
	    @Override
		public void onConnected() {
	    	Log.d("tower", "已连接到服务器");
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(error == EMError.USER_REMOVED) {
						Log.e("tower", "显示帐号已经被移除");
					}
					else if (error == EMError.CONNECTION_CONFLICT) {
						Log.e("tower", "显示帐号在其他设备登陆");
						//
						AlertDialog.Builder builder = new Builder(MainActivity.this);
						builder.setMessage("该帐号在其他地方登录");
						builder.setTitle("提示");
						builder.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Logic.token = null;
								Logic.imUsername = null;
								Logic.name = null;
								Logic.phone = null;
								Storage.setUser("token", null);
								Storage.setUser("userId", null);
								Storage.setUser("name", null);
								Storage.setUser("phone", null);
								Storage.setUser("imUsername", null);
								Storage.save();
								dialog.cancel();
								MainActivity.this.finish();
								android.os.Process.killProcess(android.os.Process.myPid());
								System.exit(0);
						   }
						});
						builder.show();
					}
					else {
						if (NetUtils.hasNetwork(MainActivity.this)) {
							Log.e("tower", "连接不到聊天服务器");
						}
						else {
							Log.e("tower", "当前网络不可用，请检查网络设置");
						}
					}
				}
			});
		}
	}
	
	
    /**
     * 选项卡对象
     */
    protected TabHost tabhost = null;

    /**
     * 对话接收器
     */
    private BroadcastReceiver chatReceiver = null;
    /**
     * 拨号接收器
     */
    private BroadcastReceiver dialReceiver = null;
    /**
     * 命令接收器
     */
    private BroadcastReceiver commandReceiver = null;


    /**
     * 界面创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TOWER", "call MainActivity.onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //
        tabhost = (TabHost)findViewById(R.id.main_tabhost);
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec("main_tab_home").setIndicator("").setContent(R.id.main_fragment_home));
        tabhost.addTab(tabhost.newTabSpec("main_tab_inquiry").setIndicator("").setContent(R.id.main_fragment_inquiry));
        tabhost.addTab(tabhost.newTabSpec("main_tab_reserve").setIndicator("").setContent(R.id.main_fragment_reserve));
        tabhost.addTab(tabhost.newTabSpec("main_tab_user").setIndicator("").setContent(R.id.main_fragment_user));
        RadioGroup group = (RadioGroup)findViewById(R.id.main_tab);
        group.check(R.id.main_tab_home);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                TabHost tabhost = (TabHost)findViewById(R.id.main_tabhost);
                switch(checkedId) {
                    case R.id.main_tab_home:
                        tabhost.setCurrentTabByTag("main_tab_home");
                        break;
                    case R.id.main_tab_inquiry:
                        tabhost.setCurrentTabByTag("main_tab_inquiry");
                        break;
                    case R.id.main_tab_reserve:
                        tabhost.setCurrentTabByTag("main_tab_reserve");
                        break;
                    case R.id.main_tab_user:
                        tabhost.setCurrentTabByTag("main_tab_user");
                        break;
                }
            }
        });
		EMChatOptions chatOptions = EMChatManager.getInstance().getChatOptions();
		chatOptions.setNotifyBySoundAndVibrate(true);
		//
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
    	intentFilter.setPriority(2);
    	chatReceiver = new BroadcastReceiver() {
           	@Override
           	public void onReceive(Context context, Intent intent) {
           		String from = intent.getStringExtra("from");
           		Logic.messageFamily.add(from);
           		MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.newmessage);
                player.start();
           	}
        };
        registerReceiver(chatReceiver, intentFilter);
        //
        dialReceiver = new BroadcastReceiver() {
           	@Override
           	public void onReceive(Context context, Intent intent) {
           		String from = intent.getStringExtra("from");
           		String type = intent.getStringExtra("type");
           		if(null != Logic.tvMap.get(from)) {
               		Intent ringIntent = new Intent(MainActivity.this, RingActivity.class);
               		ringIntent.putExtra("userId", from);
               		ringIntent.putExtra("userName", "电视");
               		ringIntent.putExtra("type", type);
               		MainActivity.this.startActivity(ringIntent);
               		return;
           		}
                Host.doCommand("member", new CommonResponse<String>(from + "|" + type) {
        			@Override
        			public void onFinished(String content) {
        				if(Response.CODE_SUCCESS != code()) {
        					return;
        				}
        				JSONObject resultObject = JSONObject.convert(content);
        				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
        					return;
        				}
                   		String from = Text.substring((String) tag, null, "|");
                   		String type = Text.substring((String) tag, "|", null);
        				JSONArray result = (JSONArray) resultObject.get("data");
        				if(0 == result.size()) {
        					Intent ringIntent = new Intent(MainActivity.this, RingActivity.class);
    		           		ringIntent.putExtra("userId", from);
    		           		ringIntent.putExtra("userName", "未知来源");
    		           		ringIntent.putExtra("type", type);
    		           		MainActivity.this.startActivity(ringIntent);
    		           		return;
        				}
        				for(IJSON item : result) {
        					JSONObject newJSONObject = (JSONObject) item;
        					if(null != newJSONObject.get("imUsername")) {
        						String imUsername = ((JSONString) newJSONObject.get("imUsername")).getValue();
        						if(!from.equals(imUsername)) {
        							continue;
        						}
        						String relation = "亲友";
        						if(null != newJSONObject.get("relation")) {
        							relation = ((JSONString) newJSONObject.get("relation")).getValue();
            					}
        						Intent ringIntent = new Intent(MainActivity.this, RingActivity.class);
        		           		ringIntent.putExtra("userId", from);
        		           		ringIntent.putExtra("userName", relation);
        		           		ringIntent.putExtra("type", type);
        		           		MainActivity.this.startActivity(ringIntent);
        		           		return;
        					}
        				}
						Intent ringIntent = new Intent(MainActivity.this, RingActivity.class);
		           		ringIntent.putExtra("userId", from);
		           		ringIntent.putExtra("userName", "医生热线");
		           		ringIntent.putExtra("type", type);
		           		MainActivity.this.startActivity(ringIntent);
        			}
        		}, Logic.token);
           	}
        };
        registerReceiver(dialReceiver, new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction()));
        //
    	commandReceiver = new BroadcastReceiver() {
    		@Override
    		public void onReceive(Context context, Intent intent) {
    			String msgId = intent.getStringExtra("msgid");
    			EMMessage message = intent.getParcelableExtra("message");
    			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
    			String aciton = cmdMsgBody.action;
    			Log.d("tower", "command message " + aciton + " receive:" + msgId);
    			if("notify".equalsIgnoreCase(aciton)) {
    				// 通知消息
    				int type = 0;
    				String title = null;
        			try {
        				type = message.getIntAttribute("type");
        				title = message.getStringAttribute("title");
    				}
        			catch (EaseMobException e) {
        				Log.e("tower", "command message parse failed", e);
    				}
    				if(!Text.isBlank(title)) {
        				Toast.makeText(MainActivity.this, title, Toast.LENGTH_LONG).show();
    				}
        			switch(type) {
        			case 1:
        				// 添加好友消息
        				break;
        			case 2:
        				// 好友接受消息
        				break;
        			case 3:
        				// 好友拒绝消息
        				break;
        			case 4:
        				// 系统通知消息
        				break;
        			}
        			Logic.hasMessage = true;
               		MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.newmessage);
                    player.start();
    			}
    		}
    	};
    	registerReceiver(commandReceiver, new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction()));
    	//
    	EMChatManager.getInstance().addConnectionListener(new ConnectionListener());
    }
    

    /**
     * 终结
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != chatReceiver) {
        	this.unregisterReceiver(chatReceiver);
        }
        chatReceiver = null;
        if(null != dialReceiver) {
        	this.unregisterReceiver(dialReceiver);
        }
        dialReceiver = null;
        if(null != commandReceiver) {
        	this.unregisterReceiver(commandReceiver);
        }
        commandReceiver = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public void goOnline(View view){
        Intent intent = new Intent(MainActivity.this,InquiryDoctorActivity.class);
        intent.putExtra("services", "inquiry");
		intent.putExtra("docLevel",2);
        MainActivity.this.startActivity(intent);
    }

    public void goSelf(View view){
        Intent intent = new Intent(this, BodyActivity.class);
        this.startActivity(intent);
    }

    public void goFamous(View view){
        Intent intent = new Intent(MainActivity.this,InquiryDoctorActivity.class);
        intent.putExtra("services", "inquiry");
		intent.putExtra("docLevel",1);
        MainActivity.this.startActivity(intent);
    }
}
