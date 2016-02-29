package com.qcast.tower.view.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.user.Relative;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pretty.general.utility.GeneralHelper;

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

/**
 * 家庭界面
 */
@ResourceView(id = R.layout.activity_family)
public class FamilyActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.family_image_close)
	public ImageView imgClose;
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
	}

	/**
	 * 处理成员
	 */
	private void dealMember() {
		ListView listview = (ListView) FamilyActivity.this.findViewById(R.id.family_list_member);
		SimpleAdapter listItemAdapter = new SimpleAdapter(FamilyActivity.this, memberList, R.layout.listitem_family,
			new String[]{"name"}, 
	        new int[]{R.id.listitem_family_label_name});
		listItemAdapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
		listview.setAdapter(listItemAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, final int index, long arg3) {
				LinkedList<String> list = new LinkedList<String>();
				list.add("编辑");
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
							Host.doCommand("removefamily", new JSONResponse(FamilyActivity.this) {
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
			memberMap.put("name", relative.nickname());
			memberList.add(memberMap);
		}
		SimpleAdapter adapter = (SimpleAdapter) listFamily.getAdapter();
		adapter.notifyDataSetChanged();
	}
}
