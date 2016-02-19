package com.qcast.tower.framework;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.slfuture.carrie.base.character.Encoding;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.core.ILink;
import com.slfuture.pluto.storage.SDCard;

/**
 * 存储器
 */
public class Storage {
	/**
	 * 路径常量
	 */
	public final static String ROOT_NAME = "tower";
	public final static String STORAGE_ROOT = SDCard.root() + ROOT_NAME + "/";
	public final static String IMAGE_ROOT = STORAGE_ROOT + "image/";
	public final static String DATA_ROOT = STORAGE_ROOT + "data/";
	public final static String VOICE_ROOT = STORAGE_ROOT + "voice/";

	/**
	 * 用户相关信息
	 */
	private static ConcurrentHashMap<String, Object> user = null;


	/**
	 * 构造函数
	 */
	private Storage() { }
	
	/**
	 * 获取图片目录
	 * 
	 * @return 图片目录
	 */
	public static String imageFolder() {
		File file = new File(IMAGE_ROOT);
		if(!file.exists()) {
			file.mkdirs();
		}
		return IMAGE_ROOT;
	}
	/**
	 * 获取数据目录
	 * 
	 * @return 数据目录
	 */
	public static String dataFolder() {
		File file = new File(DATA_ROOT);
		if(!file.exists()) {
			file.mkdirs();
		}
		return DATA_ROOT;
	}
	/**
	 * 获取语音目录
	 * 
	 * @return 语音目录
	 */
	public static String voiceFolder() {
		File file = new File(VOICE_ROOT);
		if(!file.exists()) {
			file.mkdirs();
		}
		return VOICE_ROOT;
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @return 文件后缀名
	 */
	public static String getFileSuffix(String path) {
		int i = path.lastIndexOf(".");
		if(-1 == i) {
			return "";
		}
		return path.substring(i);
	}

	/**
	 * 获取指定图片码的路径
	 * 
	 * @param code 图片码
	 * @return 图片路径
	 */
	public static String imagePath(String fileName) {
		File dir = new File(IMAGE_ROOT);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		return IMAGE_ROOT + fileName;
	}

	/**
	 * 获取指定名称的图片
	 * 
	 * @param fileName 图片名称
	 * @return 位图
	 */
	public static Bitmap getImage(String fileName) {
		if(!existImage(fileName)) {
			return null;
		}
		return BitmapFactory.decodeFile(IMAGE_ROOT + fileName);
	}

	/**
	 * 判断是否存储了指定名称的图片
	 * 
	 * @param fileName 图片名称
	 * @return 是否存储了该图片
	 */
	public static boolean existImage(String fileName) {
		return (new File(IMAGE_ROOT + fileName)).exists();
	}

	   /** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public static void saveFile(Bitmap bm, String fileName) throws IOException { 
    	String path = IMAGE_ROOT + fileName;
        File myCaptureFile = new File(path);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
        bos.flush();  
        bos.close();  
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
	
	/**
	 * 获取用户相关信息
	 * 
	 * @param key 键
	 * @param clazz 返回值类型
	 * @return 返回值
	 */
	public static <T> T user(String key, Class<T> clazz) {
		if(!(new File(IMAGE_ROOT)).exists()) {
			(new File(IMAGE_ROOT)).mkdirs();
		}
		if(null == user) {
			synchronized(Storage.class) {
				if(null == user) {
					user = new ConcurrentHashMap<String, Object>();
					String text = null;
					try {
						text = Text.loadFile(DATA_ROOT + "user", Encoding.ENCODING_UTF8);
					}
					catch (Exception ex) {
						Log.e("TOWER", "load user failed", ex);
					}
					if(Text.isBlank(text)) {
						return null;
					}
					JSONObject object = JSONObject.convert(text);
					for(ILink<String, IJSON> link : object) {
						if(link.destination() instanceof JSONNumber) {
							JSONNumber number = (JSONNumber) link.destination();
							if(null == number) {
								continue;
							}
							user.put(link.origin(), number.doubleValue());
						}
						else if(link.destination() instanceof JSONString) {
							JSONString string = (JSONString) link.destination();
							if(null == string) {
								return null;
							}
							user.put(link.origin(), string.getValue());
						}
					}
				}
			}
		}
		return (T) user.get(key);
	}
	
	/**
	 * 设置用户信息
	 * 
	 * @param key 键 
	 * @param value 值
	 */
	public static void setUser(String key, Object value) {
		if(null == user) {
			user = new ConcurrentHashMap<String, Object>();
		}
		if(null == value) {
			user.remove(key);
		}
		else {
			user.put(key, value);
		}
		save();
	}

	/**
	 * 保存相关信息
	 */
	public static void save() {
		if(null == user) {
			return;
		}
		File dir = new File(DATA_ROOT);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		JSONObject object = new JSONObject();
		for(Entry<String, Object> link : user.entrySet()) {
			if(null == link.getValue()) {
				continue;
			}
			if(link.getValue() instanceof Integer) {
				object.put(link.getKey(), new JSONNumber((Integer) link.getValue()));
			}
			else if(link.getValue() instanceof Double) {
				object.put(link.getKey(), new JSONNumber((Double) link.getValue()));
			}
			else if(link.getValue() instanceof String) {
				object.put(link.getKey(), new JSONString((String) link.getValue()));
			}
		}
		try {
			OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(DATA_ROOT + "user"), "UTF-8");
			fw.write(object.toString() + "\n");
			fw.flush();
			fw.close();
		}
		catch(Exception ex) { }
	}
}
