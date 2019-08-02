package com.gentcent.wechat.zzk.plugin;

import com.gentcent.wechat.zzk.model.friend.FriendsHook;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import static com.gentcent.zzk.xped.XposedBridge.log;


public class Friends implements IPlugin {
	@Override
	public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
		FriendsHook.hook(lpparam);
	}
}
