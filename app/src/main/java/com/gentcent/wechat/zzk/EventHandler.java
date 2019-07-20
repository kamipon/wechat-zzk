package com.gentcent.wechat.zzk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.blankj.utilcode.util.AppUtils;
import com.gentcent.wechat.zzk.bean.MessageBean;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.SendManager;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.DecryptUtils;
import com.gentcent.wechat.zzk.wcdb.FileUtils;
import com.gentcent.wechat.zzk.wcdb.Md5Utils;
import com.gentcent.wechat.zzk.wcdb.ShellUtils;
import com.threekilogram.objectbus.bus.ObjectBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class EventHandler {
	
	/**
	 * 发送消息
	 */
	public static void sendMessage(MessageBean messageBean) {
		Context context = MyApplication.getAppContext();
		XLog.d("添加至消息队列 类型:" + messageBean.getType());
		SendManager.addToQueque(messageBean);
		//添加至消息队列
		sendBroad(context);
	}
	
	/**
	 * 发送广播，传递消息队列
	 */
	private static void sendBroad(final Context context) {
		int quequeSize = SendManager.getQuequeSize();
		if(quequeSize>0 && !SendManager.isLock()){
			SendManager.lock();
			List<MessageBean> list = new ArrayList<>(SendManager.getQueque());
			SendManager.clearQueque();
			
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "send_message");
			intent.putExtra("msgQueue", (Serializable)list);
			context.sendBroadcast(intent);
			XLog.d("发送消息 | 个数："+quequeSize);
			
			//发送完成后再次执行，直到消息队列为空为止
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					SendManager.unLock();
					sendBroad(context);
				}
			};
			new Timer().schedule(task, HookParams.SEND_TIME_INTERVAL * (quequeSize + 1));
		}
	}
	
	/**
	 * 添加好友
	 */
	public static void addFriend(String id) {
		try {
			Context context = MyApplication.getAppContext();
			Intent intent = new Intent("WxAction");
			intent.putExtra("act", "add_friend");
			intent.putExtra("addFriendName", id);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 读取微信数据库
	 */
	@SuppressLint("SdCardPath")
	private static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";
	private static final String WX_DB_DIR_PATH = WX_ROOT_PATH + "MicroMsg";
	private static final String WX_DB_FILE_NAME = "EnMicroMsg.db";
	private static String mCurrApkPath = Environment.getExternalStorageDirectory().getPath() + "/";
	private static final String COPY_WX_DATA_DB = "wx_data.db";
	private static String copyFilePath = mCurrApkPath + COPY_WX_DATA_DB;
	
	public static void getWcdb() {
		final Context mContext = MyApplication.getAppContext();
		try {
			ObjectBus.newList().toPool(new Runnable() {
				@Override
				public void run() {
					//获取微信目录读写权限
					ShellUtils.execRootCmd("chmod 777 -R " + WX_ROOT_PATH);
					//获取root权限
					DecryptUtils.execRootCmd("chmod 777 -R " + copyFilePath);
					String password = DecryptUtils.initDbPassword(mContext);
					String uid = DecryptUtils.initCurrWxUin();
					try {
						String path = WX_DB_DIR_PATH + "/" + Md5Utils.md5Encode("mm" + uid) + "/" + WX_DB_FILE_NAME;
						//微信原始数据库的地址
						XLog.e("path:  " + path);
						File wxDataDir = new File(path);
						//将微信数据库拷贝出来，因为直接连接微信的db，会导致微信崩溃
						FileUtils.copyFile(wxDataDir.getAbsolutePath(), copyFilePath);
						//将微信数据库导出到sd卡操作sd卡上数据库
						FileUtils.openWxDb(new File(copyFilePath), mContext, password);
						XLog.e("path:  " + copyFilePath);
						XLog.e("path:  " + password);
					} catch (Exception e) {
						XLog.e("path:  " + e.getMessage());
						e.printStackTrace();
					}
				}
			}).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
