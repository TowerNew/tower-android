package com.qcast.tower.form;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.qcast.tower.R;

/**
 * 主界面
 */
public class MainActivity extends FragmentActivity {
	/**
	 * 选项卡对象
	 */
	protected TabHost tabhost = null;


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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public void goOnline(View view){
        Intent intent = new Intent(MainActivity.this,InquiryDoctorActivity.class);
        intent.putExtra("docLevel",2);
        MainActivity.this.startActivity(intent);
    }

    public void goSelf(View view){
		 Intent intent = new Intent(this, SelfDiagnosticActivity.class);
		 this.startActivity(intent);
    }

    public void goFamous(View view){
        Intent intent = new Intent(MainActivity.this,InquiryDoctorActivity.class);
        intent.putExtra("docLevel",1);
        MainActivity.this.startActivity(intent);
    }

}
