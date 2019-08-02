package com.gentcent.wechat.zzk.model.sns.bean;

public class SnsLikeBean {
	//微信昵称
	private String NickName;
	//时间戳
	private long Timestamp;
	//微信ID
	private String Wxid;
	
	public String getNickName() {
		return NickName;
	}
	
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	
	public long getTimestamp() {
		return Timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		Timestamp = timestamp;
	}
	
	public String getWxid() {
		return Wxid;
	}
	
	public void setWxid(String wxid) {
		Wxid = wxid;
	}
	
	@Override
	public String toString() {
		return "SnsLikeBean{" +
				"NickName='" + NickName + '\'' +
				", Timestamp=" + Timestamp +
				", Wxid='" + Wxid + '\'' +
				'}';
	}
}
