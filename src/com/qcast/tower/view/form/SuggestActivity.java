package com.qcast.tower.view.form;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 意见页面
 */
@ResourceView(id = R.layout.activity_suggest)
public class SuggestActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.suggest_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.suggest_label_confirm)
	public TextView labConfirm;
	@ResourceView(id = R.id.suggest_text_content)
	public EditText txtContent;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null == Me.instance) {
			return;
		}
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SuggestActivity.this.finish();
			}
		});
		labConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Toast.makeText(SuggestActivity.this, "用户未登录无法评论", Toast.LENGTH_LONG).show();
					return;
				}
				Host.doCommand("suggest", new JSONResponse(SuggestActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content || content.getInteger("code", -1) < 0) {
							return;
						}
						SuggestActivity.this.finish();
					}
				}, Me.instance.token, txtContent.getText().toString());
			}
		});
	}
}
