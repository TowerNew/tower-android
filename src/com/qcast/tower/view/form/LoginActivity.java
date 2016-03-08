package com.qcast.tower.view.form;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.framework.Helper;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.core.IResponse;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.general.utility.GeneralHelper;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 引导界面
 */
@ResourceView(id = R.layout.activity_login)
public class LoginActivity extends ActivityEx {
	@ResourceView(id = R.id.login_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.login_text_phone)
	public EditText txtPhone;
	@ResourceView(id = R.id.login_text_code)
	public EditText txtCode;
	@ResourceView(id = R.id.login_button_phone)
	public Button btnPhone;
	@ResourceView(id = R.id.login_button_code)
	public Button btnCode;
	@ResourceView(id = R.id.login_label_memo)
	public TextView labMemo;
	

	/**
	 * 最近一次时间
	 */
	private long lastTick = 1;
	/**
	 * 等待对话框
	 */
	private AlertDialog dialog = null;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginActivity.this.finish();
			}
		});
		labMemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Helper.openBrowser(LoginActivity.this, Host.fetchURL("UserProtocol"));
			}
		});
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
						//
						Controller.doDelay(new Runnable() {
							@Override
							public void run() {
								if((new Date()).getTime() > 30 * 1000 + lastTick) {
									btnPhone.setText("验证码");
									return;
								}
								long remain = 30 - ((new Date()).getTime() - lastTick) / 1000;
								btnPhone.setText(remain + " 秒");
								Controller.doDelay(this, 1000);
							}
						}, 1000);
					}
				}, phone);
			}
		});
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
				dialog = GeneralHelper.showWaiting(LoginActivity.this);
				Me.login(LoginActivity.this, phone, code, new IEventable<Boolean>() {
					@Override
					public void on(Boolean result) {
						if(null != dialog) {
							dialog.cancel();
						}
						dialog = null;
						if(!result) {
							return;
						}
						Intent intent = (Intent) LoginActivity.this.getIntent().getParcelableExtra("intent");
						if(null == intent) {
							intent = new Intent();
							intent.putExtra("RESULT", "SUCCESS");
							LoginActivity.this.setResult(1, intent);
						}
						else {
							LoginActivity.this.startActivity(intent);
						}
						LoginActivity.this.finish();
					}
				});
			}
		});
	}
}
