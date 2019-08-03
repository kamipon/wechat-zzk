package com.gentcent.wechat.zzk.bean;

import java.util.HashMap;

public class UserBean {
	public String username;
	public String alias;
	public String nickname;
	public String reserved1;
	public String reserved2;
	public String conRemark;
	public String memberlist;
	//群聊显示的名字
	public String displayname;
	public String pyInitial;
	public String quanPin;
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
				", memberlist='" + memberlist + '\'' +
				", displayname='" + displayname + '\'' +
				", pyInitial='" + pyInitial + '\'' +
				", quanPin='" + quanPin + '\'' +
				", NameMap=" + NameMap +
				'}';
	}
}
