package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.Program;
import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.user.Friend;
import com.qcast.tower.business.user.Relative;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pretty.im.Module;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.model.core.IEventable;
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
				String remoteId = (String) memberList.get(index).get("remoteId");
				Logic.messageFamily.clear();
				Me.instance.doChat(FamilyActivity.this, null, remoteId);
			}
		});
	}

	/**
	 * 加载成员
	 */
	public void loadMember() {
		Me.instance.refresh(FamilyActivity.this, new IEventable<Boolean>() {
			@Override
			public void on(Boolean result) {
				if(!result) {
					return;
				}
				refreshList();
			}
		});
	}

	/**
	 * 刷新列表
	 */
	public void refreshList() {
		memberList.clear();
		for(Friend friend : Me.instance.friends) {
			HashMap<String, Object> memberMap = new HashMap<String, Object>();
			memberMap.put("userId", friend.id);
			if(!Text.isBlank(friend.nickName)) {
				memberMap.put("caption", friend.nickName);
			}
			else {
				memberMap.put("caption", friend.phone);
			}
			memberMap.put("remoteId", friend.imId);
			if(Module.getUnreadMessageCount(friend.imId) > 0) {
				memberMap.put("hasmessage", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_hasmessage));
			}
			if(null == friend.nickName) {
				memberMap.put("remoteNickName", friend.nickName);
			}
			else {
				memberMap.put("remoteNickName", friend.phone);
			}
			memberMap.put("delete", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.button_delete));
			memberList.add(memberMap);
		}
		for(Relative relative : Me.instance.relatives) {
			HashMap<String, Object> memberMap = new HashMap<String, Object>();
			memberMap.put("userId", relative.id);
			if(!Text.isBlank(relative.nickName)) {
				memberMap.put("caption", relative.nickName);
			}
			else {
				memberMap.put("caption", relative.name);
			}
			memberMap.put("delete", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.button_delete));
			memberList.add(memberMap);
		}
		ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
		SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
		adapter.notifyDataSetChanged();
	}
}
