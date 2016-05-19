package com.qcast.tower.view.form;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;

/**
 * 选择私人医生
 */
@ResourceView(id = R.layout.activity_selectdoctor)
public class SelectDoctorActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.selectdoctor_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.selectdoctor_list)
	public ListView listDoctor;

	/**
	 * 医生面板列表
	 */
	private LinkedList<HashMap<String, Object>> doctorList = new LinkedList<HashMap<String, Object>>();
	/**
	 * 当前被选中的索引
	 */
	private int current = -1;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null == Me.instance) {
			return;
		}
		if(null == Profile.instance().region) {
			this.finish();
			return;
		}
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectDoctorActivity.this.finish();
			}
		});
		SimpleAdapter listItemAdapter = new SimpleAdapter(SelectDoctorActivity.this, doctorList, R.layout.listitem_selectdoctor,
				new String[]{"name", "photo", "department", "title"}, 
				new int[]{R.id.selectdoctor_label_name, R.id.selectdoctor_image_photo, R.id.selectdoctor_label_department, R.id.selectdoctor_label_title});
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
		listDoctor.setAdapter(listItemAdapter);
		listDoctor.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long id) {
				listDoctor.setEnabled(false);
				HashMap<String, Object> map = null;
				if(current >= 0) {
					map = doctorList.get(current);
					/*map.put("status", GraphicsHelper.decodeResource(SelectDoctorActivity.this, R.drawable.icon_unselected));*/
				}
				current = index;
				map = doctorList.get(index);				
				Intent intent= new Intent(SelectDoctorActivity.this,DoctorDetailActivity.class);
				intent.putExtra("name", map.get("id").toString());
			    SelectDoctorActivity.this.startActivity(intent);
				SelectDoctorActivity.this.finish();
				/*map.put("status", GraphicsHelper.decodeResource(SelectDoctorActivity.this, R.drawable.icon_selected));*/
				//
			/*	Networking.doCommand("selectDoctor", new JSONResponse(SelectDoctorActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content || content.getInteger("code", -1) < 0) {
							return;
						}
						Me.instance.refreshDoctor(SelectDoctorActivity.this, new IEventable<Boolean>() {
							@Override
							public void on(Boolean data) {
								Me.instance.doChat(SelectDoctorActivity.this, null, Me.instance.doctor.imId);
								SelectDoctorActivity.this.finish();
							}
						});
					}
				}, map.get("id"), Me.instance.token);*/
				
				((SimpleAdapter) listDoctor.getAdapter()).notifyDataSetChanged();
			}
		});
		//
		Networking.doCommand("doctorlist", new JSONResponse(SelectDoctorActivity.this) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content || content.getInteger("code", -1) < 0) {
					return;
				}
				listDoctor.setEnabled(true);
				doctorList.clear();
				Bitmap bitmap = GraphicsHelper.decodeResource(SelectDoctorActivity.this, R.drawable.icon_unselected);
				int i = 0;
				if(null == content.getVisitors("data")) {
					return;
				}
				Bitmap doctorDefault = GraphicsHelper.decodeResource(SelectDoctorActivity.this, R.drawable.icon_doctor_default);
				for(JSONVisitor doctor : content.getVisitors("data")) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", doctor.getString("userGlobalId"));
					map.put("name", doctor.getString("name"));
					map.put("photo", doctorDefault);
					map.put("department", doctor.getString("department"));
					map.put("title", doctor.getString("title"));
					map.put("imId", doctor.getString("imUsername"));
					map.put("status", bitmap);
					if(null != Me.instance && null != Me.instance.doctor) {
						if(Me.instance.doctor.id.equals(doctor.getString("userGlobalId"))) {
							current = i;
							/*map.put("status", GraphicsHelper.decodeResource(SelectDoctorActivity.this, R.drawable.icon_selected));*/
						}
					}
					doctorList.add(map);
					// 加载图片
		            Networking.doImage("image", new ImageResponse(doctor.getString("photo"), i, 100, 100) {
						@Override
						public void onFinished(Bitmap content) {
							HashMap<String, Object> map = doctorList.get((Integer) tag);
							map.put("photo", content);
							SimpleAdapter adapter = (SimpleAdapter) listDoctor.getAdapter();
							adapter.notifyDataSetChanged();
						}
		            }, doctor.getString("photo"));
					i++;
				}
				((SimpleAdapter) listDoctor.getAdapter()).notifyDataSetChanged();
			}
		}, 1, 0, "privateDoctor", Profile.instance().region.id);
	}
}
