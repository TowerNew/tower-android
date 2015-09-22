package com.qcast.tower.form;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.qcast.tower.logic.Storage;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.ImageResponse;
import com.qcast.tower.logic.response.Response;
import com.qcast.tower.model.MyChatHistoryModel;
import com.qcast.tower.model.MyReserveModel;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;

import java.util.ArrayList;

/**
 * Created by zhengningchuan on 15/9/22.
 */
public class MyReserveHistoryActivity extends Activity{
    private ArrayList<MyReserveModel> dataList;
    private MyReserveAdapter adapter;
    private ListView reserveListView;
    private Button return_btn;
    private Button bell_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_my_reserve_history_doctor);
        reserveListView = (ListView) this.findViewById(R.id.my_reserve_history_list);
        return_btn = (Button) this.findViewById(R.id.my_reserve_history_return_btn);
        bell_btn = (Button) this.findViewById(R.id.my_reserve_history_bell_btn);
        dataList = new ArrayList<MyReserveModel>();
        adapter = new MyReserveAdapter(this,dataList);
        reserveListView.setAdapter(adapter);
        loadData();
        return_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyReserveHistoryActivity.this.finish();
            }
        });


    }

    private void loadData() {
        Host.doCommand("reserveHistory", new CommonResponse<String>() {
            @Override
            public void onFinished(String content) {
                if (Response.CODE_SUCCESS != code()) {
                    Toast.makeText(MyReserveHistoryActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(MyReserveHistoryActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray result = (JSONArray) resultObject.get("data");

                if (null == result) {
                    return;
                }
                for (IJSON item : result) {
                    JSONObject newJSONObject = (JSONObject) item;
                    MyReserveModel myReserveModel = new MyReserveModel();
                    String imageUrl = "";
                    String photoName = "";

                    myReserveModel.id = ((JSONNumber) newJSONObject.get("id")).intValue();
                    myReserveModel.type = ((JSONNumber) newJSONObject.get("type")).intValue();
                    myReserveModel.date = ((JSONString) newJSONObject.get("date")).getValue();
                    myReserveModel.span = ((JSONString) newJSONObject.get("span")).getValue();
                    myReserveModel.status = ((JSONNumber) newJSONObject.get("status")).intValue();
                    if(myReserveModel.type==1){
                        JSONObject doctorObj= (JSONObject) newJSONObject.get("doctor");
                        if (doctorObj.get("photo") != null) {
                            imageUrl = ((JSONString) doctorObj.get("photo")).getValue();
                            photoName = Storage.getImageName(imageUrl);
                            myReserveModel.photoUrl = photoName;
                            myReserveModel.photoName = photoName;
                        }
                        myReserveModel.name= ((JSONString) doctorObj.get("name")).getValue();
                        myReserveModel.subId = ((JSONNumber) doctorObj.get("id")).intValue();
                        myReserveModel.memo= ((JSONString) doctorObj.get("memo")).getValue();
                    }else if(myReserveModel.type==2){
                        JSONObject obj= (JSONObject) newJSONObject.get("examination");
                        myReserveModel.name= ((JSONString) obj.get("name")).getValue();
                        myReserveModel.subId = ((JSONNumber) obj.get("id")).intValue();
                        myReserveModel.memo= ((JSONString) obj.get("memo")).getValue();
                    }


                    dataList.add(myReserveModel);
                    if (!TextUtils.isEmpty(imageUrl) && Text.isBlank(imageUrl)) {
                        continue;
                    }
                    // 加载图片
                    if (!TextUtils.isEmpty(photoName)) {
                        Host.doImage("image", new ImageResponse(photoName, dataList.size() - 1) {
                            @Override
                            public void onFinished(Bitmap content) {
                                // DoctorModel doctorModel = dataList.get((Integer) tag);
                                // doctorModel.docImage = content;
                                adapter.notifyDataSetChanged();
                            }
                        }, imageUrl);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        }, Logic.token);
    }

    class MyReserveAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<MyReserveModel> data;

        public MyReserveAdapter(Context context,ArrayList<MyReserveModel> data){
            this.context=context;
            this.data=data;
        }


        @Override
        public int getCount() {
            if(data!= null && data.size()>0){
                return data.size();
            }else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if(data!= null && data.size()>position) {
                return data.get(position);
            }else{
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyReserveModel model=data.get(position);
            ViewHolder viewHolder;
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.my_reserve_history_list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.doctor_name_tv = (TextView)convertView.findViewById(R.id.doctor_name_tv);
                viewHolder.reverseTime = (TextView)convertView.findViewById(R.id.date_tv);
                viewHolder.doctor_photo_image = (ImageView)convertView.findViewById(R.id.doctor_photo_image);
                viewHolder.cancel_btn = (Button)convertView.findViewById(R.id.cancel_btn);
                viewHolder.status_tv = (TextView)convertView.findViewById(R.id.status_tv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.doctor_name_tv.setText(model.name);
            viewHolder.reverseTime.setText(model.date + " " + model.span);
            viewHolder.doctor_photo_image.setImageBitmap(model.getPhoto());
            if(model.status==1){
                viewHolder.cancel_btn.setVisibility(View.VISIBLE);
                viewHolder.status_tv.setVisibility(View.GONE);
            }else if(model.status==2){
                viewHolder.cancel_btn.setVisibility(View.GONE);
                viewHolder.status_tv.setVisibility(View.VISIBLE);
                viewHolder.status_tv.setTextColor(Color.RED);
            }else if(model.status==3){
                viewHolder.cancel_btn.setVisibility(View.GONE);
                viewHolder.status_tv.setVisibility(View.VISIBLE);
                viewHolder.status_tv.setTextColor(Color.GRAY);
            }
            return convertView;
        }

        class ViewHolder{
            public TextView doctor_name_tv;
            public TextView reverseTime;
            public ImageView doctor_photo_image;
            public TextView status_tv;
            public Button cancel_btn;
        }
    }
}
