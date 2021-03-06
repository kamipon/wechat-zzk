package com.gentcent.wechat.zzk.activity.keepalive;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.gentcent.wechat.zzk.service.ActivityService;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KeepAlive {
	static Set<String> a = new HashSet();
	
	public static void a(Context context) {
//		if (AppUtils.isAppInstalled("com.ssrj.handmarket")) {
//			a(context, "com.ssrj.handmarket", 40000);
//		}
	}
	
	public static void a(final Context context, final String packageName, int i) {
		if (ActivityService.MIUIUtils()) {
//			XLog.d(" MIUIUtils do not keepAlive");
		} else if (TextUtils.equals(DeviceUtils.getModel(), "GRA-A0")) {
			XLog.d(" GRA do not keepAlive");
		} else if (!ObjectUtils.isNotEmpty(a) || !a.contains(packageName)) {
			if (a == null) {
				a = new HashSet();
			}
			a.add(packageName);
			ThreadPoolUtils.getInstance().a(new Runnable() {
				public void run() {
					if (ScreenUtils.isScreenLock() && context != null && TextUtils.equals(packageName, "com.tencent.mm")) {
						if (MyHelper.readLine("bright_screen").equals("true")) {
							ActivityService.a(context, false);
						} else {
							ActivityService.a(context, true);
						}
					}
					if (VERSION.SDK_INT >= 24) {
						if (context != null) {
							if (TextUtils.equals(packageName, "com.gentcent.wechat.zzk")) {
								KeepAlive7.a(context, "com.tencent.mm");
							} else if (TextUtils.equals(packageName, "com.tencent.mm")) {
								KeepAlive7.a(context, "com.gentcent.wechat.zzk");
							}
						}
						return;
					}
					if (!KeepAlive.a(packageName)) {
						XLog.d("app is not alive " + packageName);
						KeepAlive.b(context, packageName);
					}
				}
			}, 30000, (long) i, TimeUnit.MILLISECONDS);
		} else {
			XLog.d(" setPackageName not keepAlive " + packageName);
		}
	}
	
	public static void b(Context context, String str) {
		context.startActivity(context.getPackageManager().getLaunchIntentForPackage(str));
	}
	
	public static boolean a(String str) {
		for (AndroidAppProcess androidAppProcess : AndroidProcesses.getRunningAppProcesses()) {
			if (TextUtils.equals(androidAppProcess.name, str)) {
				return true;
			}
		}
		return false;
	}
}
