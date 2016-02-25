package com.qcast.tower.view.form;

import com.qcast.tower.Program;
import com.qcast.tower.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 关于界面
 */
public class AboutActivity extends Activity {
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "AboutActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		// 界面处理
		prepare();
	}
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		// 处理返回按钮
		dealReturn();
		//
		TextView labVersion = (TextView) this.findViewById(R.id.about_text_content);
		labVersion.setText("当前版本：" + Program.VERSION);
	}

	/**
	 * 处理返回按钮
	 */
	public void dealReturn() {
		ImageButton button = (ImageButton) this.findViewById(R.id.about_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
			}
		});
	}
}
