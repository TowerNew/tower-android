package com.qcast.tower.view.form;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.view.annotation.ResourceView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 安全密码页面
 */
@ResourceView(id = R.layout.activity_password)
public class PasswordActivity extends OnlyUserActivity {
	/**
	 * 模式
	 */
	public final static int MODE_VERIFY = 0;
	public final static int MODE_MODIFY_CHECK = 1;
	public final static int MODE_MODIFY_SET = 2;

	@ResourceView(id = R.id.password_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.password_lable_title)
	public TextView labTitle;
	@ResourceView(id = R.id.password_text_password)
	public EditText txtPassword;
	@ResourceView(id = R.id.password_label_password1)
	public TextView labPassword1;
	@ResourceView(id = R.id.password_label_password2)
	public TextView labPassword2;
	@ResourceView(id = R.id.password_label_password3)
	public TextView labPassword3;
	@ResourceView(id = R.id.password_label_password4)
	public TextView labPassword4;
	@ResourceView(id = R.id.password_label_password5)
	public TextView labPassword5;
	@ResourceView(id = R.id.password_label_password6)
	public TextView labPassword6;
	
	private final static int[] PASSWORD_CONTROL = {R.id.password_label_password1, R.id.password_label_password2, R.id.password_label_password3, R.id.password_label_password4, R.id.password_label_password5, R.id.password_label_password6};
	
	/**
	 * 模式
	 */
	private int mode = MODE_VERIFY;
	/**
	 * 当前密码
	 */
	public String currentPassword = null;
	/**
	 * 当前安全密码
	 */
	public String password = null;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null == Me.instance) {
			return;
		}
		mode = this.getIntent().getIntExtra("mode", MODE_VERIFY);
		Networking.doCommand("CheckPassword", new JSONResponse(PasswordActivity.this) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content || content.getInteger("code", 0) <= 0) {
					refresh();
					return;
				}
				if(MODE_VERIFY == mode || MODE_MODIFY_CHECK == mode) {
					mode = MODE_MODIFY_SET;
					reset();
					refresh();
				}
			}
		}, Me.instance.token, "");
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PasswordActivity.this.finish();
			}
		});
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				txtPassword.requestFocus();
		    	InputMethodManager inputManager = (InputMethodManager) PasswordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
		    	inputManager.showSoftInput(txtPassword, 0); 
			}
		};
		labPassword1.setOnClickListener(listener);
		labPassword2.setOnClickListener(listener);
		labPassword3.setOnClickListener(listener);
		labPassword4.setOnClickListener(listener);
		labPassword5.setOnClickListener(listener);
		labPassword6.setOnClickListener(listener);
		txtPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			@Override
			public void afterTextChanged(Editable s) {
				String text = txtPassword.getText().toString();
				if(text.length() >= 6) {
					if(MODE_VERIFY == mode) {
						Networking.doCommand("CheckPassword", new JSONResponse(PasswordActivity.this) {
							@Override
							public void onFinished(JSONVisitor content) {
								if(null == content || content.getInteger("code", 0) <= 0) {
									reset();
									return;
								}
								Intent intent = (Intent) PasswordActivity.this.getIntent().getParcelableExtra("intent");
								if(null != intent) {
									intent.putExtra("password", 1);
									PasswordActivity.this.startActivity(intent);
								}
								else {
									intent = new Intent(PasswordActivity.this, ArchiveActivity.class);
									intent.putExtra("password", 1);
									PasswordActivity.this.startActivity(intent);
								}
								PasswordActivity.this.finish();
							}
						}, Me.instance.token, text.substring(0, 6));
					}
					else if(MODE_MODIFY_CHECK == mode) {
						Networking.doCommand("CheckPassword", new JSONResponse(PasswordActivity.this) {
							@Override
							public void onFinished(JSONVisitor content) {
								if(null == content || content.getInteger("code", 0) <= 0) {
									reset();
									return;
								}
								reset();
								mode = MODE_MODIFY_SET;
								refresh();
							}
						}, Me.instance.token, text.substring(0, 6));
						currentPassword = text.substring(0, 6);
					}
					else if(MODE_MODIFY_SET == mode) {
						if(null == password) {
							password = text.substring(0, 6);
							reset();
							refresh();
						}
						else if(password.equals(text.substring(0, 6))) {
							Networking.doCommand("SetPassword", new JSONResponse(PasswordActivity.this, password) {
								@Override
								public void onFinished(JSONVisitor content) {
									if(null == content || content.getInteger("code", -1) < 0) {
										reset();
										return;
									}
									Intent intent = (Intent) PasswordActivity.this.getIntent().getParcelableExtra("intent");
									if(null != intent) {
										intent.putExtra("pasword", 1);
										PasswordActivity.this.startActivity(intent);
									}
									else {
										intent = new Intent(PasswordActivity.this, ArchiveActivity.class);
										intent.putExtra("password", 1);
										PasswordActivity.this.startActivity(intent);
									}
									PasswordActivity.this.finish();
								}
							}, Me.instance.token, currentPassword, password);
						}
						else {
							Toast.makeText(PasswordActivity.this, "两次输入不一致", Toast.LENGTH_LONG).show();
							reset();
						}
					}
				}
				else {
					refresh();
				}
			}
		});
	}

	@Override
    protected void onResume() {
    	super.onResume();
    	Controller.doDelay(new Runnable() {
			@Override
			public void run() {
		    	txtPassword.requestFocus();
		    	InputMethodManager inputManager = (InputMethodManager) PasswordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
		    	inputManager.showSoftInput(txtPassword, 0); 
			}
    	}, 1000);
    }

	/**
	 * 重置状态
	 */
	public void reset() {
		txtPassword.setText("");
		labPassword1.setText("");
		labPassword2.setText("");
		labPassword3.setText("");
		labPassword4.setText("");
		labPassword5.setText("");
		labPassword6.setText("");
	}

	/**
	 * 刷新密码栏
	 */
	public void refresh() {
		if(MODE_VERIFY == mode) {
			labTitle.setText("请输入安全密码");
		}
		else if(MODE_MODIFY_CHECK == mode) {
			labTitle.setText("请输入安全密码");
		}
		else if(MODE_MODIFY_SET == mode) {
			if(null == password) {
				labTitle.setText("请设置安全密码");
			}
			else {
				labTitle.setText("请再次输入密码");
			}
		}
		for(int i = 0; i < PASSWORD_CONTROL.length; i++) {
			TextView lab = (TextView) this.findViewById(PASSWORD_CONTROL[i]);
			lab.setText("");
		}
		for(int i = 0; i < txtPassword.getText().length(); i++) {
			TextView lab = (TextView) this.findViewById(PASSWORD_CONTROL[i]);
			lab.setText("●");
		}
	}
}
