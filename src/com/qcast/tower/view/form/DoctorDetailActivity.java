package com.qcast.tower.view.form;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.qcast.tower.business.structure.Doctor;
import com.qcast.tower.business.structure.DoctorCommentsModel;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONBoolean;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 
 */
@ResourceView(id = R.layout.activity_doctor_detail)
public class DoctorDetailActivity  extends ActivityEx{
	private TextView docdetail_title_bar;
    private TextView doctor_name_tv;
    private TextView doctor_department_tv;
    private TextView doctor_title_tv;
    //
   
	@ResourceView(id = R.id.doctor_icon_collection)
	private ImageView viewCollection;
    private TextView doctor_skill_tv;
    private TextView doctor_des_tv;
    private TextView user_comments_num_tv;
    private ImageView doctor_photo_image;
    private ListView doctor_comments_list;
    private LinearLayout doctorCollection_layout;
    private Button doctor_btn_set;
    private ArrayList<DoctorCommentsModel> dataList;
    private CommentsAdapter adapter;
    private Doctor doctor;
    /**
     * 当前页面索引
     */
    protected int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        if(null == Me.instance) {
			return;
		}	
        Intent intent = this.getIntent();
        
        docdetail_title_bar = (TextView) this.findViewById(R.id.docdetail_title_bar);
        doctor_name_tv = (TextView) this.findViewById(R.id.doctor_name_tv);
        doctor_department_tv = (TextView) this.findViewById(R.id.doctor_department_tv);
        doctor_title_tv = (TextView) this.findViewById(R.id.doctor_title_tv);
        
        doctor_skill_tv = (TextView) this.findViewById(R.id.doctor_skill_tv);
        /*doctor_skill_tv.setMovementMethod(new ScrollingMovementMethod());*/
        
        doctor_des_tv = (TextView) this.findViewById(R.id.doctor_des_tv);
        /*doctor_des_tv.setMovementMethod(new ScrollingMovementMethod());*/
        
        user_comments_num_tv = (TextView) this.findViewById(R.id.user_comments_num_tv);
              
        Button button = (Button) this.findViewById(R.id.doctordetail_return_btn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DoctorDetailActivity.this.finish();
			}
		});     		
        doctor_photo_image = (ImageView) this.findViewById(R.id.doctor_photo_image);        
        doctor_comments_list = (ListView) this.findViewById(R.id.doctor_comments_list);
        //收藏
        doctorCollection_layout = (LinearLayout) this.findViewById(R.id.doctorCollection_layout);
        doctorCollection_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
            	if(null == Me.instance) {
        			return;
        		}
            	Networking.doCommand("collectionStatus", new CommonResponse<String>() {
                    @Override
                    public void onFinished(String content) {
                        if (Response.CODE_SUCCESS != code()) {
                            Toast.makeText(DoctorDetailActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                            return;
                        }
                        JSONObject resultObject = JSONObject.convert(content);
                        if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                            Toast.makeText(DoctorDetailActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                            return;
                        }                        
                        JSONBoolean hasCollection=  (JSONBoolean) resultObject.get("data");
                        if (!hasCollection.getValue()){
                        	viewCollection.setBackgroundResource(R.drawable.favorite_selected);
                        }
                        viewCollection.setBackgroundResource(R.drawable.favorite_normal);
                    }
                },Me.instance.doctor.id,Me.instance.token,false);	
            }
        });
        //设为私人医生
        doctor_btn_set = (Button) this.findViewById(R.id.doctor_btn_set);
        doctor_btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  
            	if(null == Me.instance) {
        			return;
        		}	
        				new AlertDialog.Builder(DoctorDetailActivity.this).setTitle("你要设为私人医生吗？")  
        				.setIcon(android.R.drawable.ic_dialog_info)  
        				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
        						@Override
        						public void onClick(DialogInterface dialog, int which) { 
        							
        							DoctorDetailActivity.this.finish();
        						}  
        				}).setNegativeButton("返回", new DialogInterface.OnClickListener() {
        			        @Override  
        			        public void onClick(DialogInterface dialog, int which) {}  
        				}).show();
        			}
        		});     
        //
        dataList = new ArrayList<DoctorCommentsModel>();
        adapter = new CommentsAdapter(this,dataList);
        doctor_comments_list.setAdapter(adapter);        
        if(!TextUtils.isEmpty(doctor.name)){
            doctor_name_tv.setText(doctor.name);
            docdetail_title_bar.setText(doctor.name);
        }
        if(!TextUtils.isEmpty(doctor.department)){
            doctor_department_tv.setText(doctor.department);
        }
        if(!TextUtils.isEmpty(doctor.title)){
            doctor_title_tv.setText(doctor.title);
        }
        if(!TextUtils.isEmpty(doctor.description)){
            doctor_des_tv.setText(doctor.description);
        }
        if(doctor.photo() instanceof Bitmap){
            doctor_photo_image.setImageBitmap(doctor.photo());
        }
        if(!TextUtils.isEmpty(doctor.resume)){
            doctor_skill_tv.setText(doctor.resume);
        }
        
        /*bad_result_tv.setText(String.valueOf(doctor.badCount));
        good_result_tv.setText(String.valueOf(doctor.goodCount));*/
        
        doctor_comments_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastItemIndex;//当前ListView中最后一个Item的索引

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && lastItemIndex == adapter.getCount() - 1) {
                    loadData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        Networking.doCommand("commentlist", new CommonResponse<String>(page) {
            @Override
            public void onFinished(String content) {
                if (Response.CODE_SUCCESS != code()) {
                    Toast.makeText(DoctorDetailActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(DoctorDetailActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                int thisPage = (Integer) this.tag;
                if(page != thisPage) {
                    return;
                }
                JSONObject result = (JSONObject) resultObject.get("data");

                String commentsNum = ((JSONNumber)result.get("recordCount")).intValue()+"";
                int nextStart = ((JSONNumber)result.get("nextStart")).intValue();
                if(!TextUtils.isEmpty(commentsNum)){
                    user_comments_num_tv.setText("用户评价（"+commentsNum+"）");
                }
                JSONArray listResult = (JSONArray)result.get("records");
                if(null != listResult) {
                    for (IJSON item : listResult) {
                        JSONObject newJSONObject = (JSONObject) item;
                        DoctorCommentsModel doctorCommentsModel = new DoctorCommentsModel();
                        if(null==((JSONString) newJSONObject.get("username")).getValue()){
                        	return;
                        }
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
                page = thisPage + 1;

            }
        }, Me.instance.doctor.id ,page);
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
            viewHolder.user_name_tv.setText(data.userName);
            viewHolder.user_comments_date_tv.setText(data.commentDate);
            viewHolder.user_comments_tv.setText(data.userComment);
            return convertView;
        }

        class ViewHolder{
            public TextView user_name_tv;
            public TextView user_comments_date_tv;
            public TextView user_comments_tv;
        }
    }
}
