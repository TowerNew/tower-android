package com.qcast.tower.model;

import android.graphics.Bitmap;

import com.qcast.tower.logic.Storage;

import java.io.Serializable;

/**
 * 问诊历史
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

    public String imUsername = null;
    public String imGroupId = null;
    
    public Bitmap getPhoto() {
        return Storage.getImage(photoName);
    }
}
