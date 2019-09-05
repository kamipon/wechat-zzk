package com.gentcent.wechat.zzk.model.friend;

import android.content.Intent;
import android.util.Log;

import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.util.XLog;

public class FTSMainUI {
	public static void openUI() {
		try {
			Intent intent = new Intent();
			intent.setClassName("com.tencent.mm", "com.tencent.mm.plugin.fts.ui.FTSMainUI");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MainManager.activity.startActivity(intent);
			XLog.d("openMainSearchUi start FTSMainUI success");
		} catch (Exception e) {
			XLog.e("openMainSearchUi 执行界面操作失败：" + Log.getStackTraceString(e));
		}
	}
}
