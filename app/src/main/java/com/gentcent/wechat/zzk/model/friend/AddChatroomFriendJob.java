package com.gentcent.wechat.zzk.model.friend;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.model.friend.bean.AddNewFriend;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.Date;
import java.util.List;

public class AddChatroomFriendJob extends Job {
	public static final int PRIORITY = 1;
	private static final String TAG = "AddChatroomFriendJob";
	private AddNewFriend addNewFriend;
	
	@Override
	public void onCancel(int i, Throwable th) {
	}
	
	public RetryConstraint shouldReRunOnThrowable(Throwable th, int i, int i2) {
		return null;
	}
	
	protected AddChatroomFriendJob(String str) {
		super(new Params(PRIORITY).addTags(TAG).delayInMs(5000));
		this.addNewFriend = GsonUtils.GsonToBean(str, AddNewFriend.class);
	}
	
	@Override
	public void onAdded() {
		String localeString = new Date().toLocaleString();
		XLog.d(TAG + "AddChatroomFriendJob onAdded  data" + localeString);
	}
	
	@Override
	public void onRun() {
		addNewfriend(MainManager.activity, MainManager.wxLpparam, this.addNewFriend.FriendWxIds, this.addNewFriend.SayHi, this.addNewFriend.Chatroom, 0);
	}
	
	public static void addNewfriend(final Activity activity, final XC_LoadPackage.LoadPackageParam lpparam, final List<String> wxidList, final String explain, final String chatroom, int type) {
		XLog.d("addNewfriend wxid is " + wxidList + " explain is " + explain + " type is " + type);
		try {
			ThreadPoolUtils.getInstance().run(new Runnable() {
				public void run() {
					int i = 0;
					while (i < wxidList.size()) {
						try {
							Intent intent = new Intent(activity, lpparam.classLoader.loadClass("com.tencent.mm.plugin.profile.ui.ContactInfoUI"));
							intent.putExtra("Contact_User", wxidList.get(i));
							intent.putExtra("shen_shou", true);
							intent.putExtra("room_name", chatroom);
							intent.putExtra("Contact_Scene", 14);
							intent.putExtra("explain", explain);
							intent.putExtra("Contact_ChatRoomId", chatroom);
							AddFriendHook.c = 4;
							activity.startActivity(intent);
							Thread.sleep(10000);
							i++;
						} catch (Exception e) {
							XLog.d("addfriend run error is " + Log.getStackTraceString(e));
							return;
						}
					}
				}
			});
		} catch (Exception e) {
			XLog.d("addfriend error is " + Log.getStackTraceString(e));
		}
	}
}
