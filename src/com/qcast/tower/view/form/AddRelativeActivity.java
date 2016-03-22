package com.qcast.tower.view.form;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.user.Relative;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.model.core.ITargetEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.storage.Storage;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pretty.general.utility.GeneralHelper;

/**
 * 添加朋友
 */
@ResourceView(id = R.layout.activity_addrelative)
public class AddRelativeActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.addrelative_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.addrelative_label_confirm)
	public TextView labConfirm;
	@ResourceView(id = R.id.addrelative_text_name)
	public EditText txtName;
	@ResourceView(id = R.id.addrelative_text_idnumber)
	public EditText txtIdNumber;
	@ResourceView(id = R.id.addrelative_text_relation)
	public EditText txtRelation;
	@ResourceView(id = R.id.addrelative_image_snapshot)
	public ImageView imgSnapshot;

	/**
	 * 带编辑的成员ID
	 */
	private String userId = "";
	/**
	 * 身份证快照
	 */
	private File snapshot = null;


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
			if(null != relative.snapshot) {
				snapshot = new File(com.qcast.tower.framework.Storage.imagePath(Networking.parseFileNameWithURL(relative.snapshot)));
				Networking.doImage("image", imgSnapshot, new ITargetEventable<ImageView, Bitmap>() {
					@Override
					public void on(ImageView target, Bitmap event) {
						target.setImageBitmap(event);
					}
				}, relative.snapshot);
			}
		}
		imgClose.setOnClickListener(new View.OnClickListener() {
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
				if(null == snapshot || !snapshot.exists()) {
					Toast.makeText(AddRelativeActivity.this, "请上传身份证正面照片", Toast.LENGTH_LONG).show();
					return;
				}
				Networking.doCommand("editowner", new JSONResponse(AddRelativeActivity.this) {
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
				}, Me.instance.token, userId, mode, txtRelation.getText().toString(), txtName.getText().toString(), txtIdNumber.getText().toString(), snapshot);
			}
		});
		imgSnapshot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Text.isBlank(userId)) {
					Relative relative = Me.instance.fetchRelativeById(userId);
					if(null == relative) {
						AddRelativeActivity.this.finish();
						return;
					}
					if(relative.isAuthenticated) {
						Toast.makeText(AddRelativeActivity.this, "已认证信息无法修改", Toast.LENGTH_LONG).show();
						return;
					}
				}
				GeneralHelper.selectImage(AddRelativeActivity.this);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case GeneralHelper.INTENT_REQUEST_PHONE:
				if(RESULT_OK != resultCode || null == data) {
					return;
				}
				Uri uri = (data == null || resultCode != -1 ? null : data.getData());
				if(null == uri) {
					return;
				}
				snapshot = new File(Storage.getPathFromURI(AddRelativeActivity.this, uri));
				break;
			case GeneralHelper.INTENT_REQUEST_CAMERA:
				if(RESULT_OK != resultCode || null == data) {
					return;
				}
				snapshot = Storage.saveCamera(data);
				break;
		}
		if(null != snapshot && snapshot.exists()) {
			snapshot = com.qcast.tower.framework.Storage.compressImageFile(snapshot, 500, 500);
			imgSnapshot.setImageBitmap(GraphicsHelper.decodeFile(snapshot));
		}
	    super.onActivityResult(requestCode, resultCode, data);
	}
}
