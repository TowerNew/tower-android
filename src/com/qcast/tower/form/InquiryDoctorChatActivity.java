package com.qcast.tower.form;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.adapter.ChatMsgViewAdapter;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.core.IResponse;
import com.qcast.tower.model.ChatMsgEntity;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by zhengningchuan on 15/9/5.
 */
public class InquiryDoctorChatActivity extends Activity implements View.OnClickListener {

    private Button mBtnSend;// 发送btn
    private Button mBtnBack;// 返回btn
    private EditText mEditTextContent;
    private ListView mListView;
    private final static int COUNT = 12;// 初始化数组总数
    private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
    private ArrayList<ChatMsgEntity> mDataArrays = null;// 消息对象数组
    private Handler chatHandler;
    private String chatContent;
    private Boolean refreshFlag;
    private Boolean breakFlag;
    private String contString;
    private String docId;
    private String channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        docId = this.getIntent().getStringExtra("docId");
        if(TextUtils.isEmpty(docId)){
            this.finish();
        }
        setContentView(R.layout.activity_inquiry_doctor_chat);
        initView();// 初始化view
        mDataArrays = new ArrayList<ChatMsgEntity>();
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
        chatHandler=new Handler();
        requestChannel();
        refreshMessage();
    }

    private void requestChannel() {
        Host.doCommand("talk", new CommonResponse<String>() {
            @Override
            public void onFinished(String content) {
                if (IResponse.CODE_SUCCESS != code()) {
                    Toast.makeText(InquiryDoctorChatActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                }
                if(null == content) {
                	return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(InquiryDoctorChatActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                channel =((JSONString)(resultObject.get("data"))).getValue();

            }
        }, docId, Logic.token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFlag = true;
        breakFlag = false;
    }

    /**
     * 初始化view
     */
    public void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:// 发送按钮点击事件
                send();
                break;
            case R.id.btn_back:// 返回按钮点击事件
                finish();// 结束,实际开发中，可以返回主界面
                break;
        }
    }

    private void refreshMessage() {
        chatHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!breakFlag && !TextUtils.isEmpty(channel)) {
                    Host.doCommand("pull", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) {
                            if (IResponse.CODE_SUCCESS != code()) {
                                Toast.makeText(InquiryDoctorChatActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                            }
                            JSONObject resultObject = JSONObject.convert(content);
                            if (null == resultObject) {
                                return;
                            }
                            if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                                Toast.makeText(InquiryDoctorChatActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            mDataArrays.clear();
                            com.slfuture.carrie.base.json.JSONArray result = (com.slfuture.carrie.base.json.JSONArray) resultObject.get("data");
                            for (IJSON item : result) {
                                JSONObject newJSONObject = (JSONObject) item;
                                ChatMsgEntity entity = new ChatMsgEntity();
                                entity.setMessage(((JSONString) newJSONObject.get("content")).getValue());
                                String userID = ((JSONString) newJSONObject.get("speaker")).getValue();
                                entity.setSpeakeId(userID);
                                String time = ((JSONString) newJSONObject.get("time")).getValue();
                                entity.setTime(time);
                                if (userID.equals("docId")) {
                                    entity.setMsgType(true);
                                } else {
                                    entity.setMsgType(false);
                                }
                                mDataArrays.add(entity);

                            }
                            if (refreshFlag) {
                                mAdapter.notifyDataSetChanged();
                                mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
                            }
                            InquiryDoctorChatActivity.this.refreshMessage();
                        }

                    }, channel, Logic.token);
                }
            }
        }, 20000);

    }


    /**
     * 发送消息
     */
    private void send() {
        contString = mEditTextContent.getText().toString();
        if (contString.length() > 0) {
            Host.doCommand("push", new CommonResponse<String>() {
                @Override
                public void onFinished(String content) {
                    if (IResponse.CODE_SUCCESS != code()) {
                        Toast.makeText(InquiryDoctorChatActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                    }
                    JSONObject resultObject = JSONObject.convert(content);
                    if (null == resultObject) {
                        return;
                    }
                    if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                        Toast.makeText(InquiryDoctorChatActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    ChatMsgEntity entity = new ChatMsgEntity();
                    entity.setMessage(contString);
                    entity.setMsgType(false);
                    entity.setSpeakeId(Logic.token);
                    mDataArrays.add(entity);
                    if (refreshFlag) {
                        mAdapter.notifyDataSetChanged();
                        mEditTextContent.setText("");// 清空编辑框数据
                        mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
                    }
                }

            }, channel, contString, Logic.token);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        refreshFlag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        breakFlag = true;
        chatHandler=null;
    }
}
