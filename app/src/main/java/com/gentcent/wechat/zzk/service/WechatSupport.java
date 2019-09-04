package com.gentcent.wechat.zzk.service;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WechatSupport {
	public static final List<String> supportNameList = new ArrayList(Arrays.asList("7.0.6"));
	
	public static String getAppVersionName() {
		return AppUtils.getAppVersionName("com.tencent.mm");
	}
	
	public static final boolean isSupport(String str) {
		return supportNameList.contains(str);
	}
	
	public static void checkVersion(final AppCompatActivity appCompatActivity) {
		XLog.d("checkWechatVersion activity is " + appCompatActivity);
		if (appCompatActivity == null) {
			XLog.d("checkWechatVersion activity is null");
			return;
		}
		String version = getAppVersionName();
		if (TextUtils.isEmpty(version)) {
			new CircleDialog.Builder().setTitle("提示").setText("请下载安装对应版本微信！").setCancelable(false).setCanceledOnTouchOutside(false).setNeutral("关闭", new View.OnClickListener() {
				public void onClick(View view) {
					appCompatActivity.finish();
				}
			}).show(appCompatActivity.getSupportFragmentManager());
		} else if (!isSupport(version)) {
			String text = "当前微信版本: " + version + "\n请卸载现有微信，并下载适用版微信，以得到更好的用户体验！";
			if (!appCompatActivity.isFinishing()) {
				new CircleDialog.Builder().setTitle("提示").setText(text).setCancelable(false).setCanceledOnTouchOutside(false).setNeutral("关闭", new View.OnClickListener() {
					public void onClick(View view) {
						appCompatActivity.finish();
					}
				}).show(appCompatActivity.getSupportFragmentManager());
			}
		}
	}
	
	public static synchronized void checkVersionInWechat(Activity activity) {
		synchronized (WechatSupport.class) {
			if (activity == null) {
				XLog.d("checkWechatVersion2 activity is null");
				return;
			}
			String version = getAppVersionName();
			if (!isSupport(version)) {
				if (!activity.isFinishing()) {
					ToastUtils.showLong("当前微信版本: " + version + "\n请卸载，并安装适用版微信！");
					ThreadPoolUtils.getInstance().a(new Runnable() {
						public void run() {
							AppUtils.exitApp();
						}
					}, 5000, TimeUnit.MILLISECONDS);
				}
			}
		}
	}
}
