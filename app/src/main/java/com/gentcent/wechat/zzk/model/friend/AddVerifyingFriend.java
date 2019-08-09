package com.gentcent.wechat.zzk.model.friend;


import android.util.Log;

import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.LinkedList;

public class AddVerifyingFriend {
	static String a = "com.tencent.mm.model.av";
	static String b = "XE";
	static String c = "com.tencent.mm.model.s";
	static String d = "com.tencent.mm.storage.ad";
	static String e = "com.tencent.mm.kernel.g";
	static String f = "com.tencent.mm.plugin.messenger.foundation.a.j";
	static String g = "com.tencent.mm.pluginsdk.model.m";
	static String h = "L";
	static String i = "VI";
	static String j = "anm";
	static String k = "dia";
	static String l = "com.tencent.mm.plugin.report.service.h";
	static String m = "q";
	static String n = "Qd";
	static String o = "evj";
	static String p = "a";
	static String q = "ptS";
	static String r = "e";
	
	/**
	 * 如果不是好友发来消息，自动添加好友
	 *
	 * @param wxID
	 */
	public static void run(String wxID) {
		try {
			LoadPackageParam lpparams = MainManager.wxLpparam;
			XposedHelpers.callStaticMethod(lpparams.classLoader.loadClass(a), b);
			lpparams.classLoader.loadClass(d);
			Class loadClass = lpparams.classLoader.loadClass(c);
			Class loadClass2 = lpparams.classLoader.loadClass(e);
			Class loadClass3 = lpparams.classLoader.loadClass(f);
			Object callStaticMethod = XposedHelpers.callStaticMethod(loadClass2, h, loadClass3);
			Object callMethod = XposedHelpers.callMethod(callStaticMethod, i);
			Object callMethod2 = XposedHelpers.callMethod(callMethod, j, wxID);
			String str2 = (String) XposedHelpers.getObjectField(callMethod2, k);
			Class loadClass4 = lpparams.classLoader.loadClass(g);
			LinkedList linkedList = new LinkedList();
			linkedList.add(wxID);
			LinkedList linkedList2 = new LinkedList();
			linkedList2.add(3);
			LinkedList linkedList3 = new LinkedList();
			linkedList3.add(str2);
			Object newInstance = XposedHelpers.newInstance(loadClass4, 1, linkedList, linkedList2, linkedList3, "", "", null, "", "");
			XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(loadClass2, n), o), p, newInstance, 0);
			XposedHelpers.callStaticMethod(loadClass, m, callMethod2);
			XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(lpparams.classLoader.loadClass(l), q), r, 11004, new Object[]{wxID, 3});
		} catch (Exception e2) {
			XLog.d("addVerifyingFriend error " + Log.getStackTraceString(e2));
		}
	}
}
