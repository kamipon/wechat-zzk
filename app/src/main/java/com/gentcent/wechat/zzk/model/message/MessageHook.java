package com.gentcent.wechat.zzk.model.message;


import android.annotation.SuppressLint;
import android.content.ContentValues;

import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.concurrent.TimeUnit;


public class MessageHook {
	
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
						final ContentValues contentValues = (ContentValues) param.args[2];
//					if (str.equals("AppMessage")) {
//						a.a(this.a, contentValues);
//						return;
//					}
						if (str.equals("WxFileIndex2")) {
							ThreadPoolUtils.getInstance().a(new Runnable() {
								public void run() {
									MessageHandler.WxFileIndex2(contentValues);
								}
							}, 1, TimeUnit.SECONDS);
							return;
						}
						if ("message".equals(str)) {
							MessageHandler.message(contentValues);
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
		
		
		VideoHook.a(lpparam);
	}
	
	
}
