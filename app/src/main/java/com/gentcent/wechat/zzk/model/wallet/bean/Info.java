package com.gentcent.wechat.zzk.model.wallet.bean;

import com.gentcent.zzk.xped.XposedHelpers;

public class Info {
	public int a;
	public int b;
	public String c;
	public String d;
	public int e;
	public int f;
	public String g;
	public String h;
	public String i;
	public String j;
	public String k;
	public String l;
	public int m;
	
	public static Info a(Object obj, String str) {
		Info aVar = new Info();
		aVar.h = str;
		aVar.a = XposedHelpers.getIntField(obj, "fCo");
		aVar.b = XposedHelpers.getIntField(obj, "fCk");
		aVar.c = (String) XposedHelpers.getObjectField(obj, "fCn");
		aVar.d = (String) XposedHelpers.getObjectField(obj, "fCm");
		aVar.e = XposedHelpers.getIntField(obj, "fCp");
		aVar.f = XposedHelpers.getIntField(obj, "cYA");
		aVar.g = (String) XposedHelpers.getObjectField(obj, "cIM");
		aVar.l = (String) XposedHelpers.getObjectField(obj, "fDu");
		return aVar;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Info{invalid_time=");
		sb.append(this.a);
		sb.append(", appmsg_type=");
		sb.append(this.b);
		sb.append(", transfer_id='");
		sb.append(this.c);
		sb.append('\'');
		sb.append(", transaction_id='");
		sb.append(this.d);
		sb.append('\'');
		sb.append(", effective_date=");
		sb.append(this.e);
		sb.append(", total_fee=");
		sb.append(this.f);
		sb.append(", fee_type='");
		sb.append(this.g);
		sb.append('\'');
		sb.append('}');
		return sb.toString();
	}
}
