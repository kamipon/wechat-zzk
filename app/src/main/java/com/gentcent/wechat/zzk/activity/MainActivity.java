package com.gentcent.wechat.zzk.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.R;
import com.gentcent.wechat.zzk.WxBroadcast;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.service.MyService;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.SearchClasses;
import com.gentcent.wechat.zzk.util.XLog;
import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {
	private ConvenientBanner convenientBanner;
	private ArrayList<Integer> localImages = new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		convenientBanner = findViewById(R.id.convenientBanner);
		//获取本地的图片，循环播放
		for (int position = 0; position < 2; position++) {
			localImages.add(R.drawable.ic_banner_dkd_one);
		}
		convenientBanner.setPages(new CBViewHolderCreator() {
			@Override
			public Object createHolder() {
				return new LocalImageHolderView();
			}
		}, localImages);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// 这是前提——你的app至少运行了一个service。这里表示当进程不在前台时，马上开启一个service
		Intent intent = new Intent(this, MyService.class);
		startService(intent);
	}
	
	/**
	 * 扫码绑定设备
	 *
	 * @param view 代表被点击的视图
	 */
	public void bindDivce(View view) {
		Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
		startActivityForResult(intent, Constant.REQ_QR_CODE);
	}
	
	/**
	 * 同步好友
	 *
	 * @param view 代表被点击的视图
	 */
	public void syncFriend(View view) {
		ToastUtils.showShort("开始同步好友信息");
		WxBroadcast.sendAct("sync_info");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//扫描结果回调
		if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
			//将扫描出的信息显示出来
			XLog.d(scanResult);
			UploadService.bindDevice(scanResult);
		}
	}
	
	/**
	 * 初始化
	 *
	 * @param view 代表被点击的视图
	 */
	public void init(View view) {
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
	
	/**
	 * 获取所有已安装App信息
	 *
	 * @param view 代表被点击的视图
	 */
	public void getAppList(View view) {
		List<AppUtils.AppInfo> appsInfo = AppUtils.getAppsInfo();
		Toast.makeText(getApplication(), "已输出日志", Toast.LENGTH_SHORT).show();
		for (AppUtils.AppInfo appInfo : appsInfo) {
			String name = appInfo.getName();
			String packageName = appInfo.getPackageName();
			String versionName = appInfo.getVersionName();
			XLog.d("name: " + name + "  packageName: " + packageName + "  versionName: " + versionName);
		}
	}
	
	/**
	 * 安装app
	 *
	 * @param view 代表被点击的视图
	 */
	@SuppressLint("SdCardPath")
	public void installXposedCkeck(View view) {
		AppUtils.installApp(new File("/sdcard/XposedCheck.apk"));
	}
	
	/**
	 * 卸载app
	 *
	 * @param view 代表被点击的视图
	 */
	public void uninstallXposedCkeck(View view) {
		AppUtils.uninstallApp("com.ssrj.xposedcheck");
	}
	
	
}
