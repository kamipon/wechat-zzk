package com.gentcent.wechat.zzk.bean;

import java.util.Date;

public class JPTimeBean {
    public String Imei;
    public Date MReceiveTime;
    public String MsgId;

    public JPTimeBean(String str, String str2, Date date) {
        this.Imei = str;
        this.MsgId = str2;
        this.MReceiveTime = date;
    }
}
