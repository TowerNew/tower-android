package com.qcast.tower.view.form;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pretty.general.view.form.BrowserActivity;

@ResourceView(id = R.layout.activity_archive)
public class ArchiveActivity extends OnlyPasswordActivity {
	@ResourceView(id = R.id.archive_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.archive_web_data)
	public WebView browser;
	@ResourceView(id = R.id.archive_layout_examination)
	public View viewExamination;
	@ResourceView(id = R.id.archive_layout_medical)
	public View viewMedical;
	@ResourceView(id = R.id.archive_layout_wearables)
	public View viewWearables;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null == Me.instance) {
			this.finish();
			return;
		}
		if(-1 == this.getIntent().getIntExtra("password", -1)) {
			this.finish();
			return;
		}
//		if(!Me.instance.isAuthenticated) {
//			Toast.makeText(this, "您的身份证信息尚未认证通过", Toast.LENGTH_LONG).show();
//			Intent intent = new Intent(ArchiveActivity.this, UserInfoActivity.class);
//			this.startActivity(intent);
//			this.finish();
//			return;
//		}
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArchiveActivity.this.finish();
			}
		});
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		LayoutParams lp = (LayoutParams) browser.getLayoutParams();
		lp.height = metrics.widthPixels * 2 / 5;
		browser.setLayoutParams(lp);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Intent intent = new Intent(ArchiveActivity.this, BrowserActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
				browser.pauseTimers();
				browser.resumeTimers();
                return true;
			}
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				browser.loadUrl("about:blank");
			}
		});
		browser.loadUrl(Networking.fetchURL("jiankangdangan", Me.instance.token));
		viewExamination.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, BrowserActivity.class);
				intent.putExtra("url", Networking.fetchURL("tijianbaogao", Me.instance.token, Profile.instance().region.id));
				ArchiveActivity.this.startActivity(intent);
			}
		});
		viewMedical.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, BrowserActivity.class);
				intent.putExtra("url", Networking.fetchURL("dianzibingli", Me.instance.token, Profile.instance().region.id));
				ArchiveActivity.this.startActivity(intent);
			}
		});
		viewWearables.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, BrowserActivity.class);
				intent.putExtra("url", Networking.fetchURL("chuandaishuju", Me.instance.token, Profile.instance().region.id));
				ArchiveActivity.this.startActivity(intent);
			}
		});
	}
}
