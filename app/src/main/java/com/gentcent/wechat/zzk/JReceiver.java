package com.gentcent.wechat.zzk;

import android.content.Context;
import android.text.TextUtils;

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
public class JReceiver extends JPushMessageReceiver {
	private static final String TAG = "JReceiver:  ";
	
	/**
	 * 连接极光服务器
	 */
	@Override
	public void onConnected(Context context, boolean b) {
		super.onConnected(context, b);
		XLog.e(TAG + "onConnected");
	}
	
	/**
	 * 注册极光时的回调
	 */
	@Override
	public void onRegister(Context context, String s) {
		super.onRegister(context, s);
		XLog.e(TAG + "onRegister" + s);
		// 一般登录之后调用此方法设置别名
		// sequence 用来标识一次操作的唯一性(退出登录时根据此参数删除别名)
		// alias 设置有效的别名
		// 有效的别名组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。限制：alias 命名长度限制为 40 字节。
		JPushInterface.setAlias(context, 10086, "device_001");
	}
	
	/**
	 * 注册以及解除注册别名时回调
	 */
	@Override
	public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
		super.onAliasOperatorResult(context, jPushMessage);
		XLog.e(TAG + jPushMessage.toString());
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
		if(TextUtils.isEmpty(customMessage.extra)){
			XLog.d("act: null | Message: " + customMessage.message);
			return;
		}
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
		}else{
		
		}
	}
	
}
