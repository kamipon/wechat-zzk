package com.gentcent.wechat.zzk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.birbit.android.jobqueue.JobManager;
import com.gentcent.wechat.zzk.bean.MessageBean;
import com.gentcent.wechat.zzk.job.AddFriendJob;
import com.gentcent.wechat.zzk.job.TaskManager;
import com.gentcent.wechat.zzk.manager.FriendManager;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
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
					JobManager jobManager = TaskManager.getInstance().getJobManager();
					jobManager.addJobInBackground(new AddFriendJob(10,intent.getStringExtra("addFriendName"),null));
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + e.getMessage());
		}
	}
}