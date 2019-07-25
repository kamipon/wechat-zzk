package com.gentcent.wechat.zzk.bean;

public class SnsCommentBean {
	private String BeReviewedNickName;
	private String BeReviewedWxid;
	private String CommentId;
	private String CommentText;
	private String NickName;
	private long Timestamp;
	private String Wxid;
	
	public String getBeReviewedNickName() {
		return BeReviewedNickName;
	}
	
	public void setBeReviewedNickName(String beReviewedNickName) {
		BeReviewedNickName = beReviewedNickName;
	}
	
	public String getBeReviewedWxid() {
		return BeReviewedWxid;
	}
	
	public void setBeReviewedWxid(String beReviewedWxid) {
		BeReviewedWxid = beReviewedWxid;
	}
	
	public String getCommentId() {
		return CommentId;
	}
	
	public void setCommentId(String commentId) {
		CommentId = commentId;
	}
	
	public String getCommentText() {
		return CommentText;
	}
	
	public void setCommentText(String commentText) {
		CommentText = commentText;
	}
	
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
		return "SnsCommentBean{" +
				"BeReviewedNickName='" + BeReviewedNickName + '\'' +
				", BeReviewedWxid='" + BeReviewedWxid + '\'' +
				", CommentId='" + CommentId + '\'' +
				", CommentText='" + CommentText + '\'' +
				", NickName='" + NickName + '\'' +
				", Timestamp=" + Timestamp +
				", Wxid='" + Wxid + '\'' +
				'}';
	}
}
