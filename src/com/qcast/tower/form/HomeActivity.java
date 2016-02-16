package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.qcast.tower.Program;
import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.framework.Storage;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.Table;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 首页
 */
public class HomeActivity extends Fragment {
	/**
	 * 横栏适配器
	 */
	public class BannerAdapter extends BaseAdapter {
		/**
		 * 图片缓存
		 */
		protected Table<Integer, ImageView> imageCache = new Table<Integer, ImageView>();
		
		
		@Override
		public int getCount() {
			if(null == adList) {
				return 0;
			}
			return adList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int index, View arg1, ViewGroup arg2) {
			ImageView imageView = imageCache.get(index);
			if(null == imageView) {
				imageView = new ImageView(HomeActivity.this.getActivity());
				if(adList != null && index >= adList.size()) {
					imageView.setImageResource(R.drawable.image_loading);
					imageView.setAdjustViewBounds(true);
					imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					imageView.setScaleType(ScaleType.CENTER_CROP);
					// i.setBackgroundResource(R.drawable.e);
					return imageView;
				}
				if(null == adList) {
					return imageView;
				}
				HashMap<String, Object> adMap = (HashMap<String, Object>) adList.get(index);
				if(null == adMap.get("image")) {
					imageView.setBackgroundResource(R.drawable.image_loading);
				}
				else if(adMap.get("image") instanceof String) {
					imageView.setBackgroundResource(R.drawable.image_loading);
				}
				else if(adMap.get("image") instanceof Bitmap) {
					imageView.setImageBitmap((Bitmap)(adMap.get("image")));
				}
				imageView.setAdjustViewBounds(true);
				imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageCache.put(index, imageView);
			}
			else {
				if(adList.get(index).get("image") instanceof Bitmap) {
					imageView.setImageBitmap((Bitmap)(adList.get(index).get("image")));
				}
			}
			return imageView;
		}
	}
	
