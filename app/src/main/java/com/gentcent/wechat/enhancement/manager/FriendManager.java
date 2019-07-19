package com.gentcent.wechat.enhancement.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.enhancement.plugin.FriendsHook;
import com.gentcent.wechat.enhancement.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

/**
 * @author zuozhi
 * @since 2019-07-19
 */
public class FriendManager {
	//搜索好友的页面
	public static Activity activity;
	//是脚本任务
	public static boolean isMyTask = true;
	//添加好友打招呼语句
	public static String helloText = "增长客测试~";
	
	public static void searchFriendHook(XC_LoadPackage.LoadPackageParam lpparam){
		try {
			XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.mm.plugin.fts.ui.FTSMainUI"), "onCreate", Bundle.class, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (FriendManager.activity == null) {
						Activity activity = (Activity) methodHookParam.thisObject;
						FriendManager.activity = activity;
						XLog.d("FTSMainUI is onCreate finish");
						activity.finish();
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.ui.widget.a.c", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (FriendManager.isMyTask) {
						String charSequence = ((TextView) XposedHelpers.getObjectField(methodHookParam.thisObject, "xxr")).getText().toString();
						XLog.d("2textView msg is " + charSequence);
						if (ObjectUtils.isNotEmpty((CharSequence) charSequence) && charSequence.startsWith("操作过于频繁")) {
							final Button button = (Button) XposedHelpers.getObjectField(methodHookParam.thisObject, "sYz");
							button.postDelayed(new Runnable() {
								public void run() {
									button.performClick();
									XLog.d("2textView msg is performClick");
								}
							}, 200);
						}
					}
				}
			});
			Class loadClass = lpparam.classLoader.loadClass("com.tencent.mm.protocal.protobuf.bsk");
			XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.mm.model.j"), "a", Intent.class, loadClass, Integer.TYPE, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (FriendManager.isMyTask) {
						((Intent) methodHookParam.args[0]).putExtra("zzk", true);
						FriendsHook.c = 4;
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (FriendManager.isMyTask) {
						final EditText editText = (EditText) XposedHelpers.getObjectField(methodHookParam.thisObject, "oLC");
						StringBuilder sb2 = new StringBuilder();
						if (editText != null && !TextUtils.isEmpty(FriendManager.helloText)) {
							editText.postDelayed(new Runnable() {
								public void run() {
									editText.setText(FriendManager.helloText);
								}
							}, 200);
						}
						final Activity activity = (Activity) methodHookParam.thisObject;
						if (editText != null && !TextUtils.isEmpty(FriendManager.helloText)) {
							editText.postDelayed(new Runnable() {
								public void run() {
									Activity activity2 = activity;
									if (activity2 != null && !activity2.isDestroyed()) {
										activity2.finish();
									}
								}
							}, 12000);
						}
					}
				}
			});

//			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.fts.ui.FTSMainUI$5", lpparam.classLoader, "onSceneEnd", Integer.TYPE, Integer.TYPE, String.class, lpparam.classLoader.loadClass("com.tencent.mm.ah.m"), new XC_MethodHook() {
//				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//					super.afterHookedMethod(methodHookParam);
//					if (FriendManager.isMyTask) {
//						String str = (String) XposedHelpers.getObjectField(methodHookParam.thisObject, "Be");
//						int intValue = (Integer) methodHookParam.args[0];
//						int intValue2 = (Integer) methodHookParam.args[1];
//						Object obj = methodHookParam.args[3];
//						XLog.d("");
//						if (intValue == 4 && intValue2 == -4) {
//							FriendManager.a(-1, str, (WxInfo) null);
//						} else if (intValue == 0 && intValue2 == 0) {
//							WxInfo a2 = WxInfo.a(loadPackageParam, XposedHelpers.callMethod(obj, "bKe", new Object[0]));
//							XLog.d("FriendManager hookTargetClass wxInfo:");
//							FriendManager.a(0, str, a2);
//						} else if (intValue == 4 && intValue2 == -24) {
//							FriendManager.a(-2, str, (WxInfo) null);
//						} else {
//							FriendManager.a(-3, str, (WxInfo) null);
//						}
//					}
//				}
//			});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
