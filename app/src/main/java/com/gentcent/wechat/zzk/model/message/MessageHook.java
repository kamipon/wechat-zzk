package com.gentcent.wechat.zzk.model.message;


import android.annotation.SuppressLint;
import android.content.ContentValues;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.model.friend.AddVerifyingFriend;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class MessageHook {
	
	private static String[] appNameList = {"filehelper", "qqmail", "floatbottle", "shakeapp", "lbsapp", "medianote", "newsapp", "facebookapp", "qqfriend", "masssendapp", "feedsapp", "voipapp", "officialaccounts", "voicevoipapp", "voiceinputapp", "linkedinplugin", "notifymessage", "fmessage", "weixin", "qmessage", "tmessage"};
	
	public static void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
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
	
	private static void log(ContentValues contentValues) {
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
		final int type = contentValues.getAsInteger("type");
		//0：接受 , 1：发送
		final int isSend = contentValues.getAsInteger("isSend");
		//信息ID , 递增
		final long msgId = contentValues.getAsLong("msgId");
		//发送者ID
		final String talker = contentValues.getAsString("talker");
		//消息内容
		final String content = contentValues.getAsString("content");
		final int createTime = (int) (contentValues.getAsLong("createTime") / 1000L);
		
		XLog.d("message || type=" + type + "; msgId=" + msgId + "; isSend=" + isSend + "; talker=" + talker + "; content=" + content);
		
		if (isNeedSendToBack(talker)) {
			if (type == 1) { //文本消息
				XLog.d("messageHandle" + "MysnedText msgId =" + msgId + " content :" + content);
				if (!talker.endsWith("@chatroom")) {
					AddVerifyingFriend.run(talker);
				}
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						int status = SendMessageManager.getStatusByMsgId(msgId);
						XLog.d("receiveDelay text state is " + status);
						if (talker.endsWith("@chatroom")) {
//							aj.b(status, QNUploadUtil.a(isSend), talker, content, createTime);
							return;
						}
						UploadService.receiveTextMessage(status, isSend, talker, content, createTime);
					}
				}, 350, TimeUnit.MILLISECONDS);
				
			} else if (type == 3) {
			
			}
		}
	}
	
	public static boolean isNeedSendToBack(String wxId) {
		return (ObjectUtils.isNotEmpty(wxId) && wxId.endsWith("@chatroom")) || isNotAppWxId(wxId);
	}
	
	public static boolean isNotAppWxId(String wxId) {
		return ObjectUtils.isNotEmpty(wxId) && !wxId.startsWith("gh_") && !wxId.startsWith("fake_") && !wxId.endsWith("@chatroom") && !Arrays.asList(appNameList).contains(wxId);
	}
}
