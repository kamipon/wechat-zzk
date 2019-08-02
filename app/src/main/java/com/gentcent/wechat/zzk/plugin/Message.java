package com.gentcent.wechat.zzk.plugin;

import com.gentcent.wechat.zzk.model.message.MessageHook;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;


public class Message implements IPlugin {
	@Override
	public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
		MessageHook.hook(lpparam);
	}
}
