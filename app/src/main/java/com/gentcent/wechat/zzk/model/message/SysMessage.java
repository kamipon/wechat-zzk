package com.gentcent.wechat.zzk.model.message;

import android.content.ContentValues;
import android.text.TextUtils;

import com.gentcent.wechat.zzk.background.MessageConvert;
import com.gentcent.wechat.zzk.background.UploadUtil;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.util.DisposeXMLUtil;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.HashMap;
import java.util.Iterator;

public class SysMessage {
	public static void a(String talker, String content, long time) {
		if (content.contains("sysmsgtemplate")) {
			int indexOf = content.indexOf("<sysmsg type=");
			XLog.d("SysMsgCallback  content ||" + content + " i : " + indexOf);
			String substring = content.substring(indexOf, content.indexOf("</sysmsg>") + 9);
			XLog.d("SysMsgCallback  postSysMessageTosendMsg ");
			if (!TextUtils.isEmpty(substring)) {
				String templateText = a(substring);
				XLog.d("SysMsgCallback templateText" + templateText);
				MessageBean messageBean = new MessageBean(UserDao.getMyWxid(), talker, templateText, -1, 99);
				if (talker.contains("@chatroom")) {
					b(messageBean);
				} else {
					a(messageBean);
				}
			}
		} else {
			content = ZzkUtil.reduceHtml(content);
			MessageBean messageBean2 = new MessageBean(UserDao.getMyWxid(), talker, content, -1, 99);
			if (talker.contains("@chatroom")) {
				b(messageBean2);
			} else {
				a(messageBean2);
			}
		}
		XLog.d("SysMessage SysMsgCallback : talker:" + talker + ",content:" + content + ",time:" + time);
	}
	
	public static void a(String talker, String content, String msgId) {
		if (content.contains("<sysmsg")) {
			content = DisposeXMLUtil.b(content);
			StringBuilder sb = new StringBuilder();
			sb.append("retractCallback ");
			sb.append(content);
			XLog.d(sb.toString());
		}
		ZzkUtil.reduceHtml(content);
		MessageBean messageBean = new MessageBean(UserDao.getMyWxid(), talker, ZzkUtil.reduceHtml(content), -1, 99);
		XLog.d("撤回消息回调 retractCallback postSysMessageTosendMsg ");
		if (talker.contains("@chatroom")) {
			b(messageBean);
		} else {
			a(messageBean);
		}
		XLog.d("SysMessage retractCallback : talker:" + talker + ",content:" + content + ",msgId:" + msgId);
	}
	
	public static void a(XC_LoadPackage.LoadPackageParam loadPackageParam, ContentValues contentValues) {
		long msgId = contentValues.getAsLong("msgId");
		String content = (String) contentValues.get("content");
		XLog.d("SysMessagemessage msgId:" + msgId + ",content ：" + content);
		Object a = ZzkUtil.getMsgObj(loadPackageParam, msgId);
		if (a != null) {
			String str2 = (String) XposedHelpers.getObjectField(a, "field_talker");
			a(str2, content, String.valueOf(msgId));
		}
	}
	
	public static void b(String talker, String str2, long time) {
		String content = a(talker, str2);
		MessageBean messageBean = new MessageBean(UserDao.getMyWxid(), talker, content, -1, 99);
		XLog.d("NonfriendCallback postSysMessageTosendGroupMsg");
		if (talker.contains("@chatroom")) {
			b(messageBean);
		} else {
			a(messageBean);
		}
		XLog.d("SysMessage NonfriendCallback : talker:" + talker + ",content:" + content + ",time:" + time);
	}
	
	static String a(String str, String content) {
		if (!content.contains("sysmsgtemplate")) {
			return ZzkUtil.reduceHtml(content);
		}
		int indexOf = content.indexOf("<sysmsg type=");
		XLog.d("SysMsgCallback  content ||" + content + " i : " + indexOf);
		String substring = content.substring(indexOf);
		XLog.d("SysMsgCallback substring ||" + substring);
		if (TextUtils.isEmpty(substring)) {
			return content;
		}
		String templateText = a(substring);
		XLog.d("SysMsgCallback templateText" + templateText);
		return content;
	}
	
	public static void b(XC_LoadPackage.LoadPackageParam loadPackageParam, ContentValues contentValues) {
		b((String) contentValues.get("talker"), (String) contentValues.get("content"), contentValues.getAsLong("createTime"));
	}
	
	public static void c(XC_LoadPackage.LoadPackageParam loadPackageParam, ContentValues contentValues) {
		a((String) contentValues.get("talker"), (String) contentValues.get("content"), contentValues.getAsLong("createTime"));
	}
	
	private static void a(MessageBean messageBean) {
		UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
		uploadBean = MessageConvert.a(uploadBean, messageBean.getFriendWxId());
		UploadUtil.sendToBack(uploadBean);
	}
	
	private static void b(MessageBean messageBean) {
		XLog.e("TODO： 群聊消息");
//		UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
//		uploadBean = MessageConvert.a(uploadBean, messageBean.getFriendWxId());
//		UploadUtil.sendToBack(uploadBean);
	}
	
	static String a(String str) {
		String paserXmlContent = DisposeXMLUtil.a(str);
		StringBuilder sb = new StringBuilder();
		sb.append("paserXmlContent content ");
		sb.append(paserXmlContent);
		XLog.d(sb.toString());
		HashMap<String, Object> hashMap = new HashMap<>();
		Iterator it = DisposeXMLUtil.c(str).iterator();
		while (it.hasNext()) {
			String str2 = (String) it.next();
			if (str2.contains("$revoke$")) {
				paserXmlContent = paserXmlContent.replace("$revoke$", "");
			} else {
				StringBuilder sb2 = new StringBuilder();
				sb2.append("paserXmlContent tag");
				sb2.append(str2);
				sb2.append(" value ");
				sb2.append(DisposeXMLUtil.a(str, str2));
				XLog.d(sb2.toString());
				hashMap.put(str2, DisposeXMLUtil.a(str, str2));
			}
		}
		for (String str3 : hashMap.keySet()) {
			paserXmlContent = paserXmlContent.replace(str3, (CharSequence) hashMap.get(str3));
		}
		XLog.d("paserXmlContent  " + paserXmlContent);
		return paserXmlContent;
	}
}
