package com.qcast.tower.view.form;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.user.Friend;
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
		userId = this.getIntent().getStringExtra("userId");
		if(Text.isBlank(userId)) {
			userId = "";
		}
		else {
			Friend friend = Me.instance.fetchFriendById(userId);
			if(null == friend) {
				AddFriendActivity.this.finish();
				return;
			}
			txtPhone.setText("***");
			if(null != friend.relation) {
				txtRelation.setText(friend.relation);
			}
		}
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddFriendActivity.this.finish();
			}
		});
		labConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int mode = 1;
				if(Text.isBlank(userId)) {
					mode = 0;
				}
				Host.doCommand("editrelation", new JSONResponse(AddFriendActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null != content && content.getInteger("code") > 0) {
							Me.instance.refreshMember(AddFriendActivity.this, new IEventable<Boolean>() {
								@Override
								public void on(Boolean data) {
									AddFriendActivity.this.finish();
								}
							});
							return;
						}
					}
				}, Me.instance.token, userId, mode, txtRelation.getText().toString(), txtPhone.getText().toString());
			}
		});
	}
}
