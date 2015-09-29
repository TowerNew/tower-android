package com.qcast.tower.form;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.Response;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 自我诊断页
 */
public class SelfDiagnosticActivity extends Activity {
	/**
	 * 当前部位列表
	 */
	protected ArrayList<HashMap<String, Object>> bodyList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 当前症状列表
	 */
	protected ArrayList<HashMap<String, Object>> symptomList = new ArrayList<HashMap<String, Object>>();
	
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "SelfDiagnosticActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_selfdiagnostic);
		// 界面处理
		prepare();
		//
		loadBody();
	}
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		ImageButton button = (ImageButton) this.findViewById(R.id.selfdiagnostic_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SelfDiagnosticActivity.this.finish();
			}
		});
		dealBody();
		dealSymptom();
	}
	
	/**
	 * 处理部位
	 */
	private void dealBody() {
		final ListView listview = (ListView) this.findViewById(R.id.selfdiagnostic_list_body);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, bodyList, R.layout.listview_body,
			new String[]{"caption"}, 
	        new int[]{R.id.body_label_caption});
		listItemAdapter.setViewBinder(new ViewBinder() {
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
		listview.setAdapter(listItemAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				for(int i = 0; i < listview.getChildCount(); i++) {
					if(index == i) {
						// v.setBackgroundResource(R.color.white);
						listview.getChildAt(i).setBackgroundResource(R.color.white);
					}
					else {
						listview.getChildAt(i).setBackgroundResource(R.color.lightgrey);
					}
				}
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
				loadSymptom((String) bodyList.get(index).get("caption"));
            }
		});
	}

	/**
	 * 处理症状
	 */
	private void dealSymptom() {
		ListView listview = (ListView) this.findViewById(R.id.selfdiagnostic_list_symptom);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, symptomList, R.layout.listview_symptom,
			new String[]{"caption"}, 
	        new int[]{R.id.symptom_label_caption});
		listItemAdapter.setViewBinder(new ViewBinder() {
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
		listview.setAdapter(listItemAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				Intent intent = new Intent(SelfDiagnosticActivity.this, DiseaseListActivity.class);
				intent.putExtra("symptom", (String) symptomList.get(index).get("caption"));
				SelfDiagnosticActivity.this.startActivity(intent);
            }
		});
	}
	
	/**
	 * 加载部位列表
	 */
	public void loadBody() {
		Host.doCommand("loadBody", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(SelfDiagnosticActivity.this, "加载身体部位列表失败", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(SelfDiagnosticActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				bodyList.clear();
				for(IJSON item : result) {
					JSONString newJSONString = (JSONString) item;
					String caption = newJSONString.getValue();
					HashMap<String, Object> bodyMap = new HashMap<String, Object>();
					bodyMap.put("caption", caption);
					bodyList.add(bodyMap);
				}
				ListView listview = (ListView) SelfDiagnosticActivity.this.findViewById(R.id.selfdiagnostic_list_body);
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 加载症状列表
	 * 
	 * @param bodyName 部位名称
	 */
	public void loadSymptom(String bodyName) {
		String body = null;
		try {
			body = URLEncoder.encode(bodyName, "UTF-8");
		}
		catch (UnsupportedEncodingException e) { }
		Host.doCommand("loadSymptom", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(SelfDiagnosticActivity.this, "加载症状列表失败", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(SelfDiagnosticActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				symptomList.clear();
				for(IJSON item : result) {
					JSONString newJSONString = (JSONString) item;
					String caption = newJSONString.getValue();
					HashMap<String, Object> symptomMap = new HashMap<String, Object>();
					symptomMap.put("caption", caption);
					symptomList.add(symptomMap);
				}
				ListView listview = (ListView) SelfDiagnosticActivity.this.findViewById(R.id.selfdiagnostic_list_symptom);
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
			}
		}, body);
	}
}
