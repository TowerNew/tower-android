package com.qcast.tower.view.form;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;

/**
 * 
 */
public class MyCommentsActivity extends Activity {
    private Button my_commtents_return_btn;
    private Button my_comments_good_btn;
    private Button my_comments_bad_btn;
    private Button my_comments_submit_btn;
    private TextView my_comments_name;
    private EditText my_comments_et;
    private String docId;
    private boolean goodFlag=false;
    private boolean badFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String docName=this.getIntent().getStringExtra("doctorName");
        docId=this.getIntent().getStringExtra("doctorId");
        this.setContentView(R.layout.activity_my_comments);
        my_comments_name= (TextView) this.findViewById(R.id.my_comments_name);
        my_comments_et= (EditText) this.findViewById(R.id.my_comments_et);
        my_commtents_return_btn= (Button) this.findViewById(R.id.my_commtents_return_btn);
        my_comments_good_btn= (Button) this.findViewById(R.id.my_comments_good_btn);
        my_comments_bad_btn= (Button) this.findViewById(R.id.my_comments_bad_btn);
        my_comments_submit_btn= (Button) this.findViewById(R.id.my_comments_submit_btn);
        if(!TextUtils.isEmpty(docName)){
            my_comments_name.setText(docName);
        }
        my_commtents_return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCommentsActivity.this.finish();
            }
        });
        my_comments_bad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badFlag=!badFlag;
                if (badFlag){
                    my_comments_bad_btn.setBackgroundResource(R.drawable.askdoctor_suggest_bad);
                    my_comments_good_btn.setBackgroundResource(R.drawable.askdoctor_good);
                    goodFlag=false;
                }else{
                    my_comments_bad_btn.setBackgroundResource(R.drawable.askdoctor_bad);
                }
            }
        });
        my_comments_good_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodFlag=!goodFlag;
                if (goodFlag){
                    my_comments_good_btn.setBackgroundResource(R.drawable.askdoctor_suggest_good);
                    my_comments_bad_btn.setBackgroundResource(R.drawable.askdoctor_bad);
                    badFlag=false;
                }else{
                    my_comments_good_btn.setBackgroundResource(R.drawable.askdoctor_good);
                }
            }
        });
        my_comments_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments=my_comments_et.getText().toString();
                if(!TextUtils.isEmpty(comments) && (goodFlag || badFlag)) {
                    Host.doCommand("comment", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) {
                            if (Response.CODE_SUCCESS != code()) {
                                Toast.makeText(MyCommentsActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONObject resultObject = JSONObject.convert(content);
                            if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                                Toast.makeText(MyCommentsActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            else {
                                Toast.makeText(MyCommentsActivity.this, "评论成功", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, docId, comments, goodFlag, Logic.token);
                }
            }
        });

    }
}
