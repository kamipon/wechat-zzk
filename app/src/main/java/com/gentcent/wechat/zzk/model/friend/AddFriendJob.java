package com.gentcent.wechat.zzk.model.friend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;

import java.util.ArrayList;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 添加好友
 *
 * @author zuozhi
 * @since 2019-07-19
 */
public class AddFriendJob extends Job {
	private static String TAG = "AddFriendJob:  ";
	private static final int PRIORITY = 5000;
	private int mDelay;    //单位秒
	private String mFriendId;    //好友id
	private String helloText;    //打招呼信息
	private ArrayList<String> mFriends;
	
	
	public AddFriendJob(int mDelay, String mFriendId, String helloText, ArrayList<String> mFriends) {
		super(new Params(PRIORITY).persist());
		this.mDelay = mDelay;
		this.mFriendId = mFriendId;
		this.mFriends = mFriends;
		this.helloText = helloText;
	}
	
	@Override
	public void onAdded() {
		XLog.d(TAG + "add on " + new Date().toLocaleString());
	}
	
	@Override
	public void onRun() {
		try {
			//添加好友打招呼语句
			MyHelper.writeLine("addFriendHelloText", helloText == null ? "" : helloText);
			
			Intent intent2 = new Intent();
			intent2.setClassName(HookParams.WECHAT_PACKAGE_NAME, HookParams.FTSMainUI);
			intent2.setFlags(FLAG_ACTIVITY_NEW_TASK);
			MainManager.activity.startActivity(intent2);
			XLog.d(TAG + "跳转到FTSMainUI SUCCESS");
			XLog.d(TAG + "FriendManager.activity:  " + FriendManager.activity);
			if (FriendManager.activity != null) {
				XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass(HookParams.FTSMainUI), HookParams.method_c, FriendManager.activity, mFriendId);
				XLog.d(TAG + "callStaticMethod | FTSMainUI");
			} else {
				Thread.sleep(mDelay * 1000);
				onRun();
				return;
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
