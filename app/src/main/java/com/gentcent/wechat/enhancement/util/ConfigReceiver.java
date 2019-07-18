package com.gentcent.wechat.enhancement.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class ConfigReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            boolean hasExtras = extras != null;
            if (HookParams.SAVE_WECHAT_ENHANCEMENT_CONFIG.equals(action)) {
                if (hasExtras) {
                    MyHelper.writeLine("params",extras.getString("params"));
                }
            }
        } catch (Error | Exception e) {
        }
    }

}
