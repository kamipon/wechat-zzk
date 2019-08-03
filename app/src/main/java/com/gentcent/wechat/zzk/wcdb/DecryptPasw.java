package com.gentcent.wechat.zzk.wcdb;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.util.XLog;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class DecryptPasw {
	public static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";
	
	private static final String WX_SP_UIN_PATH = WX_ROOT_PATH + "shared_prefs/auth_info_key_prefs.xml";
	
	
	/**
	 * 根据imei和uin生成的md5码，获取数据库的密码（去前七位的小写字母）
	 *
	 * @return
	 */
	public static String initDbPassword() {
		String imei = PhoneUtils.getIMEI();
		String uin = initCurrWxUin();
		XLog.d("initDbPassword:  微信数据库密码分割线===================================");
		XLog.e("initDbPassword:  " + "imei===" + imei);
		XLog.e("initDbPassword:  " + "uin===" + uin);
		try {
			if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(uin)) {
				XLog.e("initDbPassword:  " + "初始化数据库密码失败：imei或uid为空");
				return "";
			}
			String md5 = md5Encode(imei + uin);
			String password = md5.substring(0, 7).toLowerCase();
			XLog.e("initDbPassword:  " + password);
			return password;
		} catch (Exception e) {
			XLog.e("initDbPasswo rd:  " + Log.getStackTraceString(e));
		}
		return "";
	}
	
	
	/**
	 * 获取微信的uid
	 * 微信的uid存储在SharedPreferences里面
	 * 存储位置\data\data\com.tencent.mm\shared_prefs\auth_info_key_prefs.xml
	 */
	public static String initCurrWxUin() {
		String mCurrWxUin = null;
		File file = new File(WX_SP_UIN_PATH);
		try {
			FileInputStream in = new FileInputStream(file);
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			for (Element element : elements) {
				if ("_auth_uin".equals(element.attributeValue("name"))) {
					mCurrWxUin = element.attributeValue("value");
				}
			}
			return mCurrWxUin;
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("initCurrWxUin:  " + "获取微信uid失败，请检查auth_info_key_prefs文件权限");
		}
		return "";
	}
	
	/***
	 * MD5加密 生成32位md5码
	 * @param
	 * @return 返回32位md5码
	 */
	public static String md5Encode(String inStr) throws Exception {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		
		byte[] byteArray = inStr.getBytes("UTF-8");
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
