package com.gentcent.wechat.enhancement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gentcent.wechat.enhancement.util.MessageStorage;
import com.gentcent.wechat.enhancement.util.StaticDepot;
import com.gentcent.wechat.enhancement.util.XLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.robv.android.xposed.XposedHelpers;

public class AcceptReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			String stringExtra = intent.getStringExtra("act");
			intent.putExtra("act", "none");
			if (stringExtra != null) {
				if (stringExtra.equals("send_message")) {
					// 发消息
					final Class bClass = XposedHelpers.findClass("com.tencent.mm.ah.p", StaticDepot.wxLpparam.classLoader);
					final Object sendMessageObj = XposedHelpers.getStaticObjectField(bClass, "fej");
					JsonArray jsonArray = MessageStorage.getSendMessageQueueJson();
					if(jsonArray.size()>0){
						JsonObject object = jsonArray.get(0).getAsJsonObject();
						boolean b = MessageStorage.clearSendMessageQueque();
						XLog.e("isdel:"+b);
						//消息参数
						String friendWxId = object.get("FriendWxId").getAsString();
						String content = object.get("Content").getAsString();
						int type = object.get("Type").getAsInt();
						Class h = XposedHelpers.findClass("com.tencent.mm.modelmulti.h", StaticDepot.wxLpparam.classLoader);
						Object obj = XposedHelpers.newInstance(h, friendWxId, content, type);
						
						//发送消息
						Object callMethod = XposedHelpers.callMethod(sendMessageObj, "d", obj);
						XLog.e("callmethod:" + callMethod);
					}
				}
			}
		}catch (Exception e){
			XLog.e("错误:"+e.getMessage());
		}
	}
}
