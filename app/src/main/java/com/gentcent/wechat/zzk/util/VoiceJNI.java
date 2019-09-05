package com.gentcent.wechat.zzk.util;

import android.os.Build;
import android.util.Log;

public class VoiceJNI {
	private static final String TAG = "VoiceJNI";
	public static final String WxSoPath = "/data/data/com.tencent.mm/files/AM_Voice.so";
	public static boolean init_finish = false;
	
	public static native int amr_to_mp3(String str, String str2);
	
	public static native int mp3_to_amr(String str, String str2);
	
	public static void init() {
		if (!init_finish) {
			if (VoiceManager.a(WxSoPath)) {
				VoiceManager.a();
			}
			if (!VoiceManager.a(WxSoPath)) {
				try {
					XLog.d("WxSoPath is " + WxSoPath);
					XLog.d(TAG + "init load Build.CPU_ABI is " + Build.CPU_ABI + "   Build.CPU_ABI2 is " + Build.CPU_ABI2);
					System.load(WxSoPath);
					init_finish = true;
				} catch (Throwable th) {
					XLog.d("VoiceJNI init error is " + Log.getStackTraceString(th));
				}
			}
		}
	}
}
