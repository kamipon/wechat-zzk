package com.gentcent.wechat.zzk.activity.keepalive;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/* renamed from: android.support.v7.widget.keepalive.c */
public class SystemUtils {
	public static boolean a(Context context, String str) {
		for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
			if (str.equals(runningAppProcessInfo.processName)) {
				return true;
			}
		}
		return false;
	}
}
