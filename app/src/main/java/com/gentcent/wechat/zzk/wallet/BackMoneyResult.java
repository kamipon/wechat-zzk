package com.gentcent.wechat.zzk.wallet;

public class BackMoneyResult {
    public String Context;
    public String Imei;
    public Boolean IsSuccess;
    public int Status;

    public BackMoneyResult(String str, Boolean bool, String str2, int i) {
        this.Imei = str;
        this.IsSuccess = bool;
        this.Context = str2;
        this.Status = i;
    }
}