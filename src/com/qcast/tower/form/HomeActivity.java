package com.qcast.tower.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.qcast.tower.R;
import com.qcast.tower.logic.Host;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.ImageResponse;
import com.qcast.tower.logic.response.Response;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.carrie.base.type.core.ILink;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
		loadAd();
		loadNews();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		final Button regionButton = (Button) this.getActivity().findViewById(R.id.home_button_region);
		if(null != Logic.regionName) {
			regionButton.setText(Logic.regionName);
		}
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
					Toast.makeText(HomeActivity.this.getActivity(), ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
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
		});
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
				selectRegion();
			}
		});
		if(null == Logic.regionName) {
			selectRegion();
		}
	}

	/**
	 * 处理入口按钮
	 */
	public void dealEntry() {
		ImageButton button = (ImageButton) this.getActivity().findViewById(R.id.home_button_famous);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), RadioActivity.class);
				intent.putExtra("title", "title");
				intent.putExtra("items", new String[] {"1", "2", "2"});
				intent.putExtra("index", 1);
				HomeActivity.this.startActivity(intent);
				
//				Intent intent = new Intent(HomeActivity.this.getActivity(), ExaminationActivity.class);
//				intent.putExtra("docLevel", 1);
//				HomeActivity.this.startActivity(intent);
				
//				Intent intent = new Intent(HomeActivity.this.getActivity(), InquiryDoctorActivity.class);
//				intent.putExtra("docLevel", 1);
//				HomeActivity.this.startActivity(intent);
			}
		});
		button = (ImageButton) this.getActivity().findViewById(R.id.home_button_inquiry);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), InquiryDoctorActivity.class);
				intent.putExtra("docLevel", 2);
				HomeActivity.this.startActivity(intent);
			}
		});
	}
	
	/**
	 * 处理搜索按钮
	 */
	public void dealSearch() {
		final EditText txtSearch = (EditText) this.getActivity().findViewById(R.id.home_text_search);
		txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String search = txtSearch.getText().toString();
				if (hasFocus) {
					if(HomeActivity.this.getActivity().getString(R.string.search_tip).equals(search)) {
						txtSearch.setText("");
					}
				}
				else if(Text.isBlank(search)) {
					txtSearch.setText(HomeActivity.this.getActivity().getString(R.string.search_tip));
				}
			}
		});
		txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH ||(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {                
					Intent intent = new Intent(HomeActivity.this.getActivity(), WebActivity.class);
					intent.putExtra("url", "http://www.baidu.com/s?wd=" + txtSearch.getText().toString());
					HomeActivity.this.startActivity(intent);
					return true;
				}
				return false;
			}
		});
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
	}
	
	/**
	 * 选择小区
	 */
	public void selectRegion() {
		if(null != Logic.regions) {
			final String[] regionNames = new String[Logic.regions.size()];
			regionIds = new int[Logic.regions.size()];
			int current = -1;
			int i = 0;
			for(ILink<Integer, String> entry : Logic.regions) {
				regionIds[i] = entry.origin();
				regionNames[i] = entry.destination();
				if(regionIds[i] == Logic.regionId) {
					current = i;
				}
				i++;
			}
			Intent intent = new Intent(HomeActivity.this.getActivity(), RadioActivity.class);
			intent.putExtra("title", "选择小区");
			intent.putExtra("items", regionNames);
			intent.putExtra("index", current);
			HomeActivity.this.startActivityForResult(intent, 1);
			return;
		}
		Host.doCommand("regionlist", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(HomeActivity.this.getActivity(), "网络错误", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(HomeActivity.this.getActivity(), ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				JSONArray array = (JSONArray) resultObject.get("data");
				Logic.regions = new com.slfuture.carrie.base.type.safe.Table<Integer, String>();
				for(int i = 0; i < array.size(); i++) {
					IJSON item = array.get(i);
					int regionId = ((JSONNumber)((JSONObject)item).get("id")).intValue();
					String regionName = ((JSONString)((JSONObject)item).get("name")).getValue();
					Logic.regions.put(regionId, regionName);
				}
				selectRegion();
			}
		});
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
				regionButton.setText(Logic.regionName);
			}
			Storage.setUser("regionId", Logic.regionId);
			Storage.setUser("regionName", Logic.regionName);
		}
	}
}
