package com.gentcent.wechat.zzk.model.friend;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.concurrent.TimeUnit;


public class FriendsHook{
	//添加好友的页面
	public static Activity activity;
	public static int c;
	
	public static void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
		XposedHelpers.findAndHookMethod(HookParams.sayHiWithSnsPermissionUI, lpparam.classLoader, "onResume", new XC_MethodHook() {
			protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				final Activity activity = (Activity) methodHookParam.thisObject;
				final Intent intent = activity.getIntent();
				XLog.d("SayHiWithSnsPermissionUI  ");
				if (FriendsHook.c == 2) {
					((EditText) XposedHelpers.getObjectField(activity, "qmH")).setText(MyHelper.readLine("addFriendHelloText", ""));
				}
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						XLog.d("SayHiWithSnsPermissionUI Runnable run()");
						if (FriendsHook.c == 2) {
							try {
								XLog.d(" mtA.setText    ");
								String stringExtra = intent.getStringExtra("Contact_User");
								intent.getIntExtra("Contact_Scene", 9);
								XLog.d("addfriend " + stringExtra);
								final Class loadClass = lpparam.classLoader.loadClass(HookParams.sayHiWithSnsPermissionUI_6);
								activity.runOnUiThread(new Runnable() {
									public void run() {
										MenuItem.OnMenuItemClickListener onMenuItemClickListener = (MenuItem.OnMenuItemClickListener) XposedHelpers.newInstance(loadClass, new Object[]{activity});
										XLog.d("SayHiWithSnsPermissionUI   onResume  flag  over");
										onMenuItemClickListener.onMenuItemClick(null);
									}
								});
							} catch (Exception e) {
								XLog.d("SayHiWithSnsPermissionUI  error" + Log.getStackTraceString(e));
							}
						}
					}
				}, 3000, TimeUnit.MILLISECONDS);
			}
		});
		XposedHelpers.findAndHookMethod(HookParams.ContactInfoUI, lpparam.classLoader, "initView", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				Activity activity = (Activity) methodHookParam.thisObject;
				XLog.d(" ContactInfoUI initView() ");
				boolean booleanExtra = activity.getIntent().getBooleanExtra("zzk", false);
				if (booleanExtra) {
					Intent intent = activity.getIntent();
					String stringExtra = intent.getStringExtra("Contact_Search_Mobile");
					StringBuilder sb2 = new StringBuilder();
					sb2.append("phone is ");
					sb2.append(stringExtra);
					XLog.d("AddFriendHook" + sb2.toString());
					if (ObjectUtils.isNotEmpty((CharSequence) stringExtra)) {
						String stringExtra2 = intent.getStringExtra("Contact_Nick");
						XLog.d("AddFriendHook" + "nickName is " + stringExtra2);
						String stringExtra3 = intent.getStringExtra("Contact_QuanPin");
						XLog.d("AddFriendHook" + "wxidQp is " + stringExtra3);
					}
				}
				if (FriendsHook.c == 2 && booleanExtra) {
					FriendsHook.c = 2;
					activity.finish();
				}
			}
		});
		XposedHelpers.findAndHookMethod(HookParams.ContactInfoUI, lpparam.classLoader, "onResume", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				final Activity activity = (Activity) methodHookParam.thisObject;
				final Intent intent = activity.getIntent();
				XLog.d("ContactInfoUI   onResume  ");
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						XLog.d("ContactInfoUI   onResume  run()" + FriendsHook.c);
						if (intent.getBooleanExtra("zzk", false)) {
							try {
								FriendsHook.activity = activity;
								if (FriendsHook.c == 2) {
									FriendsHook.c = 0;
									try {
										Thread.sleep(5000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									XLog.d("ContactInfoUI   AddFriendactivity  over ");
									activity.finish();
								} else if (FriendsHook.c == 4) {
									try {
										activity.runOnUiThread(new Runnable() {
											public void run() {
												FriendsHook.c = 2;
												Object objectField = XposedHelpers.getObjectField(activity, "qit");
												XLog.d("oHt class is " + objectField.getClass().getName());
												XposedHelpers.callMethod(objectField, "Lj", HookParams.ContactInfoUI_method_param);
												XLog.d("callMethod a success");
											}
										});
									} catch (Exception e2) {
										XLog.d("ContactInfoUI  1error is " + e2.getMessage());
									}
								}
							} catch (Exception e3) {
								XLog.d("ContactInfoUI error is " + e3.getMessage());
							}
						}
					}
				}, 3000, TimeUnit.MILLISECONDS);
			}
		});
		
		
		try {
			XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass(HookParams.FTSMainUI), "onCreate", Bundle.class, new XC_MethodHook() {
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
			XposedHelpers.findAndHookMethod("com.tencent.mm.ui.widget.b.c", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (FriendManager.isMyTask) {
						String charSequence = ((TextView) XposedHelpers.getObjectField(methodHookParam.thisObject, "zKf")).getText().toString();
						XLog.d("textView msg is " + charSequence);
						if (ObjectUtils.isNotEmpty((CharSequence) charSequence) && charSequence.startsWith("操作过于频繁")) {
							final Button button = (Button) XposedHelpers.getObjectField(methodHookParam.thisObject, "vcM");
							button.postDelayed(new Runnable() {
								public void run() {
									button.performClick();
									XLog.d("textView msg is performClick");
								}
							}, 200);
						}
					}
				}
			});
			Class loadClass = lpparam.classLoader.loadClass("com.tencent.mm.protocal.protobuf.bxf");
			XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass(HookParams.FTSMainUI4), "a", Intent.class, loadClass, Integer.TYPE, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (FriendManager.isMyTask) {
						((Intent) methodHookParam.args[0]).putExtra("zzk", true);
						FriendsHook.c = 4;
					}
				}
			});
			XposedHelpers.findAndHookMethod(HookParams.sayHiWithSnsPermissionUI, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (FriendManager.isMyTask) {
						final EditText editText = (EditText) XposedHelpers.getObjectField(methodHookParam.thisObject, "qmH");
						StringBuilder sb2 = new StringBuilder();
						if (editText != null && !TextUtils.isEmpty(MyHelper.readLine("addFriendHelloText", ""))) {
							editText.postDelayed(new Runnable() {
								public void run() {
									editText.setText(MyHelper.readLine("addFriendHelloText", ""));
									MyHelper.delete("addFriendHelloText");
								}
							}, 200);
						}
						final Activity activity = (Activity) methodHookParam.thisObject;
						if (editText != null && !TextUtils.isEmpty(MyHelper.readLine("addFriendHelloText", ""))) {
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
			XLog.e("error:  " + Log.getStackTraceString(e));
		}
	}
}
