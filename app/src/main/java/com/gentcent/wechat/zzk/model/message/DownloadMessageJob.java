package com.gentcent.wechat.zzk.model.message;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XposedHelpers;

import java.io.File;
import java.util.Date;

/**
 * @author zuozhi
 * @since 2019-08-12
 */
public class DownloadMessageJob extends Job {
	private static String TAG = "DownloadMessageJob:  ";
	private static final int PRIORITY = 1;
	private String talker;
	private int mDelay;    //单位秒
	private String msgId;    //msgId
	private int isSend;    //isSend
	private boolean isChartRoom;    //isChartRoom
	private String content;    //content
	private int createTime;    //createTime
	private long msgSvrId;    //msgSvrId
	private int type;    //type
	
	
	public DownloadMessageJob(ContentValues contentValues, int mDelay) {
		super(new Params(PRIORITY).persist());
		this.mDelay = mDelay;
		this.content = contentValues.getAsString("content");
		this.talker = contentValues.getAsString("talker");
		this.createTime = (int) (contentValues.getAsLong("createTime") / 1000L);
		this.isSend = contentValues.getAsInteger("isSend");
		this.msgId = contentValues.getAsString("msgId");
		this.msgSvrId = contentValues.getAsLong("msgSvrId");
		this.type = contentValues.getAsInteger("type");
		this.isChartRoom = this.talker.endsWith("@chatroom");
	}
	
	@Override
	public void onAdded() {
		XLog.d(TAG + "add on " + new Date().toLocaleString());
	}
	
	@Override
	public void onRun() {
		try {
			ImgDownload();
			
			Thread.sleep(mDelay * 1000);
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e(TAG + "错误:" + Log.getStackTraceString(e));
		}
	}
	
	private void ImgDownload() throws ClassNotFoundException {
		XLog.d(TAG + "download_img");
		Object a2 = ZzkUtil.getMsgObj(MainManager.wxLpparam, Long.valueOf(this.msgId));
		if (a2 != null) {
			long longValue = (Long) XposedHelpers.getObjectField(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.as.o"), "afi"), "u", a2), "fof");
			XLog.d("ImgHDHandle downloag fof:" + longValue);
			Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.as.o", MainManager.wxLpparam.classLoader), "afi");
			Class findClass = XposedHelpers.findClass("com.tencent.mm.a.g", MainManager.wxLpparam.classLoader);
			String sb2 = "SERVERID://" + this.msgSvrId;
			byte[] bytes = sb2.getBytes();
			XLog.d("by:" + bytes);
			String str = (String) XposedHelpers.callStaticMethod(findClass, "u", new Object[]{bytes});
			String obj = XposedHelpers.callMethod(callStaticMethod, "q", str, "th_", "").toString();
			
			String path = obj.replace("th_", "") + ".jpg";
			XLog.d("ImgHDHandle receive image path:" + path);

//				String a3 = resolver(this.content, "hdlength=");
//				long j = 0;
//				if (a3 != null && !a3.equals("")) {
//					j = Long.valueOf(a3);
//				}
//				ImgHDHandle.this.a(sb5, obj, j);
			int staticIntField = XposedHelpers.getStaticIntField(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.R$f"), "chat_img_template");
			Object callStaticMethod2 = XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.gallery.l"), "dAR");
			XposedHelpers.callMethod(XposedHelpers.getObjectField(callStaticMethod2, "yjP"), "add", Long.valueOf(this.msgId));
			XposedHelpers.callMethod(XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.as.o"), "afj"), "a", longValue, Long.valueOf(this.msgId), 1, 100000, staticIntField, callStaticMethod2, 0, Boolean.TRUE);
			XLog.d("ImgHDHandle  downloag");
			
			
			MessageBean messageBean = new MessageBean();
			messageBean.setMyWxId(UserDao.getMyWxid());
			messageBean.setFriendWxId(talker);
			messageBean.setIsSend(isSend);
			messageBean.setStatus(SendMessageManager.getStatusByMsgId(Long.valueOf(msgId)));
			messageBean.setAddTime(createTime);
			messageBean.setServiceGuid("");
			UserBean userBean = UserDao.getUserBeanByWxId(talker);
			UploadBean uploadBean = new UploadBean(userBean, messageBean, MyHelper.readLine("phone-id"));
			
			UploadService.uploadFileToBack(new File(path), uploadBean, type);
			
		}
	}
	
	private String resolver(String conent, String str2) {
		String str3 = " ";
		boolean z = false;
		try {
			int indexOf = conent.indexOf(str2);
			String substring = conent.substring(str2.length() + indexOf, indexOf + conent.substring(indexOf, conent.length() - 1).indexOf(str3));
			if (substring.endsWith(")")) {
				substring = substring.substring(0, substring.length() - 1);
			}
			if (z) {
				return substring;
			}
			return substring.replace("\"", "");
		} catch (Exception unused) {
			return "";
		}
	}
	
	@Override
	protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
	
	}
	
	@Override
	protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
		return null;
	}
}
