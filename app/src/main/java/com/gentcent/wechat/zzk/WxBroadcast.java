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
				sendMessage(jsonStr);
				break;
			case "add_friend":
				addFriend(jsonStr);
				break;
			case "send_sns":
				sendSns(jsonStr);
				break;
			case "sync_info":
				syncInfo();
				break;
			case "send_wallet_notice":
				sendWalletNotice();
				break;
			case "send_redpocket":
				moneySend(jsonStr);
				break;
			default:
				XLog.e("not found act: null | Message: " + customMessage.message);
				break;
		}
	}
	
	/**
	 * 发送消息
	 */
	public static void sendMessage(String jsonStr) {
		try {
			XLog.d("发送消息");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "send_message");
			intent.putExtra("sendmsgbean", jsonStr);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 添加好友
	 */
	public static void addFriend(String jsonStr) {
		try {
			XLog.d("添加好友");
			Map<String, Object> map = GsonUtils.GsonToMaps(jsonStr);
			String helloText = (String) map.get("helloText");
			String addFriendName = (String) map.get("addFriendName");
			
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "add_friend");
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
	public static void sendSns(String jsonStr) {
		try {
			XLog.d("发送朋友圈");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "send_sns");
			intent.putExtra("snsJson", jsonStr);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 转账发红包
	 */
	public static void moneySend(String jsonStr) {
		try {
			XLog.d("转账发红包");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "send_redpocket");
			intent.putExtra("payInfoJson", jsonStr);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 读取微信数据库
	 */
	public static void syncInfo() {
		try {
			XLog.d("读取微信数据库");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "sync_info");
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 安全检查
	 */
	private static void sendWalletNotice() {
		try {
			XLog.d("安全检查");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "send_wallet_notice");
			context.sendBroadcast(intent);
		} catch (Exception e) {
			XLog.e("错误：" + Log.getStackTraceString(e));
		}
	}
	
}
