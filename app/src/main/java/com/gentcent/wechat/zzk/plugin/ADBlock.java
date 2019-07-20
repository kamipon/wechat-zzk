package com.gentcent.wechat.zzk.plugin;


import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;
import com.gentcent.wechat.zzk.util.PreferencesUtil;
import com.gentcent.wechat.zzk.util.HookParams;


public class ADBlock implements IPlugin {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(HookParams.getInstance().XMLParserClassName, lpparam.classLoader, HookParams.getInstance().XMLParserMethod, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                try {
                    if (!PreferencesUtil.isADBlock())
                        return;

                    if (param.args[1].equals("ADInfo"))
                        param.setResult(null);
                } catch (Error | Exception e) {
                }

            }
        });
    }

}
