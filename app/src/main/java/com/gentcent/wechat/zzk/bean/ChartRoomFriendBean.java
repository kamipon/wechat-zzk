package com.gentcent.wechat.zzk.bean;

/**
 * @author zuozhi
 * @since 2019-08-07
 */
public class ChartRoomFriendBean {
	//微信ID
	public String wxId;
	//微信昵称，有备注则显示备注
	public String nickname;
	//聊天室昵称
	public String chartRoomNick;
	//头像地址
	public String headImgUrl;
	
	public ChartRoomFriendBean(String wxId, String nickname, String chartRoomName, String headImgUrl) {
		this.wxId = wxId;
		this.nickname = nickname;
		this.chartRoomNick = chartRoomName;
		this.headImgUrl = headImgUrl;
	}
	
	@Override
	public String toString() {
		return "ChartRoomFriendBean{" +
				"wxId='" + wxId + '\'' +
				", nickname='" + nickname + '\'' +
				", chartRoomNick='" + chartRoomNick + '\'' +
				", headImgUrl='" + headImgUrl + '\'' +
				'}';
	}
}
