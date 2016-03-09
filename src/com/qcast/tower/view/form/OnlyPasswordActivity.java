package com.qcast.tower.view.form;

import android.content.Intent;
import android.os.Bundle;

/**
 * 只有验证安全密码可以访问的界面
 */
public class OnlyPasswordActivity extends OnlyUserActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(this.getIntent().getBooleanExtra("password", false)) {
			return;
		}
		Intent intent = new Intent(OnlyPasswordActivity.this, PasswordActivity.class);
		intent.putExtra("mode", PasswordActivity.MODE_VERIFY);
		intent.putExtra("xx", 9527);
		intent.putExtra("intent", this.getIntent());
		this.startActivity(intent);
		finish();
	}
}
