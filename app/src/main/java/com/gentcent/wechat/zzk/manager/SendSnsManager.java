package com.gentcent.wechat.zzk.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.bean.SendSnsBean;
import com.gentcent.wechat.zzk.util.DownloadUtil;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 朋友圈
 *
 * @author zuozhi
 * @since 2019-07-19
 */
public class SendSnsManager {
	private static String TAG = "SendSnsManager:  ";
	private SendSnsBean snsBean = null;
	private List<String> imgPathList = new ArrayList<>();
	private String videoPath;
	private String videoImgPath;
	
	private SendSnsManager() {
	}
	
	public SendSnsManager(SendSnsBean snsBean) {
		this.snsBean = snsBean;
	}
	
	/**
	 * type = 1
	 * 发送纯文本朋友圈
	 */
	public void sendSnsWithText() {
		try {
			XLog.d("FriendsCircleHook sendtextsns text is " + snsBean.getContent());
			Intent intent = new Intent();
			intent.setClassName(HookParams.WECHAT_PACKAGE_NAME, HookParams.SnsUploadUI);
			intent.putExtra("Ksnsupload_type", 9);
			intent.putExtra("Kdescription", snsBean.getContent());
			intent.putExtra("WechatForwarderText", true);
			intent.putExtra("zzk", true);
//			intent.putExtra("shenshoutaskid", num);
			intent.putExtra("snsfriendsnsuploadui", true);
			intent.putExtra("LookUpType", snsBean.getLookUpType());
			intent.putExtra("LookFriendWxIdList", snsBean.getLookFriendWxIdList());
			intent.putExtra("SelfComment", snsBean.getSelfComment());
			intent.putExtra("Select_Contact", snsBean.getRemind());
			MainManager.activity.startActivity(intent);
		} catch (Exception e) {
			XLog.d("执行界面操作失败：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * type = 2
	 * 发送带图片朋友圈
	 */
	public void sendSnsWithPic() {
		getPathList(0);
	}
	
	/**
	 * type = 2
	 * 递归调用，得到图片Path的集合
	 *
	 * @param i 递归出口
	 */
	private void getPathList(int i) {
		if (i < snsBean.getImages().size()) {
			downloadImg(i);
		} else {
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
					openActivity(snsBean, str.split(","));
				}
			}, 2000, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * type = 2
	 * 打开发朋友圈页面(已经添加好图片了)（剩下的交给Hook去自动完成）
	 */
	private void openActivity(SendSnsBean snsBean, String[] strArr) {
		XLog.d("FriendsCircleHook  sendsnsline  " + snsBean.getLookUpType());
		try {
			XLog.d("sendsnsline images is " + strArr.length);
			Intent intent = new Intent();
			intent.putExtra("Select_Contact", snsBean.getRemind());
			intent.setClassName(HookParams.WECHAT_PACKAGE_NAME, HookParams.SnsUploadUI);
			intent.putExtra("Kdescription", snsBean.getContent());
			intent.putExtra("Ksnsupload_type", 0);
			intent.putExtra("zzk", true);
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
			XLog.d("执行界面操作失败：" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * type = 2
	 * 下载图片
	 *
	 * @param i 第几张图片
	 */
	private void downloadImg(final int i) {
		//先把图片下载到本地
		final String url = snsBean.getImages().get(i);
		String name = url.substring(url.lastIndexOf("/"));
		DownloadUtil.get().download(url, MyHelper.SDCARD_PATH + "sns-img/", name, new DownloadUtil.OnDownloadListener() {
			@Override
			public void onDownloadSuccess(File file) {
				XLog.d(TAG + "下载图片:" + url);
				imgPathList.add(file.getAbsolutePath());
				getPathList(i + 1);
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
	
	/**
	 * type = 3
	 * 发送带视频朋友圈
	 */
	public void sendSnsWithVideo() {
		if (videoPath == null) {
			String url = snsBean.getVideo();
			downloadVideo(url);
		} else {
			try {
				XLog.d("sendsnsVideo videoPath is " + videoPath);
				Intent intent = new Intent();
				intent.setClassName(HookParams.WECHAT_PACKAGE_NAME, HookParams.SnsUploadUI);
				intent.putExtra("Ksnsupload_type", 14);
				intent.putExtra("Kdescription", snsBean.getContent());
				intent.putExtra("KSightThumbPath", videoImgPath);
				intent.putExtra("SendType", "SSRJVideo");
				intent.putExtra("KSightPath", videoPath);
				intent.putExtra("zzk", true);
//				intent.putExtra("shenshoutaskid", num);
				intent.putExtra("Select_Contact", snsBean.getRemind());
				intent.putExtra("sight_md5", FileUtils.getFileMD5(videoPath));
				intent.putExtra("KSnsPostManu", true);
				intent.putExtra("KTouchCameraTime", System.currentTimeMillis());
				intent.putExtra("LookUpType", snsBean.getLookUpType());
				intent.putExtra("LookFriendWxIdList", snsBean.getLookFriendWxIdList());
				intent.putExtra("SelfComment", snsBean.getSelfComment());
				intent.putExtra("snsfriendsnsuploadui", true);
				MainManager.activity.startActivity(intent);
			} catch (Exception e) {
				XLog.d("sendsnsVideo error is " + Log.getStackTraceString(e));
			}
		}
	}
	
	/**
	 * type = 3
	 * 下载视频
	 */
	private void downloadVideo(final String url) {
		//先把视频下载到本地
		final String name = url.substring(url.lastIndexOf("/"));
		DownloadUtil.get().download(url, MyHelper.SDCARD_PATH + "sns-video/", name, new DownloadUtil.OnDownloadListener() {
			@Override
			public void onDownloadSuccess(File file) {
				XLog.d(TAG + "下载视频:" + url);
				
				videoPath = file.getAbsolutePath();
				try {
					MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
					mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
					Bitmap frameAtTime = mediaMetadataRetriever.getFrameAtTime();
					Bitmap createBitmap = Bitmap.createBitmap(frameAtTime.getWidth(), frameAtTime.getHeight(), Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(createBitmap);
					canvas.drawColor(-1);
					canvas.drawBitmap(frameAtTime, 0.0f, 0.0f, null);
					String str1 = MyHelper.SDCARD_PATH + "sns-video/" + System.currentTimeMillis() + "_cover.jpg";
					FileOutputStream fileOutputStream = new FileOutputStream(new File(str1));
					createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
					fileOutputStream.close();
					videoImgPath = str1.toString();
				} catch (Exception e) {
					e.printStackTrace();
					XLog.e(TAG + "create video img error:" + Log.getStackTraceString(e));
				}
				XLog.d(TAG + "videoPath:" + videoPath);
				XLog.d(TAG + "videoImgPath:" + videoImgPath);
				sendSnsWithVideo();
			}
			
			@Override
			public void onDownloading(int progress) {
//				XLog.d(TAG + "正在下载视频中:" + progress);
			}
			
			@Override
			public void onDownloadFailed(Exception e) {
				e.printStackTrace();
				XLog.e(TAG + "下载视频出错:" + Log.getStackTraceString(e));
			}
		});
	}
	
	/**
	 * type = 4
	 * 发送带链接朋友圈
	 */
	public void sendSnsWithArticle() {
		XLog.d("FriendsCircleHook  sendlinksns  " + snsBean.getLookUpType());
		try {
			Intent intent = new Intent();
			intent.setClassName(HookParams.WECHAT_PACKAGE_NAME, HookParams.SnsUploadUI);
			intent.putExtra("Kdescription", snsBean.getContent());
			intent.putExtra("Ksnsupload_link", snsBean.getArticleUrl());
			intent.putExtra("Ksnsupload_title", snsBean.getArticleTitle());
			intent.putExtra("Ksnsupload_imgurl", snsBean.getArticleImage());
			intent.putExtra("Ksnsupload_type", 1);
			intent.putExtra("zzk", true);
//			intent.putExtra("shenshoutaskid", num);
			intent.putExtra("Select_Contact", snsBean.getRemind());
			intent.putExtra("snsfriendsnsuploadui", true);
			intent.putExtra("LookUpType", snsBean.getLookUpType());
			intent.putExtra("LookFriendWxIdList", snsBean.getLookFriendWxIdList());
			intent.putExtra("SelfComment", snsBean.getSelfComment());
			MainManager.activity.startActivity(intent);
		} catch (Exception e) {
			XLog.d("执行界面操作失败：" + Log.getStackTraceString(e));
		}
	}
	
}
