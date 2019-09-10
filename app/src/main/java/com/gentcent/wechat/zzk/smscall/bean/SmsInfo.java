package com.gentcent.wechat.zzk.smscall.bean;

/**
 * 主要用于短信拦截
 *
 * @author Javen
 */
public class SmsInfo {
	public String _id = "";
	public String smsAddress = "";
	public String smsName = "";
	public String smsBody = "";
	public int type = -1;
	public long date = 0l;
	
	public SmsInfo(String _id, String smsAddress, String smsName, String smsBody, int type, long date) {
		this._id = _id;
		this.smsAddress = smsAddress;
		this.smsName = smsName;
		this.smsBody = smsBody;
		this.type = type - 1;
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "SmsInfo{" +
				"_id='" + _id + '\'' +
				", smsAddress='" + smsAddress + '\'' +
				", smsName='" + smsName + '\'' +
				", smsBody='" + smsBody + '\'' +
				", type='" + type + '\'' +
				", date=" + date +
				'}';
	}
}