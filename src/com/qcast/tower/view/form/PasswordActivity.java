package com.qcast.tower.view.form;

import java.io.IOException;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.view.annotation.ResourceView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

/**
 * 安全密码页面
 */
@ResourceView(id = R.layout.activity_password)
public class PasswordActivity extends OnlyUserActivity {
	/**
	 * 模式
	 */
	public final static int MODE_VERIFY = 0;
	public final static int MODE_MODIFY = 1;

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
	@ResourceView(id = R.id.password_button_confirm)
	public Button btnConfirm;
	
	private final static int[] PASSWORD_CONTROL = {R.id.password_label_password1, R.id.password_label_password2, R.id.password_label_password3, R.id.password_label_password4, R.id.password_label_password5, R.id.password_label_password6};
	
	/**
	 * 模式
	 */
	private int mode = MODE_VERIFY;
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
		if(MODE_VERIFY == mode && null == Me.instance.password) {
			mode = MODE_MODIFY;
		}
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PasswordActivity.this.finish();
			}
		});
		txtPassword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String text = v.getText().toString();
				if(text.length() >= 6) {
					if(null == password) {
						password = text.substring(0, 6);
						txtPassword.setText("");
						labPassword1.setText("");
						labPassword2.setText("");
						labPassword3.setText("");
						labPassword4.setText("");
						labPassword5.setText("");
						labPassword6.setText("");
						labTitle.setText("请再次输入");
					}
					else {
						if(password.equals(text.substring(0, 6))) {
							Host.doCommand("setPassword", new JSONResponse(PasswordActivity.this, password) {
								@Override
								public void onFinished(JSONVisitor content) {
									if(null == content || content.getInteger("code", -1) < 0) {
										reset();
										return;
									}
									Me.instance.password = (String) tag;
									try {
										Me.instance.save();
									}
									catch (IOException e) { }
									PasswordActivity.this.finish();
								}
							}, Me.instance.token, password);
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
				return false;
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
		password = null;
		txtPassword.setText("");
		labPassword1.setText("");
		labPassword2.setText("");
		labPassword3.setText("");
		labPassword4.setText("");
		labPassword5.setText("");
		labPassword6.setText("");
		labTitle.setText("请设置安全密码");
	}

	/**
	 * 刷新密码栏
	 */
	public void refresh() {
		for(int i = 0; i < PASSWORD_CONTROL.length; i++) {
			TextView lab = (TextView) this.findViewById(PASSWORD_CONTROL[i]);
			lab.setText("");
		}
		for(int i = 0; i < txtPassword.getText().length(); i++) {
			TextView lab = (TextView) this.findViewById(PASSWORD_CONTROL[i]);
			lab.setText("*");
		}
	}
}
