package com.gentcent.wechat.zzk.model.wallet.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;

public class PayInfo implements Parcelable, Serializable {
	public static final Creator<PayInfo> CREATOR = new Creator<PayInfo>() {
		public PayInfo createFromParcel(Parcel parcel) {
			return new PayInfo(parcel);
		}
		
		public PayInfo[] newArray(int i) {
			return new PayInfo[i];
		}
	};
	public static final String Intent_Tag = "PayInfo";
	public String Remarks = "";
	public int chatroom_num = 0;
	public int chatroom_type = -1;
	public double money;
	public int[] passward;
	public String paymethod = "";
	public String receiver_name;
	public int type = -1;
	
	public int describeContents() {
		return 0;
	}
	
	protected PayInfo(Parcel parcel) {
		this.type = parcel.readInt();
		this.receiver_name = parcel.readString();
		this.money = parcel.readDouble();
		this.passward = parcel.createIntArray();
		this.Remarks = parcel.readString();
		this.paymethod = parcel.readString();
		this.chatroom_type = parcel.readInt();
		this.chatroom_num = parcel.readInt();
	}
	
	public PayInfo() {
	}
	
	public static PayInfo revertSendRedPocketBeanToPayInfo(SendRedPocketBean sendRedPocketBean) {
		try {
			PayInfo payInfo = new PayInfo();
			payInfo.money = sendRedPocketBean.Amount;
			payInfo.Remarks = sendRedPocketBean.Content;
			payInfo.receiver_name = sendRedPocketBean.FriendWxId;
			if (sendRedPocketBean.ContentType == 6) {
				payInfo.type = 0;
			} else if (sendRedPocketBean.ContentType == 5) {
				if (sendRedPocketBean.FriendWxId.endsWith("@chatroom")) {
					Log.e("Xposed", " PayInfo  chatroom ");
					payInfo.type = 2;
					payInfo.chatroom_num = sendRedPocketBean.Num;
					StringBuilder sb = new StringBuilder();
					sb.append(" PayInfo  revertSendRedPocketBeanToPayInfo Num ：：");
					sb.append(sendRedPocketBean.Num);
					Log.e("Xposed", sb.toString());
					payInfo.chatroom_type = sendRedPocketBean.RedPocketType;
				} else {
					payInfo.type = 1;
				}
			}
			int[] iArr = new int[sendRedPocketBean.Password.length()];
			for (int i = 0; i < sendRedPocketBean.Password.length(); i++) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append(sendRedPocketBean.Password.charAt(i));
				sb2.append("");
				iArr[i] = Integer.parseInt(sb2.toString());
			}
			payInfo.passward = iArr;
			StringBuilder sb3 = new StringBuilder();
			sb3.append(" PayInfoBindSerial ：：");
			sb3.append(sendRedPocketBean.BindSerial);
			Log.e("Xposed", sb3.toString());
			if (sendRedPocketBean.BindSerial.startsWith("-1")) {
				payInfo.paymethod = "";
			} else {
				payInfo.paymethod = sendRedPocketBean.BindSerial;
			}
			return payInfo;
		} catch (Exception unused) {
			return null;
		}
	}
	
	public PayInfo clone() {
		PayInfo payInfo = new PayInfo();
		payInfo.receiver_name = this.receiver_name;
		payInfo.money = this.money;
		payInfo.passward = this.passward;
		payInfo.Remarks = this.Remarks;
		payInfo.paymethod = this.paymethod;
		payInfo.chatroom_type = this.chatroom_type;
		payInfo.chatroom_num = this.chatroom_num;
		return payInfo;
	}
	
	public boolean isBankcard() {
		String str = this.paymethod;
		return str != null && !str.equals("");
	}
	
	@Override
	public String toString() {
		return "PayInfo{" +
				"Remarks='" + Remarks + '\'' +
				", chatroom_num=" + chatroom_num +
				", chatroom_type=" + chatroom_type +
				", money=" + money +
				", passward=" + Arrays.toString(passward) +
				", paymethod='" + paymethod + '\'' +
				", receiver_name='" + receiver_name + '\'' +
				", type=" + type +
				'}';
	}
	
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(this.type);
		parcel.writeString(this.receiver_name);
		parcel.writeDouble(this.money);
		parcel.writeIntArray(this.passward);
		parcel.writeString(this.Remarks);
		parcel.writeString(this.paymethod);
		parcel.writeInt(this.chatroom_type);
		parcel.writeInt(this.chatroom_num);
	}
}
