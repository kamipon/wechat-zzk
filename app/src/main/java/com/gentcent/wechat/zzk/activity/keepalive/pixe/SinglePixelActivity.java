package com.gentcent.wechat.zzk.activity.keepalive.pixe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.gentcent.wechat.zzk.activity.MainActivity;
import com.gentcent.wechat.zzk.activity.keepalive.SystemUtils;

public class SinglePixelActivity extends AppCompatActivity {
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Window window = getWindow();
		window.setGravity(51);
		LayoutParams attributes = window.getAttributes();
		attributes.x = 0;
		attributes.y = 0;
		attributes.height = 300;
		attributes.width = 300;
		window.setAttributes(attributes);
		ScreenManager.a((Context) this).a(this);
	}
	
	public void onDestroy() {
		if (!SystemUtils.a(this, "com.gentcent.wechat.zzk")) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			Log.i("SinglePixelActivity", "SinglePixelActivity---->APP被干掉了，我要重启它");
		}
		super.onDestroy();
	}
}
