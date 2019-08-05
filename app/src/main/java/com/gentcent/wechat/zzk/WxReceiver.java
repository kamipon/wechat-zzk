package com.gentcent.wechat.zzk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.gentcent.wechat.zzk.model.message.bean.SendMessageBean;
import com.gentcent.wechat.zzk.model.sns.bean.SendSnsBean;
import com.gentcent.wechat.zzk.model.friend.AddFriendJob;
import com.gentcent.wechat.zzk.model.wallet.MoneySendJob;
import com.gentcent.wechat.zzk.model.sns.SendSnsJob;
import com.gentcent.wechat.zzk.model.message.SendMessageManager;
import com.gentcent.wechat.zzk.service.TaskManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.model.wallet.Dibs;
import com.gentcent.wechat.zzk.model.wallet.PayInfo;
import com.gentcent.wechat.zzk.model.wallet.bean.SendRedPocketBean;
import com.gentcent.wechat.zzk.model.syncinfo.SyncInfo;
import com.gentcent.wechat.zzk.wcdb.UserDao;

/**
 * 微信广播接收器
 */
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
						SendSnsBean snsBean = GsonUtils.GsonToBean(intent.getStringExtra("snsJson"), SendSnsBean.class);
						JobManager jobManager = TaskManager.getInstance().getJobManager();
						jobManager.addJobInBackground(new SendSnsJob(25, snsBean));
						break;
					}
					case "sync_info": {
						SyncInfo.syncInfo();
						break;
					}
					case "send_wallet_notice": {
						Dibs.requestDataIsRe(MainManager.wxLpparam, true);
						break;
					}
					case "send_redpocket": {
						PayInfo payInfo = PayInfo.revertSendRedPocketBeanToPayInfo(GsonUtils.GsonToBean(intent.getStringExtra("payInfoJson"), SendRedPocketBean.class));
						XLog.d("payInfo" + payInfo.toString());
						JobManager jobManager = TaskManager.getInstance().getJobManager();
						jobManager.addJobInBackground(new MoneySendJob(10, payInfo));
						break;
					}
					case "bind_weixin": {
						UserDao.getMyInfo();
						break;
					}
					default: {
						XLog.e("广播参数传递错误。");
					}
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + Log.getStackTraceString(e));
		}
	}
}
