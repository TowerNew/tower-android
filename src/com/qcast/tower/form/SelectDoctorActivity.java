package com.qcast.tower.form;

import java.util.HashMap;
import java.util.LinkedList;

import android.os.Bundle;
import android.widget.ListView;

import com.qcast.tower.R;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 选择私人医生
 */
@ResourceView(id = R.layout.activity_selectdoctor)
public class SelectDoctorActivity extends ActivityEx {
	@ResourceView(id = R.id.selectdoctor_list)
	public ListView listDoctor;

	/**
	 * 医生面板列表
	 */
	private LinkedList<HashMap<String, Object>> doctorList = new LinkedList<HashMap<String, Object>>();
	
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
