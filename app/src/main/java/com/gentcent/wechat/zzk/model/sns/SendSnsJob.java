package com.gentcent.wechat.zzk.model.sns;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.gentcent.wechat.zzk.model.sns.bean.SendSnsBean;
import com.gentcent.wechat.zzk.model.sns.SendSnsManager;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.Date;

/**
 * 发朋友圈
 *
 * @author zuozhi
 * @since 2019-07-19
 */
public class SendSnsJob extends Job {
	private static String TAG = "SendSnsJob:  ";
	private static final int PRIORITY = 1;
	private SendSnsBean snsBean;
	private int mDelay;    //单位秒
	
	public SendSnsJob(int mDelay, SendSnsBean snsBean) {
		super(new Params(PRIORITY).persist());
		this.mDelay = mDelay;
		this.snsBean = snsBean;
	}
	
	@Override
	public void onAdded() {
		XLog.d(TAG + "add on " + new Date().toLocaleString());
	}
	
	@Override
	public void onRun() throws Throwable {
		try {
			XLog.d(TAG + "send_sns");
			
			SendSnsManager snsManager = new SendSnsManager(snsBean);
			
			int type = snsBean.getType();
			if (type == 1) { //纯文本
				snsManager.sendSnsWithText();
			} else if (type == 2) { //带图片
				snsManager.sendSnsWithPic();
			} else if (type == 3) { //带视频
				snsManager.sendSnsWithVideo();
			} else if (type == 4) { //文章
				snsManager.sendSnsWithArticle();
			}
			Thread.sleep(mDelay * 1000);
		} catch (Exception e) {
			e.printStackTrace();
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
