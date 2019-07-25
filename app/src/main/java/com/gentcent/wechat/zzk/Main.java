package com.gentcent.wechat.zzk;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.plugin.ADBlock;
import com.gentcent.wechat.zzk.plugin.AntiRevoke;
import com.gentcent.wechat.zzk.plugin.AntiSnsDelete;
import com.gentcent.wechat.zzk.plugin.AutoLogin;
import com.gentcent.wechat.zzk.plugin.FriendsHook;
import com.gentcent.wechat.zzk.plugin.HideModule;
import com.gentcent.wechat.zzk.plugin.IPlugin;
import com.gentcent.wechat.zzk.plugin.Limits;
import com.gentcent.wechat.zzk.plugin.LuckMoney;
import com.gentcent.wechat.zzk.plugin.MessageHook;
import com.gentcent.wechat.zzk.plugin.SendSnsHook;
import com.gentcent.wechat.zzk.plugin.SnsHook;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.SearchClasses;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.IXposedHookLoadPackage;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;


public class Main implements IXposedHookLoadPackage {
	
	private static IPlugin[] plugins = {
			new ADBlock(),
			new AntiRevoke(),
			new AntiSnsDelete(),
			new AutoLogin(),
			new HideModule(),
			new LuckMoney(),
			new Limits(),
			new MessageHook(),
			new FriendsHook(),
			new SendSnsHook(),
			new SnsHook()
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
							XLog.d("Found wechat version:" + versionName + " " + lpparam.processName);
							SearchClasses.init(context, lpparam, versionName);
							loadPlugins(lpparam);
						}
						if (!MainManager.isInitComplete()) {
							MainManager.init(lpparam);
							try {
								if(processName.equals(HookParams.WECHAT_PACKAGE_NAME)){
									AppUtils.launchApp(HookParams.WECHAT_PACKAGE_NAME);
								}
							} catch (Exception ignored) {
							}
						}
					}
				});
				
				//全局搜索xposed字段出现的方法，可能是微信的Xposed检测
				XposedHelpers.findAndHookMethod("com.tencent.mm.app.t", lpparam.classLoader, "a", StackTraceElement[].class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						
						super.afterHookedMethod(param);
						if ((Boolean) param.getResult()) {
							param.setResult(false);
							XLog.d("--###--微信检测到xposed(已经自动隐藏)--###--");
						}
					}
				});
			}
		} catch (Error | Exception e) {
			XLog.e("错误:" + Log.getStackTraceString(e));
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
				if (!"name == null".equals(e.getMessage()))
					XLog.e("错误:" + Log.getStackTraceString(e));
			}
		}
	}
	
}
