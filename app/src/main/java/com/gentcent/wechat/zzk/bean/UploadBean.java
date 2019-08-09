package com.gentcent.wechat.zzk.bean;

import com.gentcent.wechat.zzk.model.message.bean.MessageBean;

public class UploadBean {
	public UserBean userBean;
	public MessageBean messageBean;
	public String phoneID;
	
	public UploadBean(UserBean userBean, MessageBean messageBean, String phoneID) {
		this.userBean = userBean;
		this.messageBean = messageBean;
		this.phoneID = phoneID;
	}
}
