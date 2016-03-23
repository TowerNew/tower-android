package com.qcast.tower.view.form;

import com.qcast.tower.R;
import com.slfuture.pretty.general.view.form.RadioActivity;

import android.graphics.drawable.BitmapDrawable;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 身体界面
 */
public class BodyActivity extends Activity {
	/**
	 * 图片ID列表
	 */
	private int[] idMaleFront = {R.id.male_front_arm, R.id.male_front_basin, R.id.male_front_belly, R.id.male_front_chest, R.id.male_front_head, R.id.male_front_leg, R.id.male_front_neck};
	private int[] idMaleBack = {R.id.male_back_arm, R.id.male_back_back, R.id.male_back_butt, R.id.male_back_head, R.id.male_back_leg, R.id.male_back_neck};
	private int[] idFemaleFront = {R.id.female_front_arm, R.id.female_front_basin, R.id.female_front_belly, R.id.female_front_chest, R.id.female_front_head, R.id.female_front_leg, R.id.female_front_neck};
	private int[] idFemaleBack = {R.id.female_back_arm, R.id.female_back_back, R.id.female_back_butt, R.id.female_back_head, R.id.female_back_leg, R.id.female_back_neck};

	/**
	 */
	private boolean isFinished = false;
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TOWER", "BodyActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_body);
		isFinished = false;
		// 界面处理
		prepare();
	}

	/**
	 * 过滤器回调
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
			switch(current) {
			case 0:
				this.select(true, true);
				break;
			case 1:
				this.select(true, false);
				break;
			case 2:
				this.select(false, true);
				break;
			case 3:
				this.select(false, false);
				break;
			}
		}
	}
	
	/**
	 * 界面预处理
	 */
	public void prepare() {
		dealReturn();
		dealImage();
		//
		select(true, true);
	}
	
	/**
	 * 处理返回按钮
	 */
	public void dealReturn() {
		ImageButton button = (ImageButton) this.findViewById(R.id.body_button_return);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BodyActivity.this.setResult(Activity.RESULT_CANCELED);
				BodyActivity.this.finish();
			}
		});
		TextView text = (TextView) this.findViewById(R.id.body_text_filter);
		text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BodyActivity.this, RadioActivity.class);
				intent.putExtra("title", "过滤");
				String[] items = {"男性-正面", "男性-背面", "女性-正面", "女性-背面"};
				intent.putExtra("items", items);
				intent.putExtra("index", -1);
				BodyActivity.this.startActivityForResult(intent, 1);
			}
		});
	}

	/**
	 * 处理返回按钮
	 */
	public void dealImage() {
		View.OnTouchListener onTouchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				ImageView image = (ImageView) view;
				// Bitmap bitmap = BitmapFactory.decodeResource(BodyActivity.this.getResources(), getDrawableId(image.getId()));
				// Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
				Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
				int x = (int)(event.getX() * bitmap.getWidth() / image.getWidth());
				int y = (int)(event.getY() * bitmap.getHeight() / image.getHeight());
				if(0 != bitmap.getPixel(x, y)) {
					// 设置返回值
//					Intent intent = new Intent();
//					intent.putExtra("body", image.getId());
//					BodyActivity.this.setResult(Activity.RESULT_OK, intent);
//					BodyActivity.this.finish();
					if(isFinished) {
						return true;
					}
					Intent intent = new Intent(BodyActivity.this, SelfDiagnosticActivity.class);
					intent.putExtra("body", image.getId());
					BodyActivity.this.startActivity(intent);
					BodyActivity.this.finish();
					isFinished = true;
					return true;
				}
                return false;
			}
        };
        ImageView image = null;
        for(int id : idMaleFront) {
			image = (ImageView) this.findViewById(id);
			image.setOnTouchListener(onTouchListener);
		}
		for(int id : idMaleBack) {
			image = (ImageView) this.findViewById(id);
			image.setOnTouchListener(onTouchListener);
		}
		for(int id : idFemaleFront) {
			image = (ImageView) this.findViewById(id);
			image.setOnTouchListener(onTouchListener);
		}
		for(int id : idFemaleBack) {
			image = (ImageView) this.findViewById(id);
			image.setOnTouchListener(onTouchListener);
		}
	}

	/**
	 * 清理切面
	 */
	public void clear() {
		ImageView image = null;
		image = (ImageView) this.findViewById(R.id.avatar_male_front);
		image.setVisibility(View.GONE);
		image = (ImageView) this.findViewById(R.id.avatar_male_back);
		image.setVisibility(View.GONE);
		image = (ImageView) this.findViewById(R.id.avatar_female_front);
		image.setVisibility(View.GONE);
		image = (ImageView) this.findViewById(R.id.avatar_female_back);
		image.setVisibility(View.GONE);
		//
		for(int id : idMaleFront) {
			image = (ImageView) this.findViewById(id);
			image.setVisibility(View.GONE);
			image.setImageBitmap(null);
		}
		for(int id : idMaleBack) {
			image = (ImageView) this.findViewById(id);
			image.setVisibility(View.GONE);
			image.setImageBitmap(null);
		}
		for(int id : idFemaleFront) {
			image = (ImageView) this.findViewById(id);
			image.setVisibility(View.GONE);
			image.setImageBitmap(null);
		}
		for(int id : idFemaleBack) {
			image = (ImageView) this.findViewById(id);
			image.setVisibility(View.GONE);
			image.setImageBitmap(null);
		}
	}
	
	/**
	 * 获取图像资源ID
	 * 
	 * @param id ImageView的ID
	 */
	private int getDrawableId(int id) {
		switch(id) {
		case R.id.male_front_arm:
			return R.drawable.male_front_arm;
		case R.id.male_front_basin:
			return R.drawable.male_front_basin;
		case R.id.male_front_belly:
			return R.drawable.male_front_belly;
		case R.id.male_front_chest:
			return R.drawable.male_front_chest;
		case R.id.male_front_head:
			return R.drawable.male_front_head;
		case R.id.male_front_leg:
			return R.drawable.male_front_leg;
		case R.id.male_front_neck:
			return R.drawable.male_front_neck;
		case R.id.male_back_arm:
			return R.drawable.male_back_arm;
		case R.id.male_back_back:
			return R.drawable.male_back_back;
		case R.id.male_back_butt:
			return R.drawable.male_back_butt;
		case R.id.male_back_head:
			return R.drawable.male_back_head;
		case R.id.male_back_leg:
			return R.drawable.male_back_leg;
		case R.id.male_back_neck:
			return R.drawable.male_back_neck;
		case R.id.female_front_arm:
			return R.drawable.female_front_arm;
		case R.id.female_front_basin:
			return R.drawable.female_front_basin;
		case R.id.female_front_belly:
			return R.drawable.female_front_belly;
		case R.id.female_front_chest:
			return R.drawable.female_front_chest;
		case R.id.female_front_head:
			return R.drawable.female_front_head;
		case R.id.female_front_leg:
			return R.drawable.female_front_leg;
		case R.id.female_front_neck:
			return R.drawable.female_front_neck;
		case R.id.female_back_arm:
			return R.drawable.female_back_arm;
		case R.id.female_back_back:
			return R.drawable.female_back_back;
		case R.id.female_back_butt:
			return R.drawable.female_back_butt;
		case R.id.female_back_head:
			return R.drawable.female_back_head;
		case R.id.female_back_leg:
			return R.drawable.female_back_leg;
		case R.id.female_back_neck:
			return R.drawable.female_back_neck;
		}
		return 0;
	}

	/**
	 * 选择切面
	 * 
	 * @param gender 男为True，女为False
	 * @param face 正面为True，反面为False
	 */
	public void select(boolean gender, boolean face) {
		clear();
		// 设置图层可见性
		TextView text = (TextView) this.findViewById(R.id.body_text_filter);
		//
		ImageView image = null;
		if(gender) {
			if(face) {
				text.setText("男性-正面");
				image = (ImageView) this.findViewById(R.id.avatar_male_front);
				image.setVisibility(View.GONE);
				for(int id : idMaleFront) {
					image = (ImageView) this.findViewById(id);
					image.setImageResource(getDrawableId(id));
					image.setVisibility(View.VISIBLE);
				}
			}
			else {
				text.setText("男性-背面");
				image = (ImageView) this.findViewById(R.id.avatar_male_back);
				image.setVisibility(View.GONE);
				for(int id : idMaleBack) {
					image = (ImageView) this.findViewById(id);
					image.setImageResource(getDrawableId(id));
					image.setVisibility(View.VISIBLE);
				}
			}
		}
		else {
			if(face) {
				text.setText("女性-正面");
				image = (ImageView) this.findViewById(R.id.avatar_female_front);
				image.setVisibility(View.GONE);
				for(int id : idFemaleFront) {
					image = (ImageView) this.findViewById(id);
					image.setImageResource(getDrawableId(id));
					image.setVisibility(View.VISIBLE);
				}
			}
			else {
				text.setText("女性-背面");
				image = (ImageView) this.findViewById(R.id.avatar_female_back);
				image.setVisibility(View.GONE);
				for(int id : idFemaleBack) {
					image = (ImageView) this.findViewById(id);
					image.setImageResource(getDrawableId(id));
					image.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
