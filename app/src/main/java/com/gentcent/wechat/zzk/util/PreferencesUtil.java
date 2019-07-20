package com.gentcent.wechat.zzk.util;


import com.gentcent.zzk.xped.XSharedPreferences;

public class PreferencesUtil {
	
	private static XSharedPreferences instance = null;
	
	private static XSharedPreferences getInstance() {
		if (instance == null) {
			instance = new XSharedPreferences(PreferencesUtil.class.getPackage().getName());
			instance.makeWorldReadable();
		} else {
			instance.reload();
		}
		return instance;
	}
	
	//自动开红包
	public static boolean open() {
		return Boolean.valueOf(MyHelper.readLine("open", "true"));
	}
	
	//自己发的红包不开
	public static boolean notSelf() {
		return Boolean.valueOf(MyHelper.readLine("not_self", "true"));
	}
	
	//私聊的红包不开
	public static boolean notWhisper() {
		return Boolean.valueOf(MyHelper.readLine("not_whisper", "false"));
	}
	
	//包含关键字的不开
	public static String notContains() {
		return MyHelper.readLine("not_contains", "").replace("，", ",");
	}
	
	//收款延迟（防封） （红包和转账）
	public static boolean delay() {
		return Boolean.valueOf(MyHelper.readLine("delay", "true"));
	}
	
	//延迟范围（开始）
	public static int delayMin() {
		return Integer.valueOf(MyHelper.readLine("delay_min", "2000"));
	}
	
	//延迟范围（结束）
	public static int delayMax() {
		return Integer.valueOf(MyHelper.readLine("delay_max", "5000"));
	}
	
	//自动接收转账
	public static boolean receiveTransfer() {
		return Boolean.valueOf(MyHelper.readLine("receive_transfer", "true"));
	}
	
	//快速拆包（弃用）
	public static boolean quickOpen() {
		return Boolean.valueOf(MyHelper.readLine("quick_open", "false"));
	}
	
	//显示微信ID
	public static boolean showWechatId() {
		return Boolean.valueOf(MyHelper.readLine("show_wechat_id", "true"));
	}
	
	//包含微信ID的不开
	public static String blackList() {
		return MyHelper.readLine("black_list", "").replace("，", ",");
	}
	
	//防撤回
	public static boolean isAntiRevoke() {
		return Boolean.valueOf(MyHelper.readLine("is_anti_revoke", "true"));
	}
	
	//防朋友圈删除
	public static boolean isAntiSnsDelete() {
		return Boolean.valueOf(MyHelper.readLine("is_anti_sns_delete", "true"));
	}
	
	//过滤朋友圈广告
	public static boolean isADBlock() {
		return Boolean.valueOf(MyHelper.readLine("is_ad_block", "true"));
	}
	
	//电脑微信自动登录
	public static boolean isAutoLogin() {
		return Boolean.valueOf(MyHelper.readLine("is_auto_login", "true"));
	}
	
	//朋友圈突破9张图片限制
	public static boolean isBreakLimit() {
		return Boolean.valueOf(MyHelper.readLine("is_break_limit", "false"));
	}
	
}


