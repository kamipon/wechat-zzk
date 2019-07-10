package com.gentcent.wechat.enhancement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gentcent.wechat.enhancement.util.HookParams;
import com.gentcent.wechat.enhancement.util.MessageStorage;
import com.gentcent.wechat.enhancement.util.StaticDepot;
import com.gentcent.wechat.enhancement.util.XLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.threekilogram.objectbus.bus.ObjectBus;

import de.robv.android.xposed.XposedHelpers;

public class WxReceiver extends BroadcastReceiver {
	
	private static final ObjectBus task = com.threekilogram.objectbus.bus.ObjectBus.newList();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String stringExtra = intent.getStringExtra("act");
			intent.putExtra("act", "none");
			if (stringExtra != null) {
				if (stringExtra.equals("send_message")) {
					
					task.toPool(new Runnable() {
						@Override
						public void run() {
						
						}
					}).run();
					
					// 发消息
					JsonArray jsonArray = MessageStorage.getSendMessageQueueJson();
					if (jsonArray.size() > 0) {
						JsonObject object = jsonArray.get(0).getAsJsonObject();
						//消息参数
						String friendWxId = object.get("FriendWxId").getAsString();
						String content = object.get("Content").getAsString();
						int type = object.get("Type").getAsInt();
						Class bClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageParamClassName, StaticDepot.wxLpparam.classLoader);
						Object param = XposedHelpers.newInstance(bClass, friendWxId, content, type);
						//发送消息
						final Class aClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageClassName, StaticDepot.wxLpparam.classLoader);
						final Object staticObject = XposedHelpers.getStaticObjectField(aClass, HookParams.getInstance().sendMessageStaticObject);
						Object callMethod = XposedHelpers.callMethod(staticObject, HookParams.getInstance().sendMessageMethodName, param);
						XLog.e("callmethod:" + (Boolean) callMethod);
						
						//从队列中删除
						if ((Boolean) callMethod) {
							boolean b = MessageStorage.clearSendMessageQueque();
							XLog.e("isdel:" + b);
						}
					}
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + e.getMessage());
		}
	}
	
}
