package com.gentcent.wechat.zzk.model.message;

import android.text.TextUtils;

/* renamed from: android.support.v7.utils.d */
public class MessageResolver {
    public static String a(String str, String str2) {
        return a(str, str2, " ", false);
    }

    private static String a(String str, String str2, String str3, boolean z) {
        try {
            int indexOf = str.indexOf(str2);
            String substring = str.substring(str2.length() + indexOf, indexOf + str.substring(indexOf, str.length() - 1).indexOf(str3));
            if (substring.endsWith(")")) {
                substring = substring.substring(0, substring.length() - 1);
            }
            if (z) {
                return substring;
            }
            return substring.replace("\"", "");
        } catch (Exception unused) {
            return "";
        }
    }

    public static String a(String str) {
        return str.indexOf("fromusername=") > -1 ? a(str, "fromusername=") : "";
    }

    public static String b(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.contains(":") ? str.split(":")[0] : "";
    }
}
