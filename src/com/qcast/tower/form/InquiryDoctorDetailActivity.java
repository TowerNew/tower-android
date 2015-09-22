package com.qcast.tower.form;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.Response;
import com.qcast.tower.model.DoctorCommentsModel;
import com.qcast.tower.model.DoctorModel;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONBoolean;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by zhengningchuan on 15/9/2.
 */
public class InquiryDoctorDetailActivity extends Activity{

    private TextView doctor_name_tv;
    private TextView doctor_type_tv;
    private TextView doctor_title_tv;
    private TextView doctor_skill_tv;
    private TextView doctor_des_tv;
    private TextView user_comments_num_tv;
    private TextView bad_result_tv;
    private TextView good_result_tv;
    private Button good_vote_btn;
    private Button bad_vote_btn;
    private ImageView doctor_photo_image;
    private ListView doctor_comments_list;
    private LinearLayout inquiry_layout;
    private LinearLayout reserve_layout;
    private LinearLayout comments_layout;
    private ArrayList<DoctorCommentsModel> dataList;
    private CommentsAdapter adapter;
    private DoctorModel doctorModel;
    private int nextStart;
    /**
     * 当前页面索引
     */
    protected int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        doctorModel = (DoctorModel) bundle.getSerializable("docDetail");
        if(doctorModel==null){
            this.finish();
        }
        this.setContentView(R.layout.actitity_inquiry_doctor_detail);
        doctor_name_tv = (TextView) this.findViewById(R.id.doctor_name_tv);
        doctor_type_tv = (TextView) this.findViewById(R.id.doctor_type_tv);
        doctor_title_tv = (TextView) this.findViewById(R.id.doctor_title_tv);
        doctor_skill_tv = (TextView) this.findViewById(R.id.doctor_skill_tv);
        doctor_des_tv = (TextView) this.findViewById(R.id.doctor_des_tv);
        user_comments_num_tv = (TextView) this.findViewById(R.id.user_comments_num_tv);
        bad_result_tv = (TextView) this.findViewById(R.id.bad_result_tv);
        good_result_tv = (TextView) this.findViewById(R.id.good_result_tv);
        good_vote_btn = (Button) this.findViewById(R.id.good_vote_btn);
        /*good_vote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Host.doCommand("docVote", new CommonResponse<String>() {
                    @Override
                    public void onFinished(String content) throws JSONException {
                        if (Response.CODE_SUCCESS != code()) {
                            Toast.makeText(InquiryDoctorDetailActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                            return;
                        }
                        JSONObject resultObject = JSONObject.convert(content);
                        if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                            Toast.makeText(InquiryDoctorDetailActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!TextUtils.isEmpty(good_result_tv.getText().toString())){
                            good_result_tv.setText((Integer.parseInt(good_result_tv.getText().toString())+1)+"");
                        }
                    }
                },Logic.token,true);
            }
        });*/
        Button button = (Button) this.findViewById(R.id.doctordetail_return_btn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InquiryDoctorDetailActivity.this.finish();
			}
		});
        bad_vote_btn = (Button) this.findViewById(R.id.bad_vote_btn);
        /*bad_vote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Host.doCommand("docVote", new CommonResponse<String>() {
                    @Override
                    public void onFinished(String content) throws JSONException {
                        if (Response.CODE_SUCCESS != code()) {
                            Toast.makeText(InquiryDoctorDetailActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                            return;
                        }
                        JSONObject resultObject = JSONObject.convert(content);
                        if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                            Toast.makeText(InquiryDoctorDetailActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!TextUtils.isEmpty(bad_result_tv.getText().toString())){
                            bad_result_tv.setText((Integer.parseInt(bad_result_tv.getText().toString())+1)+"");
                        }
                    }
                },Logic.token,false);
            }
        });*/
        doctor_photo_image = (ImageView) this.findViewById(R.id.doctor_photo_image);
        doctor_comments_list = (ListView) this.findViewById(R.id.doctor_comments_list);
        inquiry_layout = (LinearLayout) this.findViewById(R.id.inquiry_layout);
        inquiry_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(null == Logic.token) {
            		Toast.makeText(InquiryDoctorDetailActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                    return;
            	}
                Intent intent = new Intent(InquiryDoctorDetailActivity.this,InquiryDoctorChatActivity.class);
                intent.putExtra("docId",doctorModel.doctorId);
                InquiryDoctorDetailActivity.this.startActivity(intent);

            }
        });
        reserve_layout = (LinearLayout) this.findViewById(R.id.reserve_layout);
        comments_layout = (LinearLayout) this.findViewById(R.id.comments_layout);
        dataList = new ArrayList<DoctorCommentsModel>();
        adapter = new CommentsAdapter(this,dataList);
        doctor_comments_list.setAdapter(adapter);
        if(!TextUtils.isEmpty(doctorModel.name)){
            doctor_name_tv.setText(doctorModel.name);
        }
        if(!TextUtils.isEmpty(doctorModel.department)){
            doctor_type_tv.setText(doctorModel.department);
        }
        if(!TextUtils.isEmpty(doctorModel.title)){
            doctor_title_tv.setText(doctorModel.title);
        }
        if(!TextUtils.isEmpty(doctorModel.description)){
            doctor_des_tv.setText(doctorModel.description);
        }
        if(doctorModel.getPhoto() instanceof Bitmap){
            doctor_photo_image.setImageBitmap(doctorModel.getPhoto());
        }
        if(!TextUtils.isEmpty(doctorModel.resume)){
            doctor_skill_tv.setText(doctorModel.resume);
        }
        loadData();

    }

    private void loadData() {
        Host.doCommand("commentlist", new CommonResponse<String>() {
            @Override
            public void onFinished(String content) {
                if (Response.CODE_SUCCESS != code()) {
                    Toast.makeText(InquiryDoctorDetailActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(InquiryDoctorDetailActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject result = (JSONObject) resultObject.get("data");

                String commentsNum = ((JSONNumber)result.get("recordCount")).intValue()+"";
                nextStart = ((JSONNumber)result.get("nextStart")).intValue();
                if(!TextUtils.isEmpty(commentsNum)){
                    user_comments_num_tv.setText("用户评价（"+commentsNum+"）");
                }
                if(result.get("bad")!=null) {
                    String badNum = ((JSONString) result.get("bad")).getValue();
                    if (TextUtils.isEmpty(commentsNum)) {
                        bad_result_tv.setText(badNum);
                    }
                }
                if(result.get("good")!=null) {
                    String goodNum = ((JSONString) result.get("good")).getValue();
                    if (TextUtils.isEmpty(commentsNum)) {
                        good_result_tv.setText(goodNum);
                    }
                }
                JSONArray listResult = (JSONArray)result.get("records");
                if(null != listResult) {
                    for (IJSON item : listResult) {
                        JSONObject newJSONObject = (JSONObject) item;
                        DoctorCommentsModel doctorCommentsModel = new DoctorCommentsModel();

                        doctorCommentsModel.userName = ((JSONString) newJSONObject.get("username")).getValue();
                        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm");
                        long time = ((JSONNumber) newJSONObject.get("date")).longValue();
                        doctorCommentsModel.commentDate = sdf.format(time);

                        doctorCommentsModel.userComment = ((JSONString) newJSONObject.get("content")).getValue();
                        doctorCommentsModel.attitude = ((JSONBoolean) newJSONObject.get("attitude")).getValue();
                        dataList.add(doctorCommentsModel);
                    }
                }
                adapter.notifyDataSetChanged();
                if(page<nextStart) {
                    page = page + 1;
                }
            }
        }, doctorModel.doctorId ,page);
    }

    public class CommentsAdapter extends BaseAdapter{

        private Context context;
        private ArrayList<DoctorCommentsModel> dataList;

        public CommentsAdapter(Context context,ArrayList<DoctorCommentsModel> dataList){
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            if(dataList!=null){
                return dataList.size();
            }else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if(dataList!=null && dataList.size()>=position){
                return dataList.get(position);
            }else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DoctorCommentsModel data = dataList.get(position);
            ViewHolder viewHolder;
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.doctor_comment_item,null);
                viewHolder = new ViewHolder();
                viewHolder.user_comments_date_tv=(TextView)convertView.findViewById(R.id.user_comments_date_tv);
                viewHolder.user_name_tv=(TextView)convertView.findViewById(R.id.user_name_tv);
                viewHolder.user_comments_tv=(TextView)convertView.findViewById(R.id.user_comments_tv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.user_name_tv.setText(dataList.get(position).userName);
            viewHolder.user_comments_date_tv.setText(dataList.get(position).commentDate);
            viewHolder.user_comments_tv.setText(dataList.get(position).userComment);
            return convertView;
        }

        class ViewHolder{
            public TextView user_name_tv;
            public TextView user_comments_date_tv;
            public TextView user_comments_tv;
        }
    }
}
