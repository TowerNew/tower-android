package com.qcast.tower.form;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.core.IResponse;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 引导界面
 */
public class LoginActivity extends Activity {
	/**
	 * 最近一次时间
	 */
	private long lastTick = 1;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "LoginActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		prepare();
	}
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		final EditText txtPhone = (EditText) findViewById(R.id.login_text_phone);
		txtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					String phone = txtPhone.getText().toString();
					if(LoginActivity.this.getString(R.string.login_text_phone).equals(phone)) {
						txtPhone.setText("");
					}
				}
			}
		});
		final EditText txtCode = (EditText) findViewById(R.id.login_text_code);
		txtCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					String code = txtCode.getText().toString();
					if(LoginActivity.this.getString(R.string.login_text_code).equals(code)) {
						txtCode.setText("");
					}
				}
			}
		});
		Button btnPhone = (Button) findViewById(R.id.login_button_phone);
		btnPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = ((EditText) findViewById(R.id.login_text_phone)).getText().toString();
				Pattern pattern = Pattern.compile("^[1][35678][0-9]{9}$");
				Matcher matcher = pattern.matcher(phone);
				if(!matcher.matches()) {
					Toast.makeText(LoginActivity.this, "手机号码格式不正确", Toast.LENGTH_LONG).show();
					return;
				}
				if((new Date()).getTime() <= 30 * 1000 + lastTick) {
					Toast.makeText(LoginActivity.this, "请等待" + (lastTick + 30 * 1000 - (new Date()).getTime()) / 1000 + "秒后重试", Toast.LENGTH_LONG).show();
					return;
				}
				lastTick = (new Date()).getTime();
				Host.doCommand("fetchCode", new CommonResponse<String>() {
					@Override
					public void onFinished(String content) {
						if(IResponse.CODE_SUCCESS != code()) {
							Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_LONG).show();
							return;
						}
						JSONObject object = JSONObject.convert(content);
						if(((JSONNumber) object.get("code")).intValue() <= 0) {
							Toast.makeText(LoginActivity.this, ((JSONString) object.get("msg")).getValue(), Toast.LENGTH_LONG).show();
							return;
						}
						Toast.makeText(LoginActivity.this, "短信发送成功", Toast.LENGTH_LONG).show();
						EditText txtCode = (EditText) findViewById(R.id.login_text_code);
						txtCode.setFocusable(true);
						txtCode.setFocusableInTouchMode(true);
						txtCode.requestFocus();
						txtCode.findFocus();
					}
				}, phone);
			}
		});
		Button btnCode = (Button) findViewById(R.id.login_button_code);
		btnCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String phone = ((EditText) findViewById(R.id.login_text_phone)).getText().toString();
				if(Text.isBlank(phone)) {
					return;
				}
				final String code = ((EditText) findViewById(R.id.login_text_code)).getText().toString();
				if(Text.isBlank(code)) {
					return;
				}
				Host.doCommand("login", new CommonResponse<String>() {
					@Override
					public void onFinished(String content) {
						if(IResponse.CODE_SUCCESS != code()) {
							Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_LONG).show();
							return;
						}
						JSONObject object = JSONObject.convert(content);
						if(((JSONNumber) object.get("code")).intValue() <= 0) {
							Toast.makeText(LoginActivity.this, ((JSONString) object.get("msg")).getValue(), Toast.LENGTH_LONG).show();
							return;
						}
						Logic.phone = phone;
						Logic.token = ((JSONString) ((JSONObject) object.get("data")).get("token")).getValue();
						Logic.userId = ((JSONString) ((JSONObject) object.get("data")).get("userGlobalId")).getValue();
						Logic.name = ((JSONString) ((JSONObject) object.get("data")).get("name")).getValue();
						if(null != ((JSONObject) object.get("data")).get("imUsername")) {
							Logic.imUsername = ((JSONString) ((JSONObject) object.get("data")).get("imUsername")).getValue();
							Storage.setUser("imUsername", Logic.imUsername);
						}
						Logic.tvMap.clear();
						JSONArray array = (JSONArray) (((JSONObject) object.get("data")).get("tvList"));
						for(IJSON item : array) {
							// {"cityId":0.0,"name":"麦燕贞的电视","regionId":1.0,"imUsername":"tvuser_136"}
							JSONObject itemObject = (JSONObject) item;
							String name = "TV";
							if(null != itemObject.get("name")) {
								name = ((JSONString) itemObject.get("name")).getValue();
							}
							String imUsername = "";
							if(null != itemObject.get("imUsername")) {
								imUsername = ((JSONString) itemObject.get("imUsername")).getValue();
							}
							Logic.addTVInfo(name, imUsername);
						}
						Storage.setUser("token", Logic.token);
						Storage.setUser("userId", Logic.userId);
						Storage.setUser("phone", phone);
						Storage.setUser("name", Logic.name);
						//
						EMChatManager.getInstance().login(Logic.imUsername, Logic.phone, new EMCallBack() {
							@Override
							public void onSuccess() {
								runOnUiThread(new Runnable() {
									public void run() {
										EMGroupManager.getInstance().loadAllGroups();
										EMChatManager.getInstance().loadAllConversations();
										Log.d("main", "登陆聊天服务器成功！");		
									}
								});
							}
						 
							@Override
							public void onProgress(int progress, String status) {
						 
							}
						 
							@Override
							public void onError(int code, String message) {
								Log.d("main", "登陆聊天服务器失败！");
							}
						});
						// 返回
						Intent intent = new Intent();
						intent.putExtra("RESULT", "SUCCESS");
						LoginActivity.this.setResult(1, intent);
						LoginActivity.this.finish();
					}
				}, phone, code);
			}
		});
	}
}
