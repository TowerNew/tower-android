package com.qcast.tower.view.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.qcast.tower.business.structure.DoctorModel;
import com.qcast.tower.framework.Storage;
import com.qcast.tower.view.form.InquiryDoctorActivity.DoctorAdapter;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;

/**
 * 选择私人医生
 */
@ResourceView(id = R.layout.activity_selectdoctor)
public class SelectDoctorActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.selectdoctor_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.selectdoctor_list)
	public ListView listDoctor;

	/**
	 * 医生面板列表
	 */
	/*private LinkedList<HashMap<String, Object>> dataList = new LinkedList<HashMap<String, Object>>();*/
	private ArrayList<DoctorModel> dataList = new ArrayList<DoctorModel>();
	/**
	 * 当前被选中的索引
	 */
	private int current = -1;
	private DoctorModel doctorModel;
	private DoctorAdapter adapter;
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null == Me.instance) {
			return;
		}
		if(null == Profile.instance().region) {
			this.finish();
			return;
		}
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectDoctorActivity.this.finish();
			}
		});
		

		/*SimpleAdapter listAdapter = new SimpleAdapter(SelectDoctorActivity.this, dataList, R.layout.listitem_selectdoctor,
				new String[]{"name", "photo", "department", "title"}, 
				new int[]{R.id.selectdoctor_label_name, R.id.selectdoctor_image_photo, R.id.selectdoctor_label_department, R.id.selectdoctor_label_title});
		listAdapter.setViewBinder(new ViewBinder() {
			@SuppressWarnings("deprecation")
			public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView)view;
                    Bitmap bitmap = (Bitmap) data;
                    imageView.setImageDrawable(new BitmapDrawable(bitmap));
                    return true;
                }
                return false;
            }
        });
		listDoctor.setAdapter(listAdapter);*/
		
		//
		dataList = new ArrayList<DoctorModel>();       
		adapter = new DoctorAdapter(this, dataList);
		listDoctor.setAdapter(adapter);
		loadData();		
		//
		listDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DoctorModel doctorModel= (DoctorModel) parent.getAdapter().getItem(position);
                Intent intent = new Intent(SelectDoctorActivity.this,DoctorDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("docDetail",doctorModel);
                intent.putExtras(bundle);
                SelectDoctorActivity.this.startActivity(intent);
            }
        });
	}
//
	private void loadData() {	
		 Networking.doCommand("doctorlist", new CommonResponse<String>() {
	            @Override
	            public void onFinished(String content) {
	                if (Response.CODE_SUCCESS != code()) {
	                    Toast.makeText(SelectDoctorActivity.this, "网络问题", Toast.LENGTH_LONG).show();
	                    return;
	                }
	                JSONObject resultObject = JSONObject.convert(content);
	                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
	                    Toast.makeText(SelectDoctorActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
	                    return;
	                }
	                JSONArray result = (JSONArray) resultObject.get("data");
	                if(null == result) {
	                	return;
	                }
	           
	                for (IJSON item : result) {
	                    JSONObject newJSONObject = (JSONObject) item;
	                    DoctorModel doctorModel = new DoctorModel();
	                    String imageUrl="";
	                    String photoName="";
	                    if(newJSONObject.get("photo")!=null) {
	                        imageUrl = ((JSONString) newJSONObject.get("photo")).getValue();
	                        photoName = Storage.getImageName(imageUrl);
	                        doctorModel.imageUrl = photoName;
	                    }
	                    doctorModel.name = ((JSONString) newJSONObject.get("name")).getValue();
	                    if(null==newJSONObject.get("title")){
	                    	return;
	                    }
	                    doctorModel.title = ((JSONString) newJSONObject.get("title")).getValue();
	                    doctorModel.department = ((JSONString) newJSONObject.get("department")).getValue();
	                    doctorModel.description = ((JSONString) newJSONObject.get("description")).getValue();
	                    doctorModel.level =((JSONNumber) newJSONObject.get("level")).intValue();
	                    doctorModel.doctorId = ((JSONString) newJSONObject.get("userGlobalId")).getValue();
	                    if(null == newJSONObject.get("resume")) {
	                    	doctorModel.resume = "暂无简历信息";
	                    }
	                    else {
	                        doctorModel.resume = ((JSONString) newJSONObject.get("resume")).getValue();
	                    }
	                    doctorModel.photoName = photoName;	                   
	                    JSONArray servicesArray = (JSONArray) newJSONObject.get("services");
	                    doctorModel.services = new ArrayList<String>();
	                    for(int i=0;i<servicesArray.size();i++){
	                        doctorModel.services.add(((JSONString)servicesArray.get(i)).getValue());
	                    }
	                    for(String s : doctorModel.services){
	                        if(s.equals("reserve")) {
	                            doctorModel.isPre = true;
	                            break;
	                        }
	                    }
	                    for(String s : doctorModel.services){
	                        if(s.equals("inquiry")) {
	                            doctorModel.isAsk = true;
	                            break;
	                        }
	                    }
	                    if(doctorModel.level==1) {
	                        doctorModel.isFamous = true;
	                    }else{
	                        doctorModel.isFamous = false;
	                    }
	                    dataList.add(doctorModel);
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
	            }
	        }, 1, 0, "privateDoctor", Profile.instance().region.id);
	    
	}
	//
	 public class DoctorAdapter extends BaseAdapter{
	        Context context;
	        ArrayList<DoctorModel> data;

	        public DoctorAdapter(Context context, ArrayList<DoctorModel> data){
	            this.context = context;
	            this.data = data;
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
	            DoctorModel model = data.get(position);
	            ViewHolder viewHolder;
	            if(convertView==null){
	                convertView = LayoutInflater.from(context).inflate(R.layout.listitem_selectdoctor,null);
	                viewHolder = new ViewHolder();		               
	                viewHolder.doctor_name_tv = (TextView)convertView.findViewById(R.id.selectdoctor_label_name);
	                viewHolder.doctor_title_tv = (TextView)convertView.findViewById(R.id.selectdoctor_label_title);
	                viewHolder.doctor_type_tv = (TextView)convertView.findViewById(R.id.selectdoctor_label_department);
	                viewHolder.doctor_photo_image = (ImageView)convertView.findViewById(R.id.selectdoctor_image_photo);
	                convertView.setTag(viewHolder);
	            }else{
	                viewHolder = (ViewHolder)convertView.getTag();
	            }
	            viewHolder.doctor_name_tv.setText(model.name);
	            viewHolder.doctor_title_tv.setText(model.title);
	            viewHolder.doctor_type_tv.setText(model.department);		          
	            if(model.getPhoto() instanceof Bitmap){
	                viewHolder.doctor_photo_image.setImageBitmap(model.getPhoto());
	            }
	            else {
	            	viewHolder.doctor_photo_image.setImageResource(R.drawable.askdoctor_chat_other);
	            }
	            return convertView;
	        }
	        class ViewHolder{
	            public TextView doctor_name_tv;
	            public TextView doctor_type_tv;
	            public TextView doctor_title_tv;		       
	            public ImageView doctor_photo_image;
	        }
	    }
	}
		


