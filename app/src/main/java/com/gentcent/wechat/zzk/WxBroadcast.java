package com.gentcent.wechat.zzk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gentcent.wechat.zzk.util.GsonUtils;
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
			case "sync_info":
				sendAct(act);
				break;
			default:
				XLog.e("not found act: null | Message: " + customMessage.message);
				break;
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
			Map<String, Object> map = GsonUtils.GsonToMaps(jsonStr);
			String helloText = (String) map.get("helloText");
			String addFriendName = (String) map.get("addFriendName");
			
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", act);
			intent.putExtra("addFriendName", addFriendName);
			intent.putExtra("helloText", helloText);
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
	
}
