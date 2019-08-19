package com.gentcent.wechat.zzk.model.message;

import android.text.TextUtils;
import android.util.Log;

import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodReplacement;
import com.gentcent.zzk.xped.XposedBridge;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class FileManager {
	boolean ischartroom;
	private String content = null;
	private String path = null;
	private LoadPackageParam lpparam;
	private long msgId;
	private String g;
	private Object callMethod;
	private Object i = null;
	private int size = 0;
	
	public static class C0378b {
		public String path;
		public int size;
		public int isSend;
		public String talker;
		public String friendId;
		public boolean ischatroom;
		
		public String toString() {
			return "FileResult{path='" +
					this.path +
					'\'' +
					", size=" +
					this.size +
					", isSend=" +
					this.isSend +
					", talker='" +
					this.talker +
					'\'' +
					", friendId='" +
					this.friendId +
					'\'' +
					", ischatroom=" +
					this.ischatroom +
					'}';
		}
	}
	
	public static void a(String str, final LoadPackageParam loadPackageParam, String path, String fileName, final String username) {
		try {
			XLog.d("sendfile_to_user fileName is " + fileName + " path " + path + " username " + username);
			Object newInstance = XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.opensdk.modelmsg.WXFileObject"));
			XposedHelpers.callMethod(newInstance, "setFilePath", path);
			final Object newInstance2 = XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage"));
			XposedHelpers.setObjectField(newInstance2, "mediaObject", newInstance);
			File file = new File(path);
			if (TextUtils.isEmpty(fileName)) {
				XposedHelpers.setObjectField(newInstance2, "title", file.getName());
			} else {
				XposedHelpers.setObjectField(newInstance2, "title", fileName);
			}
			XposedHelpers.setObjectField(newInstance2, "description", XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.sdk.platformtools.bp"), "fx", file.length()));
			
			ThreadPoolUtils.getInstance().run(new Runnable() {
				@Override
				public void run() {
					try {
						XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.pluginsdk.model.app.l"), "a", newInstance2, "", "", username, 4, null);
					} catch (Exception e) {
						XLog.d(" FileManger  friendId " + e.getMessage());
					}
				}
			});
			XLog.d("sendfile_to_user fileName is success aha ~");
		} catch (Throwable th) {
			XLog.d(" FileManger  friendId " + Log.getStackTraceString(th));
		}
	}
	
	public C0378b a(LoadPackageParam lpparam, String msgId) {
		if (msgId == null || msgId.equals("")) {
			return null;
		}
		XLog.d("FileManger" + "receiveFile_handle 1");
		this.lpparam = lpparam;
		this.msgId = Long.valueOf(msgId);
		b(lpparam, msgId);
		XLog.d("FileManger" + "receiveFile_handle 2");
		Object obj = getObject();
		if (obj == null) {
			c();
			obj = getObject();
		}
		XLog.d("FileManger" + "receiveFile_handle 3");
		if (!isok(obj)) {
			a();
		}
		this.path = fileFullPath(obj);
		XLog.d("FileManger messagehandle receiveFile_handle 2 filepath:" + this.path);
		C0378b bVar = new C0378b();
		bVar.path = this.path;
		bVar.size = this.size;
		bVar.ischatroom = this.ischartroom;
		bVar.isSend = XposedHelpers.getIntField(this.callMethod, "field_isSend");
		bVar.talker = (String) XposedHelpers.getObjectField(this.callMethod, "field_talker");
		if (this.ischartroom) {
			String str2 = (String) XposedHelpers.getObjectField(this.callMethod, "field_content");
			if (str2.contains(":")) {
				bVar.friendId = str2.split(":")[0];
			} else {
				bVar.friendId = "";
			}
		} else {
			bVar.friendId = "";
		}
		return bVar;
	}
	
	private boolean isok(Object obj) {
		if (obj == null) {
			return false;
		}
		String b2 = fileFullPath(obj);
		if (b2 == null) {
			return false;
		}
		File file = new File(b2);
		if (file.exists() && file.length() == ((long) this.size)) {
			return true;
		}
		return false;
	}
	
	public static void a(LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.AppAttachDownloadUI$7", loadPackageParam.classLoader, "a", Integer.TYPE, Integer.TYPE, loadPackageParam.classLoader.loadClass("com.tencent.mm.ah.m"), new XC_MethodReplacement() {
				public Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					if (XposedHelpers.getObjectField(methodHookParam.thisObject, "xSA") == null) {
						int code = (Integer) methodHookParam.args[0];
						int result = (Integer) methodHookParam.args[1];
						XLog.d("FileManger hookTargetClass callbackObjlist coed:" + code + ",result:" + result);
						if (code == result) {
							ThreadPoolUtils.getInstance().a(new Runnable() {
								@Override
								public void run() {
//									UploadService.uploadFileToBack();
								}
							}, 2000, TimeUnit.SECONDS);
						}
						return null;
					}
					XLog.d("hookTargetClass invokeOriginalMethod ");
					return XposedBridge.invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject, methodHookParam.args);
				}
			});
		} catch (Throwable th) {
			XLog.d("FileManger hookTargetClass friendId:" + Log.getStackTraceString(th));
		}
	}
	
	private Object b() {
		try {
			return XposedHelpers.newInstance(this.lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.AppAttachDownloadUI$7"), new Class[]{this.lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.AppAttachDownloadUI")}, new Object[]{null});
		} catch (Throwable th) {
			XLog.d("FileManger getDownloadcallback friendId:" + Log.getStackTraceString(th));
			return null;
		}
	}
	
	public void a() {
		try {
			this.i = b();
			Object newInstance = XposedHelpers.newInstance(this.lpparam.classLoader.loadClass("com.tencent.mm.pluginsdk.model.app.ac"), this.msgId, this.g, this.i);
			XposedHelpers.callMethod(XposedHelpers.callStaticMethod(this.lpparam.classLoader.loadClass("com.tencent.mm.model.av"), "Pw"), "a", newInstance, 0);
			if (this.callMethod != null) {
				XposedHelpers.callStaticMethod(this.lpparam.classLoader.loadClass("com.tencent.mm.modelsimple.y"), "A", this.callMethod);
			}
		} catch (Throwable th) {
			XLog.d("FileManger getDownloadcallback friendId:" + Log.getStackTraceString(th));
		}
	}
	
	private void c() {
		try {
			XposedHelpers.callStaticMethod(this.lpparam.classLoader.loadClass("com.tencent.mm.pluginsdk.model.app.l"), "y", this.msgId, this.content);
		} catch (Throwable th) {
			XLog.d("FileManger getDownloadcallback friendId:" + Log.getStackTraceString(th));
		}
	}
	
	public void b(LoadPackageParam loadPackageParam, String str) {
		try {
			long longValue = Long.valueOf(str);
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.av"), "XE");
			Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.c"), "VK"), "iA", longValue);
			if (callMethod != null) {
				this.callMethod = callMethod;
				String field_talker = (String) XposedHelpers.getObjectField(callMethod, "field_talker");
				String field_content = (String) XposedHelpers.getObjectField(callMethod, "field_content");
				XLog.d(" getWxSysFileObj field_talker :" + field_talker);
				XLog.d(" getWxSysFileObj field_content :" + field_content);
				this.ischartroom = field_talker != null && field_talker.length() > 0 && field_talker.endsWith("@chatroom");
				if (this.ischartroom) {
					field_content = (String) XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.be"), "oa", new Object[]{field_content});
				}
				this.content = field_content;
				Object callStaticMethod = XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.ae.j$b"), "lE", field_content);
				if (callStaticMethod != null) {
					this.size = XposedHelpers.getIntField(callStaticMethod, "eRh");
					this.g = (String) XposedHelpers.getObjectField(callStaticMethod, "chy");
				}
			}
		} catch (Throwable th) {
			XLog.d("FileManger getDownloadcallback friendId:" + Log.getStackTraceString(th));
		}
	}
	
	private static String fileFullPath(Object obj) {
		return (String) XposedHelpers.getObjectField(obj, "field_fileFullPath");
	}
	
	private Object getObject() {
		try {
			Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(this.lpparam.classLoader.loadClass("com.tencent.mm.pluginsdk.model.app.ap"), "aQV"), "ln", this.msgId);
			if (callMethod == null) {
				callMethod = XposedHelpers.callStaticMethod(this.lpparam.classLoader.loadClass("com.tencent.mm.pluginsdk.model.app.l"), "ahi", this.g);
			}
			return callMethod;
		} catch (ClassNotFoundException e2) {
			XLog.d(" getWxSysFileObj friendId :" + Log.getStackTraceString(e2));
			return null;
		}
	}
}
