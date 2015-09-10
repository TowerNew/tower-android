package com.qcast.tower.form;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.core.IResponse;
import com.qcast.tower.logic.structure.FamilyMember;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;

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
		Button btnConfirm = (Button) findViewById(R.id.familyedit_button_confirm);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText txtRelation = (EditText) findViewById(R.id.familyedit_text_relation);
				EditText txtName = (EditText) findViewById(R.id.familyedit_text_name);
				EditText txtIdNumber = (EditText) findViewById(R.id.familyedit_text_idnumber);
				EditText txtBirthday = (EditText) findViewById(R.id.familyedit_text_birthday);
				member.relation = txtRelation.getText().toString();
				member.name = txtName.getText().toString();
				member.idNumber = txtIdNumber.getText().toString();
				member.birthday = txtBirthday.getText().toString();
				member.category = FamilyMember.CATEGORY_OWNER;
				member.status = FamilyMember.STATUS_UNCONFIRM;
				int mode = 1;
				if(null == userId) {
					mode = 0;
				}
				Host.doCommand("editowner", new CommonResponse<String>() {
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
						if(200 == ((JSONNumber)(result.get("code"))).intValue()) {
							Toast.makeText(FamilyEditActivity.this, "操作成功，等待审核...", Toast.LENGTH_LONG).show();
							userId = ((JSONString)(((JSONObject) result.get("data")).get("userGlobalId"))).getValue();
							member.userId = userId;
							Logic.familys.put(member.userId, member);
							FamilyEditActivity.this.finish();
						}
						else {
							Toast.makeText(FamilyEditActivity.this, ((JSONString) result.get("msg")).getValue(), Toast.LENGTH_LONG).show();
						}
					}
				}, Logic.token, userId, mode, member.relation, member.name, member.idNumber, member.birthday);
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
		EditText txtAge = (EditText) findViewById(R.id.familyedit_text_birthday);
		txtAge.setText(member.birthday);
	}
}
