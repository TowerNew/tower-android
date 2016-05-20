package com.qcast.tower.view.form;

import com.qcast.tower.R;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


@ResourceView(id = R.layout.activity_level_vip)
public class LevelVipActivity extends ActivityEx{
	
	@ResourceView(id = R.id.bad_result_tv)
	public TextView tet;
	@ResourceView(id = R.id.selectdoctor_image_close)
	public ImageButton imgClose;
	public void onCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
	}

	private void prepare() {
         Intent intent=new Intent();
         String str = intent.getStringExtra("name");
		 tet.setText(str);
		imgClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LevelVipActivity.this.finish();				
			}
		});
}}


