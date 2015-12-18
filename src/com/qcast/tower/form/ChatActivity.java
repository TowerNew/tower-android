package com.qcast.tower.form;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.qcast.tower.R;
import com.qcast.tower.adapter.MessageAdapter;
import com.qcast.tower.logic.Logic;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.qcast.tower.model.ChatMessage;
import com.slfuture.carrie.base.time.DateTime;
import com.slfuture.pluto.communication.Host;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天对话框
 */
public class ChatActivity extends Activity {
	/**
	 * 消息接收器
	 */
	public class MessageBroadcastReceiver extends BroadcastReceiver {
		/**
		 * 消息接收回调
		 */
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String messageId = intent.getStringExtra("msgid");
	        EMMessage message = EMChatManager.getInstance().getMessage(messageId);
	        String from = intent.getStringExtra("from");
	        if(!remoteId.equals(from)) {
	        	return;
	        }
	        switch(message.getType()) {
	        case TXT:
	        	TextMessageBody textBody = (TextMessageBody) message.getBody();
	        	onMessage(from, textBody.getMessage());
	        	break;
	        case IMAGE:
	        	ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
	        	Host.doImage("image", new ImageResponse(imageBody.getFileName(), from) {
					@Override
					public void onFinished(Bitmap content) {
						onMessage((String)tag, content);
					}
				}, imageBody.getThumbnailUrl());
	        	break;
	        case VOICE:
	        	break;
	        case VIDEO:
	        	break;
	        default:
	        	break;
	        }
	        abortBroadcast();
	    }
	}

	
	/**
	 * 关闭按钮
	 */
	private Button btnClose;
	/**
	 * 聊天列表
	 */
    private ListView listMessages;
	/**
	 * 更多按钮
	 */
	private ImageView btnMore;
	/**
	 * 消息文本框
	 */
	private EditText txtMessage;
	/**
	 * 发送按钮
	 */
	private ImageView btnSend;
	/**
	 * 对方ID
	 */
	private String remoteId;
	/**
	 * 对方ID
	 */
	private String remoteNickName;
	/**
	 * 消息内容列表
	 */
	private ArrayList<ChatMessage> messages = null;
	/**
	 * 会话
	 */
	private EMConversation conversation = null;
	/**
	 * 消息接收器
	 */
	private MessageBroadcastReceiver receiver = null;
	/**
	 * 列表内容适配器
	 */
	private MessageAdapter adapter = null;
	/**
	 * 句柄
	 */
	private Handler handler = null;


	/**
	 * 窗口构建
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // 准备参数
        prepareData();
        // 准备控件
        prepareClose();
        prepareMessages();
        prepareMore();
        txtMessage = (EditText) findViewById(R.id.chat_text_message);
        prepareSend();
        loadHistory();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
        handler = null;
        terminateIM();
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case 1:
				if(RESULT_OK != resultCode || null == data) {
					return;
				}
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null, null, null);
				if(cursor.moveToFirst()) {
					File imageFile = new File(cursor.getString(1));
			        onMessage("我", BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
			        //
			        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
			        ImageMessageBody body = new ImageMessageBody(imageFile);
			        message.addBody(body);
			        message.setReceipt(remoteId);
			        conversation.addMessage(message);
			        try {
						EMChatManager.getInstance().sendMessage(message);
					}
			        catch (EaseMobException e) { }
				}
				cursor.close();
				break;
		}
    }
   
    /**
     * 准备数据
     */
    private void prepareData() {
    	remoteId = getIntent().getStringExtra("remoteId");
    	remoteNickName = getIntent().getStringExtra("remoteNickName");
    	messages = new ArrayList<ChatMessage>();
    	adapter = new MessageAdapter(this, messages);
    	handler = new Handler();
    	Logic.messageFamily.remove(remoteId);
    	initializeIM();
    }

    /**
     * 准备关闭按钮
     */
    private void prepareClose() {
    	btnClose = (Button) findViewById(R.id.chat_button_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatActivity.this.finish();
			}
        });
    }

    /**
     * 准备消息列表
     */
    private void prepareMessages() {
    	listMessages = (ListView) findViewById(R.id.chat_listview_messages);
    	listMessages.setAdapter(adapter);
    }

    /**
     * 准备更多按钮
     */
    private void prepareMore() {
    	btnMore = (ImageView) findViewById(R.id.chat_button_more);
    	btnMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击更多
				showMore();
			}
        });
    }

    /**
     * 准备发送按钮
     */
    private void prepareSend() {
    	btnSend = (ImageView) findViewById(R.id.chat_button_send);
    	btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发送消息
				send();
			}
        });
    }

    /**
     * 文字消息到达回调
     * 
     * @param id 消息发送者ID，null表示自己
     * @param message 文字消息
     */
    private void onMessage(String id, String message) {
    	ChatMessage entity = new ChatMessage();
        entity.setMessage(message);
        if(id.equals(remoteId)) {
        	entity.setSelf(false);
            entity.setNickName(remoteNickName);
        }
        else {
        	entity.setSelf(true);
            entity.setNickName("我");
        }
        entity.setTime(DateTime.now());
        messages.add(entity);
        adapter.notifyDataSetChanged();
        listMessages.setSelection(listMessages.getCount() - 1);
    }

    /**
     * 图片消息到达回调
     * 
     * @param id 消息发送者ID，null表示自己
     * @param message 图片
     */
    private void onMessage(String id, Bitmap message) {
    	ChatMessage entity = new ChatMessage();
        entity.setImage(message);
        if(id.equals(remoteId)) {
        	entity.setSelf(false);
            entity.setNickName(remoteNickName);
        }
        else {
        	entity.setSelf(true);
            entity.setNickName("我");
        }
        entity.setTime(DateTime.now());
        messages.add(entity);
        adapter.notifyDataSetChanged();
        listMessages.setSelection(listMessages.getCount() - 1);
    }

    /**
     * 发送消息
     */
    private void send() {
        String content = txtMessage.getText().toString();
        if(content.equals("")) {
        	return;
        }
        txtMessage.setText("");
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtMessage.getWindowToken(), 0) ;
        // 投递数据
        onMessage("我", content);
        //
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        TextMessageBody txtBody = new TextMessageBody(content);
        message.addBody(txtBody);
        message.setReceipt(remoteId);
        conversation.addMessage(message);
        try {
			EMChatManager.getInstance().sendMessage(message);
		}
        catch (EaseMobException e) { }
    }
    
    /**
	 * 选择更多功能
	 * 
	 * @param activity 上下文
	 */
	private void showMore() {
		final AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.BOTTOM);
		window.setAttributes(layoutParams);
		window.setContentView(R.layout.dialog_chat_more);
		TextView labelCancel = (TextView) window.findViewById(R.id.chatmore_label_cancel);
		labelCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.cancel();
			}
		});
		TextView labelImage = (TextView) window.findViewById(R.id.chatmore_label_image);
		labelImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				ChatActivity.this.startActivityForResult(intent, 1);
				alertDialog.hide();
			}
		});
		TextView labelAudio = (TextView) window.findViewById(R.id.chatmore_label_audio);
		labelAudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatActivity.this, VoiceActivity.class);
				intent.putExtra("userId", remoteId);
				intent.putExtra("userName", remoteNickName);
				intent.putExtra("mode", true);
				ChatActivity.this.startActivity(intent);
				alertDialog.hide();
			}
		});
		TextView labelVideo = (TextView) window.findViewById(R.id.chatmore_label_video);
		labelVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatActivity.this, VideoActivity.class);
				intent.putExtra("userId", remoteId);
				intent.putExtra("userName", remoteNickName);
				intent.putExtra("mode", true);
				ChatActivity.this.startActivity(intent);
				alertDialog.hide();
			}
		});
	}
	
	/**
	 * 加载历史记录
	 */
	public void loadHistory() {
    	List<EMMessage> messages = conversation.getAllMessages();
    	for(EMMessage message : messages) {
    		switch(message.getType()) {
	        case TXT:
	        	TextMessageBody textBody = (TextMessageBody) message.getBody();
	        	onMessage(message.getFrom(), textBody.getMessage());
	        	break;
	        case IMAGE:
	        	ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
	        	if(null == imageBody.getFileName() || null == imageBody.getThumbnailUrl()) {
	        		return;
	        	}
	        	Host.doImage("image", new ImageResponse(imageBody.getFileName(), message.getFrom()) {
					@Override
					public void onFinished(Bitmap content) {
						onMessage((String)tag, content);
					}
				}, imageBody.getThumbnailUrl());
	        	break;
	        case VOICE:
	        	break;
	        case VIDEO:
	        	break;
	        default:
	        	break;
	        }
    	}
	}
	
    /**
     * 初始化IM
     */
    public void initializeIM() {
    	receiver = new MessageBroadcastReceiver();
    	IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
    	intentFilter.setPriority(3);
    	registerReceiver(receiver, intentFilter);
    	conversation = EMChatManager.getInstance().getConversation(remoteId);
    }
 
    /**
     * 终止IM
     */
    public void terminateIM() {
    	unregisterReceiver(receiver);
    }
}
