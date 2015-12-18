package com.qcast.tower.form;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.adapter.ChatMsgViewAdapter;
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.core.IResponse;
import com.qcast.tower.model.ChatMsgEntity;
import com.slfuture.carrie.base.json.JSONBoolean;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import org.json.JSONException;

import java.text.SimpleDateFormat;
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
    private String topic;
    private Bitmap docBitmap;
    private int messageId = 0;
    public static final int CHAT_REFRESH_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        docId = this.getIntent().getStringExtra("docId");
        topic = this.getIntent().getStringExtra("topic");
        channel = this.getIntent().getStringExtra("channel");
        if(TextUtils.isEmpty(topic)) {
            topic=System.currentTimeMillis()+"";
        }
        byte[] bytes = this.getIntent().getByteArrayExtra("BMP");
        docBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (TextUtils.isEmpty(docId)) {
            this.finish();
        }
        setContentView(R.layout.activity_inquiry_doctor_chat);
        initView();// 初始化view
        mDataArrays = new ArrayList<ChatMsgEntity>();
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays,docBitmap);
        mListView.setAdapter(mAdapter);
        chatHandler = new Handler();
        if(TextUtils.isEmpty(channel)) {
            requestChannel();
        }
        refreshMessage(500);
    }

    private void requestChannel() {
        Host.doCommand("talk", new CommonResponse<String>() {
            @Override
            public void onFinished(String content) {
                if (IResponse.CODE_SUCCESS != code()) {
                    Toast.makeText(InquiryDoctorChatActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                }
                if (null == content) {
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(InquiryDoctorChatActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                IJSON obj = resultObject.get("data");

                if(obj.toString().lastIndexOf(".") > 0) {
                    channel = obj.toString().substring(0, obj.toString().lastIndexOf("."));
                }

            }
        }, topic, docId, Logic.token);
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
            	try {
                    send();
            	}
            	catch(Throwable e) { }
                break;
            case R.id.btn_back:// 返回按钮点击事件
                finish();// 结束,实际开发中，可以返回主界面
                break;
        }
    }

    private void refreshMessage(int delayTime) {
    	try {
    		chatHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!breakFlag && !TextUtils.isEmpty(channel)) {
                        Host.doCommand("pull", new CommonResponse<String>() {
                            @Override
                            public void onFinished(String content) {
                                if(null == content){
                                    return;
                                }
                                if (IResponse.CODE_SUCCESS != code()) {
                                    // Toast.makeText(InquiryDoctorChatActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                                	refreshMessage(CHAT_REFRESH_TIME);
                                	return;
                                }
                                JSONObject resultObject = JSONObject.convert(content);
                                if (null == resultObject) {
                                    return;
                                }
                                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                                    Toast.makeText(InquiryDoctorChatActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(messageId==0){
                                    mDataArrays.clear();
                                }
                                com.slfuture.carrie.base.json.JSONArray result = (com.slfuture.carrie.base.json.JSONArray) resultObject.get("data");
                                if (result != null) {
                                    for (IJSON item : result) {
                                        JSONObject newJSONObject = (JSONObject) item;
                                        ChatMsgEntity entity = new ChatMsgEntity();
                                        IJSON obj = newJSONObject.get("content");
                                        entity.setMessage(((JSONString) newJSONObject.get("content")).getValue());
                                        String userID = ((JSONString) newJSONObject.get("speaker")).getValue();
                                        entity.setSpeakeId(userID);
                                        int mesId = ((JSONNumber) newJSONObject.get("messageId")).intValue();
                                        entity.setMessageId(mesId);
                                        long time = ((JSONNumber) newJSONObject.get("time")).longValue();
                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                                        entity.setTime(sdf.format(time));
                                        if (userID.equals(docId)) {
                                            entity.setMsgType(true);
                                        } else {
                                            entity.setMsgType(false);
                                        }
                                        mDataArrays.add(entity);
                                        if(result.get(result.size()-1)==item) {
                                            messageId = entity.getMessageId();
                                        }
                                    }
                                    if (refreshFlag) {
                                        mAdapter.notifyDataSetChanged();
                                        mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
                                    }
                                    InquiryDoctorChatActivity.this.refreshMessage(CHAT_REFRESH_TIME);
                                } else {
                                    refreshMessage(CHAT_REFRESH_TIME);
                                }
                            }
                        }, channel, Logic.token, messageId);
                    }
                }
            }, delayTime);
    	}
    	catch(Throwable e) { }
    }

    /**
     * 发送消息
     */
    private void send() {
        contString = mEditTextContent.getText().toString();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0) ;
        mEditTextContent.setText("");// 清空编辑框数据
        if (contString.length() > 0) {
            Host.doCommand("push", new CommonResponse<String>() {
                @Override
                public void onFinished(String content) {
                    if(null == content){
                        mEditTextContent.setText(contString);
                        return;
                    }
                    if (IResponse.CODE_SUCCESS != code()) {
                        Toast.makeText(InquiryDoctorChatActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                        mEditTextContent.setText(contString);
                    }
                    JSONObject resultObject = JSONObject.convert(content);
                    if (null == resultObject) {
                        mEditTextContent.setText(contString);
                        return;
                    }
                    if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                        Toast.makeText(InquiryDoctorChatActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                        mEditTextContent.setText(contString);
                        return;
                    }

                    if (!breakFlag && !TextUtils.isEmpty(channel)) {
                        chatHandler.removeCallbacksAndMessages(null);
                        refreshMessage(0);
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
        chatHandler.removeCallbacksAndMessages(null);
        chatHandler = null;
    }
}
