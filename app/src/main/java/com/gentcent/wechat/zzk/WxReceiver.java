package com.gentcent.wechat.zzk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.gentcent.wechat.zzk.bean.SendMessageBean;
import com.gentcent.wechat.zzk.bean.SendSnsBean;
import com.gentcent.wechat.zzk.job.AddFriendJob;
import com.gentcent.wechat.zzk.job.MoneySendJob;
import com.gentcent.wechat.zzk.job.SendSnsJob;
import com.gentcent.wechat.zzk.job.TaskManager;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.manager.SendMessageManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wallet.Dibs;
import com.gentcent.wechat.zzk.wallet.PayInfo;
import com.gentcent.wechat.zzk.wallet.SendRedPocketBean;
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
					case "send_message": {
						SendMessageBean sendMsgBean = GsonUtils.GsonToBean(intent.getStringExtra("sendmsgbean"), SendMessageBean.class);
						SendMessageManager.sendMessage(sendMsgBean);
						break;
					}
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
					case "sync_info": {
						XLog.d("同步信息");
						SyncInfoDao.syncInfo();
						break;
					}
					case "send_wallet_notice": {
						XLog.d("安全检查");
						Dibs.requestDataIsRe(MainManager.wxLpparam, true);
						break;
					}
					case "send_redpocket": {
						String payInfoJson = intent.getStringExtra("payInfoJson");
						PayInfo payInfo = PayInfo.revertSendRedPocketBeanToPayInfo(GsonUtils.GsonToBean(payInfoJson, SendRedPocketBean.class));
						XLog.d("payInfo" + payInfo.toString());
						
						JobManager jobManager = TaskManager.getInstance().getJobManager();
						jobManager.addJobInBackground(new MoneySendJob(10, payInfo));
						break;
					}
					default:{
						XLog.e("广播参数传递错误。");
					}
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + Log.getStackTraceString(e));
		}
	}
}
