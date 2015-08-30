package com.qcast.tower.logic;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qcast.tower.R;
import com.qcast.tower.logic.response.CommonResponse;
import com.qcast.tower.logic.response.ImageResponse;
import com.qcast.tower.logic.response.Response;
import com.qcast.tower.logic.response.core.IResponse;
import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.type.safe.Table;
import com.slfuture.pluto.config.Configuration;
import com.slfuture.pluto.config.core.IConfig;
import com.slfuture.pluto.net.HttpLoader;
import com.slfuture.pluto.net.future.FileFuture;
import com.slfuture.pluto.net.future.TextFuture;

/**
 * 通信服务器
 */
public class Host {
	/**
	 * 主机交互包类
	 */
	public static class HostBundle {
		/**
		 * 目标类
		 */
		public Class<?> clazz = null;
		/**
		 * 内容
		 */
		public Object content = null;
		/**
		 * 反馈对象
		 */
		public Response response = null;
		/**
		 * 句柄
		 */
		public HostHandler handler = null;
	}


	/**
	 * 主机文本回调
	 */
	public static class HostTextFuture extends TextFuture {
		/**
		 * 键
		 */
		public int key = 0;
		
		
		/**
		 * 构造函数
		 * 
		 * @param commandResponse 回执
		 * @param handler 句柄
		 */
		public HostTextFuture(CommonResponse<String> commandResponse, HostHandler handler) {
			key = Serial.makeLoopInteger();
			HostBundle textBundle = new HostBundle();
			textBundle.clazz = String.class;
			textBundle.response = commandResponse;
			textBundle.handler = handler;
			hostBundles.put(key, textBundle);
		}

		/**
		 * 设置状态
		 * 
		 * @param status 新状态
		 */
		@Override
		public void setStatus(int status) {
			super.setStatus(status);
			Message message = new Message();
			message.what = key;
			if(STATUS_COMPLETED == status) {
				HostBundle textBundle = hostBundles.get(key);
				textBundle.response.setCode(IResponse.CODE_SUCCESS);
				textBundle.content = this.text;
				textBundle.handler.sendMessage(message);
			}
			else if(STATUS_TIMEOUT == status) {
				HostBundle textBundle = hostBundles.get(key);
				textBundle.response.setCode(IResponse.CODE_TIMEOUT);
				textBundle.handler.sendMessage(message);
			}
			else if(STATUS_ERROR == status) {
				HostBundle textBundle = hostBundles.get(key);
				textBundle.response.setCode(IResponse.CODE_ERROR);
				textBundle.handler.sendMessage(message);
			}
		}
	}


	/**
	 * 主机文件回调
	 */
	public static class HostFileFuture extends FileFuture {
		/**
		 * 键
		 */
		public int key = 0;
		
		
		/**
		 * 构造函数
		 * 
		 * @param commandResponse 回执
		 * @param handler 句柄
		 * @param file 文件对象
		 * @param clazz 目标类
		 */
		public HostFileFuture(CommonResponse<?> commandResponse, HostHandler handler, File file, Class<?> clazz) {
			key = Serial.makeLoopInteger();
			HostBundle fileBundle = new HostBundle();
			fileBundle.response = commandResponse;
			fileBundle.handler = handler;
			this.file = file;
			fileBundle.clazz = clazz;
			hostBundles.put(key, fileBundle);
		}

		/**
		 * 设置状态
		 * 
		 * @param status 新状态
		 */
		@Override
		public void setStatus(int status) {
			super.setStatus(status);
			Message message = new Message();
			message.what = key;
			if(STATUS_COMPLETED == status) {
				HostBundle fileBundle = hostBundles.get(key);
				fileBundle.response.setCode(IResponse.CODE_SUCCESS);
				if(fileBundle.clazz.equals(Bitmap.class)) {
					fileBundle.content = BitmapFactory.decodeFile(this.file.getAbsolutePath());
				}
				else {
					fileBundle.content = this.file;
				}
				fileBundle.handler.sendMessage(message);
			}
			else if(STATUS_TIMEOUT == status) {
				HostBundle fileBundle = hostBundles.get(key);
				fileBundle.response.setCode(IResponse.CODE_TIMEOUT);
				fileBundle.handler.sendMessage(message);
			}
			else if(STATUS_ERROR == status) {
				HostBundle fileBundle = hostBundles.get(key);
				fileBundle.response.setCode(IResponse.CODE_ERROR);
				fileBundle.handler.sendMessage(message);
			}
		}
	}

