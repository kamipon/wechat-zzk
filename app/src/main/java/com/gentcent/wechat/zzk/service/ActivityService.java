package com.gentcent.wechat.zzk.service;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.AppUtils.AppInfo;
import com.gentcent.wechat.zzk.util.HookParams;

/**
 * @author zuozhi
 * @since 2019-08-30
 */
public class ActivityService {
	
	public static String getWxVersion() {
		String wxVersion;
		if (!AppUtils.isAppInstalled(HookParams.WECHAT_PACKAGE_NAME)) {
			wxVersion = "异常";
		} else {
			AppInfo appInfo = AppUtils.getAppInfo(HookParams.WECHAT_PACKAGE_NAME);
			wxVersion = appInfo.getVersionName();
		}
		return wxVersion;
	}
	
	public static String getZzkVersion() {
		String zzkVersion;
		if (!AppUtils.isAppInstalled(HookParams.MY_PACKAGE_NAME)) {
			zzkVersion = "异常";
		} else {
			AppInfo appInfo = AppUtils.getAppInfo(HookParams.MY_PACKAGE_NAME);
			zzkVersion = appInfo.getVersionName();
		}
		return zzkVersion;
	}
	
	
}
