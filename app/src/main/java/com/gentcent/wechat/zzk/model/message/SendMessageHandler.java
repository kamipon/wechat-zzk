package com.gentcent.wechat.zzk.model.message;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.gentcent.wechat.zzk.model.message.bean.SendMessageBean;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
				class1 = findClass(HookParams.modelmulti_h, classLoader);
			} catch (Throwable th) {
				XLog.d("send text init error is " + Log.getStackTraceString(th));
			}
		}
	}
	
	/**
	 * 发送纯文本消息
	 */
	public static void sendText(SendMessageBean sendMsgBean) {
		try {
			final String serviceGuid = sendMsgBean.getServiceGuid();
			final String friendWxId = sendMsgBean.getFriendWxId();
			final String Content = sendMsgBean.getContent();
			final int type = 1;
			XLog.d("发送文本消息");
			ClassLoader clsLoader = MainManager.wxLpparam.classLoader;
			init(clsLoader);
			final Class findClass = findClass(HookParams.ah_p, clsLoader);
			XLog.d("netscenequeueclass:" + findClass);
			if (findClass != null) {
				ThreadPoolUtils.getInstance().run(new Runnable() {
					@Override
					public void run() {
						final Object staticField = getStaticObjectField(findClass, HookParams.ah_p_attribute);
						if (staticField != null) {
							final String[] split = friendWxId.split("\\|");
							for (int i = 0; i < split.length; i++) {
								final int finalI = i;
								SendMessageManager.run(serviceGuid, new Runnable() {
									public void run() {
										Object callMethod = callMethod(staticField, HookParams.ah_p_method, newInstance(class1, split[finalI], Content, type));
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
	 * 发送视频
	 */
	public static void sendVideo(String serviceGuid, String path, String username) {
		try {
			Intent intent = new Intent();
			intent.setData(Uri.parse("file://" + path));
			if (MainManager.activity != null) {
				Class<?> model_j = MainManager.wxLpparam.classLoader.loadClass(HookParams.model_j);
				XLog.d("model_j:" + model_j);
				Class<?> model_j_a = MainManager.wxLpparam.classLoader.loadClass(HookParams.model_j_a);
				XLog.d("model_j_a:" + model_j_a);
				final Thread thread = (Thread) XposedHelpers.newInstance(model_j, new Class[]{Context.class, List.class, Intent.class, String.class, Integer.TYPE, model_j_a}, new Object[]{MainManager.activity, null, intent, username, 1, null});
				SendMessageManager.run(serviceGuid, new Runnable() {
					public void run() {
						thread.start();
					}
				}, 43, username, path);
				XLog.d("sendSingleVideo success send info thread start");
				return;
			}
			XLog.e("sendVideo MainManager.activity==null");
		} catch (Throwable th) {
			XLog.e("sendVideo e:" + Log.getStackTraceString(th));
		}
	}
	
	/**
	 * 发送链接
	 */
	public static void sendArticle(SendMessageBean sm, Bitmap bitmap, String username) {
		final LoadPackageParam lpparam = MainManager.wxLpparam;
		String serviceGuid = sm.getServiceGuid();
		String articleUrl = sm.getLinkUrl();
		String articleUrl2 = sm.getLinkUrl();
		String description = sm.getLinkDescription();
		String articleTitle = sm.getLinkTitle();
		try {
			XLog.d("bitmap getByteCount is " + bitmap.getByteCount());
			final Object newInstance = XposedHelpers.newInstance(lpparam.classLoader.loadClass(HookParams.a_pr));
			XLog.d("SendArtLinkToUserUtil sendArticl 1 oo is " + newInstance);
			Object objectField = XposedHelpers.getObjectField(newInstance, HookParams.a_pr_field);
			XposedHelpers.setObjectField(objectField, HookParams.a_pr_field_attribute1, "");
			XposedHelpers.setObjectField(objectField, HookParams.a_pr_field_attribute2, "");
			XLog.d("SendArtLinkToUserUtil sendArticl 2 ");
			XposedHelpers.setObjectField(objectField, HookParams.a_pr_field_attribute3, 2);
			XposedHelpers.setObjectField(objectField, HookParams.a_pr_field_attribute4, articleUrl);
			XposedHelpers.setObjectField(objectField, HookParams.a_pr_field_attribute5, articleUrl2);
			XposedHelpers.setObjectField(objectField, HookParams.a_pr_field_attribute6, username);
			XLog.d("SendArtLinkToUserUtil sendArticl 3 ");
			Object newInstance2 = XposedHelpers.newInstance(lpparam.classLoader.loadClass(HookParams.WXMediaMessage));
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute1, description);
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute2, null);
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute3, null);
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute4, null);
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute5, 0);
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute6, articleTitle);
			if (bitmap != null) {
				XposedHelpers.callMethod(newInstance2, HookParams.WXMediaMessage_method1, bitmap);
			}
			Object[] objArr = {articleUrl2};
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_method1_return_filed1, XposedHelpers.newInstance(lpparam.classLoader.loadClass(HookParams.WXWebpageObject), objArr));
			XLog.d("SendArtLinkToUserUtil sendArticl 4 ");
			XposedHelpers.setObjectField(objectField, HookParams.WXMediaMessage_method1_return_filed2, newInstance2);
			XLog.d("SendArtLinkToUserUtil sendArticl 5 serviceGuid is " + serviceGuid);
			SendMessageManager.run(serviceGuid, new Runnable() {
				public void run() {
					try {
						XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(lpparam.classLoader.loadClass(HookParams.sdk_b_a), HookParams.sdk_b_a_filed), "m", newInstance);
					} catch (Exception e) {
						XLog.e("SendArtLinkToUserUtil sendArticl e: " + Log.getStackTraceString(e));
					}
				}
			}, 5, username, articleUrl);
		} catch (Throwable th) {
			XLog.e("SendArtLinkToUserUtil sendArticl e: " + Log.getStackTraceString(th));
		}
	}
	
	/**
	 * 发送文件
	 */
	public static void sendFile(String serviceGuid, String path, String fileName, final String username) {
		final LoadPackageParam lpparam = MainManager.wxLpparam;
		try {
			XLog.d("sendfile_to_user fileName is " + fileName + " path " + path + " username " + username);
			Object newInstance = XposedHelpers.newInstance(lpparam.classLoader.loadClass(HookParams.WXFileObject));
			XposedHelpers.callMethod(newInstance, HookParams.WXFileObject_method1, path);
			final Object newInstance2 = XposedHelpers.newInstance(lpparam.classLoader.loadClass(HookParams.WXMediaMessage));
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_method1_return_filed1, newInstance);
			File file = new File(path);
			if (TextUtils.isEmpty(fileName)) {
				XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute6, file.getName());
			} else {
				XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute6, fileName);
			}
			XposedHelpers.setObjectField(newInstance2, HookParams.WXMediaMessage_attribute1, XposedHelpers.callStaticMethod(lpparam.classLoader.loadClass(HookParams.platformtools_bp), HookParams.platformtools_bp_method, file.length()));
			SendMessageManager.run(serviceGuid, new Runnable() {
				public void run() {
					try {
						XposedHelpers.callStaticMethod(lpparam.classLoader.loadClass(HookParams.app_l), HookParams.method_a, newInstance2, "", "", username, 4, null);
					} catch (Exception e) {
						XLog.d(" FileManger  e " + Log.getStackTraceString(e));
					}
				}
			}, 49, username, fileName);
			XLog.d("sendfile_to_user fileName is success aha ~");
		} catch (Throwable th) {
			XLog.d(" FileManger  e " + Log.getStackTraceString(th));
		}
	}
	
	/**
	 * 群聊@好友
	 */
	public static void sendAppointText(SendMessageBean sm) {
		String serviceGuid = sm.getServiceGuid();
		final String friendWxId = sm.getFriendWxId();
		final String content = sm.getContent();
		final String chatroomMemberWxId = sm.getChatroomMemberWxId();
		if (!friendWxId.endsWith("@chatroom") || chatroomMemberWxId == null || chatroomMemberWxId.length() <= 0) {
			XLog.d("SendMessage  sendTest2 参数不对！");
			return;
		}
		XLog.d("sendText2 groupId is " + friendWxId + " aites is " + chatroomMemberWxId + " message is " + content);
		try {
			SendMessageManager.run(serviceGuid, new Runnable() {
				public void run() {
					try {
						Map<String, String> map = new HashMap<>();
						map.put("atuserlist", "<![CDATA[" + chatroomMemberWxId + "]]>");
						Object newInstance = XposedHelpers.newInstance(MainManager.wxLpparam.classLoader.loadClass(HookParams.modelmulti_h), friendWxId, content, 1, 291, map);
						XposedHelpers.callMethod(XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass(HookParams.model_av), HookParams.model_av_method1), HookParams.method_a, newInstance, 0);
						XLog.d("callMethod(OCB is success");
					} catch (Exception ignored) {
					}
				}
			}, 1, friendWxId, content);
			XLog.d("sendText2 success aha ~");
		} catch (Throwable th) {
			XLog.d("SendMessage  sendTest2 msgid:" + Log.getStackTraceString(th));
		}
	}
	
	/**
	 * 发送语音调用过程
	 */
	private static void sendVoiceProcessor(String serviceGuid, String path, String username) {
		Class modelvoice_f = XposedHelpers.findClass(HookParams.modelvoice_f, MainManager.wxLpparam.classLoader);
		XLog.d("modelvoice_f:" + modelvoice_f);
		final Class modelvoice_q = XposedHelpers.findClass(HookParams.modelvoice_q, MainManager.wxLpparam.classLoader);
		XLog.d("modelvoice_q:" + modelvoice_q);
		String str = (String) XposedHelpers.callStaticMethod(modelvoice_q, HookParams.modelvoice_q_method1, username);
		XLog.d("str:" + str);
		String path2 = (String) XposedHelpers.callStaticMethod(modelvoice_q, HookParams.modelvoice_q_method2, str, Boolean.FALSE);
		XLog.d("path2:" + path2);
		MyHelper.copyFile(path, path2);
		long length = new File(path).length();
		XLog.d("length:" + length);
		final Object[] objArr = {str, (int) length, 0};
		SendMessageManager.run(serviceGuid, new Runnable() {
			public void run() {
				boolean booleanValue = (Boolean) XposedHelpers.callStaticMethod(modelvoice_q, HookParams.modelvoice_q_method3, objArr);
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
		Object nNew = XposedHelpers.getStaticObjectField(XposedHelpers.findClass(HookParams.ah_p, MainManager.wxLpparam.classLoader), HookParams.ah_p_attribute);
		XLog.d("nNew:" + nNew);
		XposedHelpers.callMethod(nNew, HookParams.ah_p_method, localObject);
		XLog.d("发送语音成功");
	}
	
	/**
	 * 发送图片(gif)
	 */
	public static void sendGif(String serviceGuid, final String username, String path) {
		final LoadPackageParam lpparam = MainManager.wxLpparam;
		try {
			XLog.d("GifHandle sendGif serviceGuid is " + serviceGuid);
			if (MainManager.activity != null) {
				Object newInstance = newInstance(lpparam.classLoader.loadClass(HookParams.WXEmojiObject), path);
				Object newInstance2 = newInstance(lpparam.classLoader.loadClass(HookParams.WXMediaMessage), newInstance);
				Object callMethod = callMethod(callStaticMethod(lpparam.classLoader.loadClass(HookParams.kernel_g), HookParams.kernel_g_method1, lpparam.classLoader.loadClass(HookParams.emoji_b_d)), HookParams.emoji_b_d_method1);
				final Object callMethod2 = callMethod(callMethod, HookParams.emoji_b_d_method1_return_method, (String) callMethod(callMethod, HookParams.method_a, MainManager.activity, newInstance2, ""));
				final Object newInstance3 = newInstance(lpparam.classLoader.loadClass(HookParams.WXMediaMessage));
				callStaticMethod(lpparam.classLoader.loadClass(HookParams.model_av), HookParams.model_av_method2);
				String result1 = (String) callStaticMethod(lpparam.classLoader.loadClass(HookParams.model_c), HookParams.model_c_method);
				final String result2 = (String) callMethod(callMethod2, HookParams.emoji_b_d_method1_return_method_return_method);
				String sb3 = result1 + result2;
				String sb5 = result1 + result2 + "_thumb";
				if ((Boolean) callStaticMethod(lpparam.classLoader.loadClass(HookParams.vfs_e), HookParams.vfs_e_method1, sb5)) {
					setObjectField(newInstance3, HookParams.WXMediaMessage_file, callStaticMethod(lpparam.classLoader.loadClass(HookParams.vfs_e), HookParams.vfs_e_method2, sb5, 0, (int) ((Long) callStaticMethod(lpparam.classLoader.loadClass(HookParams.vfs_e), HookParams.vfs_e_method3, sb5)).longValue()));
				} else {
					Object callStaticMethod = callStaticMethod(lpparam.classLoader.loadClass(HookParams.vfs_e), HookParams.vfs_e_method4, sb3);
					setObjectField(newInstance3, HookParams.WXMediaMessage_method1, callStaticMethod(lpparam.classLoader.loadClass(HookParams.BackwardSupportUtil_b), HookParams.method_b, callStaticMethod, 1.0f));
					callStaticMethod(lpparam.classLoader.loadClass(HookParams.platformtools_bp), HookParams.method_b, callStaticMethod);
				}
				setObjectField(newInstance3, HookParams.WXMediaMessage_method1_return_filed1, newInstance(lpparam.classLoader.loadClass(HookParams.WXEmojiObject), sb3));
				SendMessageManager.run(serviceGuid, new Runnable() {
					@Override
					public void run() {
						try {
							callStaticMethod(lpparam.classLoader.loadClass(HookParams.app_l), HookParams.method_a, newInstance3, getObjectField(callMethod2, HookParams.emoji_b_d_method1_return_method_return_method_return_file), "", username, 1, result2);
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
			Class findClass = XposedHelpers.findClass(HookParams.as_n, MainManager.wxLpparam.classLoader);
			XLog.d("m:" + findClass);
			Object obj = XposedHelpers.callStaticMethod(findClass, HookParams.as_n_method);
			if (obj == null) {
				obj = XposedHelpers.newInstance(findClass);
			}
			XLog.d("newM:" + obj);
			if (obj != null) {
				XposedHelpers.callMethod(obj, HookParams.method_a, 0, 0, path, username, true, 2130837934);
				final ArrayList<String> arrayList = new ArrayList<>();
				arrayList.add(path);
				final Object finalObj = obj;
				SendMessageManager.run(serviceGuid, new Runnable() {
					public void run() {
						XposedHelpers.callMethod(finalObj, HookParams.method_a, arrayList, true, 0, 0, username, 2130837934);
					}
				}, 3, username, path);
				ConcurrentHashMap cQk1 = (ConcurrentHashMap) XposedHelpers.getObjectField(obj, HookParams.as_n_ConcurrentHashMap);
				XLog.d("cQk1:" + cQk1);
				for (Object object : cQk1.values()) {
					try {
						XLog.d("object:" + object);
						String cQA = (String) getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute1);
						XLog.d("cQA:" + cQA);
						int cNS = (Integer) getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute2);
						XLog.d("cNS:" + cNS);
						int bcs = (Integer) getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute3);
						XLog.d("bcs:" + bcs);
						int bhG = (Integer) getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute4);
						XLog.d("bhG:" + bhG);
						Class pstring = findClass(HookParams.PString, MainManager.wxLpparam.classLoader);
						XLog.d("pstring:" + pstring);
						Object pstringNew = newInstance(pstring, new Object[intparam]);
						XLog.d("pstringNew:" + pstringNew);
						Object pInt1New = newInstance(findClass(HookParams.PInt, MainManager.wxLpparam.classLoader), new Object[intparam]);
						XLog.d("pInt1New:" + pInt1New);
						Object pInt2New = newInstance(findClass(HookParams.PInt, MainManager.wxLpparam.classLoader), new Object[intparam]);
						XLog.d("pInt2New:" + pInt2New);
						String cQB = (String) getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute5);
						String cQC = (String) getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute6);
						XLog.d("cQB:" + cQB);
						XLog.d("cQC:" + cQC);
						long longValue = (Long) getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute7);
						Object objectField = getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute8);
						Object objectField2 = getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute9);
						Object objectField3 = getObjectField(object, HookParams.as_n_ConcurrentHashMap_attribute10);
						Object f = callStaticMethod(findClass(HookParams.as_o, MainManager.wxLpparam.classLoader), HookParams.as_o_method);
						XLog.d("sendSingleImg f is " + f);
						XposedHelpers.callMethod(f, HookParams.method_a, cQA, cNS, bcs, bhG, pstringNew, pInt1New, pInt2New, cQB, cQC, longValue, objectField, objectField2, objectField3);
						Class h = findClass(HookParams.as_i, MainManager.wxLpparam.classLoader);
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
						callMethod(newh, HookParams.method_a, arrayList2, myWechatID, username, arrayList3, 0, true, 2130837934);
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
