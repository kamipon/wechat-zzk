package com.gentcent.wechat.zzk.bean;

import com.gentcent.zzk.xped.XposedHelpers;

import java.util.HashMap;

public class UserBean {
	//微信ID
	public String username;
	//微信号
	public String alias;
	//微信昵称
	public String nickname;
	//小头像
	public String reserved1;
	//大头像
	public String reserved2;
	//备注
	public String conRemark;
	//性别
	public int sex;
	//个性签名
	public String signature;
	//省
	public String province;
	//市
	public String region;
	//添加来源
	public int sourceType;
	public String sourceText;
	//拼音首字母大写
	public String pyInitial;
	//全拼
	public String quanPin;
	//群聊好友微信id （';'号分割）
	public String memberlist;
	//群聊显示的名字 ('、'号分割)
	public String displayname;
	//群聊群主
	public String roomOwner;
	//保存到通讯录
	public boolean isAddAddressBook;
	//消息免打扰
	public String notice;
	
	public HashMap<String, String> NameMap;
	
	public UserBean(String username, String alias, String nickname, String reserved1, String reserved2, String conRemark, String memberlist, String displayname, String pyInitial, String quanPin) {
		this.username = username;
		this.alias = alias;
		this.nickname = nickname;
		this.reserved1 = reserved1;
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
				", reserved1='" + reserved1 + '\'' +
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
				", roomOwner='" + roomOwner + '\'' +
				", isAddAddressBook=" + isAddAddressBook +
				", notice='" + notice + '\'' +
				", NameMap=" + NameMap +
				'}';
	}
}
