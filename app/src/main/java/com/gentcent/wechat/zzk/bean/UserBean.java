package com.gentcent.wechat.zzk.bean;

import java.util.List;

public class UserBean {
	//微信ID
	public String username;
	//微信号
	public String alias;
	//微信昵称
	public String nickname;
	//小头像
//	public String reserved1;
	//大头像
	public String reserved2;
	//备注
	public String conRemark;
	//性别 0：群聊 1：男 2：女
	public int sex;
	//个性签名
	public String signature;
	//省
	public String province;
	//市
	public String region;
	/**
	 * 添加来源
	 * 0:未知来源，微信没有||群聊
	 * 1:我加别人
	 * 2:别人加我
	 */
	public int sourceType;
	public String sourceText;
	//拼音首字母大写
	public String pyInitial;
	//全拼
	public String quanPin;
	
	//-----------------群聊-------------------------
	
	//群聊好友微信id （';'号分割）
	public String memberlist;
	//群聊成员的微信昵称 ('、'号分割)
	public String displayname;
	//群昵成员新信息表
	public List<ChartRoomFriendBean> chartRoomFriendsList;
	//群聊群主
	public String roomOwner;
	//群聊是否保存到通讯录
	public boolean isAddAddressBook;
	//群公告
	public String notice;
	
	public UserBean() {
	}
	
	public UserBean(String username, String alias, String nickname, String reserved2, String conRemark, String memberlist, String displayname, String pyInitial, String quanPin) {
		this.username = username;
		this.alias = alias;
		this.nickname = nickname;
		this.reserved2 = reserved2;
		this.conRemark = conRemark;
		this.memberlist = memberlist;
		this.displayname = displayname;
		this.pyInitial = pyInitial;
		this.quanPin = quanPin;
	}
	
	@Override
	public String toString() {
		return "UserBean{" +
				"username='" + username + '\'' +
				", alias='" + alias + '\'' +
				", nickname='" + nickname + '\'' +
				", reserved2='" + reserved2 + '\'' +
				", conRemark='" + conRemark + '\'' +
				", sex=" + sex +
				", signature='" + signature + '\'' +
				", province='" + province + '\'' +
				", region='" + region + '\'' +
				", sourceType=" + sourceType +
				", sourceText='" + sourceText + '\'' +
				", pyInitial='" + pyInitial + '\'' +
				", quanPin='" + quanPin + '\'' +
				", memberlist='" + memberlist + '\'' +
				", displayname='" + displayname + '\'' +
				", chartRoomFriendsList=" + chartRoomFriendsList +
				", roomOwner='" + roomOwner + '\'' +
				", isAddAddressBook=" + isAddAddressBook +
				", notice='" + notice + '\'' +
				'}';
	}
}
