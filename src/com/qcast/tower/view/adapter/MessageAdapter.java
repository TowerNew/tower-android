package com.qcast.tower.view.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcast.tower.R;
import com.qcast.tower.business.structure.ChatMessage;

/**
 * 列表适配器
 */
public class MessageAdapter extends BaseAdapter {
	/**
	 * 对话元素试图
	 */
	public static class ChatItemView {
		/**
		 * 对话昵称
		 */
		public TextView nickName;
		/**
		 * 消息内容
		 */
		public TextView text;
		/**
		 * 消息图片
		 */
		public ImageView image;
		/**
		 * 是否是自己投递
		 */
		public boolean isSelf = true;
	}
	
	
	/**
	 * 消息列表
	 */ 
	private ArrayList<ChatMessage> messages;
	/**
	 * 渲染器
	 */
	private LayoutInflater inflater;


	/**
	 * 构造函数
	 * 
	 * @param context 上下文
	 * @param messages 消息列表
	 */
	public MessageAdapter(Context context, ArrayList<ChatMessage> messages) {
		this.messages = messages;
		inflater = LayoutInflater.from(context);
	}
	
	public int getCount() {
		return messages.size();
	}

	public Object getItem(int position) {
		return messages.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getViewTypeCount() {
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage entity = messages.get(position);
		ChatItemView chatItemView = new ChatItemView();
		if(entity.isSelf()) {
			convertView = inflater.inflate(R.layout.div_message_left, null);
			chatItemView.nickName = (TextView) convertView.findViewById(R.id.message_left_label_nickname);
			chatItemView.text = (TextView) convertView.findViewById(R.id.message_left_label_message);
			chatItemView.image = (ImageView) convertView.findViewById(R.id.message_left_image_message);
		}
		else {
			convertView = inflater.inflate(R.layout.div_message_right, null);
			chatItemView.nickName = (TextView) convertView.findViewById(R.id.message_right_label_nickname);
			chatItemView.text = (TextView) convertView.findViewById(R.id.message_right_label_message);
			chatItemView.image = (ImageView) convertView.findViewById(R.id.message_right_image_message);
		}
		chatItemView.nickName.setText(entity.getNickName());
		if(null == entity.getMessage()) {
			chatItemView.text.setText("");
		}
		else {
			chatItemView.text.setText(entity.getMessage());
			chatItemView.text.setVisibility(View.VISIBLE);
		}
		if(null == entity.getImage()) {
			chatItemView.image.setVisibility(View.GONE);
		}
		else {
			chatItemView.image.setImageBitmap(entity.getImage());
			chatItemView.image.setVisibility(View.VISIBLE);
			chatItemView.text.setVisibility(View.GONE);
		}
		return convertView;
	}
}
