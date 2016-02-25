package com.qcast.tower.view.form;

import com.qcast.tower.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InquiryActivity extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_inquiry, container, true);

	}

	@Override
	public void onStart() {
		super.onStart();
		Button inquiry_bell_btn = (Button) this.getActivity().findViewById(R.id.inquiry_bell_btn);
		inquiry_bell_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(InquiryActivity.this.getActivity(), MyMessagesActivity.class);
				InquiryActivity.this.getActivity().startActivity(intent);
			}
		});
	}
	

	@Override
	public void onStop() {
		super.onStop();
	}
}
