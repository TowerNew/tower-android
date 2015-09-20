package com.qcast.tower.form;

import com.qcast.tower.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
		ImageButton button = (ImageButton) this.getActivity().findViewById(R.id.home_button_inquiry);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), InquiryDoctorActivity.class);
				intent.putExtra("docLevel", 2);
				HomeActivity.this.startActivity(intent);
			}
		});
		//
		dealTime();
	}
}
