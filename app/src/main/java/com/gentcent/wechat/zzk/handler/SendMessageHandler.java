package com.gentcent.wechat.zzk.handler;

import android.database.Cursor;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.manager.SendMessageManager;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static com.gentcent.zzk.xped.XposedHelpers.callMethod;
import static com.gentcent.zzk.xped.XposedHelpers.callStaticMethod;
import static com.gentcent.zzk.xped.XposedHelpers.findClass;
import static com.gentcent.zzk.xped.XposedHelpers.getObjectField;
import static com.gentcent.zzk.xped.XposedHelpers.getStaticObjectField;
import static com.gentcent.zzk.xped.XposedHelpers.newInstance;
import static com.gentcent.zzk.xped.XposedHelpers.setObjectField;

/**
 * 发送消息
 *
 * @author zuozhi
 * @since 2019-07-08
 */
public class SendMessageHandler {
	private static Class<?> class1;
	
	private static void init(ClassLoader classLoader) {
		if (class1 == null) {
			try {
				class1 = findClass(HookParams.sendMessageClass1, classLoader);
			} catch (Throwable th) {
				XLog.d("send text init error is " + Log.getStackTraceString(th));
			}
		}
	}
	
	/**
	 * 发送纯文本消息
	 */
	public static void sendText(final String serviceGuid, final String friendWxId, final String Content, final int type) {
		try {
			XLog.d("发送文本消息");
			ClassLoader clsLoader = MainManager.wxLpparam.classLoader;
			init(clsLoader);
			final Class findClass = findClass(HookParams.sendMessageClass2, clsLoader);
			XLog.d("netscenequeueclass:" + findClass);
			if (findClass != null) {
				ThreadPoolUtils.getInstance().run(new Runnable() {
					@Override
					public void run() {
						final Object staticField = getStaticObjectField(findClass, HookParams.sendMessageStaticField);
						if (staticField != null) {
							final String[] split = friendWxId.split("\\|");
							for (int i = 0; i < split.length; i++) {
								final int finalI = i;
								SendMessageManager.a(serviceGuid, new Runnable() {
									public void run() {
										Object callMethod = callMethod(staticField, HookParams.sendMessageMethodName, newInstance(class1, split[finalI], Content, type));
										XLog.d("sendText 发送消息成功 : " + callMethod.toString() + "  wxid " + split[finalI] + "  sleep time is 1000");
									}
								}, 1, friendWxId, Content);
								if (i > 0) {
									try {
										Thread.sleep((long) 1000);
									} catch (InterruptedException e2) {
										XLog.d("send text sleep Exception is " + Log.getStackTraceString(e2));
									}
								}
							}
						}
					}
				});
			}
		} catch (Exception e) {
			XLog.d("send text Exception is " + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 发送图片
	 */
	public static void sendImg(String serviceGuid, String username, String path) {
		if (FileUtils.isFileExists(path)) {
			sendImgProcessor(serviceGuid, path, username);
			XLog.d("sendImg 发送消息成功:" + username + "  sleep time is " + 1000);
			try {
				Thread.sleep((long) 1000);
			} catch (InterruptedException e) {
				XLog.d("sendImg sleep Exception is " + Log.getStackTraceString(e));
			}
		} else {
			XLog.d("找不到图片路径");
		}
	}
	
	/**
	 * 发送语音
	 */
	public static void sendVoice(String serviceGuid, String path, String username) {
		String[] split = username.split("\\|");
		for (int i = 0; i < split.length && FileUtils.isFileExists(path); i++) {
			sendVoiceProcessor(serviceGuid, path, split[i]);
			XLog.d("sendVoice 发送消息成功:" + split[i] + "  sleep time is " + 1000);
			if (i > 0) {
				try {
					Thread.sleep((long) 1000);
				} catch (InterruptedException e) {
					XLog.d("sendVoice sleep Exception is " + Log.getStackTraceString(e));
				}
			}
		}
	}
	
	/**
	 * 发送语音调用过程
	 */
	private static void sendVoiceProcessor(String serviceGuid, String path, String username) {
		Class modelvoice_f = XposedHelpers.findClass(HookParams.send_voice_class1, MainManager.wxLpparam.classLoader);
		XLog.d("modelvoice_f:" + modelvoice_f);
		final Class modelvoice_q = XposedHelpers.findClass(HookParams.send_voice_class2, MainManager.wxLpparam.classLoader);
		XLog.d("modelvoice_q:" + modelvoice_q);
		String str = (String) XposedHelpers.callStaticMethod(modelvoice_q, HookParams.send_voice_class2_method1, username);
		XLog.d("str:" + str);
		String path2 = (String) XposedHelpers.callStaticMethod(modelvoice_q, HookParams.send_voice_class2_method2, str, Boolean.FALSE);
		XLog.d("path2:" + path2);
		MyHelper.copyFile(path, path2);
		long length = new File(path).length();
		XLog.d("length:" + length);
		final Object[] objArr = {str, (int) length, 0};
		SendMessageManager.a(serviceGuid, new Runnable() {
			public void run() {
				boolean booleanValue = (Boolean) XposedHelpers.callStaticMethod(modelvoice_q, HookParams.send_voice_class2_method3, objArr);
				XLog.d("boo:" + booleanValue);
			}
		}, 34, username, path);
		Object localObject = null;
		try {
			localObject = modelvoice_f.getDeclaredConstructor(String.class).newInstance(str);
		} catch (Exception e) {
			XLog.e("发送语音 error: " + Log.getStackTraceString(e));
		}
		XLog.d("localObject:" + localObject);
		Object nNew = XposedHelpers.getStaticObjectField(XposedHelpers.findClass(HookParams.send_voice_class3, MainManager.wxLpparam.classLoader), HookParams.send_voice_class3_attribute);
		XLog.d("nNew:" + nNew);
		XposedHelpers.callMethod(nNew, HookParams.send_voice_class3_method, localObject);
		XLog.d("发送语音成功");
	}
	
	/**
	 * 发送图片(gif)
	 */
	public static void sendGif(String serviceGuid, final String username, String path) {
		final XC_LoadPackage.LoadPackageParam lpparam = MainManager.wxLpparam;
		try {
			XLog.d("GifHandle sendGif serviceGuid is " + serviceGuid);
			if (MainManager.activity != null) {
				Object newInstance = newInstance(lpparam.classLoader.loadClass("com.tencent.mm.opensdk.modelmsg.WXEmojiObject"), path);
				Object newInstance2 = newInstance(lpparam.classLoader.loadClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage"), newInstance);
				Object callMethod = callMethod(callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.kernel.g"), "N", lpparam.classLoader.loadClass("com.tencent.mm.plugin.emoji.b.d")), "getEmojiMgr");
				final Object callMethod2 = callMethod(callMethod, "HO", (String) callMethod(callMethod, "a", MainManager.activity, newInstance2, ""));
				final Object newInstance3 = newInstance(lpparam.classLoader.loadClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage"));
				callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.model.av"), "XE");
				String result1 = (String) callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.model.c"), "VX");
				final String result2 = (String) callMethod(callMethod2, "ze");
				String sb3 = result1 + result2;
				String sb5 = result1 + result2 + "_thumb";
				if ((Boolean) callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.vfs.e"), "cl", sb5)) {
					setObjectField(newInstance3, "thumbData", callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.vfs.e"), "e", sb5, 0, (int) ((Long) callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.vfs.e"), "ars", sb5)).longValue()));
				} else {
					Object callStaticMethod = callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.vfs.e"), "openRead", sb3);
					setObjectField(newInstance3, "setThumbImage", callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.sdk.platformtools.BackwardSupportUtil$b"), "b", callStaticMethod, 1.0f));
					callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.sdk.platformtools.bp"), "b", callStaticMethod);
				}
				setObjectField(newInstance3, "mediaObject", newInstance(lpparam.classLoader.loadClass("com.tencent.mm.opensdk.modelmsg.WXEmojiObject"), sb3));
				SendMessageManager.a(serviceGuid, new Runnable() {
					@Override
					public void run() {
						try {
							callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.pluginsdk.model.app.l"), "a", newInstance3, getObjectField(callMethod2, "field_app_id"), "", username, 1, result2);
						} catch (Exception e2) {
							XLog.d("GifHandlesendGif e:" + Log.getStackTraceString(e2));
							e2.printStackTrace();
						}
					}
				}, 99, username, result2);
			}
		} catch (Throwable th) {
			XLog.d("GifHandlesendGif e:" + Log.getStackTraceString(th));
			th.printStackTrace();
		}
	}
	
	/**
	 * 发送图片调用过程
	 */
	private static void sendImgProcessor(String serviceGuid, String path, final String username) {
		try {
			int intparam = 0;
			Class findClass = XposedHelpers.findClass(HookParams.send_img_class1, MainManager.wxLpparam.classLoader);
			XLog.d("m:" + findClass);
			Object obj = XposedHelpers.callStaticMethod(findClass, HookParams.send_img_class1_method);
			if (obj == null) {
				obj = XposedHelpers.newInstance(findClass);
			}
			XLog.d("newM:" + obj);
			if (obj != null) {
				XposedHelpers.callMethod(obj, HookParams.send_img_class2_method1, 0, 0, path, username, true, 2130837934);
				final ArrayList<String> arrayList = new ArrayList<>();
				arrayList.add(path);
				final Object finalObj = obj;
				SendMessageManager.a(serviceGuid, new Runnable() {
					public void run() {
						XposedHelpers.callMethod(finalObj, HookParams.send_img_class2_method2, arrayList, true, 0, 0, username, 2130837934);
					}
				}, 3, username, path);
				ConcurrentHashMap cQk1 = (ConcurrentHashMap) XposedHelpers.getObjectField(obj, HookParams.send_img_class2_ConcurrentHashMap);
				XLog.d("cQk1:" + cQk1);
				for (Object object : cQk1.values()) {
					try {
						XLog.d("object:" + object);
						String cQA = (String) getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute1);
						XLog.d("cQA:" + cQA);
						int cNS = (Integer) getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute2);
						XLog.d("cNS:" + cNS);
						int bcs = (Integer) getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute3);
						XLog.d("bcs:" + bcs);
						int bhG = (Integer) getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute4);
						XLog.d("bhG:" + bhG);
						Class pstring = findClass(HookParams.send_img_class3, MainManager.wxLpparam.classLoader);
						XLog.d("pstring:" + pstring);
						Object pstringNew = newInstance(pstring, new Object[intparam]);
						XLog.d("pstringNew:" + pstringNew);
						Object pInt1New = newInstance(findClass(HookParams.send_img_class4, MainManager.wxLpparam.classLoader), new Object[intparam]);
						XLog.d("pInt1New:" + pInt1New);
						Object pInt2New = newInstance(findClass(HookParams.send_img_class5, MainManager.wxLpparam.classLoader), new Object[intparam]);
						XLog.d("pInt2New:" + pInt2New);
						String cQB = (String) getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute5);
						String cQC = (String) getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute6);
						XLog.d("cQB:" + cQB);
						XLog.d("cQC:" + cQC);
						long longValue = (Long) getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute7);
						Object objectField = getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute8);
						Object objectField2 = getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute9);
						Object objectField3 = getObjectField(object, HookParams.send_img_ConcurrentHashMap_attribute10);
						Object f = callStaticMethod(findClass(HookParams.send_img_class6, MainManager.wxLpparam.classLoader), HookParams.send_img_class6_method);
						XLog.d("sendSingleImg f is " + f);
						XposedHelpers.callMethod(f, HookParams.send_img_class6_return_method, cQA, cNS, bcs, bhG, pstringNew, pInt1New, pInt2New, cQB, cQC, longValue, objectField, objectField2, objectField3);
						Class h = findClass(HookParams.send_img_class7, MainManager.wxLpparam.classLoader);
						XLog.d("h:" + h);
						Object newh = newInstance(h);
						XLog.d("newh:" + newh);
						ArrayList<Integer> arrayList2 = new ArrayList<>();
						Cursor c1 = WcdbHolder.excute("select id from ImgInfo2 order by id desc limit 0,1 ");
						while (c1.moveToNext()) {
							arrayList2.add(Integer.valueOf(c1.getString(c1.getColumnIndex("id"))));
						}
						if (arrayList2.size() == 0) {
							arrayList2.add(2);
						}
						c1.close();
						XLog.d("list_type:" + arrayList2);
						ArrayList<String> arrayList3 = new ArrayList<>();
						arrayList3.add(path);
						String myWechatID = UserDao.getMyWxid();
						String methodName = HookParams.send_img_class7_method;
						callMethod(newh, methodName, arrayList2, myWechatID, username, arrayList3, 0, true, 2130837934);
						XLog.d("发送图片成功");
					} catch (Throwable th2) {
						XLog.e("发送图片内部error:" + Log.getStackTraceString(th2));
					}
				}
			}
		} catch (Throwable th) {
			XLog.e("发送图片error:" + Log.getStackTraceString(th));
		}
	}
	
}
