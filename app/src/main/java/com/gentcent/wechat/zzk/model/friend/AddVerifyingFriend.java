package com.gentcent.wechat.zzk.model.friend;


import android.util.Log;

import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;

import java.util.LinkedList;

public class AddVerifyingFriend {
	static String a = "com.tencent.mm.model.aw";
	static String b = "aeU";
	static String c = "com.tencent.mm.model.t";
	static String d = "q";
	static String e = "com.tencent.mm.kernel.g";
	static String f = "ab";
	static String g = "acU";
	static String h = "asX";
	static String i = "com.tencent.mm.plugin.messenger.foundation.a.j";
	static String j = "com.tencent.mm.pluginsdk.model.m";
	static String k = "dLi";
	static String l = "com.tencent.mm.plugin.report.service.h";
	static String m = "Wa";
	static String n = "fbW";
	static String o = "a";
	static String p = "rdc";
	static String q = "e";
	
	/**
	 * 如果不是好友发来消息，自动添加好友
	 *
	 * @param wxID
	 */
	public static void run(String wxID) {
		try {
			XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass(a), b);
			Class loadClass = MainManager.wxLpparam.classLoader.loadClass(c);
			Class loadClass2 = MainManager.wxLpparam.classLoader.loadClass(e);
			Class loadClass3 = MainManager.wxLpparam.classLoader.loadClass(i);
			Object callStaticMethod = XposedHelpers.callStaticMethod(loadClass2, f, loadClass3);
			XLog.d("friendHook q ::" + callStaticMethod.getClass().getName());
			Object callMethod = XposedHelpers.callMethod(callStaticMethod, g);
			XLog.d("friendHook eo ::" + callMethod.getClass().getName());
			Object callMethod2 = XposedHelpers.callMethod(callMethod, h, wxID);
			String str2 = (String) XposedHelpers.getObjectField(callMethod2, k);
			Class loadClass4 = MainManager.wxLpparam.classLoader.loadClass(j);
			LinkedList linkedList = new LinkedList();
			linkedList.add(wxID);
			LinkedList linkedList2 = new LinkedList();
			linkedList2.add(3);
			LinkedList linkedList3 = new LinkedList();
			linkedList3.add(str2);
			Object newInstance = XposedHelpers.newInstance(loadClass4, 1, linkedList, linkedList2, linkedList3, "", "", null, "", "");
			XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(loadClass2, m), n), o, newInstance, 0);
			XposedHelpers.callStaticMethod(loadClass, d, callMethod2);
			XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(MainManager.wxLpparam.classLoader.loadClass(l), p), q, 11004, new Object[]{wxID, 3});
		} catch (Exception e2) {
			XLog.d("addVerifyingFriend error " + Log.getStackTraceString(e2));
		}
	}
}
