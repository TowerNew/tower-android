package com.qcast.tower.view.form;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.general.utility.GeneralHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.structure.IM;
import com.qcast.tower.business.user.Friend;
import com.qcast.tower.business.user.User;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

@ResourceView(id = R.layout.activity_conversation)
public class ConversationActivity extends FragmentEx {
	@ResourceView(id = R.id.conversation_image_add)
	public ImageView imgAdd;
	@ResourceView(id = R.id.conversation_layout_doctor)
	public ViewGroup viewDoctor;
	@ResourceView(id = R.id.conversation_list_family)
	public ListView listFamily;

	/**
	 * 数据列表
	 */
	private List<Map<String, Object>> conversationList = new ArrayList<Map<String, Object>>();


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//
		imgAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog alertDialog = new AlertDialog.Builder(ConversationActivity.this.getActivity()).create();
				alertDialog.show();
				Window window = alertDialog.getWindow();
				WindowManager.LayoutParams layoutParams = window.getAttributes();
				layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
				window.setGravity(Gravity.BOTTOM);
				window.setAttributes(layoutParams);
				window.setContentView(R.layout.dialog_addcontact);
				TextView labelCancel = (TextView) window.findViewById(R.id.addcontact_label_cancel);
				labelCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						alertDialog.cancel();
					}
				});
				TextView labRelative = (TextView) window.findViewById(R.id.addcontact_label_relative);
				labRelative.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ConversationActivity.this.getActivity(), AddRelativeActivity.class);
						ConversationActivity.this.getActivity().startActivity(intent);
						alertDialog.hide();
					}
				});
				TextView labFriend = (TextView) window.findViewById(R.id.addcontact_label_friend);
				labFriend.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ConversationActivity.this.getActivity(), AddFriendActivity.class);
						ConversationActivity.this.getActivity().startActivity(intent);
						alertDialog.hide();
					}
				});
			}
		});
		viewDoctor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance || null == Me.instance.doctor) {
					Intent intent = new Intent(ConversationActivity.this.getActivity(), SelectDoctorActivity.class);
					ConversationActivity.this.getActivity().startActivity(intent);
					return;
				}
				Me.instance.doChat(ConversationActivity.this.getActivity(), null, Me.instance.doctor.imId);
			}
		});
		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), 
				conversationList,
                R.layout.listitem_family,
                new String[] {"name"},
                new int[] { R.id.family_label_name});
		listFamily.setAdapter(adapter);
		listFamily.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(0 == position) {
					pop(Me.instance);
				}
				else if(Me.instance.friends.size() < position) {
					return;
				}
				pop(Me.instance.friends.get(position - 1));
			}
		});
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {  
			    if(view instanceof ImageView && data instanceof Bitmap) {
			        ((ImageView) view).setImageBitmap((Bitmap) data);
			        return true;
			    }
		        return false;
			}
		});
    }

	@Override
    public void onResume() {
		super.onResume();
		if(null == Me.instance) {
			((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
			return;
		}
		conversationList.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", Me.instance.id);
		map.put("name", "我");
		conversationList.add(map);
    	for(Friend friend : Me.instance.friends) {
    		map = new HashMap<String, Object>();
    		map.put("id", friend.id);
    		if(null != friend.relation) {
        		map.put("name", friend.relation);
    		}
    		else if(null != friend.nickname) {
        		map.put("name", friend.nickname);
    		}
    		conversationList.add(map);
    	}
    	((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
    }

	/**
	 * 弹出菜单
	 * 
	 * @param user 用户
	 */
	private void pop(final User user) {
		ArrayList<String> list = new ArrayList<String>();
		for(IM item : user.im) {
			if(item.type.equals(IM.TYPE_PHONE)) {
				continue;
			}
			list.add(item.title);
		}
		if(0 == list.size()) {
			return;
		}
		list.add(null);
		list.add("取  消");
		GeneralHelper.showSelector(ConversationActivity.this.getActivity(), new IEventable<Integer>() {
			@Override
			public void on(Integer index) {
				int i = -1;
				for(IM item : user.im) {
					if(item.type.equals(IM.TYPE_PHONE)) {
						continue;
					}
					i++;
					if(index == i) {
						Me.instance.doChat(ConversationActivity.this.getActivity(), null, item.imId);
					}
				}
			}
		}, list.toArray(new String[0]));
	}
}