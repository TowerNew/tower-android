package com.qcast.tower.view.form;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 评价窗口
 */
@ResourceView(id = R.layout.activity_comment)
public class CommentActivity extends ActivityEx {
	@ResourceView(id = R.id.comment_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.comment_button_confirm)
	public Button btnConfirm;
	@ResourceView(id = R.id.comment_image_verygood)
	public ImageView imgVerygood;
	@ResourceView(id = R.id.comment_image_good)
	public ImageView imgGood;
	@ResourceView(id = R.id.comment_image_bad)
	public ImageView imgBad;
	
	@ResourceView(id = R.id.comment_image_attitude1)
	public ImageView imgAttitude1;
	@ResourceView(id = R.id.comment_image_attitude2)
	public ImageView imgAttitude2;
	@ResourceView(id = R.id.comment_image_attitude3)
	public ImageView imgAttitude3;
	@ResourceView(id = R.id.comment_image_attitude4)
	public ImageView imgAttitude4;
	@ResourceView(id = R.id.comment_image_attitude5)
	public ImageView imgAttitude5;

	@ResourceView(id = R.id.comment_image_service1)
	public ImageView imgService1;
	@ResourceView(id = R.id.comment_image_service2)
	public ImageView imgService2;
	@ResourceView(id = R.id.comment_image_service3)
	public ImageView imgService3;
	@ResourceView(id = R.id.comment_image_service4)
	public ImageView imgService4;
	@ResourceView(id = R.id.comment_image_service5)
	public ImageView imgService5;

	@ResourceView(id = R.id.comment_image_skill1)
	public ImageView imgSkill1;
	@ResourceView(id = R.id.comment_image_skill2)
	public ImageView imgSkill2;
	@ResourceView(id = R.id.comment_image_skill3)
	public ImageView imgSkill3;
	@ResourceView(id = R.id.comment_image_skill4)
	public ImageView imgSkill4;
	@ResourceView(id = R.id.comment_image_skill5)
	public ImageView imgSkill5;
	
	@ResourceView(id = R.id.comment_text_content)
	public EditText txtContent;

