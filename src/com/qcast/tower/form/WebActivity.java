package com.qcast.tower.form;

import com.qcast.tower.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

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
				if(url.startsWith("mailto:") || url.startsWith("geo:") ||url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	                startActivity(intent);
	                browser.pauseTimers();
	                return false;
	            }
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
            @Override  
            public void onReceivedTitle(WebView view, String title) {  
                super.onReceivedTitle(view, title);  
                TextView textView = (TextView) WebActivity.this.findViewById(R.id.web_text_caption);
				textView.setText(title);
            }
		});
		browser.setDownloadListener(new DownloadListener() {
			@Override  
	        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {  
	            Uri uri = Uri.parse(url);  
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	            startActivity(intent);  
	        }
		});
		ImageButton button = (ImageButton) this.findViewById(R.id.web_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				browser.goBack();
			}
		});
		TextView closeButton = (TextView) this.findViewById(R.id.web_text_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebActivity.this.finish();
			}
		});
		String url = this.getIntent().getStringExtra("url");
		if(null == url) {
			return;
		}
		browser.loadUrl(url);
	}
}
