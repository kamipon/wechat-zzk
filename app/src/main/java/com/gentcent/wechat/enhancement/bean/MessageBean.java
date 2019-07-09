package com.gentcent.wechat.enhancement.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.gentcent.wechat.enhancement.MyApplication;
import com.gentcent.wechat.enhancement.wcdb.DecryptUtils;

/**
 * @author zuozhi
 * @since 2019-07-01
 */
public class MessageBean implements Parcelable {
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
	
	public MessageBean(String deviceIMEI, String myWxId, String friendWxId, String content, int pos, int type, int interval, String linkImg, String linkTitle, String linkDescription, String linkUrl, String fileName, String chatroomMemberWxId, String serviceGuid) {
		DeviceIMEI = deviceIMEI;
		MyWxId = myWxId;
		FriendWxId = friendWxId;
		Content = content;
		Pos = pos;
		Type = type;
		Interval = interval;
		LinkImg = linkImg;
		LinkTitle = linkTitle;
		LinkDescription = linkDescription;
		LinkUrl = linkUrl;
		FileName = fileName;
		ChatroomMemberWxId = chatroomMemberWxId;
		ServiceGuid = serviceGuid;
	}
	
	public MessageBean() {
	}
	
	protected MessageBean(Parcel in) {
		DeviceIMEI = in.readString();
		MyWxId = in.readString();
		FriendWxId = in.readString();
		Content = in.readString();
		Pos = in.readInt();
		Type = in.readInt();
		Interval = in.readInt();
		LinkImg = in.readString();
		LinkTitle = in.readString();
		LinkDescription = in.readString();
		LinkUrl = in.readString();
		FileName = in.readString();
		ChatroomMemberWxId = in.readString();
		ServiceGuid = in.readString();
	}
	
	public static final Creator<MessageBean> CREATOR = new Creator<MessageBean>() {
		@Override
		public MessageBean createFromParcel(Parcel in) {
			return new MessageBean(in);
		}
		
		@Override
		public MessageBean[] newArray(int size) {
			return new MessageBean[size];
		}
	};
	
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
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int i) {
		
		parcel.writeString(DeviceIMEI);
		parcel.writeString(MyWxId);
		parcel.writeString(FriendWxId);
		parcel.writeString(Content);
		parcel.writeInt(Pos);
		parcel.writeInt(Type);
		parcel.writeInt(Interval);
		parcel.writeString(LinkImg);
		parcel.writeString(LinkTitle);
		parcel.writeString(LinkDescription);
		parcel.writeString(LinkUrl);
		parcel.writeString(FileName);
		parcel.writeString(ChatroomMemberWxId);
		parcel.writeString(ServiceGuid);
	}
}
