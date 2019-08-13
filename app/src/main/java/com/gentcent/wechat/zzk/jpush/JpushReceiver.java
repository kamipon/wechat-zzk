package com.gentcent.wechat.zzk.jpush;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.MyApplication;
import com.gentcent.wechat.zzk.WxBroadcast;
import com.gentcent.wechat.zzk.activity.MainActivity;
import com.gentcent.wechat.zzk.util.XLog;

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
		XLog.e(TAG + "onConnected " + b);
		JPushInterface.setAlias(context, 1, PhoneUtils.getIMEI());
		//重连
		if (!b) {
			JPushInterface.init(context);
			JPushInterface.onResume(context);
		}
		super.onConnected(context, b);
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
		XLog.e(TAG + "setAlias" + jPushMessage);
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
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(MyApplication.getAppContext(), notification);
		r.play();
		XLog.d(customMessage.toString());
		try {
			WxBroadcast.onMessage(customMessage);
		} catch (Exception e) {
			XLog.e("error:" + Log.getStackTraceString(e));
		}
		
		//测试用 给xzt转发消息
//		customMessage.message = "{\n" +
//				"\t\"DeviceIMEI\": \"867078031939281\",\n" +
//				"\t\"MyWxId\": \"wxid_dephvhve1wpt22\",\n" +
//				"\t\"FriendWxId\": \"wxid_kew7mmulpykg22\",\n" +
//				"\t\"Content\": \"" + customMessage.toString().replace("{", " ").replace("}", " ").replace("\"", "\'") + "\",\n" +
//				"\t\"Pos\": 0,\n" +
//				"\t\"Type\": 0,\n" +
//				"\t\"Interval\": 0,\n" +
//				"\t\"LinkImg\": null,\n" +
//				"\t\"LinkTitle\": null,\n" +
//				"\t\"LinkDescription\": null,\n" +
//				"\t\"LinkUrl\": null,\n" +
//				"\t\"FileName\": null,\n" +
//				"\t\"ChatroomMemberWxId\": null,\n" +
//				"\t\"ServiceGuid\": \"35fa78b4-b956-400b-9ce4-23ba7aecd30c\"\n" +
//				"}";
//		customMessage.extra = "{\"act\":\"send_message\"}";
//		WxBroadcast.onMessage(customMessage);
	}
	
}
