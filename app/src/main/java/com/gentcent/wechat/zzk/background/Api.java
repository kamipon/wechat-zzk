package com.gentcent.wechat.zzk.background;

/**
 * @author zuozhi
 * @since 2019-08-01
 */
public class Api {
//	public static String prefix = "http://zzk.cj0524.xin/";
	public static String prefix = "http://192.168.0.133/";
	public static String walletInfo = prefix + "appRedPocket/notice";
	public static String sendMoneyResult = prefix + "sendMoney/result";
	public static String blank = prefix + ""; //占位 ，测试用
	
	public static String appPhone = prefix + "appPhone/add";
	public static String addweixin = prefix + "appWchat/addweixin/";
	public static String appfriend = prefix + "appfriend/add";
	public static String appfriendList = prefix + "appfriend/addFriends";
	public static String addgroup = prefix + "";
	public static String addWchat = prefix + "appWchat/add";
	public static String fileUploadsWchat = prefix + "appWchat/fileUploads";
	public static String syncSns = prefix + "appSns/sync";
}
