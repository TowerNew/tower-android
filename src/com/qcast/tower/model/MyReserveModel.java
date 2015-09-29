package com.qcast.tower.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qcast.tower.R;
import com.qcast.tower.logic.Logic;
import com.qcast.tower.logic.Storage;

/**
 * Created by zhengningchuan on 15/9/22.
 */
public class MyReserveModel {
    public int id;
    public int type;
    public String date;
    public String span;
    public int status;
    public int subId;
    public String name;
    public String photoUrl;
    public String userGlobalId;
    public String memo;
    public String photoName =null;
    public String discount = null;

    public Bitmap getPhoto() {
    	if(1 == type) {
            return Storage.getImage(photoName);
    	}
    	else {
    		return BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.askdoctor_self);
    	}
    }
}
