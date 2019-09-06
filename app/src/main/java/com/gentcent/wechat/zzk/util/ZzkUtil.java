package com.gentcent.wechat.zzk.util;

import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import org.json.JSONObject;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
			return XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.kernel.g"), "ab", loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.messenger.foundation.a.j"));
		} catch (Throwable th) {
			XLog.e("ZzkUtil  getWxCore e:" + Log.getStackTraceString(th));
			return null;
		}
	}
	
	public static String getImgPath(LoadPackageParam loadPackageParam, String str) throws ClassNotFoundException {
		String str2 = (String) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.av.o"), "alV"), "J", new Object[]{str, Boolean.TRUE});
		XLog.d("HToolsgetImgb imagepath :" + str2);
		return str2;
	}
	
	public static String xmlToJson(String xml) {
		JSONObject build = new XmlToJson.Builder(xml).build();
		return build.toString();
	}
	
	public static boolean m(String str) {
		return (ObjectUtils.isNotEmpty(str) && str.endsWith("@chatroom")) || l(str);
	}
	
	public static boolean l(String str) {
		return ObjectUtils.isNotEmpty(str) && !str.startsWith("gh_") && !str.startsWith("fake_") && !str.endsWith("@chatroom") && !Arrays.asList(appList).contains(str);
	}
	
	public static double arith(double d, double d2, int i) {
		if (i >= 0) {
			return new BigDecimal(Double.toString(d)).divide(new BigDecimal(Double.toString(d2)), i, 4).doubleValue();
		}
		throw new IllegalArgumentException("The scale must be a positive integer or zero");
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
	
	public static String reduceHtml(String str) {
		if (str == null) {
			return null;
		}
		String str2 = "";
		String str3 = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
		String str4 = "<[^>]+>";
		String str5 = "\\&[a-zA-Z]{1,10};";
		try {
			str2 = Pattern.compile(str5, 2).matcher(Pattern.compile(str4, 2).matcher(Pattern.compile(str3, 2).matcher(Pattern.compile("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", 2).matcher(str).replaceAll("")).replaceAll("")).replaceAll("")).replaceAll("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str2;
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
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.aw"), "aeU");
			return XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.c"), "acW"), "kP", msgId);
		} catch (Throwable th) {
			XLog.d("HTools  getMsgObj e:" + Log.getStackTraceString(th));
			return null;
		}
	}
	
	
	public static String GetClassFieldAndValue(Object obj) {
		StringBuilder stringBuffer = new StringBuilder();
		stringBuffer.append("GetClassFieldAndValueUtils ::");
		if (obj == null) {
			return "GetClassFieldAndValueUtils :: is null";
		}
		ArrayList arrayList = new ArrayList();
		Class cls = obj.getClass();
		while (cls != null && !cls.getName().toLowerCase().equals("java.lang.object")) {
			arrayList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass();
		}
		for (int i = 0; i < arrayList.size(); i++) {
			try {
				((Field) arrayList.get(i)).setAccessible(true);
				PrintStream printStream = System.out;
				printStream.print(((Field) arrayList.get(i)).getName() + ",");
				if (!((Field) arrayList.get(i)).getType().getName().equals(String.class.getName())) {
					if (!((Field) arrayList.get(i)).getType().getName().equals("string")) {
						if (!((Field) arrayList.get(i)).getType().getName().equals(Integer.class.getName())) {
							if (!((Field) arrayList.get(i)).getType().getName().equals("int")) {
								if (!((Field) arrayList.get(i)).getType().getName().equals(Boolean.class.getName())) {
									if (!((Field) arrayList.get(i)).getType().getName().equals("boolean")) {
										if (!((Field) arrayList.get(i)).getType().getName().equals(TextView.class.getName())) {
											if (!((Field) arrayList.get(i)).getType().getName().equals("android.widget.TextView")) {
												stringBuffer.append("  lishi-").append(((Field) arrayList.get(i)).getName()).append("-::").append(((Field) arrayList.get(i)).get(obj));
											}
										}
										TextView textView = (TextView) ((Field) arrayList.get(i)).get(obj);
										stringBuffer.append(((Field) arrayList.get(i)).getName()).append("::").append(textView.getText()).append("  ;;;  ");
									}
								}
								stringBuffer.append("boolean  :: ").append(((Field) arrayList.get(i)).getName()).append("::").append(((Field) arrayList.get(i)).getBoolean(obj)).append("  ;;;  ");
							}
						}
						stringBuffer.append("  int :: ").append(((Field) arrayList.get(i)).getName()).append("::").append(((Field) arrayList.get(i)).getInt(obj)).append("  ;;;  ");
					}
				}
				stringBuffer.append("  String :: ").append(((Field) arrayList.get(i)).getName()).append("::").append(((Field) arrayList.get(i)).get(obj)).append("  ;;;  ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}
}
