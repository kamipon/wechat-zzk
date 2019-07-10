package com.gentcent.wechat.enhancement;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.gentcent.wechat.enhancement.bean.MessageBean;
import com.gentcent.wechat.enhancement.util.MessageStorage;
import com.gentcent.wechat.enhancement.util.XLog;
import com.gentcent.wechat.enhancement.wcdb.DecryptUtils;
import com.gentcent.wechat.enhancement.wcdb.FileUtils;
import com.gentcent.wechat.enhancement.wcdb.Md5Utils;
import com.gentcent.wechat.enhancement.wcdb.ShellUtils;
import com.threekilogram.objectbus.bus.ObjectBus;

import java.io.File;
import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-08
 */
public class EventHandler {
	
	public static void sendMessage(int type){
		Context context = MyApplication.getAppContext();
		String str = "unknown";
		if(type==1){
			str = "文本消息";
		}
		XLog.d("开始调用发送消息 类型:" + str);

//		List<MessageBean> sendMessageQueue = MessageStorage.getSendMessageQueue();
//		MessageBean messageBean = new MessageBean();
//		messageBean.setFriendWxId("wxid_pcxj1zyjpc5n21");
//		messageBean.setContent("7.9号信息");
//		messageBean.setType(1);
//		sendMessageQueue.add(messageBean);
//		MessageStorage.setSendMessageQueque(sendMessageQueue);

		Intent intent = new Intent("WxAction");
		intent.putExtra("act", "send_message");
		intent.putExtra("friendWxId", "wxid_pcxj1zyjpc5n21");
		intent.putExtra("content", "7.10日");
		intent.putExtra("type", 1);
		context.sendBroadcast(intent);
		
		Toast toast = Toast.makeText(context, "开始调用发送消息 类型:" + str, Toast.LENGTH_SHORT);
		toast.show();
		
	}
	
	
	
	public static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";
	private static final String WX_DB_DIR_PATH = WX_ROOT_PATH + "MicroMsg";
	private static final String WX_DB_FILE_NAME = "EnMicroMsg.db";
	private static String mCurrApkPath = Environment.getExternalStorageDirectory().getPath() + "/";
	private static final String COPY_WX_DATA_DB = "wx_data.db";
	private static String copyFilePath = mCurrApkPath + COPY_WX_DATA_DB;
	public static void getWcdb(final Context mContext){
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
