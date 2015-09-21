package com.qcast.tower.form;

import java.io.File;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.ImageResponse;
import com.qcast.tower.logic.response.Response;
import com.qcast.tower.logic.util.FileUtils;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * 我的信息页
 */
public class UserInfoActivity extends Activity {
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "UserInfoActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userinfo);
		// 界面处理
		prepare();
		//
		loadUserInfo();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		ImageButton imageButton = (ImageButton) this.findViewById(R.id.userinfo_button_return);
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserInfoActivity.this.finish();
			}
		});
		Button button = (Button) this.findViewById(R.id.userinfo_button_confirm);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveUserInfo();
			}
		});
		button = (Button) this.findViewById(R.id.userinfo_button_exit);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Logic.token = null;
				Logic.name = null;
				Storage.setUser("token", null);
				Storage.setUser("userId", null);
				Storage.save();
				UserInfoActivity.this.finish();
			}
		});
		ImageButton buttonIdCardFront = (ImageButton) UserInfoActivity.this.findViewById(R.id.userinfo_image_idcardfront);
		buttonIdCardFront.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFileChooser(5);
			}
		});
		ImageButton buttonIdCardBack = (ImageButton) UserInfoActivity.this.findViewById(R.id.userinfo_image_idcardback);
		buttonIdCardBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFileChooser(6);
			}
		});
	}

	/**
	 * 加载用户信息
	 */
	public void loadUserInfo() {
		Host.doCommand("loadUserInfo", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(UserInfoActivity.this, "获取我的信息失败", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					JSONString msg = (JSONString) resultObject.get("msg");
					if(null == msg) {
						Toast.makeText(UserInfoActivity.this, "服务器异常", Toast.LENGTH_LONG).show();
					}
					else {
						Toast.makeText(UserInfoActivity.this, msg.getValue(), Toast.LENGTH_LONG).show();
					}
					return;
				}
				JSONObject dataObject = (JSONObject) resultObject.get("data");
				String phone = ((JSONString) dataObject.get("phone")).getValue();
				if(null == phone) {
					phone = "";
				}
				String name = ((JSONString) dataObject.get("name")).getValue();
				if(null == name) {
					name = "";
				}
				String idnumber = ((JSONString) dataObject.get("idnumber")).getValue();
				if(null == idnumber) {
					idnumber = "";
				}
				int gender = ((JSONNumber) dataObject.get("gender")).intValue();
				String birthday = ((JSONString) dataObject.get("birthday")).getValue();
				if(null == birthday) {
					birthday = "";
				}
				String bankNumber = ((JSONString) dataObject.get("bankNumber")).getValue();
				if(null == bankNumber) {
					bankNumber = "";
				}
				String idCardFrontImage = ((JSONString) dataObject.get("idcardfront")).getValue();
				String idCardBackImage = ((JSONString) dataObject.get("idcardback")).getValue();
				//
				EditText txtPhone = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_photo);
				txtPhone.setText(phone);
				EditText txtName = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_name);
				txtName.setText(name);
				EditText txtIdNumber = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_idnumber);
				txtIdNumber.setText(idnumber);
				EditText txtGender= (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_gender);
				if(1 == gender) {
					txtGender.setText("男");
				}
				else if(2 == gender) {
					txtGender.setText("女");
				}
				else {
					txtGender.setText("未填写");
				}
				EditText txtBirthday = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_birthday);
				txtBirthday.setText(birthday);
				EditText txtBanknumber = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_banknumber);
				txtBanknumber.setText(bankNumber);
	            // 加载图片
				if(null != idCardFrontImage) {
		            Host.doImage("image", new ImageResponse(idCardFrontImage, null) {
						@Override
						public void onFinished(Bitmap content) {
							ImageButton buttonIdCardFront = (ImageButton) UserInfoActivity.this.findViewById(R.id.userinfo_image_idcardfront);
							buttonIdCardFront.setImageBitmap(content);
						}
		            }, idCardFrontImage);
				}
				if(null != idCardBackImage) {
		            Host.doImage("image", new ImageResponse(idCardBackImage, null) {
						@Override
						public void onFinished(Bitmap content) {
							ImageButton buttonIdCardBack = (ImageButton) UserInfoActivity.this.findViewById(R.id.userinfo_image_idcardback);
							buttonIdCardBack.setImageBitmap(content);
						}
		            }, idCardBackImage);
				}
			}
		}, Logic.token);
	}
	
	/**
	 * 保存用户信息
	 */
	public void saveUserInfo() {
		EditText txtPhone = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_photo);
		final String phone = txtPhone.getText().toString();
		EditText txtName = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_name);
		final String name = txtName.getText().toString();
		EditText txtIdNumber = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_idnumber);
		final String idnumber = txtIdNumber.getText().toString();
		EditText txtGender= (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_gender);
		final String gender = txtGender.getText().toString();
		EditText txtBirthday = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_birthday);
		final String birthday = txtBirthday.getText().toString();
		EditText txtBanknumber = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_banknumber);
		final String banknumber = txtBanknumber.getText().toString();
		Host.doCommand("saveUserInfo", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(UserInfoActivity.this, "获取我的信息失败", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					JSONString msg = (JSONString) resultObject.get("msg");
					if(null == msg) {
						Toast.makeText(UserInfoActivity.this, "服务器异常", Toast.LENGTH_LONG).show();
					}
					else {
						Toast.makeText(UserInfoActivity.this, msg.getValue(), Toast.LENGTH_LONG).show();
					}
					return;
				}
				Logic.name = name;
				Logic.idNumber = idnumber;
				if("男".equals(gender)) {
					Logic.gender = 1;
				}
				else if("女".equals(gender)) {
					Logic.gender = 2;
				}
				else {
					Logic.gender = 0;
				}
				Logic.birthday = birthday;
				Logic.bankNumber = banknumber;
				UserInfoActivity.this.finish();
				return;
			}
		}, Logic.token, name, idnumber, gender, birthday, banknumber);
	}
	
	private void showFileChooser(int rId) {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
	    try {
	        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), rId);
	    }
	    catch (android.content.ActivityNotFoundException ex) {
	        Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri uri = null;
		String path = null;
		switch (requestCode) {
	        case 5:
		        if (resultCode != RESULT_OK) {
		        	return;
		        }
	            uri = data.getData();
	            path = FileUtils.getPath(this, uri);
	            Host.doCommand("idcardfront", new CommonResponse<String>() {
					@Override
					public void onFinished(String content) {
						if(Response.CODE_SUCCESS != code()) {
							Toast.makeText(UserInfoActivity.this, "网络异常", Toast.LENGTH_LONG).show();
							return;
						}
						JSONObject resultObject = JSONObject.convert(content);
						if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
							JSONString msg = (JSONString) resultObject.get("msg");
							if(null == msg) {
								Toast.makeText(UserInfoActivity.this, "服务器异常", Toast.LENGTH_LONG).show();
							}
							else {
								Toast.makeText(UserInfoActivity.this, msg.getValue(), Toast.LENGTH_LONG).show();
							}
							return;
						}
						String url = ((JSONString) resultObject.get("data")).getValue();
			            Host.doImage("image", new ImageResponse(url, null) {
							@Override
							public void onFinished(Bitmap content) {
								ImageButton buttonIdCardFront = (ImageButton) UserInfoActivity.this.findViewById(R.id.userinfo_image_idcardfront);
								buttonIdCardFront.setImageBitmap(content);
							}
			            }, url);
					}
	            }, Logic.token, new File(path));
		        break;
	        case 6:
	        	if (resultCode != RESULT_OK) {
		        	return;
		        }
	            uri = data.getData();
	            path = FileUtils.getPath(this, uri);
	            Host.doCommand("idcardback", new CommonResponse<String>() {
					@Override
					public void onFinished(String content) {
						if(Response.CODE_SUCCESS != code()) {
							Toast.makeText(UserInfoActivity.this, "网络异常", Toast.LENGTH_LONG).show();
							return;
						}
						JSONObject resultObject = JSONObject.convert(content);
						if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
							JSONString msg = (JSONString) resultObject.get("msg");
							if(null == msg) {
								Toast.makeText(UserInfoActivity.this, "服务器异常", Toast.LENGTH_LONG).show();
							}
							else {
								Toast.makeText(UserInfoActivity.this, msg.getValue(), Toast.LENGTH_LONG).show();
							}
							return;
						}
						String url = ((JSONString) resultObject.get("data")).getValue();
			            Host.doImage("image", new ImageResponse(url, null) {
							@Override
							public void onFinished(Bitmap content) {
								ImageButton buttonIdCardBack = (ImageButton) UserInfoActivity.this.findViewById(R.id.userinfo_image_idcardback);
								buttonIdCardBack.setImageBitmap(content);
							}
			            }, url);
					}
	            }, Logic.token, new File(path));
		        break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
}
