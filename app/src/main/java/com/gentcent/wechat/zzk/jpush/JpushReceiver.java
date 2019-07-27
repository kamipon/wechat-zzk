package com.gentcent.wechat.zzk.jpush;

import android.content.Context;
import android.text.TextUtils;

import com.gentcent.wechat.zzk.EventHandler;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.Map;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * @author zuozhi
 * @since 2019-07-26
 */
public class JpushReceiver extends JPushMessageReceiver {
	private static final String TAG = "JReceiver:  ";
	
	/**
	 * 连接极光服务器
	 */
	@Override
	public void onConnected(Context context, boolean b) {
		super.onConnected(context, b);
		XLog.e(TAG + "onConnected " + b);
		
		//重连
		if (!b) {
			JPushInterface.init(context);
			JPushInterface.onResume(context);
		}
	}
	
	/**
	 * 注册极光时的回调
	 */
	@Override
	public void onRegister(Context context, String s) {
		super.onRegister(context, s);
		XLog.e(TAG + "onRegister" + s);
	}
	
	/**
	 * 注册以及解除注册别名时回调
	 */
	@Override
	public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
		super.onAliasOperatorResult(context, jPushMessage);
//		String alias = jPushMessage.getAlias();
//		boolean needAlias = TextUtils.isEmpty(alias);
//		XLog.e(TAG + "need set alias " + needAlias + " alias is : " + alias);
//		if (needAlias) {
//			boolean connectionState = JPushInterface.getConnectionState(context);
//			XLog.e(TAG + "jPushState : " + connectionState);
//			if (connectionState) {
//				XLog.e(TAG + "setAlias is : device_002");
//				JPushInterface.setAlias(context, 1, "device_002");
//			}else{
//				XLog.e("Jpush重连");
//				JPushInterface.init(context);
//				JPushInterface.onResume(context);
//				JPushInterface.setAlias(context, 1, "device_002");
//			}
//		}
	}
	
	/**
	 * 接收到推送下来的通知
	 * 可以利用附加字段（notificationMessage.notificationExtras）来区别Notication,指定不同的动作,附加字段是个json字符串
	 * 通知（Notification），指在手机的通知栏（状态栏）上会显示的一条通知信息
	 */
	@Override
	public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
		super.onNotifyMessageArrived(context, notificationMessage);
		XLog.e(TAG + notificationMessage.toString());
	}
	
	/**
	 * 打开了通知
	 * notificationMessage.notificationExtras(附加字段)的内容处理代码
	 * 比如打开新的Activity， 打开一个网页等..
	 */
	@Override
	public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
		super.onNotifyMessageOpened(context, notificationMessage);
		XLog.e(TAG + notificationMessage.notificationExtras);
	}
	
	/**
	 * 接收到推送下来的自定义消息
	 * 自定义消息不是通知，默认不会被SDK展示到通知栏上，极光推送仅负责透传给SDK。其内容和展示形式完全由开发者自己定义。
	 * 自定义消息主要用于应用的内部业务逻辑和特殊展示需求
	 */
	@Override
	public void onMessage(Context context, CustomMessage customMessage) {
		super.onMessage(context, customMessage);
		
		Map<String, Object> extra = GsonUtils.GsonToMaps(customMessage.extra);
		String act = (String) extra.get("act");
		String jsonStr = customMessage.message;
		
		XLog.d("act: " + act + " | Message: " + jsonStr);
		if ("send_message".equals(act)) {
			EventHandler.sendMessage(jsonStr);
		} else if ("add_friend".equals(act)) {
			EventHandler.addFriend(jsonStr);
		} else if ("send_sns".equals(act)) {
			EventHandler.sendSns(jsonStr);
		} else if ("sync_info".equals(act)) {
			EventHandler.syncInfo();
		} else {
			XLog.e("not found act: null | Message: " + customMessage.message);
		}
	}
	
}
