package com.gentcent.wechat.zzk.util;

import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * @author zuozhi
 * @since 2019-08-01
 */
public class ZzkUtil {
	private static String[] appList = {"filehelper", "qqmail", "floatbottle", "shakeapp", "lbsapp", "medianote", "newsapp", "facebookapp", "qqfriend", "masssendapp", "feedsapp", "voipapp", "officialaccounts", "voicevoipapp", "voiceinputapp", "linkedinplugin", "notifymessage", "fmessage", "weixin", "qmessage", "tmessage"};
	
	public static Object getWxCore(LoadPackageParam loadPackageParam) {
		try {
			return XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.kernel.g"), "L", loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.messenger.foundation.a.j"));
		} catch (Throwable th) {
			XLog.e("ZzkUtil  getWxCore e:" + Log.getStackTraceString(th));
			return null;
		}
	}
	
	public static String b(String str) {
		JSONObject build = new XmlToJson.Builder(str).build();
		return build.toString();
	}
	
	public static boolean m(String str) {
		return (ObjectUtils.isNotEmpty((CharSequence) str) && str.endsWith("@chatroom")) || l(str);
	}
	
	public static boolean l(String str) {
		return ObjectUtils.isNotEmpty((CharSequence) str) && !str.startsWith("gh_") && !str.startsWith("fake_") && !str.endsWith("@chatroom") && !Arrays.asList(appList).contains(str);
	}
}
