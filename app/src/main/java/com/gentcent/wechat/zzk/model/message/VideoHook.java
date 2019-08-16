package com.gentcent.wechat.zzk.model.message;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.background.MessageConvert;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedBridge;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class VideoHook {
	static Set a;
	static long[] b = new long[2];
	static String[] c = {""};
	static int d = 0;
	static long e = 0;
	
	public static void a(LoadPackageParam loadPackageParam) {
		try {
			c(loadPackageParam);
			d(loadPackageParam);
			b(loadPackageParam);
		} catch (Throwable th) {
			XposedBridge.log("videoHook e: " + Log.getStackTraceString(th));
		}
	}
	
	private static void b(LoadPackageParam loadPackageParam) throws Throwable {
		XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.viewitems.c", loadPackageParam.classLoader, "a", loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.chatting.viewitems.c$a"), Integer.TYPE, loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.chatting.d.a"), loadPackageParam.classLoader.loadClass("com.tencent.mm.storage.bi"), new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) {
				if (XposedHelpers.getIntField(methodHookParam.args[3], "field_type") == 3) {
					FrameLayout frameLayout = (FrameLayout) XposedHelpers.getObjectField(methodHookParam.args[0], "jwK");
					int childCount = frameLayout.getChildCount();
					for (int i = 0; i < childCount; i++) {
						View childAt = frameLayout.getChildAt(i);
						if (childAt instanceof LinearLayout) {
							childAt.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		});
	}
	
	/* access modifiers changed from: private */
	public static boolean c(String str) {
		if (a == null) {
			a = new HashSet();
		}
		boolean contains = a.contains(str);
		if (!contains) {
			a.add(str);
		}
		if (a.size() > 10) {
			a.clear();
		}
		return contains;
	}
	
	private static void c(final LoadPackageParam loadPackageParam) {
		XposedHelpers.findAndHookMethod("com.tencent.mm.modelvideo.g", loadPackageParam.classLoader, "aja", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				String fileName = (String) XposedHelpers.getObjectField(methodHookParam.thisObject, "fileName");
				Object objectField = XposedHelpers.getObjectField(methodHookParam.thisObject, "fFO");
				final String talker = (String) XposedHelpers.callMethod(objectField, "getUser", new Object[0]);
				String filePath = (String) XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.modelvideo.t"), "tF", new Object[]{fileName});
				if (!VideoHook.c(filePath)) {
					XLog.d("VideoHook enter uploadSendVideo talker: " + talker + ",fileName:" + filePath);
					final File file = new File(filePath);
					XLog.d("VideoHook videoFile exists is " + file.exists());
					int intField = XposedHelpers.getIntField(objectField, "fHJ");
//					SendManager.a(intField + "", filePath);
//					SendManager.a(new Runnable() {
//						public void run() {
//							if (!file.exists()) {
//								return;
//							}
//							if (talker.endsWith("@chatroom")) {
//								QNUploadUtil.a("", -1, talker, file, "", true);
//							} else {
//								QNUploadUtil.a(talker, file);
//							}
//						}
//					}, 43, (long) intField, -1);
				}
			}
		});
	}
	
	/* access modifiers changed from: private */
	public static boolean d(String str) {
		try {
			boolean z = !c[0].equals(str);
			c[0] = str;
			System.arraycopy(b, 1, b, 0, b.length - 1);
			b[b.length - 1] = System.currentTimeMillis();
			if (500 <= b[b.length - 1] - b[b.length - 2]) {
				return z;
			}
			return false;
		} catch (Throwable th) {
			XLog.d("continuousClick error is " + Log.getStackTraceString(th));
			return true;
		}
	}
	
	private static void d(LoadPackageParam loadPackageParam) throws Throwable {
		XposedHelpers.findAndHookMethod("com.tencent.mars.cdn.CdnLogic", loadPackageParam.classLoader, "startC2CDownload", loadPackageParam.classLoader.loadClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest"), new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) {
				XLog.d("VideoHook" + " enter uploadReceiverVideo startC2CDownload method");
				final Object obj = methodHookParam.args[0];
				XLog.d("VideoHook" + "get enter uploadReceiverVideo startC2CDownload method");
				int intField = XposedHelpers.getIntField(obj, "fileType");
				XLog.d("VideoHook fileType3333:" + intField);
				if (intField == 3) {
					new Thread(new Runnable() {
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							String aeskey = (String) XposedHelpers.getObjectField(obj, "aeskey");
							String fileKey = (String) XposedHelpers.getObjectField(obj, "fileKey");
							String fileid = (String) XposedHelpers.getObjectField(obj, "fileid");
							String savePath = (String) XposedHelpers.getObjectField(obj, "savePath");
							XLog.d("VideoHook 判断视频" + savePath.contains("video"));
							if (savePath.contains("video")) {
								if (!VideoHook.d(savePath)) {
									XLog.d("VideoHook" + "videohook is not allown");
									return;
								}
								String[] split = savePath.split("/");
								XLog.d("VideoHook a_savePath:" + savePath);
								String str5 = split[split.length - 1];
								XLog.d("VideoHook video:" + str5);
								Cursor a2 = WcdbHolder.excute(videoSql(str5.split("\\.")[0]));
								while (a2.moveToNext()) {
									String string = a2.getString(a2.getColumnIndex("user"));
									XLog.d("VideoHook userid:" + string);
								}
								a2.close();
								VideoHook.b(aeskey, fileKey, fileid, savePath);
							}
						}
					}).start();
				}
			}
		});
	}
	
	public static synchronized void b(String str, String str2, String str3, String str4) {
		synchronized (VideoHook.class) {
			String[] split = str2.split("_");
			String[] split2 = str4.split("/");
			String[] split3 = split2[split2.length - 1].split("\\.");
			Class findClass = XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", MainManager.wxLpparam.classLoader);
			Class findClass2 = XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic", MainManager.wxLpparam.classLoader);
			Object newInstance = XposedHelpers.newInstance(findClass);
			XposedHelpers.setObjectField(newInstance, "aeskey", str);
			XposedHelpers.setObjectField(newInstance, "fileid", str3);
			XposedHelpers.setObjectField(newInstance, "fileKey", "adownvideo_" + split[1] + "_" + split[2] + "_" + split3[0]);
			XposedHelpers.setObjectField(newInstance, "savePath", str4.replace("jpg", "mp4").replace(".tmp", ""));
			XposedHelpers.setObjectField(newInstance, "queueTimeoutSeconds", 0);
			XposedHelpers.setObjectField(newInstance, "transforTimeoutSeconds", 0);
			Cursor a2 = WcdbHolder.excute(videoSql(split3[0]));
			String totallen = "";
			String user = "";
			String reserved4 = "";
			String replace = str4.replace("jpg", "mp4").replace(".tmp", "");
			String md5 = null;
			while (a2.moveToNext()) {
				totallen = a2.getString(a2.getColumnIndex("totallen"));
				user = a2.getString(a2.getColumnIndex("user"));
				reserved4 = a2.getString(a2.getColumnIndex("reserved4"));
				md5 = ZzkUtil.a(reserved4, "md5");
			}
			a2.close();
			if (ObjectUtils.isNotEmpty(totallen)) {
				XposedHelpers.setObjectField(newInstance, "fileSize", Integer.valueOf(totallen));
			}
			XposedHelpers.setObjectField(newInstance, "fileType", 4);
			XposedHelpers.callStaticMethod(findClass2, "startC2CDownload", newInstance);
			String myWxid = UserDao.getMyWxid();
			if (myWxid.equals("")) {
				Cursor a3 = WcdbHolder.excute("select value from userinfo where id='2'");
				while (a3.moveToNext()) {
					myWxid = a3.getString(a3.getColumnIndex("value"));
				}
				a3.close();
			}
			d = 0;
			e = 0;
			XLog.d("VideoHook start reserved4 is " + reserved4);
			if (!TextUtils.isEmpty(reserved4)) {
				if (reserved4.startsWith("<?xml")) {
					reserved4 = MessageResolver.a(reserved4);
				} else if (reserved4.contains(":")) {
					reserved4 = reserved4.split(":")[0];
				}
			}
			XLog.d("VideoHook end reserved4 is " + reserved4);
			a(myWxid, user, reserved4, replace, md5);
		}
	}
	
	private static synchronized void a(String recWXID, String sendWXID, String friendId, String str4, String md5) {
		synchronized (VideoHook.class) {
			XLog.d("VideoHook recWXID : " + recWXID + "  sendWXID : " + sendWXID + "  friendId : " + friendId + ",md5:" + md5);
			try {
				d++;
				if (d < 120) {
					long currentTimeMillis = System.currentTimeMillis();
					Thread.sleep(1000);
					float currentTimeMillis2 = (float) (System.currentTimeMillis() - currentTimeMillis);
					XLog.d("VideoHook secounds wxid ::" + currentTimeMillis2);
					File file = new File(str4);
					if (file.exists()) {
						long length = file.length();
						if (e == length) {
							XLog.d("VideoHook file is :" + file.getAbsolutePath());
							boolean endsWith = sendWXID.endsWith("@chatroom");
							boolean isSend = TextUtils.equals(friendId, UserDao.getMyWxid());
							XLog.d("VideoHook uploadVideo isSend " + isSend + "  friendId is " + friendId+ "  sendWXID is " + sendWXID);
							if (!isSend) { //接收
								if (endsWith) { //群聊
								
								} else { //私聊
									MessageBean messageBean = new MessageBean();
									messageBean.setIsSend(0);
									messageBean.setMyWxId(UserDao.getMyWxid());
									messageBean.setFriendWxId(friendId);
									UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
									uploadBean = MessageConvert.a(uploadBean, friendId);
									UploadService.uploadFileToBack(file, uploadBean, 43);
								}
							}
						} else {
							Thread.sleep(3000);
							e = length;
							a(recWXID, sendWXID, friendId, str4, md5);
						}
					} else {
						a(recWXID, sendWXID, friendId, str4, md5);
					}
				} else {
					return;
				}
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}
		return;
	}
	
	
	/**
	 * 生成根据文件名查询SQL
	 *
	 * @param filename 文件名
	 * @return 查询SQL
	 */
	public static String videoSql(String filename) {
		return "select totallen,user,reserved4 from videoinfo2 where filename='" + filename + "'";
	}
}
