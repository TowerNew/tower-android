package com.qcast.tower.business;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import com.qcast.tower.Program;
import com.qcast.tower.business.structure.Region;
import com.qcast.tower.framework.Storage;
import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.type.List;
import com.slfuture.pluto.etc.Version;

/**
 * 个人设置
 */
public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 当前小区
	 */
	private static Profile instance = null;
	
	/**
	 * 当前小区
	 */
	public Region region = null;
	/**
	 * 启动页图片名
	 */
	public String poster = null;
	/**
	 * 搜索过的关键词
	 */
	public List<String> keywords = null;


	/**
	 * 获取实例
	 * 
	 * @return 实例
	 */
	public static Profile instance() {
		if(null == instance) {
			try {
				if(file().exists()) {
					instance = load();
				}
				else {
					instance = new Profile();
				}
			}
			catch (IOException e) { }
			if(null == instance.keywords) {
				instance.keywords = new List<String>();
			}
		}
		return instance;
	}

	/**
	 * 获取存储文件
	 * 
	 * @return 存储文件
	 */
	public static File file() {
		return new File(Storage.DATA_ROOT + "profile." + Version.fetchVersion(Program.application).toString() + ".dat");
	}

	/**
	 * 保存
	 */
	public void save() throws IOException {
		Serial.restore(instance, file());
	}

	/**
	 * 读取
	 * 
	 * @return 返回存储的对象
	 */
	private static Profile load() throws IOException {
		try {
			return Serial.extract(file(), Profile.class);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}
}
