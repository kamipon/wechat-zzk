package com.gentcent.wechat.zzk.bean;

import com.blankj.utilcode.util.PhoneUtils;

import java.io.Serializable;

/**
 * @author zuozhi
 * @since 2019-07-01
 */
public class SendMessageBean implements Serializable {
	//手机imei
	private String DeviceIMEI = PhoneUtils.getIMEI();
	//我的微信id
	private String MyWxId;
	//好友的微信id
	private String FriendWxId;
	//发送的消息内容
	private String Content;
	//TODO:?
	private int Pos;
	/**
	 * 消息类型
	 * 0:文本
	 * 1：图片
	 * 2：语音
	 * 3：视频
	 * 7：链接
	 */
	private int Type;
	private int Interval;
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
	
	private String ChatroomMemberWxId;
	//TODO:服务器ID
	private String ServiceGuid;
	
	public SendMessageBean() {
	}
	
	public String getDeviceIMEI() {
		return DeviceIMEI;
	}
	
	public void setDeviceIMEI(String deviceIMEI) {
		DeviceIMEI = deviceIMEI;
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
	
	public int getPos() {
		return Pos;
	}
	
	public void setPos(int pos) {
		Pos = pos;
	}
	
	public int getType() {
		return Type;
	}
	
	public void setType(int type) {
		Type = type;
	}
	
	public int getInterval() {
		return Interval;
	}
	
	public void setInterval(int interval) {
		Interval = interval;
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
				"FriendWxId='" + FriendWxId + '\'' +
				", Content='" + Content + '\'' +
				", Type=" + Type +
				'}';
	}
}
