package com.gentcent.wechat.enhancement.util;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;

import com.gentcent.wechat.enhancement.WxReceiver;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;

/**
 * @author zuozhi
 * @since 2019-07-09
 */
public class StaticDepot {
	public static Activity activity;
	public static LoadPackageParam wxLpparam;
	public static WxReceiver wxReceiver;
	
	public static void init (final LoadPackageParam lpparam){
		XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				if (lpparam.isFirstApplication) {
					Application application = (Application) methodHookParam.thisObject;
					
					StaticDepot.wxLpparam = lpparam;
					StaticDepot.wxLpparam.classLoader = application.getClassLoader();
					
					IntentFilter intentFilter = new IntentFilter("WxAction");
					StaticDepot.wxReceiver = new WxReceiver();
					application.registerReceiver(StaticDepot.wxReceiver, intentFilter);
				}
			}
		});
		XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				if (StaticDepot.activity == null) {
					StaticDepot.activity = (Activity) methodHookParam.thisObject;
					XLog.d("mActivity is reset success!");
				}
			}
		});
	}
}
