package com.qcast.tower.business.structure;

import java.io.Serializable;

import com.qcast.tower.Program;
import com.qcast.tower.R;
import com.qcast.tower.framework.Storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
	public Bitmap docImage;
//    public Bitmap getPhoto() {
//        return Storage.getImage(photoName);       
//}
	public Bitmap portrait;
}