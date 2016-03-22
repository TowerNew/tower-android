package com.qcast.tower.view.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.slfuture.pluto.communication.Networking;
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
 * 健康管理
 */
public class PrescriptionActivity extends Activity {
	/**
	 * 当前部位列表
	 */
	protected ArrayList<HashMap<String, Object>> prescriptionList = new ArrayList<HashMap<String, Object>>();
	
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "PrescriptionActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_prescription);
		// 界面处理
		prepare();
		//
		load();
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
		ImageButton button = (ImageButton) this.findViewById(R.id.prescription_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PrescriptionActivity.this.finish();
			}
		});
	}

	/**
	 * 处理列表
	 */
	private void dealList() {
		ListView listview = (ListView) this.findViewById(R.id.prescription_list_prescriptions);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, prescriptionList, R.layout.listview_radio,
			new String[]{"caption"}, 
	        new int[]{R.id.radiolist_label_caption});
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
				HashMap<String, Object> map = prescriptionList.get(index);
				openWeb((String) map.get("url"));
			}
		});
	}

	/**
	 * 加载数据
	 */
	private void load() {
		Networking.doCommand("prescriptionlist", new CommonResponse<String>() {
            @Override
            public void onFinished(String content) {
                if (Response.CODE_SUCCESS != code()) {
                    Toast.makeText(PrescriptionActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(PrescriptionActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray result = (JSONArray) resultObject.get("data");
                prescriptionList.clear();
                for(IJSON prescriptionJSON : result) {
                	String caption = ((JSONString) ((JSONObject) prescriptionJSON).get("caption")).getValue();
                	String url = ((JSONString) ((JSONObject) prescriptionJSON).get("url")).getValue();
                	HashMap<String, Object> map = new HashMap<String, Object>();
                	map.put("caption", caption);
                	map.put("url", url);
                	prescriptionList.add(map);
                }
				ListView listview = (ListView) PrescriptionActivity.this.findViewById(R.id.prescription_list_prescriptions);
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
            }
		}, Logic.token);
	}
	
	/**
	 * 打开浏览器
	 * 
	 * @param url 地址
	 */
	private void openWeb(String url) {
		Intent intent = new Intent(PrescriptionActivity.this, BrowserActivity.class);
		intent.putExtra("url", url);
		PrescriptionActivity.this.startActivity(intent);
	}
}
