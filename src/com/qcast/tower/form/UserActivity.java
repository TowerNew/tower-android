package com.qcast.tower.form;

import java.util.HashMap;
import java.util.LinkedList;

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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 用户界面
 */
public class UserActivity extends Fragment {
	/**
	 * 用户面板静态功能个数
	 */
	public final static int USER_BOARD_COUNT = 5;
	
	/**
	 * 用户面板列表
	 */
	private LinkedList<HashMap<String, Object>> userBoardList = new LinkedList<HashMap<String, Object>>();
	/**
	 * 是否加载
	 */
	private boolean isLoad = false;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_user, container, true);
	}

	@Override
	public void onStart() {
		super.onStart();
		//
		prepare();
		//
		load();
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
				if(null != Logic.token) {
					Intent intent = new Intent(UserActivity.this.getActivity(), UserInfoActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				Intent intent = new Intent(UserActivity.this.getActivity(), LoginActivity.class);
				UserActivity.this.startActivity(intent);
			}
		});
		//
		dealList();
	}

	/**
	 * 加载用户信息
	 */
	public void load() {
		if(isLoad) {
			return;
		}
		Host.doCommand("userboardlist", new CommonResponse<String>() {
			@Override
			public void onFinished(String content) {
				if(Response.CODE_SUCCESS != code()) {
					Toast.makeText(UserActivity.this.getActivity(), "网络异常", Toast.LENGTH_LONG).show();
					return;
				}
				JSONObject resultObject = JSONObject.convert(content);
				if(((JSONNumber) resultObject.get("code")).intValue() <= 0) {
					Toast.makeText(UserActivity.this.getActivity(), ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				isLoad = true;
				JSONArray result = (JSONArray) resultObject.get("data");
				for(IJSON item : result) {
					JSONObject newJSONObject = (JSONObject) item;
					String icon = ((JSONString) newJSONObject.get("icon")).getValue();
					String caption = ((JSONString) newJSONObject.get("caption")).getValue();
					String url = ((JSONString) newJSONObject.get("url")).getValue();
					HashMap<String, Object> map = new HashMap<String, Object>();
					String iconName = Storage.getImageName(icon);
					map.put("icon", iconName);
					map.put("caption", caption);
					map.put("url", url);
					userBoardList.add(map);
					// 加载图片
		            Host.doImage("image", new ImageResponse(iconName, userBoardList.size() - 1) {
						@Override
						public void onFinished(Bitmap content) {
							HashMap<String, Object> map = userBoardList.get((Integer) tag);
							map.put("icon", content);
						}
		            }, icon);
				}
				ListView listview = (ListView) UserActivity.this.getActivity().findViewById(R.id.user_list);
				SimpleAdapter adapter = (SimpleAdapter) listview.getAdapter();
				adapter.notifyDataSetChanged();
			}
		}, Logic.token);
	}

	/**
	 * 处理咨询
	 */
	private void dealList() {
		HashMap<String, Object> map = null;
		//
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_healthmanage));
		map.put("caption", "健康管理");
		userBoardList.add(map);
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_family));
		map.put("caption", "我的家庭");
		userBoardList.add(map);
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_packet));
		map.put("caption", "我的钱包");
		userBoardList.add(map);
        map = new HashMap<String, Object>();
        map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_inquiry));
        map.put("caption", "我的问诊");
        userBoardList.add(map);
		map = new HashMap<String, Object>();
		map.put("icon", BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.user_icon_reserve));
		map.put("caption", "我的预约");
		userBoardList.add(map);
		//
		ListView listview = (ListView) this.getActivity().findViewById(R.id.user_list);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this.getActivity(), userBoardList, R.layout.listview_user,
			new String[]{"icon", "caption"}, 
	        new int[]{R.id.user_listview_icon, R.id.user_listview_caption});
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
				if(null == Logic.token) {
					Toast.makeText(UserActivity.this.getActivity(), "尚未登录", Toast.LENGTH_LONG).show();
					return;
				}
				if(0 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), HealthManageActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				else if(1 == index) {
					Intent intent = new Intent(UserActivity.this.getActivity(), FamilyActivity.class);
					UserActivity.this.startActivity(intent);
					return;
				}
				else if(2 == index) {
                    Intent intent = new Intent(UserActivity.this.getActivity(), MyWalletActivity.class);
                    UserActivity.this.startActivity(intent);
                    return;
                }
				else if(3 == index) {
                    Intent intent = new Intent(UserActivity.this.getActivity(), MyInquiryDoctorActivity.class);
                    UserActivity.this.startActivity(intent);
                    return;
                }
				else if(4 == index) {
                    // Intent intent = new Intent(UserActivity.this.getActivity(), MyReserveHistoryActivity.class);
                    // UserActivity.this.startActivity(intent);
                    return;
                }
				else if(index >= USER_BOARD_COUNT) {
					String url = (String) (userBoardList.get(index).get("url"));
					openWeb(url);
					return;
				}
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
	
	/**
	 * 打开浏览器
	 * 
	 * @param url 地址
	 */
	private void openWeb(String url) {
		Intent intent = new Intent(UserActivity.this.getActivity(), WebActivity.class);
		intent.putExtra("url", url);
		UserActivity.this.getActivity().startActivity(intent);
	}
}
