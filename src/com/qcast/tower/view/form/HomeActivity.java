package com.qcast.tower.view.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.slfuture.pluto.communication.response.JSONResponse;
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
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.model.core.IFilter;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.core.ICollection;
import com.slfuture.carrie.base.type.core.ILink;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import android.text.TextUtils;
import android.view.View.OnClickListener;
import android.widget.ImageView.ScaleType;


/**
 * 首页
 */
@ResourceView(id = R.layout.activity_home)
public class HomeActivity extends FragmentEx implements IMeListener {
	private static final String TAG = HomeActivity.class.getSimpleName();
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径

	private ViewPager adViewPager;
	private List<ImageView> imageViews;// 滑动的图片集合

	private List<View> dots; // 图片标题正文的那些点
	private List<View> dotList;
	private  Boolean hasSignIn = false;

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
	
	
	/**
	 * 新闻列表
	 */
	@ResourceView(id = R.id.home_list_news)
	public ListView listNews;
	@ResourceView(id = R.id.reserve_image_discount)
	public Button btnDiscount;

	/**
	 * 当前
	 */
	protected int page = 1;
	protected int regionId = 1;
	private int NewPrice ;
	private int OldPrice ;
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

	/** 广告位*/
	View viewHead;
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//加载签到状态
		
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
		viewHead = LayoutInflater.from(this.getActivity()).inflate(R.layout.div_home_head, null);		
		loadSignStatus();	
		
