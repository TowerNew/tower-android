package com.qcast.tower.view.form;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.qcast.tower.R;
import com.slfuture.pluto.view.annotation.ResourceView;

@ResourceView(id = R.layout.activity_myzone)
public class MyZoneActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.myzone_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.myzone_layout_collection)
	public View viewCollection;
	@ResourceView(id = R.id.myzone_layout_check)
	public View viewCheck;
	@ResourceView(id = R.id.myzone_layout_book)
	public View viewBook;
	@ResourceView(id = R.id.myzone_layout_massage)
	public View viewMassage;
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyZoneActivity.this.finish();
			}
		});
	}
}
