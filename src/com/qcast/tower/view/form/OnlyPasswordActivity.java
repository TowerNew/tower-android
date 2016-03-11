package com.qcast.tower.view.form;

import com.qcast.tower.business.Me;
import com.slfuture.pluto.view.component.ActivityEx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 只有验证安全密码可以访问的界面
 */
public class OnlyPasswordActivity extends ActivityEx {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null == Me.instance) {
			Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		switch(this.getIntent().getIntExtra("password", -1)) {
		case -1:
			finish();
			break;
		case 0:
			Intent intent = new Intent(OnlyPasswordActivity.this, PasswordActivity.class);
			intent.putExtra("mode", PasswordActivity.MODE_VERIFY);
			intent.putExtra("intent", this.getIntent());
			this.startActivity(intent);
			finish();
			break;
		case 1:
			break;
		}
	}
}
