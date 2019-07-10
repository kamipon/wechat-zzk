package com.gentcent.wechat.enhancement.plugin;


import android.annotation.SuppressLint;
import android.content.ContentValues;

import com.gentcent.wechat.enhancement.util.HookParams;
import com.gentcent.wechat.enhancement.util.XLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MessageHook implements IPlugin {
	
	@Override
	public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
		XposedHelpers.findAndHookMethod(HookParams.getInstance().SQLiteDatabaseClassName, lpparam.classLoader, HookParams.getInstance().SQLiteDatabaseUpdateMethod, String.class, String.class, ContentValues.class, int.class, new XC_MethodHook() {
			@SuppressLint("CommitPrefEdits")
			@Override
			protected void beforeHookedMethod(MethodHookParam param) {
				try {
					String str = (String) param.args[0];
					XLog.e(str);
//				if (str.equals("fmessage_conversation")) {
//				}
					if (param.args[2] != null) {
						ContentValues contentValues = (ContentValues) param.args[2];
//					if (str.equals("AppMessage")) {
//						a.a(this.a, contentValues);
//						return;
//					}
//					if (str.equals("WxFileIndex2")) {
//						m.a().a(new Runnable(this, contentValues) {
//							public void run() {
//								g.a(this.b.a, this.a);
//							}
//						} 1L, TimeUnit.SECONDS);
//						return;
//					}
						if ("message".equals(str)) {
							log(contentValues);
							return;
						}
//					if ("chatroom".equals(str)) {
//						b.a(this.a, contentValues);
//					}
					}
				} catch (Error | Exception e) {
					XLog.e("错误：" + e.toString());
				}
			}
			
		});
	}
	
	private void log(ContentValues contentValues) {
		//1：纯文本消息
		int type = contentValues.getAsInteger("type");
		//0：接受 , 1：发送
		int isSend = contentValues.getAsInteger("isSend");
		//信息ID ,递增
		long msgId = contentValues.getAsLong("msgId");
		//发送者ID
		String talker = contentValues.getAsString("talker");
		//消息内容
		String content = contentValues.getAsString("content");
		int createTime = (int) (contentValues.getAsLong("createTime") / 1000L);
//		MessageBean messageBean = a(content);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("message || type=");
		stringBuilder.append(type);
		stringBuilder.append("; msgId=");
		stringBuilder.append(msgId);
		stringBuilder.append("; isSend=");
		stringBuilder.append(isSend);
		stringBuilder.append("; talker=");
		stringBuilder.append(talker);
//		stringBuilder.append("; friendId is ");
//		stringBuilder.append(messageBean);
		stringBuilder.append("; content=");
		stringBuilder.append(content);
		XLog.d(stringBuilder.toString());
	}
}
