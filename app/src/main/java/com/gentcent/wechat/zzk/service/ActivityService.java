package com.gentcent.wechat.zzk.service;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.AppUtils.AppInfo;
import com.gentcent.wechat.zzk.MyApplication;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.XLog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import cn.jpush.android.api.JPushInterface;

/**
 * @author zuozhi
 * @since 2019-08-30
 */
public class ActivityService {
	
	public static boolean checkZzk(Context context) {
		//连接极光
		boolean connectionState = JPushInterface.getConnectionState(MyApplication.getAppContext());
//		XLog.d("JPushInfoReceiver jPushState " + connectionState);
		if (!connectionState) {
			JPushInterface.init(context);
			JPushInterface.onResume(context);
		}
		return connectionState;
	}
	
	/**
	 * 微信当前版本号
	 */
	public static String getWxVersion() {
		String wxVersion;
		if (!AppUtils.isAppInstalled(HookParams.WECHAT_PACKAGE_NAME)) {
			wxVersion = "未知";
		} else {
			AppInfo appInfo = AppUtils.getAppInfo(HookParams.WECHAT_PACKAGE_NAME);
			wxVersion = appInfo.getVersionName();
		}
		return wxVersion;
	}
	
	/**
	 * 增长客当前版本号
	 */
	public static String getZzkVersion() {
		String zzkVersion;
		if (!AppUtils.isAppInstalled(HookParams.MY_PACKAGE_NAME)) {
			zzkVersion = "未知";
		} else {
			AppInfo appInfo = AppUtils.getAppInfo(HookParams.MY_PACKAGE_NAME);
			zzkVersion = appInfo.getVersionName();
		}
		return zzkVersion;
	}
	
	public static boolean MIUIUtils() {
		String str = Build.MANUFACTURER;
		PrintStream printStream = System.out;
		String sb = "Build.MANUFACTURER = " + str;
		printStream.println(sb);
		boolean z = false;
		if (!str.equals("Xiaomi")) {
			return false;
		}
		System.out.println("this is a xiaomi device");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
			if (!(properties.getProperty("ro.miui.ui.version.code", null) == null && properties.getProperty("ro.miui.ui.version.name", null) == null && properties.getProperty("ro.miui.internal.storage", null) == null)) {
				z = true;
			}
			return z;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	static String bright = "bright";
	
	public static void a(Context context, boolean z) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (z) {
			PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(1, bright);
			newWakeLock.acquire(OkHttpUtils.DEFAULT_MILLISECONDS);
			newWakeLock.release();
		}
		if (!powerManager.isScreenOn() && !z) {
			PowerManager.WakeLock newWakeLock2 = powerManager.newWakeLock(268435482, bright);
			newWakeLock2.acquire(OkHttpUtils.DEFAULT_MILLISECONDS);
			newWakeLock2.release();
		}
		KeyguardManager.KeyguardLock newKeyguardLock = ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock("unLock");
		newKeyguardLock.reenableKeyguard();
		newKeyguardLock.disableKeyguard();
	}
	
	
}
