package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.Response;
import com.qcast.tower.logic.response.core.IResponse;
import com.qcast.tower.logic.structure.FamilyMember;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 家庭界面
 */
public class FamilyActivity extends Activity {
	/**
	 * 当前资讯列表
	 */
	protected ArrayList<HashMap<String, Object>> memberList = new ArrayList<HashMap<String, Object>>();
	
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "FamilyActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_family);
		// 界面处理
		prepare();
		//
		loadMember();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		dealAdd();
		dealMember();
	}

	/**
	 * 处理添加按钮
	 */
	public void dealAdd() {
		Button btnAdd = (Button) findViewById(R.id.family_button_add);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(FamilyActivity.this).setTitle("添加").setMessage("请选择家庭成员的添加方式").setNeutralButton("手机号码", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LayoutInflater inflater = getLayoutInflater();
						final View layout = inflater.inflate(R.layout.dialog_family, (ViewGroup) findViewById(R.id.familydialog_layout));
						Dialog inputDialog = new AlertDialog.Builder(FamilyActivity.this)
							.setTitle("请输入手机号码")
							.setView(layout)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
								public void onClick(DialogInterface dialog, int which) {
									EditText txtRelation = (EditText) layout.findViewById(R.id.familydialog_text_relation);
									final String relation = txtRelation.getText().toString();
									EditText txtPhone = (EditText) layout.findViewById(R.id.familydialog_text_phone);
									final String phone = txtPhone.getText().toString();
									Host.doCommand("editrelation", new CommonResponse<String>() {
										@Override
										public void onFinished(String content) {
											if(IResponse.CODE_SUCCESS != code()) {
												Toast.makeText(FamilyActivity.this, "网络错误", Toast.LENGTH_LONG).show();
												return;
											}
											JSONObject result = JSONObject.convert(content);
											if(null == result) {
												return;
											}
											if(200 == ((JSONNumber)(result.get("code"))).intValue()) {
												Toast.makeText(FamilyActivity.this, "操作成功，等待审核...", Toast.LENGTH_LONG).show();
												String userId = ((JSONString)(((JSONObject) result.get("data")).get("userGlobalId"))).getValue();
												Logic.familys.put(userId, new FamilyMember(userId, phone, relation));
											}
											else {
												Toast.makeText(FamilyActivity.this, ((JSONString) result.get("msg")).getValue(), Toast.LENGTH_LONG).show();
											}
										}
									}, Logic.token, "", relation, phone);
                                }
							}).setNegativeButton("取消", null).create();
						inputDialog.show();
					}
				}).setNegativeButton("身份证号", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(FamilyActivity.this, FamilyEditActivity.class);
						intent.putExtra("userId", 0);
						FamilyActivity.this.startActivity(intent);
					}
				}).setPositiveButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create();
				dialog.show();
			}
		});
	}
	
	/**
	 * 处理成员
	 */
	private void dealMember() {
		ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
		SimpleAdapter listItemAdapter = new SimpleAdapter(FamilyActivity.this, memberList, R.layout.listview_family,
			new String[]{"caption", "arrow"}, 
	        new int[]{R.id.family_listview_caption, R.id.family_listview_arrow});
		listItemAdapter.setViewBinder(new ViewBinder() {
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
		listview.setAdapter(listItemAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				//TODO:点击成员
            }
		});
	}
	
	/**
	 * 加载成员
	 */
	public void loadMember() {
		Host.doCommand("member", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(FamilyActivity.this, "加载家庭成员失败", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(FamilyActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				Logic.familys.clear();
				for(IJSON item : result) {
					JSONObject newJSONObject = (JSONObject) item;
					FamilyMember member = new FamilyMember();
					if(null != newJSONObject.get("userGlobalId")) {
						member.userId = ((JSONString) newJSONObject.get("userGlobalId")).getValue();
					}
					member.category = ((JSONNumber) newJSONObject.get("category")).intValue();
					member.status = ((JSONNumber) newJSONObject.get("status")).intValue();
					if(null != newJSONObject.get("phone")) {
						member.phone = ((JSONString) newJSONObject.get("phone")).getValue();
					}
					if(null != newJSONObject.get("relation")) {
						member.relation = ((JSONString) newJSONObject.get("relation")).getValue();
					}
					member.name = ((JSONString) newJSONObject.get("name")).getValue();
					if(null != newJSONObject.get("idnumber")) {
						member.idNumber = ((JSONString) newJSONObject.get("idnumber")).getValue();
					}
					if(null != newJSONObject.get("birthday")) {
						member.birthday = ((JSONString) newJSONObject.get("birthday")).getValue();
					}
					Logic.familys.put(member.userId, member);
					//
					HashMap<String, Object> memberMap = new HashMap<String, Object>();
					memberMap.put("caption", member.relation);
					memberMap.put("arrow", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.arrow));
					memberList.add(memberMap);
				}
				ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
			}
		}, Logic.token);
	}
}
