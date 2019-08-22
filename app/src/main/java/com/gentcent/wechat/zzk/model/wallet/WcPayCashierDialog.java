package com.gentcent.wechat.zzk.model.wallet;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class WcPayCashierDialog {
	private static final String ui = "com.tencent.mm.plugin.wallet_core.ui.cashier.WcPayCashierDialog";
	
	public static void hook(final LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod(ui, loadPackageParam.classLoader, "c", Boolean.TYPE, Integer.TYPE, Boolean.TYPE, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					Object next = null;
					if (WalletPayUI.a && WalletPayUI.b != null) {
						Object objectField = XposedHelpers.getObjectField(methodHookParam.thisObject, "vfC");
						if (objectField != null) {
							Object objectField2 = XposedHelpers.getObjectField(objectField, "veU");
							if (objectField2 != null) {
								Object objectField3 = XposedHelpers.getObjectField(methodHookParam.thisObject, "mPayInfo");
								if (objectField3 != null) {
									ArrayList arrayList = (ArrayList) XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.wallet_core.model.ae"), "ok", new Object[]{XposedHelpers.getIntField(objectField3, "cWh") == 8});
									if (arrayList != null) {
										Iterator it = arrayList.iterator();
										while (true) {
											if (!it.hasNext()) {
												break;
											}
											next = it.next();
											String str = (String) XposedHelpers.getObjectField(next, "field_bindSerial");
											XLog.d("WcPayCashierDialog bankcardlist bindSerial:" + str + "----------WalletPayUI.payinfo.paymethod" + WalletPayUI.b.paymethod);
											if (WalletPayUI.b.paymethod != null && !TextUtils.isEmpty(WalletPayUI.b.paymethod)) {
												if (!TextUtils.isEmpty(str) && str.equals(WalletPayUI.b.paymethod)) {
													break;
												}
											} else {
												XLog.d("bankcardlist isEquals " + StringUtils.equals(str, "CFT"));
												if (StringUtils.equals(str, "CFT")) {
													break;
												}
											}
										}
										XposedHelpers.callMethod(objectField, "dismiss");
										if (next != null) {
											XposedHelpers.callMethod(objectField2, "i", next);
										}
										ThreadPoolUtils.getInstance().a(new Runnable() {
											public void run() {
												MyKeyboardWindow.a(WalletPayUI.b.passward);
												WalletPayUI.b = null;
											}
										}, 2, TimeUnit.SECONDS);
									}
								}
							}
						}
					}
				}
			});
		} catch (Throwable th) {
			XLog.d("WcPayCashierDialog hookTargetClass e:" + Log.getStackTraceString(th));
		}
	}
}
