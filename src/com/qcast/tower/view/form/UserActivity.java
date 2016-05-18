package com.qcast.tower.view.form;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

import com.qcast.tower.Program;
import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.Profile;
import com.qcast.tower.framework.Helper;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.general.view.form.ImageActivity;
import com.slfuture.pretty.qcode.Module;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 用户界面
 */
@ResourceView(id = R.layout.activity_user)
public class UserActivity extends FragmentEx {
	@ResourceView(id = R.id.user_button_photo)
	public ImageButton btnPhoto;
	@ResourceView(id = R.id.memo_layout_1)
	public View viewMemo1;
	@ResourceView(id = R.id.memo_layout_2)
	public View viewMemo2;
	@ResourceView(id = R.id.memo_layout_3)
	public View viewMemo3;
	@ResourceView(id = R.id.user_layout_order)
	public View viewOrder;
	@ResourceView(id = R.id.user_layout_money)
	public View viewMoney;
	//收藏和签到
	@ResourceView(id = R.id.user_layout_01)
	public RelativeLayout viewMycollection;
	@ResourceView(id = R.id.user_layout_02)
	public RelativeLayout viewSignin;
	//认证状态
	@ResourceView(id = R.id.user_status)
	public ImageView viewStatus;
	//二维码
	@ResourceView(id = R.id.userinfo_image_qcode)
	public ImageView viewQcode;
	//积分
	@ResourceView(id = R.id.money_text_points)
	public TextView viewpoint;
	//账户余额
	@ResourceView(id = R.id. user_text_balance)
	public TextView viewBalance;
	//会员等级
	@ResourceView(id = R.id.user_vip)
	public ImageView viewVip;
	@ResourceView(id = R.id.money_layout_01)
	public RelativeLayout layout_Point;
	@ResourceView(id = R.id.money_layout_00)
	public RelativeLayout layout_Coupons;
	
