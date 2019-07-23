package com.gentcent.wechat.zzk.job;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gentcent.wechat.zzk.bean.SnsBean;
import com.gentcent.wechat.zzk.manager.FriendManager;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.util.DownloadUtil;
import com.gentcent.wechat.zzk.util.HttpUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.Tag;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 发朋友圈
 *
 * @author zuozhi
 * @since 2019-07-19
 */
public class SendSnsJob extends Job {
	private static String TAG = "SendSnsJob:  ";
	private static final int PRIORITY = 1;
	private SnsBean snsBean;
	private int mDelay;    //单位秒
	private List<String> imgPathList = new ArrayList<>();
	
	public SendSnsJob(int mDelay, SnsBean snsBean) {
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
			int type = snsBean.getType();
			if (type == 1) { //纯文本
			
			} else if (type == 2) { //带图片
				getPathList(0);
			} else if (type == 3) { //带视频
			
			} else if (type == 4) { //文章
			
			}
			Thread.sleep(mDelay * 1000);
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e(TAG + "错误:" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 递归调用，得到图片Path的集合
	 * @param i 递归出口
	 */
	private void getPathList(int i){
		if(i < snsBean.getImages().size()){
			downloadImg(i);
		}else{
			//全部下载完毕
			XLog.d(TAG + "全部下载完毕" + imgPathList.toString());
			ThreadPoolUtils.getInstance().a(new Runnable() {
				public void run() {
					StringBuilder sb = new StringBuilder();
					if (ObjectUtils.isNotEmpty((Collection) imgPathList)) {
						for (String str2 : imgPathList) {
							sb.append(",").append(str2);
						}
						if (sb.toString().startsWith(",")) {
							sb = new StringBuilder(sb.substring(1));
						}
					}
					String str = sb.toString();
					//开始发朋友圈
					openActivity(snsBean,str.split(","));
				}
			}, 2000, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * 打开发朋友圈页面(已经添加好图片了)（剩下的交给Hook去自动完成）
	 */
	private void openActivity(SnsBean snsBean,String[] strArr){
		StringBuilder sb = new StringBuilder();
		sb.append("FriendsCircleHook  sendsnsline  ");
		sb.append(snsBean.getLookUpType());
		XLog.d(sb.toString());
		try {
			StringBuilder sb2 = new StringBuilder();
			sb2.append("sendsnsline images is ");
			sb2.append(strArr.length);
			XLog.d(sb2.toString());
			Intent intent = new Intent();
			intent.putExtra("Select_Contact", snsBean.getRemind());
			intent.setClassName("com.tencent.mm", "com.tencent.mm.plugin.sns.ui.SnsUploadUI");
			intent.putExtra("Kdescription", snsBean.getContent());
			intent.putExtra("Ksnsupload_type", 0);
			intent.putExtra("shenshou", true);
//			intent.putExtra("shenshoutaskid", num);
			intent.putExtra("snsfriendsnsuploadui", true);
			ArrayList arrayList = new ArrayList();
			arrayList.clear();
			arrayList.addAll(Arrays.asList(strArr));
			intent.putExtra("LookUpType", snsBean.getLookUpType());
			intent.putExtra("LookFriendWxIdList", snsBean.getLookFriendWxIdList());
			intent.putExtra("SelfComment", snsBean.getSelfComment());
			intent.putStringArrayListExtra("sns_kemdia_path_list", arrayList);
			MainManager.activity.startActivity(intent);
		} catch (Exception e) {
			StringBuilder sb3 = new StringBuilder();
			sb3.append("执行界面操作失败：");
			sb3.append(Log.getStackTraceString(e));
			XLog.d(sb3.toString());
		}
	}
	
	/**
	 * 下载图片
	 * @param i 第几张图片
	 */
	private void downloadImg(final int i) {
		//先把图片下载到本地
		final String url = snsBean.getImages().get(i);
		String name = url.substring(url.lastIndexOf("/"));
		DownloadUtil.get().download(url, MyHelper.SDCARD_PATH+"sns-img/", name, new DownloadUtil.OnDownloadListener() {
			@Override
			public void onDownloadSuccess(File file) {
				XLog.d(TAG + "下载图片:" +url);
				imgPathList.add(file.getAbsolutePath());
				getPathList(i+1);
			}
			
			@Override
			public void onDownloading(int progress) {
//				XLog.d(TAG + "正在下载图片中:"+progress);
			}
			
			@Override
			public void onDownloadFailed(Exception e) {
				e.printStackTrace();
				XLog.e(TAG + "下载图片出错:" + Log.getStackTraceString(e));
			}
		});
	}
	
	@Override
	protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
	
	}
	
	@Override
	protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
		return null;
	}
}
