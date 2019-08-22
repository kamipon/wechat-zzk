package com.gentcent.wechat.zzk.model.message;

import android.content.ContentValues;
import android.util.Log;

import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.background.MessageConvert;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XposedHelpers;

import java.io.File;

/**
 * @author zuozhi
 * @since 2019-08-12
 */
public class DownloadMessageJob{
	private static String TAG = "DownloadMessageJob:  ";
	private static final int PRIORITY = 100;
	private String talker;
	private int mDelay;    //单位秒
	private String msgId;    //msgId
	private int isSend;    //isSend
	private boolean isChartRoom;    //isChartRoom
	private String content;    //content
	private String createTime;    //createTime
	private long msgSvrId;    //msgSvrId
	private int type;    //type
	private ContentValues contentValues;
	private String aeskey;
	private String cdnthumburl;
	private String length;
	
	
	public DownloadMessageJob(ContentValues contentValues, int mDelay) {
		this.mDelay = mDelay;
		this.content = contentValues.getAsString("content");
		this.talker = contentValues.getAsString("talker");
		this.createTime = contentValues.getAsString("createTime");
		this.isSend = contentValues.getAsInteger("isSend");
		this.msgId = contentValues.getAsString("msgId");
		this.msgSvrId = contentValues.getAsLong("msgSvrId");
		this.type = contentValues.getAsInteger("type");
		this.isChartRoom = this.talker.endsWith("@chatroom");
		this.contentValues = contentValues;
		this.aeskey = MessageResolver.a(content, "aeskey=");
		this.cdnthumburl = MessageResolver.a(content, "cdnthumburl=");
		this.length = MessageResolver.a(content, "length=");
	}
	
	public void onRun() {
		XLog.d(TAG + "run");
		try {
			if (type == 3) { //图片
				ImgDownload();
			}
			Thread.sleep(mDelay * 1000);
		} catch (Exception e) {
			XLog.e(TAG + "错误:" + Log.getStackTraceString(e));
		}
	}
	
	private void ImgDownload(){
		Object a2 = ZzkUtil.getMsgObj(MainManager.wxLpparam, Long.valueOf(this.msgId));
		if (a2 != null) {
			XLog.d("保存图片");
			Class findClass = XposedHelpers.findClass(HookParams.receive_imgFile_class1, MainManager.wxLpparam.classLoader);
			XLog.d("ImageHook n:" + findClass);
			boolean z = false;
			Object callStaticMethod = XposedHelpers.callStaticMethod(findClass, HookParams.receive_imgFile_class1_method);
			XLog.d("ImageHook f:" + callStaticMethod);
			Class findClass2 = XposedHelpers.findClass(HookParams.receive_imgFile_class2, MainManager.wxLpparam.classLoader);
			XLog.d("ImageHook g:" + findClass2);
			String sb4 = "SERVERID://" + msgSvrId;
			byte[] bytes = sb4.getBytes();
			XLog.d("by:" + bytes);
			String str9 = (String) XposedHelpers.callStaticMethod(findClass2, HookParams.receive_imgFile_class2_method, new Object[]{bytes});
			XLog.d("ImageHook str:" + str9);
			String obj = XposedHelpers.callMethod(callStaticMethod, HookParams.receive_imgFile_method, str9, "th_", "").toString();
			XLog.d("ImageHook str2:" + obj);
			String f = UserDao.getMyWxid();
			XLog.d("ImageHook 图片路径:" + obj + "\n发送id:" + talker + "\n接收id:" + f);
			Class findClass3 = XposedHelpers.findClass(HookParams.receive_fileKey_class, MainManager.wxLpparam.classLoader);
			long parseLong = Long.parseLong(createTime.substring(0, 10));
			XLog.d("ImageHook time is " + parseLong);
			String str10 = (String) XposedHelpers.callStaticMethod(findClass3, HookParams.receive_fileKey_class_method, new Object[]{"downimg", parseLong, talker, msgId});
			XLog.d("ImageHook fileKey:" + str10);
			Class findClass4 = XposedHelpers.findClass(HookParams.receive_imgDownload_parameter, MainManager.wxLpparam.classLoader);
			Class findClass5 = XposedHelpers.findClass(HookParams.receive_imgDownload_class, MainManager.wxLpparam.classLoader);
			Object newInstance = XposedHelpers.newInstance(findClass4);
			String path = obj.replace("th_", "") + ".jpg";
			XLog.d("ImageHook requestNew:" + newInstance);
			XposedHelpers.setObjectField(newInstance, "aeskey", aeskey);
			XposedHelpers.setObjectField(newInstance, "fileid", cdnthumburl);
			XposedHelpers.setObjectField(newInstance, "fileKey", str10);
			XposedHelpers.setObjectField(newInstance, "savePath", path);
			XposedHelpers.setObjectField(newInstance, "queueTimeoutSeconds", 0);
			XposedHelpers.setObjectField(newInstance, "transforTimeoutSeconds", 0);
			XposedHelpers.setObjectField(newInstance, "fileSize", Integer.valueOf(length));
			XposedHelpers.setObjectField(newInstance, "fileType", 2);
			Object callStaticMethod2 = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.an.f", MainManager.wxLpparam.classLoader), "aki");
			XposedHelpers.callStaticMethod(findClass5, HookParams.receive_imgDownload_class_method, newInstance, callStaticMethod2);
			XLog.d("ImageHook 成功了");
			
			MessageBean messageBean = new MessageBean();
			messageBean.setMyWxId(UserDao.getMyWxid());
			messageBean.setFriendWxId(talker);
			messageBean.setIsSend(isSend);
			messageBean.setStatus(SendMessageManager.getStatusByMsgId(Long.valueOf(msgId)));
			messageBean.setAddTime(parseLong);
			messageBean.setServiceGuid("");
			messageBean.setType(UploadService.mappingType(type));
			UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
			uploadBean = MessageConvert.a(uploadBean, talker);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UploadService.uploadFileToBack(new File(path), uploadBean);
		}
	}
}
