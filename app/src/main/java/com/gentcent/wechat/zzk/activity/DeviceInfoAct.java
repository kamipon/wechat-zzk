package com.gentcent.wechat.zzk.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.R;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceInfoAct extends BaseActivity {
	
	private boolean mBindCompany = true;
	@BindView(R.id.header_view)
	HandHeaderView header_view;
	@BindView(R.id.iv_home)
	ImageView iv_home;
	@BindView(R.id.tv_name)
	TextView tv_name;
	@BindView(R.id.tv_mode)
	TextView tv_mode;
	@BindView(R.id.iv_auth)
	ImageView iv_auth;
	@BindView(R.id.tv_line)
	TextView tv_line;
	@BindView(R.id.tv_memo)
	TextView tv_memo;
	@BindView(R.id.tv_device_mode)
	TextView tv_device_mode;
	@BindView(R.id.tv_imei)
	TextView tv_imei;
	@BindView(R.id.tv_device_version)
	TextView tv_device_version;
	@BindView(R.id.tv_cpu)
	TextView tv_cpu;
	@BindView(R.id.tv_yc)
	TextView tv_yc;
	@BindView(R.id.tv_nc)
	TextView tv_nc;
	@BindView(R.id.tv_version_name)
	TextView tv_version_name;
	@BindView(R.id.tv_device_code)
	TextView tv_device_code;
	
	public int bindLayout() {
		return R.layout.activity_device_info;
	}
	
	public View bindView() {
		return null;
	}
	
	@OnClick({R.id.tv_imei})
	public void getImei() {
		copy(PhoneUtils.getIMEI(), getContext());
	}
	
	@OnClick({R.id.tv_memo})
	public void getPhoneId() {
		copy(MyHelper.readLine("phone-id"), getContext());
	}
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		XLog.d("DeviceUtils.getModel() is " + DeviceUtils.getModel());
		this.tv_device_mode.setText(DeviceUtils.getModel());
		this.tv_imei.setText(PhoneUtils.getIMEI());
		this.tv_device_version.setText(Build.DISPLAY);
		TextView textView = this.tv_cpu;
		String sb2 = Runtime.getRuntime().availableProcessors() + " 核";
		textView.setText(sb2);
		this.tv_yc.setText(getTotalRam());
		this.tv_nc.setText(getRomInfoString());
		this.tv_version_name.setText(DeviceUtils.getSDKVersionName());
		this.tv_device_code.setText(AppUtils.getAppVersionName());
		String company = MyHelper.readLine("company", "");
		String deviceMemo = MyHelper.readLine("deviceMemo", "");
		boolean bindCompany = Boolean.valueOf(MyHelper.readLine("bindCompany", "false"));
		this.mBindCompany = bindCompany;
		if (bindCompany) {
			this.iv_auth.setImageResource(R.mipmap.ic_device_auth);
			this.tv_name.setVisibility(View.VISIBLE);
			this.tv_name.setText(company);
			String phoneId = "设备编号：" + MyHelper.readLine("phone-id", "未绑定设备");
			this.tv_memo.setVisibility(View.VISIBLE);
			this.tv_memo.setText(phoneId);
			if (!TextUtils.isEmpty(deviceMemo)) {
				this.tv_mode.setVisibility(View.VISIBLE);
				this.tv_mode.setText(deviceMemo);
			}
		}
	}
	
	public void copy(String str, Context context) {
		((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setText(str.trim());
		String sb = "已复制：" + str.trim();
		ToastUtils.showShort(sb);
	}
	
	private String getRomInfoString() {
		StatFs statFs = new StatFs(getPhoneStorageDirectory().getPath());
		double blockCount = (double) (((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize()));
		double pow = Math.pow(1024.0d, 3.0d);
		Double.isNaN(blockCount);
		double d = blockCount / pow;
		if (d < 1.0d) {
			StringBuilder sb = new StringBuilder();
			sb.append((int) (d * 1024.0d));
			sb.append(" MB");
			return sb.toString();
		}
		int i = 0;
		while (d > 0.0d) {
			int i2 = i + 1;
			if (d < Math.pow(2.0d, (double) i2) && d >= Math.pow(2.0d, (double) i)) {
				break;
			}
			i = i2;
		}
		StringBuilder sb2 = new StringBuilder();
		sb2.append((int) Math.pow(2.0d, (double) (i + 1)));
		sb2.append(" GB");
		return sb2.toString();
	}
	
	private File getPhoneStorageDirectory() {
		if (!Environment.isExternalStorageRemovable()) {
			return Environment.getExternalStorageDirectory();
		}
		File file = null;
		try {
			Method method = Class.forName("android.os.Environment").getMethod("getSecondaryStorageDirectory", (Class<?>[]) null);
			file = (File) method.invoke(null, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static String getTotalRam() {
		String str = null;
		int i = 0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
			str = bufferedReader.readLine().split("\\s+")[1];
			bufferedReader.close();
		} catch (Exception e) {
			try {
				e.printStackTrace();
			} catch (Exception unused) {
				return "2 GB";
			}
		}
		if (str != null) {
			i = (int) Math.ceil(Float.valueOf(Float.valueOf(str) / 1048576.0f).doubleValue());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(i);
		sb.append(" GB");
		return sb.toString();
	}
}
