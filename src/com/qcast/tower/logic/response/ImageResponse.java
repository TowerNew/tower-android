package com.qcast.tower.logic.response;

import java.io.File;

import com.qcast.tower.logic.Storage;

import android.graphics.Bitmap;

/**
 * 图片反馈
 */
public abstract class ImageResponse extends CommonResponse<Bitmap> {
	/**
	 * 附属信息
	 */
	public Object tag = null;
	/**
	 * 图片码
	 */
	protected String imageCode = null;


	/**
	 * 构造函数
	 * 
	 * @param imageCode 图片码
	 * @param tag 附属信息
	 */
	public ImageResponse(String imageCode, Object tag) {
		this.imageCode = imageCode;
		this.tag = tag;
	}

	/**
	 * 获取下载的文件对象
	 * 
	 * @return 文件对象
	 */
	public File file() {
		return new File(Storage.imagePath(imageCode));
	}
}
