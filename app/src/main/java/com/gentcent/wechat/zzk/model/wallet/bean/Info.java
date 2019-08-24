package com.gentcent.wechat.zzk.model.wallet.bean;

import com.gentcent.zzk.xped.XposedHelpers;

public class Info {
	public int invalid_time;
	public int appmsg_type;
	public String transfer_id;
	public String transaction_id;
	public int effective_date;
	public int total_fee;
	public String fee_type;
	public String msgId;
	public String field_content;
	public String field_reserved;
	public String field_talker;
	public String l;
	public int field_isSend;
	
	public static Info init(Object obj, String msgId) {
		Info aVar = new Info();
		aVar.msgId = msgId;
		aVar.invalid_time = XposedHelpers.getIntField(obj, "fCo");
		aVar.appmsg_type = XposedHelpers.getIntField(obj, "fCk");
		aVar.transfer_id = (String) XposedHelpers.getObjectField(obj, "fCn");
		aVar.transaction_id = (String) XposedHelpers.getObjectField(obj, "fCm");
		aVar.effective_date = XposedHelpers.getIntField(obj, "fCp");
		aVar.total_fee = XposedHelpers.getIntField(obj, "cYA");
		aVar.fee_type = (String) XposedHelpers.getObjectField(obj, "cIM");
		aVar.l = (String) XposedHelpers.getObjectField(obj, "fDu");
		return aVar;
	}
	
	public String toString() {
		return "Info{invalid_time=" +
				this.invalid_time +
				", appmsg_type=" +
				this.appmsg_type +
				", transfer_id='" +
				this.transfer_id +
				'\'' +
				", transaction_id='" +
				this.transaction_id +
				'\'' +
				", effective_date=" +
				this.effective_date +
				", total_fee=" +
				this.total_fee +
				", fee_type='" +
				this.fee_type +
				'\'' +
				'}';
	}
}
