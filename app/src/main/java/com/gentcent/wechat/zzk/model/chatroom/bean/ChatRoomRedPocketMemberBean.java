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

    public ChatRoomRedPocketMemberBean(String userName, String note, String userNick, String moneyNumber, String time) {
        this.userName = userName;
        this.note = note;
        this.userNick = userNick;
        this.moneyNumber = moneyNumber;
        this.time = time;
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
