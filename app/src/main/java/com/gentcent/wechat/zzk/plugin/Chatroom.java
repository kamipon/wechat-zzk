package com.gentcent.wechat.zzk.plugin;

import com.gentcent.wechat.zzk.model.sns.SendSnsHook;
import com.gentcent.wechat.zzk.model.sns.SnsHook;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;


public class Chatroom implements IPlugin {
	@Override
	public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
		SendSnsHook.hook(lpparam);
		SnsHook.hook(lpparam);
	}
}
