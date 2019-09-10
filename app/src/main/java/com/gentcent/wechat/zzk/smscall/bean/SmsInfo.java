package com.gentcent.wechat.zzk.smscall.bean;

import java.util.Objects;

/**
 * 主要用于短信拦截
 *
 * @author Javen
 */
public class SmsInfo {
	public String _id = "";
	public String smsAddress = "";
	public String smsBody = "";
	public int type = -1;
	public long date = 0l;
	
	public SmsInfo(String _id, String smsAddress, String smsBody, int type, long date) {
		this._id = _id;
		this.smsAddress = smsAddress;
		this.smsBody = smsBody;
		this.type = type - 1;
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "SmsInfo{" +
				"_id='" + _id + '\'' +
				", smsAddress='" + smsAddress + '\'' +
				", smsBody='" + smsBody + '\'' +
				", type='" + type + '\'' +
				", date=" + date +
				'}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SmsInfo smsInfo = (SmsInfo) o;
		return type == smsInfo.type &&
				date == smsInfo.date &&
				_id.equals(smsInfo._id) &&
				smsAddress.equals(smsInfo.smsAddress) &&
				smsBody.equals(smsInfo.smsBody);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(_id, smsAddress, smsBody, type, date);
	}
}