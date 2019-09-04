package com.gentcent.wechat.zzk.activity.keepalive.daemon;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.SystemClock;

import com.gentcent.wechat.zzk.R;


public class CancelNoticeService extends Service {
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void onCreate() {
		super.onCreate();
	}
	
	public int onStartCommand(Intent intent, int i, int i2) {
		if (VERSION.SDK_INT > 18) {
			Builder builder = new Builder(this);
			builder.setSmallIcon(R.drawable.icon_yun);
			startForeground(100, builder.build());
			new Thread(new Runnable() {
				public void run() {
					SystemClock.sleep(1000);
					CancelNoticeService.this.stopForeground(true);
					((NotificationManager) CancelNoticeService.this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(100);
					CancelNoticeService.this.stopSelf();
				}
			}).start();
		}
		return super.onStartCommand(intent, i, i2);
	}
	
	public void onDestroy() {
		super.onDestroy();
	}
}
