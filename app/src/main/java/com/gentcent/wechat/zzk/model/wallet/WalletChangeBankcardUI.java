package com.gentcent.wechat.zzk.model.wallet;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.blankj.utilcode.util.StringUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.ArrayList;

public class WalletChangeBankcardUI {
	public static void hook(LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.wallet.pay.ui.WalletChangeBankcardUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(final MethodHookParam methodHookParam) throws Throwable {
					if (WalletPayUI.a && WalletPayUI.b != null) {
						new Handler().postDelayed(new Runnable() {
							public void run() {
								ArrayList arrayList = (ArrayList) XposedHelpers.getObjectField(methodHookParam.thisObject, "uya");
								if (arrayList == null || arrayList.size() <= 0) {
									XLog.d("MYWalletChangeBankcardUI" + " bankcardlist is null");
									return;
								}
								XLog.d("bankcardlist size " + arrayList.size());
								int i = 0;
								while (i < arrayList.size()) {
									try {
										String str = (String) XposedHelpers.getObjectField(arrayList.get(i), "field_bindSerial");
										XLog.d("MYWalletChangeBankcardUI bankcardlist bindSerial:" + str + "----------WalletPayUI.payinfo.paymethod" + WalletPayUI.b.paymethod);
										if (WalletPayUI.b.paymethod == null || TextUtils.isEmpty(WalletPayUI.b.paymethod)) {
											XLog.d("bankcardlist isEquals " + StringUtils.equals(str, "CFT"));
											if (StringUtils.equals(str, "CFT")) {
												XLog.d("MYWalletChangeBankcardUI" + "equalsIgnoreCase CFT: true");
												XposedHelpers.callMethod(methodHookParam.thisObject, "Jy", i);
												return;
											}
										} else if (!TextUtils.isEmpty(str) && str.equals(WalletPayUI.b.paymethod)) {
											XposedHelpers.callMethod(methodHookParam.thisObject, "Jy", i);
											return;
										}
										i++;
									} catch (Throwable th) {
										StringBuilder sb4 = new StringBuilder();
										sb4.append(" bankcardlist error ");
										sb4.append(th.getMessage());
										XLog.d("MYWalletChangeBankcardUI" + sb4.toString());
										return;
									}
								}
							}
						}, 1000);
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.wallet.pay.ui.WalletChangeBankcardUI", loadPackageParam.classLoader, "cZm", new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					XLog.d("MYWalletChangeBankcardUI  WalletChangeBankcardUI bPu 1");
					Activity activity = (Activity) methodHookParam.thisObject;
					StringBuilder sb = new StringBuilder();
					sb.append("MYWalletChangeBankcardUI  WalletChangeBankcardUI bPu 2");
					sb.append(WalletPayUI.a);
					sb.append(",");
					sb.append(WalletPayUI.b);
					XLog.d(sb.toString());
					if (WalletPayUI.a && WalletPayUI.b != null && WalletPayUI.b.passward != null) {
						XLog.d("MYWalletChangeBankcardUI  WalletChangeBankcardUI bPu 3");
						if (!activity.getIntent().getBooleanExtra("shenshou", false)) {
							XLog.d("MYWalletChangeBankcardUI  WalletChangeBankcardUI bPu 4");
							Object objectField = XposedHelpers.getObjectField(activity, "uGg");
							StringBuilder sb2 = new StringBuilder();
							sb2.append("MYWalletChangeBankcardUI  WalletChangeBankcardUI bPu pBu");
							sb2.append(objectField);
							XLog.d(sb2.toString());
							if (objectField != null) {
								XLog.d("MYWalletChangeBankcardUI  WalletChangeBankcardUI bPu 5");
								MyKeyboardWindow.a(WalletPayUI.b.passward);
								activity.getIntent().putExtra("shenshou", true);
								WalletPayUI.b = null;
							}
							XLog.d("MYWalletChangeBankcardUI  WalletPayUI bPu  6");
						}
					}
				}
			});
		} catch (Throwable th) {
			StringBuilder sb = new StringBuilder();
			sb.append("hookTargetClass e ");
			sb.append(th.getMessage());
			XLog.d("MYWalletChangeBankcardUI" + sb.toString());
			th.printStackTrace();
		}
	}
}
