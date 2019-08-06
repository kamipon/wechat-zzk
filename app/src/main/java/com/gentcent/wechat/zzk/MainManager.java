package com.gentcent.wechat.zzk;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;

import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.*;

/**
 * @author zuozhi
 * @since 2019-07-09
 */
public class MainManager {
	public static Activity activity;
	public static LoadPackageParam wxLpparam;
	public static WxReceiver wxReceiver;
	
	public static void init(final LoadPackageParam lpparam) {
		XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				if (lpparam.isFirstApplication) {
					
					Application application = (Application) methodHookParam.thisObject;
					
					if (MainManager.wxLpparam == null) {
						MainManager.wxLpparam = lpparam;
						MainManager.wxLpparam.classLoader = application.getClassLoader();
						XLog.d("wxLpparam is reset success!  " + lpparam.processName);
					}
					if (MainManager.wxReceiver == null && lpparam.processName.equals(HookParams.WECHAT_PACKAGE_NAME)) {
						IntentFilter intentFilter = new IntentFilter("WxAction");
						MainManager.wxReceiver = new WxReceiver();
						application.registerReceiver(MainManager.wxReceiver, intentFilter);
						XLog.d("wxReceiver is reset success!  " + lpparam.processName);
					}
				}
			}
		});
		XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				if (MainManager.activity == null && methodHookParam.thisObject != null) {
					MainManager.activity = (Activity) methodHookParam.thisObject;
					XLog.d("mActivity is reset success!  " + lpparam.processName);
				}
			}
		});
	}
	
	/**
	 * 判断是否初始化完成
	 */
	public static boolean isInitComplete() {
		if (MainManager.activity == null) {
			return false;
		}
		if (MainManager.wxLpparam == null) {
			return false;
		}
		if (MainManager.wxReceiver == null) {
			return false;
		}
		return true;
	}
}
