package com.qcast.tower.view.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.storage.Storage;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.general.utility.GeneralHelper;
import com.slfuture.pretty.general.view.form.ImageActivity;
import com.slfuture.pretty.general.view.form.TextEditActivity;
import com.slfuture.pretty.qcode.Module;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 首页
 */
@ResourceView(id = R.layout.activity_userinfo)
public class UserInfoActivity extends ActivityEx {
	@ResourceView(id = R.id.userinfo_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.userinfo_label_logout)
	public TextView labLogout;
	@ResourceView(id = R.id.userinfo_image_photo)
	public ImageView imgPhoto;
	@ResourceView(id = R.id.userinfo_layout_phone)
	public View viewPhone;
	@ResourceView(id = R.id.userinfo_text_photo)
	public TextView labPhone;
	@ResourceView(id = R.id.userinfo_layout_nickname)
	public View viewNickname;
	@ResourceView(id = R.id.userinfo_text_nickname)
	public TextView labNickname;
	@ResourceView(id = R.id.userinfo_layout_address)
	public View viewAddress;
	@ResourceView(id = R.id.userinfo_text_address)
	public TextView labAddress;
	@ResourceView(id = R.id.userinfo_layout_name)
	public View viewName;
	@ResourceView(id = R.id.userinfo_text_name)
	public TextView labName;
	@ResourceView(id = R.id.userinfo_layout_idnumber)
	public View viewIdNumber;
	@ResourceView(id = R.id.userinfo_text_idnumber)
	public TextView labIdNumber;
	@ResourceView(id = R.id.userinfo_image_snapshot)
	public ImageView imgSnapshot;
	@ResourceView(id = R.id.userinfo_label_status)
	public TextView labStatus;
	@ResourceView(id = R.id.userinfo_image_qcode)
	public ImageView imgQCode;
	

