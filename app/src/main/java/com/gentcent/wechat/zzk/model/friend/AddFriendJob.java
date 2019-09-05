package com.gentcent.wechat.zzk.model.friend;


import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.service.TaskStateManager;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AddFriendJob extends Job {
	public static final int PRIORITY = 5000;
	int mDelay;
	String mFriendId;
	ArrayList<String> mFriends;
	int mIndex;
	int mTaskId;
	
	public void onCancel(int i, Throwable th) {
	}
	
	public RetryConstraint shouldReRunOnThrowable(Throwable th, int i, int i2) {
		return null;
	}
	
	public AddFriendJob(ArrayList mFriends, int mIndex, String mFriendId, int mTaskId, int mDelay) {
		super(new Params(PRIORITY).addTags("AddFriendJob").delayInMs(2000).persist());
		this.mFriends = mFriends;
		this.mIndex = mIndex;
		this.mTaskId = mTaskId;
		this.mDelay = mDelay;
		this.mFriendId = mFriendId;
	}
	
	public void onAdded() {
		XLog.d("AddFriendJob onAdded mFriendId is " + this.mFriendId + "  mDelay" + this.mDelay + "mIndex" + this.mIndex);
	}
	
	public void onRun() throws Throwable {
		XLog.d("AddFriendJob onRun  mIndex" + this.mIndex);
		if (!FriendManager.mIsAddFriend) {
			if (StringUtils.equals("true", MyHelper.readLine("FriendManager.mIsAddFriend"))) {
				FriendManager.mIsAddFriend = true;
				XLog.d("AddFriendJob FriendManager.mIsAddFriend = true ");
			}
			if (!FriendManager.mIsAddFriend) {
				XLog.d("AddFriendJob  没有待执行 addfriend job ");
				return;
			} else if (FriendManager.mFTSMainUI == null) {
				FTSMainUI.openUI();
				XLog.d("AddFriendJob FriendManager.mFTSMainUI get ");
				Thread.sleep(4000);
			}
		}
		if (ObjectUtils.isNotEmpty(MainManager.wxLpparam) && ObjectUtils.isNotEmpty(FriendManager.mFTSMainUI) && ObjectUtils.isNotEmpty(this.mFriendId) && this.mDelay >= 0) {
			XLog.d("AddFriendJob onRun enter mFriendId is " + this.mFriendId + " mDelay" + this.mDelay);
			FriendManager.taskId = this.mTaskId;
			ThreadPoolUtils.getInstance().run(
					new Runnable() {
						public void run() {
							FriendManager.a(MainManager.wxLpparam, FriendManager.mFTSMainUI, AddFriendJob.this.mFriendId);
						}
					});
			if (this.mIndex >= this.mFriends.size() - 1) {
				XLog.d("AddFriendJob mIndex " + this.mIndex + "mFriends .size ::" + (this.mFriends.size() - 1));
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						FriendManager.mIsAddFriend = false;
						MyHelper.writeLine("FriendManager.mIsAddFriend", FriendManager.mIsAddFriend + "");
						FriendManager.mFTSMainUI = null;
						TaskStateManager.b(AddFriendJob.this.mTaskId);
					}
				}, 20000, TimeUnit.MILLISECONDS);
			}
			XLog.d("AddFriendJob onRun searchFriend start mFriendId is " + this.mFriendId);
			Thread.sleep((long) (this.mDelay + 15000));
			XLog.d("AddFriendJob  mDelay over");
		}
	}
}
