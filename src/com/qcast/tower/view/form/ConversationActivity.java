package com.qcast.tower.view.form;

import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.general.utility.GeneralHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qcast.tower.R;
import com.qcast.tower.business.Me;
import com.qcast.tower.business.core.IMeListener;
import com.qcast.tower.business.structure.IM;
import com.qcast.tower.business.user.Friend;
import com.qcast.tower.business.user.User;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

@ResourceView(id = R.layout.activity_conversation)
public class ConversationActivity extends FragmentEx implements IMeListener {
	@ResourceView(id = R.id.conversation_image_add)
	public ImageView imgAdd;
	@ResourceView(id = R.id.conversation_layout_doctor)
	public ViewGroup viewDoctor;
	@ResourceView(id = R.id.conversation_list_family)
	public ListView listFamily;

	/**
	 * 数据列表
	 */
	private List<Map<String, Object>> conversationList = new ArrayList<Map<String, Object>>();


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//
		imgAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog alertDialog = new AlertDialog.Builder(ConversationActivity.this.getActivity()).create();
				alertDialog.show();
				Window window = alertDialog.getWindow();
				WindowManager.LayoutParams layoutParams = window.getAttributes();
				layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
				window.setGravity(Gravity.BOTTOM);
				window.setAttributes(layoutParams);
				window.setContentView(R.layout.dialog_addcontact);
				TextView labelCancel = (TextView) window.findViewById(R.id.addcontact_label_cancel);
				labelCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						alertDialog.cancel();
					}
				});
				TextView labRelative = (TextView) window.findViewById(R.id.addcontact_label_relative);
				labRelative.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ConversationActivity.this.getActivity(), AddRelativeActivity.class);
						ConversationActivity.this.getActivity().startActivity(intent);
						alertDialog.hide();
					}
				});
				TextView labFriend = (TextView) window.findViewById(R.id.addcontact_label_friend);
				labFriend.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ConversationActivity.this.getActivity(), AddFriendActivity.class);
						ConversationActivity.this.getActivity().startActivity(intent);
						alertDialog.hide();
					}
				});
			}
		});
		viewDoctor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null == Me.instance || null == Me.instance.doctor) {
					Intent intent = new Intent(ConversationActivity.this.getActivity(), SelectDoctorActivity.class);
					ConversationActivity.this.getActivity().startActivity(intent);
					return;
				}
				Me.instance.doChat(ConversationActivity.this.getActivity(), null, Me.instance.doctor.imId);
			}
		});
		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), 
				conversationList,
                R.layout.listitem_conversation,
                new String[] {"photo", "name", "tip", "hasphone", "hastv"},
                new int[] {R.id.listitem_conversation_image_photo, R.id.listitem_conversation_label_name, R.id.listitem_conversation_label_tip, R.id.listitem_conversation_image_hasphone, R.id.listitem_conversation_image_hastv});
		listFamily.setAdapter(adapter);
		listFamily.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(0 == position) {
					pop(Me.instance);
					return;
				}
				else if(Me.instance.friends.size() < position) {
					return;
				}
				pop(Me.instance.friends.get(position - 1));
			}
		});
		listFamily.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				if(0 == position) {
					return true;
				}
				GeneralHelper.showSelector(ConversationActivity.this.getActivity(), new IEventable<Integer>() {
					@Override
					public void on(Integer index) {
						if(0 == index) {
							Intent intent = new Intent(ConversationActivity.this.getActivity(), AddFriendActivity.class);
							intent.putExtra("userId", Me.instance.friends.get(position - 1).id);
							ConversationActivity.this.getActivity().startActivity(intent);
						}
						else if(1 == index) {
							Host.doCommand("removefamily", new JSONResponse(ConversationActivity.this.getActivity()) {
								@Override
								public void onFinished(JSONVisitor content) {
									if(null == content || content.getInteger("code", 0) <= 0) {
										return;
									}
									Me.instance.refreshMember(ConversationActivity.this.getActivity(),  new IEventable<Boolean>() {
										@Override
										public void on(Boolean result) {
											if(!result) {
												return;
											}
											ConversationActivity.this.refreshList();
										}
									});
								}
							}, Me.instance.token, Me.instance.friends.get(position - 1).id);
						}
					}
				}, "编  辑", "删  除", "", "取  消");
				return true;
			}
		});
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {  
			    if(view instanceof ImageView) {
			    	if(null ==  data) {
			    		((ImageView) view).setImageResource(R.drawable.drawable_null);
			    	}
			    	else if(data instanceof Bitmap) {
			    		((ImageView) view).setImageBitmap((Bitmap) data);
			    	}
			        return true;
			    }
			    if(view instanceof TextView) {
			    	TextView text = (TextView) view;
			    	if(text.getId() == R.id.listitem_conversation_label_tip) {
				    	if(null == data || 0 == (Integer) data) {
				    		text.setVisibility(View.GONE);
				    	}
				    	else {
				    		text.setVisibility(View.VISIBLE);
				    		text.setText(String.valueOf(data));
				    	}
			    	}
			    	else {
			    		if(null != data) {
				    		text.setText(String.valueOf(data));
			    		}
			    	}
			        return true;
			    }
		        return false;
			}
		});
    }

	@Override
    public void onResume() {
		super.onResume();
		refreshList();
   }

	/**
	 * 刷新列表
	 */
	private void refreshList() {		
		if(null == Me.instance) {
			conversationList.clear();
			((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
			return;
		}
		conversationList.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", Me.instance.id);
		if(null == Me.instance.photo) {
			map.put("photo", GraphicsHelper.decodeResource(ConversationActivity.this.getActivity(), R.drawable.icon_user_default));
		}
		else {
			Host.doImage("image", new ImageResponse(Me.instance.photo, map) {
				@SuppressWarnings("unchecked")
				@Override
				public void onFinished(Bitmap content) {
					((Map<String, Object>) tag).put("photo", content);
					((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
				}
			}, Me.instance.photo);
		}
		map.put("name", "我");
		map.put("tip", Me.instance.unreadMessageCount());
		if(null != Me.instance.fetchIMId(IM.TYPE_PHONE)) {
			map.put("hasphone", GraphicsHelper.decodeResource(ConversationActivity.this.getActivity(), R.drawable.icon_phone));
		}
		if(null != Me.instance.fetchIMId(IM.TYPE_TV)) {
			map.put("hastv", GraphicsHelper.decodeResource(ConversationActivity.this.getActivity(), R.drawable.icon_tv));
		}
		conversationList.add(map);
		for(Friend friend : Me.instance.friends) {
			map = new HashMap<String, Object>();
			map.put("id", friend.id);
			if(null == friend.photo) {
				map.put("photo", GraphicsHelper.decodeResource(ConversationActivity.this.getActivity(), R.drawable.icon_user_default));
			}
			else {
				Host.doImage("image", new ImageResponse(friend.photo, map) {
					@SuppressWarnings("unchecked")
					@Override
					public void onFinished(Bitmap content) {
						((Map<String, Object>) tag).put("photo", content);
						((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
					}
				}, friend.photo);
			}
			map.put("name", friend.nickname());
			map.put("tip", friend.unreadMessageCount());
			if(null != friend.fetchIMId(IM.TYPE_PHONE)) {
				map.put("hasphone", GraphicsHelper.decodeResource(ConversationActivity.this.getActivity(), R.drawable.icon_phone));
			}
			if(null != friend.fetchIMId(IM.TYPE_TV)) {
				map.put("hastv", GraphicsHelper.decodeResource(ConversationActivity.this.getActivity(), R.drawable.icon_tv));
			}
			conversationList.add(map);
		}
		((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
	}

	/**
	 * 弹出菜单
	 * 
	 * @param user 用户
	 */
	private void pop(final User user) {
		if(0 == user.im.size()) {
			return;
		}
		else if(1 == user.im.size()) {
			if(user == Me.instance) {
				return;
			}
			Me.instance.doChat(ConversationActivity.this.getActivity(), null, user.im.get(0).imId);
			return;
		}
		ArrayList<String> list = new ArrayList<String>();
		for(IM item : user.im) {
			if(Me.instance.fetchIMId(IM.TYPE_PHONE).equals(item.imId)) {
				continue;
			}
			list.add(item.title);
		}
		if(0 == list.size()) {
			return;
		}
		list.add(null);
		list.add("取  消");
		GeneralHelper.showSelector(ConversationActivity.this.getActivity(), new IEventable<Integer>() {
			@Override
			public void on(Integer index) {
				if(Me.instance == user) {
					index++;
				}
				int i = -1;
				for(IM item : user.im) {
					i++;
					if(index == i) {
						Me.instance.doChat(ConversationActivity.this.getActivity(), null, item.imId);
					}
				}
			}
		}, list.toArray(new String[0]));
	}

	@Override
	public void onConflict() {
		conversationList.clear();
		((SimpleAdapter) listFamily.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onCommand(String from, String action, Table<String, Object> data) {
		refreshList();
	}
}
