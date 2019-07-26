package com.gentcent.wechat.zzk;

import android.app.Application;
import android.content.Context;

import cn.jpush.android.api.JPushInterface;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class MyApplication extends Application {
	
	private static Context context;
	
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}
	
	public static Context getAppContext() {
		return MyApplication.context;
	}
}
