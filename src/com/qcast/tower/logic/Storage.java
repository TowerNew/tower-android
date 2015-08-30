package com.qcast.tower.logic;

import java.io.File;

import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.storage.SDCard;

/**
 * 存储器
 */
public class Storage {
	/**
	 * 路径常量
	 */
	public final static String ROOT_NAME = "tower";
	public final static String STORAGE_ROOT = SDCard.root() + ROOT_NAME;
	public final static String IMAGE_ROOT = STORAGE_ROOT + "/image/";
	
	
	/**
	 * 构造函数
	 */
	private Storage() { }

	/**
	 * 获取指定图片码的路径
	 * 
	 * @param code 图片码
	 * @return 图片路径
	 */
	public static String imagePath(String code) {
		File dir = new File(IMAGE_ROOT);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		return IMAGE_ROOT + code;
	}
	
	/**
	 * 获取指定URL中的图片名称
	 * 
	 * @param url 图片URL
	 * @return 图片名称
	 */
	public static String getImageName(String url) {
		if(Text.isBlank(url)) {
			return null;
		}
		int i = url.lastIndexOf("/");
		if(-1 == i) {
			return url;
		}
		return url.substring(i + 1);
	}
}
