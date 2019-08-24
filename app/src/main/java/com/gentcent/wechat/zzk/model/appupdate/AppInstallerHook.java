package com.gentcent.wechat.zzk.model.appupdate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

public class AppInstallerHook {
	public static void hook(final LoadPackageParam lpparam) {
		try {
			AppUpdaterUIHook.hook(lpparam);
			XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.mm.sandbox.updater.AppInstallerUI"), "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) {
					try {
						XLog.d("MyAppInstallerHook" + "AppInstallerUI onCreate enter");
						String str = (String) XposedHelpers.callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.sandbox.monitor.c"), "QC", new Object[]{XposedHelpers.getObjectField(methodHookParam.thisObject, "ckO")});
						if (str != null) {
							FileUtils.deleteFile(str);
						}
						((Activity) methodHookParam.thisObject).finish();
						XLog.d("MyAppInstallerHook" + "AppInstallerUI onCreate success");
					} catch (Exception e) {
						XLog.d("MyAppInstallerHook AppInstallerUI finish error is " + Log.getStackTraceString(e));
					}
				}
			});
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	public static void b(LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.kernel.g"), "Qe"), "PO"), "set", 7, 16912485);
			Object newInstance = XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.protocal.protobuf.abj"));
			XposedHelpers.setObjectField(newInstance, "vzd", 35);
			XposedHelpers.setObjectField(newInstance, "oRN", 1);
			XposedHelpers.callMethod(XposedHelpers.callMethod(ZzkUtil.getWxCore(loadPackageParam), "VH"), "c", XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.messenger.foundation.a.a.j$a"), 23, newInstance));
			XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.setting.b"), "fUS"), "AF");
		} catch (Throwable th) {
			XLog.d("MyAppInstallerHook setupdate_mode e:" + Log.getStackTraceString(th));
		}
	}
}