	private boolean isAlteringIdCard = false;

	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
	}

	@Override
	public void onStart() {
		super.onStart();
		prepare();
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
				if(isAlteringIdCard) {
					Networking.doCommand("idcardfront", new JSONResponse(UserInfoActivity.this) {
						@Override
						public void onFinished(JSONVisitor content) {
							if(null == content || content.getInteger("code", 0) <= 0) {
								return;
							}
							String url = content.getString("data");
							if(null == url) {
								return;
							}
							Me.instance.snapshot = url;
							Networking.doImage("image", new ImageResponse(url) {
								@Override
								public void onFinished(Bitmap content) {
									imgSnapshot.setImageBitmap(content);
								}
							}, url);
						}
					}, Me.instance.token, com.qcast.tower.framework.Storage.compressImageFile(new File(Storage.getPathFromURI(UserInfoActivity.this, uri)), 500, 500));
				}
				else {
					alter("photo", new File(Storage.getPathFromURI(UserInfoActivity.this, uri)));
				}
				break;
			case GeneralHelper.INTENT_REQUEST_CAMERA:
				if(RESULT_OK != resultCode || null == data) {
					return;
				}
				if(isAlteringIdCard) {
					Networking.doCommand("idcardfront", new JSONResponse(UserInfoActivity.this) {
						@Override
						public void onFinished(JSONVisitor content) {
							if(null == content || content.getInteger("code", 0) <= 0) {
								return;
							}
							String url = content.getString("data");
							if(null == url) {
								return;
							}
							Me.instance.snapshot = url;
							Networking.doImage("image", new ImageResponse(url) {
								@Override
								public void onFinished(Bitmap content) {
									imgSnapshot.setImageBitmap(content);
								}
							}, url);
						}
					}, Me.instance.token, com.qcast.tower.framework.Storage.compressImageFile(Storage.saveCamera(data), 500, 500));
				}
				else {
					alter("photo", Storage.saveCamera(data));
				}
				break;
			case 1:
				if(TextEditActivity.RESULT_CANCEL == resultCode) {
					return;
				}
				alter("nickname", data.getStringExtra("result"));
				break;
			case 2:
				if(TextEditActivity.RESULT_CANCEL == resultCode) {
					return;
				}
				alter("address", data.getStringExtra("result"));
				break;
			case 3:
				if(TextEditActivity.RESULT_CANCEL == resultCode) {
					return;
				}
				alter("name", data.getStringExtra("result"));
				break;
			case 4:
				if(RegionActivity.RESULT_CANCEL == resultCode) {
					return;
				}
				alter("idnumber", data.getStringExtra("result"));
				break;
		}
	    super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserInfoActivity.this.finish();
			}
		});
		labLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(UserInfoActivity.this).setTitle("确认注销吗？")  
				.setIcon(android.R.drawable.ic_dialog_info)  
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Me.instance.logout();
							UserInfoActivity.this.finish();
						}  
				}).setNegativeButton("返回", new DialogInterface.OnClickListener() {
			        @Override  
			        public void onClick(DialogInterface dialog, int which) {}  
				}).show();
			}
		});
		final ImageView imgPhoto = (ImageView) this.findViewById(R.id.userinfo_image_photo);
		if(Text.isBlank(Me.instance.photoUrl)) {
			imgPhoto.setImageBitmap(GraphicsHelper.makeImageRing(GraphicsHelper.makeCycleImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.user_photo_default), 200, 200), Color.WHITE, 4));
		}
		else {
            Networking.doImage("image", new ImageResponse(Me.instance.photoUrl) {
				@Override
				public void onFinished(Bitmap content) {
					if(null == content) {
						return;
					}
					imgPhoto.setImageBitmap(GraphicsHelper.makeImageRing(GraphicsHelper.makeCycleImage(content, 200, 200), Color.WHITE, 4));
				}
            }, Me.instance.photoUrl);
		}
		imgPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isAlteringIdCard = false;
				GeneralHelper.selectImage(UserInfoActivity.this);
			}
		});
		labPhone.setText(Me.instance.phone);
		labNickname.setText(Me.instance.nickname);
		viewNickname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfoActivity.this, TextEditActivity.class);
				intent.putExtra("title", "编辑昵称");
				intent.putExtra("default", Me.instance.nickname);
				intent.putExtra("length", 8);
				intent.putExtra("description", "最多8个字符");
				UserInfoActivity.this.startActivityForResult(intent, 1);
			}
		});
		labAddress.setText(Me.instance.address);
		viewAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfoActivity.this, TextEditActivity.class);
				intent.putExtra("title", "编辑地址");
				intent.putExtra("default", Me.instance.address);
				intent.putExtra("description", "最多30个字符");
				intent.putExtra("length", 30);
				UserInfoActivity.this.startActivityForResult(intent, 2);
			}
		});
		labName.setText(Me.instance.name);
		viewName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Me.instance.isAuthenticated) {
					Toast.makeText(UserInfoActivity.this, "已认证信息无法修改", Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(UserInfoActivity.this, TextEditActivity.class);
				intent.putExtra("title", "编辑姓名");
				intent.putExtra("default", Me.instance.name);
				intent.putExtra("description", "请填写真实姓名");
				intent.putExtra("length", 6);
				UserInfoActivity.this.startActivityForResult(intent, 3);
			}
		});
    	if(Me.instance.isAuthenticated) {
    		labStatus.setBackgroundResource(R.drawable.button_green);
    		labStatus.setText("已认证");
    	}
    	else {
    		labStatus.setBackgroundResource(R.drawable.button_red);
        	labStatus.setText("未认证");
    	}
		labIdNumber.setText(Me.instance.idNumber);
		viewIdNumber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Me.instance.isAuthenticated) {
					Toast.makeText(UserInfoActivity.this, "已认证信息无法修改", Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(UserInfoActivity.this, TextEditActivity.class);
				intent.putExtra("title", "编辑身份证号");
				intent.putExtra("default", Me.instance.idNumber);
				intent.putExtra("description", "请务必填写真实身份证号");
				intent.putExtra("length", 20);
				UserInfoActivity.this.startActivityForResult(intent, 4);
			}
		});
		if(!Text.isBlank(Me.instance.snapshot)) {
            Networking.doImage("image", new ImageResponse(Me.instance.snapshot, 100, 100) {
				@Override
				public void onFinished(Bitmap content) {
					if(null == content) {
						return;
					}
					imgSnapshot.setImageBitmap(content);
				}
            }, Me.instance.snapshot);
		}
		imgSnapshot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Me.instance.isAuthenticated) {
					Toast.makeText(UserInfoActivity.this, "已认证信息无法修改", Toast.LENGTH_LONG).show();
					return;
				}
				isAlteringIdCard = true;
				GeneralHelper.selectImage(UserInfoActivity.this);
			}
		});
		imgQCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String path = com.qcast.tower.framework.Storage.IMAGE_ROOT + "qcode." + Me.instance.phone + ".png";
				if(!(new File(path)).exists()) {
					Bitmap bitmap = Module.createQRImage("add://" + Me.instance.phone, 500, 500);
					if(null == bitmap) {
						return;
					}
					FileOutputStream stream = null;
					try {
						stream = new FileOutputStream(new File(path));
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						stream.flush();
					}
					catch(Exception ex) {
						Log.e("TOWER", "保存二维码失败", ex);
					}
					finally {
						if(null != stream) {
							try {
								stream.close();
							}
							catch(Exception ex) { }
						}
						stream = null;
					}
				}
				Intent intent = new Intent(UserInfoActivity.this, ImageActivity.class);
				intent.putExtra("path", path);
				UserInfoActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * 修改字段
	 * 
	 * @param field 字段名
	 * @param value 值
	 */
	public void alter(String field, Object value) {
		Object tag = null;
		if(value instanceof File) {
			tag = field + "=" + ((File) value).getAbsolutePath();
		}
		else {
			tag = field + "=" + value;
		}
		Networking.doCommand("AlterUserInfo", new JSONResponse(this, tag) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content) {
					return;
				}
				if(content.getInteger("code") < 0) {
					return;
				}
				String value = (String) tag;
				String field = Text.substring(value, null, "=");
				value = Text.substring(value, "=", null);
				String data = content.getString("data");
				if("nickname".equals(field)) {
					Me.instance.nickname = value;
					labNickname.setText(Me.instance.nickname);
				}
				else if("address".equals(field)) {
					Me.instance.address = value;
					labAddress.setText(Me.instance.address);
				}
				else if("name".equals(field)) {
					Me.instance.name = value;
					labName.setText(Me.instance.name);
				}
				else if("idnumber".equals(field)) {
					Me.instance.idNumber = value;
					labIdNumber.setText(Me.instance.idNumber);
				}
				else if("photo".equals(field)) {
					imgPhoto.setImageBitmap(GraphicsHelper.makeImageRing(GraphicsHelper.makeCycleImage(BitmapFactory.decodeFile(value), 200, 200), Color.WHITE, 4));
					Me.instance.photoUrl = data;
				}
				try {
					Me.instance.save();
				}
				catch (IOException e) { }
			}
		}, Me.instance.token, field, value);
	}
}
