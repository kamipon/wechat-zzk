package com.gentcent.wechat.zzk.activity.keepalive.pixe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gentcent.wechat.zzk.util.XLog;

import java.lang.ref.WeakReference;

/* renamed from: android.support.v7.widget.keepalive.pixe.a */
public class ScreenManager {
	private static ScreenManager b;
	private Context a;
	private WeakReference<Activity> c;
	
	private ScreenManager(Context context) {
		this.a = context;
	}
	
	public static ScreenManager a(Context context) {
		if (b == null) {
			b = new ScreenManager(context);
		}
		return b;
	}
	
	public void a(Activity activity) {
		this.c = new WeakReference<>(activity);
	}
	
	public void a() {
		XLog.d("ScreenManager" + "准备启动SinglePixelActivity...");
		Intent intent = new Intent(this.a, SinglePixelActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.a.startActivity(intent);
	}
	
	public void b() {
		XLog.d("ScreenManager" + "准备结束SinglePixelActivity...");
		WeakReference<Activity> weakReference = this.c;
		if (weakReference != null) {
			Activity activity = (Activity) weakReference.get();
			if (activity != null) {
				activity.finish();
			}
		}
	}
}
