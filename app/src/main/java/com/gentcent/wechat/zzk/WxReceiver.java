package com.gentcent.wechat.zzk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.blankj.utilcode.util.AppUtils;
import com.gentcent.wechat.zzk.model.friend.AddFriendJob;
import com.gentcent.wechat.zzk.model.message.SendMessageManager;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.model.sns.SendSnsJob;
import com.gentcent.wechat.zzk.model.sns.bean.SendSnsBean;
import com.gentcent.wechat.zzk.model.syncinfo.SyncInfo;
import com.gentcent.wechat.zzk.model.wallet.Dibs;
import com.gentcent.wechat.zzk.model.wallet.MoneySendJob;
import com.gentcent.wechat.zzk.model.wallet.ReceivableManger;
import com.gentcent.wechat.zzk.model.wallet.ReceiverLuckyMoney;
import com.gentcent.wechat.zzk.model.wallet.bean.PayInfo;
import com.gentcent.wechat.zzk.model.wallet.bean.ReceiveRedPocketBean;
import com.gentcent.wechat.zzk.model.wallet.bean.SendRedPocketBean;
import com.gentcent.wechat.zzk.service.TaskManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.concurrent.TimeUnit;

/**
 * 微信广播接收器
 */
public class WxReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, final Intent intent) {
		try {
			if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				MyHelper.writeLine("isZzkOpen", "false");
				if (MainManager.activity != null) {
					Context context2 = MainManager.activity;
					Intent intent2 = new Intent("ZzkAction");
					intent2.putExtra("act", "is_zzk_open");
					context2.sendBroadcast(intent2);
					ThreadPoolUtils.getInstance().a(new Runnable() {
						@Override
						public void run() {
							String s = MyHelper.readLine("isZzkOpen", "false");
							if (!Boolean.valueOf(s)) {
								AppUtils.launchApp(HookParams.MY_PACKAGE_NAME);
							}
						}
					}, 10000, TimeUnit.MILLISECONDS);
				}
			}
			String act = intent.getStringExtra("act");
			if (act != null) {
				//添加好友
				//发送朋友圈
				switch (act) {
					case "send_message": {
						MessageBean sendMsgBean = GsonUtils.GsonToBean(intent.getStringExtra("sendmsgbean"), MessageBean.class);
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
						String serverId = intent.getStringExtra("serverId");
						Dibs.requestDataIsRe(true, serverId);
						break;
					}
					case "send_redpocket": {
						PayInfo payInfo = PayInfo.revertSendRedPocketBeanToPayInfo(GsonUtils.GsonToBean(intent.getStringExtra("payInfoJson"), SendRedPocketBean.class));
						XLog.d("payInfo" + payInfo.toString());
						JobManager jobManager = TaskManager.getInstance().getJobManager();
						jobManager.addJobInBackground(new MoneySendJob(10, payInfo));
						break;
					}
					case "transfer_refund": { // 退款（转账）
						ReceivableManger.refund(Long.parseLong(intent.getStringExtra("msgId")));
						break;
					}
					case "transfer_receive": { //收款（转账）
						ReceivableManger.recieve(Long.parseLong(intent.getStringExtra("msgId")));
						break;
					}
					case "group_recevice":
						ReceiverLuckyMoney.groupRecevice(intent.getStringExtra("sessionName"), intent.getStringExtra("linkUrl"));
						break;
					case "personal_recevice":
						ReceiverLuckyMoney.personalRecevice(intent.getStringExtra("sessionName"), intent.getStringExtra("linkUrl"));
						break;
					case "is_wechat_open":
						isWechatOpen();
						break;
					default: {
						XLog.e("广播参数传递错误。");
					}
				}
			}
		} catch (Exception e) {
			XLog.e("错误:" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 检测微信环境
	 */
	private void isWechatOpen() {
		ThreadPoolUtils.getInstance().run(new Runnable() {
			@Override
			public void run() {
				MyHelper.writeLine("isWechatOpen", "true");
			}
		});
	}
}
