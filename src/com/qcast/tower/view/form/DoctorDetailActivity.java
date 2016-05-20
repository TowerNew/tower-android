package com.qcast.tower.view.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.qcast.tower.business.structure.DoctorCommentsModel;
import com.qcast.tower.business.structure.DoctorModel;
import com.qcast.tower.framework.Helper;
import com.qcast.tower.view.form.InquiryDoctorDetailActivity.CommentsAdapter.ViewHolder;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONBoolean;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.List;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


@ResourceView(id = R.layout.activity_doctor_detail)
public class DoctorDetailActivity extends ActivityEx{
	
	@ResourceView(id = R.id.doctordetail_image_close)
	public Button buttonClose;
	
	@ResourceView(id = R.id.doctordetail_text_title)
	public TextView doctorName ;
	
	@ResourceView(id = R.id.doctordetail_image_photo)
	public ImageView doctorPhoto ;
	
	@ResourceView(id = R.id.doctordetail_lable_title)
	public TextView reserveTitle ;
	
	@ResourceView(id = R.id.doctordetail_lable_title1)
	public TextView doctorTitle1 ;
	
	@ResourceView(id = R.id.doctordetail_lable_title2)
	public TextView doctorTitle2 ;
	
	@ResourceView(id = R.id.doctordetail_lable_department)
	public TextView doctorDepartment ;
	
	@ResourceView(id = R.id.doctordetail_layout_collection)
	public RelativeLayout doctorCollection ;
		
	@ResourceView(id = R.id.doctor_btn_set)
	public Button doctorSet ;
	
	@ResourceView(id = R.id.doctor_text_detail)
	public TextView doctorDetail ;
	/**
     * 当前页面索引
     */
    protected int page = 1;
    private ArrayList<DoctorCommentsModel> dataList;
    private CommentsAdapter adapter;
    
    private int nextStart;
	private String docId = null;
	public void onCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
		
	}
	
	private void prepare() {
		
		    Intent intent = this.getIntent();
		    final String docId = intent.getStringExtra("id");    			
		    String name = intent.getStringExtra("name");
		    String department = intent.getStringExtra("department");
		    String photo = intent.getStringExtra("photo");
		    doctorName.setText(name);
		 buttonClose.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {	
					DoctorDetailActivity.this.finish();						    				
				}				
		 	});		
		/* doctorSet.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {					 
				                AlertDialog.Builder builder = new AlertDialog.Builder(DoctorDetailActivity.this);  				                  
				                builder.setIcon(R.drawable.ic_launcher);  
				                builder.setTitle("设置此医生为你的私人医生？");  
				                builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {  				                      
				                    @Override  
				                    public void onClick(DialogInterface arg0, int arg1) {  
				                    	//
				                    }  				          
				                });  
				                builder.setNegativeButton("是的", new DialogInterface.OnClickListener() {  				                      
				                    @Override  
				                    public void onClick(DialogInterface arg0, int arg1) {  
				                    Networking.doCommand("selectDoctor", new JSONResponse(DoctorDetailActivity.this) {
				    					@Override
				    					public void onFinished(JSONVisitor content) {
				    						if(null == content || content.getInteger("code", -1) < 0) {
				    							return;
				    						}
				    						Me.instance.refreshDoctor(DoctorDetailActivity.this, new IEventable<Boolean>() {
				    							public void on(Boolean data) {
				    								Me.instance.doChat(DoctorDetailActivity.this, null, Me.instance.doctor.imId);
				    								DoctorDetailActivity.this.finish();
				    							}
				    						});
				    					}
				    				}, docId, Me.instance.token);  
				                    }  				                    
				                });  			                  
				                builder.create().show();  
				            }  				 
				        });  */
		 /* if(!TextUtils.isEmpty(doctorModel.name)){
	        	doctorName.setText(doctorModel.name);
	        }
	        if(!TextUtils.isEmpty(doctorModel.department)){
	        	doctorDepartment.setText(doctorModel.department);
	        }
	        if(!TextUtils.isEmpty(doctorModel.title)){
	        	doctorTitle1.setText(doctorModel.title);
	        }
	        if(!TextUtils.isEmpty(doctorModel.description)){
	        	doctorTitle2.setText(doctorModel.description);
	        }
	        if(doctorModel.getPhoto() instanceof Bitmap){
	        	doctorPhoto.setImageBitmap(doctorModel.getPhoto());
	        }*/	         
		 ListView doctor_comments_list = (ListView) this.findViewById(R.id.doctor_comments_list);
		 ArrayList<DoctorCommentsModel> dataList = new ArrayList<DoctorCommentsModel>();
	         final CommentsAdapter adapter = new CommentsAdapter(this,dataList);
	        doctor_comments_list.setAdapter(adapter);
	         
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
	                /*String commentsNum = ((JSONNumber)result.get("recordCount")).intValue()+"";*/
	                nextStart = ((JSONNumber)result.get("nextStart")).intValue();	               
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
	                page = thisPage + 1;

	            }
	        }, docId ,page);
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

