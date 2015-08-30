package com.qcast.tower.form;

import com.qcast.tower.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 浏览器页
 */
public class WebActivity extends Activity {
	/**
	 * 浏览器对象
	 */
	protected WebView browser = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web);
		browser = (WebView) this.findViewById(R.id.web_browser);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.requestFocus();
		browser.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				browser.loadUrl(url);
				return true;
			}
		});
		browser.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(100 == newProgress) {
					view.setVisibility(View.VISIBLE);
				}
			}
		});
		String url = this.getIntent().getStringExtra("url");
		if(null == url) {
			return;
		}
		browser.loadUrl(url);
	}
}
