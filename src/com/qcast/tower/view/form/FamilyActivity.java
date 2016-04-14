package com.qcast.tower.view.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.user.Relative;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pretty.general.utility.GeneralHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 家庭界面
 */
@ResourceView(id = R.layout.activity_family)
public class FamilyActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.family_image_close)
	public ImageView imgClose;
	@ResourceView(id = R.id.family_image_add)
	public ImageView imgAdd;
	@ResourceView(id = R.id.family_list_member)
	public ListView listFamily;

	/**
	 * 当前成员列表
	 */
	protected ArrayList<HashMap<String, Object>> memberList = new ArrayList<HashMap<String, Object>>();


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		dealClose();
		dealMember();
	}

	/**
	 * 处理返回按钮
	 */
	public void dealClose() {
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FamilyActivity.this.finish();
			}
		});
		imgAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FamilyActivity.this, AddRelativeActivity.class);
				FamilyActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * 处理成员
	 */
	private void dealMember() {
		ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
		SimpleAdapter listItemAdapter = new SimpleAdapter(FamilyActivity.this, memberList, R.layout.listitem_family,
			new String[]{"name", "status"}, 
	        new int[]{R.id.listitem_family_label_name, R.id.listitem_family_label_status});
		listItemAdapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageBitmap((Bitmap) data);
                    return true;
                }
                else if(view instanceof TextView && data instanceof Integer) {
                	TextView textView = (TextView) view;
                	switch((Integer) data) {
                	case 1:
                    	textView.setBackgroundResource(R.drawable.button_red);
                    	textView.setText("待审核");
                		break;
                	case 2:
                    	textView.setBackgroundResource(R.drawable.button_green);
                    	textView.setText("已认证");
                		break;
                	case 3:
                    	textView.setBackgroundResource(R.drawable.button_red);
                    	textView.setText("被驳回");
                		break;
                	case 4:
                    	textView.setBackgroundResource(R.drawable.button_red);
                    	textView.setText("未认证");
                		break;
                	}
                    return true;
                }
                return false;
			}});
		listview.setAdapter(listItemAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, final int index, long arg3) {
				LinkedList<String> list = new LinkedList<String>();
				list.add("查看");
				list.add("删除");
				list.add("");
				list.add("取消");
				GeneralHelper.showSelector(FamilyActivity.this, new IEventable<Integer>() {
					@Override
					public void on(Integer position) {
						if(0 == position) {
							Intent intent = new Intent(FamilyActivity.this, AddRelativeActivity.class);
							intent.putExtra("userId", Me.instance.relatives.get(index).id);
							FamilyActivity.this.startActivity(intent);
						}
						else if(1 == position) {
							new AlertDialog.Builder(FamilyActivity.this).setTitle("确认删除吗？")  
							.setIcon(android.R.drawable.ic_dialog_info)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override  
									public void onClick(DialogInterface dialog, int which) {
										Networking.doCommand("RemoveOwner", new JSONResponse(FamilyActivity.this) {
											@Override
											public void onFinished(JSONVisitor content) {
												if(null == content || content.getInteger("code", 0) <= 0) {
													return;
												}
												Me.instance.refreshMember(FamilyActivity.this, new IEventable<Boolean>() {
													@Override
													public void on(Boolean result) {
														if(!result) {
															return;
														}
														FamilyActivity.this.refreshList();
													}
												});
											}
										}, Me.instance.token, Me.instance.relatives.get(index).id);
									}
							}).setNegativeButton("返回", new DialogInterface.OnClickListener() {
						        @Override  
						        public void onClick(DialogInterface dialog, int which) {}  
							}).show();
						}
					}
				}, list.toArray(new String[0]));
			}
		});
	}

	/**
	 * 刷新列表
	 */
	public void refreshList() {
		memberList.clear();
		for(Relative relative : Me.instance.relatives) {
			HashMap<String, Object> memberMap = new HashMap<String, Object>();
			memberMap.put("userId", relative.id);
			if(Text.isBlank(relative.nickname())) {
				memberMap.put("name", relative.name);
			}
			else {
				memberMap.put("name", relative.nickname());
			}
			memberMap.put("status", relative.status);
			memberList.add(memberMap);
		}
		SimpleAdapter adapter = (SimpleAdapter) listFamily.getAdapter();
		adapter.notifyDataSetChanged();
	}
}