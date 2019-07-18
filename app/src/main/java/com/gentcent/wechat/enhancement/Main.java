package com.gentcent.wechat.enhancement;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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
import com.gentcent.wechat.enhancement.util.SearchClasses;
import com.gentcent.wechat.enhancement.util.StaticDepot;
import com.gentcent.wechat.enhancement.util.XLog;

import com.gentcent.zzk.xped.IXposedHookLoadPackage;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import static com.gentcent.zzk.xped.XposedBridge.log;


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
					String versionName = getVersionName(context, HookParams.WECHAT_PACKAGE_NAME);
					//Only hook important process
					String processName = lpparam.processName;
					if (!processName.equals(HookParams.WECHAT_PACKAGE_NAME) &&
							!processName.equals(HookParams.WECHAT_PACKAGE_NAME + ":tools")
					) {
						return;
					}
					if (!HookParams.hasInstance()) {
						XLog.e("Found wechat version:" + versionName);
						SearchClasses.init(context, lpparam, versionName);
						loadPlugins(lpparam);
					}
					if (StaticDepot.isInitComplete == false && processName.equals(HookParams.WECHAT_PACKAGE_NAME)) {
						StaticDepot.init(lpparam);
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
		} catch (PackageManager.NameNotFoundException ignored) {
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
