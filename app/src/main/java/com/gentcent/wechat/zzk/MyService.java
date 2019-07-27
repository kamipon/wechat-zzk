package com.gentcent.wechat.zzk;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.gentcent.wechat.zzk.util.XLog;

/**
 * 保持app常驻系统内存
 * @author zuozhi
 * @since 2019-07-27
 */
public class MyService extends Service
{
	private final static String TAG = MyService.class.getSimpleName();
	// 启动notification的id，两次启动应是同一个id
	private final static int NOTIFICATION_ID = android.os.Process.myPid();
	private AssistServiceConnection mServiceConnection;
	
	public MyService() {
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 测试线程，判断Service是否在工作
		new Thread(mRunnable).start();
		// 设置为前台进程，降低oom_adj，提高进程优先级，提高存活机率
		setForeground();
		
		return START_STICKY;
		
	}
	
	// 要注意的是android4.3之后Service.startForeground() 会强制弹出通知栏，解决办法是再
	// 启动一个service和推送共用一个通知栏，然后stop这个service使得通知栏消失。
	private void setForeground() {
		if (Build.VERSION.SDK_INT < 18)
		{
			startForeground(NOTIFICATION_ID, getNotification());
			return;
		}
		
		if (mServiceConnection == null)
		{
			mServiceConnection = new AssistServiceConnection();
		}
		// 绑定另外一条Service，目的是再启动一个通知，然后马上关闭。以达到通知栏没有相关通知
		// 的效果
		bindService(new Intent(this, AssistService.class), mServiceConnection,
				Service.BIND_AUTO_CREATE);
	}
	
	private class AssistServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Service assistService = ((AssistService.LocalBinder)service)
					.getService();
			MyService.this.startForeground(NOTIFICATION_ID, getNotification());
			assistService.startForeground(NOTIFICATION_ID, getNotification());
			assistService.stopForeground(true);
			
			MyService.this.unbindService(mServiceConnection);
			mServiceConnection = null;
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		
		}
	}
	
	private Notification getNotification()
	{
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "")
				.setContentTitle("增长客")
				.setContentText("增长客正在运行")
				.setTicker("增长客正在后台运行...")
				.setPriority(NotificationCompat.PRIORITY_MAX)
				.setWhen(System.currentTimeMillis())
				.setDefaults(NotificationCompat.DEFAULT_ALL)
				.setContentIntent(pendingIntent);
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return notification;
	}
	
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			while (true)
			{
//				XLog.e(TAG+ "" + System.currentTimeMillis());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
	}
}
