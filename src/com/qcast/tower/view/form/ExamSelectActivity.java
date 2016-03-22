package com.qcast.tower.view.form;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 体检选择页
 */
public class ExamSelectActivity extends Activity {
	/**
	 * 放弃修改
	 */
	public final static int RESULT_CANCEL = 0;
	/**
	 * 已更新
	 */
	public final static int RESULT_UPDATED = 1;
	/**
	 * 区域医院
	 */
	public final static int EXAM_HOSPITAL = 1;
	/**
	 * 区域套餐
	 */
	public final static int EXAM_PACKAGE = 2;
	
	
	/**
	 * 当前区域级别
	 */
	protected int selectLevel = EXAM_HOSPITAL;
	/**
	 * 当前省份ID
	 */
	protected int currentHospitalId = 0;
	/**
	 * 当前城市ID
	 */
	protected int currentPackageId = 0;
	/**
	 * 数据列表
	 */
	protected ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "ExamSelectyActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_examselect);
		// 参数处理
		currentPackageId = this.getIntent().getIntExtra("packageId", 0);
		// 界面处理
		prepare();
		// 加载数据
		load();
	}

	/**
	 * 处理列表
	 */
	private void prepare() {
		ImageButton button = (ImageButton) this.findViewById(R.id.examselect_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				ExamSelectActivity.this.setResult(RESULT_CANCEL, intent);
				ExamSelectActivity.this.finish();
			}
		});
		//
		ListView listview = (ListView) this.findViewById(R.id.examselect_list);
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataList, R.layout.listview_radio, new String[]{"name", "status"}, new int[]{R.id.radiolist_label_caption, R.id.radiolist_image_status});
		simpleAdapter.setViewBinder(new ViewBinder() {
			@SuppressWarnings("deprecation")
			public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView)view;
                    Bitmap bitmap = (Bitmap) data;
                    imageView.setImageDrawable(new BitmapDrawable(bitmap));
                    return true;
                }
                return false;
            }
        });
		listview.setAdapter(simpleAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				if(EXAM_HOSPITAL == selectLevel) {
					selectLevel = EXAM_PACKAGE;
					currentHospitalId = (Integer) dataList.get(index).get("id");
					load();
				}
				else if(EXAM_PACKAGE == selectLevel) {
					currentPackageId = (Integer) dataList.get(index).get("id");
					Intent intent = new Intent();
					intent.putExtra("packageId", currentPackageId);
					intent.putExtra("name", (String) dataList.get(index).get("name"));
					intent.putExtra("detail", (String) dataList.get(index).get("detail"));
					intent.putExtra("image", (String) dataList.get(index).get("image"));
					ExamSelectActivity.this.setResult(RESULT_UPDATED, intent);
					ExamSelectActivity.this.finish();
				}
            }
		});
	}

	/**
	 * 加载数据
	 */
	public void load() {
		if(EXAM_HOSPITAL == selectLevel) {
			TextView labTitle = (TextView) this.findViewById(R.id.examselect_label_title);
			labTitle.setText("医院");
			Networking.doCommand("gethospital", new CommonResponse<String>() {
				@Override
				public void onFinished(String content) {
					if(Response.CODE_SUCCESS != code()) {
						return;
					}
					JSONObject resultObject = JSONObject.convert(content);
					if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
						return;
					}
					JSONArray jsonArray = (JSONArray) resultObject.get("data");
					if(null == jsonArray) {
						return;
					}
					dataList.clear();
					for(IJSON object : jsonArray) {
						JSONObject jsonObject = (JSONObject) object;
						//
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("id", ((JSONNumber) (jsonObject.get("id"))).intValue());
						map.put("name", ((JSONString) (jsonObject.get("name"))).getValue());
						dataList.add(map);
					}
					ListView listview = (ListView) findViewById(R.id.examselect_list);
					SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
					adapter.notifyDataSetChanged();
				}
			}, Logic.regionId);
		}
		else if(EXAM_PACKAGE == selectLevel) {
			TextView labTitle = (TextView) this.findViewById(R.id.examselect_label_title);
			labTitle.setText("套餐");
			Networking.doCommand("examinationlist", new CommonResponse<String>() {
				@Override
				public void onFinished(String content) {
					if(Response.CODE_SUCCESS != code()) {
						return;
					}
					JSONObject resultObject = JSONObject.convert(content);
					if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
						return;
					}
					JSONArray jsonArray = (JSONArray) resultObject.get("data");
					if(null == jsonArray) {
						return;
					}
					dataList.clear();
					for(IJSON object : jsonArray) {
						JSONObject jsonObject = (JSONObject) object;
						//
						HashMap<String, Object> map = new HashMap<String, Object>();
						if(null != jsonObject.get("id")) {
							map.put("id", ((JSONNumber) (jsonObject.get("id"))).intValue());
						}
						if(null != jsonObject.get("name")) {
							map.put("name", ((JSONString) (jsonObject.get("name"))).getValue());
						}
						if(null != jsonObject.get("detail")) {
							map.put("detail", ((JSONString) jsonObject.get("detail")).getValue());
						}
						if(null != jsonObject.get("image")) {
							map.put("image", ((JSONString) jsonObject.get("image")).getValue());
						}
						if(null != jsonObject.get("times")) {
							List<TimePeriod> periods = new List<TimePeriod>();
							for(IJSON time : (JSONArray) jsonObject.get("times")) {
								periods.add(TimePeriod.build((JSONObject) time));
							}
							map.put("times", periods);
						}
						dataList.add(map);
					}
					ListView listview = (ListView) findViewById(R.id.examselect_list);
					SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
					adapter.notifyDataSetChanged();
				}
			}, Logic.token, currentHospitalId);
		}
	}
}
