package com.qcast.tower.view.form;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.core.IMeListener;
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
					return false;
				}
				Intent intent = new Intent(MainActivity.this, SiriActivity.class);
				MainActivity.this.startActivity(intent);
				return false;
			}
        });
    }

	@Override
	public void onConflict() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("帐号在其他设备登录");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				MainActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
		   }
		});
		builder.show();
	}

	@Override
	public void onCommand(String from, String action, com.slfuture.carrie.base.type.Table<String, Object> data) {
		
	}
}
