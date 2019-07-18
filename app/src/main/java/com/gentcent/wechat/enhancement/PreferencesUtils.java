package com.gentcent.wechat.enhancement;


import com.gentcent.zzk.xped.XSharedPreferences;

public class PreferencesUtils {

    private static XSharedPreferences instance = null;
    
    private static XSharedPreferences getInstance() {
        if (instance == null) {
            instance = new XSharedPreferences(PreferencesUtils.class.getPackage().getName());
            instance.makeWorldReadable();
        } else {
            instance.reload();
        }
        return instance;
    }
    
    public static boolean open() {
        return getInstance().getBoolean("open", true);
    }

    public static boolean notSelf() {
        return getInstance().getBoolean("not_self", true);
    }

    public static boolean notWhisper() {
        return getInstance().getBoolean("not_whisper", false);
    }

    public static String notContains() {
        return getInstance().getString("not_contains", "").replace("，", ",");
    }

    public static boolean delay() {
        return getInstance().getBoolean("delay", true);
    }

    public static int delayMin() {
        return getInstance().getInt("delay_min", 2000);
    }

    public static int delayMax() {
        return getInstance().getInt("delay_max", 5000);
    }

    public static boolean receiveTransfer() {
        return getInstance().getBoolean("receive_transfer", true);
    }

    public static boolean quickOpen() {
        return getInstance().getBoolean("quick_open", false);
    }

    public static boolean showWechatId() {
        return getInstance().getBoolean("show_wechat_id", true);
    }

    public static String blackList() {
        return getInstance().getString("black_list", "").replace("，", ",");
    }

    public static boolean isAntiRevoke() {
        return getInstance().getBoolean("is_anti_revoke", true);
    }

    public static boolean isAntiSnsDelete() {
        return getInstance().getBoolean("is_anti_sns_delete", true);
    }

    public static boolean isADBlock() {
        return getInstance().getBoolean("is_ad_block", true);
    }

    public static boolean isAutoLogin() {
        return getInstance().getBoolean("is_auto_login", true);
    }

    public static boolean isBreakLimit() {
        return getInstance().getBoolean("is_break_limit", true);
    }

}


