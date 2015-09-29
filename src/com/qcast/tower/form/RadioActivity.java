package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.qcast.tower.logic.Logic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * 单选对话框
 */
public class RadioActivity extends Activity {
	/**
	 * 放弃修改
	 */
	public final static int RESULT_CANCEL = 0;
	/**
	 * 已更新
	 */
	public final static int RESULT_UPDATED = 1;


	/**
	 * 标题
	 */
	protected String title = "";
	/**
	 * 数据列表
	 */
	protected ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 当前选择索引
	 */
	protected int current = -1;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "RadioActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_radio);
		// 加载数据
		load();
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
		ImageButton button = (ImageButton) this.findViewById(R.id.radio_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("index", current);
				RadioActivity.this.setResult(RESULT_CANCEL, intent);
				RadioActivity.this.finish();
			}
		});
		TextView text = (TextView) this.findViewById(R.id.radio_text_title);
		text.setText(title);
	}
	
	/**
	 * 处理列表
	 */
	private void dealList() {
		ListView listview = (ListView) this.findViewById(R.id.radio_list);
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataList, R.layout.listview_radio, new String[]{"caption", "status"}, new int[]{R.id.radiolist_label_caption, R.id.radiolist_image_status});
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
				current = index;
				Intent intent = new Intent();
				intent.putExtra("index", current);
				RadioActivity.this.setResult(RESULT_UPDATED, intent);
				RadioActivity.this.finish();
            }
		});
	}

	/**
	 * 加载数据
	 */
	public void load() {
		title = this.getIntent().getStringExtra("title");
		if(null == title) {
			title = "";
		}
		String[] items = this.getIntent().getStringArrayExtra("items");
		if(null == items) {
			return;
		}
		current = this.getIntent().getIntExtra("index", current);
		int i = 0;
		for(String item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("caption", item);
			if(current == i) {
				map.put("status", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.yes));
			}
			i++;
			dataList.add(map);
		}
	}
}
