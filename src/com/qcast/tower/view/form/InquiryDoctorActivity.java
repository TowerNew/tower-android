package com.qcast.tower.view.form;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.structure.DoctorModel;
import com.qcast.tower.framework.Storage;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;

import java.util.ArrayList;

/**
 * 
 */
public class InquiryDoctorActivity extends Activity {
	/**
	 * 支持的服务种类
	 */
	private String services = null;
	
    private ArrayList<DoctorModel> dataList;
    private DoctorAdapter adapter;
    private ListView doctorList;
    private Button inquiry_return_btn;
    private Button inquiry_bell_btn;
    private int doctorLevel;
    /**
     * 当前页面索引
     */
    protected int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorLevel = this.getIntent().getIntExtra("docLevel", 1);
        services = this.getIntent().getStringExtra("services");
        if(null == services) {
        	services = "inquiry";
        }
        this.setContentView(R.layout.activity_inquiry_doctor);
        doctorList = (ListView)findViewById(R.id.doctor_list);
        inquiry_return_btn = (Button)findViewById(R.id.inquiry_return_btn);
        inquiry_bell_btn = (Button)findViewById(R.id.inquiry_bell_btn);
        dataList = new ArrayList<DoctorModel>();
        adapter = new DoctorAdapter(this, dataList);
        doctorList.setAdapter(adapter);
        loadData();
        inquiry_bell_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InquiryDoctorActivity.this.finish();
            }
        });

        inquiry_return_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InquiryDoctorActivity.this.finish();
            }
        });

        doctorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DoctorModel doctorModel= (DoctorModel) parent.getAdapter().getItem(position);
                Intent intent = new Intent(InquiryDoctorActivity.this,InquiryDoctorDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("docDetail",doctorModel);
                intent.putExtras(bundle);
                InquiryDoctorActivity.this.startActivity(intent);
            }
        });
        doctorList.setOnScrollListener(new OnScrollListener() {    
	        boolean isLastRow = false;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {      
					loadData();
	                isLastRow = false;      
	            }
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 2) {      
	                isLastRow = true;      
	            }
			}
		});
    }

    private void loadData() {
        Networking.doCommand("doctorlist", new CommonResponse<String>(page) {
            @Override
            public void onFinished(String content) {
                if (Response.CODE_SUCCESS != code()) {
                    Toast.makeText(InquiryDoctorActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(InquiryDoctorActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray result = (JSONArray) resultObject.get("data");
                if(null == result) {
                	return;
                }
                int thisPage = (Integer) this.tag;
                if(page != thisPage) {
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
                    if(newJSONObject.get("bad") != null) {
                    	doctorModel.badCount = ((JSONNumber) newJSONObject.get("bad")).intValue();
                    }
                    if(newJSONObject.get("good")!=null) {
                    	doctorModel.goodCount = ((JSONNumber) newJSONObject.get("good")).intValue();
                    }
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
                page = thisPage + 1;
            }
        }, page, doctorLevel, services, Logic.regionId);
    }

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
                convertView = LayoutInflater.from(context).inflate(R.layout.inquriy_doctor_list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.doctor_des_tv = (TextView)convertView.findViewById(R.id.doctor_des_tv);
                viewHolder.doctor_name_tv = (TextView)convertView.findViewById(R.id.doctor_name_tv);
                viewHolder.doctor_title_tv = (TextView)convertView.findViewById(R.id.doctor_title_tv);
                viewHolder.doctor_type_tv = (TextView)convertView.findViewById(R.id.doctor_type_tv);
                viewHolder.isask_tv = (TextView)convertView.findViewById(R.id.isask_tv);
                viewHolder.isfamous_tv = (TextView)convertView.findViewById(R.id.isfamous_tv);
                viewHolder.ispre_tv = (TextView)convertView.findViewById(R.id.ispre_tv);
                viewHolder.doctor_photo_image = (ImageView)convertView.findViewById(R.id.doctor_photo_image);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.doctor_des_tv.setText(model.description);
            viewHolder.doctor_name_tv.setText(model.name);
            viewHolder.doctor_title_tv.setText(model.title);
            viewHolder.doctor_type_tv.setText(model.department);
            if(model.isAsk){
                viewHolder.isask_tv.setVisibility(View.VISIBLE);
            }else{
                viewHolder.isask_tv.setVisibility(View.GONE);
            }

            if(model.isFamous){
                viewHolder.isfamous_tv.setVisibility(View.VISIBLE);
            }else{
                viewHolder.isfamous_tv.setVisibility(View.INVISIBLE);
            }

            if(model.isPre){
                viewHolder.ispre_tv.setVisibility(View.VISIBLE);
            }else{
                viewHolder.ispre_tv.setVisibility(View.GONE);
            }
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
            public TextView isfamous_tv;
            public TextView isask_tv;
            public TextView ispre_tv;
            public TextView doctor_des_tv;
            public ImageView doctor_photo_image;
        }
    }

}
