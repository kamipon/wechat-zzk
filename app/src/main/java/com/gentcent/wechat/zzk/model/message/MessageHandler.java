package com.gentcent.wechat.zzk.model.message;

import android.content.ContentValues;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.background.MessageConvert;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.model.friend.AddVerifyingFriend;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.service.TaskManager;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.VoiceManager;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author zuozhi
 * @since 2019-08-15
 */
public class MessageHandler {
	public static void message(ContentValues contentValues) {
		/*
		 * 1：纯文本消息√
		 * 3：图片√
		 * 34：语音√
		 * 42：名片
		 * 43：视频√
		 * 47：表情√
		 * 48：指定定位
		 * 49：文件、链接信息
		 * 50：视屏通话 content：voip_content_video
		 * 50：语音通话 content：voip_content_voice
		 * 436207665：红包
		 * 419430449：转账
		 * -1879048186：共享实时位置
		 * 10000：提示字体
		 */
		final int type = contentValues.getAsInteger("type");
		//0：接受 , 1：发送
		final int isSend = contentValues.getAsInteger("isSend");
		//信息ID , 递增
		final long msgId = contentValues.getAsLong("msgId");
		//发送者ID
		final String talker = contentValues.getAsString("talker");
		//消息内容
		final String content = contentValues.getAsString("content");
		final int createTime = (int) (contentValues.getAsLong("createTime") / 1000L);
		final int status = SendMessageManager.getStatusByMsgId(msgId);
		XLog.d("message || type=" + type + "; msgId=" + msgId + "; isSend=" + isSend + "; talker=" + talker + "; content=" + content);
		
		if (isSend == 0) {
			if (isNeedSendToBack(talker)) {
				if (!talker.endsWith("@chatroom")) {
					if (UserDao.getUserBeanByWxId(talker) == null) {
						AddVerifyingFriend.run(talker);
					}
				}
				if (type == 1) { //文本消息
					XLog.d("messageHandle" + "MysnedText msgId =" + msgId + " content :" + content);
					ThreadPoolUtils.getInstance().a(new Runnable() {
						public void run() {
							XLog.d("receiveDelay text state is " + status);
							if (talker.endsWith("@chatroom")) {
								//aj.b(status, QNUploadUtil.a(isSend), talker, content, createTime);
								return;
							}
							UploadService.receiveTextMessage(status, isSend, talker, content, createTime, msgId);
						}
					}, 350, TimeUnit.MILLISECONDS);
				} else if (type == 3) { //图片消息
					JobManager jobManager = TaskManager.getInstance().getJobManager();
					jobManager.addJobInBackground(new DownloadMessageJob(contentValues, 2));
				} else if (type == 47) { //表情
					UploadService.receiveAnimationMessage(status, isSend, talker, content, createTime, msgId);
				} else if (type == 34) { //语音
					MessageBean messageBean = new MessageBean();
					messageBean.setMyWxId(UserDao.getMyWxid());
					messageBean.setType(2);
					messageBean.setServiceGuid("");
					messageBean.setAddTime(createTime);
					messageBean.setFriendWxId(talker);
					messageBean.setStatus(status);
					messageBean.setIsSend(isSend);
					messageBean.setMsgId(String.valueOf(msgId));
					UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
					uploadBean = MessageConvert.a(uploadBean, talker);
					MsgHelper.getInstance().putMsg(msgId, uploadBean);
					XLog.d("接受语音消息： STEP1  保存到MsgHelper中.");
				} else if (type == 43) { //视频
					String imgPath = contentValues.getAsString("imgPath");
					XLog.d("messageHandle MysnedVideo msgId =" + msgId + " content :" + content + ",imgPath:" + imgPath);
				}
			}
		}
	}
	
