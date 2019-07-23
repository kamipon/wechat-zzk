package com.gentcent.wechat.zzk.plugin;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.gentcent.wechat.zzk.manager.PengyouquanRangeManger;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class SnsHook implements IPlugin {
	private static final String TAG = "SnsHook";
	
	@Override
	public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				final Activity activity = (Activity) methodHookParam.thisObject;
				if (activity.getIntent().getBooleanExtra("shenshou", false)) {
					new Timer().schedule(new TimerTask() {
						public void run() {
							activity.finish();
						}
					}, OkHttpUtils.DEFAULT_MILLISECONDS);
				}
			}
		});
		final String[] strArr = {""};
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.SnsUploadUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				XLog.d("SnsUploadUI onCreate sleep");
				final Activity activity = (Activity) methodHookParam.thisObject;
				boolean booleanExtra = activity.getIntent().getBooleanExtra("shenshou", false);
				boolean booleanExtra2 = activity.getIntent().getBooleanExtra("snsfriendsnsuploadui", false);
				StringBuilder sb = new StringBuilder();
				sb.append("isMoreChatSendFalg  ");
				sb.append(booleanExtra);
				sb.append("  snsfriendFlag is ");
				sb.append(booleanExtra2);
				XLog.d(sb.toString());
				if (booleanExtra || booleanExtra2) {
					Intent intent = activity.getIntent();
					if (intent != null) {
						String stringExtra = intent.getStringExtra("Kdescription");
						StringBuilder sb2 = new StringBuilder();
						sb2.append("SnsUploadUI onCreate stringExtra ");
						sb2.append(stringExtra);
						XLog.d(sb2.toString());
						StringBuilder sb3 = new StringBuilder();
						sb3.append("SnsUploadUI onCreate LookUpType ");
						sb3.append(intent.getIntExtra("LookUpType", 0));
						XLog.d(sb3.toString());
						if (!TextUtils.isEmpty(stringExtra)) {
							XposedHelpers.callMethod((TextView) XposedHelpers.getObjectField(activity, "qHY"), "setText", new Object[]{stringExtra});
						}
					}
					int intExtra = intent.getIntExtra("LookUpType", 0);
					
					//首次回复
//					Constant.b = intent.getStringExtra("SelfComment");
//					StringBuilder sb4 = new StringBuilder();
//					sb4.append("Constant.SelfCommend is ");
//					sb4.append(Constant.b);
//					sb4.append(" LookUpType is ");
//					sb4.append(intExtra);
//					XLog.d(sb4.toString());
					if (intExtra != 0) {
						switch (intExtra) {
							case 2:
								SnsHook.setWhoSeeSns(activity, intent, 2);
								break;
							case 3:
								SnsHook.setWhoSeeSns(activity, intent, 3);
								break;
						}
					}
					String stringExtra2 = intent.getStringExtra("SendType");
					if (stringExtra2 == null || !stringExtra2.equals("SSRJVideo")) {
						ThreadPoolUtils.getInstance().a(new Runnable() {
							public void run() {
								SnsHook.clickSendSns(activity);
							}
						}, 5000, TimeUnit.MILLISECONDS);
						return;
					}
					String stringExtra3 = intent.getStringExtra("KSightThumbPath");
					String stringExtra4 = intent.getStringExtra("KSightPath");
					Intent intent2 = new Intent(activity, lpparam.classLoader.loadClass("com.tencent.mm.plugin.mmsight.segment.VideoCompressUI"));
					intent2.putExtra("K_SEGMENTVIDEOPATH", stringExtra4);
					intent2.putExtra("KSEGMENTVIDEOTHUMBPATH", stringExtra3);
					activity.startActivityForResult(intent2, 9);
				}
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.SnsUploadUI", lpparam.classLoader, "onActivityResult", Integer.TYPE, Integer.TYPE, Intent.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				String str = SnsHook.TAG;
				StringBuilder sb = new StringBuilder();
				sb.append("onActivityResult:");
				sb.append(methodHookParam.args[0]);
				sb.append(",");
				sb.append(methodHookParam.args[1]);
				XLog.d(str+ sb.toString());
				if (((Integer) methodHookParam.args[0]).intValue() == 9) {
					final Activity activity = (Activity) methodHookParam.thisObject;
					Intent intent = activity.getIntent();
					String stringExtra = intent.getStringExtra("SendType");
					if (stringExtra != null || stringExtra.equals("SSRJVideo")) {
						String stringExtra2 = intent.getStringExtra("KSightPath");
						String str2 = SnsHook.TAG;
						StringBuilder sb2 = new StringBuilder();
						sb2.append("onActivityResult KSightPath:");
						sb2.append(stringExtra2);
						XLog.d(str2+ sb2.toString());
						if (stringExtra2 != null) {
							ThreadPoolUtils.getInstance().a(new Runnable() {
								public void run() {
									SnsHook.clickSendSns(activity);
								}
							}, 2000, TimeUnit.MILLISECONDS);
						}
					}
				}
			}
		});
//		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.SnsUploadUI", lpparam.classLoader, "onDestroy", new XC_MethodHook() {
//			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//				Activity activity = (Activity) methodHookParam.thisObject;
//				if (activity != null && activity.getIntent().getBooleanExtra("snsfriendsnsuploadui", false)) {
//					XLog.d("SnsUploadUI onDestroy ");
//					if (TextUtils.isEmpty(strArr[0])) {
//						ad.a();
//					} else {
//						ad.a(strArr[0]);
//					}
//				}
//			}
//		});
	}
	
	public static void clickSendSns(Activity activity) {
		Object objectField = XposedHelpers.getObjectField(activity, "mController");
		if (objectField == null) {
			XLog.d(TAG+ "send sns click mController is null");
			return;
		}
		final TextView textView = (TextView) XposedHelpers.getObjectField(objectField, "xrx");
		if (textView == null) {
			XLog.d(TAG+ "send sns click commit error is textView null");
		} else {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					XLog.d("send sns click start");
					textView.performClick();
					XLog.d("send sns click success");
				}
			});
		}
	}
	
	public static void setWhoSeeSns(Activity activity, Intent intent, int i) {
		StringBuffer stringBuffer = new StringBuffer();
		ArrayList stringArrayListExtra = intent.getStringArrayListExtra("LookFriendWxIdList");
		if (stringArrayListExtra != null && stringArrayListExtra.size() > 0) {
			Iterator it = stringArrayListExtra.iterator();
			while (it.hasNext()) {
				String str = (String) it.next();
				StringBuilder sb = new StringBuilder();
				sb.append("setWhoSeeSns str");
				sb.append(str);
				XLog.d(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append(str);
				sb2.append(",");
				stringBuffer.append(sb2.toString());
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		}
		StringBuilder sb3 = new StringBuilder();
		sb3.append("setWhoSeeSns");
		sb3.append(stringBuffer.toString());
		XLog.d(sb3.toString());
		new PengyouquanRangeManger(i, "", stringBuffer.toString()).addRange(activity);
	}
	
	private static void IntentArgsLog(Intent intent) {
		for (String str : intent.getExtras().keySet()) {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("SnsUploadUI onCreate");
				sb.append(str);
				sb.append("  value ::");
				sb.append(intent.getStringExtra(str));
				XLog.d(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
