package com.gentcent.wechat.zzk;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.activity.keepalive.KeepAlive;
import com.gentcent.wechat.zzk.service.WechatSupport;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.MyWxNames;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.*;

import java.util.concurrent.TimeUnit;

/**
 * @author zuozhi
 * @since 2019-07-09
 */
public class MainManager {
	public static Activity activity;
	public static LoadPackageParam wxLpparam;
	public static WxReceiver wxReceiver;
	public static MyWxNames myWxNames;
	public static boolean wxCoreIsinit = false;
	
	public static void init(final LoadPackageParam lpparam) throws ClassNotFoundException {
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
						intentFilter.addAction(Intent.ACTION_TIME_TICK);
						MainManager.wxReceiver = new WxReceiver();
						application.registerReceiver(MainManager.wxReceiver, intentFilter);
						XLog.d("wxReceiver is reset success!  " + lpparam.processName);
					}
					if (myWxNames == null) {
						myWxNames = new MyWxNames();
						XLog.d("myWxNames is reset success!  " + lpparam.processName);
					}
				}
			}
		});
		XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				//版本检测
				if (methodHookParam.thisObject != null && TextUtils.equals(methodHookParam.thisObject.getClass().getName(), "com.tencent.mm.ui.LauncherUI")) {
					WechatSupport.checkVersionInWechat((Activity) methodHookParam.thisObject);
				}
				if (MainManager.activity == null && methodHookParam.thisObject != null) {
					MainManager.activity = (Activity) methodHookParam.thisObject;
					XLog.d("mActivity is reset success!  " + lpparam.processName);
				}
			}
		});
		
		XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.mm.ui.LauncherUI"), "onCreate", Bundle.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				if (lpparam.isFirstApplication) {
				}
			}
		});
		XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.mm.kernel.a"), "Vk", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) {
				boolean booleanValue = (Boolean) methodHookParam.getResult();
				if (booleanValue && !wxCoreIsinit) {
					keep(lpparam);
				}
				wxCoreIsinit = booleanValue;
			}
		});
	}
	
	public static void keep(LoadPackageParam lpparam) {
		ThreadPoolUtils.getInstance().a(new Runnable() {
			public void run() {
				if (activity != null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
//							XLog.d("Xposed - - - - - - KeepAlive");
							KeepAlive.a(MainManager.activity, "com.gentcent.wechat.zzk", 30000);
							if (ObjectUtils.isEmpty(MyHelper.readLine("verify_already_force"))) {
								MyHelper.writeLine("verify_already_force", "true");
							}
						}
					});
				}
			}
		}, 5000, TimeUnit.MILLISECONDS);
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
