package com.gentcent.wechat.zzk.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 朋友圈Bean
 *
 * @author zuozhi
 * @since 2019-07-22
 */
public class SnsBean implements Serializable {
	/*
	1:	纯文本
	2:	带图片
	3:	带视屏
	4:	文章（分享链接）
	 */
	private int Type;
	//是否添加水印
	private boolean AddWatermark;
	//文章图片
	private String ArticleImage;
	//文章标题
	private String ArticleTitle;
	//文章链接
	private String ArticleUrl;
	//内容
	private String Content;
	//图片
	private ArrayList<String> Images;
	//允许查看的好友
	private ArrayList<String> LookFriendWxIdList;
	//TODO:？ 不知道是啥
	private int LookUpType;
	//@好友
	private String Remind;
	//首次评论
	private String SelfComment;
	//视屏
	private String Video;
	
	public boolean isAddWatermark() {
		return AddWatermark;
	}
	
	public void setAddWatermark(boolean addWatermark) {
		AddWatermark = addWatermark;
	}
	
	public String getArticleImage() {
		return ArticleImage;
	}
	
	public void setArticleImage(String articleImage) {
		ArticleImage = articleImage;
	}
	
	public String getArticleTitle() {
		return ArticleTitle;
	}
	
	public void setArticleTitle(String articleTitle) {
		ArticleTitle = articleTitle;
	}
	
	public String getArticleUrl() {
		return ArticleUrl;
	}
	
	public void setArticleUrl(String articleUrl) {
		ArticleUrl = articleUrl;
	}
	
	public String getContent() {
		return Content;
	}
	
	public void setContent(String content) {
		Content = content;
	}
	
	public ArrayList<String> getImages() {
		return Images;
	}
	
	public void setImages(ArrayList<String> images) {
		Images = images;
	}
	
	public ArrayList<String> getLookFriendWxIdList() {
		return LookFriendWxIdList;
	}
	
	public void setLookFriendWxIdList(ArrayList<String> lookFriendWxIdList) {
		LookFriendWxIdList = lookFriendWxIdList;
	}
	
	public int getLookUpType() {
		return LookUpType;
	}
	
	public void setLookUpType(int lookUpType) {
		LookUpType = lookUpType;
	}
	
	public String getRemind() {
		return Remind;
	}
	
	public void setRemind(String remind) {
		Remind = remind;
	}
	
	public String getSelfComment() {
		return SelfComment;
	}
	
	public void setSelfComment(String selfComment) {
		SelfComment = selfComment;
	}
	
	public int getType() {
		return Type;
	}
	
	public void setType(int type) {
		Type = type;
	}
	
	public String getVideo() {
		return Video;
	}
	
	public void setVideo(String video) {
		Video = video;
	}
	
	@Override
	public String toString() {
		return "SnsBean{" +
				"AddWatermark=" + AddWatermark +
				", ArticleImage='" + ArticleImage + '\'' +
				", ArticleTitle='" + ArticleTitle + '\'' +
				", ArticleUrl='" + ArticleUrl + '\'' +
				", Content='" + Content + '\'' +
				", Images=" + Images +
				", LookFriendWxIdList=" + LookFriendWxIdList +
				", LookUpType=" + LookUpType +
				", Remind='" + Remind + '\'' +
				", SelfComment='" + SelfComment + '\'' +
				", Type=" + Type +
				", Video='" + Video + '\'' +
				'}';
	}
}
