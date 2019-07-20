package com.gentcent.wechat.zzk.job;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.gentcent.wechat.zzk.manager.FriendManager;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author zuozhi
 * @since 2019-07-19
 */
public class AddFriendJob extends Job {
	
	private static final int PRIORITY = 5000;
	private int mDelay;	//单位秒
	private String mFriendId;	//好友id
	private ArrayList<String> mFriends;
	
	public AddFriendJob(int mDelay, String mFriendId, ArrayList<String> mFriends) {
		super(new Params(PRIORITY).persist());
		this.mDelay = mDelay;
		this.mFriendId = mFriendId;
		this.mFriends = mFriends;
	}
	
	@Override
	public void onAdded() {
	
	}
	
	@Override
	public void onRun() throws Throwable {
		try{
			XLog.d("add_friend");
			Intent intent2 = new Intent();
			intent2.setClassName("com.tencent.mm", "com.tencent.mm.plugin.fts.ui.FTSMainUI");
			intent2.setFlags(FLAG_ACTIVITY_NEW_TASK);
			MainManager.activity.startActivity(intent2);
			XLog.d("跳转到FTSMainUI SUCCESS");
			XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.plugin.fts.ui.FTSMainUI"), "c", FriendManager.activity, mFriendId);
			XLog.d("callStaticMethod | FTSMainUI");
			Thread.sleep(mDelay * 1000);
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("错误:" + e.getMessage());
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