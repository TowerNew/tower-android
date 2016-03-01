package com.qcast.tower.view.control;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * 滚动视图
 */
public class HorizontalScrollViewEx extends HorizontalScrollView {
	public interface HorizontalScrollViewListenner {
		public void onScrollChanged(int x, int y, int oldx, int oldy);
	}
	
	protected HorizontalScrollViewListenner listenner = null;
	
	public void setHorizontalScrollViewListenner(HorizontalScrollViewListenner listenner) {
		this.listenner = listenner;
	}
	
	public HorizontalScrollViewEx(Context context) {
		super(context);
	}
    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }
    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);  
    }
    @Override  
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(null != listenner) {
        	listenner.onScrollChanged(x, y, oldx, oldy);
        }
    }
}
