package com.qcast.tower.view.form;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.model.core.ITargetEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.storage.Storage;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.general.utility.GeneralHelper;
import com.slfuture.pretty.general.view.form.TextEditActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 首页
 */
@ResourceView(id = R.layout.activity_id_authentication)
public class IdAuthenticationActivity extends ActivityEx{
	
	@ResourceView(id = R.id.authentication_image_close)
	public ImageView imgClose;
	
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
	
	@ResourceView(id = R.id.userinfo_button_submit)
	public Button submitButton;
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
					Networking.doCommand("idcardfront", new JSONResponse(IdAuthenticationActivity.this) {
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
					}, Me.instance.token, com.qcast.tower.framework.Storage.compressImageFile(new File(Storage.getPathFromURI(IdAuthenticationActivity.this, uri)), 500, 500));
				}
				
				break;
			case GeneralHelper.INTENT_REQUEST_CAMERA:
				if(RESULT_OK != resultCode || null == data) {
					return;
				}
				if(isAlteringIdCard) {
					Networking.doCommand("idcardfront", new JSONResponse(IdAuthenticationActivity.this) {
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
				break;			
			case 1:
				if(TextEditActivity.RESULT_CANCEL == resultCode) {
					return;
				}
				alter("name", data.getStringExtra("result"));
				break;
			case 2:
				if(RegionActivity.RESULT_CANCEL == resultCode) {
					return;
				}
				alter("idnumber", data.getStringExtra("result"));
				break;
			
		}
	    super.onActivityResult(requestCode, resultCode, data);
	}


	/*
	 * 界面预处理
	 */
    public void prepare() { 
    	imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    Intent intent=new Intent(IdAuthenticationActivity.this,UserInfoActivity.class);
			    IdAuthenticationActivity.this.startActivity(intent);
				IdAuthenticationActivity.this.finish();				
			}
		});  
    	
    	if(Me.instance.isAuthenticated){
			isAlteringIdCard = false;
			submitButton.setVisibility(View.GONE);
			labName.setText(Me.instance.name);
			viewName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(Me.instance.isAuthenticated) {
						Toast.makeText(IdAuthenticationActivity.this, "已认证信息无法修改", Toast.LENGTH_LONG).show();
						return;
				}}});			
			labIdNumber.setText(Me.instance.idNumber);
			viewIdNumber.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(Me.instance.isAuthenticated) {
						Toast.makeText(IdAuthenticationActivity.this, "已认证信息无法修改", Toast.LENGTH_LONG).show();
						return;
					}
				}});
			imgSnapshot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(Me.instance.isAuthenticated) {
						Toast.makeText(IdAuthenticationActivity.this, "已认证信息无法修改", Toast.LENGTH_LONG).show();
						return;
					}			
				}
			});	
		}else {
		labName.setText(Me.instance.name);
		viewName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent(IdAuthenticationActivity.this, TextEditActivity.class);
				intent.putExtra("title", "编辑姓名");
				intent.putExtra("default", Me.instance.name);
				intent.putExtra("description", "请填写真实姓名");
				intent.putExtra("length", 6);
				IdAuthenticationActivity.this.startActivityForResult(intent, 1);				
			}
		});   	
		labIdNumber.setText(Me.instance.idNumber);
		viewIdNumber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent(IdAuthenticationActivity.this, TextEditActivity.class);
				intent.putExtra("title", "编辑身份证号");
				intent.putExtra("default", Me.instance.idNumber);
				intent.putExtra("description", "请务必填写真实身份证号");
				intent.putExtra("length", 20);
				IdAuthenticationActivity.this.startActivityForResult(intent, 2);
			}
		});
		
    	if(!Text.isBlank(Me.instance.snapshot)){
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
					isAlteringIdCard = true;
					GeneralHelper.selectImage(IdAuthenticationActivity.this);
				}
			});	
    	
    	
    	submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				if(Text.isBlank(labName.getText().toString())) {
					Toast.makeText(IdAuthenticationActivity.this, "姓名不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				Pattern pattern = Pattern.compile("^(^\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$");
				Matcher matcher = pattern.matcher(labIdNumber.getText().toString());
				if(!matcher.matches()) {
					Toast.makeText(IdAuthenticationActivity.this, "身份证号码格式不正确", Toast.LENGTH_LONG).show();
					return;
				}
				if(Text.isBlank(Me.instance.snapshot)) {
					Toast.makeText(IdAuthenticationActivity.this, "请上传身份证正面照片", Toast.LENGTH_LONG).show();
					return;
				}
							
				Networking.doCommand("saveIdInfo", new JSONResponse(IdAuthenticationActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content || content.getInteger("code", -1) < 0) {
							return;
						}
						Toast.makeText(IdAuthenticationActivity.this, "提交成功", Toast.LENGTH_LONG).show();	
						
					}
				},labIdNumber.getText().toString(),labName.getText().toString(),Me.instance.token);	
			 
			}	
			});	
    	
    	if(submitButton.hasOnClickListeners()){
    		
    		labName.setText(Me.instance.name);
    		viewName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					Toast.makeText(IdAuthenticationActivity.this, "已提交不可更改", Toast.LENGTH_SHORT).show();
				}
			});	
    		labIdNumber.setText(Me.instance.idNumber);
    		viewIdNumber.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					Toast.makeText(IdAuthenticationActivity.this, "已提交不可更改", Toast.LENGTH_SHORT).show();
				}
			});	
    		imgSnapshot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					Toast.makeText(IdAuthenticationActivity.this, "已提交不可更改", Toast.LENGTH_SHORT).show();
				}
			});	
    		
    	}
    	
    	}}
		
	
   
			
    	
  		
    	
    	
    	
    	
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
				
				 if("name".equals(field)) {
					Me.instance.name = value;
					labName.setText(Me.instance.name);
				}
				else if("idnumber".equals(field)) {
					Me.instance.idNumber = value;
					labIdNumber.setText(Me.instance.idNumber);
				}
				try {
					Me.instance.save();
				}
				catch (IOException e) { }
			}
		}, Me.instance.token, field, value);
	}
}
