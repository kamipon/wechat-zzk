package com.gentcent.wechat.zzk.model.wallet;

import android.util.Log;

import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.ArrayList;

public class Bankcard {
	private static final String TAG = "MyBankcard";
	
	public static class Card {
		String bankName;
		String bankPhone;
		int bankcardTag;
		String bankcardTail;
		String bankcardType;
		String bankcardTypeName;
		String bindSerial;
		double dayQuotaKind;
		String mobile;
		double onceQuotaKind;
		
		public String toString() {
			return "Card{bankcardTag=" +
					this.bankcardTag +
					", bankName='" +
					this.bankName +
					'\'' +
					", bankcardTypeName='" +
					this.bankcardTypeName +
					'\'' +
					", bindSerial='" +
					this.bindSerial +
					'\'' +
					", mobile='" +
					this.mobile +
					'\'' +
					", bankcardTail='" +
					this.bankcardTail +
					'\'' +
					", bankPhone='" +
					this.bankPhone +
					'\'' +
					", dayQuotaKind=" +
					this.dayQuotaKind +
					", onceQuotaKind=" +
					this.onceQuotaKind +
					", bankcardType='" +
					this.bankcardType +
					'\'' +
					'}';
		}
	}
	
	public static ArrayList<Card> getBankcards(LoadPackageParam loadPackageParam) {
		ArrayList bankcardObjs = getBankcardObjs(loadPackageParam);
		ArrayList<Card> arrayList = new ArrayList<>();
		for (Object next : bankcardObjs) {
			Card card = new Card();
			card.bankcardTag = XposedHelpers.getIntField(next, "field_bankcardTag");
			card.bankName = (String) XposedHelpers.getObjectField(next, "field_bankName");
			card.bankcardTypeName = (String) XposedHelpers.getObjectField(next, "field_bankcardTypeName");
			card.bindSerial = (String) XposedHelpers.getObjectField(next, "field_bindSerial");
			card.mobile = (String) XposedHelpers.getObjectField(next, "field_mobile");
			card.bankcardTail = (String) XposedHelpers.getObjectField(next, "field_bankcardTail");
			card.bankPhone = (String) XposedHelpers.getObjectField(next, "field_bankPhone");
			card.dayQuotaKind = XposedHelpers.getDoubleField(next, "field_dayQuotaKind");
			card.onceQuotaKind = XposedHelpers.getDoubleField(next, "field_onceQuotaKind");
			card.bankcardType = (String) XposedHelpers.getObjectField(next, "field_bankcardType");
			if (!card.bankName.equals("亲属卡")) {
				arrayList.add(card);
			}
		}
		return arrayList;
	}
	
	private static ArrayList getBankcardObjs(LoadPackageParam loadPackageParam) {
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		try {
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.wallet.a.s"), "cYZ");
			Object callStaticMethod = XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.wallet.a.s"), "cIh");
			boolean booleanValue = (Boolean) XposedHelpers.callMethod(callStaticMethod, "dbK", new Object[0]);
			if (!booleanValue) {
				XposedHelpers.callMethod(callStaticMethod, "f", arrayList, arrayList2);
			} else {
				XLog.d(TAG + " isinit =" + booleanValue);
			}
		} catch (Throwable th) {
			XLog.d(TAG + "getBankcards e:" + Log.getStackTraceString(th));
		}
		return arrayList;
	}
	
	public static void test2(LoadPackageParam loadPackageParam) {
		try {
			ArrayList bankcards = getBankcards(loadPackageParam);
			XLog.d(TAG + "test2 list ：" + bankcards.size());
			for (Object bankcard : bankcards) {
				Card card = (Card) bankcard;
				XLog.d(TAG + "test2 " + card.toString());
			}
		} catch (Throwable th) {
			XLog.d(TAG + "test2 e:" + Log.getStackTraceString(th));
		}
	}
}
