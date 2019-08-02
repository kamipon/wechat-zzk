package com.gentcent.wechat.zzk.model.message.bean;

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
		/* access modifiers changed from: private */
		public String ChatroomMemberWxId;
		/* access modifiers changed from: private */
		public String Content;
		/* access modifiers changed from: private */
		public String DeviceIMEI = PhoneUtils.getIMEI();
		/* access modifiers changed from: private */
		public String FileName;
		/* access modifiers changed from: private */
		public int FileSize;
		/* access modifiers changed from: private */
		public String FriendAlias;
		/* access modifiers changed from: private */
		public String FriendConRemark;
		/* access modifiers changed from: private */
		public String FriendHeadMaxImg;
		/* access modifiers changed from: private */
		public String FriendHeadMinImg;
		/* access modifiers changed from: private */
		public String FriendNickName;
		/* access modifiers changed from: private */
		public String FriendWxId;
		public String GroupID;
		/* access modifiers changed from: private */
		public int Interval;
		/* access modifiers changed from: private */
		public String LinkDescription;
		/* access modifiers changed from: private */
		public String LinkImg;
		/* access modifiers changed from: private */
		public String LinkTitle;
		/* access modifiers changed from: private */
		public String LinkUrl;
		public List<ChatRoomRedPocketMemberBean> MemberInfo;
		/* access modifiers changed from: private */
		public String MessageId;
		/* access modifiers changed from: private */
		public String Money;
		/* access modifiers changed from: private */
		public int MoneyStatus;
		/* access modifiers changed from: private */
		public String MyHeadMaxImg;
		/* access modifiers changed from: private */
		public String MyHeadMinImg;
		/* access modifiers changed from: private */
		public String MyNickName;
		/* access modifiers changed from: private */
		public String MyWxId = MyHelper.readLine("myWechatID", "");
		/* access modifiers changed from: private */
		public int Pos;
		/* access modifiers changed from: private */
		public String Province;
		/* access modifiers changed from: private */
		public String Region;
		/* access modifiers changed from: private */
		public int SendGroupType = 0;
		/* access modifiers changed from: private */
		public String ServiceGuid;
		/* access modifiers changed from: private */
		public int Sex;
		/* access modifiers changed from: private */
		public String SourceTxt;
		/* access modifiers changed from: private */
		public int SourceType;
		/* access modifiers changed from: private */
		public int Status;
		/* access modifiers changed from: private */
		public int Timestamp;
		/* access modifiers changed from: private */
		public int Type;
		public List<ContentMessageBean> contentMessageList = new ArrayList();
		
		public static MessageBeanBuilder newinstance() {
			return new MessageBeanBuilder();
		}
		
		public MessageBeanBuilder buildArticle(int i, String str, String str2, String str3, String str4, String str5) {
			this.FriendWxId = str;
			this.Pos = i;
			this.Type = 7;
			this.LinkTitle = str2;
			this.LinkImg = str3;
			this.LinkUrl = str4;
			this.LinkDescription = str5;
			if (ObjectUtils.isNotEmpty((CharSequence) str4) && str4.startsWith("https://mp.weixin.qq.com/mp/waerrpage?appid=")) {
				this.Type = 0;
				this.Content = "收到一个小程序，请在手机上查看";
			}
			return this;
		}
		
		public MessageBeanBuilder builderMoenyMessageBean(String str, int i, String str2, String str3, String str4, String str5, int i2, int i3) {
			StringBuilder sb = new StringBuilder();
			sb.append("url:: ");
			sb.append(str5);
			Log.e("Xposed", sb.toString());
			this.MessageId = str;
			this.FriendWxId = str2;
			this.Content = str3;
			this.Pos = i;
			this.Money = str4;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.LinkUrl = str5;
			this.MoneyStatus = i3;
			this.FileSize = i2;
			if (str5 != null) {
				if (str5.startsWith("wxpay://")) {
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
		
		public MessageBeanBuilder builderGroupMoneyMessageBean(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, int i2, int i3, List<ChatRoomRedPocketMemberBean> list) {
			this.MessageId = str;
			this.FriendWxId = str2;
			this.Content = str4;
			this.LinkDescription = str6;
			this.Pos = i;
			this.ChatroomMemberWxId = str3;
			this.Money = str5;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.LinkUrl = str7;
			this.MoneyStatus = i3;
			this.FileSize = i2;
			this.Type = 5;
			this.MemberInfo = list;
			return this;
		}
		
		public MessageBeanBuilder builderReMoenyMessageBean(String str, int i, String str2, String str3, int i2, int i3, int i4) {
			this.MessageId = str;
			this.FriendWxId = str2;
			this.Pos = i;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.MoneyStatus = i2;
			this.Type = i3;
			this.ChatroomMemberWxId = str3;
			this.FileSize = i4;
			return this;
		}
		
		public MessageBeanBuilder builderSysMoenyMessageBean(String str, int i, String str2, int i2, int i3) {
			this.MessageId = str;
			this.FriendWxId = str2;
			this.Pos = i;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.MoneyStatus = i2;
			this.Type = i3;
			return this;
		}
		
		public MessageBeanBuilder builderSendGroupMessage(String str, String str2, String str3, int i, String str4) {
			this.DeviceIMEI = str;
			this.MyWxId = str2;
			this.FriendWxId = str3;
			this.Pos = i;
			this.GroupID = str4;
			return this;
		}
		
		public MessageBeanBuilder builderTypeAndContent(String str, int i) {
			this.Type = i;
			this.Content = str;
			this.contentMessageList.add(new ContentMessageBean(i, str));
			return this;
		}
		
		public MessageBeanBuilder builderSystemMessage(String str, String str2, String str3) {
			this.MyWxId = str;
			this.FriendWxId = str2;
			this.Content = str3;
			this.Pos = -1;
			this.Type = 99;
			return this;
		}
		
		public MessageBean build() {
			return new MessageBean(this);
		}
		
		public MessageBeanBuilder builderSendRequestsBackMessageBean(String str, String str2, int i, int i2, String str3) {
			this.Status = i2;
			this.ServiceGuid = str;
			this.MessageId = str2;
			this.FriendWxId = str3;
			this.Pos = 0;
			this.Type = i;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			return this;
		}
		
		public MessageBeanBuilder builderSendAddVisitingCardMessageBean(String str, String str2, int i, String str3, String str4, String str5, String str6, String str7, int i2) {
			this.MessageId = str3;
			this.FriendWxId = str;
			this.ChatroomMemberWxId = str2;
			if (i == 0) {
				this.Pos = 1;
			} else if (i == 1) {
				this.Pos = 0;
			}
			this.CardIsFriend = i2;
			this.Content = str7;
			this.Type = 11;
			this.Timestamp = (int) (System.currentTimeMillis() / 1000);
			this.CardSmallHeadImgUrl = str4;
			this.CardUserName = str5;
			this.CardNickname = str6;
			return this;
		}
	}
	
	public int describeContents() {
		return 0;
	}
	
	public MessageBean(String str, String str2, String str3, String str4, int i, int i2) {
		this.DeviceIMEI = str;
		this.MyWxId = str2;
		this.FriendWxId = str3;
		this.Content = str4;
		this.Pos = i;
		this.Type = i2;
	}
	
	public MessageBean(String str, String str2, String str3, String str4, int i, int i2, String str5, String str6, String str7, String str8, String str9, String str10) {
		this.DeviceIMEI = str;
		this.MyWxId = str2;
		this.FriendWxId = str3;
		this.Content = str4;
		this.Pos = i;
		this.Type = i2;
		this.MyNickName = str5;
		this.MyHeadMaxImg = str6;
		this.MyHeadMinImg = str7;
		this.FriendNickName = str8;
		this.FriendHeadMaxImg = str9;
		this.FriendHeadMinImg = str10;
	}
	
	public MessageBean(String str, String str2, String str3, String str4, String str5, int i, int i2) {
		this.DeviceIMEI = str;
		this.MyWxId = str2;
		this.FriendWxId = str3;
		this.Content = str5;
		this.Pos = i;
		this.Type = i2;
		this.ChatroomMemberWxId = str4;
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
