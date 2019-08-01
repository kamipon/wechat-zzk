package com.gentcent.wechat.zzk;

import android.content.Context;
import android.content.Intent;

import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.Map;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class EventHandler {
	
	/**
	 * 发送消息
	 */
	public static void sendMessage(String jsonStr) {
		Context context = MyApplication.getAppContext();
		Intent intent = new Intent("WxAction");
		intent.putExtra("act", "send_message");
		intent.putExtra("sendmsgbean", jsonStr);
		context.sendBroadcast(intent);
		
//		Context context = MyApplication.getAppContext();
//		SendMessageBean sendMessageBean = GsonUtils.GsonToBean(jsonStr, SendMessageBean.class);
//		XLog.d("添加至消息队列 类型:" + sendMessageBean.getType());
//		SendMessageManager.addToQueque(sendMessageBean);
//		//添加至消息队列
//		sendBroad(context);
	}
	
	/**
	 * 发送广播，传递消息队列
	 */
//	private static void sendBroad(final Context context) {
//		int quequeSize = SendMessageManager.getQuequeSize();
//		if(quequeSize>0 && !SendMessageManager.isLock()){
//			SendMessageManager.lock();
//			List<SendMessageBean> list = new ArrayList<>(SendMessageManager.getQueque());
//			SendMessageManager.clearQueque();
//
//			Intent intent = new Intent("WxAction");
//			intent.putExtra("act", "send_message");
//			intent.putExtra("msgQueue", (Serializable)list);
//			context.sendBroadcast(intent);
//			XLog.d("发送消息 | 个数："+quequeSize);
//
//			//发送完成后再次执行，直到消息队列为空为止
//			TimerTask task = new TimerTask() {
//				@Override
//				public void run() {
//					SendMessageManager.unLock();
//					sendBroad(context);
//				}
//			};
//			new Timer().schedule(task, HookParams.SEND_TIME_INTERVAL * (quequeSize + 1));
//		}
//	}
	
	/**
	 * 添加好友
	 */
	public static void addFriend(String jsonStr) {
		try {
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
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送朋友圈
	 */
	public static void sendSns(String snsJson) {
		try {
			XLog.d("发送朋友圈");
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "send_sns");
			intent.putExtra("snsJson", snsJson);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}
	
	/**
	 * 安全检查
	 */
	public static void sendWalletNotice() {
		try {
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "send_wallet_notice");
			context.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
