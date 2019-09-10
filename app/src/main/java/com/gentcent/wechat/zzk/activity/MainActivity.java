package com.gentcent.wechat.zzk.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.MyApplication;
import com.gentcent.wechat.zzk.R;
import com.gentcent.wechat.zzk.WxBroadcast;
import com.gentcent.wechat.zzk.WxReceiver;
import com.gentcent.wechat.zzk.ZzkReceiver;
import com.gentcent.wechat.zzk.activity.keepalive.KeepAlive;
import com.gentcent.wechat.zzk.activity.keepalive.daemon.DaemonService;
import com.gentcent.wechat.zzk.activity.keepalive.pixe.ScreenManager;
import com.gentcent.wechat.zzk.activity.keepalive.pixe.ScreenReceiverUtil;
import com.gentcent.wechat.zzk.activity.keepalive.pixe.ScreenReceiverUtil.C0415a;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.service.ActivityService;
import com.gentcent.wechat.zzk.service.WechatSupport;
import com.gentcent.wechat.zzk.smscall.SmsService;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.SearchClasses;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;
import com.mylhyl.circledialog.CircleDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import dalvik.system.PathClassLoader;

public class MainActivity extends BaseActivity {
	@BindView(R.id.iv_setting)
	ImageView ivSetting;
	@BindView(R.id.lly_scan)
	LinearLayout llyScan;
	@BindView(R.id.lly_device_info)
	LinearLayout llyDeviceInfo;
	@BindView(R.id.lly_sync)
	LinearLayout llySync;
	@BindView(R.id.lly_sync_sns)
	LinearLayout llySyncSns;
	@BindView(R.id.linearLayout2)
	LinearLayout linearLayout2;
	@BindView(R.id.rly_header)
	RelativeLayout rlyHeader;
	@BindView(R.id.tv_xposed)
	TextView tvXposed;
	@BindView(R.id.tv_wechat)
	TextView tvWechat;
	@BindView(R.id.lly_state)
	LinearLayout llyState;
	@BindView(R.id.rly_content)
	LinearLayout rlyContent;
	@BindView(R.id.tv_wechat_current_version)
	TextView tvWechatCurrentVersion;
	@BindView(R.id.tv_wechat_adaptation_version)
	TextView tvWechatAdaptationVersion;
	@BindView(R.id.tv_service_current_version)
	TextView tvServiceCurrentVersion;
	@BindView(R.id.tv_service_adaptation_version)
	TextView tvServiceAdaptationVersion;
	@BindView(R.id.linearLayout3)
	LinearLayout linearLayout3;
	@BindView(R.id.linearLayout)
	LinearLayout linearLayout;
	@BindView(R.id.tv_version_adaptation)
	TextView tvVersionAdaptation;
	@BindView(R.id.tv_result)
	TextView tvResult;
	@BindView(R.id.convenientBanner)
	ConvenientBanner convenientBanner;
	@BindColor(R.color.state_sure)
	int mSure;
	@BindColor(R.color.state_error)
	int mError;
	
	boolean mIsResume = false;
	boolean mHadOpen = false;
	Context context;
	public ScreenManager mScreenManager;
	private C0415a mScreenStateListener = new C0415a() {
		public void onScreenOn() {
			XLog.d("screen onScreenOn");
			if (ObjectUtils.isNotEmpty(MainActivity.this.mScreenManager)) {
				MainActivity.this.mScreenManager.b();
			}
		}
		
		public void onScreenOff() {
			XLog.d("screen onScreenOff");
			if (ObjectUtils.isNotEmpty((Object) MainActivity.this.mScreenManager)) {
				MainActivity.this.mScreenManager.a();
			}
		}
		
		public void onUserPresent() {
			XLog.d("screen onUserPresent");
		}
	};
	
	@Override
	public int bindLayout() {
		return R.layout.activity_main;
	}
	
	@Override
	public View bindView() {
		return null;
	}
	
