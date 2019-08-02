package com.gentcent.wechat.zzk.model.chatroom.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatRoomRedPocketMemberBean implements Parcelable {
    public static final Creator<ChatRoomRedPocketMemberBean> CREATOR = new Creator<ChatRoomRedPocketMemberBean>() {
        public ChatRoomRedPocketMemberBean createFromParcel(Parcel parcel) {
            return new ChatRoomRedPocketMemberBean(parcel);
        }

        public ChatRoomRedPocketMemberBean[] newArray(int i) {
            return new ChatRoomRedPocketMemberBean[i];
        }
    };
    String moneyNumber;
    String note;
    String time;
    String userName;
    String userNick;

    public int describeContents() {
        return 0;
    }

    public ChatRoomRedPocketMemberBean(String str, String str2, String str3, String str4, String str5) {
        this.userName = str;
        this.note = str2;
        this.userNick = str3;
        this.moneyNumber = str4;
        this.time = str5;
    }

    protected ChatRoomRedPocketMemberBean(Parcel parcel) {
        this.userName = parcel.readString();
        this.note = parcel.readString();
        this.userNick = parcel.readString();
        this.moneyNumber = parcel.readString();
        this.time = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userName);
        parcel.writeString(this.note);
        parcel.writeString(this.userNick);
        parcel.writeString(this.moneyNumber);
        parcel.writeString(this.time);
    }
}
