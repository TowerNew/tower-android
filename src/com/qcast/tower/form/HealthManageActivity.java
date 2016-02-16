package com.qcast.tower.form;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

/**
 * 健康管理
 */
public class HealthManageActivity extends Activity {
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "HealthManageActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_healthmanage);
		// 界面处理
		prepare();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		// 处理返回按钮
		dealReturn();
		// 处理列表
		dealList();
	}

	/**
	 * 处理返回按钮
	 */
	public void dealReturn() {
		ImageButton button = (ImageButton) this.findViewById(R.id.healthmanage_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HealthManageActivity.this.finish();
			}
		});
	}

	/**
	 * 处理列表
	 */
	private void dealList() {
		View prescription = (View) this.findViewById(R.id.healthmanage_layout_prescription);
		prescription.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HealthManageActivity.this, PrescriptionActivity.class);
				HealthManageActivity.this.startActivity(intent);
			}
		});
		View report = (View) this.findViewById(R.id.healthmanage_layout_report);
		report.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWeb("http://cdn.oss.wehop-resources.wehop.cn/user/sites/health-data/v-1/index.html?token=" + Logic.token);
			}
		});
		View survey = (View) this.findViewById(R.id.healthmanage_layout_survey);
		survey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWeb("http://cdn.oss.wehop-resources.wehop.cn/well-risk/sites/v-1/index.html#/form?token=" + Logic.token);
			}
		});
		View paper = (View) this.findViewById(R.id.healthmanage_layout_paper);
		paper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWeb("http://cdn.oss.wehop-resources.wehop.cn/user/sites/health-report/v-1/index.html?token=" + Logic.token);
			}
		});
	}

	/**
	 * 打开浏览器
	 * 
	 * @param url 地址
	 */
	private void openWeb(String url) {
		Intent intent = new Intent(HealthManageActivity.this, WebActivity.class);
		intent.putExtra("url", url);
		HealthManageActivity.this.startActivity(intent);
	}
}
