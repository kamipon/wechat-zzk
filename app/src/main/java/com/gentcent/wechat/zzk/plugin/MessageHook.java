package com.gentcent.wechat.zzk.plugin;


import android.annotation.SuppressLint;
import android.content.ContentValues;

import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.XLog;

import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;


public class MessageHook implements IPlugin {
	
	@Override
	public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
		XposedHelpers.findAndHookMethod(HookParams.getInstance().SQLiteDatabaseClassName, lpparam.classLoader, HookParams.getInstance().SQLiteDatabaseInsertWithOnConflictMethod, String.class, String.class, ContentValues.class, int.class, new XC_MethodHook() {
			@SuppressLint("CommitPrefEdits")
			@Override
			protected void beforeHookedMethod(MethodHookParam param) {
				try {
					String str = (String) param.args[0];
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
		/*
			1：纯文本消息
			3：图片
			34：语音
			42：名片
			47：表情
			48：指定定位
			49：文件、卡片信息
			50：视屏通话 content：voip_content_video
			50：语音通话 content：voip_content_voice
			436207665：红包
			419430449：转账
			-1879048186：共享实时位置
			10000：提示字体
		 */
		int type = contentValues.getAsInteger("type");
		//0：接受 , 1：发送
		int isSend = contentValues.getAsInteger("isSend");
		//信息ID , 递增
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
