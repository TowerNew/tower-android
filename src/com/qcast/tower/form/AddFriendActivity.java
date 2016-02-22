package com.qcast.tower.form;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 添加朋友
 */
@ResourceView(id = R.layout.activity_addfriend)
public class AddFriendActivity extends ActivityEx {
	@ResourceView(id = R.id.addfriend_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.addfriend_label_confirm)
	public TextView labConfirm;
	@ResourceView(id = R.id.addfriend_text_phone)
	public EditText txtPhone;
	@ResourceView(id = R.id.addfriend_text_relation)
	public EditText txtRelation;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddFriendActivity.this.finish();
			}
		});
		labConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Host.doCommand("editrelation", new JSONResponse(AddFriendActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null != content && content.getInteger("code") > 0) {
							AddFriendActivity.this.finish();
						}
					}
				}, Me.instance.token, "", txtRelation.getText().toString(), txtPhone.getText().toString());
			}
		});
	}
}
