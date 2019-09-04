package com.gentcent.wechat.zzk;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.VoiceManager;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class MyApplication extends Application {
	private static final String TAG = "MyApplication:  ";
	
	private static Context context;
	
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
		
//		ThreadPoolUtils.getInstance().run(new Runnable() {
//			public void run() {
//				XLog.d(TAG + "app_releaseSo 1");
//				VoiceManager.a(MyApplication.this);
//				XLog.d(TAG + "app_releaseSo 2");
//			}
//		});
		
		//初始化极光推送
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		XLog.e("Jpush id:" + JPushInterface.getRegistrationID(context));
		XLog.e("Phone id:" + MyHelper.readLine("phone-id", ""));
	}
	
	public static Context getAppContext() {
		return MyApplication.context;
	}
}
