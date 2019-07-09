package com.gentcent.wechat.enhancement;

import android.app.Application;
import android.content.Context;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class MyApplication extends Application {
	
	private static Context context;
	
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
	}
	
	public static Context getAppContext() {
		return MyApplication.context;
	}
}
