package com.gentcent.wechat.zzk.manager;

import android.util.Log;

import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;

/**
 * 发送消息
 *
 * @author zuozhi
 * @since 2019-07-08
 */
public class SendMessageManager {
	private static Class<?> class1;
	
	private static void init(ClassLoader classLoader) {
		if (class1 == null) {
			try {
				class1 = XposedHelpers.findClass(HookParams.sendMessageClass1, classLoader);
			} catch (Throwable th) {
				XLog.d("send text init error is " + Log.getStackTraceString(th));
			}
		}
	}
	
	/**
	 * 发送纯文本消息
	 */
	public static void sendText(final String serviceGuid, final String friendWxId, final String Content, final int type) {
		try {
			XLog.d("发送文本消息");
			ClassLoader clsLoader = MainManager.wxLpparam.classLoader;
			init(clsLoader);
			final Class findClass = XposedHelpers.findClass(HookParams.sendMessageClass2, clsLoader);
			XLog.d("netscenequeueclass:" + findClass);
			if (findClass != null) {
				ThreadPoolUtils.getInstance().run(new Runnable() {
					@Override
					public void run() {
						final Object staticField = XposedHelpers.getStaticObjectField(findClass, HookParams.sendMessageStaticField);
						if (staticField != null) {
							final String[] split = friendWxId.split("\\|");
							for (int i = 0; i < split.length; i++) {
								final int finalI = i;
								SendManager.a(serviceGuid, new Runnable() {
									public void run() {
										Object callMethod = XposedHelpers.callMethod(staticField, HookParams.sendMessageMethodName, XposedHelpers.newInstance(class1, split[finalI], Content, type));
										XLog.d("sendText 发送消息成功 : " + callMethod.toString() + "  wxid " + split[finalI] + "  sleep time is 2000");
									}
								}, 1, friendWxId, Content);
								if (i > 0) {
									try {
										Thread.sleep((long) 2000);
									} catch (InterruptedException e2) {
										XLog.d("send text sleep Exception is " + Log.getStackTraceString(e2));
									}
								}
							}
						}
					}
				});
			}
		} catch (Exception e) {
			XLog.d("send text Exception is " + Log.getStackTraceString(e));
		}
	}
}
