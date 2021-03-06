package com.qcast.tower.view.form;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.core.IMeListener;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentActivityEx;

/**
 * 主界面
 */
@ResourceView(id = R.layout.activity_main)
public class MainActivity extends FragmentActivityEx implements IMeListener {
    /**
     * 选项卡对象
     */
	@ResourceView(id = R.id.main_tabhost)
    public TabHost tabhost = null;
    /**
     * 切换按钮集合对象
     */
	@ResourceView(id = R.id.main_tab)
	public RadioGroup group;
    /**
     * 会话按钮
     */
	@ResourceView(id = R.id.main_image_conversation)
	public ImageView imgConversation;


    /**
     * 界面创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        tabhost = (TabHost)findViewById(R.id.main_tabhost);
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec("main_tab_home").setIndicator("").setContent(R.id.main_fragment_home));
        tabhost.addTab(tabhost.newTabSpec("main_tab_conversation").setIndicator("").setContent(R.id.main_fragment_conversation));
        tabhost.addTab(tabhost.newTabSpec("main_tab_user").setIndicator("").setContent(R.id.main_fragment_user));
        group.check(R.id.main_tab_home);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                TabHost tabhost = (TabHost)findViewById(R.id.main_tabhost);
                switch(checkedId) {
                    case R.id.main_tab_home:
                        tabhost.setCurrentTabByTag("main_tab_home");
        				imgConversation.setImageResource(R.drawable.main_conversation_normal);
                        break;
                    case R.id.main_tab_conversation:
                        tabhost.setCurrentTabByTag("main_tab_conversation");
                        imgConversation.setImageResource(R.drawable.main_conversation_selected);
                        break;
                    case R.id.main_tab_user:
                        tabhost.setCurrentTabByTag("main_tab_user");
        				imgConversation.setImageResource(R.drawable.main_conversation_normal);
                        break;
                }
            }
        });
        imgConversation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				group.check(R.id.main_tab_conversation);
			}
		});
        imgConversation.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					MainActivity.this.startActivity(intent);
					return false;
				}
				if(null == Me.instance.doctor) {
					Intent intent = new Intent(MainActivity.this, SelectDoctorActivity.class);
					MainActivity.this.startActivity(intent);
					return false;
				}
				Intent intent = new Intent(MainActivity.this, SiriActivity.class);
				MainActivity.this.startActivity(intent);
				return false;
			}
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onConflict() {
		Toast.makeText(MainActivity.this, "账号在其他设备上登录，程序即将退出", Toast.LENGTH_LONG).show();
		Controller.doDelay(new Runnable() {
			@Override
			public void run() {
				MainActivity.this.finish();
				Intent intenn = new Intent();  
				intenn.setAction("android.intent.action.MAIN");  
				intenn.addCategory("android.intent.category.HOME");  
				MainActivity.this.startActivity(intenn);
				android.os.Process.killProcess(android.os.Process.myPid());
//				Intent intent = new Intent(MainActivity.this, TextActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.putExtra("title", "提示");
//				intent.putExtra("content", "本设备已被其他设备踢下线");
//				startActivity(intent);
//				android.os.Process.killProcess(android.os.Process.myPid());
//				System.exit(0);
			}
		}, 3000);
//		AlertDialog.Builder builder = new Builder(MainActivity.this);
//		builder.setMessage("帐号在其他设备登录\n程序即将退出");
//		builder.setTitle("提示");
//		builder.setPositiveButton("确认", new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.cancel();
//				MainActivity.this.finish();
//				android.os.Process.killProcess(android.os.Process.myPid());
//				System.exit(0);
//		   }
//		});
//		builder.show();
	}

	@Override
	public void onCommand(String from, String action, com.slfuture.carrie.base.type.Table<String, Object> data) {
		
	}
}
