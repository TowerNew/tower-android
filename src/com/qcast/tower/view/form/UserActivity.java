package com.qcast.tower.view.form;

import java.util.HashMap;
import java.util.LinkedList;

import com.qcast.tower.Program;
import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.framework.Helper;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;

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
import android.view.LayoutInflater;
import android.view.View;
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

	/**
	 * 用户面板列表
	 */
	private LinkedList<HashMap<String, Object>> itemList = new LinkedList<HashMap<String, Object>>();


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		prepare();
	}

	@Override
	public void onResume() {
		super.onResume();
		TextView txtCaption = (TextView) this.getActivity().findViewById(R.id.user_text_caption);
		if(null == Me.instance) {
			txtCaption.setText("点击登录");
		}
		else {
			txtCaption.setText(Me.instance.nickname);
		}
		if(null == Me.instance) {
			btnPhoto.setBackgroundResource(R.drawable.user_photo_null);
		}
		else {
            Host.doImage("image", new ImageResponse(Me.instance.photoUrl) {
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

	/**
	 * 界面预处理
	 */
	public void prepare() {
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
		dealList();
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
		map.put("icon", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_user_mycollection));
		map.put("caption", "我的收藏");
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
		map.put("icon", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_user_config));
		map.put("caption", "系统设置");
		itemList.add(map);
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Program.application.getResources(), R.drawable.icon_user_suggest));
		map.put("caption", "意见反馈");
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
					if(null == Me.instance) {
						Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
						UserActivity.this.startActivity(intent);
						return;
					}
					String url = Host.fetchURL("MyCollection", Me.instance.token);
					Helper.openBrowser(UserActivity.this.getActivity(), url);
					return;
				}
				else if(3 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), ArchiveActivity.class);
					intent.putExtra("password", 0);
					UserActivity.this.startActivity(intent);
					return;
				}
				else if(4 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), SelectDoctorActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				else if(5 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), ConfigActivity.class);
					UserActivity.this.startActivity(intent);
                    return;
                }
				else if(6 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), SuggestActivity.class);
                    UserActivity.this.startActivity(intent);
                    return;
                }
            }
		});
		viewMemo1 = viewHead.findViewById(R.id.memo_layout_1);
		viewMemo1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance) {
					Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
					UserActivity.this.getActivity().startActivity(intent);
					return;
				}
				String url = Host.fetchURL("daipingfen", Me.instance.token);
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
				String url = Host.fetchURL("daifukuan", Me.instance.token);
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
				String url = Host.fetchURL("daiyuyue", Me.instance.token);
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
				String url = Host.fetchURL("MyOrder", Me.instance.token);
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
				String url = Host.fetchURL("MyMoney", Me.instance.token);
				Helper.openBrowser(UserActivity.this.getActivity(), url);
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
