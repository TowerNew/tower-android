package com.qcast.tower.business.structure;

import android.graphics.Bitmap;

import java.io.Serializable;

import com.qcast.tower.framework.Storage;

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
