package com.gentcent.wechat.zzk.model.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.model.wallet.bean.PayInfo;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

public class LuckyMoney {
	public static long a;
	public static Activity b;
	
	public static void a() {
		Activity activity = b;
		if (activity != null) {
			activity.finish();
		}
	}
	
	public static void a(LoadPackageParam loadPackageParam, PayInfo payInfo) {
		Intent intent = new Intent();
		intent.putExtra("key_way", 0);
		intent.putExtra("key_type", 0);
		intent.putExtra("key_from", 1);
		intent.putExtra("key_username", payInfo.receiver_name);
		intent.putExtra("pay_channel", 11);
		intent.putExtra("shenshou", true);
		intent.putExtra(PayInfo.Intent_Tag, GsonUtils.GsonString(payInfo));
		a(loadPackageParam, intent);
	}
	
	public static void a(LoadPackageParam loadPackageParam, Intent intent) {
		String str = "MyLuckyMoney";
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("startActivity start ");
			sb.append(MainManager.activity != null);
			XLog.d(str + sb.toString());
			if (MainManager.activity != null) {
				XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.br.d"), "b", MainManager.activity, "luckymoney", ".ui.LuckyMoneyPrepareUI", intent);
			}
			XLog.d("MyLuckyMoney" + "startActivity success");
		} catch (Throwable th) {
			StringBuilder sb2 = new StringBuilder();
			sb2.append("startActivity  e：");
			sb2.append(th.getMessage());
			XLog.d("MyLuckyMoney" + sb2.toString());
			th.printStackTrace();
		}
	}
	
	public static void b(LoadPackageParam loadPackageParam, PayInfo payInfo) {
		try {
			Intent intent = new Intent();
			int intValue = (Integer) XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.m"), "mb", new Object[]{payInfo.receiver_name});
			StringBuilder sb = new StringBuilder();
			sb.append("gI : ");
			sb.append(intValue);
			XLog.d("MyLuckyMoney" + sb.toString());
			intent.putExtra("key_way", 1);
			intent.putExtra("key_chatroom_num", intValue);
			intent.putExtra("key_type", 1);
			intent.putExtra("key_from", 1);
			intent.putExtra("key_username", payInfo.receiver_name);
			intent.putExtra("pay_channel", 14);
			intent.putExtra("shenshou", true);
			intent.putExtra(PayInfo.Intent_Tag, GsonUtils.GsonString(payInfo));
			a(loadPackageParam, intent);
		} catch (Throwable th) {
			StringBuilder sb2 = new StringBuilder();
			sb2.append(" send_chroom e:");
			sb2.append(th.getMessage());
			XLog.d("MyLuckyMoney" + sb2.toString());
			th.printStackTrace();
		}
	}
	
	public static void a(final LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					XLog.d("MyLuckyMoney" + "LuckyMoneyPrepareUI onCreate first");
					if (!LuckyMoney.b()) {
						XLog.d("MyLuckyMoney" + "LuckyMoneyPrepareUI onCreate second");
						final Activity activity = (Activity) methodHookParam.thisObject;
						if (activity.getIntent().getBooleanExtra("shenshou", false)) {
							new Handler().postDelayed(new Runnable() {
								public void run() {
									PayInfo payInfo = GsonUtils.GsonToBean(activity.getIntent().getStringExtra(PayInfo.Intent_Tag), PayInfo.class);
									Object a2 = LuckyMoney.b(loadPackageParam, activity, payInfo);
									if (a2 != null) {
										LuckyMoney.b = activity;
										WalletPayUI.a = true;
										WalletPayUI.c = false;
										WalletPayUI.b = payInfo;
										XposedHelpers.callMethod(activity, "b", new Object[]{a2, Boolean.valueOf(false)});
									}
								}
							}, 1000);
						}
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI", loadPackageParam.classLoader, "onDestroy", new XC_MethodHook() {
				public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					if (LuckyMoney.b != null) {
						LuckyMoney.b = null;
					}
				}
			});
		} catch (Throwable th) {
			StringBuilder sb = new StringBuilder();
			sb.append("hookTargetClass e:");
			sb.append(th.getMessage());
			XLog.d("MyLuckyMoney" + sb.toString());
			th.printStackTrace();
		}
	}
	
	public static boolean b() {
		boolean z = false;
		try {
			if (System.currentTimeMillis() - a < 500) {
				z = true;
			}
			a = System.currentTimeMillis();
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("onDoubleClick error is ");
			sb.append(e.getMessage());
			XLog.d(sb.toString());
		}
		return z;
	}
	
	public static Object b(LoadPackageParam loadPackageParam, Activity activity, PayInfo payInfo) {
		int i;
		int i2;
		int i3;
		long j;
		LoadPackageParam loadPackageParam2 = loadPackageParam;
		PayInfo payInfo2 = payInfo;
		try {
			long j2 = (long) (payInfo2.money * 100.0d);
			String str = (String) XposedHelpers.callStaticMethod(loadPackageParam2.classLoader.loadClass("com.tencent.mm.plugin.luckymoney.model.w"), "bHy", new Object[0]);
			String stringExtra = activity.getIntent().getStringExtra("key_username");
			String str2 = (String) XposedHelpers.callStaticMethod(loadPackageParam2.classLoader.loadClass("com.tencent.mm.plugin.luckymoney.model.w"), "mj", new Object[]{stringExtra});
			String str3 = (String) XposedHelpers.callStaticMethod(loadPackageParam2.classLoader.loadClass("com.tencent.mm.model.q"), "Wt", new Object[0]);
			String str4 = (String) XposedHelpers.callStaticMethod(loadPackageParam2.classLoader.loadClass("com.tencent.mm.model.q"), "Wv", new Object[0]);
			if (payInfo2.chatroom_type != 1) {
				if (payInfo2.chatroom_type != 0) {
					j = j2;
					i3 = 1;
					i2 = 0;
					i = 0;
					return XposedHelpers.newInstance(loadPackageParam2.classLoader.loadClass("com.tencent.mm.plugin.luckymoney.model.an"), i3, j2, j, i2, payInfo2.Remarks, str, stringExtra, str2, str3, str4, Integer.valueOf(i), "");
				}
			}
			i3 = payInfo2.chatroom_num;
			i2 = payInfo2.chatroom_type;
			if (payInfo2.chatroom_type == 0) {
				long j3 = (long) (payInfo2.money * 100.0d);
				i = 1;
				long j4 = j3;
				j2 = ((long) i3) * j3;
				j = j4;
			} else {
				j = 0;
				i = 1;
			}
			return XposedHelpers.newInstance(loadPackageParam2.classLoader.loadClass("com.tencent.mm.plugin.luckymoney.model.an"), i3, j2, j, i2, payInfo2.Remarks, str, stringExtra, str2, str3, str4, Integer.valueOf(i), "");
		} catch (Throwable th) {
			StringBuilder sb = new StringBuilder();
			sb.append(" getMsgObj e:");
			sb.append(th.getMessage());
			XLog.d("MyLuckyMoney" + sb.toString());
			th.printStackTrace();
			return null;
		}
	}
}
