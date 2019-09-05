package com.gentcent.wechat.zzk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.Utils;
import com.gentcent.wechat.zzk.bean.LuckyMoneyBean;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.Map;

import cn.jpush.android.api.CustomMessage;

/**
 * 微信广播发送器
 *
 * @author zuozhi
 * @since 2019-07-08
 */
public class WxBroadcast {
	
	/**
	 * 自定义消息转发器
	 *
	 * @param customMessage 自定义消息，json
	 */
	public static void onMessage(CustomMessage customMessage) {
		Map<String, Object> extra = GsonUtils.GsonToMaps(customMessage.extra);
		String act = (String) extra.get("act");
		String jsonStr = customMessage.message;
		
		XLog.d("act: " + act + " | Message: " + jsonStr);
		switch (act) {
			case "send_message":
				sendMessage(act, jsonStr);
				break;
			case "add_friend":
				addFriend(act, jsonStr);
				break;
			case "del_friends":
				delFriend(act, jsonStr);
				break;
			case "send_sns":
				sendSns(act, jsonStr);
				break;
			case "send_redpocket":
				moneySend(act, jsonStr);
				break;
			case "transfer_refund":
			case "transfer_receive":
				transferReference(act, jsonStr);
				break;
			case "send_wallet_notice":
				walletNotice(act, jsonStr);
				break;
			case "sync_info":
				sendAct(act);
				break;
			case "group_recevice":
			case "personal_recevice":
				redpacketReference(act, jsonStr);
				break;
			case "is_wechat_open":
				isWechatOpen();
				break;
			default:
				XLog.e("not found act: null | Message: " + customMessage.message);
				break;
		}
	}
	
	/**
	 * 检测微信环境
	 */
	public static void isWechatOpen() {
		try {
			MyHelper.writeLine("isWechatOpen", "false");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "is_wechat_open");
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 获取钱包信息
	 */
	private static void walletNotice(String act, String jsonStr) {
		try {
			String serverId = (String) GsonUtils.GsonToMaps(jsonStr).get("serverId");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("serverId", serverId);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 红包相关
	 */
	private static void redpacketReference(String act, String jsonStr) {
		try {
			LuckyMoneyBean luckyMoneyBean = GsonUtils.GsonToBean(jsonStr, LuckyMoneyBean.class);
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("sessionName", luckyMoneyBean.sessionName);
			intent.putExtra("linkUrl", luckyMoneyBean.linkUrl);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 转账相关 ，msgId
	 */
	private static void transferReference(String act, String jsonStr) {
		try {
			String msgId = (String) GsonUtils.GsonToMaps(jsonStr).get("msgId");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("msgId", msgId);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 发送消息
	 */
	public static void sendMessage(String act, String jsonStr) {
		try {
			XLog.d("发送消息");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("sendmsgbean", jsonStr);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 添加好友
	 */
	public static void addFriend(String act, String jsonStr) {
		try {
			XLog.d("添加好友");
			JSONObject jsonObject = JSONObject.parseObject(jsonStr);
			String taskid = jsonObject.getString("taskid");
			String TaskContent = jsonObject.getString("TaskContent");
			
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("doTaskType", "AddFriendsPowder");
			intent.putExtra("TaskContent", TaskContent);
			intent.putExtra("taskid", taskid);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 删除好友
	 */
	private static void delFriend(String act, String jsonStr) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(jsonStr);
			String username = jsonObject.getString("username");
			
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("username", username);
			intent.putExtra("sleep_time", 10000);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 发送朋友圈
	 */
	public static void sendSns(String act, String jsonStr) {
		try {
			XLog.d("发送朋友圈");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("snsJson", jsonStr);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 转账发红包
	 */
	public static void moneySend(String act, String jsonStr) {
		try {
			XLog.d("转账发红包");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("payInfoJson", jsonStr);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 无参数
	 */
	public static void sendAct(String act) {
		try {
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * openApp
	 *
	 * @param context
	 */
	public static void openApp(Context context) {
		Intent launchIntentForPackage = Utils.getApp().getPackageManager().getLaunchIntentForPackage("com.gentcent.wechat.zzk");
		launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Utils.getApp().startActivity(launchIntentForPackage);
	}
	
}
