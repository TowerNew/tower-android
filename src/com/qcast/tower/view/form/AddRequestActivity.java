package com.qcast.tower.view.form;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.structure.Notify;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;


/**
 * 好友添加请求
 */
@ResourceView(id = R.layout.activity_addrequest)
public class AddRequestActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.addrequest_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.addrequest_label_name)
	public TextView labName;
	@ResourceView(id = R.id.addrequest_label_description)
	public TextView labDescription;
	@ResourceView(id = R.id.addrequest_button_refuse)
	public Button btnRefuse;
	@ResourceView(id = R.id.addrequest_button_accept)
	public Button btnAccept;

    private Notify model = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        Bundle bundle = this.getIntent().getExtras();
        model = (Notify) bundle.get("message");
        if(null == model){
            finish();
            return;
        }
        if(model.hasRead) {
        	btnRefuse.setText("已处理");
        	btnRefuse.setEnabled(false);
        	btnAccept.setVisibility(View.GONE);
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRequestActivity.this.finish();
            }
        });
        labName.setText(model.name + "(" + model.phone + ")");
        labDescription.setText("请求添加您为：" + model.relation);
        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(!model.hasRead) {
            		Host.doCommand("readMessage", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) { }
                    }, Me.instance.token, model.id);
            	}
                Host.doCommand("responseFamily", new JSONResponse(AddRequestActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content) {
							return;
						}
						AddRequestActivity.this.finish();
					}
                }, Me.instance.token, false, model.requestId);
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(!model.hasRead) {
            		Host.doCommand("readMessage", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) { }
                    }, Me.instance.token, model.id);
            	}
                Host.doCommand("responseFamily", new JSONResponse(AddRequestActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content) {
							return;
						}
						Me.instance.refreshMember(AddRequestActivity.this, new IEventable<Boolean>() {
							@Override
							public void on(Boolean data) {
								AddRequestActivity.this.finish();
							}
						});
					}
                }, Me.instance.token, true, model.requestId);
            }
        });
    }
}
