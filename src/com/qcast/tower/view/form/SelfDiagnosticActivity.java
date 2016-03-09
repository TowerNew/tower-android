package com.qcast.tower.view.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.general.view.form.BrowserActivity;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.type.List;
import com.slfuture.carrie.base.type.Set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 自我诊断页
 */
@ResourceView(id = R.layout.activity_selfdiagnostic)
public class SelfDiagnosticActivity extends ActivityEx {
	@ResourceView(id = R.id.selfdiagnostic_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.selfdiagnostic_button_graph)
	public Button btnGraph;
	@ResourceView(id = R.id.selfdiagnostic_button_list)
	public Button btnList;
	@ResourceView(id = R.id.selfdiagnostic_layout_graph)
	public View viewGraph;
	@ResourceView(id = R.id.selfdiagnostic_layout_list)
	public View viewList;
	@ResourceView(id = R.id.selfdiagnostic_list_body)
	public ListView listBody;
	@ResourceView(id = R.id.selfdiagnostic_list_symptom)
	public ListView listSymptom;
	@ResourceView(id = R.id.selfdiagnostic_button_gender)
	public Button btnGender;
	@ResourceView(id = R.id.selfdiagnostic_button_towards)
	public Button btnTowards;
	@ResourceView(id = R.id.selfdiagnostic_layout_graph)
	public android.widget.RelativeLayout container;


