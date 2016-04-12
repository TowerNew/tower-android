package com.qcast.tower.view.form;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.structure.FamilyMember;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.core.IResponse;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.text.Text;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 编辑家庭成员
 */
public class FamilyEditActivity extends Activity {
	/**
	 * 成员标志
	 */
	public String userId = null;
	/**
	 * 家庭成员数据
	 */
	public FamilyMember member = new FamilyMember();
	/**
	 * 当前Tab
	 */
	public int tab = 0;


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "FamilyEditActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_familyedit);
		// 界面处理
		prepare();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		//
		Button btnTab = (Button) findViewById(R.id.familyedit_button_relation);
		btnTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTab(0);
			}
		});
		btnTab = (Button) findViewById(R.id.familyedit_button_owner);
		btnTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTab(1);
			}
		});
		selectTab(0);
		//
		Button btnConfirm = (Button) findViewById(R.id.familyedit_button_confirm);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(0 == tab) {
					EditText txtRelation = (EditText) findViewById(R.id.familyedit_text_relation);
					member.relation = txtRelation.getText().toString();
					EditText txtPhone = (EditText) findViewById(R.id.familyedit_text_phone);
					member.phone = txtPhone.getText().toString();
					Networking.doCommand("editrelation", new CommonResponse<String>() {
						@Override
						public void onFinished(String content) {
							if(IResponse.CODE_SUCCESS != code()) {
								Toast.makeText(FamilyEditActivity.this, "网络错误", Toast.LENGTH_LONG).show();
								return;
							}
							JSONObject result = JSONObject.convert(content);
							if(null == result) {
								return;
							}
							if(((JSONNumber)(result.get("code"))).intValue() > 0) {
								Toast.makeText(FamilyEditActivity.this, "操作成功，等待审核...", Toast.LENGTH_LONG).show();
								if(null != result.get("data")) {
									String userId = ((JSONString)(((JSONObject) result.get("data")).get("userGlobalId"))).getValue();
									Logic.familys.put(userId, new FamilyMember(userId, member.phone, member.relation));
								}
								FamilyEditActivity.this.finish();
								return;
							}
							else {
								Toast.makeText(FamilyEditActivity.this, ((JSONString) result.get("msg")).getValue(), Toast.LENGTH_LONG).show();
							}
						}
					}, Logic.token, "", txtRelation.getText().toString(), txtPhone.getText().toString());
					return;
				}
				EditText txtRelation = (EditText) findViewById(R.id.familyedit_text_relation);
				EditText txtName = (EditText) findViewById(R.id.familyedit_text_name);
				EditText txtIdNumber = (EditText) findViewById(R.id.familyedit_text_idnumber);
				member.relation = txtRelation.getText().toString();
				if(Text.isBlank(txtRelation.getText().toString())) {
					Toast.makeText(FamilyEditActivity.this, "请填写关系", Toast.LENGTH_LONG).show();
					return;
				}
				member.name = txtName.getText().toString();
				if(Text.isBlank(txtName.getText().toString())) {
					Toast.makeText(FamilyEditActivity.this, "请填写姓名", Toast.LENGTH_LONG).show();
					return;
				}
				member.idNumber = txtIdNumber.getText().toString();
				Pattern pattern = Pattern.compile("^(^\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$");
				Matcher matcher = pattern.matcher(txtIdNumber.getText().toString());
				if(!matcher.matches()) {
					Toast.makeText(FamilyEditActivity.this, "身份证号码格式不正确", Toast.LENGTH_LONG).show();
					return;
				}					
				member.category = FamilyMember.CATEGORY_OWNER;
				member.status = FamilyMember.STATUS_UNCONFIRM;
				int mode = 1;
				if(null == userId) {
					mode = 0;
				}
				Networking.doCommand("editowner", new CommonResponse<String>() {
					@Override
					public void onFinished(String content) {
						if(IResponse.CODE_SUCCESS != code()) {
							Toast.makeText(FamilyEditActivity.this, "网络错误", Toast.LENGTH_LONG).show();
							return;
						}
						JSONObject result = JSONObject.convert(content);
						if(null == result) {
							return;
						}
						if(((JSONNumber)(result.get("code"))).intValue() > 0) {
							Toast.makeText(FamilyEditActivity.this, "操作成功，等待审核...", Toast.LENGTH_LONG).show();
//							userId = ((JSONString)(((JSONObject) result.get("data")).get("userGlobalId"))).getValue();
//							member.userId = userId;
//							Logic.familys.put(member.userId, member);
							FamilyEditActivity.this.finish();
						}
						else {
							Toast.makeText(FamilyEditActivity.this, ((JSONString) result.get("msg")).getValue(), Toast.LENGTH_LONG).show();
						}
					}
				}, Logic.token, userId, mode, member.relation, member.name, member.idNumber);
			}
    	});
		// 数据处理
		userId = this.getIntent().getStringExtra("userId");
		if(null == userId) {
			// 添加
			return;
		}
		member = Logic.familys.get(userId);
		if(null == member) {
			Toast.makeText(this, "数据错误", Toast.LENGTH_LONG).show();
			return;
		}
		// 界面渲染
		EditText txtRelation = (EditText) findViewById(R.id.familyedit_text_relation);
		txtRelation.setText(member.relation);
		EditText txtName = (EditText) findViewById(R.id.familyedit_text_name);
		txtName.setText(member.name);
		EditText txtIdNumber = (EditText) findViewById(R.id.familyedit_text_idnumber);
		txtIdNumber.setText(member.idNumber);
	}
	
	/**
	 * 选择指定的选项卡
	 * 
	 * @param which 0/1
	 */
	public void selectTab(int which) {
		tab = which;
		if(0 == which) {
			findViewById(R.id.familyedit_button_relation).setBackgroundResource(R.color.white);
			findViewById(R.id.familyedit_button_owner).setBackgroundResource(R.color.lightgrey);
			
			findViewById(R.id.familyedit_item_name).setVisibility(View.GONE);
			findViewById(R.id.familyedit_divide2).setVisibility(View.GONE);
			findViewById(R.id.familyedit_item_idnumber).setVisibility(View.GONE);
			findViewById(R.id.familyedit_divide3).setVisibility(View.GONE);
			findViewById(R.id.familyedit_item_phone).setVisibility(View.VISIBLE);
		}
		else {
			findViewById(R.id.familyedit_button_relation).setBackgroundResource(R.color.lightgrey);
			findViewById(R.id.familyedit_button_owner).setBackgroundResource(R.color.white);
			
			findViewById(R.id.familyedit_item_name).setVisibility(View.VISIBLE);
			findViewById(R.id.familyedit_divide2).setVisibility(View.VISIBLE);
			findViewById(R.id.familyedit_item_idnumber).setVisibility(View.VISIBLE);
			findViewById(R.id.familyedit_divide3).setVisibility(View.VISIBLE);
			findViewById(R.id.familyedit_item_phone).setVisibility(View.GONE);
		}
	}
}
