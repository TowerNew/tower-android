package com.qcast.tower.form;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 添加朋友
 */
@ResourceView(id = R.layout.activity_addrelative)
public class AddRelativeActivity extends ActivityEx {
	@ResourceView(id = R.id.addrelative_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.addrelative_label_confirm)
	public TextView labConfirm;
	@ResourceView(id = R.id.addrelative_text_name)
	public EditText txtName;
	@ResourceView(id = R.id.addrelative_text_idnumber)
	public EditText txtIdNumber;
	@ResourceView(id = R.id.addrelative_text_relation)
	public EditText txtRelation;

	/**
	 * 带编辑的成员ID
	 */
	private String userId = "";


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
				AddRelativeActivity.this.finish();
			}
		});
		labConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int mode = 1;
				if(Text.isBlank(userId)) {
					mode = 0;
				}
				Host.doCommand("editowner", new JSONResponse(AddRelativeActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null != content && content.getInteger("code") > 0) {
							Me.instance.refresh(AddRelativeActivity.this, new IEventable<Boolean>() {
								@Override
								public void on(Boolean data) {
									AddRelativeActivity.this.finish();
								}
							});
							return;
						}
					}
				}, Me.instance.token, userId, mode, txtRelation.getText().toString(), txtName.getText().toString(), txtIdNumber.getText().toString());
			}
		});
	}
}
