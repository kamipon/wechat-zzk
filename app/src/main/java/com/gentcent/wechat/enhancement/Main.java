package com.gentcent.wechat.enhancement;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.gentcent.wechat.enhancement.plugin.ADBlock;
import com.gentcent.wechat.enhancement.plugin.AntiRevoke;
import com.gentcent.wechat.enhancement.plugin.AntiSnsDelete;
import com.gentcent.wechat.enhancement.plugin.AutoLogin;
import com.gentcent.wechat.enhancement.plugin.HideModule;
import com.gentcent.wechat.enhancement.plugin.IPlugin;
import com.gentcent.wechat.enhancement.plugin.Limits;
import com.gentcent.wechat.enhancement.plugin.LuckMoney;
import com.gentcent.wechat.enhancement.plugin.MessageHook;
import com.gentcent.wechat.enhancement.util.HookParams;
import com.gentcent.wechat.enhancement.util.MyHelper;
import com.gentcent.wechat.enhancement.util.SearchClasses;
import com.gentcent.wechat.enhancement.util.StaticDepot;
import com.gentcent.wechat.enhancement.util.XLog;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.log;


public class Main implements IXposedHookLoadPackage {
	
	private static IPlugin[] plugins = {
			new ADBlock(),
			new AntiRevoke(),
			new AntiSnsDelete(),
			new AutoLogin(),
			new HideModule(),
			new LuckMoney(),
			new Limits(),
			new MessageHook()
	};
	
	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) {
		
		try {
			if (lpparam.packageName.equals(HookParams.WECHAT_PACKAGE_NAME)) {
				XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
						super.afterHookedMethod(param);
						Context context = (Context) param.args[0];
						String processName = lpparam.processName;
						//Only hook important process
						if (!processName.equals(HookParams.WECHAT_PACKAGE_NAME) &&
								!processName.equals(HookParams.WECHAT_PACKAGE_NAME + ":tools")
						) {
							return;
						}
						String versionName = getVersionName(context, HookParams.WECHAT_PACKAGE_NAME);
						if (!HookParams.hasInstance()) {
							XLog.e("Found wechat version:" + versionName);
							SearchClasses.init(context, lpparam, versionName);
							loadPlugins(lpparam);
							
						}
					}
				});
				XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
					public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
						super.afterHookedMethod(methodHookParam);
						if (lpparam.isFirstApplication) {
							Application application = (Application) methodHookParam.thisObject;
							
							StaticDepot.wxLpparam = lpparam;
							StaticDepot.wxLpparam.classLoader = application.getClassLoader();
							
							IntentFilter intentFilter = new IntentFilter("MyAction");
							StaticDepot.acceptReceiver = new AcceptReceiver();
							application.registerReceiver(StaticDepot.acceptReceiver, intentFilter);
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
			
		} catch (Error | Exception e) {
			XLog.e("错误:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private String getVersionName(Context context, String packageName) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
			return packInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
		}
		return "";
	}
	
	
	private void loadPlugins(LoadPackageParam lpparam) {
		for (IPlugin plugin : plugins) {
			try {
				plugin.hook(lpparam);
			} catch (Error | Exception e) {
				log("loadPlugins error" + e);
			}
		}
	}
	
}
