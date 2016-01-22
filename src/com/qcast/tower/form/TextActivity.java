package com.qcast.tower.form;

import com.qcast.tower.R;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.os.Bundle;
import android.widget.TextView;

/**
 * 纯文本页面
 */
@ResourceView(id = R.layout.activity_text)
public class TextActivity extends ActivityEx {
	@ResourceView(id = R.id.text_text_title)
	public TextView labTitle;
	@ResourceView(id = R.id.text_text_content)
	public TextView labContent;
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = this.getIntent().getStringExtra("title");
		if(null == title) {
			title = "";
		}
		String content = this.getIntent().getStringExtra("content");
		if(null == content) {
			content = "";
		}
		labTitle.setText(title);
		labContent.setText(content);
	}
}
