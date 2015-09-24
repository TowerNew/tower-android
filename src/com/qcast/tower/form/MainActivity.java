package com.qcast.tower.form;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.Response;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;

/**
 * 主界面
 */
public class MainActivity extends FragmentActivity {
    /**
     * 选项卡对象
     */
    protected TabHost tabhost = null;
    private Handler hasMessageHandler;



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
        hasMessageHandler=new Handler();
        refreshMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public void refreshMessage(){
        hasMessageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Logic.token==null){
                    Host.doCommand("comment", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) {
                            if (Response.CODE_SUCCESS != code()) {
                                Toast.makeText(MainActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONObject resultObject = JSONObject.convert(content);
                            if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                                Toast.makeText(MainActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                if (tabhost.getCurrentTab() == 0) {
                                    View v = tabhost.getCurrentView();
                                    Button aa = (Button) v.findViewById(R.id.home_button_notify);
                                    if (aa != null)
                                        aa.setVisibility(View.GONE);
                                }
                            }
                        }
                    }, Logic.token);
                }

                MainActivity.this.refreshMessage();

            }
        },5000);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hasMessageHandler.removeCallbacksAndMessages(null);
    }
}
