package com.gentcent.wechat.zzk.activity.keepalive.pixe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenReceiverUtil {
	private Context a;
	private ScreenBroadcastReceiver b;
	/* access modifiers changed from: private */
	public C0415a c;
	
	public class ScreenBroadcastReceiver extends BroadcastReceiver {
		public ScreenBroadcastReceiver() {
		}
		
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			StringBuilder sb = new StringBuilder();
			sb.append("SreenLockReceiver-->监听到系统广播：");
			sb.append(action);
			Log.d("KeepAppAlive", sb.toString());
			if (ScreenReceiverUtil.this.c != null) {
				if ("android.intent.action.SCREEN_ON".equals(action)) {
					ScreenReceiverUtil.this.c.onScreenOn();
				} else if ("android.intent.action.SCREEN_OFF".equals(action)) {
					ScreenReceiverUtil.this.c.onScreenOff();
				} else if ("android.intent.action.USER_PRESENT".equals(action)) {
					ScreenReceiverUtil.this.c.onUserPresent();
				}
			}
		}
	}
	
	/* renamed from: android.support.v7.widget.keepalive.pixe.ScreenReceiverUtil$a */
	public interface C0415a {
		void onScreenOff();
		
		void onScreenOn();
		
		void onUserPresent();
	}
	
	public ScreenReceiverUtil(Context context) {
		this.a = context;
	}
	
	public void a(C0415a aVar) {
		this.c = aVar;
		this.b = new ScreenBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		intentFilter.addAction("android.intent.action.SCREEN_OFF");
		intentFilter.addAction("android.intent.action.USER_PRESENT");
		this.a.registerReceiver(this.b, intentFilter);
	}
}
