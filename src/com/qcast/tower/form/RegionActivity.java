package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 城市选择页
 */
public class RegionActivity extends Activity {
	/**
	 * 放弃修改
	 */
	public final static int RESULT_CANCEL = 0;
	/**
	 * 已更新
	 */
	public final static int RESULT_UPDATED = 1;
	/**
	 * 区域省份
	 */
	public final static int REGION_CITYID = 1;
	/**
	 * 区域城市
	 */
	public final static int REGION_REGION = 2;
	
	/**
	 * 区域级别
	 */
	public final static String REGION_LEVEL = "region_level";

	/**
	 * 省份/城市 ID
	 */
	public final static String STRING_CITY_ID = "region_cityId";

	/**
	 * 区域 ID
	 */
	public final static String STRING_REGION_ID = "region_regionId";

	/**
	 * 当前区域级别
	 */
	protected int regionLevel = REGION_CITYID;
	/**
	 * 当前省份ID
	 */
	protected int currentCityId = 0;
	/**
	 * 当前省份名字
	 */
	protected String currentCityName = null;
	/**
	 * 当前城市ID
	 */
	protected int currentRegionId = 0;
	/**
	 * 数据列表
	 */
	protected ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Angel", "CityActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_region);

		Intent intent = getIntent();
		if(intent != null) {
			regionLevel = intent.getIntExtra(REGION_LEVEL, REGION_CITYID);
			currentCityId = intent.getIntExtra(STRING_CITY_ID, 0);
		}

		// 参数处理
		currentRegionId = this.getIntent().getIntExtra("regionId", 0);
		// 界面处理
		prepare();
		// 加载数据
		load();
	}

	/**
	 * 处理列表
	 */
	private void prepare() {
		ImageButton button = (ImageButton) this.findViewById(R.id.region_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(REGION_CITYID == regionLevel) {
					Intent intent = new Intent();
					RegionActivity.this.setResult(RESULT_CANCEL, intent);
					RegionActivity.this.finish();
				} else {
					regionLevel = REGION_CITYID;
					load();
				}
			}
		});
		//
		ListView listview = (ListView) this.findViewById(R.id.region_list);
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
				if(REGION_CITYID == regionLevel) {
					regionLevel = REGION_REGION;
					currentCityId = (Integer) dataList.get(index).get("id");
					currentCityName = (String) dataList.get(index).get("name");
					load();
				}
				else if(REGION_REGION == regionLevel) {
					currentRegionId = (Integer) dataList.get(index).get("id");
					Intent intent = new Intent();
					intent.putExtra("cityId", currentCityId);
					intent.putExtra("cityName", currentCityName);
					intent.putExtra("regionId", currentRegionId);
					intent.putExtra("regionName", (String) dataList.get(index).get("name"));
					RegionActivity.this.setResult(RESULT_UPDATED, intent);
					RegionActivity.this.finish();
				}
            }
		});
	}

	/**
	 * 加载数据
	 */
	public void load() {
		if(REGION_CITYID == regionLevel) {
			TextView labTitle = (TextView) this.findViewById(R.id.region_label_title);
			labTitle.setText("城市");
			Host.doCommand("citylist", new CommonResponse<String>() {
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
					ListView listview = (ListView) findViewById(R.id.region_list);
					SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
					adapter.notifyDataSetChanged();
				}
			});
		}
		else if(REGION_REGION == regionLevel) {
			TextView labTitle = (TextView) this.findViewById(R.id.region_label_title);
			labTitle.setText("小区");
			Host.doCommand("regionlist", new CommonResponse<String>() {
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
					ListView listview = (ListView) findViewById(R.id.region_list);
					SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
					adapter.notifyDataSetChanged();
				}
			}, currentCityId);
		}
	}
}
