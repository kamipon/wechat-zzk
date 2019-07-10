package com.gentcent.wechat.enhancement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.ThreadUtils;
import com.gentcent.wechat.enhancement.util.HookParams;
import com.gentcent.wechat.enhancement.util.MessageStorage;
import com.gentcent.wechat.enhancement.util.StaticDepot;
import com.gentcent.wechat.enhancement.util.ThreadPoolUtils;
import com.gentcent.wechat.enhancement.util.XLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.threekilogram.objectbus.bus.ObjectBus;

import de.robv.android.xposed.XposedHelpers;

public class WxReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, final Intent intent) {
		try {
			String act = intent.getStringExtra("act");
			if (act != null) {
				if (act.equals("send_message")) {
					
					// 发送消息
					ThreadPoolUtils tp = ThreadPoolUtils.getInstance();
					tp.run(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < 3; i++) {
								new Runnable() {
									@Override
									public void run() {
										//构造参数
										String friendWxId = intent.getStringExtra("friendWxId");
										String content = intent.getStringExtra("content");
										int type = intent.getIntExtra("type", 1);
										Class aClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageParamClassName, StaticDepot.wxLpparam.classLoader);
										Object param = XposedHelpers.newInstance(aClass, friendWxId, content, type);
										//调用方法
										final Class bClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageClassName, StaticDepot.wxLpparam.classLoader);
										final Object staticObject = XposedHelpers.getStaticObjectField(bClass, HookParams.getInstance().sendMessageStaticObject);
										Object callMethod = XposedHelpers.callMethod(staticObject, HookParams.getInstance().sendMessageMethodName, param);
										XLog.e("发送成功:" + (Boolean) callMethod);
										
										//从队列中删除
										if ((Boolean) callMethod) {
										
										}
									}
								}.run();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + e.getMessage());
		}
	}
}
