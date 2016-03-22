package com.qcast.tower.view.form;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.structure.TimePeriod;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.type.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * 预约医生页
 */
public class ReserveDoctorActivity extends Activity {
	/**
	 * 医生ID
	 */
	private String doctorId = null;
	/**
	 * 时间段
	 */
	public List<TimePeriod> periods = new List<TimePeriod>();
	/**
	 * 当前选择的套餐
	 */
	private int current = -1;
	
	
	/**
	 * 界面创建
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "ReserveDoctorActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reservedoctor);
		//
		load();
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
	}
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		// 处理返回按钮
		dealReturn();
		// 处理选择套餐
		dealTime();
		//
		dealConfirm();
		//
		loadTimes();
	}
	
	/**
	 * 加载数据
	 */
	public void load() {
		doctorId = this.getIntent().getStringExtra("doctorId");
		if(null == doctorId) {
			this.finish();
			return;
		}
	}

	/**
	 * 处理返回按钮
	 */
	public void dealReturn() {
		ImageButton button = (ImageButton) this.findViewById(R.id.reservedoctor_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReserveDoctorActivity.this.finish();
			}
		});
	}
	
	/**
	 * 处理选择时间段
	 */
	public void dealTime() {
		Button button = (Button) this.findViewById(R.id.reservedoctor_button_time);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] captions = new String[periods.size()];
				int i = 0;
				for(TimePeriod timePeriod : periods) {
					captions[i++] = timePeriod.toString();
				}
				Intent intent = new Intent(ReserveDoctorActivity.this, RadioActivity.class);
				intent.putExtra("title", "请选择预约时间");
				intent.putExtra("items", captions);
				intent.putExtra("index", current);
				ReserveDoctorActivity.this.startActivityForResult(intent, 1);
			}
		});
	}
	
	/**
	 * 处理确认按钮
	 */
	public void dealConfirm() {
		Button button = (Button) this.findViewById(R.id.reservedoctor_button_confirm);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!reserve()) {
					return;
				}
				ReserveDoctorActivity.this.finish();
			}
		});
	}
	
	/**
	 * 加载体检列表
	 */
	public void loadTimes() {
		Networking.doCommand("doctortimelist", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(ReserveDoctorActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(ReserveDoctorActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				periods.clear();
				for(IJSON item : result) {
					periods.add(TimePeriod.build((JSONObject) item));
				}
			}
		}, doctorId);
	}
	
	/**
	 * 获取当前套餐
	 * 
	 * @return 当前套餐
	 */
	public TimePeriod getCurrentTime() {
		if(-1 == current) {
			return null;
		}
		if(current >= periods.size()) {
			return null;
		}
		return periods.get(current);
	}

	/**
	 * 预约
	 */
	public boolean reserve() {
		if(null == getCurrentTime()) {
			Toast.makeText(ReserveDoctorActivity.this, "请选择时间段", Toast.LENGTH_LONG).show();
			return false;
		}
		EditText memo = (EditText) this.findViewById(R.id.reservedoctor_text_memo); 
		Networking.doCommand("reserveDoctor", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(ReserveDoctorActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(ReserveDoctorActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(ReserveDoctorActivity.this, "预约成功", Toast.LENGTH_LONG).show();
				ReserveDoctorActivity.this.finish();
				Intent intent = new Intent(ReserveDoctorActivity.this, MyReserveHistoryActivity.class);
				ReserveDoctorActivity.this.startActivity(intent);
                return;
			}
		}, getCurrentTime().date.toString(), getCurrentTime().span, memo.getText().toString(), doctorId, Logic.token);
		return true;
	}
	
	/**
	 * 刷新
	 */
	public void fresh() {
		Button button = (Button) this.findViewById(R.id.reservedoctor_button_time);
		button.setText(getCurrentTime().toString());
	}
}
