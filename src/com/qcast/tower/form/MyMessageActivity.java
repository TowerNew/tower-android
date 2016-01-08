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
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.qcast.tower.model.NotifyModel;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONBoolean;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 我的消息页
 */
public class MyMessageActivity extends Activity {
    private ArrayList<NotifyModel> dataList;
    private MyMessageAdapter adapter;
    private ListView messageListView;
    private Button return_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_my_message);
        messageListView = (ListView) this.findViewById(R.id.my_message_list);
        return_btn = (Button) this.findViewById(R.id.mymessage_return_btn);
        dataList = new ArrayList<NotifyModel>();
        adapter = new MyMessageAdapter(this, dataList);
        messageListView.setAdapter(adapter);
        return_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyMessageActivity.this.finish();
            }
        });
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotifyModel notifyModel = dataList.get(position);
                if(!notifyModel.hasRead) {
                    Host.doCommand("readMessage", new CommonResponse<String>() {
                        @Override
                        public void onFinished(String content) { }
                    }, Logic.token, notifyModel.id);
                }
                switch(notifyModel.type) {
                case 1:
                	Intent intent1 = new Intent(MyMessageActivity.this, MyFriendMessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("myFriendMessage", notifyModel);
                    intent1.putExtras(bundle);
                    intent1.putExtra("messageId", notifyModel.id);
                    MyMessageActivity.this.startActivity(intent1);
                	break;
                case 2:
                	Intent intent2 = new Intent(MyMessageActivity.this, MyFriendMessageActivity.class);
                	intent2.putExtra("title", "通知");
                	intent2.putExtra("content", "对方接受了您的添加请求");
                    MyMessageActivity.this.startActivity(intent2);
                	break;
                case 3:
                	Intent intent3 = new Intent(MyMessageActivity.this, MyFriendMessageActivity.class);
                	intent3.putExtra("title", "通知");
                	intent3.putExtra("content", "对方拒绝了您的添加请求");
                    MyMessageActivity.this.startActivity(intent3);
                	break;
                case 4:
                	Intent intent4 = new Intent(MyMessageActivity.this, WebActivity.class);
                	intent4.putExtra("url", notifyModel.url);
                    MyMessageActivity.this.startActivity(intent4);
                	break;
                case 5:
                	Intent intent5 = new Intent(MyMessageActivity.this, MyFriendMessageActivity.class);
                	intent5.putExtra("title", "通知");
                	intent5.putExtra("content", "问诊");
                    MyMessageActivity.this.startActivity(intent5);
                	break;
                case 6:
                	Intent intent6 = new Intent(MyMessageActivity.this, MyFriendMessageActivity.class);
                	intent6.putExtra("title", "通知");
                	intent6.putExtra("content", "互动");
                    MyMessageActivity.this.startActivity(intent6);
                	break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	loadData();
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
                	dataList.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }
                dataList.clear();
                SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                for(IJSON item : result) {
                    JSONObject newJSONObject = (JSONObject) item;
                    NotifyModel myMessageModel = new NotifyModel();
                    myMessageModel.id = ((JSONNumber) newJSONObject.get("id")).intValue();
                    myMessageModel.title = ((JSONString) newJSONObject.get("title")).getValue();
                    myMessageModel.time = format.format(((JSONNumber) newJSONObject.get("time")).longValue());
                    myMessageModel.type = ((JSONNumber) newJSONObject.get("type")).intValue();
                    myMessageModel.hasRead = ((JSONBoolean) newJSONObject.get("hasRead")).getValue();
                    switch(myMessageModel.type) {
                    case 1:
                        JSONObject infomation1 = (JSONObject) newJSONObject.get("info");
                        if(null == infomation1) {
                        	break;
                        }
                        myMessageModel.requestId = ((JSONString) infomation1.get("requestId")).getValue();
                        myMessageModel.name = ((JSONString) infomation1.get("name")).getValue();
                        myMessageModel.phone = ((JSONString) infomation1.get("phone")).getValue();
                        myMessageModel.relation = ((JSONString) infomation1.get("relation")).getValue();
                    	break;
                    case 2:
                        JSONObject infomation2 = (JSONObject) newJSONObject.get("info");
                        if(null == infomation2) {
                        	break;
                        }
                        myMessageModel.name = "";
                        if(null != infomation2.get("name")) {
                        	myMessageModel.name = ((JSONString) infomation2.get("name")).getValue();
                        }
                        myMessageModel.phone = "";
                        if(null != infomation2.get("phone")) {
                            myMessageModel.phone = ((JSONString) infomation2.get("phone")).getValue();
                        }
                        myMessageModel.relation = "";
                        if(null != infomation2.get("relation")) {
                            myMessageModel.relation = ((JSONString) infomation2.get("relation")).getValue();
                        }
                    	break;
                    case 3:
                        JSONObject infomation3 = (JSONObject) newJSONObject.get("info");
                        if(null == infomation3) {
                        	break;
                        }
                        myMessageModel.name = "";
                        if(null != infomation3.get("name")) {
                        	myMessageModel.name = ((JSONString) infomation3.get("name")).getValue();
                        }
                        myMessageModel.phone = "";
                        if(null != infomation3.get("phone")) {
                            myMessageModel.phone = ((JSONString) infomation3.get("phone")).getValue();
                        }
                        myMessageModel.relation = "";
                        if(null != infomation3.get("relation")) {
                            myMessageModel.relation = ((JSONString) infomation3.get("relation")).getValue();
                        }
                    	break;
                    case 4:
                        JSONObject infomation4 = (JSONObject) newJSONObject.get("info");
                        if(null == infomation4) {
                        	break;
                        }
                        myMessageModel.url = "";
                        if(null != infomation4.get("url")) {
                        	myMessageModel.url = ((JSONString) infomation4.get("url")).getValue();
                        }
                    	break;
                    case 5:
                    	break;
                    case 6:
                    	break;
                    }
                    dataList.add(myMessageModel);
                }
                adapter.notifyDataSetChanged();
            }
        }, Logic.token);
    }

    class MyMessageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<NotifyModel> data;

        public MyMessageAdapter(Context context, ArrayList<NotifyModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            if (data != null && data.size() > 0) {
                return data.size();
            }
            else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if (data != null && data.size() > position) {
                return data.get(position);
            }
            else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NotifyModel model = data.get(position);
            ViewHolder viewHolder = null;
            if(null == convertView) {
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_notify, parent);
                viewHolder = new ViewHolder();
                viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.notify_icon_type);
                viewHolder.labTitle = (TextView) convertView.findViewById(R.id.notify_label_title);
                viewHolder.labTime = (TextView) convertView.findViewById(R.id.notify_label_time);
                viewHolder.labDescription = (TextView) convertView.findViewById(R.id.notify_label_description);
                viewHolder.imgRead = (ImageView) convertView.findViewById(R.id.notify_icon_read);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if(NotifyModel.TYPE_1 == model.type) {
            	viewHolder.imgIcon.setImageResource(R.drawable.icon_notify_1);
            }
            else if(NotifyModel.TYPE_2 == model.type) {
            	viewHolder.imgIcon.setImageResource(R.drawable.icon_notify_2);
            }
			else if(NotifyModel.TYPE_3 == model.type) {
            	viewHolder.imgIcon.setImageResource(R.drawable.icon_notify_3);
			}
			else if(NotifyModel.TYPE_4 == model.type) {
            	viewHolder.imgIcon.setImageResource(R.drawable.icon_notify_4);
			}
			else if(NotifyModel.TYPE_5 == model.type) {
            	viewHolder.imgIcon.setImageResource(R.drawable.icon_notify_5);
			}
			else if(NotifyModel.TYPE_6 == model.type) {
            	viewHolder.imgIcon.setImageResource(R.drawable.icon_notify_6);
			}
            viewHolder.labTitle.setText(model.title);
            viewHolder.labTime.setText(model.time);
            viewHolder.labDescription.setText(model.description);
            if(model.hasRead) {
                viewHolder.imgRead.setVisibility(View.INVISIBLE);
            }
            else {
                viewHolder.imgRead.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            public ImageView imgIcon;
            public TextView labTitle;
            public TextView labTime;
            public TextView labDescription;
            public ImageView imgRead;
        }
    }
}
