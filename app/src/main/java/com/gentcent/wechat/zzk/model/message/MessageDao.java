package com.gentcent.wechat.zzk.model.message;

import android.database.Cursor;
import android.util.Log;

import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;

/**
 * @author zuozhi
 * @since 2019-08-02
 */
public class MessageDao {
	
	/**
	 * 获取当前msgId（Gif）（被轮询调用）
	 *
	 * @param info 封装的内部类
	 * @return 当前msgId
	 */
	public static long getGifCurMsgId(SendMessageManager.Info info) {
		Cursor cursor;
		try {
			if (info.lastMsgId > 0) {
				String sql1 = "SELECT msgId FROM message WHERE isSend = 1 and ( type =1048625 or  type =47 ) and talker ='" + info.touser + "'  and msgId > " + info.lastMsgId + " ORDER BY msgId desc limit 1 ";
				cursor = WcdbHolder.excute(sql1, "EnMicroMsg.db");
			} else {
				String sql2 = "SELECT msgId FROM message WHERE isSend = 1 and ( type =1048625 or  type =47 ) and talker ='" + info.touser + "'  and createTime>=" + info.currentTimeMillis + " ORDER BY msgId desc limit 1 ";
				cursor = WcdbHolder.excute(sql2, "EnMicroMsg.db");
			}
			if (cursor == null) {
				return 0;
			}
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				return cursor.getLong(cursor.getColumnIndex("msgId"));
			}
			cursor.close();
			return 0;
		} catch (Throwable th) {
			XLog.d("SendManager" + "getCurSendId msgid:" + Log.getStackTraceString(th));
			return 0;
		}
	}
	
	/**
	 * 获取当前最新的msgId（被轮询调用）
	 *
	 * @param info 内部封装类
	 * @return 当前最新的msgId
	 */
	public static long getCurMsgId(SendMessageManager.Info info) {
		Cursor cursor;
		try {
			XLog.d("Mysned getLastMsgId select info.time is " + info.currentTimeMillis + " type is " + info.type + "  info.touser is " + info.touser + "  info.lastMsgId is " + info.lastMsgId);
			if (info.lastMsgId > 0) {
				String sql1 = "SELECT msgId FROM message WHERE isSend = 1 and type =" + info.type + " and talker ='" + info.touser + "'  and msgId > " + info.lastMsgId + " ORDER BY msgId desc limit 1 ";
				cursor = WcdbHolder.excute(sql1, "EnMicroMsg.db");
			} else {
				String sql2 = "SELECT msgId FROM message WHERE isSend = 1 and type =" + info.type + " and talker ='" + info.touser + "'  and createTime>=" + info.currentTimeMillis + " ORDER BY msgId desc limit 1 ";
				cursor = WcdbHolder.excute(sql2, "EnMicroMsg.db");
			}
			if (cursor == null) {
				XLog.d("getLastMsgId cur is null");
				return 0;
			}
			XLog.d("getLastMsgId size is " + cursor.getCount());
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				long msgId = cursor.getLong(cursor.getColumnIndex("msgId"));
				XLog.d("1getCurMsgId msgId is " + msgId);
				return msgId;
			}
			cursor.close();
			XLog.d("2getCurMsgId msgId is 000");
			return 0;
		} catch (Throwable th) {
			XLog.d("SendManager" + " getCurSendId msgid:" + Log.getStackTraceString(th));
			return 0;
		}
	}
	
	/**
	 * 查询当前最近的msgId
	 *
	 * @param type       类型
	 * @param friendWxId 接受者的微信id
	 * @return 当前最近的msgId
	 */
	public static long getLastMsgId(int type, String friendWxId) {
		Cursor c1 = WcdbHolder.excute("SELECT msgId FROM message ORDER BY msgId desc limit 1", "EnMicroMsg.db");
		if (c1 == null) {
			XLog.d("getLastMsgId cur is null");
			return 0;
		}
		XLog.d("getLastMsgId size is " + c1.getCount());
		c1.moveToFirst();
		if (c1.isAfterLast()) {
			return 0;
		}
		long msgId = c1.getLong(c1.getColumnIndex("msgId"));
		XLog.d("1getCurMsgId msgId is " + msgId);
		return msgId;
	}
}
