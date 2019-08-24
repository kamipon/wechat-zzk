package com.gentcent.wechat.zzk.model.message.bean;

import android.util.Log;

import com.gentcent.wechat.zzk.util.XLog;

import java.io.Serializable;

/**
 * @author zuozhi
 * @since 2019-07-01
 */
public class MessageBean implements Serializable {
	//我的微信id
	private String MyWxId;
	//好友的微信id
	private String FriendWxId;
	//发送的消息内容
	private String Content;
	//1：发送	2:接收
	private int isSend;
	/**
	 * 消息类型
	 * 0:文本
	 * 1：图片（.gif和其他）
	 * 2：语音
	 * 3：视频
	 * 5: 微信红包
	 * 6: 微信转账
	 * 7：链接
	 * 8：文件
	 * 9：群聊
	 */
	private int Type;
	//链接图片
	private String LinkImg;
	//链接标题
	private String LinkTitle;
	//链接描述
	private String LinkDescription;
	//链接地址
	private String LinkUrl;
	//文件名
	private String FileName;
	//@好友
	private String ChatroomMemberWxId;
	
	
	//---------后端数据
	//服务器ID
	private String ServiceGuid;
	//消息ID
	private String msgId;
	//添加时间
	private long addTime;
	/*
	状态：
		0: 已发出
		1: 已收款
		2: 已退款
	 */
	private int status;
	//文件大小
	private int fileSize;
	//钱包
	private String money;
	
	
	public static MessageBean buldeMoneyMessageBean(String msgId, int isSend, String friendWxId, String content, String money, String linkUrl, int status) {
		MessageBean messageBean = new MessageBean();
		messageBean.msgId = msgId;
		messageBean.isSend = isSend;
		messageBean.FriendWxId = friendWxId;
		messageBean.Content = content;
		messageBean.money = money;
		messageBean.LinkUrl = linkUrl;
		messageBean.status = status;
		messageBean.addTime = (System.currentTimeMillis() / 1000);
		if (linkUrl != null) {
			if (linkUrl.startsWith("wxpay://")) {
				messageBean.Type = 5;
			} else {
				messageBean.Type = 6;
			}
			XLog.e("builderMoenyMessageBean  Type:: " + messageBean.Type);
		} else {
			messageBean.Type = 6;
		}
		return messageBean;
	}
	
	public String getMoney() {
		return money;
	}
	
	public void setMoney(String money) {
		this.money = money;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public MessageBean() {
	}
	
	public String getMsgId() {
		return msgId;
	}
	
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	public long getAddTime() {
		return addTime;
	}
	
	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}
	
	public String getMyWxId() {
		return MyWxId;
	}
	
	public void setMyWxId(String myWxId) {
		MyWxId = myWxId;
	}
	
	public String getFriendWxId() {
		return FriendWxId;
	}
	
	public void setFriendWxId(String friendWxId) {
		FriendWxId = friendWxId;
	}
	
	public String getContent() {
		return Content;
	}
	
	public void setContent(String content) {
		Content = content;
	}
	
	public int getIsSend() {
		return isSend;
	}
	
	public void setIsSend(int isSend) {
		this.isSend = isSend;
	}
	
	public int getType() {
		return Type;
	}
	
	public void setType(int type) {
		Type = type;
	}
	
	public String getLinkImg() {
		return LinkImg;
	}
	
	public void setLinkImg(String linkImg) {
		LinkImg = linkImg;
	}
	
	public String getLinkTitle() {
		return LinkTitle;
	}
	
	public void setLinkTitle(String linkTitle) {
		LinkTitle = linkTitle;
	}
	
	public String getLinkDescription() {
		return LinkDescription;
	}
	
	public void setLinkDescription(String linkDescription) {
		LinkDescription = linkDescription;
	}
	
	public String getLinkUrl() {
		return LinkUrl;
	}
	
	public void setLinkUrl(String linkUrl) {
		LinkUrl = linkUrl;
	}
	
	public String getFileName() {
		return FileName;
	}
	
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	
	public String getChatroomMemberWxId() {
		return ChatroomMemberWxId;
	}
	
	public void setChatroomMemberWxId(String chatroomMemberWxId) {
		ChatroomMemberWxId = chatroomMemberWxId;
	}
	
	public String getServiceGuid() {
		return ServiceGuid;
	}
	
	public void setServiceGuid(String serviceGuid) {
		ServiceGuid = serviceGuid;
	}
	
	@Override
	public String toString() {
		return "MessageBean{" +
				"MyWxId='" + MyWxId + '\'' +
				", FriendWxId='" + FriendWxId + '\'' +
				", Content='" + Content + '\'' +
				", isSend=" + isSend +
				", Type=" + Type +
				", LinkImg='" + LinkImg + '\'' +
				", LinkTitle='" + LinkTitle + '\'' +
				", LinkDescription='" + LinkDescription + '\'' +
				", LinkUrl='" + LinkUrl + '\'' +
				", FileName='" + FileName + '\'' +
				", ChatroomMemberWxId='" + ChatroomMemberWxId + '\'' +
				", ServiceGuid='" + ServiceGuid + '\'' +
				", msgId='" + msgId + '\'' +
				", addTime=" + addTime +
				", status=" + status +
				", fileSize=" + fileSize +
				", money='" + money + '\'' +
				'}';
	}
}
