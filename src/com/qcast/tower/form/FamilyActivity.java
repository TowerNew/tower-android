package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.slfuture.pluto.communication.Host;
import com.qcast.tower.logic.Logic;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.qcast.tower.logic.structure.FamilyMember;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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

	@Override
	protected void onResume() {
		super.onResume();
		for(HashMap<String, Object> member : memberList) {
			if(null != member.get("hasmessage")) {
				if(Logic.messageFamily.contains((String) member.get("remoteId"))) {
					
				}
				else {
					member.remove("remoteId");
				}
			}
		}
		ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
		SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
		adapter.notifyDataSetChanged();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		dealReturn();
		dealAdd();
		dealMember();
	}
	
	/**
	 * 处理返回按钮
	 */
	public void dealReturn() {
		ImageButton button = (ImageButton) this.findViewById(R.id.family_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FamilyActivity.this.finish();
			}
		});
	}

	/**
	 * 处理添加按钮
	 */
	public void dealAdd() {
		Button btnAdd = (Button) findViewById(R.id.family_button_add);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FamilyActivity.this, FamilyEditActivity.class);
				intent.putExtra("userId", 0);
				FamilyActivity.this.startActivity(intent);
			}
		});
	}
	
	/**
	 * 处理成员
	 */
	private void dealMember() {
		ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
		SimpleAdapter listItemAdapter = new SimpleAdapter(FamilyActivity.this, memberList, R.layout.listview_family,
			new String[]{"caption", "hasmessage", "delete"}, 
	        new int[]{R.id.family_listview_caption, R.id.family_listview_hasmessage, R.id.family_listview_delete});
		listItemAdapter.setViewBinder(new ViewBinder() {
			@SuppressWarnings("deprecation")
			public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView) view;
                    imageView.setTag("");
                    imageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ListView listView = (ListView) v.getParent().getParent();
							for(int i = 0; i < listView.getChildCount(); i++) {
								if(v.getParent() == listView.getChildAt(i)) {
									HashMap<String, Object> map = memberList.get(i);
									String userId = (String) map.get("userId");
									Log.i("TOWER", "DELETE FAMILY");
									Host.doCommand("removefamily", new CommonResponse<String>() {
										@Override
										public void onFinished(String content) {
											if(Response.CODE_SUCCESS != code()) {
												Toast.makeText(FamilyActivity.this, "删除家庭成员失败", Toast.LENGTH_LONG).show();
												return;
											}
											JSONObject resultObject = JSONObject.convert(content);
											if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
												Toast.makeText(FamilyActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
												return;
											}
											Toast.makeText(FamilyActivity.this, "删除家庭成员成功", Toast.LENGTH_LONG).show();
											loadMember();
										}
									}, Logic.token, userId);
									break;
								}
							}
						}
					});
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
				Log.i("TOWER", "CLICK FAMILY");
				String remoteId = (String) memberList.get(index).get("remoteId");
				String remoteNickName = (String) memberList.get(index).get("remoteNickName");
				if(null == remoteId) {
					remoteId = "t2";
					remoteNickName = "t2";
				}
				Logic.messageFamily.clear();
				Intent intent = new Intent(FamilyActivity.this, GroupChatActivity.class);
				intent.putExtra("localId", Logic.imUsername);
				intent.putExtra("remoteId", remoteId);
				intent.putExtra("remoteName", remoteNickName);
				FamilyActivity.this.startActivity(intent);
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
				memberList.clear();
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
					if(null != newJSONObject.get("imUsername")) {
						member.imUsername = ((JSONString) newJSONObject.get("imUsername")).getValue();
					}
					if(null != member.userId) {
						Logic.familys.put(member.userId, member);
					}
					//
					HashMap<String, Object> memberMap = new HashMap<String, Object>();
					memberMap.put("userId", member.userId);
					if(!Text.isBlank(member.relation)) {
						memberMap.put("caption", member.relation);
					}
					else if(!Text.isBlank(member.name)) {
						memberMap.put("caption", member.name);
					}
					else {
						memberMap.put("caption", member.phone);
					}
					memberMap.put("remoteId", member.imUsername);
					if(null != member.imUsername) {
						if(Logic.messageFamily.contains(member.imUsername)) {
							memberMap.put("hasmessage", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.icon_hasmessage));
						}
					}
					if(null == member.relation) {
						memberMap.put("remoteNickName", member.name);
					}
					else {
						memberMap.put("remoteNickName", member.relation);
					}
					memberMap.put("delete", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.button_delete));
					memberList.add(memberMap);
				}
				ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
			}
		}, Logic.token);
	}
}
