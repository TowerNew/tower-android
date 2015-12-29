package com.qcast.tower.form;

import android.widget.Button;
import android.widget.ImageButton;

import com.qcast.tower.R;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

/**
 * 评价窗口
 */
@ResourceView(id = R.layout.activity_comment)
public class CommentActivity extends ActivityEx {
	@ResourceView(id = R.id.comment_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.comment_button_confirm)
	public Button btnConfirm;
	
	
}
