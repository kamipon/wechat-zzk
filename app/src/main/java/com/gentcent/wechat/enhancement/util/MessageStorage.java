package com.gentcent.wechat.enhancement.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.gentcent.wechat.enhancement.MyApplication;
import com.gentcent.wechat.enhancement.bean.MessageBean;
import com.gentcent.wechat.enhancement.wcdb.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class MessageStorage {
	//发送消息的队列
	public static List<MessageBean> getSendMessageQueue(){
		SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences(HookParams.WECHAT_ENHANCEMENT_CONFIG_NAME, Context.MODE_WORLD_WRITEABLE);
		String sendMessageQueueStr = sharedPreferences.getString("sendMessageQueue", "[]");
		XLog.d("getSendMessageQueue:" + sendMessageQueueStr);
		return GsonUtils.GsonToList(sendMessageQueueStr, MessageBean.class);
	}
	
	public static boolean setSendMessageQueque(List<MessageBean> sendMessageQueue){
		try{
			SharedPreferences.Editor editor = MyApplication.getAppContext().getSharedPreferences(HookParams.WECHAT_ENHANCEMENT_CONFIG_NAME, Context.MODE_WORLD_WRITEABLE).edit();
			XLog.d("setSendMessageQueque:" + GsonUtils.GsonString(sendMessageQueue));
			editor.putString("sendMessageQueue", GsonUtils.GsonString(sendMessageQueue));
			editor.commit();
		}catch (Exception e){
			XLog.e(e.toString());
			return false;
		}
		return true;
	}
}
