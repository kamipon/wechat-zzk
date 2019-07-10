package com.gentcent.wechat.enhancement.util;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.gentcent.wechat.enhancement.bean.MessageBean;
import com.gentcent.wechat.enhancement.wcdb.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class MessageStorage {
	
	//发送消息的队列
	public static List<MessageBean> sendMessageQueue = new ArrayList();
	
	//发送消息的队列
	public static List<MessageBean> getSendMessageQueue(){
		String sendMessageQueueStr = MyHelper.readLine("sendMessageQueue");
		if(TextUtils.isEmpty(sendMessageQueueStr)){
			sendMessageQueueStr = "[]";
		}
		XLog.d("getSendMessageQueue:" + sendMessageQueueStr);
		return GsonUtils.GsonToList(sendMessageQueueStr, MessageBean.class);
	}
	
	//发送消息的队列
	public static JsonArray getSendMessageQueueJson(){
		String sendMessageQueueStr = MyHelper.readLine("sendMessageQueue");
		if(TextUtils.isEmpty(sendMessageQueueStr)){
			sendMessageQueueStr = "[]";
		}
		XLog.d("getSendMessageQueue:" + sendMessageQueueStr);
		return new JsonParser().parse(sendMessageQueueStr).getAsJsonArray();
	}
	
	public static void setSendMessageQueque(List<MessageBean> sendMessageQueue){
		try{
			MyHelper.writeLine("sendMessageQueue",GsonUtils.GsonString(sendMessageQueue));
			XLog.d("setSendMessageQueque:" + GsonUtils.GsonString(sendMessageQueue));
		}catch (Exception e){
			XLog.e(e.toString());
		}
	}
	
	public static boolean clearSendMessageQueque(){
		try{
			return MyHelper.delete("sendMessageQueue");
		}catch (Exception e){
			XLog.e(e.toString());
			return false;
		}
	}
}
