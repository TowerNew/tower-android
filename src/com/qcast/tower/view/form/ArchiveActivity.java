package com.qcast.tower.view.form;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.pluto.view.annotation.ResourceView;

@ResourceView(id = R.layout.activity_archive)
public class ArchiveActivity extends OnlyUserActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Me.instance.isAuthenticated) {
			Toast.makeText(this, "您的身份证信息尚未认证通过", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(ArchiveActivity.this, UserInfoActivity.class);
			this.startActivity(intent);
			this.finish();
			return;
		}
		if(null == Me.instance.password) {
			Toast.makeText(this, "请先设置安全密码", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(ArchiveActivity.this, PasswordActivity.class);
			this.startActivity(intent);
			this.finish();
			return;
		}
	}
}
