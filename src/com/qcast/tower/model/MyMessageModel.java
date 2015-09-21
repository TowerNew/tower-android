package com.qcast.tower.model;

import java.io.Serializable;

/**
 * Created by zhengningchuan on 15/9/17.
 */
public class MyMessageModel implements Serializable{

    public int id;
    public boolean hasRead;
    public String title;
    public String time;
    public int type;
    public String name;
    public String phone;
    public int requestId;
    public String relation;


}
