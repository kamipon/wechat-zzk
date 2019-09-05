package com.gentcent.wechat.zzk.bean;

public class TaskStateBean {
    public String Imei;
    public int Status;
    public int TaskId;

    public TaskStateBean(int i, String str, int i2) {
        this.TaskId = i;
        this.Imei = str;
        this.Status = i2;
    }
}
