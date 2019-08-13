package com.gentcent.wechat.zzk.background;

/**
 * @author zuozhi
 * @since 2019-08-01
 */
public class Api {
	public static String prefix = "http://192.168.0.33/";
	public static String getWallet = prefix + "card/get";
	public static String sendMoneyResult = prefix + "sendMoney/result";
	public static String blank = prefix + ""; //占位 ，测试用
	
	public static String appPhone = prefix + "appPhone/add";
	public static String addweixin = prefix + "appWchat/addweixin/";
	public static String appfriend = prefix + "appfriend/add";
	public static String addgroup = prefix + "";
	public static String addWchat = prefix + "appWchat/add";
}