	/**
	 * 当前部位列表
	 */
	protected ArrayList<HashMap<String, Object>> bodyList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 当前症状列表
	 */
	protected ArrayList<HashMap<String, Object>> symptomList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 当前肢体ID
	 */
	private int currentBodyId = 1;
	/**
	 * 当前性别, 1:男, 2:女
	 */
	private int currentGender = 1;
	/**
	 * 当前朝向, true:正面, false:反面
	 */
	private boolean currentTowards = true;
	/**
	 * 当前显示的部位图层
	 */
	private List<ImageView> currentImages = new List<ImageView>();


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
		loadBody();
	}

	/**
	 * 选择回调
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//
		if(resultCode == Activity.RESULT_CANCELED) {
			return;
		}
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SelfDiagnosticActivity.this.finish();
			}
		});
		btnGraph.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnGraph.setBackgroundColor(SelfDiagnosticActivity.this.getResources().getColor(R.color.white));
				btnList.setBackgroundColor(SelfDiagnosticActivity.this.getResources().getColor(R.color.grey_bg));
				viewGraph.setVisibility(View.VISIBLE);
				viewList.setVisibility(View.GONE);
				loadGraph();
			}
		});
		btnList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnGraph.setBackgroundColor(SelfDiagnosticActivity.this.getResources().getColor(R.color.grey_bg));
				btnList.setBackgroundColor(SelfDiagnosticActivity.this.getResources().getColor(R.color.white));
				viewGraph.setVisibility(View.GONE);
				viewList.setVisibility(View.VISIBLE);
			}
		});
		btnGender.getBackground().setAlpha(100);
		btnGender.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentGender = 3 - currentGender;
				loadGraph();
			}
		});
		btnTowards.getBackground().setAlpha(100);
		btnTowards.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentTowards = ! currentTowards;
				loadGraph();
			}
		});
		prepareBody();
		prepareSymptom();
		//
		selectBody(1);
		loadGraph();
	}

	/**
	 * 处理部位
	 */
	private void prepareBody() {
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, bodyList, R.layout.listitem_body,
			new String[]{"icon", "name"}, 
	        new int[]{R.id.listitem_body_image_icon, R.id.listitem_body_label_name});
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
		listBody.setAdapter(listItemAdapter);
		listBody.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				selectBody((Integer) bodyList.get(index).get("id"));
            }
		});
	}

	/**
	 * 处理症状
	 */
	private void prepareSymptom() {
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, symptomList, R.layout.listitem_symptom,
			new String[]{"name"}, 
	        new int[]{R.id.listitem_symptom_label_name});
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
		listSymptom.setAdapter(listItemAdapter);
		listSymptom.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				String url = Host.fetchURL("loadDisease", symptomList.get(index).get("uuid"));
				Intent intent = new Intent(SelfDiagnosticActivity.this, BrowserActivity.class);
				intent.putExtra("url", url);
				SelfDiagnosticActivity.this.startActivity(intent);
            }
		});
	}

	/**
	 * 查看指定部位
	 * 
	 * @param bodyId 部位ID
	 */
	public void selectBody(int bodyId) {
		currentBodyId = bodyId;
		loadBody();
		loadSymptom();
	}

	/**
	 * 加载器官列表
	 */
	public void loadBody() {
		String text = Host.fetchMock("loadBody");
		if(null == text) {
			return;
		}
		bodyList.clear();
		for(JSONVisitor item : (new JSONVisitor(JSONObject.convert(text)).getVisitors("data"))) {
			HashMap<String, Object> bodyMap = new HashMap<String, Object>();
			bodyMap.put("id", item.getInteger("id", 0));
			bodyMap.put("name", item.getString("name"));
			try {
				// 未选中图标
				String imageString = item.getString("icon");
				int imageResourceId = R.drawable.class.getField(imageString).getInt(null);
				bodyMap.put("icon", GraphicsHelper.decodeResource(this, imageResourceId));
				// 选中图标
				String imageSelectedString = item.getString("icon_selected");
				int imageSelectedResourceId = R.drawable.class.getField(imageSelectedString).getInt(null);
				bodyMap.put("icon_selected", GraphicsHelper.decodeResource(this, imageSelectedResourceId));
				// 设置正确图标
				if(currentBodyId == item.getInteger("id", 0)) {
					bodyMap.put("icon", bodyMap.get("icon_selected"));
				}
				else {
					bodyMap.put("icon", bodyMap.get("icon"));
				}
			}
			catch (Exception e) { }
			Set<Integer> set = new Set<Integer>();
			if(null != item.getVisitors("images")) {
				for(JSONVisitor json : item.getVisitors("images")) {
					set.add(json.toInteger());
				}
			}
			bodyMap.put("images", set);
			bodyList.add(bodyMap);
		}
		((SimpleAdapter) listBody.getAdapter()).notifyDataSetChanged();
	}

	/**
	 * 加载症状列表
	 */
	public void loadSymptom() {
		Host.doCommand("loadSymptoms", new JSONResponse(SelfDiagnosticActivity.this) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content || content.getInteger("code", 0) <= 0) {
					return;
				}
				symptomList.clear();
				for(JSONVisitor item : content.getVisitors("data")) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", item.getInteger("id", 0));
					map.put("name", item.getString("name"));
					map.put("uuid", item.getString("uuid"));
					symptomList.add(map);
				}
				((SimpleAdapter) listSymptom.getAdapter()).notifyDataSetChanged();
			}
		}, currentBodyId, currentGender);
	}

	/**
	 * 加载部位视图
	 */
	public void loadGraph() {
		if(1 == currentGender) {
			btnGender.setText("性别：男");
		}
		else {
			btnGender.setText("性别：女");
		}
		if(currentTowards) {
			btnTowards.setText("朝向：正面");
		}
		else {
			btnTowards.setText("朝向：背面");
		}
		for(ImageView view : currentImages) {
			container.removeView(view);
		}
		currentImages.clear();
		for(HashMap<String, Object> map : bodyList) {
			if(null == map.get("images")) {
				continue;
			}
			Set<Integer> set = (Set<Integer>) map.get("images");
			int sentry = 0;
			if(1 == currentGender) {
				if(currentTowards) {
					sentry = 1;
				}
				else {
					sentry = 2;
				}
			}
			else {
				if(currentTowards) {
					sentry = 3;
				}
				else {
					sentry = 4;
				}
			}
			if(set.contains(sentry)) {
				ImageView image = new ImageView(SelfDiagnosticActivity.this);
				LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				image.setLayoutParams(params);
				image.setScaleType(ScaleType.CENTER_INSIDE);
				try {
					image.setImageResource(R.drawable.class.getField("image_body_" + map.get("id") + "_" + sentry).getInt(null));
				}
				catch (Exception e) {
					continue;
				}
				image.setTag(map.get("id"));
				container.addView(image);
				currentImages.add(image);
				image.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent event) {
						ImageView image = (ImageView) view;
						Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
						int x = (int)(event.getX() * bitmap.getWidth() / image.getWidth());
						int y = (int)(event.getY() * bitmap.getHeight() / image.getHeight());
						if(0 != bitmap.getPixel(x, y)) {
							Integer id = (Integer) view.getTag();
							btnGraph.setBackgroundColor(SelfDiagnosticActivity.this.getResources().getColor(R.color.grey_bg));
							btnList.setBackgroundColor(SelfDiagnosticActivity.this.getResources().getColor(R.color.white));
							viewGraph.setVisibility(View.GONE);
							viewList.setVisibility(View.VISIBLE);
							selectBody(id);
							return true;
						}
		                return false;
					}
		        });
			}
		}
		btnGender.bringToFront();
		btnTowards.bringToFront();
	}
}
