package com.qcast.tower.view.form;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import internal.org.apache.http.entity.mime.content.ContentBody;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.structure.DoctorCommentsModel;
import com.qcast.tower.framework.Storage;
import com.qcast.tower.view.form.InquiryDoctorDetailActivity.CommentsAdapter;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pluto.communication.response.core.IResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONBoolean;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @param <adapter>
 * 
 */
@ResourceView(id = R.layout.activity_doctor_detail)
public class DoctorDetailActivity<adapter>  extends ActivityEx{
	private TextView docdetail_title_bar;
    private TextView doctor_name_tv;
    private TextView doctor_department_tv;
    private TextView doctor_title_tv;
    //
   
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
    private String doctorId;
   
    /**
     * 当前页面索引
     */
    protected int page = 1;
    boolean isCollected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        if(null == Me.instance) {
			return;
		}	
        doctorId = this.getIntent().getStringExtra("doctorId");        
       //  
        docdetail_title_bar = (TextView) this.findViewById(R.id.docdetail_title_bar);
        doctor_name_tv = (TextView) this.findViewById(R.id.doctor_name_tv);
        doctor_department_tv = (TextView) this.findViewById(R.id.doctor_department_tv);
        doctor_title_tv = (TextView) this.findViewById(R.id.doctor_title_tv);
        doctor_skill_tv = (TextView) this.findViewById(R.id.doctor_skill_tv);
        doctor_photo_image = (ImageView) this.findViewById(R.id.doctor_photo_image);       
        doctor_des_tv = (TextView) this.findViewById(R.id.doctor_des_tv);
        viewCollection = (ImageView) this.findViewById(R.id.doctor_icon_collection);

        //
        user_comments_num_tv = (TextView) this.findViewById(R.id.user_comments_num_tv);              
        Button button = (Button) this.findViewById(R.id.doctordetail_return_btn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DoctorDetailActivity.this.finish();
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
	        				Networking.doCommand("selectDoctor", new JSONResponse(DoctorDetailActivity.this) {
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
	        						},doctorId, Me.instance.token);
	        						
	        						}  
        				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
        			        @Override  
        			        public void onClick(DialogInterface dialog, int which) {}  
        				}).show();
        			}
        		});     
        //处理用户评论
        doctor_comments_list = (ListView) this.findViewById(R.id.doctor_comments_list);
        dataList = new ArrayList<DoctorCommentsModel>();
        adapter = new CommentsAdapter(this,dataList);
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
        //处理私人医生收藏
        loadCollection();
        
        doctorCollection_layout = (LinearLayout) this.findViewById(R.id.doctorCollection_layout);
        doctorCollection_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  
            	if(null==Me.instance){
            		return ;
            	}   
            int function =  isCollected ? 1 : 2 ;
