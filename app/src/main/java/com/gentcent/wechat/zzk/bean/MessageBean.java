package com.gentcent.wechat.zzk.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.model.chatroom.bean.ChatRoomRedPocketMemberBean;
import com.gentcent.wechat.zzk.util.MyHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageBean implements Parcelable {
	public static final Creator<MessageBean> CREATOR = new Creator<MessageBean>() {
		public MessageBean createFromParcel(Parcel parcel) {
			return new MessageBean(parcel);
		}
		
		public MessageBean[] newArray(int i) {
			return new MessageBean[i];
		}
	};
	public int CardIsFriend;
	public String CardNickname;
	public String CardSmallHeadImgUrl;
	public String CardUserName;
	public String ChatroomMemberWxId;
	public String Content;
	public String DeviceIMEI;
	public String FileName;
	public int FileSize;
	public String FriendAlias;
	public String FriendConRemark;
	public String FriendHeadMaxImg;
	public String FriendHeadMinImg;
	public int FriendInterval;
	public String FriendNickName;
	public String FriendWxId;
	public String GroupID;
	public int Interval;
	public boolean IsOpen;
	public String LinkDescription;
	public String LinkImg;
	public String LinkTitle;
	public String LinkUrl;
	public List<ChatRoomRedPocketMemberBean> MemberInfo;
	public String MessageId;
	public String Money;
	public int MoneyStatus;
	public int MsgInterval;
	public String MyHeadMaxImg;
	public String MyHeadMinImg;
	public String MyNickName;
	public String MyWxId;
	public int Pos;
	public String Province;
	public String Region;
	public int SendGroupType = 0;
	public String ServiceGuid;
	public int Sex;
	public String SourceTxt;
	public int SourceType;
	public int Status;
	public int Timestamp;
	public int Type;
	public List<ContentMessageBean> contentMessageList;
	
	public static class MessageBeanBuilder {
		private static final int RECEVER = 1;
		private static final int SEND = 0;
		public int CardIsFriend;
		public String CardNickname;
		public String CardSmallHeadImgUrl;
		public String CardUserName;
		public String ChatroomMemberWxId;
		public String Content;
		public String DeviceIMEI = PhoneUtils.getIMEI();
		public String FileName;
		public int FileSize;
		public String FriendAlias;
		public String FriendConRemark;
		public String FriendHeadMaxImg;
		public String FriendHeadMinImg;
		public String FriendNickName;
		public String FriendWxId;
		public String GroupID;
		public int Interval;
		public String LinkDescription;
		public String LinkImg;
		public String LinkTitle;
		public String LinkUrl;
		public List<ChatRoomRedPocketMemberBean> MemberInfo;
		public String MessageId;
		public String Money;
		public int MoneyStatus;
		public String MyHeadMaxImg;
		public String MyHeadMinImg;
		public String MyNickName;
		public String MyWxId = MyHelper.readLine("myWechatID", "");
		public int Pos;
		public String Province;
		public String Region;
		public int SendGroupType = 0;
		public String ServiceGuid;
		public int Sex;
		public String SourceTxt;
		public int SourceType;
		public int Status;
		public int Timestamp;
		public int Type;
		public List<ContentMessageBean> contentMessageList = new ArrayList();
		
		public static MessageBeanBuilder newinstance() {
			return new MessageBeanBuilder();
		}
		
		public MessageBeanBuilder buildArticle(int Pos, String FriendWxId, String LinkTitle, String LinkImg, String LinkUrl, String LinkDescription) {
			this.FriendWxId = FriendWxId;
			this.Pos = Pos;
			this.Type = 7;
			this.LinkTitle = LinkTitle;
			this.LinkImg = LinkImg;
			this.LinkUrl = LinkUrl;
			this.LinkDescription = LinkDescription;
			if (ObjectUtils.isNotEmpty(LinkUrl) && LinkUrl.startsWith("https://mp.weixin.qq.com/mp/waerrpage?appid=")) {
				this.Type = 0;
				this.Content = "收到一个小程序，请在手机上查看";
			}
			return this;
		}
		
		public MessageBeanBuilder builderMoenyMessageBean(String MessageId, int Pos, String FriendWxId, String Content, String Money, String LinkUrl, int FileSize, int MoneyStatus) {
			Log.e("zzk", "url:: " + LinkUrl);
			this.MessageId = MessageId;
			this.FriendWxId = FriendWxId;
			this.Content = Content;
			this.Pos = Pos;
			this.Money = Money;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.LinkUrl = LinkUrl;
			this.MoneyStatus = MoneyStatus;
			this.FileSize = FileSize;
			if (LinkUrl != null) {
				if (LinkUrl.startsWith("wxpay://")) {
					this.Type = 5;
				} else {
					this.Type = 6;
				}
				StringBuilder sb2 = new StringBuilder();
				sb2.append("builderMoenyMessageBean  Type:: ");
				sb2.append(this.Type);
				Log.e("Xposed", sb2.toString());
			} else {
				this.Type = 6;
			}
			return this;
		}
		
		public MessageBeanBuilder builderGroupMoneyMessageBean(String MessageId, int Pos, String FriendWxId, String ChatroomMemberWxId, String Content, String Money, String LinkDescription, String LinkUrl, int FileSize, int MoneyStatus, List<ChatRoomRedPocketMemberBean> MemberInfoList) {
			this.MessageId = MessageId;
			this.FriendWxId = FriendWxId;
			this.Content = Content;
			this.LinkDescription = LinkDescription;
			this.Pos = Pos;
			this.ChatroomMemberWxId = ChatroomMemberWxId;
			this.Money = Money;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.LinkUrl = LinkUrl;
			this.MoneyStatus = MoneyStatus;
			this.FileSize = FileSize;
			this.Type = 5;
			this.MemberInfo = MemberInfoList;
			return this;
		}
		
		public MessageBeanBuilder builderReMoenyMessageBean(String MessageId, int Pos, String FriendWxId, String ChatroomMemberWxId, int MoneyStatus, int Type, int FileSize) {
			this.MessageId = MessageId;
			this.FriendWxId = FriendWxId;
			this.Pos = Pos;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.MoneyStatus = MoneyStatus;
			this.Type = Type;
			this.ChatroomMemberWxId = ChatroomMemberWxId;
			this.FileSize = FileSize;
			return this;
		}
		
		public MessageBeanBuilder builderSysMoenyMessageBean(String MessageId, int Pos, String FriendWxId, int MoneyStatus, int Type) {
			this.MessageId = MessageId;
			this.FriendWxId = FriendWxId;
			this.Pos = Pos;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.MoneyStatus = MoneyStatus;
			this.Type = Type;
			return this;
		}
		
		public MessageBeanBuilder builderSendGroupMessage(String DeviceIMEI, String MyWxId, String FriendWxId, int Pos, String GroupID) {
			this.DeviceIMEI = DeviceIMEI;
			this.MyWxId = MyWxId;
			this.FriendWxId = FriendWxId;
			this.Pos = Pos;
			this.GroupID = GroupID;
			return this;
		}
		
		public MessageBeanBuilder builderTypeAndContent(String Content, int Type) {
			this.Type = Type;
			this.Content = Content;
			this.contentMessageList.add(new ContentMessageBean(Type, Content));
			return this;
		}
		
		public MessageBeanBuilder builderSystemMessage(String MyWxId, String FriendWxId, String Content) {
			this.MyWxId = MyWxId;
			this.FriendWxId = FriendWxId;
			this.Content = Content;
			this.Pos = -1;
			this.Type = 99;
			return this;
		}
		
		public MessageBean build() {
			return new MessageBean(this);
		}
		
		public MessageBeanBuilder builderSendRequestsBackMessageBean(String ServiceGuid, String MessageId, int Type, int Status, String FriendWxId) {
			this.Status = Status;
			this.ServiceGuid = ServiceGuid;
			this.MessageId = MessageId;
			this.FriendWxId = FriendWxId;
			this.Pos = 0;
			this.Type = Type;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			return this;
		}
		
		public MessageBeanBuilder builderSendAddVisitingCardMessageBean(String FriendWxId, String ChatroomMemberWxId, int i, String MessageId, String CardSmallHeadImgUrl, String CardUserName, String CardNickname, String Content, int CardIsFriend) {
			this.MessageId = MessageId;
			this.FriendWxId = FriendWxId;
			this.ChatroomMemberWxId = ChatroomMemberWxId;
			if (i == 0) {
				this.Pos = 1;
			} else if (i == 1) {
				this.Pos = 0;
			}
			this.CardIsFriend = CardIsFriend;
			this.Content = Content;
			this.Type = 11;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.CardSmallHeadImgUrl = CardSmallHeadImgUrl;
			this.CardUserName = CardUserName;
			this.CardNickname = CardNickname;
			return this;
		}
	}
	
	public int describeContents() {
		return 0;
	}
	
	public MessageBean(String DeviceIMEI, String MyWxId, String FriendWxId, String Content, int Pos, int Type) {
		this.DeviceIMEI = DeviceIMEI;
		this.MyWxId = MyWxId;
		this.FriendWxId = FriendWxId;
		this.Content = Content;
		this.Pos = Pos;
		this.Type = Type;
	}
	
	public MessageBean(String DeviceIMEI, String MyWxId, String FriendWxId, String Content, int Pos, int Type, String MyNickName, String MyHeadMaxImg, String MyHeadMinImg, String FriendNickName, String FriendHeadMaxImg, String FriendHeadMinImg) {
		this.DeviceIMEI = DeviceIMEI;
		this.MyWxId = MyWxId;
		this.FriendWxId = FriendWxId;
		this.Content = Content;
		this.Pos = Pos;
		this.Type = Type;
		this.MyNickName = MyNickName;
		this.MyHeadMaxImg = MyHeadMaxImg;
		this.MyHeadMinImg = MyHeadMinImg;
		this.FriendNickName = FriendNickName;
		this.FriendHeadMaxImg = FriendHeadMaxImg;
		this.FriendHeadMinImg = FriendHeadMinImg;
	}
	
	public MessageBean(String DeviceIMEI, String MyWxId, String FriendWxId, String ChatroomMemberWxId, String Content, int Pos, int Type) {
		this.DeviceIMEI = DeviceIMEI;
		this.MyWxId = MyWxId;
		this.FriendWxId = FriendWxId;
		this.Content = Content;
		this.Pos = Pos;
		this.Type = Type;
		this.ChatroomMemberWxId = ChatroomMemberWxId;
	}
	
	public MessageBean(MessageBeanBuilder messageBeanBuilder) {
		this.MemberInfo = messageBeanBuilder.MemberInfo;
		this.CardIsFriend = messageBeanBuilder.CardIsFriend;
		this.GroupID = messageBeanBuilder.GroupID;
		this.CardSmallHeadImgUrl = messageBeanBuilder.CardSmallHeadImgUrl;
		this.CardUserName = messageBeanBuilder.CardUserName;
		this.CardNickname = messageBeanBuilder.CardNickname;
		this.ServiceGuid = messageBeanBuilder.ServiceGuid;
		this.SendGroupType = messageBeanBuilder.SendGroupType;
		this.MessageId = messageBeanBuilder.MessageId;
		this.DeviceIMEI = messageBeanBuilder.DeviceIMEI;
		this.MyWxId = messageBeanBuilder.MyWxId;
		this.FriendWxId = messageBeanBuilder.FriendWxId;
		this.FriendAlias = messageBeanBuilder.FriendAlias;
		this.Content = messageBeanBuilder.Content;
		this.Pos = messageBeanBuilder.Pos;
		this.Type = messageBeanBuilder.Type;
		this.Interval = messageBeanBuilder.Interval;
		this.ChatroomMemberWxId = messageBeanBuilder.ChatroomMemberWxId;
		this.Timestamp = messageBeanBuilder.Timestamp;
		this.Money = messageBeanBuilder.Money;
		this.MyNickName = messageBeanBuilder.MyNickName;
		this.MyHeadMaxImg = messageBeanBuilder.MyHeadMaxImg;
		this.MyHeadMinImg = messageBeanBuilder.MyHeadMinImg;
		this.FriendNickName = messageBeanBuilder.FriendNickName;
		this.FriendConRemark = messageBeanBuilder.FriendConRemark;
		this.FriendHeadMaxImg = messageBeanBuilder.FriendHeadMaxImg;
		this.FriendHeadMinImg = messageBeanBuilder.FriendHeadMinImg;
		this.LinkImg = messageBeanBuilder.LinkImg;
		this.LinkTitle = messageBeanBuilder.LinkTitle;
		this.LinkDescription = messageBeanBuilder.LinkDescription;
		this.LinkUrl = messageBeanBuilder.LinkUrl;
		this.FileName = messageBeanBuilder.FileName;
		this.FileSize = messageBeanBuilder.FileSize;
		this.Sex = messageBeanBuilder.Sex;
		this.SourceType = messageBeanBuilder.SourceType;
		this.SourceTxt = messageBeanBuilder.SourceTxt;
		this.Province = messageBeanBuilder.Province;
		this.Region = messageBeanBuilder.Region;
		this.Status = messageBeanBuilder.Status;
		this.MoneyStatus = messageBeanBuilder.MoneyStatus;
		this.contentMessageList = messageBeanBuilder.contentMessageList;
	}
	
	protected MessageBean(Parcel parcel) {
		boolean z = false;
		this.MsgInterval = parcel.readInt();
		this.FriendInterval = parcel.readInt();
		this.ServiceGuid = parcel.readString();
		this.GroupID = parcel.readString();
		this.SendGroupType = parcel.readInt();
		this.MessageId = parcel.readString();
		this.DeviceIMEI = parcel.readString();
		this.MyWxId = parcel.readString();
		this.FriendWxId = parcel.readString();
		this.FriendAlias = parcel.readString();
		this.Content = parcel.readString();
		this.contentMessageList = parcel.createTypedArrayList(ContentMessageBean.CREATOR);
		this.Pos = parcel.readInt();
		this.Type = parcel.readInt();
		this.Interval = parcel.readInt();
		this.ChatroomMemberWxId = parcel.readString();
		this.Timestamp = parcel.readInt();
		this.Money = parcel.readString();
		this.MyNickName = parcel.readString();
		this.MyHeadMaxImg = parcel.readString();
		this.MyHeadMinImg = parcel.readString();
		this.FriendNickName = parcel.readString();
		this.FriendConRemark = parcel.readString();
		this.FriendHeadMaxImg = parcel.readString();
		this.FriendHeadMinImg = parcel.readString();
		this.LinkImg = parcel.readString();
		this.LinkTitle = parcel.readString();
		this.LinkDescription = parcel.readString();
		this.LinkUrl = parcel.readString();
		this.FileName = parcel.readString();
		this.FileSize = parcel.readInt();
		this.Sex = parcel.readInt();
		this.SourceType = parcel.readInt();
		this.SourceTxt = parcel.readString();
		this.Province = parcel.readString();
		this.Region = parcel.readString();
		this.Status = parcel.readInt();
		this.MoneyStatus = parcel.readInt();
		this.CardSmallHeadImgUrl = parcel.readString();
		this.CardUserName = parcel.readString();
		this.CardNickname = parcel.readString();
		this.CardIsFriend = parcel.readInt();
		if (parcel.readByte() != 0) {
			z = true;
		}
		this.IsOpen = z;
	}
	
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(this.MsgInterval);
		parcel.writeInt(this.FriendInterval);
		parcel.writeString(this.ServiceGuid);
		parcel.writeString(this.GroupID);
		parcel.writeInt(this.SendGroupType);
		parcel.writeString(this.MessageId);
		parcel.writeString(this.DeviceIMEI);
		parcel.writeString(this.MyWxId);
		parcel.writeString(this.FriendWxId);
		parcel.writeString(this.FriendAlias);
		parcel.writeString(this.Content);
		parcel.writeTypedList(this.contentMessageList);
		parcel.writeTypedList(this.MemberInfo);
		parcel.writeInt(this.Pos);
		parcel.writeInt(this.Type);
		parcel.writeInt(this.Interval);
		parcel.writeString(this.ChatroomMemberWxId);
		parcel.writeInt(this.Timestamp);
		parcel.writeString(this.Money);
		parcel.writeString(this.MyNickName);
		parcel.writeString(this.MyHeadMaxImg);
		parcel.writeString(this.MyHeadMinImg);
		parcel.writeString(this.FriendNickName);
		parcel.writeString(this.FriendConRemark);
		parcel.writeString(this.FriendHeadMaxImg);
		parcel.writeString(this.FriendHeadMinImg);
		parcel.writeString(this.LinkImg);
		parcel.writeString(this.LinkTitle);
		parcel.writeString(this.LinkDescription);
		parcel.writeString(this.LinkUrl);
		parcel.writeString(this.FileName);
		parcel.writeInt(this.FileSize);
		parcel.writeInt(this.Sex);
		parcel.writeInt(this.SourceType);
		parcel.writeString(this.SourceTxt);
		parcel.writeString(this.Province);
		parcel.writeString(this.Region);
		parcel.writeInt(this.Status);
		parcel.writeInt(this.MoneyStatus);
		parcel.writeString(this.CardSmallHeadImgUrl);
		parcel.writeString(this.CardUserName);
		parcel.writeString(this.CardNickname);
		parcel.writeInt(this.CardIsFriend);
		parcel.writeByte(this.IsOpen ? (byte) 1 : 0);
	}
}
