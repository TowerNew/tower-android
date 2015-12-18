package com.qcast.tower.form;

import java.io.File;

import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.Response;
import com.qcast.tower.logic.util.FileUtils;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.text.Text;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的信息页
 */
public class UserInfoActivity extends Activity {
	/**
	 * 支持的银行种类
	 */
	public final static String[] BANK_NAMES = {"中国银行", "招商银行", "农业银行", "建设银行", "工商银行"};
	

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
				Logic.phone = null;
				Storage.setUser("token", null);
				Storage.setUser("userId", null);
				Storage.setUser("name", null);
				Storage.setUser("phone", null);
				Storage.setUser("imUsername", null);
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
				String idnumber = "";
				if(null != dataObject.get("idnumber")) {
					idnumber = ((JSONString) dataObject.get("idnumber")).getValue();
				}
				String bankName = "";
				if(null != dataObject.get("bankName")) {
					bankName = ((JSONString) dataObject.get("bankName")).getValue();
				}
				if(Text.isBlank(bankName)) {
					bankName = "点击选择";
				}
				String bankRegion = "";
				if(null != dataObject.get("bankRegion")) {
					bankRegion = ((JSONString) dataObject.get("bankRegion")).getValue();
				}
				String bankNumber = "";
				if(null != dataObject.get("bankNumber")) {
					bankNumber = ((JSONString) dataObject.get("bankNumber")).getValue();
				}
				String idCardFrontImage = null;
				String idCardFrontImageName = null;
				if(null != dataObject.get("idcardfront")) {
					idCardFrontImage = ((JSONString) dataObject.get("idcardfront")).getValue();
					idCardFrontImageName = Storage.getImageName(idCardFrontImage);
				}
				String idCardBackImage = null;
				String idCardBackImageName = null;
				if(null != dataObject.get("idcardback")) {
					idCardBackImage = ((JSONString) dataObject.get("idcardback")).getValue();
					idCardBackImageName = Storage.getImageName(idCardBackImage);
				}
				//
				EditText txtPhone = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_photo);
				txtPhone.setText(phone);
				txtPhone.setEnabled(false);
				EditText txtName = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_name);
				txtName.setText(name);
				EditText txtIdNumber = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_idnumber);
				txtIdNumber.setText(idnumber);
				TextView labBankName = (TextView) UserInfoActivity.this.findViewById(R.id.userinfo_text_bankname);
				labBankName.setText(bankName);
				labBankName.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView labBankName = (TextView) UserInfoActivity.this.findViewById(R.id.userinfo_text_bankname);
						String bankName = labBankName.getText().toString();
						Intent intent = new Intent(UserInfoActivity.this, RadioActivity.class);
						intent.putExtra("title", "选择银行");
						intent.putExtra("items", BANK_NAMES);
						intent.putExtra("index", -1);
						for(int i = 0; i < BANK_NAMES.length; i++) {
							String item = BANK_NAMES[i];
							if(item.equals(bankName)) {
								intent.putExtra("index", i);
								break;
							}
						}
						UserInfoActivity.this.startActivityForResult(intent, 1);
					}
				});
				EditText txtBankRegion = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_bankregion);
				txtBankRegion.setText(bankRegion);
				EditText txtBanknumber = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_banknumber);
				txtBanknumber.setText(bankNumber);
	            // 加载图片
				if(!Text.isBlank(idCardFrontImage)) {
		            Host.doImage("image", new ImageResponse(idCardFrontImageName, null) {
						@Override
						public void onFinished(Bitmap content) {
							ImageButton buttonIdCardFront = (ImageButton) UserInfoActivity.this.findViewById(R.id.userinfo_image_idcardfront);
							buttonIdCardFront.setImageBitmap(content);
						}
		            }, idCardFrontImage);
				}
				if(!Text.isBlank(idCardBackImage)) {
		            Host.doImage("image", new ImageResponse(idCardBackImageName, null) {
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
		Log.i("TOWER", phone);
		EditText txtName = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_name);
		final String name = txtName.getText().toString();
		EditText txtIdNumber = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_idnumber);
		final String idnumber = txtIdNumber.getText().toString();
		TextView labBankName = (TextView) UserInfoActivity.this.findViewById(R.id.userinfo_text_bankname);
		String bankName = labBankName.getText().toString();
		if(bankName.equals("点击选择")) {
			bankName = "";
		}
		EditText txtBankRegion = (EditText) UserInfoActivity.this.findViewById(R.id.userinfo_text_bankregion);
		final String bankRegion = txtBankRegion.getText().toString();
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
				Storage.setUser("name", Logic.name);
				Logic.idNumber = idnumber;
				Logic.bankNumber = banknumber;
				UserInfoActivity.this.finish();
				return;
			}
		}, Logic.token, name, idnumber, bankName, bankRegion, banknumber);
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
			case 1:
				if(resultCode == RadioActivity.RESULT_CANCEL) {
					return;
				}
				if(1 == requestCode) {
					int current = -1;
					current = data.getIntExtra("index", current);
					if(-1 == current) {
						return;
					}
					TextView labBankName = (TextView) UserInfoActivity.this.findViewById(R.id.userinfo_text_bankname);
					labBankName.setText(BANK_NAMES[current]);
				}
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
						String imageName = Storage.getImageName(url);
			            Host.doImage("image", new ImageResponse(imageName, null) {
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
						if(null == resultObject.get("data")) {
							return;
						}
						String url = ((JSONString) resultObject.get("data")).getValue();
						String imageName = Storage.getImageName(url);
			            Host.doImage("image", new ImageResponse(imageName, null) {
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