	/**
	 * 语音
	 */
	public static void WxFileIndex2(final ContentValues contentValues) {
		if (contentValues.containsKey("msgSubType") && contentValues.containsKey("msgType") && contentValues.containsKey("msgId") && contentValues.containsKey("msgtime")) {
			int msgSubType = contentValues.getAsInteger("msgSubType");
			final int msgType = contentValues.getAsInteger("msgType");
			final long size = contentValues.getAsLong("size");
			final String username = (String) contentValues.get("username");
			final int msgtime = (int) (contentValues.getAsLong("msgtime") / 1000);
			final Long msgId = contentValues.getAsLong("msgId");
			if (msgType == 49 && msgSubType == 34) {
				XLog.d("WxFileIndex2Handle MysnedFile msgId =" + msgId);
//				SendMessageManager.a(new Runnable() {
//					public void run() {
//						aa aV = new aa();
//						C0378b a2 = aV.a(loadPackageParam, "" + msgId);
//						if (ObjectUtils.isNotEmpty((Object) a2)) {
//							XLog.d("WxFileIndex2Handle   insertWithOnConflict WxFileIndex2 xxxx res  " + a2.toString());
//							QNUploadUtil.a(a2.c == 1 ? QNUploadUtil.e : QNUploadUtil.f, a2.d, a2.a, a2.e, a2.b, a2.f, msgtime);
//						}
//					}
//				}, 49, msgId.longValue(), -1);
			} else if (msgType == 34 && msgSubType == 10) {
				if (MsgHelper.getInstance().getMsg(msgId) != null) {
					final String localpath = "/storage/emulated/0/tencent/MicroMsg/" + contentValues.getAsString("path");
					ThreadPoolUtils.getInstance().run(new Runnable() {
						@Override
						public void run() {
							XLog.d("接受语音消息： STEP2  receivehandle msgId is " + msgId + " username is " + username + " path is " + localpath + " size is " + size);
							UploadBean uploadBean = MsgHelper.getInstance().getMsg(msgId);
							if (uploadBean != null) {
								String newPath = localpath.substring(0, localpath.lastIndexOf('.') + 1) + "mp3";
								int i = VoiceManager.amrToMp3(localpath, newPath);
								if (i == -1) {
									newPath = localpath;
								}
								UploadService.uploadFileToBack(new File(newPath), uploadBean);
							}
						}
					});
				}
			} else if (msgType == 3 && (msgSubType == 20 || msgSubType == 21)) {
//				String asString2 = contentValues.getAsString("path");
//				XLog.d("1 receivehandle msgId is " + msgId + " username is " + username + " path is " + asString2 + " size is " + size + "  msgSubType is " + msgSubType);
//				if (msgSubType == 21 && size > 0) {
//					MsgHelperBean a = MsgHelper.a().a(msgId);
//					XLog.d("msgHelperBean is " + GsonUtils.a((Object) a));
//					if (!(a == null || a.contentValues == null)) {
//						ImageManager.a().b(loadPackageParam2, a.contentValues, a.friendId, aj.a(a.username));
//					}
//					return;
//				} else if (MsgHelper.a().a(msgId) != null && size > 0) {
//					SendManager.a(msgId + "", asString2);
//					XLog.d("receivehandle fixMessageContent is success path " + asString2);
//					AnonymousClass3 r02 = new Runnable() {
//						public void run() {
//							String asString = contentValues.getAsString("path");
//							XLog.d("2 receivehandle msgId is " + msgId + " username is " + username + " path is " + asString + " size is " + size);
//							MsgHelperBean a2 = MsgHelper.getInstance().getMsg(msgId);
//							if (a2 != null && size > 0) {
//								int i = a2.isSend;
//								QNUploadUtil.a("", -1, sb3, i, str, asString, str, aj.a(msgId + ""), msgtime);
//							}
//						}
//					};
//					SendManager.a(r02, 3, msgId.longValue(), -1);
//				}
			}
			if (contentValues.containsKey("msgtime") && contentValues.containsKey("username")) {
				XLog.d("WxFileIndex2Handle  insertWithOnConflict WxFileIndex2 : msgtime :" + msgtime + ",msgSubType :" + msgSubType + ",username :" + username + ",msgId :" + msgId + ",values :" + contentValues);
			}
		}
	}
	