	@ResourceView(id = R.id.user_layout_10)
	public RelativeLayout viewMyfooter10;
	@ResourceView(id = R.id.user_layout_20)
	public RelativeLayout viewMyfooter20;
	@ResourceView(id = R.id.user_layout_30)
	public RelativeLayout viewMyfooter30;
	/**
	 * 用户面板列表
	 */
	private LinkedList<HashMap<String, Object>> itemList = new LinkedList<HashMap<String, Object>>();	
	/**
	 * 当前审核状态
	 */
	private int authorityStatus = 0;
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		prepare();
		
	}
	@Override
	public void onResume() {
		super.onResume();
		TextView txtCaption = (TextView) this.getActivity().findViewById(R.id.user_label_caption);
		if(null == Me.instance) {
			txtCaption.setText("点击登录");
			viewStatus.setVisibility(View.INVISIBLE);
			viewQcode.setVisibility(View.INVISIBLE);
			viewVip.setVisibility(View.INVISIBLE);
		}
		else {			
			txtCaption.setText(Me.instance.nickname());
			viewStatus.setVisibility(View.VISIBLE);
			viewQcode.setVisibility(View.VISIBLE);
			viewVip.setVisibility(View.VISIBLE);							
			//获取用户认证状态				
			Me.instance.fetchAuthorityStatus(new IEventable<com.slfuture.carrie.base.type.Table<String, Object>>() {
				@Override
				public void on(Table<String, Object> event) {				
					if(null == event) {
						authorityStatus = 0;
						viewStatus.setBackgroundResource(R.drawable.button_status0);
						return;
					}
					if(null==event.get("status")){
						return;
					}
					authorityStatus = (Integer) event.get("status");
					switch(authorityStatus) {
					case 1:
						viewStatus.setBackgroundResource(R.drawable.button_status1);
						break;
					case 2:
						viewStatus.setBackgroundResource(R.drawable.button_status2);
						break;
					case 3:
						viewStatus.setBackgroundResource(R.drawable.button_status3);
						break;
					}
				}
			});	
			}												
		if(null == Me.instance) {
			btnPhoto.setImageResource(R.drawable.user_photo_null);
		}
		else {
			if(0==Me.instance.level){
				return;
			}else {
				String point= String.valueOf(Me.instance.point);
				String account= String.valueOf(Me.instance.account);
				viewpoint.setText(point+"分");
				viewBalance.setText(account+"元");
				 switch(Me.instance.level){
				 case 1:					 
					 viewVip.setImageResource(R.drawable.icon_v1);
					 break;
				 case 2:
					 viewVip.setImageResource(R.drawable.icon_v2);
					 break;
				 case 3:
					 viewVip.setImageResource(R.drawable.icon_v3);
					 break;
				 case 4:
					 viewVip.setImageResource(R.drawable.icon_v4);
					 break;
				 }
				 }
			if(null == Me.instance.photoUrl) {
				btnPhoto.setImageResource(R.drawable.user_photo_default);
				return;
			}
			else{
            Networking.doImage("image", new ImageResponse(Me.instance.photoUrl) {
				@Override
				public void onFinished(Bitmap content) {
					if(null == content) {
						return;
					}
					btnPhoto.setImageBitmap(GraphicsHelper.makeImageRing(GraphicsHelper.makeCycleImage(content, 200, 200), Color.WHITE, 4));
				}
            }, Me.instance.photoUrl);
		}
			}
		
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {			
		dealList();			
		btnPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), UserInfoActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
				UserActivity.this.startActivity(intent);
			}
		});
		viewMycollection.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				String url = Networking.fetchURL("MyCollection", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
				return;
				
			}
		});
		viewSignin.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(null != Me.instance) {					
						String	token = Me.instance.token;
		    				Helper.openBrowser(UserActivity.this.getActivity(), Networking.fetchURL("activity3", token));
		    				return;
						}				
				Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
				UserActivity.this.startActivity(intent);
				
			}
		});
		viewQcode.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				String path = com.qcast.tower.framework.Storage.IMAGE_ROOT + "qcode." + Me.instance.phone + ".png";
				if(!(new File(path)).exists()) {
					Bitmap bitmap = Module.createQRImage("add://" + Me.instance.phone, 500, 500);
					if(null == bitmap) {
						return;
					}
					FileOutputStream stream = null;
					try {
						stream = new FileOutputStream(new File(path));
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						stream.flush();
					}
					catch(Exception ex) {
						Log.e("TOWER", "保存二维码失败", ex);
					}
					finally {
						if(null != stream) {
							try {
								stream.close();
							}
							catch(Exception ex) { }
						}
						stream = null;
					}
				}
				Intent intent = new Intent(UserActivity.this.getActivity(), ImageActivity.class);
				intent.putExtra("path", path);
				UserActivity.this.startActivity(intent);						
			}
		});
	/*	viewVip.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(null != Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LevelVipActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
				UserActivity.this.startActivity(intent);	
				
			}
		});*/
	}
	/**
	 * 处理咨询
	 */
	private void dealList() {
		HashMap<String, Object> map = null;
		//
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_user_myfamily));
		map.put("caption", "家庭成员");
		itemList.add(map);	
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_user_archive));
		map.put("caption", "健康档案");
		itemList.add(map);
        map = new HashMap<String, Object>();
        map.put("icon", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_user_doctor));
        map.put("caption", "私人医生");
        itemList.add(map);
        map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_user_medication));
		map.put("caption", "用药提醒");
		itemList.add(map);		
		//
		ListView listview = (ListView) this.getActivity().findViewById(R.id.user_list);
		if(listview.getHeaderViewsCount() > 0) {
			return;
		}
		View viewHead = LayoutInflater.from(this.getActivity()).inflate(R.layout.div_user_head, null);
		listview.addHeaderView(viewHead);					
		SimpleAdapter listItemAdapter = new SimpleAdapter(this.getActivity(), itemList, R.layout.listitem_user,
			new String[]{"icon", "caption"}, 
	        new int[]{R.id.listitem_user_image_icon, R.id.listitem_user_label_caption});
		listItemAdapter.setViewBinder(new ViewBinder() {
			@SuppressWarnings("deprecation")
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
				if(1 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), FamilyActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}		
				else if(2 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), ArchiveActivity.class);
					intent.putExtra("password", 0);
					UserActivity.this.startActivity(intent);
					return;
				}
				else if(3 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), SelectDoctorActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}else if(4 == index){
					String token = "";
    				if(null != Me.instance) {
    					token = Me.instance.token;
    				}
    				String regionId = "";
    				if(null != Profile.instance().region) {
    					regionId = String.valueOf(Profile.instance().region.id);
    				}
    				Helper.openBrowser(UserActivity.this.getActivity(), Networking.fetchURL("activity1", token, regionId));
    				return;
				}				
            }
		});
		btnPhoto = (ImageButton) viewHead.findViewById(R.id.user_button_photo);
		viewMycollection = (RelativeLayout) viewHead.findViewById(R.id.user_layout_01);
		viewSignin = (RelativeLayout) viewHead.findViewById(R.id.user_layout_02);
		
		viewQcode = (ImageView) viewHead.findViewById(R.id.userinfo_image_qcode);
		viewStatus = (ImageView) viewHead.findViewById(R.id.user_status);
		
		viewBalance = (TextView) viewHead.findViewById(R.id.user_text_balance);
		viewpoint = (TextView) viewHead.findViewById(R.id.money_text_points);
		viewVip = (ImageView) viewHead.findViewById(R.id.user_vip);	

		layout_Point = (RelativeLayout) viewHead.findViewById(R.id.money_layout_01);
		layout_Point.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.getActivity().startActivity(intent);
					return;
				}
				String url = Networking.fetchURL("jifen", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
			}
		});
		layout_Coupons = (RelativeLayout) viewHead.findViewById(R.id.money_layout_00);	
		viewMemo1 = viewHead.findViewById(R.id.memo_layout_1);
		viewMemo1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.getActivity().startActivity(intent);
					return;
				}
				String url = Networking.fetchURL("daipingfen", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
			}
		});
		viewMemo2 = viewHead.findViewById(R.id.memo_layout_2);
		viewMemo2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.getActivity().startActivity(intent);
					return;
				}
				String url = Networking.fetchURL("daifukuan", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
			}
		});
		viewMemo3 = viewHead.findViewById(R.id.memo_layout_3);
		viewMemo3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.getActivity().startActivity(intent);
					return;
				}
				String url = Networking.fetchURL("daiyuyue", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
			}
		});
		viewOrder = viewHead.findViewById(R.id.user_layout_order);
		viewOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.getActivity().startActivity(intent);
					return;
				}
				String url = Networking.fetchURL("MyOrder", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
			}
		});
		viewMoney = viewHead.findViewById(R.id.user_layout_money);
		viewMoney.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.getActivity().startActivity(intent);
					return;
				}
				String url = Networking.fetchURL("MyMoney", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
			}
		});	
	
		View viewFooter = LayoutInflater.from(this.getActivity()).inflate(R.layout.div_user_footer, null);
		listview.addFooterView(viewFooter);
		viewMyfooter10 = (RelativeLayout) viewFooter.findViewById(R.id.user_layout_10);
		viewMyfooter20 = (RelativeLayout) viewFooter.findViewById(R.id.user_layout_20);
		viewMyfooter30 = (RelativeLayout) viewFooter.findViewById(R.id.user_layout_30);
		viewMyfooter10.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserActivity.this.getActivity(), ConfigActivity.class);
				UserActivity.this.startActivity(intent);
                return;					
			}
		});
		viewMyfooter20.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserActivity.this.getActivity(), SuggestActivity.class);
                UserActivity.this.startActivity(intent);
                return;
			}
		});
		viewMyfooter30.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String token = "";
				if(null != Me.instance) {
					token = Me.instance.token;
				}
				String regionId = "";
				if(null != Profile.instance().region) {
					regionId = String.valueOf(Profile.instance().region.id);
				}
				Helper.openBrowser(UserActivity.this.getActivity(), Networking.fetchURL("activity1", token, regionId));
				return;	
			}
		});
	}

	/**
	 * 根据原图和变长绘制圆形图片
	 * 
	 * @param source
	 * @param min
	 * @return
	 */
	public Bitmap transferCircleImage(Bitmap source, int min) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 首先绘制圆形
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * 使用SRC_IN
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}
}
