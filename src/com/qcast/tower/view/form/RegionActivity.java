package com.qcast.tower.view.form;

import java.io.IOException;
import java.util.ArrayList;

import com.qcast.tower.R;
import com.qcast.tower.business.Profile;
import com.qcast.tower.business.structure.City;
import com.qcast.tower.business.structure.Region;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.type.core.ILink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 城市选择页
 */
@ResourceView(id = R.layout.activity_region)
public class RegionActivity extends ActivityEx {
	/**
	 * 列表适配器
	 */
	public class MessagesAdapter extends BaseAdapter {
		/**
		 * 渲染器
		 */
        private LayoutInflater inflater;
        
        
        public MessagesAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

		@Override
		public int getCount() {
			int result = 0;
			for(City city : cityList) {
				result += 1 + city.regions.size();
			}
			return result;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int i = 0;
			for(City city : cityList) {
				if(i == position) {
					convertView = inflater.inflate(R.layout.listitem_city, null);
					TextView text = (TextView) convertView.findViewById(R.id.listitem_city_label_name);
					text.setText(city.name);
					return convertView;
				}
				else if(i + 1 + city.regions.size() > position) {
					convertView = inflater.inflate(R.layout.listitem_region, null);
					TextView text = (TextView) convertView.findViewById(R.id.listitem_region_label_name);
					text.setText(city.regions.get(position - i - 1).name);
					ImageView image = (ImageView) convertView.findViewById(R.id.listitem_region_image_status);
					if(null != Profile.instance().region) {
						if(city.regions.get(position - i - 1).id == Profile.instance().region.id) {
							image.setVisibility(View.VISIBLE);
						}
						else {
							image.setVisibility(View.GONE);
						}
					}
					else {
						image.setVisibility(View.GONE);
					}
					return convertView;
				}
				i += 1 + city.regions.size();
			}
			return null;
		}
	}


	/**
	 * 放弃修改
	 */
	public final static int RESULT_CANCEL = 0;
	/**
	 * 已更新
	 */
	public final static int RESULT_UPDATED = 1;

	/**
	 * 关闭按钮
	 */
	@ResourceView(id = R.id.region_button_return)
	public ImageButton btnClose;
	/**
	 * 区域列表
	 */
	@ResourceView(id = R.id.region_list)
	public ListView list;
	/**
	 * 适配器
	 */
	private MessagesAdapter messagesAdapter = null;
	/**
	 * 城市列表
	 */
	protected ArrayList<City> cityList = new ArrayList<City>();


	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 界面处理
		prepare();
		// 加载数据
		load();
	}

	/**
	 * 处理列表
	 */
	private void prepare() {
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RegionActivity.this.setResult(RESULT_CANCEL, new Intent());
				RegionActivity.this.finish();
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				int i = 0;
				for(City city : cityList) {
					if(i == index) {
						return;
					}
					else if(i + 1 + city.regions.size() > index) {
						int regionId = city.regions.get(index - i - 1).id;
						String regionName = city.regions.get(index - i - 1).name;
						Profile.instance().region = new Region(regionId, regionName);
						try {
							Profile.instance().save();
						}
						catch (IOException e) { }
						//
						Intent intent = new Intent();
						intent.putExtra("regionId", regionId);
						intent.putExtra("regionName", regionName);
						RegionActivity.this.setResult(RESULT_UPDATED, intent);
						RegionActivity.this.finish();
						return;
					}
					i += 1 + city.regions.size();
				}
            }
		});
		messagesAdapter = new MessagesAdapter(this);
		list.setAdapter(messagesAdapter);
		messagesAdapter.notifyDataSetChanged();
	}

	/**
	 * 加载数据
	 */
	public void load() {
		Networking.doCommand("regionlist", new JSONResponse(RegionActivity.this) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content || content.getInteger("code", 0) < 0) {
					return;
				}
				content = content.getVisitor("data");
				if(null == content) {
					return;
				}
				cityList.clear();
				for(ILink<String, JSONVisitor> link : content.toVisitorMap()) {
					City city = new City();
					city.name = link.origin();
					for(JSONVisitor visitor : link.destination().toVisitors()) {
						city.id = visitor.getInteger("cityId", 0);
						city.regions.add(new Region(visitor.getInteger("id", 0), visitor.getString("name")));
					}
					cityList.add(city);
				}
				BaseAdapter adapter = (BaseAdapter) list.getAdapter();
				adapter.notifyDataSetChanged();
			}
		});
	}
}
