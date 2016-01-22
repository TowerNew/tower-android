package com.qcast.tower.form;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.qcast.tower.model.MyFriendMessageModel;
import com.qcast.tower.model.NotifyModel;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;


import java.util.ArrayList;

/**
 * Created by zhengningchuan on 15/9/20.
 */
public class MyFriendMessageActivity extends Activity {
	private int messageId = 0;
    private ArrayList<MyFriendMessageModel> dataList;
    private MyFriendMessageAdapter adapter;
    private ListView messageListView;
    private Button return_btn;
    private NotifyModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        messageId = this.getIntent().getIntExtra("messageId", 0);
        model= (NotifyModel) bundle.get("myFriendMessage");
        if(model==null){
            finish();
        }
        this.setContentView(R.layout.activity_my_friend_message);
        messageListView = (ListView) this.findViewById(R.id.my_friend_message_list);
        return_btn = (Button) this.findViewById(R.id.my_friend_message_return_btn);
        dataList = new ArrayList<MyFriendMessageModel>();
        adapter = new MyFriendMessageAdapter(this, dataList);
        messageListView.setAdapter(adapter);
        loadData();
        return_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyFriendMessageActivity.this.finish();
            }
        });
    }

    private void loadData() {
        dataList.clear();
        if(model!=null){
            MyFriendMessageModel friendMessageModel =new MyFriendMessageModel();
            friendMessageModel.friendName=model.name;
            friendMessageModel.phoneNum=model.phone;
            friendMessageModel.relation=model.relation;
            friendMessageModel.requestId=model.requestId;
            dataList.add(friendMessageModel);
            adapter.notifyDataSetChanged();
        }
    }

    class MyFriendMessageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<MyFriendMessageModel> data;

        public MyFriendMessageAdapter(Context context, ArrayList<MyFriendMessageModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            if (data != null && data.size() > 0) {
                return data.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if (data != null && data.size() > position) {
                return data.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyFriendMessageModel model = data.get(position);
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.my_friend_message_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.my_friend_massage_sub_tv = (TextView) convertView.findViewById(R.id.my_friend_massage_sub_tv);
                viewHolder.my_friend_message_name_tv = (TextView) convertView.findViewById(R.id.my_friend_message_name_tv);
                viewHolder.my_friend_message_refuse = (Button) convertView.findViewById(R.id.my_friend_message_refuse);
                viewHolder.my_friend_message_accept = (Button) convertView.findViewById(R.id.my_friend_message_accept);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.my_friend_message_name_tv.setText(model.friendName);
            viewHolder.my_friend_massage_sub_tv.setText("请求添加你为"+model.relation);
            viewHolder.my_friend_message_refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Host.doCommand("responseFamily", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) {
                            if (Response.CODE_SUCCESS != code()) {
                                Toast.makeText(MyFriendMessageActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONObject resultObject = JSONObject.convert(content);
                            if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                                Toast.makeText(MyFriendMessageActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                                return;
                            }else{
                                Toast.makeText(MyFriendMessageActivity.this, "操作成功", Toast.LENGTH_LONG).show();
                                viewHolder.my_friend_message_refuse.setVisibility(View.GONE);
                                viewHolder.my_friend_message_accept.setVisibility(View.GONE);
                            }
                        }
                    }, Logic.token,false,model.requestId);
                }
            });
            viewHolder.my_friend_message_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Host.doCommand("responseFamily", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) {
                            if (Response.CODE_SUCCESS != code()) {
                                Toast.makeText(MyFriendMessageActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONObject resultObject = JSONObject.convert(content);
                            if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                                Toast.makeText(MyFriendMessageActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                                return;
                            }else{
                                Toast.makeText(MyFriendMessageActivity.this, "操作成功", Toast.LENGTH_LONG).show();
                                viewHolder.my_friend_message_refuse.setVisibility(View.GONE);
                                viewHolder.my_friend_message_accept.setVisibility(View.GONE);
                            }
                        }
                    }, Logic.token,true, model.requestId);
                    if(messageId > 0) {
                        Host.doCommand("readMessage", new CommonResponse<String>() {
                            @Override
                            public void onFinished(String content) {
                            }
                        }, Logic.token, messageId);
                    }
                }
            });
            return convertView;

        }

        class ViewHolder {
            public Button my_friend_message_refuse;
            public Button my_friend_message_accept;
            public TextView my_friend_message_name_tv;
            public TextView my_friend_massage_sub_tv;


        }
    }
}
