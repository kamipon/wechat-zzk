package com.gentcent.wechat.zzk.model.message;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ImageUtils;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.util.DownloadUtil;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.VoiceJNI2;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.gentcent.wechat.zzk.model.message.messageDao.getCurMsgId;
import static com.gentcent.wechat.zzk.model.message.messageDao.getGifCurMsgId;
import static com.gentcent.wechat.zzk.model.message.messageDao.getLastMsgId;

/**
 * 管理发送消息任务
 *
 * @author zuozhi
 * @since 2019-07-29
 */
public class SendMessageManager {
	private static final Object textLock = new Object();
	private static final Object imgLock = new Object();
	private static final Object voiceLock = new Object();
	private static final Object videoLock = new Object();
	private static final Object fileAndArticleLock = new Object();
	private static final Object gifLock = new Object();
	private static final Object mp3ToAmrLock = new Object();
	private static HashMap<String, Info> managerMap = new HashMap<>();
	
	public static class Info {
		public String touser;
		public String serviceGuid;
		public int type;
		public long currentTimeMillis;
		public long msgid;
		public long lastMsgId;
		public String text;
		public boolean h = false;
	}
	
	/**
	 * 发送消息入口（入口)
	 */
	public static void sendMessage(final MessageBean sm) {
		String url = null;
		String name = null;
		int type = sm.getType();
		if (type == 0) {
			sendMessageDispatcher(sm, null);
			return;
		} else if (type == 1 || type == 2 || type == 3) {
			url = sm.getContent();
			name = MyHelper.getName(url);
		} else if (type == 7) {
			url = sm.getLinkImg();
			name = MyHelper.getName(url);
		} else if (type == 8) {
			url = sm.getContent();
			name = url.substring(url.lastIndexOf('/') + 1);
		} else if (type == 9) {
			sendMessageDispatcher(sm, null);
			return;
		} else {
			XLog.e("未知消息类型：" + type);
		}
		
		//先下载资源到本地
		try {
			assert url != null;
			XLog.d("下载文件:" + url);
			XLog.d("文件扩展名:" + name);
			final String finalUrl = url;
			final String finalName = name;
			ThreadPoolUtils.getInstance().run(new Runnable() {
				@Override
				public void run() {
					DownloadUtil.get().download(finalUrl, MyHelper.getDir("message"), finalName, new DownloadUtil.OnDownloadListener() {
						@Override
						public void onDownloadSuccess(File file) {
							String absolutePath = file.getAbsolutePath();
							XLog.d("下载文件完成:" + absolutePath);
							sendMessageDispatcher(sm, absolutePath);
						}
						
						@Override
						public void onDownloading(int progress) {
						}
						
						@Override
						public void onDownloadFailed(Exception e) {
							e.printStackTrace();
							XLog.e("下载文件出错:" + Log.getStackTraceString(e));
						}
					});
				}
			});
		} catch (Exception e) {
			XLog.d("download file Exception is " + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 发送消息入口（分类器）
	 */
	private static void sendMessageDispatcher(MessageBean sm, @Nullable String path) {
		final String extName = getExtName(sm.getContent());
		String[] split = sm.getFriendWxId().split("\\|");
		int type = sm.getType();
		switch (type) {
			case 0:    //文本
				SendMessageHandler.sendText(sm);
				break;
			case 1:    //图片
				for (String username : split) {
					if (TextUtils.equals(extName, ".gif")) {
						SendMessageHandler.sendGif(sm.getServiceGuid(), username, path);
					} else {
						SendMessageHandler.sendImg(sm.getServiceGuid(), username, path);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:    //语音
				assert path != null;
				boolean b = mp3ToAmr(path);
				if (b) {
					SendMessageHandler.sendVoice(sm.getServiceGuid(), path, sm.getFriendWxId());
				} else {
					XLog.e("下载音频格式不正确：" + path.substring(path.lastIndexOf(".")));
				}
				break;
			case 3:    //视频
				for (String username : split) {
					SendMessageHandler.sendVideo(sm.getServiceGuid(), path, username);
				}
				break;
			case 7:    //链接
				for (String username : split) {
					SendMessageHandler.sendArticle(sm, ImageUtils.getBitmap(path), username);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						XLog.e(Log.getStackTraceString(e));
					}
				}
				break;
			case 8:    //文件
				for (String username : split) {
					SendMessageHandler.sendFile(sm.getServiceGuid(), path, sm.getFileName(), username);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						XLog.e(Log.getStackTraceString(e));
					}
				}
				break;
			case 9:    //群聊@好友
				SendMessageHandler.sendAppointText(sm);
				break;
		}
	}
	
	/**
	 * MP3转AMR
	 */
	private static boolean mp3ToAmr(String path) {
		if (path.toUpperCase().endsWith(".AMR")) return true;
		if (!path.toUpperCase().endsWith(".MP3")) return false;
		int afterDuration;
		try {
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			int duration = mediaPlayer.getDuration();
			XLog.d("VoiceSend: " + "mp3 len:" + duration);
			if (duration <= 0) {
				XLog.d("VoiceSend: " + "mediaPlayer len:" + duration);
				return false;
			}
			int durationSec = duration / 1000;
			if (durationSec < 10) {
				afterDuration = 25000;
			} else {
				double d2 = (double) durationSec;
				afterDuration = (int) (((((60.0d - d2) / 50.0d) * 17000.0d) / 2.5d) + 8000.0d);
			}
			String replace = path.replace(".mp3", ".pcm");
			String replace2 = path.replace(".mp3", ".amr");
			new File(replace).delete();
			new File(replace2).delete();
			synchronized (mp3ToAmrLock) {
				int ffmpegCmd = VoiceJNI2.ffmpegCmd(VoiceJNI2.transformAudio(path, replace));
				XLog.d("VoiceSend: " + "mp3 result:" + ffmpegCmd);
				if (ffmpegCmd != 0) {
					XLog.d("VoiceSend: " + "mp3 to pcm fail");
					return false;
				} else if (VoiceJNI2.pcm_to_amr(replace, replace2, afterDuration) != 0) {
					XLog.d("VoiceSend: " + "pcm to amr fail");
					return false;
				}
			}
		} catch (Throwable th) {
			XLog.d("VoiceSend " + "error:" + Log.getStackTraceString(th));
		}
		return true;
	}
	
	/**
	 * 获取文件扩展名
	 *
	 * @param path 路径
	 */
	private static String getExtName(String path) {
		String ext = "";
		int lastIndexOf = path.lastIndexOf(".");
		if (lastIndexOf > 0) {
			ext = path.substring(lastIndexOf);
		}
		if (path.endsWith(".apk.1")) {
			return ".apk";
		}
		return path.endsWith(".pic") ? ".jpg" : ext;
	}
	
	/**
	 * 执行线程，并轮询查询数据库看是否成功发送
	 *
	 * @param serviceGuid 服务器id
	 * @param type        类型
	 * @param friendWxId  接受者的微信id
	 * @param content     信息内容
	 */
	public static void run(String serviceGuid, Runnable runnable, int type, String friendWxId, String content) {
		XLog.d("runLock serviceGuid is " + serviceGuid);
		if (TextUtils.isEmpty(serviceGuid)) {
			runnable.run();
			return;
		}
		switch (type) {
			case 1: {
				synchronized (textLock) {
					long lastMsgId = getLastMsgId(1, friendWxId);
					long currentTimeMillis = System.currentTimeMillis();
					runnable.run();
					normalHandle(serviceGuid, type, friendWxId, content, currentTimeMillis, lastMsgId);
				}
				break;
			}
			case 3: {
				synchronized (imgLock) {
					long lastMsgId = getLastMsgId(3, friendWxId);
					long currentTimeMillis = System.currentTimeMillis();
					runnable.run();
					normalHandle(serviceGuid, type, friendWxId, content, currentTimeMillis, lastMsgId);
				}
				break;
			}
			case 5: {
				synchronized (fileAndArticleLock) {
					long lastMsgId = getLastMsgId(49, friendWxId);
					long currentTimeMillis = System.currentTimeMillis();
					runnable.run();
					normalHandle(serviceGuid, 49, friendWxId, content, currentTimeMillis, lastMsgId);
				}
				break;
			}
			case 34: {
				synchronized (voiceLock) {
					long lastMsgId = getLastMsgId(34, friendWxId);
					long currentTimeMillis = System.currentTimeMillis();
					runnable.run();
					normalHandle(serviceGuid, type, friendWxId, content, currentTimeMillis, lastMsgId);
				}
				break;
			}
			case 43: {
				synchronized (videoLock) {
					long lastMsgId = getLastMsgId(43, friendWxId);
					long currentTimeMillis = System.currentTimeMillis();
					runnable.run();
					normalHandle(serviceGuid, type, friendWxId, content, currentTimeMillis, lastMsgId);
				}
				break;
			}
			case 49: {
				synchronized (fileAndArticleLock) {
					long lastMsgId = getLastMsgId(49, friendWxId);
					long currentTimeMillis = System.currentTimeMillis();
					runnable.run();
					normalHandle(serviceGuid, type, friendWxId, content, currentTimeMillis, lastMsgId);
				}
				break;
			}
			case 99: {
				synchronized (gifLock) {
					long lastMsgId = getLastMsgId(99, friendWxId);
					long currentTimeMillis = System.currentTimeMillis();
					runnable.run();
					gifHandle(serviceGuid, type, friendWxId, content, currentTimeMillis, lastMsgId);
				}
				break;
			}
		}
		updateConversation(friendWxId);
	}
	
	/**
	 * 正常类型消息轮询处理器 (text,img,voice,video,file,article)
	 *
	 * @param serviceGuid       服务器id
	 * @param type              类型
	 * @param friendWxId        接受者的微信id
	 * @param content           信息内容
	 * @param currentTimeMillis 当前毫秒数
	 * @param lastMsgId         当前最新的msgId
	 */
	private static void normalHandle(String serviceGuid, int type, String friendWxId, String content, long currentTimeMillis, long lastMsgId) {
		Info info = new Info();
		info.serviceGuid = serviceGuid;
		info.currentTimeMillis = currentTimeMillis;
		info.touser = friendWxId;
		info.type = type;
		info.text = content;
		info.lastMsgId = lastMsgId;
		@SuppressLint("SimpleDateFormat") SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
		XLog.d("SendManager Mysned Time: " + time.format(currentTimeMillis));
		int maxTime = getMaxTime(type);
		int curTime = 0;
		while (true) {
			if (curTime > maxTime) {
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				XLog.e("normalHandle" + Log.getStackTraceString(e));
			}
			curTime += 50;
			long msgId = getCurMsgId(info);
			if (msgId != 0) {
				info.msgid = msgId;
				addToMap(info);
				break;
			}
		}
		XLog.d("SendManager Mysned info.msgid =" + info.msgid + " text :" + content);
	}
	
	/**
	 * GIF类型消息轮询处理器
	 *
	 * @param serviceGuid       服务器id
	 * @param type              类型
	 * @param friendWxId        接受者的微信id
	 * @param content           信息内容
	 * @param currentTimeMillis 当前毫秒数
	 * @param lastMsgId         当前最新的msgId
	 */
	private static void gifHandle(String serviceGuid, int type, String friendWxId, String content, long currentTimeMillis, long lastMsgId) {
		Info info = new Info();
		info.serviceGuid = serviceGuid;
		info.currentTimeMillis = currentTimeMillis;
		info.touser = friendWxId;
		info.type = type;
		info.lastMsgId = lastMsgId;
		info.text = content;
		@SuppressLint("SimpleDateFormat") SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
		XLog.d("SendManager" + " Mysned Time2: " + time.format(currentTimeMillis) + " serviceGuid is " + serviceGuid);
		int maxTime = getMaxTime(type);
		int curTime = 0;
		while (true) {
			if (curTime > maxTime) {
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				XLog.e("gifHandle" + Log.getStackTraceString(e));
			}
			curTime += 50;
			long msgId = getGifCurMsgId(info);
			if (msgId != 0) {
				info.msgid = msgId;
				addToMap(info);
				break;
			}
		}
		XLog.d("SendManager" + "addMessage_gif Mysned info.msgid =" + info.msgid + " text :" + content);
	}
	
	/**
	 * 获取定义的轮询超时时间
	 *
	 * @param type 类型
	 * @return 定义的轮询超时时间
	 */
	private static int getMaxTime(int type) {
		if (!(type == 1 || type == 3)) {
			if (type == 5) {
				return 500;
			}
			if (type != 34) {
				return (type == 43 || type == 49) ? 1000 : type != 99 ? 0 : 1000;
			}
		}
		return 100;
	}
	
	/**
	 * 吧info保存到map中
	 *
	 * @param info 内部的封装类
	 */
	private static void addToMap(Info info) {
		if (managerMap == null) {
			managerMap = new HashMap<>();
		}
		HashMap<String, Info> hashMap = managerMap;
		hashMap.put("" + info.msgid, info);
	}
	
	/**
	 * 更新对话
	 *
	 * @param friendWxId 接受者的微信Id
	 */
	private static void updateConversation(String friendWxId) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("unReadCount", 0);
		contentValues.put("attrflag", 0);
		contentValues.put("atCount", 0);
		contentValues.put("UnReadInvite", 0);
		contentValues.put("unReadMuteCount", 0);
		String[] idArr = {friendWxId};
		WcdbHolder.excute("rconversation", contentValues, "username= ?", idArr);
		ContentValues contentValues2 = new ContentValues();
		contentValues2.put("atCount", 0);
		contentValues2.put("UnReadInvite", 0);
		WcdbHolder.excute("rconversation", contentValues2, "username= ?", idArr);
	}
	
	public static int getStatusByMsgId(long msgId) {
		try {
			String sql = "SELECT status FROM message WHERE msgId = " + msgId;
			Cursor cur = WcdbHolder.excute(sql, "EnMicroMsg.db");
			if (cur == null) {
				return 0;
			}
			cur.moveToFirst();
			if (!cur.isAfterLast()) {
				int i = cur.getInt(cur.getColumnIndex("status"));
				cur.close();
				return i;
			}
			cur.close();
			return 0;
		} catch (Throwable th) {
			XLog.d("SendManager getCurSendId e:" + Log.getStackTraceString(th));
			return 0;
		}
	}
	
}
