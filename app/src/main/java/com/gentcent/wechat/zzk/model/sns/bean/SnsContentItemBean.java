package com.gentcent.wechat.zzk.model.sns.bean;

import java.util.LinkedList;
import java.util.List;

public class SnsContentItemBean {
	/*
	1:	纯文本
	2:	带图片
	3:	带视屏
	4:	链接
	 */
	private int Type;
	//2 图片列表
	private List<String> Images;
	//3 视屏
	private String Video;
	//4 链接图片
	private String ArticleImage;
	//4 链接标题
	private String ArticleTitle;
	//4 链接地址
	private String ArticleUrl;
	//指定好友
	private LinkedList<String> ChooseFriends = new LinkedList<>();
	//评论
	private LinkedList<SnsCommentBean> Commentlist = new LinkedList<>();
	//文本内容
	private String Content;
	//是否公开
	private boolean IsPublic;
	//是否指定好友可以看
	private boolean IsSee;
	//点赞
	private LinkedList<SnsLikeBean> Likelist = new LinkedList<>();
	//0：公开  2:指定好友可以看  3:指定好友不能看
	private int LookUpType;
	//所有者的昵称
	private String NickName;
	//是否自己点赞自己
	private boolean SelfLike;
	//wcdb的sbsid
	private String SnsID;
	//所属好友的wxid
	private String SnsWxid;
	//时间戳
	private long Timestamp;
	
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
	
	public LinkedList<String> getChooseFriends() {
		return ChooseFriends;
	}
	
	public void setChooseFriends(LinkedList<String> chooseFriends) {
		ChooseFriends = chooseFriends;
	}
	
	public LinkedList<SnsCommentBean> getCommentlist() {
		return Commentlist;
	}
	
	public void setCommentlist(LinkedList<SnsCommentBean> commentlist) {
		Commentlist = commentlist;
	}
	
	public String getContent() {
		return Content;
	}
	
	public void setContent(String content) {
		Content = content;
	}
	
	public List<String> getImages() {
		return Images;
	}
	
	public void setImages(List<String> images) {
		Images = images;
	}
	
	public boolean isPublic() {
		return IsPublic;
	}
	
	public void setPublic(boolean aPublic) {
		IsPublic = aPublic;
	}
	
	public boolean isSee() {
		return IsSee;
	}
	
	public void setSee(boolean see) {
		IsSee = see;
	}
	
	public LinkedList<SnsLikeBean> getLikelist() {
		return Likelist;
	}
	
	public void setLikelist(LinkedList<SnsLikeBean> likelist) {
		Likelist = likelist;
	}
	
	public int getLookUpType() {
		return LookUpType;
	}
	
	public void setLookUpType(int lookUpType) {
		LookUpType = lookUpType;
	}
	
	public String getNickName() {
		return NickName;
	}
	
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	
	public boolean isSelfLike() {
		return SelfLike;
	}
	
	public void setSelfLike(boolean selfLike) {
		SelfLike = selfLike;
	}
	
	public String getSnsID() {
		return SnsID;
	}
	
	public void setSnsID(String snsID) {
		SnsID = snsID;
	}
	
	public String getSnsWxid() {
		return SnsWxid;
	}
	
	public void setSnsWxid(String snsWxid) {
		SnsWxid = snsWxid;
	}
	
	public long getTimestamp() {
		return Timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		Timestamp = timestamp;
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
		return "SnsContentItemBean{" +
				"ArticleImage='" + ArticleImage + '\'' +
				", ArticleTitle='" + ArticleTitle + '\'' +
				", ArticleUrl='" + ArticleUrl + '\'' +
				", ChooseFriends=" + ChooseFriends +
				", Commentlist=" + Commentlist +
				", Content='" + Content + '\'' +
				", Images=" + Images +
				", IsPublic=" + IsPublic +
				", IsSee=" + IsSee +
				", Likelist=" + Likelist +
				", LookUpType=" + LookUpType +
				", NickName='" + NickName + '\'' +
				", SelfLike=" + SelfLike +
				", SnsID='" + SnsID + '\'' +
				", SnsWxid='" + SnsWxid + '\'' +
				", Timestamp=" + Timestamp +
				", Type=" + Type +
				", Video='" + Video + '\'' +
				'}';
	}
}