	/**
	 * 链接
	 */
	public static void AppMessageHandle(ContentValues contentValues) {
		final LoadPackageParam loadPackageParam = MainManager.wxLpparam;
		if (contentValues.containsKey("type") && contentValues.containsKey("msgId")) {
			int type = contentValues.getAsInteger("type");
			XLog.d("AppMessageHandle type" + type);
			final long msgid = contentValues.getAsLong("msgId");
			if (type == 5) { //app页面分享
				XLog.d("MysnedUrl  msgid = " + msgid + " insert time is " + System.currentTimeMillis());
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						receiveArtic(loadPackageParam, msgid);
					}
				}, 2000, TimeUnit.MILLISECONDS);
			} else if (type == 33) {
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						XLog.e("TODO: type==33 AppBrandHandle.a(loadPackageParam, msgid)");
//						AppBrandHandle.a(loadPackageParam, msgid);
					}
				}, 2000, TimeUnit.MILLISECONDS);
			}
			if (contentValues.containsKey("source") && contentValues.containsKey("appId") && contentValues.containsKey("description") && contentValues.containsKey("title")) {
				String source = contentValues.getAsString("source");
				String appId = contentValues.getAsString("appId");
				String description = contentValues.getAsString("description");
				String title = contentValues.getAsString("title");
				XLog.d("AppMessageHandleinsertWithOnConflict AppMessage tpye: " + type + ",msgId:" + msgid + ",source:" + source + ",appId:" + appId + ",description:" + description + ",title:" + title);
			}
		}
	}
	
	public static void receiveArtic(LoadPackageParam loadPackageParam, long msgId) {
		boolean ischartRoom;
		try {
			Object a = ZzkUtil.getMsgObj(loadPackageParam, msgId);
			int field_isSend = XposedHelpers.getIntField(a, "field_isSend");
			String field_imgPath = (String) XposedHelpers.getObjectField(a, "field_imgPath");
			String field_talker = (String) XposedHelpers.getObjectField(a, "field_talker");
			String field_content = (String) XposedHelpers.getObjectField(a, "field_content");
			XLog.d("ReceiveArticUtil handle field_talker :" + field_talker);
			XLog.d("ReceiveArticUtil handle field_content :" + field_content);
			if (field_talker != null && field_talker.length() > 0 && field_talker.endsWith("@chatroom")) {
				field_content = (String) XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.be"), "oa", field_content);
				ischartRoom = true;
			} else {
				ischartRoom = false;
			}
			Object callStaticMethod = XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.ae.j$b"), "lE", field_content);
			if (callStaticMethod != null) {
				String imgPath = (field_imgPath == null || field_imgPath.length() <= 0) ? null : ZzkUtil.getImgPath(loadPackageParam, field_imgPath);
				String url = (String) XposedHelpers.getObjectField(callStaticMethod, "url");
				String description = (String) XposedHelpers.getObjectField(callStaticMethod, "description");
				String title = (String) XposedHelpers.getObjectField(callStaticMethod, "title");
//				if (field_isSend == 0) {
//					GroupInvite.load_openUrl(loadPackageParam, url);
//				}
				XLog.d("msgId:" + msgId);
				XLog.d("ischartRoom:" + ischartRoom);
				XLog.d("imgPath:" + imgPath);
				XLog.d("title:" + title);
				XLog.d("field_talker:" + field_talker);
				XLog.d("url:" + url);
				XLog.d("description:" + description);
				XLog.d("field_isSend:" + field_isSend);
				
				MessageBean messageBean = new MessageBean();
				messageBean.setFriendWxId(field_talker);
				messageBean.setMyWxId(UserDao.getMyWxid());
				messageBean.setIsSend(field_isSend);
				messageBean.setLinkDescription(description);
				messageBean.setLinkUrl(url);
				messageBean.setLinkTitle(title);
				messageBean.setMsgId(String.valueOf(msgId));
				messageBean.setType(7);
				UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
				uploadBean = MessageConvert.a(uploadBean, field_talker);
				File file = new File(imgPath);
				if (field_isSend == 0) {
					UploadService.uploadFileToBack(file, uploadBean);
				}
				
			}
		} catch (Throwable th) {
			XLog.d("ReceiveArticUtil  handle e:" + Log.getStackTraceString(th));
		}
	}
	
	
	public static boolean isNeedSendToBack(String wxId) {
		return (ObjectUtils.isNotEmpty(wxId) && wxId.endsWith("@chatroom")) || isNotAppWxId(wxId);
	}
	
	private static String[] appNameList = {"filehelper", "qqmail", "floatbottle", "shakeapp", "lbsapp", "medianote", "newsapp", "facebookapp", "qqfriend", "masssendapp", "feedsapp", "voipapp", "officialaccounts", "voicevoipapp", "voiceinputapp", "linkedinplugin", "notifymessage", "fmessage", "weixin", "qmessage", "tmessage"};
	
	public static boolean isNotAppWxId(String wxId) {
		return ObjectUtils.isNotEmpty(wxId) && !wxId.startsWith("gh_") && !wxId.startsWith("fake_") && !wxId.endsWith("@chatroom") && !Arrays.asList(appNameList).contains(wxId);
	}
	
}
