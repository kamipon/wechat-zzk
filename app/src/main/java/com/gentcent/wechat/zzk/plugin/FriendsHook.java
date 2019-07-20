package com.gentcent.wechat.zzk.plugin;


import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.EditText;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.manager.FriendManager;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.concurrent.TimeUnit;


public class FriendsHook implements IPlugin {
	//添加好友的页面
	public static Activity activity;
	public static int c;
	
	@Override
	public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI", lpparam.classLoader, "onResume", new XC_MethodHook() {
			protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				final Activity activity = (Activity) methodHookParam.thisObject;
				final Intent intent = activity.getIntent();
				XLog.d("SayHiWithSnsPermissionUI  ");
				if (FriendsHook.c == 2) {
					((EditText) XposedHelpers.getObjectField(activity, "oLC")).setText(MyHelper.readLine("addFriendHelloText", ""));
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
								final Class loadClass = lpparam.classLoader.loadClass("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI$6");
								activity.runOnUiThread(new Runnable() {
									public void run() {
										MenuItem.OnMenuItemClickListener onMenuItemClickListener = (MenuItem.OnMenuItemClickListener) XposedHelpers.newInstance(loadClass, new Object[]{activity});
										XLog.d("SayHiWithSnsPermissionUI   onResume  flag  over");
										onMenuItemClickListener.onMenuItemClick(null);
									}
								});
							} catch (Exception e) {
								XLog.d("SayHiWithSnsPermissionUI  error" + e.getMessage());
							}
						}
					}
				}, 3000, TimeUnit.MILLISECONDS);
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI$6", lpparam.classLoader, "onMenuItemClick", MenuItem.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", lpparam.classLoader, "initView", new XC_MethodHook() {
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
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", lpparam.classLoader, "onResume", new XC_MethodHook() {
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
												Object objectField = XposedHelpers.getObjectField(activity, "oHt");
												XLog.d("oHt class is " + objectField.getClass().getName());
												XposedHelpers.callMethod(objectField, "EF", "contact_profile_add_contact");
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
		
		
		FriendManager.searchFriendHook(lpparam);
	}
	
}
