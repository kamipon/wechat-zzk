package com.gentcent.wechat.zzk.service;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.AppUtils.AppInfo;
import com.gentcent.wechat.zzk.MyApplication;
import com.gentcent.wechat.zzk.activity.MainActivity;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

/**
 * @author zuozhi
 * @since 2019-08-30
 */
public class ActivityService {
	
	public static boolean checkWechat() {
		return true;
	}
	
	public static boolean checkZzk() {
		//连接极光
		boolean connectionState = JPushInterface.getConnectionState(MyApplication.getAppContext());
		XLog.d("JPushInfoReceiver jPushState " + connectionState);
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
	
	
}