	LoadingDailog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//开启服务
		Intent intentService = new Intent(this, SmsService.class);
		startService(intentService);
		//创建等待弹窗
		LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this).setMessage("认证中...");
		dialog = loadBuilder.create();
		//设置样式
		getWindow().getDecorView().setSystemUiVisibility(1280);
		getWindow().setStatusBarColor(0);
		
		//keepAlive
		KeepAlive.a(getContext(), "com.tencent.mm", 20000);
		startService(new Intent(getApplicationContext(), DaemonService.class));
		
		new ScreenReceiverUtil(this).a(this.mScreenStateListener);
		this.mScreenManager = ScreenManager.a((Context) this);
		
		//微信当前版本
		String appVersionName = AppUtils.getAppVersionName("com.tencent.mm");
		if (WechatSupport.isSupport(appVersionName)) {
			this.tvWechatCurrentVersion.setTextColor(getResources().getColor(R.color.text_color));
		} else {
			this.tvWechatCurrentVersion.setTextColor(getResources().getColor(R.color.state_error));
		}
		WechatSupport.checkVersion(this);
		this.mHadOpen = false;
		
		context = MainActivity.this;
		
		initBanner();
		initDetection();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.mIsResume = true;
		try {
			checkState();
		} catch (Exception e) {
			XLog.e("HomeAct onResume crash is " + Log.getStackTraceString(e));
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mIsResume = false;
		// 这是前提——你的app至少运行了一个service。这里表示当进程不在前台时，马上开启一个service
//		Intent intent = new Intent(this, MyService.class);
//		startService(intent);
	}
	
	LoadingDailog loadingDailog2;
	
	/**
	 * 环境检测
	 */
	@OnClick(R.id.giv_find)
	public void findState() {
		loadingDailog2 = new LoadingDailog.Builder(this).setMessage("检测中...").setCancelable(false).setCancelOutside(false).create();
		loadingDailog2.show();
		MainActivity.this.onResume();
		ThreadPoolUtils.getInstance().a(new Runnable() {
			@Override
			public void run() {
				loadingDailog2.cancel();
			}
		}, 1000, TimeUnit.MILLISECONDS);
	}
	
	public void checkState() {
		boolean checkZzk = ActivityService.checkZzk(MainActivity.this.getContext());
		WxBroadcast.isWechatOpen();
		boolean isModuleActive = isModuleActive();
//		XLog.d("wxOpen isOpen is " + MyHelper.readLine("isWechatOpen") + "  isModuleActive is " + isModuleActive);
		setViewState(this.tvXposed, isModuleActive && checkZzk);
		this.tvWechat.postDelayed(new Runnable() {
			public void run() {
				boolean isWechatOpen = TextUtils.equals(MyHelper.readLine("isWechatOpen"), "true");
//				XLog.d("wxOpen isWechatOpen " + MyHelper.readLine("isWechatOpen") + "  isOpen is " + isWechatOpen + " mHadOpen is " + MainActivity.this.mHadOpen);
				MainActivity homeAct = MainActivity.this;
				homeAct.setViewState(homeAct.tvWechat, isWechatOpen);
				if (MainActivity.isModuleActive() && !isWechatOpen && !MainActivity.this.mHadOpen) {
//					XLog.d("wxOpen checkState openWechat success is " + MainActivity.this.getContext());
					AppUtils.launchApp(HookParams.WECHAT_PACKAGE_NAME);
					MainActivity.this.mHadOpen = true;
				}
			}
		}, 1000);
	}
	
	/**
	 * 设置状态
	 */
	public void setViewState(TextView textView, boolean z) {
		try {
			if (this.mIsResume) {
				if (z) {
					textView.setText("正常");
					textView.setBackgroundResource(R.drawable.btn_selected_blue);
					textView.setTextColor(this.mSure);
				} else {
					textView.setText("异常");
					textView.setBackgroundResource(R.drawable.btn_selected_red);
					textView.setTextColor(this.mError);
					ThreadPoolUtils.getInstance().a(new Runnable() {
						@Override
						public void run() {
							((MainActivity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									checkState();
								}
							});
						}
					}, 2000, TimeUnit.MILLISECONDS);
				}
			}
		} catch (Exception e) {
			XLog.d("setViewState error is " + Log.getStackTraceString(e));
		}
	}
	
	public static boolean isModuleActive() {
		return false;
	}
	
	/**
	 * 初始化版本信息
	 */
	private void initDetection() {
		String wxVersion = ActivityService.getWxVersion();
		String zzkVersion = ActivityService.getZzkVersion();
		tvWechatCurrentVersion.setText("当前版本:" + wxVersion);
		tvServiceCurrentVersion.setText("当前版本:" + zzkVersion);
	}
	
	/**
	 * 初始化轮播图
	 */
	private void initBanner() {
		ArrayList<Integer> localImages = new ArrayList<Integer>();
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
	
	/**
	 * 扫码回调轮询
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//扫描结果回调
		if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
			//将扫描出的信息显示出来
			dialog.show();
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					MainActivity.this.mHandler.sendEmptyMessage(1);
				}
			}, 1000, 1000);
			mTimes = 0;
			MyHelper.writeLine("bindCompany", "false");
			MyHelper.writeLine("sys-info", "");
			MyHelper.writeLine("phone-id", "");
			MyHelper.writeLine("company", "");
			UploadService.bindDevice(scanResult);
		}
	}
	
	public int mTimes;
	private Timer timer;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			if (message.what == 1) {
				mTimes = mTimes + 1;
				String bindCompany = MyHelper.readLine("bindCompany", "false");
				String sysInfo = MyHelper.readLine("sys-info", "");
				String phoneId = MyHelper.readLine("phone-id", "");
				String company = MyHelper.readLine("company", "");
				
				if (ObjectUtils.equals(bindCompany, "true")) {
					if (!ObjectUtils.equals(sysInfo, "")) {
						if (!ObjectUtils.equals(phoneId, "")) {
							if (!ObjectUtils.equals(company, "")) {
								timer.cancel();
								dialog.cancel();
								confirmLayer("绑定成功！");
							}
						}
					}
				} else if (mTimes >= 30) {
					timer.cancel();
					dialog.cancel();
					confirmLayer("绑定设备超时");
				}
			}
			super.handleMessage(message);
		}
	};
	
	/**
	 * 扫码绑定设备
	 */
	@OnClick(R.id.lly_scan)
	public void bindDivce(View view) {
		Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
		startActivityForResult(intent, Constant.REQ_QR_CODE);
	}
	
	/**
	 * 同步好友
	 */
	@OnClick(R.id.lly_sync)
	public void syncFriend(View view) {
		startActivity(new Intent(getContext(), SyncFriendAct.class));
	}
	
	/**
	 * 设备信息
	 */
	@OnClick(R.id.lly_device_info)
	public void goDeviceInfo(View view) {
		startActivity(new Intent(getBaseContext(), DeviceInfoAct.class));
	}
	
	/**
	 * 群友头像
	 */
	@OnClick(R.id.lly_sync_sns)
	public void syncGroup(View view) {
		confirmLayer("群聊功能正在开发中..");
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
	
	
	public void confirmLayer(String text) {
		new CircleDialog.Builder().setTitle("提示").setCancelable(false).setCanceledOnTouchOutside(false).setText(text).setNeutral("确定", new View.OnClickListener() {
			public void onClick(View view) {
			}
		}).show(getSupportFragmentManager());
	}
	
}
