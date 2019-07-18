package com.gentcent.wechat.enhancement;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gentcent.wechat.enhancement.util.HookParams;
import com.gentcent.wechat.enhancement.util.MyHelper;
import com.gentcent.wechat.enhancement.util.SearchClasses;
import com.google.gson.Gson;

import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;


public class SettingsActivity extends AppCompatActivity {
	
	private SettingsFragment mSettingsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		if (savedInstanceState == null) {
			mSettingsFragment = new SettingsFragment();
			replaceFragment(R.id.settings_container, mSettingsFragment);
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void replaceFragment(int viewId, android.app.Fragment fragment) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(viewId, fragment).commit();
	}
	
	/**
	 * A placeholder fragment containing a settings view.
	 */
	@SuppressLint("ValidFragment")
	public class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
			addPreferencesFromResource(R.xml.pref_setting);
			
//			Preference reset = findPreference("author");
//			reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//				@Override
//				public boolean onPreferenceClick(Preference pref) {
//					Intent intent = new Intent();
//					intent.setAction("android.intent.action.VIEW");
//					intent.setData(Uri.parse("https://github.com/gentcentCN"));
//					startActivity(intent);
//					return true;
//				}
//			});
			
			Preference repair = findPreference("repair");
			repair.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference pref) {
					Context context = getApplication();
					if (context != null) {
						SharedPreferences.Editor editor = context.getSharedPreferences(HookParams.WECHAT_ENHANCEMENT_CONFIG_NAME, Context.MODE_WORLD_READABLE).edit();
						editor.clear();
						editor.commit();
						Toast toast = Toast.makeText(context, getString(R.string.repair_done), Toast.LENGTH_SHORT);
						toast.show();
					}
					return true;
				}
			});
			
			Preference generate = findPreference("generate");
			generate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference pref) {
					final Context context = getApplication();
					if (context == null) {
						return false;
					}
					final PackageManager packageManager = context.getPackageManager();
					if (packageManager == null) {
						return false;
					}
					
					final ProgressDialog dialog = new ProgressDialog(getActivity());
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
					
					return true;
				}
			});
			
			
			Preference getWcdb = findPreference("get_wcdb");
			getWcdb.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference pref) {
					EventHandler.getWcdb(SettingsActivity.this);
					return true;
				}
			});
			
			
			Preference sendMessage = findPreference("send_message");
			sendMessage.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference pref) {
					EventHandler.sendMessage(1);
					return true;
				}
			});
		}
		
		
		private Application getApplication() {
			try {
				final Class<?> activityThreadClass =
						Class.forName("android.app.ActivityThread");
				final Method method = activityThreadClass.getMethod("currentApplication");
				return (Application) method.invoke(null, (Object[]) null);
			} catch (Exception e) {
			}
			return null;
		}
		
	}
	
}
