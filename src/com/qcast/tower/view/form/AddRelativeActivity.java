package com.qcast.tower.view.form;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.user.Relative;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;

/**
 * 添加朋友
 */
@ResourceView(id = R.layout.activity_addrelative)
public class AddRelativeActivity extends OnlyUserActivity {
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
		userId = this.getIntent().getStringExtra("userId");
		if(Text.isBlank(userId)) {
			userId = "";
		}
		else {
			Relative relative = Me.instance.fetchRelativeById(userId);
			if(null == relative) {
				AddRelativeActivity.this.finish();
				return;
			}
			if(null != relative.relation) {
				txtRelation.setText(relative.relation);
			}
			if(null != relative.name) {
				txtName.setText(relative.name);
				txtName.setEnabled(false);
			}
			if(null != relative.idNumber) {
				txtIdNumber.setText(relative.idNumber);
				txtIdNumber.setEnabled(false);
			}
		}
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
				if(Text.isBlank(txtName.getText().toString())) {
					Toast.makeText(AddRelativeActivity.this, "姓名不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				Pattern pattern = Pattern.compile("^(^\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$");
				Matcher matcher = pattern.matcher(txtIdNumber.getText().toString());
				if(!matcher.matches()) {
					Toast.makeText(AddRelativeActivity.this, "身份证号码格式不正确", Toast.LENGTH_LONG).show();
					return;
				}
				Host.doCommand("editowner", new JSONResponse(AddRelativeActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null != content && content.getInteger("code") > 0) {
							Me.instance.refreshMember(AddRelativeActivity.this, new IEventable<Boolean>() {
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
