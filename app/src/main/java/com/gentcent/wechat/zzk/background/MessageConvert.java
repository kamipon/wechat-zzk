package com.gentcent.wechat.zzk.background;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;

import java.util.HashMap;
import java.util.Map;

public class MessageConvert {
	private static Map<String, UserBean> userList = new HashMap();
	
	public static synchronized UploadBean a(UploadBean uploadBean, String talker) {
		synchronized (MessageConvert.class) {
			try {
				XLog.d("MessageConvert convert wxid is " + talker);
				UserBean userBean = userList.get(talker);
				if (userBean == null) {
					userBean = UserDao.getUserBeanByWxId(talker);
					userList.put(talker, userBean);
				}
				if (userBean != null) {
					uploadBean.userBean = userBean;
				} else {
					XLog.d("MessageConvert" + "uploadBean.Type is " + uploadBean.messageBean.getType());
					if (uploadBean.messageBean.getType() == 99 && talker.endsWith("@chatroom")) {
						String displayname = "";
						String sql = "select displayname from chatroom where chatroomname='" + talker + "'";
						Cursor a2 = WcdbHolder.excute(sql);
						while (a2.moveToNext()) {
							displayname = a2.getString(a2.getColumnIndex("displayname"));
						}
						XLog.d("displayname is " + displayname);
						if (!TextUtils.isEmpty(displayname)) {
							if (displayname.length() <= 30) {
								uploadBean.userBean.nickname = displayname;
							} else {
								uploadBean.userBean.nickname = displayname.substring(0, 30);
							}
						}
						a2.close();
						String sb5 = "uploadBean.userBean.nickname is " + uploadBean.userBean.nickname;
						XLog.d("MessageConvert" + sb5);
					}
				}
				if (!talker.endsWith("@chatroom") && ObjectUtils.isEmpty(uploadBean.userBean.nickname)) {
					uploadBean.userBean.nickname = UserDao.getNickByWxid(talker);
					XLog.d("uploadBean.userBean.nickname second is " + uploadBean.userBean.nickname);
					if (uploadBean.messageBean.getType() == 99 && ObjectUtils.isEmpty(uploadBean.userBean.nickname) && ObjectUtils.isNotEmpty(uploadBean.messageBean.getContent())) {
						String str3 = "刚刚把你添加到通讯录，现在可以开始聊天了。";
						String str4 = "你已添加了";
						String str5 = "，现在可以开始聊天了。";
						if (uploadBean.messageBean.getContent().endsWith(str3)) {
							uploadBean.userBean.nickname = uploadBean.messageBean.getContent().replace(str3, "");
						} else if (uploadBean.messageBean.getContent().startsWith(str4) && uploadBean.messageBean.getContent().endsWith(str5)) {
							uploadBean.userBean.nickname = uploadBean.messageBean.getContent().replace(str4, "");
							uploadBean.userBean.nickname = uploadBean.userBean.nickname.replace(str5, "");
						}
						XLog.d("uploadBean.userBean.nickname is " + uploadBean.userBean.nickname);
					}
				}
			} catch (Exception e) {
				XLog.d("MessageConvert error:" + Log.getStackTraceString(e));
			}
		}
		return uploadBean;
	}
}
