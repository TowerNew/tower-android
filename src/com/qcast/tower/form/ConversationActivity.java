package com.qcast.tower.form;

import com.slfuture.carrie.base.type.core.ILink;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qcast.tower.R;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.structure.FamilyMember;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
    public void onCreate(Bundle savedInstanceState) {
		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), 
				conversationList,
                R.layout.listitem_family,
                new String[] {"relation"},
                new int[] { R.id.family_label_relation});
		listFamily.setAdapter(adapter);
		listFamily.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
    }

	@Override
    public void onResume() {
		conversationList.clear();
    	for(ILink<String, FamilyMember> link : Logic.familys) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		// map.put("icon", BitmapFactory.decodeResource(this.getResources(), R.drawable.conversation_icon_family));
    		map.put("relation", link.destination().relation);
    		map.put("id", link.destination().imUsername);
    	}
    	((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
    }
}
