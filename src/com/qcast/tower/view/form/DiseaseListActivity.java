package com.qcast.tower.view.form;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pretty.general.view.form.BrowserActivity;
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
public class DiseaseListActivity extends Activity {
	/**
	 * 当前部位列表
	 */
	protected ArrayList<HashMap<String, Object>> diseaseList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 症状名称
	 */
	protected String symptom = null;
	
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "DiseaseListActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_diseaselist);
		//
		symptom = this.getIntent().getStringExtra("symptom");
		try {
			symptom = URLEncoder.encode(symptom, "UTF-8");
		}
		catch(Exception ex) {}
		if(null == symptom) {
			symptom = "";
		}
		// 界面处理
		prepare();
		//
		loadDiseaseList();
	}
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		ImageButton button = (ImageButton) this.findViewById(R.id.diseaselist_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DiseaseListActivity.this.finish();
			}
		});
		dealDisease();
	}

	/**
	 * 处理疾病
	 */
	private void dealDisease() {
		ListView listview = (ListView) this.findViewById(R.id.diseaselist_list);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, diseaseList, R.layout.listview_disease,
			new String[]{"caption", "description"}, 
	        new int[]{R.id.diseaselist_listview_caption, R.id.diseaselist_listview_description});
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
				Intent intent = new Intent(DiseaseListActivity.this, BrowserActivity.class);
				String caption = (String) diseaseList.get(index).get("caption");
				try {
					caption = URLEncoder.encode(caption, "UTF-8");
				}
				catch(Exception ex) { }
				intent.putExtra("url", Host.fetchURL("searchDisease", caption));
				DiseaseListActivity.this.startActivity(intent);
            }
		});
	}

	/**
	 * 加载疾病列表
	 */
	public void loadDiseaseList() {
		Host.doCommand("loadDisease", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(DiseaseListActivity.this, "加载相关疾病列表失败", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(DiseaseListActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				diseaseList.clear();
				for(IJSON item : result) {
					JSONObject newJSONObject = (JSONObject) item;
					String caption = ((JSONString) newJSONObject.get("caption")).getValue();
					String description = ((JSONString) newJSONObject.get("description")).getValue();
					HashMap<String, Object> bodyMap = new HashMap<String, Object>();
					bodyMap.put("caption", caption);
					bodyMap.put("description", description);
					diseaseList.add(bodyMap);
				}
				ListView listview = (ListView) DiseaseListActivity.this.findViewById(R.id.diseaselist_list);
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
			}
		}, symptom);
	}
}
