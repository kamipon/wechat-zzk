package com.gentcent.wechat.enhancement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.AppUtils;
import com.gentcent.wechat.enhancement.bean.MessageBean;
import com.gentcent.wechat.enhancement.manager.FriendManager;
import com.gentcent.wechat.enhancement.manager.MainManager;
import com.gentcent.wechat.enhancement.util.HookParams;
import com.gentcent.wechat.enhancement.util.MyHelper;
import com.gentcent.wechat.enhancement.util.ThreadPoolUtils;
import com.gentcent.wechat.enhancement.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class WxReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, final Intent intent) {
		try {
			String act = intent.getStringExtra("act");
			if (act != null) {
				// 发送消息
				if (act.equals("send_message")) {
					final List<MessageBean> msgQueue = (List<MessageBean>) intent.getSerializableExtra("msgQueue");
					ThreadPoolUtils tp = ThreadPoolUtils.getInstance();
					tp.run(new Runnable() {
						@Override
						public void run() {
							for (final MessageBean messageBean: msgQueue) {
								new Runnable() {
									@Override
									public void run() {
										//构造参数
										String friendWxId = messageBean.getFriendWxId();
										String content = messageBean.getContent();
										int type = messageBean.getType();
										Class aClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageParamClassName, MainManager.wxLpparam.classLoader);
										Object param = XposedHelpers.newInstance(aClass, friendWxId, content, type);
										//调用方法
										final Class bClass = XposedHelpers.findClass(HookParams.getInstance().sendMessageClassName, MainManager.wxLpparam.classLoader);
										final Object staticObject = XposedHelpers.getStaticObjectField(bClass, HookParams.getInstance().sendMessageStaticObject);
										Object callMethod = XposedHelpers.callMethod(staticObject, HookParams.getInstance().sendMessageMethodName, param);
										XLog.e("发送成功:" + (Boolean) callMethod);
									}
								}.run();
								try {
									Thread.sleep(HookParams.SEND_TIME_INTERVAL);
								} catch (InterruptedException e) {
									XLog.e("发送消息线程休眠出错："+e.getMessage());
									e.printStackTrace();
								}
							}
						}
					});
				}else if(act.equals("add_friend")){
					try{
						XLog.d("add_friend");
						Intent intent2 = new Intent();
						intent2.setClassName("com.tencent.mm", "com.tencent.mm.plugin.fts.ui.FTSMainUI");
						intent2.setFlags(FLAG_ACTIVITY_NEW_TASK);
						MainManager.activity.startActivity(intent2);
						XLog.d("跳转到FTSMainUI SUCCESS");
						String addFriendName = MyHelper.readLine("addFriendName", "");
						MyHelper.delete("addFriendName");
						XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.plugin.fts.ui.FTSMainUI"), "c", FriendManager.activity, addFriendName);
						XLog.d("callStaticMethod | FTSMainUI");
					} catch (Exception e) {
						e.printStackTrace();
						XLog.e("错误:" + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + e.getMessage());
		}
	}
}
