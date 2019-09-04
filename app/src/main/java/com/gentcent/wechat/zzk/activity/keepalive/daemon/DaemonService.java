package com.gentcent.wechat.zzk.activity.keepalive.daemon;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;

import com.gentcent.wechat.zzk.R;


public class DaemonService extends Service {
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public int onStartCommand(Intent intent, int i, int i2) {
		return 1;
	}
	
	public void onCreate() {
		super.onCreate();
		if (VERSION.SDK_INT >= 18) {
			Builder builder = new Builder(this);
			builder.setSmallIcon(R.drawable.icon_yun);
			builder.setContentTitle("KeepAppAlive");
			builder.setContentText("DaemonService is runing...");
			startForeground(100, builder.build());
			startService(new Intent(this, CancelNoticeService.class));
			return;
		}
		startForeground(100, new Notification());
	}
	
	public void onDestroy() {
		super.onDestroy();
		if (VERSION.SDK_INT >= 18) {
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(100);
		}
		startService(new Intent(getApplicationContext(), DaemonService.class));
	}
}
