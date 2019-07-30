package com.gentcent.wechat.zzk.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.gentcent.wechat.zzk.bean.SendMessageBean;
import com.gentcent.wechat.zzk.handler.SendMessageHandler;
import com.gentcent.wechat.zzk.util.DownloadUtil;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.VoiceJNI2;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * 管理发送消息任务
 *
 * @author zuozhi
 * @since 2019-07-29
 */
public class SendMessageManager {
	private static Object a = new Object();
	private static Object b = new Object();
	private static Object c = new Object();
	private static Object d = new Object();
	private static Object e = new Object();
	private static Object f = new Object();
	private static Object g = new Object();
	private static final Object mp3ToAmrLock = new Object();
	private static HashMap<String, C0415a> h = new HashMap<>();
	
	public static class C0415a {
		public String a;
		public String b;
		public int c;
		public long d;
		public long e;
		public long f;
		public String g;
		public boolean h = false;
	}
	
	/**
	 * 发送图片入口（下载）
	 */
	public static void sendImg(final SendMessageBean sendMessageBean) {
		try {
			XLog.d("发送图片");
			final String extName = getExtName(sendMessageBean.getContent());
			XLog.d("发送图片:  文件扩展名:" + extName);
			final String url = sendMessageBean.getContent();
			final String name = url.substring(url.lastIndexOf("/"));
			ThreadPoolUtils.getInstance().run(new Runnable() {
				@Override
				public void run() {
					DownloadUtil.get().download(url, MyHelper.getDir("message"), MyHelper.getName(url), new DownloadUtil.OnDownloadListener() {
						@Override
						public void onDownloadSuccess(File file) {
							String absolutePath = file.getAbsolutePath();
							XLog.d("下载文件完成:" + absolutePath);
							
							String[] split = sendMessageBean.getFriendWxId().split("\\|");
							for (String s : split) {
								if (TextUtils.equals(extName, ".gif")) {
									SendMessageHandler.sendGif(sendMessageBean.getServiceGuid(), s, absolutePath);
								} else {
									SendMessageHandler.sendImg(sendMessageBean.getServiceGuid(), s, absolutePath);
								}
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
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
			XLog.d("send img Exception is " + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 发送语音入口（下载）
	 */
	public static void sendVoice(final SendMessageBean sendMessageBean) {
		try {
			XLog.d("发送语音");
			final String url = sendMessageBean.getContent();
			final String name = url.substring(url.lastIndexOf("/"));
			ThreadPoolUtils.getInstance().run(new Runnable() {
				@Override
				public void run() {
					DownloadUtil.get().download(url, MyHelper.getDir("message"), MyHelper.getName(url), new DownloadUtil.OnDownloadListener() {
						@Override
						public void onDownloadSuccess(File file) {
							String absolutePath = file.getAbsolutePath();
							XLog.d("下载文件完成:" + absolutePath);
							boolean b = mp3ToAmr(absolutePath);
							if (b) {
								SendMessageHandler.sendVoice(sendMessageBean.getServiceGuid(), absolutePath, sendMessageBean.getFriendWxId());
							} else {
								XLog.e("下载音频格式不正确：" + absolutePath.substring(url.lastIndexOf(".")));
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
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
			XLog.d("send img Exception is " + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * MP3转AMR
	 */
	public static boolean mp3ToAmr(String path) {
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
	
	public static void a(String str, Runnable runnable, int i, String str2, String str3) {
		XLog.d("runLock serviceGuid is " + str);
		if (TextUtils.isEmpty(str)) {
			runnable.run();
			return;
		}
		if (i == 1) {
			synchronized (a) {
				long a2 = a(1, str2);
				long currentTimeMillis = System.currentTimeMillis();
				runnable.run();
				b(str, i, str2, str3, currentTimeMillis, a2);
			}
		} else if (i == 3) {
			synchronized (b) {
				long a3 = a(3, str2);
				long currentTimeMillis2 = System.currentTimeMillis();
				runnable.run();
				b(str, i, str2, str3, currentTimeMillis2, a3);
			}
		} else if (i == 5) {
			synchronized (f) {
				long currentTimeMillis3 = System.currentTimeMillis();
				long a4 = a(49, str2);
				runnable.run();
				a(str, i, str2, str3, currentTimeMillis3, a4);
			}
		} else if (i == 34) {
			synchronized (c) {
				long currentTimeMillis4 = System.currentTimeMillis();
				long a5 = a(34, str2);
				runnable.run();
				b(str, i, str2, str3, currentTimeMillis4, a5);
			}
		} else if (i == 43) {
			synchronized (d) {
				long a6 = a(43, str2);
				long currentTimeMillis5 = System.currentTimeMillis();
				runnable.run();
				b(str, i, str2, str3, currentTimeMillis5, a6);
			}
		} else if (i == 49) {
			synchronized (f) {
				long a7 = a(49, str2);
				long currentTimeMillis6 = System.currentTimeMillis();
				runnable.run();
				b(str, i, str2, str3, currentTimeMillis6, a7);
			}
		} else if (i == 99) {
			synchronized (g) {
				long currentTimeMillis7 = System.currentTimeMillis();
				long a8 = a(99, str2);
				runnable.run();
				c(str, i, str2, str3, currentTimeMillis7, a8);
			}
		}
		b(str2);
	}
	
	private static void a(String str, int i, String str2, String str3, long j, long j2) {
		C0415a aVar = new C0415a();
		aVar.b = str;
		aVar.d = j;
		aVar.a = str2;
		aVar.c = 49;
		aVar.f = j2;
		aVar.g = str3;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
		StringBuilder sb = new StringBuilder();
		sb.append("Mysned Time2: ");
		sb.append(simpleDateFormat.format(Long.valueOf(j)));
		XLog.d("SendManager" + sb.toString());
		int a2 = a(i);
		int i2 = 0;
		while (true) {
			if (i2 > a2) {
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			i2 += 50;
			long e3 = e(aVar);
			if (e3 != 0) {
				aVar.e = e3;
				c(aVar);
				break;
			}
		}
		StringBuilder sb2 = new StringBuilder();
		sb2.append("Mysned info.msgid =");
		sb2.append(aVar.e);
		sb2.append(" text :");
		sb2.append(str3);
		XLog.d("SendManager" + sb2.toString());
	}
	
	private static void b(String str, int i, String str2, String str3, long j, long j2) {
		C0415a aVar = new C0415a();
		aVar.b = str;
		aVar.d = j;
		aVar.a = str2;
		aVar.c = i;
		aVar.g = str3;
		aVar.f = j2;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
		XLog.d("SendManager Mysned Time2: " + simpleDateFormat.format(Long.valueOf(j)));
		int a2 = a(i);
		int i2 = 0;
		while (true) {
			if (i2 > a2) {
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			i2 += 50;
			long e3 = e(aVar);
			if (e3 != 0) {
				aVar.e = e3;
				c(aVar);
				break;
			}
		}
		XLog.d("SendManager Mysned info.msgid =" + aVar.e + " text :" + str3);
	}
	
	private static void c(String str, int i, String str2, String str3, long j, long j2) {
		C0415a aVar = new C0415a();
		aVar.b = str;
		aVar.d = j;
		aVar.a = str2;
		aVar.c = i;
		aVar.f = j2;
		aVar.g = str3;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
		StringBuilder sb = new StringBuilder();
		sb.append("Mysned Time2: ");
		sb.append(simpleDateFormat.format(Long.valueOf(j)));
		sb.append(" serviceGuid is ");
		sb.append(str);
		XLog.d("SendManager" + sb.toString());
		int a2 = a(i);
		int i2 = 0;
		while (true) {
			if (i2 > a2) {
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			i2 += 50;
			long d2 = d(aVar);
			if (d2 != 0) {
				aVar.e = d2;
				c(aVar);
				break;
			}
		}
		StringBuilder sb2 = new StringBuilder();
		sb2.append("addMessage_gif Mysned info.msgid =");
		sb2.append(aVar.e);
		sb2.append(" text :");
		sb2.append(str3);
		XLog.d("SendManager" + sb2.toString());
	}
	
	private static long d(C0415a aVar) {
		Cursor cursor;
		try {
			if (aVar.f > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT msgId FROM message WHERE isSend = 1 and ( type =1048625 or  type =47 ) and talker ='");
				sb.append(aVar.a);
				sb.append("'  and msgId > ");
				sb.append(aVar.f);
				sb.append(" ORDER BY msgId desc limit 1 ");
				cursor = WcdbHolder.excute(sb.toString(), "EnMicroMsg.db");
			} else {
				StringBuilder sb2 = new StringBuilder();
				sb2.append("SELECT msgId FROM message WHERE isSend = 1 and ( type =1048625 or  type =47 ) and talker ='");
				sb2.append(aVar.a);
				sb2.append("'  and createTime>=");
				sb2.append(aVar.d);
				sb2.append(" ORDER BY msgId desc limit 1 ");
				cursor = WcdbHolder.excute(sb2.toString(), "EnMicroMsg.db");
			}
			if (cursor == null) {
				return 0;
			}
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				return cursor.getLong(cursor.getColumnIndex("msgId"));
			}
			cursor.close();
			return 0;
		} catch (Throwable th) {
			StringBuilder sb3 = new StringBuilder();
			sb3.append("getCurSendId e:");
			sb3.append(th.getMessage());
			XLog.d("SendManager" + sb3.toString());
			th.printStackTrace();
			return 0;
		}
	}
	
	private static void c(C0415a aVar) {
		if (h == null) {
			h = new HashMap<>();
		}
		HashMap<String, C0415a> hashMap = h;
		StringBuilder sb = new StringBuilder();
		sb.append("");
		sb.append(aVar.e);
		hashMap.put(sb.toString(), aVar);
	}
	
	private static void b(String str) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("unReadCount", Integer.valueOf(0));
		contentValues.put("attrflag", Integer.valueOf(0));
		contentValues.put("atCount", Integer.valueOf(0));
		contentValues.put("UnReadInvite", Integer.valueOf(0));
		contentValues.put("unReadMuteCount", Integer.valueOf(0));
		String[] strArr = {str};
		WcdbHolder.excute("rconversation", contentValues, "username= ?", strArr);
		ContentValues contentValues2 = new ContentValues();
		contentValues2.put("atCount", Integer.valueOf(0));
		contentValues2.put("UnReadInvite", Integer.valueOf(0));
		WcdbHolder.excute("rconversation", contentValues2, "username= ?", strArr);
	}
	
	static int a(int i) {
		if (!(i == 1 || i == 3)) {
			if (i == 5) {
				return 500;
			}
			if (i != 34) {
				return (i == 43 || i == 49) ? 1000 : i != 99 ? 0 : 1000;
			}
		}
		return 100;
	}
	
	private static long e(C0415a aVar) {
		Cursor cursor;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("Mysned getCurMsgId select info.time is ");
			sb.append(aVar.d);
			sb.append(" type is ");
			sb.append(aVar.c);
			sb.append("  info.touser is ");
			sb.append(aVar.a);
			sb.append("  info.lastMsgId is ");
			sb.append(aVar.f);
			XLog.d(sb.toString());
			if (aVar.f > 0) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append("SELECT msgId FROM message WHERE isSend = 1 and type =");
				sb2.append(aVar.c);
				sb2.append(" and talker ='");
				sb2.append(aVar.a);
				sb2.append("'  and msgId > ");
				sb2.append(aVar.f);
				sb2.append(" ORDER BY msgId desc limit 1 ");
				cursor = WcdbHolder.excute(sb2.toString(), "EnMicroMsg.db");
			} else {
				StringBuilder sb3 = new StringBuilder();
				sb3.append("SELECT msgId FROM message WHERE isSend = 1 and type =");
				sb3.append(aVar.c);
				sb3.append(" and talker ='");
				sb3.append(aVar.a);
				sb3.append("'  and createTime>=");
				sb3.append(aVar.d);
				sb3.append(" ORDER BY msgId desc limit 1 ");
				cursor = WcdbHolder.excute(sb3.toString(), "EnMicroMsg.db");
			}
			if (cursor == null) {
				XLog.d("getCurMsgId cur is null");
				return 0;
			}
			StringBuilder sb4 = new StringBuilder();
			sb4.append("getCurMsgId size is ");
			sb4.append(cursor.getCount());
			XLog.d(sb4.toString());
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				long j = cursor.getLong(cursor.getColumnIndex("msgId"));
				StringBuilder sb5 = new StringBuilder();
				sb5.append("1getCurMsgId msgId is ");
				sb5.append(j);
				XLog.d(sb5.toString());
				return j;
			}
			cursor.close();
			XLog.d("2getCurMsgId msgId is 000");
			return 0;
		} catch (Throwable th) {
			StringBuilder sb6 = new StringBuilder();
			sb6.append("getCurSendId e:");
			sb6.append(th.getMessage());
			XLog.d("SendManager" + sb6.toString());
			th.printStackTrace();
			return 0;
		}
	}
	
	public static long a(int i, String str) {
		Cursor a2 = WcdbHolder.excute("SELECT msgId FROM message ORDER BY msgId desc limit 1", "EnMicroMsg.db");
		if (a2 == null) {
			XLog.d("getCurMsgId cur is null");
			return 0;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("getCurMsgId size is ");
		sb.append(a2.getCount());
		XLog.d(sb.toString());
		a2.moveToFirst();
		if (a2.isAfterLast()) {
			return 0;
		}
		long j = a2.getLong(a2.getColumnIndex("msgId"));
		StringBuilder sb2 = new StringBuilder();
		sb2.append("1getCurMsgId msgId is ");
		sb2.append(j);
		XLog.d(sb2.toString());
		return j;
	}
	
	
}
