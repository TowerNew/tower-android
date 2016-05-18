package com.qcast.tower.view.form;

import com.qcast.tower.R;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

	/**
	 * 积分打卡 
	 */
@ResourceView(id = R.layout.activity_signin)
public class SignInActivity extends ActivityEx {
	
	@ResourceView(id = R.id.signin_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.signin_label_version)
	public TextView labRuler;
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
	}  
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SignInActivity.this.finish();
			}
		});
		
		labRuler.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {				
				
			}
			
		});
	
	}
	
	
	
	
	
	
	
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

