package com.qcast.tower.form;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.Response;
import com.qcast.tower.logic.structure.ExaminationPackage;
import com.qcast.tower.logic.structure.TimePeriod;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.type.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 预约检查页
 */
public class ExaminationActivity extends Activity {
	/**
	 * 套餐列表
	 */
	private List<ExaminationPackage> packages = new List<ExaminationPackage>();
	/**
	 * 当前选择的套餐
	 */
	private int current = -1;
	/**
	 * 当前选择的时间段
	 */
	private int period = -1;


	/**
	 * 界面创建
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "ExaminationActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_examination);
		// 界面处理
		prepare();
	}

	/**
	 * 回调
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RadioActivity.RESULT_CANCEL) {
			return;
		}
		if(1 == requestCode) {
			current = data.getIntExtra("index", current);
			fresh();
		}
		else if(2 == requestCode) {
			period = data.getIntExtra("index", period);
			fresh();
		}
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		// 处理返回按钮
		dealReturn();
		// 处理选择套餐
		dealPackage();
		//
		dealTime();
		//
		dealConfirm();
		//
		loadExaminationList();
	}

	/**
	 * 处理返回按钮
	 */
	public void dealReturn() {
		ImageButton button = (ImageButton) this.findViewById(R.id.examination_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExaminationActivity.this.finish();
			}
		});
	}

	/**
	 * 处理选择套餐
	 */
	public void dealPackage() {
		Button button = (Button) this.findViewById(R.id.examination_button_package);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] captions = new String[packages.size()];
				int i = 0;
				for(ExaminationPackage examinationPackage : packages) {
					captions[i++] = examinationPackage.name;
				}
				Intent intent = new Intent(ExaminationActivity.this, RadioActivity.class);
				intent.putExtra("title", "请选择体检套餐");
				intent.putExtra("items", captions);
				intent.putExtra("index", current);
				ExaminationActivity.this.startActivityForResult(intent, 1);
			}
		});
		TextView text = (TextView) this.findViewById(R.id.examination_text_description);
		text.setMovementMethod(new ScrollingMovementMethod());
	}

	/**
	 * 处理选择时间段
	 */
	public void dealTime() {
		Button button = (Button) this.findViewById(R.id.examination_button_time);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(-1 == current) {
					return;
				}
				List<TimePeriod> timePeriodList = packages.get(current).periods;
				String[] captions = new String[timePeriodList.size()];
				int i = 0;
				for(TimePeriod timePeriod : timePeriodList) {
					captions[i++] = timePeriod.toString();
				}
				Intent intent = new Intent(ExaminationActivity.this, RadioActivity.class);
				intent.putExtra("title", "请选择体检时间");
				intent.putExtra("items", captions);
				intent.putExtra("index", period);
				ExaminationActivity.this.startActivityForResult(intent, 2);
			}
		});
	}
	
	/**
	 * 处理确认按钮
	 */
	public void dealConfirm() {
		Button button = (Button) this.findViewById(R.id.examination_button_confirm);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!reserve()) {
					return;
				}
				ExaminationActivity.this.finish();
			}
		});
	}

	/**
	 * 加载体检列表
	 */
	public void loadExaminationList() {
		Host.doCommand("examinationlist", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(ExaminationActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(ExaminationActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				packages.clear();
				for(IJSON item : result) {
					packages.add(ExaminationPackage.build((JSONObject) item));
				}
			}
		}, Logic.token);
	}
	
	/**
	 * 预约
	 */
	public boolean reserve() {
		if(null == getCurrentPeriod()) {
			Toast.makeText(ExaminationActivity.this, "请选择套餐和时间段", Toast.LENGTH_LONG).show();
			return false;
		}
		Host.doCommand("reserveExamination", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(ExaminationActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(ExaminationActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(ExaminationActivity.this, "预约成功", Toast.LENGTH_LONG).show();
				ExaminationActivity.this.finish();
				Intent intent = new Intent(ExaminationActivity.this, MyReserveHistoryActivity.class);
				ExaminationActivity.this.startActivity(intent);
				return;
			}
		}, getCurrentPackage().id, getCurrentPeriod().date.toString(), getCurrentPeriod().span, Logic.token);
		return true;
	}

	/**
	 * 获取当前套餐
	 * 
	 * @return 当前套餐
	 */
	public ExaminationPackage getCurrentPackage() {
		if(-1 == current) {
			return null;
		}
		if(current >= packages.size()) {
			return null;
		}
		return packages.get(current);
	}

	/**
	 * 获取当前时间段
	 * 
	 * @return 当前时间段
	 */
	public TimePeriod getCurrentPeriod() {
		if(-1 == period) {
			return null;
		}
		ExaminationPackage examinationPackage = getCurrentPackage();
		if(null == examinationPackage) {
			return null;
		}
		if(period >= examinationPackage.periods.size()) {
			return null;
		}
		return examinationPackage.periods.get(period);
	}

	/**
	 * 刷新
	 */
	public void fresh() {
		if(null == getCurrentPackage()) {
			return;
		}
		Button button = (Button) this.findViewById(R.id.examination_button_package);
		button.setText(getCurrentPackage().name);
		TextView text = (TextView) this.findViewById(R.id.examination_text_description);
		text.setText(getCurrentPackage().detail);
		if(null == getCurrentPeriod()) {
			return;
		}
		button = (Button) this.findViewById(R.id.examination_button_time);
		button.setText(getCurrentPeriod().toString());
	}
}
