package com.gentcent.wechat.zzk.model.message.bean;

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
        StringBuilder sb = new StringBuilder();
        sb.append("ContentMessageBean{type=");
        sb.append(this.type);
        sb.append(", Content='");
        sb.append(this.Content);
        sb.append('\'');
        sb.append(", path='");
        sb.append(this.path);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
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
