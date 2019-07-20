package com.gentcent.wechat.zzk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gentcent.wechat.zzk.bean.MessageBean;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.SearchClasses;
import com.google.gson.Gson;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	/**
	 * 发消息
	 * @param view 代表被点击的视图
	 */
	public void sendMessage(View view){
		EditText editText = findViewById(R.id.send_message_content);
		String content = editText.getText().toString();
		EditText editText2 = findViewById(R.id.send_message_target_id);
		String sendId = editText2.getText().toString();
		
		MessageBean messageBean = new MessageBean();
		messageBean.setFriendWxId(sendId);
		messageBean.setContent(content);
		messageBean.setType(1);
		
		EventHandler.sendMessage(messageBean);
	}
	
	
	/**
	 * 添加好友
	 * @param view 代表被点击的视图
	 */
	public void addFriend(View view){
		EditText editText = findViewById(R.id.add_friend);
		String id = editText.getText().toString();
		EditText editText2 = findViewById(R.id.hello_text);
		String helloText = editText2.getText().toString();
		//添加好友打招呼语句
		MyHelper.writeLine("addFriendHelloText", helloText);
		
		EventHandler.addFriend(id);
	}
	
	/**
	 * 读取微信数据库
	 * @param view 代表被点击的视图
	 */
	public void getWcdb(View view){
		EventHandler.getWcdb();
	}
	
	/**
	 * 初始化
	 * @param view 代表被点击的视图
	 */
	public void init(View view){
		final Context context = getApplication();
		if (context == null) {
			return;
		}
		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			return;
		}
		
		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		dialog.setCancelable(false);
		dialog.setMessage(getResources().getString(R.string.generating));
		dialog.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean success = false;
				try {
					PackageInfo packageInfo = packageManager.getPackageInfo(HookParams.WECHAT_PACKAGE_NAME, 0);
					String wechatApk = packageInfo.applicationInfo.sourceDir;
					PathClassLoader wxClassLoader = new PathClassLoader(wechatApk, ClassLoader.getSystemClassLoader());
					SearchClasses.generateConfig(wechatApk, wxClassLoader, packageInfo.versionName);
					
					String config = new Gson().toJson(HookParams.getInstance());
					MyHelper.writeLine("params", config);
					success = true;
					
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				final String msg = getResources().getString(success ? R.string.generate_success : R.string.generate_failed);
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}, "generate-config").start();
		
	}
	
	
}