/*          Log.e("sss", "zhenglihao. doctorid=" + doctorId);
            Log.e("sss", "zhenglihao. token=" + Me.instance.token);
            Log.e("sss", "zhenglihao. function=" + String.valueOf((isCollected ? 1 : 2)));*/
			Networking.doCommand("doctorCollect", new JSONResponse(DoctorDetailActivity.this,function){
				@Override
				public void onFinished(JSONVisitor content) {	
					
					if (null == content) {
						return;
					}
					if (content.getInteger("code", -1) == 1) {
						switch ((Integer)tag) {
						case 1:							
							viewCollection.setBackgroundResource(R.drawable.favorite_normal);
							isCollected = false;
							break;
						case 2:							
							viewCollection.setBackgroundResource(R.drawable.favorite_selected);
							isCollected = true;
							break;
						default:
							viewCollection.setBackgroundResource(R.drawable.favorite_normal);
						}
					}				
				}       	   
	           }, doctorId, Me.instance.token, String.valueOf((isCollected ? 1 : 2)));        	
            }
        });
    }
    private void loadCollection() {
    	 //加载收藏状态
    	Networking.doCommand("collectionStatus", new JSONResponse(DoctorDetailActivity.this,null) {			
			@Override
			public void onFinished(JSONVisitor content) {			
				if (null == content || content.getInteger("code") < 0) {
					Toast.makeText(DoctorDetailActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
				}
				boolean data = content.getBoolean("data");
				if(!data){
					viewCollection.setBackgroundResource(R.drawable.favorite_normal);					
				}else{
					 viewCollection.setBackgroundResource(R.drawable.favorite_selected);
				}
				isCollected = data;
			}
		}, this.getIntent().getStringExtra("doctorId"), Me.instance.token);
    	
	}
	@Override
	protected void onResume() {
		super.onResume();
		loadData();
		loadDetail();
	}

	private void loadDetail() {
    	  //  加载医生详情  	
        Networking.doCommand("doctorDetail", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(DoctorDetailActivity.this, "网络错误", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject result = JSONObject.convert(content);
				if(null == result) {
					return;
				}
				if(((JSONNumber) result.get("code")).intValue() <= 0) {
					return;
				}		
					String name = ((JSONString)(((JSONObject) result.get("data")).get("name"))).getValue();
					String department = ((JSONString)(((JSONObject) result.get("data")).get("department"))).getValue();
					String description = ((JSONString)(((JSONObject) result.get("data")).get("description"))).getValue();
					String title = ((JSONString)(((JSONObject) result.get("data")).get("title"))).getValue();
					String photoName = ((JSONString)(((JSONObject) result.get("data")).get("photo"))).getValue();
					String address = ((JSONString)(((JSONObject) result.get("data")).get("address"))).getValue();
					
					String	photo = Storage.getImageName(photoName);
				if(!TextUtils.isEmpty(name)){
		            doctor_name_tv.setText(name);
		            docdetail_title_bar.setText(name);
		        }
				if(!TextUtils.isEmpty(title)){
				 doctor_title_tv.setText(title);
				}
		        if(!TextUtils.isEmpty(department)){
		            doctor_department_tv.setText(department);
		        }
		        if(!TextUtils.isEmpty(description)){
		           doctor_skill_tv.setText(description);
		        }
		        if(!TextUtils.isEmpty(address)){
		            doctor_des_tv.setText(address);
		        }
		        // 加载图片
                if(!TextUtils.isEmpty(photoName)) {
                	 Networking.doImage("image", new ImageResponse(photo) {
		                    @Override
		                    public void onFinished(Bitmap content) {
		                    	doctor_photo_image.setImageBitmap(content); 
		                    }
		                },photo);
                }
			}
		},this.getIntent().getStringExtra("doctorId"));
    }
	//
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
                if(!TextUtils.isEmpty(commentsNum)){
                    user_comments_num_tv.setText("用户评价（"+commentsNum+"）");
                }
                JSONArray listResult = (JSONArray)result.get("records");
                if(null != listResult) {
                    for (IJSON item : listResult) {
                        JSONObject newJSONObject = (JSONObject) item;
                        DoctorCommentsModel doctorCommentsModel = new DoctorCommentsModel();
                        String imageUrl = "";
                        String photoName = "";
                        if(null==((JSONString) newJSONObject.get("username")).getValue()){
                        	return;
                        }                      
                        if(newJSONObject.get("portrait")!=null) {
                            imageUrl = ((JSONString) newJSONObject.get("portrait")).getValue();
                            photoName = Storage.getImageName(imageUrl);
                            doctorCommentsModel.imageUrl = photoName;
                        }
                        doctorCommentsModel.userName = ((JSONString) newJSONObject.get("username")).getValue();
                        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm");
                        long time = ((JSONNumber) newJSONObject.get("date")).longValue();
                        doctorCommentsModel.date = sdf.format(time);
                        doctorCommentsModel.content = ((JSONString) newJSONObject.get("content")).getValue();
                        doctorCommentsModel.score = ((JSONNumber) newJSONObject.get("score")).intValue();
                        dataList.add(doctorCommentsModel);
                        if (!TextUtils.isEmpty(imageUrl)&&Text.isBlank(imageUrl)) {
                            continue;
                        }
                        // 加载图片
                        if(!TextUtils.isEmpty(photoName)) {
                            Networking.doImage("image", new ImageResponse(photoName, dataList.size() - 1) {
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
                    page = thisPage + 1;
                }
            }
            },page,10,this.getIntent().getStringExtra("doctorId"),3);       
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
                viewHolder.user_photo_iv = (ImageView)convertView.findViewById(R.id.user_photo_iv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.user_name_tv.setText(data.userName);
            viewHolder.user_comments_date_tv.setText(data.date);
            viewHolder.user_comments_tv.setText(data.content);
            if(data.getPhoto()instanceof Bitmap){
            viewHolder.user_photo_iv.setImageBitmap(data.getPhoto());
            }
            return convertView;
        }

        class ViewHolder{
            public TextView user_name_tv;
            public TextView user_comments_date_tv;
            public TextView user_comments_tv;
            public ImageView user_photo_iv;
        }
    }
}