	   /**
       * 广告数据
       */
		      adList = new ArrayList<AdDomain>();;
		      imageViews = new ArrayList<ImageView>();
		      // 点
		      dots = new ArrayList<View>();
		      dotList = new ArrayList<View>();
		      View dot0 =(View)viewHead.findViewById(R.id.v_dot0);
		      View dot1 =(View)viewHead.findViewById(R.id.v_dot1);
		      View dot2 =(View)viewHead.findViewById(R.id.v_dot2);
		      dots.add(dot0);
		      dots.add(dot1);
		      dots.add(dot2);		   
		        loadEntry();
		        loadBannerAd();
				startAd();		
				dealNews();
				dealRegion();
				dealSearch();		
				//
				loadVersion();
				
	}	

  private void loadSignStatus() {
	  final Button btnSignin=(Button)viewHead.findViewById(R.id.home_button_signin);
	  if(null !=Me.instance){
	  Networking.doCommand("signinStatus", new JSONResponse(HomeActivity.this.getActivity()) {			
			@Override
			public void onFinished(JSONVisitor content) {			
				if (null == content || content.getInteger("code") < 0) {
					Toast.makeText(HomeActivity.this.getActivity(), "网络问题", Toast.LENGTH_LONG).show();
                return;
				}
				boolean data = content.getBoolean("data");					
				hasSignIn= data;
				  //
				if(null==Me.instance){
					btnSignin.setVisibility(View.GONE);
				}else if(!hasSignIn){			
						btnSignin.getBackground().setAlpha(200);
						btnSignin.setVisibility(View.VISIBLE);					
				}else{
						btnSignin.setVisibility(View.GONE);
					}	
			}			
		}, Me.instance.token);
	  }
	 /**
	  * 处理签到
	  */
		btnSignin.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {	
						String token = "";
	    				if(null != Me.instance) {
	    					token = Me.instance.token;
	    				}
	    				Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("activity3", token));		    				
					}				
			});	
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
		loadSignStatus();
		prepare();		
		loadEntry();
		loadNews();
		
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
				if (result.get("appVersion") == null || result.get("downloadUrl") == null) {
					return;
				}
				
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
		Networking.doCommand("hotRecommend", new JSONResponse(HomeActivity.this.getActivity(), (Integer) currentRegionId) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content || content.getInteger("code", 0) <= 0) {
					//Log.e(TAG, "hotRecommend  err:" + content.getString("msg"));
					return;
				}
				newsList.clear();
				ListView listview = (ListView) HomeActivity.this.getActivity().findViewById(R.id.home_list_news);
				SimpleAdapter adapter = (SimpleAdapter) ((HeaderViewListAdapter) listview.getAdapter()).getWrappedAdapter();
				adapter.notifyDataSetChanged();
				
				JSONVisitor data = content.getVisitor("data");
				if (null == data) {
					return;
				}
				int thisPage = (Integer) this.tag;
				if(currentRegionId != thisPage) {
					return;
				}
				for(ILink<String, JSONVisitor> link : data.toVisitorMap()) {
					String key = link.origin();
					JSONVisitor value = link.destination();
					HashMap<String, Object> newsMap = new HashMap<String, Object>();
					String photoURL = "";
				if(key.equals("physicalExam")||key.equals("physiotherapy")){
					if(!TextUtils.isEmpty(value.getString("image"))) {
						photoURL = value.getString("image");
					} 
					String photoName = Storage.getImageName(photoURL);
					newsMap.put("image", photoName);
					newsMap.put("name",  value.getString("name"));
					newsMap.put("image", value.getString("image"));
					newsMap.put("price", value.getInteger("price"));
					newsMap.put("originalPrice", value.getInteger("originalPrice"));
					newsMap.put("score", value.getInteger("score"));
					newsMap.put("url", value.getString("url"));
					newsMap.put("pop", value.getInteger("pop"));
					newsList.add(newsMap);
					if(!Text.isBlank(photoURL)) {
						// 加载图片
						Networking.doImage("image", new ImageResponse(photoName, newsList.size() - 1) {
							@Override
							public void onFinished(Bitmap content) {
								HashMap<String, Object> map = newsList.get((Integer) tag);
								map.put("image", content);
								ListView listview = (ListView) HomeActivity.this.getActivity().findViewById(R.id.home_list_news);
								SimpleAdapter adapter = (SimpleAdapter) ((HeaderViewListAdapter) listview.getAdapter()).getWrappedAdapter();
								adapter.notifyDataSetChanged();
							}
						}, photoURL);
		            }
				}else if(key.equals("doctor")){
					//医生类型
					if(!TextUtils.isEmpty(value.getString("imgUrl"))) {
						photoURL = value.getString("imgUrl");
					}
					String photoName = Storage.getImageName(photoURL);
					newsMap.put("imgUrl", photoName);
					newsMap.put("name",  value.getString("name"));
					newsMap.put("score", value.getInteger("score"));
					newsMap.put("url", value.getString("url"));
					newsMap.put("pop", value.getInteger("totalNumber"));
					newsList.add(newsMap);
					if(!Text.isBlank(photoURL)) {
						// 加载图片
						Networking.doImage("image", new ImageResponse(photoName, newsList.size() - 1) {
							@Override
							public void onFinished(Bitmap content) {
								HashMap<String, Object> map = newsList.get((Integer) tag);
								map.put("image", content);
								ListView listview = (ListView) HomeActivity.this.getActivity().findViewById(R.id.home_list_news);
								SimpleAdapter adapter = (SimpleAdapter) ((HeaderViewListAdapter) listview.getAdapter()).getWrappedAdapter();
								adapter.notifyDataSetChanged();
							}
						}, photoURL);
		            }
					
				}
			}
				adapter.notifyDataSetChanged();
				page = thisPage + 1;
			}
		}, currentRegionId);
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
		}
		Log.i(TAG, "loadEntry, currentRegionId=" + currentRegionId + ",currentUserId=" + currentUserId);
	}
	
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
	 * 广告位
	 */
	private void loadBannerAd() {
		if(listNews.getHeaderViewsCount() > 0) {
			return;
		}
	    listNews.removeHeaderView(viewHead);       
        Networking.doCommand("bannerAd", new JSONResponse(HomeActivity.this.getActivity(), (Integer) currentRegionId) {
            @Override
            public void onFinished(JSONVisitor content) {
                if(null == content || content.getInteger("code", 1) < 0) {
                     return;
                }
                ICollection<JSONVisitor> data = content.getVisitors("data");
                if (data == null) {
                    return;
                }
                adList.clear();
                for(JSONVisitor banner : data){              
                    AdDomain adDomain = new AdDomain();
                    adDomain.setImgUrl(banner.getString("image"));
                    adDomain.setUrl(banner.getString("url"));
                    adDomain.setAd(true);
                    adList.add(adDomain);
                }
               
                adViewPager = (ViewPager) viewHead.findViewById(R.id.vp);
                adViewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
                // 设置一个监听器，当ViewPager中的页面改变时调用
                adViewPager.setOnPageChangeListener(new MyPageChangeListener());
//                MyAdapter adapter = (MyAdapter) adViewPager.getAdapter();
//                adapter.notifyDataSetChanged();
                listNews.addHeaderView(viewHead);
                
                addDynamicView();
            }
        }, currentRegionId);
	}


	/**
	 * 界面预设
	 */
	private void prepare() {
		
		animRight = new RotateAnimation(-30, 30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animRight.setDuration(1000);
        animRight.setAnimationListener(listener);
		animLeft = new RotateAnimation(30f, -30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animLeft.setDuration(1000);
		animLeft.setAnimationListener(listener);			
		
		//
		ImageView childImg =(ImageView)viewHead.findViewById(R.id.home_img_child);
		ImageView womanImg =(ImageView)viewHead.findViewById(R.id.home_img_woman);
		ImageView oldImg =(ImageView)viewHead.findViewById(R.id.home_img_old);
		final ImageView[] opImageViews = {childImg, womanImg, oldImg};
		final TextView[] titles = {(TextView) viewHead.findViewById(R.id.tv_title_child),
				(TextView) viewHead.findViewById(R.id.tv_title_woman),
				(TextView) viewHead.findViewById(R.id.tv_title_old)};
		final TextView[] subTitles = {(TextView) viewHead.findViewById(R.id.tv_subtitle_child),
				(TextView) viewHead.findViewById(R.id.tv_subtitle_woman),
				(TextView) viewHead.findViewById(R.id.tv_subtitle_old)};
		final RelativeLayout[] opLayouts = {
				(RelativeLayout) viewHead.findViewById(R.id.home_layout_1),
				(RelativeLayout) viewHead.findViewById(R.id.home_layout_2),
				(RelativeLayout) viewHead.findViewById(R.id.home_layout_3)
		};
		Networking.doCommand("ThreeBanner", new JSONResponse(null) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content) {
					return;
				}
				if(content.getInteger("code", 0) <= 0) {
					return;
				}
				ICollection<JSONVisitor> data = content.getVisitors("data");
				if (data == null) {
					return;
				}
				int i = 0;
				for(JSONVisitor banner : data) {
					String title = banner.getString("title");
					String subTitle = banner.getString("subtitle");
					final String url = banner.getString("url");
					String imgUrl = banner.getString("imgUrl");
					titles[i].setText(title);
					subTitles[i].setText(subTitle);
					opLayouts[i].setClickable(true);
					opLayouts[i].setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Helper.openBrowser(HomeActivity.this.getActivity(), url);
						}
					});
					// 加载图片
					String photoName = Storage.getImageName(imgUrl);
					Networking.doImage("image", new ImageResponse(photoName, (Integer)i) {
						@Override
						public void onFinished(Bitmap content) {
							opImageViews[(Integer) tag].setImageBitmap(content);
						}
					}, imgUrl);
					
					i++;
				}
			}
		}, currentRegionId);
				
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
            		if(null != Me.instance) {
            			Intent intent0 = new Intent(HomeActivity.this.getActivity(), SelectDoctorActivity.class);
                		HomeActivity.this.startActivity(intent0);
                		return;
    				}            		
            		Intent intent0 = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
					HomeActivity.this.startActivity(intent0);
				break;
            	case 1:            		
    				if(null == Me.instance) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
    					HomeActivity.this.startActivity(intent);
    					return;
    				}
    				if(null == Profile.instance().region) {
    					Intent intent = new Intent(HomeActivity.this.getActivity(), RegionActivity.class);
    					HomeActivity.this.startActivityForResult(intent, MESSAGE_REGION);
    					Toast.makeText(HomeActivity.this.getActivity(), "请设置所在小区", Toast.LENGTH_LONG).show();
    					return;
    				}
    				if(null != Me.instance&& null != Profile.instance().region){
    						Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("yuyuetijian", Profile.instance().region.id, Me.instance.token));	
    					}    				 				
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
    					return;
    				}
    				if(null != Me.instance&& null != Profile.instance().region){
    					Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("yuyueliliao", Profile.instance().region.id, Me.instance.token));
					} 
    				
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
    					return;
    				}
    				if(null != Me.instance&& null != Profile.instance().region){
    				Helper.openBrowser(HomeActivity.this.getActivity(), Networking.fetchURL("yuyueguahao", Profile.instance().region.id, Me.instance.token));
    				}
    				break;
            	case 4:
                    if(null != Me.instance){		
            		Intent intent = new Intent(HomeActivity.this.getActivity(), ArchiveActivity.class);
					intent.putExtra("password", 0);
					HomeActivity.this.startActivity(intent);
					return;
                    }                  
    					Intent intent = new Intent(HomeActivity.this.getActivity(), LoginActivity.class);
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
	    imageViews.clear();
	    dotList.clear();
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageView = new ImageView(this.getActivity());
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setTag(adList.get(i));
            imageView.setClickable(true);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.openBrowser(HomeActivity.this.getActivity(), ((AdDomain)v.getTag()).getUrl());
                }
            });
            imageViews.add(imageView);
			
			String photoURL = adList.get(i).getImgUrl();
			String photoName = Storage.getImageName(photoURL);
            if(!Text.isBlank(photoURL)) {
                // 加载图片
                Networking.doImage("image", new ImageResponse(photoName, imageView) {
                    @Override
                    public void onFinished(Bitmap content) {
                        ((ImageView)tag).setImageBitmap(content);
                        
                        ViewPager ads = (ViewPager) viewHead.findViewById(R.id.vp);
                        MyAdapter adapter = (MyAdapter) ads.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }, photoURL);
            }
			
			
			
//			// 异步加载图片
//			mImageLoader.displayImage(adList.get(i).getImgUrl(), imageView,
//					options);
//			imageView.setScaleType(ScaleType.CENTER_CROP);
//			imageView.setTag(adList.get(i));
//			imageView.setClickable(true);
//			imageView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Helper.openBrowser(HomeActivity.this.getActivity(), ((AdDomain)v.getTag()).getUrl());
//                }
//            });
//			imageViews.add(imageView);
			dots.get(i).setVisibility(View.VISIBLE);
			dotList.add(dots.get(i));
		}
		
		for (int i = adList.size()-1 ; i < dotList.size(); i++) {
		    dots.get(i).setVisibility(View.GONE);
        }
        
	}
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = imageViews.get(position);
			((ViewPager) container).addView(iv);
			final AdDomain adDomain = adList.get(position);
			// 在这个方法里面设置图片的点击事件
		/*	iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 处理跳转逻辑
					
				}
			});*/
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
	 * 处理资讯
	 */
	private void dealNews() {		
		SimpleAdapter listItemAdapter = new SimpleAdapter(this.getActivity(), newsList, R.layout.listitem_hot_news,
			new String[]{"image", "name", "price", "originalPrice","score","pop"}, 
	        new int[]{R.id.reserve_image_photo, R.id.reserve_lable_title, R.id.reserve_lable_newprice, R.id.reserve_lable_oldprice,R.id.reaserve_lable_docLevel,R.id.reserve_lable_number});
		listItemAdapter.setViewBinder(new ViewBinder() {
			@SuppressWarnings("unused")
			public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView)view;
                    Bitmap bitmap = (Bitmap) data;
                    imageView.setImageDrawable(new BitmapDrawable(bitmap));
                    return true;
                }
                else if(view instanceof ImageView && data instanceof Integer) {  
                	ImageView textView = (ImageView)view;
	                switch((Integer) data) {
	                case 0:
	                	textView.setBackgroundResource(R.drawable.star_0);
	            		break;
	                case 1:
	                	textView.setBackgroundResource(R.drawable.star_1);
	            		break;
	            	case 2:
	            		textView.setBackgroundResource(R.drawable.star_2);
	            		break;
	            	case 3:
	            		textView.setBackgroundResource(R.drawable.star_3);
	            		break;
	            	case 4:
	            		textView.setBackgroundResource(R.drawable.star_4);
	            		break;
	            	case 5:
	            		textView.setBackgroundResource(R.drawable.star_5);
	            		break;	
	            	}
	                return true;
                }
                else if(view instanceof TextView && data instanceof Integer) {
	                	TextView textView = (TextView)view;	 	                	
	                	if(null == data && 0 == (Integer) data) {
	                		textView.setText("");	
			    	}else {			    		
				    		if("old".equals(textView.getTag())) {
				    			textView.setText("￥"+String.valueOf(data));
				    			OldPrice = (Integer)data;
		                		textView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); //中间横线   	                	
	                		}else if("new".equals(textView.getTag())){
	                			textView.setText("￥"+String.valueOf(data));
	                			NewPrice = (Integer)data;
	                		}else if("people".equals(textView.getTag())){
	                			textView.setText(String.valueOf(data)+"人");	                		
	                		}				    	
				    		return true;
				    	}	
                }  
                return false;
			}});
		listNews.setAdapter(listItemAdapter);
		listNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				index = index-1;
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