package com.qcast.tower.model;

import android.graphics.Bitmap;

import com.qcast.tower.logic.Storage;

import java.io.Serializable;

/**
 * Created by zhengningchuan on 15/9/16.
 */
public class MyChatHistoryModel implements Serializable {
    public String userId;
    public int id;
    public String docName;
    public String docId;
    public String topic;
    public String time;
    public int status;
    public String photoName =null;
    public String imageUrl;

    public Bitmap getPhoto() {
        return Storage.getImage(photoName);
    }
}