	/**
	 * 通信回调线程通知句柄
	 */
	public static class HostHandler extends Handler {
		/**
		 * 处理消息
		 * 
		 * @param msg 消息对象
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Log.i("TOWER", "HostHandler HOST MESSAGE");
			HostBundle bundle = hostBundles.get(msg.what);
			hostBundles.delete(msg.what);
			if(null == bundle.content) {
				((CommonResponse<?>)(bundle.response)).onFinished(null);
			}
			else {
				if(bundle.clazz.equals(String.class)) {
					((CommonResponse<String>)(bundle.response)).onFinished((String)bundle.content);
				}
				else if(bundle.clazz.equals(File.class)) {
					((CommonResponse<File>)(bundle.response)).onFinished((File) bundle.content);
				}
				else if(bundle.clazz.equals(Bitmap.class)) {
					((CommonResponse<Bitmap>)(bundle.response)).onFinished((Bitmap) bundle.content);
				}
			}
			super.handleMessage(msg);
		}
	}


	/**
	 * 消息结束
	 */
	public final static int MESSAGE_FINISHED = 9527;
	/**
	 * 命令信道
	 */
	private static HttpLoader command = null;
	/**
	 * 物料信道
	 */
	private static HttpLoader material = null;
	/**
	 * 协议映射
	 */
	private static Table<String, Protocol> protocols = new Table<String, Protocol>();
	/**
	 * 域名
	 */
	public static String domain = null;
	/**
	 * 当前是否正在模拟
	 */
	public static boolean isMock = false;
	/**
	 * 主机句柄
	 */
	private static final ThreadLocal<HostHandler> hostHandlers = new ThreadLocal<HostHandler>();
	/**
	 * 回调包映射
	 */
	private static Table<Integer, HostBundle> hostBundles = new Table<Integer, HostBundle>();


	/**
	 * 初始化
	 * 
	 * @return 执行结果
	 */
	public static boolean initialize() {
		domain = Configuration.root().visit("/program/server").get("domain");
		if("true".equalsIgnoreCase(Configuration.root().visit("/program/server").get("mock"))) {
			isMock = true;
		}
		else {
			isMock = false;
		}
		protocols.clear();
		for(IConfig conf : Configuration.root().visits("/protocols/protocol")) {
			String urlTemplate = null;
			if(null != conf.get("url")) {
				urlTemplate = conf.get("url");
			}
			else if(null != conf.get("path")) {
				urlTemplate = "http://" + domain + "/" + conf.get("path");
			}
			protocols.put(conf.get("name"), Protocol.build(urlTemplate, conf.get("mock")));
		}
		return true;
	}

	/**
	 * 销毁
	 */
	public static void terminate() {
		if(null != command) {
			command.terminate();
			command = null;
		}
		if(null != material) {
			material.terminate();
			material = null;
		}
		protocols.clear();
	}

	/**
	 * 获取有效命令信道
	 * 
	 * @return 命令信道
	 */
	public static HttpLoader command() {
		if(null == command) {
			synchronized(Host.class) {
				if(null == command) {
					command = new HttpLoader();
					command.initialize(2);
				}
			}
		}
		return command;
	}

	/**
	 * 获取有效物料信道
	 * 
	 * @return 物料信道
	 */
	public static HttpLoader material() {
		if(null == material) {
			synchronized(Host.class) {
				if(null == material) {
					material = new HttpLoader();
					material.initialize(5);
				}
			}
		}
		return material;
	}

	/**
	 * 执行网络命令
	 * 
	 * @param protocol 协议名称
	 * @param commandResponse 回调
	 * @param parameters 参数列表
	 */
	public static void doCommand(String protocol, CommonResponse<String> commandResponse, Object... parameters) {
		if(isMock) {
			commandResponse.setCode(Response.CODE_SUCCESS);
			commandResponse.onFinished(protocols.get(protocol).mock);
			return;
		}
		HostHandler hostHandler = hostHandlers.get();
		if(null == hostHandler) {
			hostHandler = new HostHandler();
			hostHandlers.set(hostHandler);
		}
		protocols.get(protocol).invoke(command(), new HostTextFuture(commandResponse, hostHandler), parameters);
	}

	/**
	 * 执行图片下载命令
	 * 
	 * @param protocol 协议名称
	 * @param imageResponse 图片回调
	 * @param parameters 参数列表
	 */
	public static void doImage(String protocol, ImageResponse imageResponse, Object... parameters) {
		if(imageResponse.file().exists()) {
			imageResponse.setCode(Response.CODE_SUCCESS);
			imageResponse.onFinished(BitmapFactory.decodeFile(imageResponse.file().getAbsolutePath()));
			return;
		}
		if(isMock) {
			imageResponse.setCode(Response.CODE_SUCCESS);
			imageResponse.onFinished(BitmapFactory.decodeResource(Logic.application.getResources(), R.drawable.image_default));
			return;
		}
		HostHandler hostHandler = hostHandlers.get();
		if(null == hostHandler) {
			hostHandler = new HostHandler();
			hostHandlers.set(hostHandler);
		}
		protocols.get(protocol).invoke(material(), new HostFileFuture(imageResponse, hostHandler, imageResponse.file(), Bitmap.class), parameters);
	}
}
