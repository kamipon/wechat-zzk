package com.gentcent.wechat.enhancement.plugin;


import android.app.Activity;
import android.widget.Button;

import java.lang.reflect.Field;

import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;
import com.gentcent.wechat.enhancement.util.PreferencesUtil;
import com.gentcent.wechat.enhancement.util.HookParams;


public class AutoLogin implements IPlugin {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(android.app.Activity.class, "onStart", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                try {
                    if (!PreferencesUtil.isAutoLogin())
                        return;
                    if (!(param.thisObject instanceof Activity)) {
                        return;
                    }
                    Activity activity = (Activity) param.thisObject;
                    if (activity.getClass().getName().equals(HookParams.getInstance().WebWXLoginUIClassName)) {
                        Class clazz = activity.getClass();
                        Field field = XposedHelpers.findFirstFieldByExactType(clazz, Button.class);
                        Button button = (Button) field.get(activity);
                        if (button != null) {
                            button.performClick();
                        }
                    }

                } catch (Error | Exception e) {
                }
            }
        });

    }

}