	/**
	 * 医生ID
	 */
	private String doctorId;
	/**
	 * 选择，1：非常满意，2：满意，3：不满意
	 */
	private boolean choice;
	/**
	 * 态度，0-5星
	 */
	private int attitudeLevel;
	/**
	 * 服务，0-5星
	 */
	private int serviceLevel;
	/**
	 * 专业，0-5星
	 */
	private int skillLevel;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		prepare();
	}

	/**
	 * 准备
	 */
	public void prepare() {
		doctorId = this.getIntent().getStringExtra("doctorId");
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentActivity.this.finish();
			}
		});
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Networking.doCommand("comment", new JSONResponse(CommentActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content) {
							return;
						}
						
						if(content.getInteger("code", 0) > 0) {
							CommentActivity.this.finish();
						}
					}
				}, doctorId, choice, attitudeLevel, serviceLevel, skillLevel, txtContent.getText().toString(), Logic.token);
			}
		});
		imgVerygood.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				choice = true;
				//
				imgVerygood.setImageResource(R.drawable.verygood_checked);
				imgGood.setImageResource(R.drawable.good_uncheck);
				imgBad.setImageResource(R.drawable.bad_uncheck);
			}
		});
		imgGood.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				choice = true;
				//
				imgVerygood.setImageResource(R.drawable.verygood_uncheck);
				imgGood.setImageResource(R.drawable.good_checked);
				imgBad.setImageResource(R.drawable.bad_uncheck);
			}
		});
		imgBad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				choice = false;
				//
				imgVerygood.setImageResource(R.drawable.verygood_uncheck);
				imgGood.setImageResource(R.drawable.good_uncheck);
				imgBad.setImageResource(R.drawable.bad_checked);
			}
		});
		//
		imgAttitude1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgAttitude1.setImageResource(R.drawable.star_selected);
				imgAttitude2.setImageResource(R.drawable.star_unselected);
				imgAttitude3.setImageResource(R.drawable.star_unselected);
				imgAttitude4.setImageResource(R.drawable.star_unselected);
				imgAttitude5.setImageResource(R.drawable.star_unselected);
				attitudeLevel = 1;
			}
		});
		imgAttitude2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgAttitude1.setImageResource(R.drawable.star_selected);
				imgAttitude2.setImageResource(R.drawable.star_selected);
				imgAttitude3.setImageResource(R.drawable.star_unselected);
				imgAttitude4.setImageResource(R.drawable.star_unselected);
				imgAttitude5.setImageResource(R.drawable.star_unselected);
				attitudeLevel = 2;
			}
		});
		imgAttitude3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgAttitude1.setImageResource(R.drawable.star_selected);
				imgAttitude2.setImageResource(R.drawable.star_selected);
				imgAttitude3.setImageResource(R.drawable.star_selected);
				imgAttitude4.setImageResource(R.drawable.star_unselected);
				imgAttitude5.setImageResource(R.drawable.star_unselected);
				attitudeLevel = 3;
			}
		});
		imgAttitude4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgAttitude1.setImageResource(R.drawable.star_selected);
				imgAttitude2.setImageResource(R.drawable.star_selected);
				imgAttitude3.setImageResource(R.drawable.star_selected);
				imgAttitude4.setImageResource(R.drawable.star_selected);
				imgAttitude5.setImageResource(R.drawable.star_unselected);
				attitudeLevel = 4;
			}
		});
		imgAttitude5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgAttitude1.setImageResource(R.drawable.star_selected);
				imgAttitude2.setImageResource(R.drawable.star_selected);
				imgAttitude3.setImageResource(R.drawable.star_selected);
				imgAttitude4.setImageResource(R.drawable.star_selected);
				imgAttitude5.setImageResource(R.drawable.star_selected);
				attitudeLevel = 5;
			}
		});
		//
		imgService1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgService1.setImageResource(R.drawable.star_selected);
				imgService2.setImageResource(R.drawable.star_unselected);
				imgService3.setImageResource(R.drawable.star_unselected);
				imgService4.setImageResource(R.drawable.star_unselected);
				imgService5.setImageResource(R.drawable.star_unselected);
				serviceLevel = 1;
			}
		});
		imgService2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgService1.setImageResource(R.drawable.star_selected);
				imgService2.setImageResource(R.drawable.star_selected);
				imgService3.setImageResource(R.drawable.star_unselected);
				imgService4.setImageResource(R.drawable.star_unselected);
				imgService5.setImageResource(R.drawable.star_unselected);
				serviceLevel = 2;
			}
		});
		imgService3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgService1.setImageResource(R.drawable.star_selected);
				imgService2.setImageResource(R.drawable.star_selected);
				imgService3.setImageResource(R.drawable.star_selected);
				imgService4.setImageResource(R.drawable.star_unselected);
				imgService5.setImageResource(R.drawable.star_unselected);
				serviceLevel = 3;
			}
		});
		imgService4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgService1.setImageResource(R.drawable.star_selected);
				imgService2.setImageResource(R.drawable.star_selected);
				imgService3.setImageResource(R.drawable.star_selected);
				imgService4.setImageResource(R.drawable.star_selected);
				imgService5.setImageResource(R.drawable.star_unselected);
				serviceLevel = 4;
			}
		});
		imgService5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgService1.setImageResource(R.drawable.star_selected);
				imgService2.setImageResource(R.drawable.star_selected);
				imgService3.setImageResource(R.drawable.star_selected);
				imgService4.setImageResource(R.drawable.star_selected);
				imgService5.setImageResource(R.drawable.star_selected);
				serviceLevel = 5;
			}
		});
		//
		imgSkill1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgSkill1.setImageResource(R.drawable.star_selected);
				imgSkill2.setImageResource(R.drawable.star_unselected);
				imgSkill3.setImageResource(R.drawable.star_unselected);
				imgSkill4.setImageResource(R.drawable.star_unselected);
				imgSkill5.setImageResource(R.drawable.star_unselected);
				skillLevel = 1;
			}
		});
		imgSkill2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgSkill1.setImageResource(R.drawable.star_selected);
				imgSkill2.setImageResource(R.drawable.star_selected);
				imgSkill3.setImageResource(R.drawable.star_unselected);
				imgSkill4.setImageResource(R.drawable.star_unselected);
				imgSkill5.setImageResource(R.drawable.star_unselected);
				skillLevel = 2;
			}
		});
		imgSkill3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgSkill1.setImageResource(R.drawable.star_selected);
				imgSkill2.setImageResource(R.drawable.star_selected);
				imgSkill3.setImageResource(R.drawable.star_selected);
				imgSkill4.setImageResource(R.drawable.star_unselected);
				imgSkill5.setImageResource(R.drawable.star_unselected);
				skillLevel = 3;
			}
		});
		imgSkill4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgSkill1.setImageResource(R.drawable.star_selected);
				imgSkill2.setImageResource(R.drawable.star_selected);
				imgSkill3.setImageResource(R.drawable.star_selected);
				imgSkill4.setImageResource(R.drawable.star_selected);
				imgSkill5.setImageResource(R.drawable.star_unselected);
				skillLevel = 4;
			}
		});
		imgSkill5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgSkill1.setImageResource(R.drawable.star_selected);
				imgSkill2.setImageResource(R.drawable.star_selected);
				imgSkill3.setImageResource(R.drawable.star_selected);
				imgSkill4.setImageResource(R.drawable.star_selected);
				imgSkill5.setImageResource(R.drawable.star_selected);
				skillLevel = 5;
			}
		});
	}
}
