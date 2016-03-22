package com.qcast.tower.view.form;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 话题设置页
 */
@ResourceView(id = R.layout.activity_topic)
public class TopicActivity extends ActivityEx {
	/**
	 * 控件
	 */
	@ResourceView(id = R.id.topic_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.topic_label_finish)
	public TextView btnFinish;
	@ResourceView(id = R.id.topic_text_content)
	public EditText txtContent;
	@ResourceView(id = R.id.topic_label_description)
	public TextView labDescription;
	/**
	 * 数据
	 */
	private String doctorId = null;
	private String localId = null;
	private String groupId = null;
	private String remoteId = null;
	private String remoteName = null;
	private String localPhoto = null;
	private String remotePhoto = null;
	
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		prepare();
	}

	/**
	 * 准备视图
	 */
	public void prepare() {
		prepareParameter();
		prepareClose();
		prepareFinish();
		load();
	}

    /**
     * 准备数据
     */
    private void prepareParameter() {
    	doctorId = getIntent().getStringExtra("doctorId");
    	localId = getIntent().getStringExtra("localId");
    	groupId = getIntent().getStringExtra("groupId");
    	remoteId = getIntent().getStringExtra("remoteId");
    	remoteName = getIntent().getStringExtra("remoteName");
    	localPhoto = getIntent().getStringExtra("localPhoto");
    	remotePhoto = getIntent().getStringExtra("remotePhoto");
    }

    /**
     * 准备关闭按钮
     */
    private void prepareClose() {
    	btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TopicActivity.this.finish();
			}
        });
    }
    
    /**
     * 准备完成按钮
     */
    private void prepareFinish() {
    	btnFinish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Networking.doCommand("talk", new JSONResponse(TopicActivity.this) {
        			@Override
        			public void onFinished(JSONVisitor content) {
        				if(code() < 0) {
        					return;
        				}
        				if(content.getInteger("code") <= 0) {
        					return;
        				}
        				content = content.getVisitor("data");
        				if(null == content) {
        					return;
        				}
        				groupId = content.getString("groupId");
        				remoteId = content.getString("doctorImUsername");
        				Me.instance.doChat(TopicActivity.this, groupId, remoteId);
                        TopicActivity.this.finish();
        			}
                }, txtContent.getText().toString(), doctorId, Logic.token);
			}
        });
    }

    /**
     * 加载
     */
    public void load() {
    	Networking.doCommand("loadAvailableTopic", new JSONResponse(TopicActivity.this) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(code() < 0) {
					return;
				}
				if(content.getInteger("code") <= 0) {
					return;
				}
				content = content.getVisitor("data");
				if(null == content) {
					return;
				}
				groupId = content.getString("groupId");
				remoteId = content.getString("doctorImUsername");
				Me.instance.doChat(TopicActivity.this, groupId, remoteId);
                TopicActivity.this.finish();
			}
    	}, doctorId, Logic.token);
    }
}
