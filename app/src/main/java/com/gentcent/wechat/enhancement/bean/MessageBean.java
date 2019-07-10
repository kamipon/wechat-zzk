package com.gentcent.wechat.enhancement.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.gentcent.wechat.enhancement.MyApplication;
import com.gentcent.wechat.enhancement.wcdb.DecryptUtils;

import java.io.Serializable;

/**
 * @author zuozhi
 * @since 2019-07-01
 */
public class MessageBean implements Serializable {
	private String DeviceIMEI = DecryptUtils.initPhoneIMEI(MyApplication.getAppContext());
	private String MyWxId;
	private String FriendWxId;
	private String Content;
	private int Pos;
	private int Type;
	private int Interval;
	private String LinkImg;
	private String LinkTitle;
	private String LinkDescription;
	private String LinkUrl;
	private String FileName;
	private String ChatroomMemberWxId;
	private String ServiceGuid;
	
	public MessageBean() {
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
