package com.qcast.tower.view.form;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pretty.general.view.form.BrowserActivity;

@ResourceView(id = R.layout.activity_archive)
public class ArchiveActivity extends OnlyPasswordActivity {
	@ResourceView(id = R.id.archive_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.archive_web_data)
	public WebView webData;
	@ResourceView(id = R.id.archive_layout_examination)
	public View viewExamination;
	@ResourceView(id = R.id.archive_layout_medical)
	public View viewMedical;
	@ResourceView(id = R.id.archive_layout_wearables)
	public View viewWearables;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Me.instance.isAuthenticated) {
			Toast.makeText(this, "您的身份证信息尚未认证通过", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(ArchiveActivity.this, UserInfoActivity.class);
			this.startActivity(intent);
			this.finish();
			return;
		}
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArchiveActivity.this.finish();
			}
		});
		webData.loadUrl(Host.fetchURL("jiankangdangan", Me.instance.token));
		viewExamination.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, BrowserActivity.class);
				intent.putExtra("url", Host.fetchURL("tijianbaogao", Profile.instance().region.id, Me.instance.token));
				ArchiveActivity.this.startActivity(intent);
			}
		});
		viewMedical.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, BrowserActivity.class);
				intent.putExtra("url", Host.fetchURL("dianzibingli", Profile.instance().region.id, Me.instance.token));
				ArchiveActivity.this.startActivity(intent);
			}
		});
		viewWearables.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, BrowserActivity.class);
				intent.putExtra("url", Host.fetchURL("chuandaishuju", Profile.instance().region.id, Me.instance.token));
				ArchiveActivity.this.startActivity(intent);
			}
		});
	}
}
