package com.gentcent.wechat.zzk.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentMessageBean implements Parcelable {
	public static final Creator<ContentMessageBean> CREATOR = new Creator<ContentMessageBean>() {
		public ContentMessageBean createFromParcel(Parcel parcel) {
			return new ContentMessageBean(parcel);
		}
		
		public ContentMessageBean[] newArray(int i) {
			return new ContentMessageBean[i];
		}
	};
	public String Content;
	public String path;
	public int type;
	
	public int describeContents() {
		return 0;
	}
	
	public String toString() {
		return "ContentMessageBean{type=" + this.type + ", Content='" + this.Content + '\'' + ", path='" + this.path + '\'' + '}';
	}
	
	protected ContentMessageBean(Parcel parcel) {
		this.type = parcel.readInt();
		this.Content = parcel.readString();
		this.path = parcel.readString();
	}
	
	public ContentMessageBean(int i, String str) {
		this.type = i;
		this.Content = str;
	}
	
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(this.type);
		parcel.writeString(this.Content);
		parcel.writeString(this.path);
	}
}
