package com.qcast.tower.form;

import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.sensor.SoundRecorder;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import java.io.File;

import com.qcast.tower.R;
import com.qcast.tower.framework.Storage;

import android.os.Bundle;
import android.widget.ImageView;

/**
 * 仿Siri的对话界面
 */
@ResourceView(id = R.layout.activity_siri)
public class SiriActivity extends ActivityEx {
	@ResourceView(id = R.id.siri_image_microphone)
	public ImageView imgMicrophone;
	/**
	 * 录音器
	 */
	private SoundRecorder recorder = null;
	/**
	 * 振幅哨兵
	 */
	private int sentry = -1;

	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Controller.doDelay(new Runnable() {
				@Override
				public void run() {
					if(null == recorder) {
						return;
					}
					if(sentry > 5 * 2) {
						File file = recorder.stop();
						recorder = null;
						// TODO:
						SiriActivity.this.finish();
						return;
					}
					int amplitude = recorder.getAmplitude();
					if(amplitude < 2) {
						imgMicrophone.setImageResource(R.drawable.icon_microphone);
						sentry++;
					}
					else if(amplitude < 5) {
						imgMicrophone.setImageResource(R.drawable.icon_microphone_1);
						sentry = 0;
					}
					else if(amplitude < 8) {
						imgMicrophone.setImageResource(R.drawable.icon_microphone_2);
						sentry = 0;
					}
					else {
						imgMicrophone.setImageResource(R.drawable.icon_microphone_3);
						sentry = 0;
					}
					Controller.doDelay(this, 200);
				}
			}, 1000);
			recorder = new SoundRecorder(Storage.VOICE_ROOT);
			recorder.start(this);
	}

	/**
	 * 界面销毁
	 */
	@Override
    protected void onDestroy() {
		super.onDestroy();
		
		if(null != recorder) {
			recorder.discard();
		}
		recorder = null;
	}
}
