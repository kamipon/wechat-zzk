package com.gentcent.wechat.zzk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wallet.LuckyMoney;
import com.gentcent.wechat.zzk.wallet.PayInfo;
import com.gentcent.wechat.zzk.wallet.Remittance;

import java.util.Date;

/**
 * 转账发红包
 *
 * @author zuozhi
 * @since 2019-07-19
 */
public class MoneySendJob extends Job {
	private static String TAG = "MoneySendJob:  ";
	private static final int PRIORITY = 5000;
	private int mDelay;    //单位秒
	private PayInfo payInfo; //转账信息
	
	
	public MoneySendJob(int mDelay, PayInfo payInfo) {
		super(new Params(PRIORITY).persist());
		this.mDelay = mDelay;
		this.payInfo = payInfo;
	}
	
	@Override
	public void onAdded() {
		XLog.d(TAG + "add on " + new Date().toLocaleString());
	}
	
	@Override
	public void onRun() {
		try {
			XLog.d("MoneySendManager sendmoney " + payInfo);
			if (payInfo != null) {
				switch (payInfo.type) {
					case 0:
						Remittance.a(MainManager.wxLpparam, payInfo);
						break;
					case 1:
						LuckyMoney.a(MainManager.wxLpparam, payInfo);
						break;
					case 2:
						LuckyMoney.b(MainManager.wxLpparam, payInfo);
						break;
				}
			}
			Thread.sleep(mDelay * 1000);
		} catch (Exception e) {
			XLog.e(TAG + "错误:" + Log.getStackTraceString(e));
		}
	}
	
	@Override
	protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
	
	}
	
	@Override
	protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
		return null;
	}
}
