package com.gentcent.wechat.enhancement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gentcent.wechat.enhancement.bean.MessageBean;
import com.gentcent.wechat.enhancement.util.HookParams;
import com.gentcent.wechat.enhancement.util.StaticDepot;
import com.gentcent.wechat.enhancement.util.ThreadPoolUtils;
import com.gentcent.wechat.enhancement.util.XLog;

import java.util.List;

import de.robv.android.xposed.XposedHelpers;

public class WxReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, final Intent intent) {
		try {
			String act = intent.getStringExtra("act");
			if (act != null) {
				// 发送消息
				if (act.equals("send_message")) {
					final List<MessageBean> msgQueue = (List<MessageBean>) intent.getSerializableExtra("msgQueue");
					ThreadPoolUtils tp = ThreadPoolUtils.getInstance();
					tp.run(new Runnable() {
						@Override
						public void run() {
							for (final MessageBean messageBean: msgQueue) {
								new Runnable() {
									@Override
									public void run() {
										//构造参数
										String friendWxId = messageBean.getFriendWxId();
										String content = messageBean.getContent();
										int type = messageBean.getType();
										Class aClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageParamClassName, StaticDepot.wxLpparam.classLoader);
										Object param = XposedHelpers.newInstance(aClass, friendWxId, content, type);
										//调用方法
										final Class bClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageClassName, StaticDepot.wxLpparam.classLoader);
										final Object staticObject = XposedHelpers.getStaticObjectField(bClass, HookParams.getInstance().sendMessageStaticObject);
										Object callMethod = XposedHelpers.callMethod(staticObject, HookParams.getInstance().sendMessageMethodName, param);
										XLog.e("发送成功:" + (Boolean) callMethod);
									}
								}.run();
								try {
									Thread.sleep(HookParams.SEND_TIME_INTERVAL);
								} catch (InterruptedException e) {
									XLog.e("发送消息线程休眠出错："+e.getMessage());
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
