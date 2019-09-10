package com.gentcent.wechat.zzk.smscall;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.MyApplication;
import com.gentcent.wechat.zzk.ZzkReceiver;
import com.gentcent.wechat.zzk.background.UploadUtil;
import com.gentcent.wechat.zzk.smscall.bean.SmsInfo;
import com.gentcent.wechat.zzk.util.XLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @author zuozhi
 * @since 2019-09-09
 */
public class SmsService extends Service {
	public static List<Integer> list = new ArrayList<Integer>();
	private SmsObserver mObserver;
	private ZzkReceiver myReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
//		XLog.e("SmsService 服务器启动了....");
		// 在这里启动
		ContentResolver resolver = getContentResolver();
		mObserver = new SmsObserver(this, smsHandler);
		resolver.registerContentObserver(Uri.parse("content://sms"), true, mObserver);
		
		//注册接收器
		IntentFilter intentFilter = new IntentFilter("ZzkAction");
		myReceiver = new ZzkReceiver();
		MyApplication.getAppContext().registerReceiver(myReceiver, intentFilter);
		XLog.d("注册ZZK接收器");
	}
	
	@SuppressLint("HandlerLeak")
	public Handler smsHandler = new Handler() {
		//这里可以进行回调的操作
		
	};
	
	class SmsObserver extends ContentObserver {
		public SmsObserver(Context context, Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			//每当有新短信到来时，使用我们获取短消息的方法
			getSmsFromPhone();
		}
	}
	
	private Uri SMS_INBOX = Uri.parse("content://sms/");
	
	public void getSmsFromPhone() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[]{"_id", "address", "person", "body", "date", "type", "read"};
		Cursor cur = cr.query(SMS_INBOX, projection, "(type = ? AND read = ?) OR (type = ?)", new String[]{"1", "0", "2"}, "date desc");
		if (null == cur || !cur.moveToNext()) {
			return;
		}
		cur.moveToFirst();
		String id = cur.getString(cur.getColumnIndex("_id"));//短信内容
		if (list.contains(id)) return;
		String number = cur.getString(cur.getColumnIndex("address"));//手机号
		String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
		String body = cur.getString(cur.getColumnIndex("body"));//短信内容
		int type = cur.getInt(cur.getColumnIndex("type"));//类型 1:接受 2:发送
		long date = cur.getLong(cur.getColumnIndex("date"));//发生时间
		SmsInfo smsInfo = new SmsInfo(id, number, name, body, type, date);
//		XLog.e(smsInfo.toString());
		
		StringCallback stringCallback = new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				XLog.e("error: " + Log.getStackTraceString(e));
				ToastUtils.showShort("同步失败，请检查网络后重新再试");
			}
			
			@Override
			public void onResponse(String response, int id) {
				XLog.d("response: " + response);
				JSONObject jsonObject = JSONObject.parseObject(response);
				if (jsonObject.getBoolean("flag")) {
					list.add(id);
				}
			}
		};
		UploadUtil.sendToBack(smsInfo, stringCallback);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.getContentResolver().unregisterContentObserver(mObserver);
		if (myReceiver != null) {
			unregisterReceiver(myReceiver);
			myReceiver = null;
		}
		Process.killProcess(Process.myPid());
	}
}
