package com.qcast.tower.business.structure;


/**
 * 广告轮播图
 * 
 * @author ltt
 * 
 */
public class AdDomain {
	private String id; 
	private String imgUrl;
	private String url;
	public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private boolean isAd; 
	private String startTime;
	private String endTime; 
	private String targetUrl;
	private int width;
	private int height;
	private boolean available;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public boolean isAd() {
		return isAd;
	}

	public void setAd(boolean isAd) {
		this.isAd = isAd;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
	

}