	/**
	 * 横栏滚动句柄
	 */
	private class BannerRollHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
            	if(null == HomeActivity.this.getActivity()) {
            		return;
            	}
            	if(null == gallery) {
            		gallery = ((Gallery) HomeActivity.this.getActivity().findViewById(R.id.home_gallery_ad));
            		gallery.setAdapter(new BannerAdapter());
            		gallery.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							String url = (String) adList.get(position).get("url");
							if(null == url) {
								return;
							}
							Intent intent = new Intent(HomeActivity.this.getActivity(), WebActivity.class);
							intent.putExtra("url", url);
							HomeActivity.this.startActivity(intent);
						}
					});
            	}
            	if(0 == adList.size()) {
            		return;
            	}
            	if(gallery.getSelectedItemPosition() >= adList.size() - 1) {
            		gallery.setSelection(0);
            	}
            	else {
            		gallery.setSelection(gallery.getSelectedItemPosition() + 1);
            	}
                break;
            }
            super.handleMessage(msg);
        }
	}

	/**
	 * 横栏滚动任务
	 */
	private class BannerRollTask extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			if(null == timer) {
				cancel();
				return;
			}
			if(null != handler) {
				handler.sendMessage(message);
			}
		}
	}

	/**
	 * 消息ID
	 */
	public final static int MESSAGE_REGION = 1;
	
	
	/**
	 * 广告列表
	 */
	protected ArrayList<HashMap<String, Object>> adList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 当前资讯列表
	 */
	protected ArrayList<HashMap<String, Object>> newsList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 定时器
	 */
	protected static Timer timer = null;
    /**
     * 命令接收器
     */
    private BroadcastReceiver commandReceiver = null;
	/**
	 * 横栏句柄
	 */
	protected static BannerRollHandler handler = null;
	/**
	 * 滚动栏
	 */
	protected Gallery gallery = null;
	/**
	 * 区域ID列表
	 */
	protected int[] regionIds = null;
	/**
	 * 当前页面索引
	 */
	protected int page = 1;
	/**
	 * 是否启动
	 */
	protected boolean isStarted = false;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_home, container, true);
	}

	@Override
	public void onStart() {
		super.onStart();
		//
		if(isStarted) {
			return;
		}
		isStarted = true;
		dealNews();
		dealRegion();
		dealSearch();
		dealEntry();
		//
		loadVersion();
		loadAd();
		loadNews();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		final Button regionButton = (Button) this.getActivity().findViewById(R.id.home_button_region);
		if(null != Logic.regionName) {
			regionButton.setText(fetchRegionName());
		}
		if(Logic.hasMessage) {
			shakeBell();
		}
		else {
			stopBell();
		}
		//
		dealData();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopBell();
        if(null != commandReceiver) {
        	this.getActivity().unregisterReceiver(commandReceiver);
        }
        commandReceiver = null;
	}

	@Override
	public void onStop() {
		super.onStop();
		if(null == timer) {
			return;
		}
		timer.cancel();
		timer = null;
	}

	/**
	 * 加载版本准备升级
	 */
	private void loadVersion() {
		Host.doCommand("readVersion", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					return;
				}
				JSONObject result = (JSONObject) resultObject.get("data");
				String version = ((JSONString) (result.get("appVersion"))).getValue();
				String url = ((JSONString) (result.get("downloadUrl"))).getValue();
				int v = Integer.parseInt(version.replace(".", ""));
				if(v <= Integer.valueOf(Program.VERSION.replace(".", ""))) {
					return;
				}
				Intent intent = new Intent(HomeActivity.this.getActivity(), WebActivity.class);
				intent.putExtra("url", url);
				HomeActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * 加载广告列表
	 */
	private void loadAd() {
		Host.doCommand("banner", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(HomeActivity.this.getActivity(), "访问网络失败", Toast.LENGTH_LONG).show();
					return;
				}
				timer = new Timer();
				timer.schedule(new BannerRollTask(), 500, 5000);
				handler = new BannerRollHandler();
				//
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					// Toast.makeText(HomeActivity.this.getActivity(), ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				adList.clear();
				for(IJSON newJSON : result) {
					JSONObject jsonObject = (JSONObject) newJSON;
					HashMap<String, Object> newsMap = new HashMap<String, Object>();
					String imageURL = ((JSONString)(jsonObject.get("image"))).getValue();
					String imageName = Storage.getImageName(imageURL);
					newsMap.put("image", imageName);
					newsMap.put("url", ((JSONString)(jsonObject.get("url"))).getValue());
					adList.add(newsMap);
		            if(Text.isBlank(imageName)) {
		            	continue;
		            }
		            // 加载图片
		            Host.doImage("image", new ImageResponse(imageName, adList.size() - 1) {
						@Override
						public void onFinished(Bitmap content) {
							HashMap<String, Object> map = adList.get((Integer) tag);
							map.put("image", content);
						}
		            }, imageURL);
				}
			}
		}, Logic.regionId);
	}

	/**
	 * 加载资讯列表
	 */
	private void loadNews() {
		Host.doCommand("news", new CommonResponse<String>(page) {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(HomeActivity.this.getActivity(), "加载资讯失败", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(HomeActivity.this.getActivity(), ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray result = (JSONArray) resultObject.get("data");
				int thisPage = (Integer) this.tag;
				if(page != thisPage) {
					return;
				}
				if(0 == result.size()) {
					return;
				}
				for(IJSON item : result) {
					JSONObject newJSONObject = (JSONObject) item;
					HashMap<String, Object> newsMap = new HashMap<String, Object>();
					String photoURL = ((JSONString) newJSONObject.get("imageUrl")).getValue();
					String photoName = Storage.getImageName(photoURL);
					newsMap.put("photo", photoName);
					newsMap.put("title", ((JSONString) newJSONObject.get("title")).getValue());
					newsMap.put("publisher", ((JSONString) newJSONObject.get("publisher")).getValue());
					newsMap.put("date", ((JSONString) newJSONObject.get("date")).getValue());
					newsMap.put("url", ((JSONString) newJSONObject.get("url")).getValue());
					newsList.add(newsMap);
					if(Text.isBlank(photoURL)) {
		            	continue;
		            }
		            // 加载图片
		            Host.doImage("image", new ImageResponse(photoName, newsList.size() - 1) {
						@Override
						public void onFinished(Bitmap content) {
							HashMap<String, Object> map = newsList.get((Integer) tag);
							map.put("photo", content);
							ListView listview = (ListView) HomeActivity.this.getActivity().findViewById(R.id.home_list_news);
							SimpleAdapter adapter = (SimpleAdapter) ((HeaderViewListAdapter) listview.getAdapter()).getWrappedAdapter();
							adapter.notifyDataSetChanged();
						}
		            }, photoURL);
				}
				ListView listview = (ListView) HomeActivity.this.getActivity().findViewById(R.id.home_list_news);
				SimpleAdapter adapter = (SimpleAdapter) ((HeaderViewListAdapter) listview.getAdapter()).getWrappedAdapter();
				adapter.notifyDataSetChanged();
				page = thisPage + 1;
			}
		}, page);
	}

	/**
	 * 处理区域按钮
	 */
	public void dealRegion() {
		final Button regionButton = (Button) this.getActivity().findViewById(R.id.home_button_region);
		regionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Logic.regionName) {
					selectRegion(RegionActivity.REGION_CITYID);
				} else {
					selectRegion(RegionActivity.REGION_REGION);
				}
			}
		});
		final ImageView home_button_notify = (ImageView) this.getActivity().findViewById(R.id.home_button_notify);
		home_button_notify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this.getActivity(), MyMessageActivity.class);
				HomeActivity.this.getActivity().startActivity(intent);
			}
		});
		if(null == Logic.regionName) {
			selectRegion(RegionActivity.REGION_CITYID);
		}
	}

	/**
	 * 处理入口按钮
	 */
	public void dealEntry() {
		ImageButton button = (ImageButton) this.getActivity().findViewById(R.id.home_button_free_inquiry);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), InquiryDoctorActivity.class);
				intent.putExtra("services", "inquiry");
				intent.putExtra("docLevel", 0);
				HomeActivity.this.startActivity(intent);
			}
		});
		button = (ImageButton) this.getActivity().findViewById(R.id.home_button_healthy_archive);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), HealthManageActivity.class);
				HomeActivity.this.startActivity(intent);
			}
		});

		button = (ImageButton) this.getActivity().findViewById(R.id.home_button_reserve);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), InquiryDoctorActivity.class);
				intent.putExtra("services", "inquiry");
				intent.putExtra("docLevel", 2);
				HomeActivity.this.startActivity(intent);
			}
		});

		button = (ImageButton) this.getActivity().findViewById(R.id.home_button_family_fastview);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), FamilyActivity.class);
				HomeActivity.this.startActivity(intent);
			}
		});
	}
	
	/**
	 * 处理搜索按钮
	 */
	public void dealSearch() {
		Button btnSearch = (Button) this.getActivity().findViewById(R.id.home_button_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), SearchActivity.class);
				HomeActivity.this.getActivity().startActivity(intent);
			}
		});
	}
	
	/**
	 */
	private void dealData() {
		commandReceiver = new BroadcastReceiver() {
    		@Override
    		public void onReceive(Context context, Intent intent) {
    			String msgId = intent.getStringExtra("msgid");
    			EMMessage message = intent.getParcelableExtra("message");
    			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
    			String aciton = cmdMsgBody.action;
    			Log.d("tower", "command message " + aciton + " receive:" + msgId);
    			if("notify".equalsIgnoreCase(aciton)) {
    				// 通知消息
    				int type = 0;
    				String title = null;
        			try {
        				type = message.getIntAttribute("type");
        				title = message.getStringAttribute("title");
    				}
        			catch (EaseMobException e) {
        				Log.e("tower", "command message parse failed", e);
    				}
    				if(!Text.isBlank(title)) {
        				Toast.makeText(HomeActivity.this.getActivity(), title, Toast.LENGTH_LONG).show();
    				}
        			switch(type) {
        			case 1:
        				// 添加好友消息
        				break;
        			case 2:
        				// 好友接受消息
        				break;
        			case 3:
        				// 好友拒绝消息
        				break;
        			case 4:
        				// 系统通知消息
        				break;
        			}
        			Logic.hasMessage = true;
        			shakeBell();
    			}
    		}
    	};
    	this.getActivity().registerReceiver(commandReceiver, new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction()));
	}

	/**
	 * 处理资讯
	 */
	private void dealNews() {
		ListView listview = (ListView) this.getActivity().findViewById(R.id.home_list_news);
		if(listview.getHeaderViewsCount() > 0) {
			return;
		}
		View viewHead = LayoutInflater.from(this.getActivity()).inflate(R.layout.div_home_head, null);
		listview.addHeaderView(viewHead);
		//
		SimpleAdapter listItemAdapter = new SimpleAdapter(this.getActivity(), newsList, R.layout.listview_news,
			new String[]{"photo", "title", "publisher", "date"}, 
	        new int[]{R.id.news_image_photo, R.id.news_label_title, R.id.news_label_publisher, R.id.news_label_date});
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
				index = index - 1;
				HashMap<String, Object> newsMap = newsList.get(index);
				Intent intent = new Intent(HomeActivity.this.getActivity(), WebActivity.class);
				intent.putExtra("url", newsMap.get("url").toString());
				HomeActivity.this.startActivity(intent);
            }
		});
		listview.setOnScrollListener(new OnScrollListener() {    
	        boolean isLastRow = false;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {      
					loadNews();
	                isLastRow = false;      
	            }
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 2) {      
	                isLastRow = true;      
	            }
			}
		});
	}
	
	/**
	 * 选择小区
	 */
	public void selectRegion(int regionLevel) {
		Intent intent = new Intent(HomeActivity.this.getActivity(), RegionActivity.class);
		intent.putExtra(RegionActivity.REGION_LEVEL, regionLevel);
		intent.putExtra(RegionActivity.STRING_CITY_ID, Logic.cityId);
		HomeActivity.this.startActivityForResult(intent, 2);
	}

	/**
	 * 回调
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//
		if(resultCode == RadioActivity.RESULT_CANCEL) {
			return;
		}
		if(1 == requestCode) {
			int current = -1;
			current = data.getIntExtra("index", current);
			if(-1 == current) {
				return;
			}
			Logic.regionId = regionIds[current];
			Logic.regionName = Logic.regions.get(Logic.regionId);
			if(null != Logic.regionName) {
				final Button regionButton = (Button) this.getActivity().findViewById(R.id.home_button_region);
				regionButton.setText(fetchRegionName());
			}
			Storage.setUser("regionId", Logic.regionId);
			Storage.setUser("regionName", Logic.regionName);
		}
		else if(2 == requestCode) {
			Logic.cityId = data.getIntExtra("cityId", 0);
			Logic.cityName = data.getStringExtra("cityName");
			Logic.regionId = data.getIntExtra("regionId", 0);
			Logic.regionName = data.getStringExtra("regionName");
			if(null != Logic.regionName) {
				final Button regionButton = (Button) this.getActivity().findViewById(R.id.home_button_region);
				regionButton.setText(fetchRegionName());
			}
			Storage.setUser("cityId", Logic.cityId);
			Storage.setUser("cityName", Logic.cityName);
			Storage.setUser("regionId", Logic.regionId);
			Storage.setUser("regionName", Logic.regionName);
		}
	}

	/**
	 * 获取处理后的区域名称
	 * 
	 * @return 处理后的区域名称
	 */
	public String fetchRegionName() {
		if(null == Logic.regionName) {
			return "选择小区";
		}
		if(Logic.regionName.length() <= 4) {
			return Logic.regionName;
		}
		return Logic.regionName.substring(Logic.regionName.length() - 4);
	}
	
	/**
	 * 摇晃铃铛
	 */
	public void shakeBell() {
		final ImageView btnBell = (ImageView) this.getActivity().findViewById(R.id.home_button_notify);
		btnBell.clearAnimation();
		btnBell.setImageResource(R.drawable.bell_active);
		//
		final RotateAnimation animRight = new RotateAnimation(-30, 30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animRight.setDuration(1000);
		final RotateAnimation animLeft = new RotateAnimation(30f, -30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animLeft.setDuration(1000);
		//
		final AnimationListener listener = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				Log.d("tower", "");
			}
			@Override
			public void onAnimationRepeat(Animation animation) { }
			@Override
			public void onAnimationEnd(Animation animation) {
				btnBell.clearAnimation();
				animation.setAnimationListener(null);
				if(animation == animRight) {
			        animLeft.setAnimationListener(this);
					btnBell.startAnimation(animLeft);
				}
				else if(animation == animLeft) {
					animRight.setAnimationListener(this);
					btnBell.startAnimation(animRight);
				}
			}
        };
        animRight.setAnimationListener(listener);
        //
        btnBell.startAnimation(animRight);
	}

	/**
	 * 停止摇晃铃铛
	 */
	public void stopBell() {
		final ImageView btnBell = (ImageView) this.getActivity().findViewById(R.id.home_button_notify);
		btnBell.clearAnimation();
		btnBell.setImageResource(R.drawable.bell_normal);
	}
}
