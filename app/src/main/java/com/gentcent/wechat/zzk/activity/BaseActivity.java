package com.gentcent.wechat.zzk.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.R;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
	public static boolean mOpenScan = false;
	private boolean isAllowScreenRotate = false;
	private boolean isSetStatusBar = true;
	private boolean mAllowFullScreen = false;
	protected boolean mConflictTimer;
	private View mContextView;
	
	public abstract int bindLayout();
	
	public abstract View bindView();
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		View bindView = bindView();
		if (bindView == null) {
			this.mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);
		} else {
			this.mContextView = bindView;
		}
		setContentView(this.mContextView);
		ButterKnife.bind(this);
		setDefaultHeaderEvent(this.mContextView);
		if (this.isSetStatusBar) {
			BarUtils.setStatusBarLightMode(this, true);
		}
		if (this.mAllowFullScreen) {
			requestWindowFeature(1);
		}
		if (!this.isAllowScreenRotate) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	/* access modifiers changed from: protected */
	public void onResume() {
		super.onResume();
		if (VERSION.SDK_INT >= 17 && !isDestroyed()) {
		}
	}
	
	public Context getContext() {
		return getBaseContext();
	}
	
	private void setDefaultHeaderEvent(View view) {
		HandHeaderView handHeaderView = (HandHeaderView) view.findViewById(R.id.header_view);
		if (ObjectUtils.isNotEmpty(handHeaderView)) {
			handHeaderView.setLeftClickListener(new OnClickListener() {
				public void onClick(View view) {
					BaseActivity.this.finish();
				}
			});
		}
	}
	
	/* access modifiers changed from: protected */
	public void onDestroy() {
		super.onDestroy();
		resetToast();
	}
	
	private void resetToast() {
		ToastUtils.setMsgColor(-16777217);
		ToastUtils.setBgColor(-16777217);
		ToastUtils.setBgResource(-1);
		ToastUtils.setGravity(81, 0, getResources().getDimensionPixelSize(R.dimen.offset_64));
	}
}
