package com.qcast.tower.view.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.qcast.tower.Program;
import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.qcast.tower.business.core.IMeListener;
import com.qcast.tower.business.structure.AdDomain;
import com.qcast.tower.framework.Helper;
import com.qcast.tower.framework.Storage;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.etc.Version;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.general.view.form.BrowserActivity;
import com.slfuture.pretty.general.view.form.RadioActivity;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View.OnClickListener;
import android.widget.ImageView.ScaleType;


/**
 * 首页
 */
@ResourceView(id = R.layout.activity_home)
public class HomeActivity extends FragmentEx implements IMeListener {
	
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径

	private ViewPager adViewPager;
	private List<ImageView> imageViews;// 滑动的图片集合

	private List<View> dots; // 图片标题正文的那些点
	private List<View> dotList;


	private int currentItem = 0; // 当前图片的索引号

	private ScheduledExecutorService scheduledExecutorService;

	// 异步加载图片
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	// 轮播banner的数据
	private static List<AdDomain> adList;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adViewPager.setCurrentItem(currentItem);
		};
	};
	

	/**
	 * 消息ID
	 */
	public final static int MESSAGE_REGION = 1;

	/**
	 * 当前资讯列表
	 */
	protected ArrayList<HashMap<String, Object>> newsList = new ArrayList<HashMap<String, Object>>();
	protected ArrayList<HashMap<String, Object>> bannerList = new ArrayList<HashMap<String, Object>>();
	/**
	 * 小区选择按钮
	 */
	@ResourceView(id = R.id.home_button_region)
	public Button btnRegion;
	/**
	 * 铃铛
	 */
	@ResourceView(id = R.id.home_button_notify)
	public ImageView btnBell;
	/**
	 * 搜索按钮
	 */
	@ResourceView(id = R.id.home_button_search)
	public Button btnSearch;
	/**
	 *签到按钮
	 */
	@ResourceView(id = R.id.home_button_signin)
	public Button btnSignin;
	
	/**
	 * 新闻列表
	 */
	@ResourceView(id = R.id.home_list_news)
	public ListView listNews;
	/**
	 * 当前页面索引
	 */
	protected int page = 1;
	/**
	 * 当前信息
	 */
	protected int currentRegionId = -1;
	protected String currentUserId = null;
	/**
	 * 动画
	 */
	private AnimationListener listener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			Log.d("tower", "onAnimationStart()");
		}
		@Override
		public void onAnimationRepeat(Animation animation) { }
		@Override
		public void onAnimationEnd(Animation animation) {
			if(1 == shakeDirection) {
				Log.d("tower", "onAnimationEnd(Right)");
				btnBell.startAnimation(animLeft);
				shakeDirection = -1;
			}
			else if(-1 == shakeDirection) {
				Log.d("tower", "onAnimationEnd(Left)");
				btnBell.startAnimation(animRight);
				shakeDirection = 1;
			}
			else {
				btnBell.clearAnimation();
			}
		}
    };
	protected int shakeDirection = 1;
	protected RotateAnimation animRight = null; 
	protected RotateAnimation animLeft = null; 


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initImageLoader();
		// 获取图片加载实例
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.top_banner_android)
				.showImageForEmptyUri(R.drawable.top_banner_android)
				.showImageOnFail(R.drawable.top_banner_android)
				.cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY).build();
		prepare();
		startAd();
		dealNews();
		dealSignin();
		dealRegion();
		dealSearch();		
		//
		loadVersion();
		loadNews();
	}	

  @Override
	public void onResume() {
		super.onResume();
		final Button regionButton = (Button) this.getActivity().findViewById(R.id.home_button_region);
		if(null != Logic.regionName) {
			regionButton.setText(fetchRegionName());
		}
		if(com.qcast.tower.business.Runtime.hasUnreadMessage) {
			Controller.doDelay(new Runnable() {
				@Override
				public void run() {
					shakeBell();
				}
			}, 1000);
		}
		else {
			stopBell();
		}
		loadEntry();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopBell();
	}
	@Override
	public void onStop() {
		super.onStop();
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
	}


	/**
	 * 加载版本准备升级
	 */
	private void loadVersion() {
		Networking.doCommand("readVersion", new CommonResponse<String>() {
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
				Version current = Version.fetchVersion(Program.application);
				Version server = Version.build(version);
				if(null == server) {
					return;
				}
				if(current.compareTo(server) >= 0) {
					 return;
				}
				Intent intent = new Intent(HomeActivity.this.getActivity(), BrowserActivity.class);
				intent.putExtra("url", url);
				HomeActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * 加载资讯列表
	 */
	private void loadNews() {
		Networking.doCommand("news", new CommonResponse<String>(page) {
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
					String photoURL = "";
					if(null != newJSONObject.get("imageUrl")) {
						photoURL = ((JSONString) newJSONObject.get("imageUrl")).getValue();
					}
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
		            Networking.doImage("image", new ImageResponse(photoName, newsList.size() - 1) {
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
	 * 处理签到
	 */
	public void dealSignin() {	
 		/*btnSignin.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
					HomeActivity.this.startActivity(intent);
					Toast.makeText(HomeActivity.this.getActivity(), "请先登录账号", Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(HomeActivity.this.getActivity(), SignInActivity.class);
				HomeActivity.this.startActivity(intent);
			}
			
		});*/
	}
	/**
	 * 处理区域按钮
	 */
	public void dealRegion() {
		btnRegion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), RegionActivity.class);
				HomeActivity.this.startActivityForResult(intent, MESSAGE_REGION);
			}
		});
		btnBell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
					HomeActivity.this.startActivity(intent);
					Toast.makeText(HomeActivity.this.getActivity(), "请先登录账号", Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this.getActivity(), MyMessagesActivity.class);
				HomeActivity.this.getActivity().startActivity(intent);
			}
		});
		if(null == Profile.instance().region) {
			Intent intent = new Intent(HomeActivity.this.getActivity(), RegionActivity.class);
			HomeActivity.this.startActivityForResult(intent, MESSAGE_REGION);
		}
		else {
			btnRegion.setText(fetchRegionName());
		}
	}

	/**
	 * 加载入口
	 */
	public void loadEntry() {
		boolean sentry = false;
		if(null == Profile.instance().region && 0 != currentRegionId) {
			currentRegionId = 0;
			sentry = true;
		}
		else if(null != Profile.instance().region && Profile.instance().region.id != currentRegionId) {
			currentRegionId = Profile.instance().region.id;
			sentry = true;
		}
		if(null == Me.instance && null != currentUserId) {
			currentUserId = null;
			sentry = true;
		}
		else if(null != Me.instance && !Me.instance.id.equals(currentUserId)) {
			currentUserId = Me.instance.id;
			sentry = true;
		}}
	
	/**
	 * 处理搜索按钮
	 */
	public void dealSearch() {
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this.getActivity(), SearchActivity.class);
				HomeActivity.this.getActivity().startActivity(intent);
			}
		});
	}
	

	/**
	 * 界面预设
	 */
	private void prepare() {
		if(listNews.getHeaderViewsCount() > 0) {
			return;
		}
		animRight = new RotateAnimation(-30, 30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animRight.setDuration(1000);
        animRight.setAnimationListener(listener);
		animLeft = new RotateAnimation(30f, -30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animLeft.setDuration(1000);
		animLeft.setAnimationListener(listener);
		
		View viewHead = LayoutInflater.from(this.getActivity()).inflate(R.layout.div_home_head, null);
		listNews.addHeaderView(viewHead);


		/**
		 * 广告数据
		 */
			adList = getBannerAd();
			imageViews = new ArrayList<ImageView>();
			// 点
			dots = new ArrayList<View>();
			dotList = new ArrayList<View>();
			View dot0 =(View)viewHead.findViewById(R.id.v_dot0);
			View dot1 =(View)viewHead.findViewById(R.id.v_dot1);
			View dot2 =(View)viewHead.findViewById(R.id.v_dot2);
			View dot3 =(View)viewHead.findViewById(R.id.v_dot3);
			View dot4 =(View)viewHead.findViewById(R.id.v_dot4);
			dots.add(dot0);
			dots.add(dot1);
			dots.add(dot2);
			dots.add(dot3);
			dots.add(dot4);			
			adViewPager = (ViewPager) viewHead.findViewById(R.id.vp);
			adViewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
			// 设置一个监听器，当ViewPager中的页面改变时调用
			adViewPager.setOnPageChangeListener(new MyPageChangeListener());
			addDynamicView();
			//
		/*	ImageView childImg =(ImageView)viewHead.findViewById(R.id.home_img_child);
			ImageView womanImg =(ImageView)viewHead.findViewById(R.id.home_img_woman);
			ImageView oldImg =(ImageView)viewHead.findViewById(R.id.home_img_old);
			Networking.doCommand("ThreeBanner", new JSONResponse(null) {
				@Override
				public void onFinished(JSONVisitor content) {
					if(null == content) {
						return;
					}
					if(content.getInteger("code", 0) <= 0) {
						return;
					}
					content = content.getVisitor("data");
					for(JSONVisitor banner : content.getVisitors("data")) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("title", banner.getString("title"));
						map.put("subtitle", banner.getString("subtitle"));
						map.put("url", banner.getString("url"));
						map.put("imgUrl", banner.getString("imgUrl"));
						bannerList.add(map);
					}
				}
			}, currentRegionId);
		*/
	
	/**
	 * 8大功能按钮	
	 */
		int[] icon = {R.drawable.icon_function_1,R.drawable.icon_function_2,R.drawable.icon_function_3,R.drawable.icon_function_4,
        		R.drawable.icon_function_5,R.drawable.icon_function_6,R.drawable.icon_function_7,R.drawable.icon_function_8};
        String[] iconName = {"私人医生","预约体检","预约理疗","预约挂号","健康档案","自我诊断","健康监测","慢病百科"};
        GridView gridview = (GridView) viewHead.findViewById(R.id.home_gridView);        
        ArrayList<HashMap<String, Object>> functionList = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<icon.length;i++){
        	HashMap<String, Object> map =new HashMap<String,Object>();
        	map.put("image", icon[i]);
        	map.put("text", iconName[i]);
        	functionList.add(map);
        }
        SimpleAdapter adapter =new SimpleAdapter(this.getActivity(),functionList,
        		R.layout.listitem_entry,
        		new String[]{"image","text"},
        		new int[]{R.id.entry_image_icon,R.id.entry_label_title});
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new OnItemClickListener() 
        { 
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
            { 
            	switch (position){
            	case 0:
            		Intent intent0 = new Intent(HomeActivity.this.getActivity(), SelectDoctorActivity.class);
            		HomeActivity.this.startActivity(intent0);
				break;
            	case 1:
            		if(null == Profile.instance().region) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), RegionActivity.class);
    					HomeActivity.this.startActivityForResult(intent, MESSAGE_REGION);
    					Toast.makeText(HomeActivity.this.getActivity(), "请设置所在小区", Toast.LENGTH_LONG).show();
    					return;
    				}
    				if(null == Me.instance) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
    					HomeActivity.this.startActivity(intent);
    					Toast.makeText(HomeActivity.this.getActivity(), "请先登录账号", Toast.LENGTH_LONG).show();
    					return;
    				}
    				Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("yuyuetijian", Profile.instance().region.id, Me.instance.token));
                    break;
            	case 2:
            		if(null == Profile.instance().region) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), RegionActivity.class);
    					HomeActivity.this.startActivityForResult(intent, MESSAGE_REGION);
    					Toast.makeText(HomeActivity.this.getActivity(), "请设置所在小区", Toast.LENGTH_LONG).show();
    					return;
    				}
    				if(null == Me.instance) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
    					HomeActivity.this.startActivity(intent);
    					Toast.makeText(HomeActivity.this.getActivity(), "请先登录账号", Toast.LENGTH_LONG).show();
    					return;
    				}
    				Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("yuyueliliao", Profile.instance().region.id, Me.instance.token));
    			    break;
            	case 3:
            		if(null == Profile.instance().region) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), RegionActivity.class);
    					HomeActivity.this.startActivityForResult(intent, MESSAGE_REGION);
    					Toast.makeText(HomeActivity.this.getActivity(), "请设置所在小区", Toast.LENGTH_LONG).show();
    					return;
    				}
    				if(null == Me.instance) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
    					HomeActivity.this.startActivity(intent);
    					Toast.makeText(HomeActivity.this.getActivity(), "请先登录账号", Toast.LENGTH_LONG).show();
    					return;
    				}
    				Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("yuyueguahao", Profile.instance().region.id, Me.instance.token));
    			    break;
            	case 4:
            		if(null == Me.instance) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
    					HomeActivity.this.startActivity(intent);
    					Toast.makeText(HomeActivity.this.getActivity(), "请先登录账号", Toast.LENGTH_LONG).show();
    					return;
    				}
            		Intent intent = new Intent(HomeActivity.this.getActivity(), ArchiveActivity.class);
					intent.putExtra("password", 0);
					HomeActivity.this.startActivity(intent);
            		break;
            	case 5:
            		Intent intent5 = new Intent(HomeActivity.this.getActivity(), SelfDiagnosticActivity.class);
    				HomeActivity.this.startActivity(intent5);
            		break;
            	case 6:
            		String token = "";
    				if(null != Me.instance) {
    					token = Me.instance.token;
    				}
    				String regionId = "";
    				if(null != Profile.instance().region) {
    					regionId = String.valueOf(Profile.instance().region.id);
    				}
    				Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("activity1", token, regionId));
            		break;
            	case 7:
            		String token1 = "";
    				if(null != Me.instance) {
    					token = Me.instance.token;
    				}
    				String regionId1 = "";
    				if(null != Profile.instance().region) {
    					regionId = String.valueOf(Profile.instance().region.id);
    				}
    				Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("activity1", token1, regionId1));
            		break;
            	}
            		
            } 
        }); 

        btnSignin.getBackground().setAlpha(200);	
		btnRegion.getBackground().setAlpha(200);
		btnSearch.getBackground().setAlpha(200);
		btnBell.setImageAlpha(200);
		this.getActivity().findViewById(R.id.home_layout_header).bringToFront();
		
	}
		

	private void initImageLoader() {
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
				.getOwnCacheDirectory(this.getActivity(),IMAGE_CACHE_PATH);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.getActivity()).defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new LruMemoryCache(12 * 1024 * 1024))
				.memoryCacheSize(12 * 1024 * 1024)
				.discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
		
	}

	private void addDynamicView() {
		// 动态添加图片和下面指示的圆点
				// 初始化图片资源
				for (int i = 0; i < adList.size(); i++) {
					ImageView imageView = new ImageView(this.getActivity());
					// 异步加载图片
					mImageLoader.displayImage(adList.get(i).getImgUrl(), imageView,
							options);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					imageViews.add(imageView);
					dots.get(i).setVisibility(View.VISIBLE);
					dotList.add(dots.get(i));
				}
		
	}
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return adList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = imageViews.get(position);
			((ViewPager) container).addView(iv);
			final AdDomain adDomain = adList.get(position);
			// 在这个方法里面设置图片的点击事件
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 处理跳转逻辑
					
				}
			});
			return iv;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

	}
	private class MyPageChangeListener implements OnPageChangeListener {

		private int oldPosition = 0;

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			currentItem = position;
			AdDomain adDomain = adList.get(position);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}
	}
	private void startAd() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
				TimeUnit.SECONDS);
		
	}
	private class ScrollTask implements Runnable {

		@Override
		public void run() {
			synchronized (adViewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget();
			}
		}
	}
	/**
	 * 轮播广播模拟数据
	 * @return
	 */
	public static List<AdDomain> getBannerAd() {
	
	/*	Networking.doCommand("newBanner", new JSONResponse(context,null) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content || content.getInteger("code", 1) < 0) {
					 return;
				 }
				content = content.getVisitor("data");
				List<AdDomain> adList = new ArrayList<AdDomain>();
				for(JSONVisitor banner : content.getVisitors("data")){				
					AdDomain adDomain = new AdDomain();
					adDomain.setImgUrl(banner.getString("image"));
					adDomain.setAd(false);
					adList.add(adDomain);
				}
			
			}
		},Logic.token);	*/	
		List<AdDomain> adList = new ArrayList<AdDomain>();
		AdDomain adDomain = new AdDomain();
		adDomain.setId("108078");
		adDomain.setImgUrl("http://g.hiphotos.baidu.com/image/w%3D310/sign=bb99d6add2c8a786be2a4c0f5708c9c7/d50735fae6cd7b8900d74cd40c2442a7d9330e29.jpg");
		adDomain.setAd(false);
		adList.add(adDomain);

		AdDomain adDomain2 = new AdDomain();
		adDomain2.setId("108078");
		adDomain2.setImgUrl("http://g.hiphotos.baidu.com/image/w%3D310/sign=7cbcd7da78f40ad115e4c1e2672e1151/eaf81a4c510fd9f9a1edb58b262dd42a2934a45e.jpg");
		adDomain2.setAd(false);
		adList.add(adDomain2);
		
		AdDomain adDomain3 = new AdDomain();
		adDomain3.setId("108078");
		adDomain3.setImgUrl("http://e.hiphotos.baidu.com/image/w%3D310/sign=392ce7f779899e51788e3c1572a6d990/8718367adab44aed22a58aeeb11c8701a08bfbd4.jpg");
		adDomain3.setAd(false);
		adList.add(adDomain3);

		AdDomain adDomain4 = new AdDomain();
		adDomain4.setId("108078");
		adDomain4.setImgUrl("http://d.hiphotos.baidu.com/image/w%3D310/sign=54884c82b78f8c54e3d3c32e0a282dee/a686c9177f3e670932e4cf9338c79f3df9dc55f2.jpg");
		adDomain4.setAd(false);
		adList.add(adDomain4);

		AdDomain adDomain5 = new AdDomain();
		adDomain5.setId("108078");
		adDomain5.setImgUrl("http://e.hiphotos.baidu.com/image/w%3D310/sign=66270b4fe8c4b7453494b117fffd1e78/0bd162d9f2d3572c7dad11ba8913632762d0c30d.jpg");
		adDomain5.setAd(true); // 代表是广告
		adList.add(adDomain5);

		return adList;
	}


	

	/**
	 * 处理资讯
	 */
	private void dealNews() {
		SimpleAdapter listItemAdapter = new SimpleAdapter(this.getActivity(), newsList, R.layout.listitem_news,
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
		listNews.setAdapter(listItemAdapter);
		listNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				index = index - 1;
				HashMap<String, Object> newsMap = newsList.get(index);
				Intent intent = new Intent(HomeActivity.this.getActivity(), BrowserActivity.class);
				intent.putExtra("url", newsMap.get("url").toString());
				HomeActivity.this.startActivity(intent);
            }
		});
		listNews.setOnScrollListener(new OnScrollListener() {    
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
	 * 回调
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RadioActivity.RESULT_CANCEL) {
			return;
		}
		if(MESSAGE_REGION == requestCode) {
			int regionId = data.getIntExtra("regionId", 0);
			if(0 == regionId) {
				return;
			}
			btnRegion.setText(fetchRegionName());
		}
	}

	/**
	 * 获取处理后的区域名称
	 * 
	 * @return 处理后的区域名称
	 */
	public String fetchRegionName() {
		if(null == Profile.instance().region) {
			return "选择小区";
		}
		return Profile.instance().region.getShortName();
	}

	/**
	 * 摇晃铃铛
	 */
	public void shakeBell() {
		btnBell.setImageResource(R.drawable.bell_active);
		//
		if(0 == shakeDirection) {
	        btnBell.startAnimation(animRight);
	        shakeDirection = 1;
		}
	}

	/**
	 * 停止摇晃铃铛
	 */
	public void stopBell() {
		btnBell.setImageResource(R.drawable.bell_normal);
		shakeDirection = 0;
	}

	@Override
	public void onConflict() {
		
	}

	@Override
	public void onCommand(String from, String action, com.slfuture.carrie.base.type.Table<String, Object> data) {
		if("systemMessage".equals(action)) {
			shakeBell();
		}
	}
}