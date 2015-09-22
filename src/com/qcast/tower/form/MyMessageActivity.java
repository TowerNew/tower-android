package com.qcast.tower.form;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.Response;
import com.qcast.tower.model.MyMessageModel;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONBoolean;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import java.util.ArrayList;

/**
 * Created by zhengningchuan on 15/9/17.
 */
public class MyMessageActivity extends Activity {
    private ArrayList<MyMessageModel> dataList;
    private MyMessageAdapter adapter;
    private ListView messageListView;
    private Button return_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_my_message);
        messageListView = (ListView) this.findViewById(R.id.my_message_list);
        return_btn = (Button) this.findViewById(R.id.mymessage_return_btn);
        dataList = new ArrayList<MyMessageModel>();
        adapter = new MyMessageAdapter(this, dataList);
        messageListView.setAdapter(adapter);
        loadData();
        return_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyMessageActivity.this.finish();
            }
        });
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyMessageModel myMessageModel=dataList.get(position);
                if(myMessageModel.type==1){
//                    Intent intent = new Intent(MyMessageActivity.this, MyFriendMessageActivity.class);
//                    Bundle bundle =new Bundle();
//                    bundle.putSerializable("myFriendMessage",myMessageModel);
//                    intent.putExtras(bundle);
//                    MyMessageActivity.this.startActivity(intent);
                }
            }
        });
    }

    private void loadData() {
        Host.doCommand("myMessage", new CommonResponse<String>() {
            @Override
            public void onFinished(String content) {
                if (Response.CODE_SUCCESS != code()) {
                    Toast.makeText(MyMessageActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(MyMessageActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray result = (JSONArray) resultObject.get("data");
                if (null == result) {
                    return;
                }

                for (IJSON item : result) {
                    JSONObject newJSONObject = (JSONObject) item;
                    MyMessageModel myMessageModel = new MyMessageModel();

                    myMessageModel.title = ((JSONString) newJSONObject.get("title")).getValue();
                    myMessageModel.id = ((JSONNumber) newJSONObject.get("id")).intValue();
                    myMessageModel.time = ((JSONString) newJSONObject.get("time")).getValue();
                    myMessageModel.type =((JSONNumber) newJSONObject.get("type")).intValue();
                    myMessageModel.hasRead = ((JSONBoolean) newJSONObject.get("hasRead")).getValue();
                    if(myMessageModel.type==1){
                        JSONObject infoJSObj = (JSONObject) newJSONObject.get("info");
                        myMessageModel.name=((JSONString) infoJSObj.get("name")).getValue();
                        myMessageModel.phone=((JSONString) infoJSObj.get("phone")).getValue();
                        myMessageModel.relation=((JSONString) infoJSObj.get("relation")).getValue();
                        myMessageModel.requestId=((JSONNumber) infoJSObj.get("requestId")).intValue();
                    }
                    dataList.add(myMessageModel);

                }
                adapter.notifyDataSetChanged();
            }
        }, Logic.token);
    }

    class MyMessageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<MyMessageModel> data;

        public MyMessageAdapter(Context context, ArrayList<MyMessageModel> data) {
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
            MyMessageModel model = data.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.my_message_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.message_tv = (TextView) convertView.findViewById(R.id.message_tv);
                viewHolder.mymessage_date_tv = (TextView) convertView.findViewById(R.id.mymessage_date_tv);
                viewHolder.message_mark_iv = (ImageView) convertView.findViewById(R.id.message_mark_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.message_tv.setText(model.title);
            viewHolder.mymessage_date_tv.setText(model.time);
            if (model.hasRead) {
                viewHolder.message_mark_iv.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.message_mark_iv.setVisibility(View.VISIBLE);
            }
            return convertView;

        }

        class ViewHolder {
            public ImageView message_mark_iv;
            public TextView message_tv;
            public TextView mymessage_date_tv;


        }
    }
}
