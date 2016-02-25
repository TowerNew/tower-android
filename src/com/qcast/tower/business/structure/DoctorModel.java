package com.qcast.tower.business.structure;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

import com.qcast.tower.framework.Storage;

/**
 * 医生结构体
 */
public class DoctorModel implements Serializable {
    public String imageUrl;
    public String name;
    public String title;
    public String department;
    public ArrayList<String> services;
    public int level;
    public String doctorId;
    public boolean isFamous;
    public boolean isAsk;
    public boolean isPre;
    public String description;
    public String resume;
    public String photoName = null;
    public int goodCount;
    public int badCount;
    
    public String imUsername = null;
    public String imGroupId = null;
    
    public Bitmap getPhoto() {
    	return Storage.getImage(photoName);
    }
}
