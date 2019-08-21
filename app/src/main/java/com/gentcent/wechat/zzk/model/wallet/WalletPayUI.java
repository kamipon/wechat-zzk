package com.gentcent.wechat.zzk.model.wallet;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gentcent.wechat.zzk.model.wallet.bean.PayInfo;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

public class WalletPayUI {
	public static boolean a = false;
	public static PayInfo b = null;
	public static boolean c = false;
	public static Activity d;
	
	public static void a() {
		Activity activity = d;
		if (activity != null) {
			activity.finish();
		}
	}
	
	public static void a(LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.wallet.pay.ui.WalletPayUI", loadPackageParam.classLoader, "oe", Boolean.TYPE, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					XLog.d("MyWalletPayUI  WalletPayUI bPu 1");
					Activity activity = (Activity) methodHookParam.thisObject;
					XLog.d("MyWalletPayUI  WalletPayUI bPu 2");
					if (WalletPayUI.a && WalletPayUI.b != null && WalletPayUI.b.passward != null) {
						XLog.d("MyWalletPayUI  WalletPayUI bPu 3");
						if (!activity.getIntent().getBooleanExtra("shenshou", false)) {
							XLog.d("MyWalletPayUI  WalletPayUI bPu 4");
							Object objectField = XposedHelpers.getObjectField(activity, "uHr");
							Object objectField2 = XposedHelpers.getObjectField(activity, "uGk");
							StringBuilder sb = new StringBuilder();
							sb.append("MyWalletPayUI  WalletPayUI bPu pBu");
							sb.append(objectField);
							sb.append(",pHD =");
							sb.append(objectField2);
							XLog.d(sb.toString());
							if (!(objectField == null || objectField2 == null)) {
								View view = (View) XposedHelpers.getObjectField(objectField, "vfy");
								if (view != null) {
									XposedHelpers.callMethod(objectField, "c", Boolean.FALSE, 0, Boolean.FALSE);
									WalletPayUI.d = activity;
								}
								activity.getIntent().putExtra("shenshou", true);
							}
							XLog.d("MyWalletPayUI  WalletPayUI bPu  6");
						}
					}
				}
			});
//			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.wallet.pay.ui.WalletPayUI", loadPackageParam.classLoader, "c", Integer.TYPE, Integer.TYPE, String.class, loadPackageParam.classLoader.loadClass("com.tencent.mm.ah.m"), new XC_MethodHook() {
//				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//					super.afterHookedMethod(methodHookParam);
//					int intValue = (Integer) methodHookParam.args[0];
//					int intValue2 = (Integer) methodHookParam.args[1];
//					XLog.d("MyWalletPayUI  WalletPayUI 1");
//					if (WalletPayUI.a && WalletPayUI.b != null && WalletPayUI.b.passward != null && intValue == 0 && intValue2 == 0) {
//						Activity activity = (Activity) methodHookParam.thisObject;
//						XLog.d("MyWalletPayUI  WalletPayUI 2");
//						Button button = (Button) XposedHelpers.getObjectField(methodHookParam.thisObject, "poR");
//						if (button.isEnabled()) {
//							button.callOnClick();
//							WalletPayUI.d = activity;
//						}
//					}
//				}
//			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.wallet.pay.ui.WalletPayUI", loadPackageParam.classLoader, "onDestroy", new XC_MethodHook() {
				public void beforeHookedMethod(MethodHookParam methodHookParam) {
					if (WalletPayUI.d != null) {
						WalletPayUI.d = null;
					}
				}
			});
		} catch (Throwable th) {
			XLog.d("MyWalletPayUI hookTargetClass e:" + Log.getStackTraceString(th));
		}
	}
}
