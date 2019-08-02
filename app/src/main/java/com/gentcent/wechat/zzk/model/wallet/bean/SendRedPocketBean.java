package com.gentcent.wechat.zzk.model.wallet.bean;

import com.blankj.utilcode.util.PhoneUtils;

public class SendRedPocketBean {
	public double Amount;
	public String BindSerial;
	public String Content;
	public int ContentType;
	public String FriendWxId;
	public String Imei = PhoneUtils.getIMEI();
	public int Num;
	public String Password;
	public int RedPocketType;
}
