package com.qcast.tower.view.form;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


@ResourceView(id = R.layout.activity_doctor_detail)
public class DoctorDetailActivity extends ActivityEx{
	
	@ResourceView(id = R.id.doctordetail_image_close)
	public ImageView imgClose;
	
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
	
	public void onCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
	}

	private void prepare() {
		
		 Intent intent = getIntent();
		 int id = intent.getIntExtra("id", 0);  
		
		
		 imgClose.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				DoctorDetailActivity.this.finish();
				
			}
		 });
		 doctorSet.setOnClickListener(new OnClickListener() {			
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
				                    public void onClick(DialogInterface arg0, int arg1) {/*  
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
				    				}, map.get("id"), Me.instance.token);  
				                    */}  
				                    
				                });  			                  
				                builder.create().show();  
				            }  				 
				        });  
			
		 
		
	}
		

}
