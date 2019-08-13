package com.gentcent.wechat.zzk.util;

import android.util.Base64;
import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author zuozhi
 * @since 2019-08-01
 */
public class ZzkUtil {
	private static String[] appList = {"filehelper", "qqmail", "floatbottle", "shakeapp", "lbsapp", "medianote", "newsapp", "facebookapp", "qqfriend", "masssendapp", "feedsapp", "voipapp", "officialaccounts", "voicevoipapp", "voiceinputapp", "linkedinplugin", "notifymessage", "fmessage", "weixin", "qmessage", "tmessage"};
	
	public static Object getWxCore(LoadPackageParam loadPackageParam) {
		try {
			return XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.kernel.g"), "L", loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.messenger.foundation.a.j"));
		} catch (Throwable th) {
			XLog.e("ZzkUtil  getWxCore e:" + Log.getStackTraceString(th));
			return null;
		}
	}
	
	public static String b(String str) {
		JSONObject build = new XmlToJson.Builder(str).build();
		return build.toString();
	}
	
	public static boolean m(String str) {
		return (ObjectUtils.isNotEmpty((CharSequence) str) && str.endsWith("@chatroom")) || l(str);
	}
	
	public static boolean l(String str) {
		return ObjectUtils.isNotEmpty((CharSequence) str) && !str.startsWith("gh_") && !str.startsWith("fake_") && !str.endsWith("@chatroom") && !Arrays.asList(appList).contains(str);
	}
	
	public class MyConstant {
		public static final String PASSWORD_ENC_SECRET = "gentcent.zzk";
	}
	
	public static String a(String str, String str2) {
		try {
			int indexOf = str.indexOf("\"", str.indexOf(str2)) + 1;
			return str.substring(indexOf, str.indexOf("\"", indexOf));
		} catch (Exception unused) {
			return "";
		}
	}
	
	/**
	 * 加密
	 **/
	public static String encryptPassword(String clearText) {
		try {
			DESKeySpec keySpec = new DESKeySpec(
					MyConstant.PASSWORD_ENC_SECRET.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText
					.getBytes("UTF-8")), Base64.DEFAULT);
			return encrypedPwd.replaceAll("\r|\n", "");
		} catch (Exception e) {
		}
		return clearText;
	}
	
	/**
	 * 解密
	 **/
	public static String decryptPassword(String encryptedPwd) {
		try {
			DESKeySpec keySpec = new DESKeySpec(MyConstant.PASSWORD_ENC_SECRET.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			
			byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
			return new String(plainTextPwdBytes);
		} catch (Exception e) {
		}
		return encryptedPwd;
	}
	
	public static Object getMsgObj(LoadPackageParam loadPackageParam, long msgId) {
		try {
			XLog.d("HTools getMsgObj");
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.av"), "XE");
			return XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.c"), "VK"), "iA", msgId);
		} catch (Throwable th) {
			XLog.d("HTools  getMsgObj e:" + Log.getStackTraceString(th));
			return null;
		}
	}
}
