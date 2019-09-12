package com.gentcent.wechat.zzk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.concurrent.TimeUnit;

/**
 * zzk广播接收器
 */
public class ZzkReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, final Intent intent) {
		try {
			if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				ThreadPoolUtils.getInstance().a(new Runnable() {
					@Override
					public void run() {
						boolean isZzkopen = Boolean.valueOf(MyHelper.readLine("isZzkOpen", "false"));
						boolean isWechatOpen = Boolean.valueOf(MyHelper.readLine("isWechatOpen", "false"));
						int isusual = isZzkopen && isWechatOpen ? 1 : 0;
						UploadService.sendStatus(isusual);
					}
				}, 30000, TimeUnit.MILLISECONDS);
				
			}
			if (intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int battery;
				if (level > 80) {
					battery = 4;
				} else if (level > 50) {
					battery = 3;
				} else if (level > 30) {
					battery = 2;
				} else if (level > 10) {
					battery = 1;
				} else {
					battery = 0;
				}
				MyHelper.writeLine("battery", battery + "");
			}
			
			String act = intent.getStringExtra("act");
			if (act != null) {
				switch (act) {
					case "is_zzk_open":
						ThreadPoolUtils.getInstance().run(new Runnable() {
							@Override
							public void run() {
								MyHelper.writeLine("isZzkOpen", "true");
							}
						});
						break;
					default: {
						XLog.e("广播参数传递错误。");
					}
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + Log.getStackTraceString(e));
		}
	}
}
