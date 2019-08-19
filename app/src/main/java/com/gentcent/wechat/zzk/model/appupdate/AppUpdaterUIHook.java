package com.gentcent.wechat.zzk.model.appupdate;

import android.os.Bundle;
import android.util.Log;

import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

public class AppUpdaterUIHook {
	public static String a = "com.tencent.mm.sandbox.updater.AppUpdaterUI";
	
	public static void hook(LoadPackageParam loadPackageParam) {
		try {
			final Class loadClass = loadPackageParam.classLoader.loadClass(a);
			XposedHelpers.findAndHookMethod(loadClass, "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) {
					try {
						XLog.d("MyAppInstallerHook" + "AppUpdaterUI onCreate enter ya ");
						XposedHelpers.callStaticMethod(loadClass, "g", methodHookParam.thisObject);
						XLog.d("MyAppInstallerHook" + "AppUpdaterUI closeUpdate success aha ");
					} catch (Exception e) {
						StringBuilder sb = new StringBuilder();
						sb.append("AppUpdaterUI finish error is ");
						sb.append(e.getMessage());
						XLog.d("MyAppInstallerHook" + sb.toString());
					}
				}
			});
		} catch (Throwable th) {
			XLog.e("MyAppInstallerHook:" + Log.getStackTraceString(th));
		}
	}
}
