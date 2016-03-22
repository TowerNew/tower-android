package com.qcast.tower.view.form;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pretty.general.view.form.BrowserActivity;

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
		viewCollection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyZoneActivity.this, BrowserActivity.class);
				intent.putExtra("url", Networking.fetchURL("wodeshoucang", Me.instance.token));
				MyZoneActivity.this.startActivity(intent);
			}
		});
		viewCheck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyZoneActivity.this, BrowserActivity.class);
				intent.putExtra("url", Networking.fetchURL("tijiandingdan", Me.instance.token));
				MyZoneActivity.this.startActivity(intent);
			}
		});
		viewBook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyZoneActivity.this, BrowserActivity.class);
				intent.putExtra("url", Networking.fetchURL("guahaodingdan", Me.instance.token));
				MyZoneActivity.this.startActivity(intent);
			}
		});
		viewMassage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyZoneActivity.this, BrowserActivity.class);
				intent.putExtra("url", Networking.fetchURL("liliaodingdan", Me.instance.token));
				MyZoneActivity.this.startActivity(intent);
			}
		});
	}
}
