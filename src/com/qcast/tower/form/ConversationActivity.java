package com.qcast.tower.form;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.user.Friend;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

@ResourceView(id = R.layout.activity_conversation)
public class ConversationActivity extends FragmentEx {
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
		viewDoctor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance || null == Me.instance.doctor) {
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
				if(Me.instance.friends.size() <= position) {
					return;
				}
				Me.instance.doChat(ConversationActivity.this.getActivity(), null, Me.instance.friends.get(position).imId);
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
    	for(Friend friend : Me.instance.friends) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", friend.id);
    		map.put("name", friend.nickName);
    		map.put("imId", friend.imId);
    		conversationList.add(map);
    	}
    	((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
    }
}
