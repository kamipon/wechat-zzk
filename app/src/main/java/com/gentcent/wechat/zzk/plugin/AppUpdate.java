package com.gentcent.wechat.zzk.plugin;

import com.gentcent.wechat.zzk.model.appupdate.AppInstallerHook;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;


public class AppUpdate implements IPlugin {
	@Override
	public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
		AppInstallerHook.hook(lpparam);
	}
}
