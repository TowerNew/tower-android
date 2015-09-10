package com.qcast.tower.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhengningchuan on 15/9/1.
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
    public Bitmap docImage;
}
