package com.gentcent.wechat.zzk.wcdb;

/**
 * @author zuozhi
 * @since 2019-08-05
 */
public class HookSQL {
	public static String findChatRoomOwner(String roomId) {
		return "select roomowner from chatroom where  chatroomname='" + roomId + "'";
	}
	
}
