package com.gentcent.wechat.zzk.model.wallet;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.concurrent.TimeUnit;

public class MyKeyboardWindow {
	public static LinearLayout a;
	public static OnClickListener b;
	public static Handler c;
	
	public static void a(final LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod("com.tenpay.android.wechat.MyKeyboardWindow", loadPackageParam.classLoader, "init", Context.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					MyKeyboardWindow.a = (LinearLayout) methodHookParam.thisObject;
					MyKeyboardWindow.a.getContext();
					MyKeyboardWindow.b = (OnClickListener) XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tenpay.android.wechat.MyKeyboardWindow$1"), new Object[]{MyKeyboardWindow.a});
					MyKeyboardWindow.c = new Handler(Looper.getMainLooper());
					XLog.d("MyKeyboardWindow init");
				}
			});
		} catch (Throwable th) {
			XLog.d("MyKeyboardWindow  hookTargetClass e:" + Log.getStackTraceString(th));
		}
	}
	
	public static void b(int i) {
		XLog.d("MyKeyboardWindow  input :" + i);
		if (i >= 0 && i <= 9) {
			XLog.d("MyKeyboardWindow  input setup1  g_MyKeyboardWindow is " + a);
			LinearLayout linearLayout = a;
			Button button = (Button) XposedHelpers.getObjectField(linearLayout, "mKey" + i);
			if (button != null) {
				XLog.d("MyKeyboardWindow  input setup2");
				b.onClick(button);
				return;
			}
			XLog.d("input password ");
		}
	}
	
	public static void a(final int[] iArr) {
		StringBuilder sb = new StringBuilder();
		sb.append("MyKeyboardWindow  inputs : ");
		sb.append(iArr.length);
		XLog.d(sb.toString());
		LinearLayout linearLayout = a;
		if (linearLayout != null && b != null && linearLayout.getVisibility() == View.VISIBLE) {
			XLog.d("MyKeyboardWindow  inputs start");
			if (c == null) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append("error inputs mHandler is null bug fix success MainHook.mActivity is ");
				sb2.append(MainManager.activity);
				XLog.d("MyKeyboardWindow" + sb2.toString());
			}
			for (int i = 0; i < iArr.length; i++) {
				Handler handler = c;
				if (handler == null) {
					final int finalI = i;
					ThreadPoolUtils.getInstance().a(new Runnable() {
						public void run() {
							if (MainManager.activity != null) {
								MainManager.activity.runOnUiThread(new Runnable() {
									public void run() {
										MyKeyboardWindow.b(iArr[finalI]);
										if (finalI == 5) {
											MyKeyboardWindow.a = null;
											MyKeyboardWindow.b = null;
											MyKeyboardWindow.c = null;
											WalletPayUI.c = true;
										}
									}
								});
							}
						}
					}, (long) ((i + 1) * 500), TimeUnit.MILLISECONDS);
				} else {
					final int finalI1 = i;
					handler.postDelayed(new Runnable() {
						public void run() {
							MyKeyboardWindow.b(iArr[finalI1]);
							if (finalI1 == 5) {
								MyKeyboardWindow.a = null;
								MyKeyboardWindow.b = null;
								MyKeyboardWindow.c = null;
								WalletPayUI.c = true;
							}
						}
					}, (long) ((i + 1) * 500));
				}
			}
		}
	}
}
