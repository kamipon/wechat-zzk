package com.gentcent.wechat.zzk.model.wallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.model.wallet.bean.PayInfo;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XC_MethodReplacement;
import com.gentcent.zzk.xped.XposedBridge;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * 转账
 */
public class Remittance {
	public static boolean a = false;
	public static Activity b;
	
	public static void a() {
		Activity activity = b;
		if (activity != null) {
			activity.finish();
		}
	}
	
	/**
	 * 开始转账
	 */
	public static void a(LoadPackageParam loadPackageParam, PayInfo payInfo) {
		try {
			XLog.d("MYRemittance " + " MYRemittance 3");
			if (MainManager.activity != null) {
				a = true;
				Intent intent = new Intent(MainManager.activity, loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.remittance.ui.RemittanceAdapterUI"));
				intent.putExtra("scene", 2);
				intent.putExtra("receiver_name", payInfo.receiver_name);
				intent.putExtra("shenshou", true);
				intent.putExtra(PayInfo.Intent_Tag, GsonUtils.GsonString(payInfo));
				MainManager.activity.startActivity(intent);
				XLog.d("MYRemittance " + " MYRemittance 4");
			}
		} catch (Throwable th) {
			StringBuilder sb = new StringBuilder();
			sb.append("MYRemittance OpenRemittanceUI e: ");
			sb.append(th.getMessage());
			XLog.d(sb.toString());
			th.printStackTrace();
		}
	}
	
	public static void a(final LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceAdapterUI", loadPackageParam.classLoader, "c", String.class, Integer.TYPE, Intent.class, new XC_MethodReplacement() {
				public Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					Intent intent;
					Activity activity = (Activity) methodHookParam.thisObject;
					boolean booleanExtra = activity.getIntent().getBooleanExtra("shenshou", false);
					boolean booleanExtra2 = activity.getIntent().getBooleanExtra("shenshou1", false);
					if (booleanExtra && !booleanExtra2) {
						activity.getIntent().putExtra("shenshou1", true);
						String stringExtra = activity.getIntent().getStringExtra(PayInfo.Intent_Tag);
						Intent intent2 = (Intent) methodHookParam.args[2];
						if (intent2 != null) {
							intent = new Intent(intent2);
						} else {
							intent = new Intent();
						}
						XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.wallet.a"), "a", XposedHelpers.getObjectField(activity, "qVn"), intent);
						if (intent.getIntExtra("busi_type", 0) != 1) {
							intent.setClass(activity, loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.remittance.ui.RemittanceUI"));
							intent.putExtra("receiver_name", (String) methodHookParam.args[0]);
							intent.putExtra("scene", XposedHelpers.getIntField(activity, "mScene"));
							intent.putExtra("pay_scene", ((Integer) methodHookParam.args[1]).intValue());
							intent.putExtra("pay_channel", XposedHelpers.getIntField(activity, "mChannel"));
							intent.putExtra("shenshou", true);
							intent.putExtra(PayInfo.Intent_Tag, stringExtra);
							activity.startActivity(intent);
							activity.setResult(-1);
							activity.finish();
							return null;
						}
					}
					return XposedBridge.invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject, methodHookParam.args);
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceUI", loadPackageParam.classLoader, "a", String.class, Integer.TYPE, String.class, String.class, String.class, String.class, String.class, loadPackageParam.classLoader.loadClass("com.tencent.mm.g.a.fy"), new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					String sb = "  send info :" +
							methodHookParam.args[0] +
							"," +
							methodHookParam.args[1] +
							"," +
							methodHookParam.args[2] +
							"," +
							methodHookParam.args[3] +
							"," +
							methodHookParam.args[4] +
							"," +
							methodHookParam.args[5] +
							"," +
							methodHookParam.args[6];
					XLog.d("MYRemittance " + sb);
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceUI", loadPackageParam.classLoader, "onSceneEnd", Integer.TYPE, Integer.TYPE, String.class, loadPackageParam.classLoader.loadClass("com.tencent.mm.ak.m"), new XC_MethodHook() {
				@SuppressLint("SetTextI18n")
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					int intValue = (Integer) methodHookParam.args[0];
					int intValue2 = (Integer) methodHookParam.args[1];
					if (intValue == 0 && intValue2 == 0) {
						final Activity activity = (Activity) methodHookParam.thisObject;
						if (activity.getIntent().getBooleanExtra("shenshou", false)) {
							activity.getIntent().putExtra("shenshou", false);
							PayInfo payInfo = GsonUtils.GsonToBean(activity.getIntent().getStringExtra(PayInfo.Intent_Tag), PayInfo.class);
							if (payInfo.money >= 0.0d) {
								double d = payInfo.money;
								WalletPayUI.a = true;
								WalletPayUI.c = false;
								WalletPayUI.b = payInfo;
								Remittance.b = activity;
								if (payInfo.Remarks != null && !payInfo.Remarks.equals("")) {
									XposedHelpers.setObjectField(activity, "mDesc", payInfo.Remarks);
								}
								XLog.d("MYRemittance  onSceneEnd 1：" + WalletPayUI.b);
								Object objectField = XposedHelpers.getObjectField(activity, "ltQ");
								if (objectField != null) {
									EditText editText = (EditText) XposedHelpers.getObjectField(objectField, "BIX");
									if (editText != null) {
										editText.setText("" + payInfo.money);
										XLog.d("MYRemittance  onSceneEnd 2");
										editText.postDelayed(new Runnable() {
											public void run() {
												try {
													((View.OnClickListener) XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.remittance.ui.RemittanceNewBaseUI$12"), new Object[]{activity})).onClick(null);
												} catch (ClassNotFoundException e) {
													XLog.d("MYRemittance onSceneEnd e:" + Log.getStackTraceString(e));
												}
											}
										}, 1000);
										XLog.d("MYRemittance  onSceneEnd 3");
									}
								}
							}
						}
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceNewBaseUI", loadPackageParam.classLoader, "a", loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.remittance.model.v"), new XC_MethodHook() {
				public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					if (Remittance.a) {
						boolean booleanField = XposedHelpers.getBooleanField(methodHookParam.thisObject, "qWi");
						boolean booleanField2 = XposedHelpers.getBooleanField(methodHookParam.thisObject, "qWj");
						boolean booleanField3 = XposedHelpers.getBooleanField(methodHookParam.thisObject, "qWk");
						XposedHelpers.setBooleanField(methodHookParam.thisObject, "qWi", true);
						XposedHelpers.setBooleanField(methodHookParam.thisObject, "qWj", true);
						XposedHelpers.setBooleanField(methodHookParam.thisObject, "qWk", true);
						XLog.d("MYRemittance forward mUR:" + booleanField + ",mUS:" + booleanField2 + ",mUT:" + booleanField3);
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceResultNewUI", loadPackageParam.classLoader, "initView", new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (Remittance.a) {
						Remittance.a = false;
						final Activity activity = (Activity) methodHookParam.thisObject;
						new Handler().postDelayed(new Runnable() {
							public void run() {
								((Button) XposedHelpers.getObjectField(activity, "rbA")).callOnClick();
							}
						}, 1000);
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceResultNewUI", loadPackageParam.classLoader, "onDestroy", new XC_MethodHook() {
				public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					if (Remittance.b != null) {
						Remittance.b = null;
					}
				}
			});
		} catch (Throwable th) {
			XLog.d("MYRemittance  hookTargetClass e:" + Log.getStackTraceString(th));
		}
	}
}
