package com.gentcent.wechat.zzk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
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
import com.gentcent.wechat.zzk.model.wallet.bean.SendRedPocketBean;
import com.gentcent.wechat.zzk.service.TaskManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

/**
 * 微信广播接收器
 */
public class ZzkReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, final Intent intent) {
		try {
			String act = intent.getStringExtra("act");
			if (act != null) {
				switch (act) {
					case "is_zzk_open":
						ThreadPoolUtils.getInstance().run(new Runnable() {
							@Override
							public void run() {
								MyHelper.writeLine("isZzkOpen", "true");
							}
						});
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
}
