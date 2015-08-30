package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.qcast.tower.R;
import com.qcast.tower.logic.Logic;
import com.slfuture.carrie.base.text.Text;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 用户界面
 */
public class UserActivity extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_user, container, true);
	}

	@Override
	public void onStart() {
		super.onStart();
		//
		prepare();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		TextView txtCaption = (TextView) this.getActivity().findViewById(R.id.user_text_caption);
		if(Text.isBlank(Logic.name)) {
			return;
		}
		txtCaption.setText(Logic.name);
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		ImageButton btnPhoto = (ImageButton) this.getActivity().findViewById(R.id.user_button_photo);
		btnPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
				UserActivity.this.startActivity(intent);
			}
		});
		//
		dealList();
	}
	
	/**
	 * 处理咨询
	 */
	private void dealList() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = null;
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_info));
		map.put("caption", "我的信息");
		map.put("arrow", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.arrow));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_profile));
		map.put("caption", "健康档案");
		map.put("arrow", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.arrow));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_family));
		map.put("caption", "我的家庭");
		map.put("arrow", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.arrow));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_product));
		map.put("caption", "产品包");
		map.put("arrow", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.arrow));
		list.add(map);		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_packet));
		map.put("caption", "我的钱包");
		map.put("arrow", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.arrow));
		list.add(map);
		//
		ListView listview = (ListView) this.getActivity().findViewById(R.id.user_list);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this.getActivity(), list, R.layout.listview_user,
			new String[]{"icon", "caption", "arrow"}, 
	        new int[]{R.id.user_listview_icon, R.id.user_listview_caption, R.id.user_listview_arrow});
		listItemAdapter.setViewBinder(new ViewBinder() {
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
				if(2 != index) {
					return;
				}
				Intent intent = new Intent(UserActivity.this.getActivity(), FamilyActivity.class);
				UserActivity.this.startActivity(intent);
            }
		});
	}
}
