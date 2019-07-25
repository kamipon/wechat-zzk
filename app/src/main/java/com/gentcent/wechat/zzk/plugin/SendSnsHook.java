package com.gentcent.wechat.zzk.plugin;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.manager.PengyouquanRangeManger;
import com.gentcent.wechat.zzk.manager.SnsManager;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class SendSnsHook implements IPlugin {
	private static final String TAG = "SnsHook:  ";
	
	@Override
	public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
		
		final String[] selfComment = {""};
		XposedHelpers.findAndHookMethod(HookParams.SnsUploadUI, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				XLog.d("SnsUploadUI onCreate sleep");
				final Activity activity = (Activity) methodHookParam.thisObject;
				boolean booleanExtra = activity.getIntent().getBooleanExtra("zzk", false);
				boolean booleanExtra2 = activity.getIntent().getBooleanExtra("snsfriendsnsuploadui", false);
				XLog.d("isMoreChatSendFalg  " + booleanExtra + "  snsfriendFlag is " + booleanExtra2);
				if (booleanExtra || booleanExtra2) {
					Intent intent = activity.getIntent();
					if (intent != null) {
						String stringExtra = intent.getStringExtra("Kdescription");
						XLog.d("SnsUploadUI onCreate stringExtra " + stringExtra);
						XLog.d("SnsUploadUI onCreate LookUpType " + intent.getIntExtra("LookUpType", 0));
						if (!TextUtils.isEmpty(stringExtra)) {
							XposedHelpers.callMethod((TextView) XposedHelpers.getObjectField(activity, "qHY"), "setText", stringExtra);
						}
					}
					//首次回复
					selfComment[0] = intent.getStringExtra("SelfComment");
					XLog.d("SelfCommend is " + selfComment[0]);
					
					int intExtra = intent.getIntExtra("LookUpType", 0);
					if (intExtra != 0) {
						switch (intExtra) {
							case 2:
								SendSnsHook.setWhoSeeSns(activity, intent, 2);
								break;
							case 3:
								SendSnsHook.setWhoSeeSns(activity, intent, 3);
								break;
						}
					}
					String stringExtra2 = intent.getStringExtra("SendType");
					if (stringExtra2 == null || !stringExtra2.equals("SSRJVideo")) {
						ThreadPoolUtils.getInstance().a(new Runnable() {
							public void run() {
								XLog.d("clickSendSns");
								SendSnsHook.clickSendSns(activity);
							}
						}, 5000, TimeUnit.MILLISECONDS);
						return;
					}
					XLog.d("is equals SSRJVideo");
					String stringExtra3 = intent.getStringExtra("KSightThumbPath");
					String stringExtra4 = intent.getStringExtra("KSightPath");
					
					Class<?> aClass = lpparam.classLoader.loadClass(HookParams.VideoCompressUI);
					
					Intent intent2 = new Intent(activity, aClass);
					intent2.putExtra("KSEGMENTVIDEOTHUMBPATH", stringExtra3);
					intent2.putExtra("K_SEGMENTVIDEOPATH", stringExtra4);
					activity.startActivityForResult(intent2, 9);
				}
			}
		});
		XposedHelpers.findAndHookMethod(HookParams.SnsUploadUI, lpparam.classLoader, "onActivityResult", Integer.TYPE, Integer.TYPE, Intent.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				XLog.d(SendSnsHook.TAG + "onActivityResult:" + methodHookParam.args[0] + "," + methodHookParam.args[1]);
				if ((Integer) methodHookParam.args[0] == 9) {
					final Activity activity = (Activity) methodHookParam.thisObject;
					Intent intent = activity.getIntent();
					String stringExtra = intent.getStringExtra("SendType");
					if (stringExtra != null || stringExtra.equals("SSRJVideo")) {
						String stringExtra2 = intent.getStringExtra("KSightPath");
						XLog.d(SendSnsHook.TAG + "onActivityResult KSightPath:" + stringExtra2);
						if (stringExtra2 != null) {
							ThreadPoolUtils.getInstance().a(new Runnable() {
								public void run() {
									SendSnsHook.clickSendSns(activity);
								}
							}, 2000, TimeUnit.MILLISECONDS);
						}
					}
				}
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.SnsUploadUI", lpparam.classLoader, "onDestroy", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				Activity activity = (Activity) methodHookParam.thisObject;
				if (activity != null && activity.getIntent().getBooleanExtra("snsfriendsnsuploadui", false)) {
					XLog.d("SnsUploadUI onDestroy ");
					if (TextUtils.isEmpty(selfComment[0])) {
//						goSnsTimeLine();
					} else {
						goSnsTimeLine(selfComment[0]);
					}
				}
			}
		});
		
//		XposedHelpers.findAndHookMethod(HookParams.SnsTimeLineUI, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
//			public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//				super.beforeHookedMethod(methodHookParam);
//				final Activity activity = (Activity) methodHookParam.thisObject;
//				Intent intent = activity.getIntent();
//				boolean zzk = intent.getBooleanExtra("zzk", false);
//				if(zzk){
//					String comment = intent.getStringExtra("comment");
//
//				}
//			}
//		});
	}
	
	/**
	 * 跳转至朋友圈
	 */
	private static void goSnsTimeLine() {
		try {
			Intent intent = new Intent();
			intent.setClassName(HookParams.WECHAT_PACKAGE_NAME, HookParams.SnsTimeLineUI);
			intent.putExtra("zzk", true);
			MainManager.activity.startActivity(intent);
		} catch (Exception e) {
			XLog.d("执行界面操作失败：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 跳转至朋友圈+首次评论
	 *
	 * @param selfComment 评论内容
	 */
	private static void goSnsTimeLine(String selfComment) {
		try {
			Intent intent = new Intent();
			intent.setClassName(HookParams.WECHAT_PACKAGE_NAME, HookParams.SnsTimeLineUI);
			intent.putExtra("comment", selfComment);
			intent.putExtra("zzk", true);
			MainManager.activity.startActivity(intent);
			SnsManager.SelfCommend = selfComment;
		} catch (Exception e) {
			XLog.d("执行界面操作失败：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 点击发送按钮
	 */
	private static void clickSendSns(Activity activity) {
		Object objectField = XposedHelpers.getObjectField(activity, "mController");
		if (objectField == null) {
			XLog.d(TAG + "send sns click mController is null");
			return;
		}
		final TextView textView = (TextView) XposedHelpers.getObjectField(objectField, "xrx");
		if (textView == null) {
			XLog.d(TAG + "send sns click commit error is textView null");
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
	
	/**
	 * 设置谁可以看
	 */
	private static void setWhoSeeSns(Activity activity, Intent intent, int i) {
		StringBuilder stringBuffer = new StringBuilder();
		ArrayList stringArrayListExtra = intent.getStringArrayListExtra("LookFriendWxIdList");
		if (stringArrayListExtra != null && stringArrayListExtra.size() > 0) {
			for (Object o : stringArrayListExtra) {
				String str = (String) o;
				XLog.d("setWhoSeeSns str" + str);
				stringBuffer.append(str).append(",");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		}
		XLog.d("setWhoSeeSns" + stringBuffer.toString());
		new PengyouquanRangeManger(i, "", stringBuffer.toString()).addRange(activity);
	}
	
}
