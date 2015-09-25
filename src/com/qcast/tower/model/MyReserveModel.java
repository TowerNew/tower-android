package com.qcast.tower.model;

import android.graphics.Bitmap;

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

    public Bitmap getPhoto() {
        return Storage.getImage(photoName);
    }
}
