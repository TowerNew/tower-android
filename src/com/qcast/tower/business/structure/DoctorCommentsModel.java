package com.qcast.tower.business.structure;

import com.qcast.tower.framework.Storage;

import android.graphics.Bitmap;

/**
 * Created by zhengningchuan on 15/9/4.
 */
public class DoctorCommentsModel {
	
    public String userName;
    public String date;
    public String content;
    public int score;
    public String photoName;
    public String imageUrl;
    public Bitmap getPhoto() {
        return Storage.getImage(photoName);
}
}