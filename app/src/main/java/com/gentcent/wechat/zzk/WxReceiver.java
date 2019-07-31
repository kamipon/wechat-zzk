package com.gentcent.wechat.zzk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.gentcent.wechat.zzk.bean.SendMessageBean;
import com.gentcent.wechat.zzk.bean.SendSnsBean;
import com.gentcent.wechat.zzk.job.AddFriendJob;
import com.gentcent.wechat.zzk.job.SendSnsJob;
import com.gentcent.wechat.zzk.job.TaskManager;
import com.gentcent.wechat.zzk.handler.SendMessageHandler;
import com.gentcent.wechat.zzk.manager.SendMessageManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.SyncInfoDao;

public class WxReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, final Intent intent) {
		try {
			String act = intent.getStringExtra("act");
			if (act != null) {
				//添加好友
				//发送朋友圈
				switch (act) {
					case "send_message":
						SendMessageBean sendMsgBean = GsonUtils.GsonToBean(intent.getStringExtra("sendmsgbean"), SendMessageBean.class);
						SendMessageManager.sendMessage(sendMsgBean);
						break;
					case "add_friend": {
						String addFriendName = intent.getStringExtra("addFriendName");
						String helloText = intent.getStringExtra("helloText");
						JobManager jobManager = TaskManager.getInstance().getJobManager();
						jobManager.addJobInBackground(new AddFriendJob(10, addFriendName, helloText, null));
						break;
					}
					case "send_sns": {
						String snsJson = intent.getStringExtra("snsJson");
						XLog.d("snsJson:  " + snsJson);
						SendSnsBean snsBean = GsonUtils.GsonToBean(snsJson, SendSnsBean.class);
						JobManager jobManager = TaskManager.getInstance().getJobManager();
						jobManager.addJobInBackground(new SendSnsJob(25, snsBean));
						break;
					}
					case "sync_info": //同步信息
						XLog.d("同步信息");
						SyncInfoDao.syncInfo();
						break;
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + Log.getStackTraceString(e));
		}
	}
}
