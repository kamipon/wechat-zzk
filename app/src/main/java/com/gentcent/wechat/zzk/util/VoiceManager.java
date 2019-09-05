package com.gentcent.wechat.zzk.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.blankj.utilcode.constant.MemoryConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VoiceManager {
	public static int amrToMp3(String amrPath, String mp3Path) {
		VoiceJNI.init();
		XLog.d("VoiceTools amr_to_mp3 org :" + amrPath + ",dest:" + mp3Path);
		if (VoiceJNI.init_finish) {
			return VoiceJNI.amr_to_mp3(amrPath, mp3Path);
		}
		XLog.d("VoiceTools VoiceJNI.init_finish :" + VoiceJNI.init_finish);
		return -1;
	}
	
	
	public static boolean a(String str) {
		File file = new File(str);
		if (!file.exists()) {
			XLog.d("VoiceManager isNeedCopy :true");
			return true;
		}
		try {
			String filemd5 = MyHelper.a(new FileInputStream(file));
			XLog.d("VoiceManager isNeedCopy: filemd5:" + filemd5);
			if (filemd5.equals("2165fce1c4d60bc5f97ffaa7be0d283b")) {
				XLog.d("VoiceManager" + "isNeedCopy :false");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void a(Context context) {
		if (a("/sdcard/more-chat/libsilk.so")) {
			b(context);
		}
	}
	
	public static void b(Context context) {
		InputStream inputStream;
		XLog.d("VoiceManager" + "creating file /sdcard/more-chat/libsilk.so from libsilk.so");
		try {
			AssetManager assets = context.getAssets();
			File file = new File("/sdcard/more-chat/libsilk.so");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			inputStream = assets.open("libsilk.so");
			XLog.d("VoiceManager" + "Creating outputstream");
			FileOutputStream fileOutputStream = new FileOutputStream(file, false);
			a(inputStream, fileOutputStream);
			fileOutputStream.close();
		} catch (Throwable th) {
			XLog.d("error:" + Log.getStackTraceString(th));
		}
	}
	
	private static void a(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] bArr = new byte[2048];
		while (true) {
			int read = inputStream.read(bArr);
			if (read != -1) {
				outputStream.write(bArr, 0, read);
			} else {
				return;
			}
		}
	}
	
	public static void a() {
		boolean a = a("/sdcard/more-chat/libsilk.so", VoiceJNI.WxSoPath);
		StringBuilder sb = new StringBuilder();
		sb.append("moveSo finish:");
		sb.append(a);
		XLog.d("VoiceManager" + sb.toString());
	}
	
	public static boolean a(String str, String str2) {
		try {
			File file = new File(str);
			if (!file.exists()) {
				XLog.d("VoiceManager" + "copyFile:  oldFile not exist.");
				return false;
			} else if (!file.isFile()) {
				XLog.d("VoiceManager" + "copyFile:  oldFile not file.");
				return false;
			} else if (!file.canRead()) {
				XLog.d("VoiceManager" + "copyFile:  oldFile cannot read.");
				return false;
			} else {
				FileInputStream fileInputStream = new FileInputStream(str);
				FileOutputStream fileOutputStream = new FileOutputStream(str2);
				byte[] bArr = new byte[MemoryConstants.KB];
				while (true) {
					int read = fileInputStream.read(bArr);
					if (-1 != read) {
						fileOutputStream.write(bArr, 0, read);
					} else {
						fileInputStream.close();
						fileOutputStream.flush();
						fileOutputStream.close();
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
