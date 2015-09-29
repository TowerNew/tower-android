package com.qcast.tower.form;

import com.qcast.tower.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 预约页
 */
public class ReserveActivity extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_reserve, container, true);
	}

	@Override
	public void onStart() {
		super.onStart();
		prepare();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		// 处理选择套餐
		View view = (View) this.getActivity().findViewById(R.id.reserve_layout_examination);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReserveActivity.this.getActivity(), ExaminationActivity.class);
				ReserveActivity.this.getActivity().startActivity(intent);
			}
		});
		view = (View) this.getActivity().findViewById(R.id.reserve_layout_seedoctor);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReserveActivity.this.getActivity(), InquiryDoctorActivity.class);
				intent.putExtra("services", "reserve");
				intent.putExtra("docLevel", 2);
				ReserveActivity.this.getActivity().startActivity(intent);
			}
		});
	}
}
