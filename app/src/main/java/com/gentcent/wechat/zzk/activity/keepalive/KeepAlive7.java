package com.gentcent.wechat.zzk.activity.keepalive;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.WxBroadcast;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;

public class KeepAlive7 {
	private static boolean b(Context context, String str) {
		for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
			if (str.equals(runningAppProcessInfo.processName)) {
				return true;
			}
		}
		return false;
	}
	
	public static void a(Context context, String str) {
		try {
			if (b(context, str)) {
				long currentTimeMillis = System.currentTimeMillis();
				if (TextUtils.equals(str, "com.gentcent.wechat.zzk")) {
					MyHelper.writeLine("alive_time_dl", currentTimeMillis + "");
					String f = MyHelper.readLine("alive_time_wx");
					if (ObjectUtils.isNotEmpty(f)) {
						Long.parseLong(f);
					}
				} else if (TextUtils.equals(str, "com.tencent.mm")) {
					String sb2 = System.currentTimeMillis() + "";
					MyHelper.writeLine("alive_time_wx", sb2);
					String f2 = MyHelper.readLine("alive_time_dl");
					if (ObjectUtils.isNotEmpty(f2) && currentTimeMillis - Long.parseLong(f2) > 40000) {
						XLog.d("doAlive openMoreChat is " + context);
						WxBroadcast.openApp(context);
					}
				}
			}
		} catch (Exception e) {
			XLog.d("KeepAlive7" + "isAlive error is " + e.getMessage());
		}
	}
}
