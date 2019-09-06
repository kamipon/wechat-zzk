package com.gentcent.wechat.zzk.model.friend;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.bean.MapBean;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class AddFriendHook {
	static String a = "";
	static Activity b;
	public static int c;
	public static Observable<String> observableSession;
	
	public static void hook(final LoadPackageParam loadPackageParam) {
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				final Activity activity = (Activity) methodHookParam.thisObject;
				final Intent intent = activity.getIntent();
				XLog.d("SayHiWithSnsPermissionUI  " + AddFriendHook.c);
				if (AddFriendHook.c == 2) {
					((EditText) XposedHelpers.getObjectField(activity, "qmH")).setText(AddFriendHook.a);
				}
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						XLog.d("SayHiWithSnsPermissionUI Runnable run()" + AddFriendHook.c);
						if (AddFriendHook.c == 2) {
							try {
								XLog.d(" mtA.setText    ");
								String stringExtra = intent.getStringExtra("Contact_User");
								intent.getIntExtra("Contact_Scene", 9);
								XLog.d("addfriend " + stringExtra);
								final Class loadClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI$7");
								activity.runOnUiThread(new Runnable() {
									public void run() {
										OnMenuItemClickListener onMenuItemClickListener = (OnMenuItemClickListener) XposedHelpers.newInstance(loadClass, new Object[]{activity});
										XLog.d("SayHiWithSnsPermissionUI   onResume  flag  over" + AddFriendHook.c);
										onMenuItemClickListener.onMenuItemClick(null);
									}
								});
							} catch (Exception e) {
								XLog.e("SayHiWithSnsPermissionUI  error" + Log.getStackTraceString(e));
							}
						}
					}
				}, 3000, TimeUnit.MILLISECONDS);
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", loadPackageParam.classLoader, "initView", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				Activity activity = (Activity) methodHookParam.thisObject;
				XLog.d(" ContactInfoUI initView() " + AddFriendHook.c);
				boolean booleanExtra = activity.getIntent().getBooleanExtra("shen_shou", false);
				if (booleanExtra || FriendManager.mIsAddFriend) {
					Intent intent = activity.getIntent();
					String phone = intent.getStringExtra("Contact_Search_Mobile");
					XLog.d("AddFriendHook phone is " + phone);
					String userAlias = intent.getStringExtra("Contact_Alias");
					XLog.d("AddFriendHook userAlias is " + userAlias);
					String wxidQp = intent.getStringExtra("Contact_QuanPin");
					XLog.d("AddFriendHook wxidQp is " + wxidQp);
					if (ObjectUtils.isNotEmpty(phone)) {
						String stringExtra4 = intent.getStringExtra("Contact_Nick");
						XLog.d("AddFriendHook nickName is " + stringExtra4);
						String AddFriendTag = MyHelper.readLine("AddFriendTag");
						if (ObjectUtils.isNotEmpty(AddFriendTag)) {
							ArrayList arrayList = GsonUtils.GsonToType(AddFriendTag, new TypeToken<List<MapBean>>() {
							}.getType());
							if (ObjectUtils.isNotEmpty(arrayList)) {
								Iterator it = arrayList.iterator();
								while (it.hasNext()) {
									MapBean mapBean = (MapBean) it.next();
									if (TextUtils.equals(mapBean.Key, phone) && ObjectUtils.isNotEmpty(mapBean.Value)) {
										XLog.d("phone and tags is " + phone + "  " + mapBean.Value + "#_" + phone);
										AddFriendHelper.getInstance().writeRemarkMap(wxidQp, mapBean.Value + "#_" + phone);
									}
								}
							}
						}
						if (AddFriendHelper.getInstance().isContainKey(phone)) {
							AddFriendHelper.getInstance().writeRemarkMap(wxidQp, "SetStar");
						}
						AddFriendHelper a = AddFriendHelper.getInstance();
						a.writeRemarkMap(wxidQp, stringExtra4 + "-" + phone);
					}
					if (AddFriendHelper.getInstance().isContainKey(userAlias)) {
						AddFriendHelper.getInstance().writeRemarkMap(wxidQp, "SetStar");
					}
				}
				if ((AddFriendHook.c == 2 && booleanExtra) || FriendManager.mIsAddFriend) {
					AddFriendHook.c = 2;
					activity.finish();
				}
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				final Activity activity = (Activity) methodHookParam.thisObject;
				final Intent intent = activity.getIntent();
				XLog.d("ContactInfoUI   onResume  " + AddFriendHook.c);
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						XLog.d("ContactInfoUI   onResume  run()" + AddFriendHook.c);
						AddFriendHook.a = "";
						if (intent.getBooleanExtra("shen_shou", false)) {
							try {
								AddFriendHook.b = activity;
								if (AddFriendHook.c == 2) {
									AddFriendHook.c = 0;
									try {
										Thread.sleep(5000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									if (AddFriendHook.observableSession == null) {
										XLog.d("SayHiWithSnsPermissionUI$6 sendAddFriendFinishObservable ==null and create ");
										AddFriendHook.observableSession = Observable.create(new ObservableOnSubscribe<String>() {
											@Override
											public void subscribe(ObservableEmitter<String> emitter) {
												XLog.d(" sendAddFriendFinishObservable send Emitter haha !!!!!!!!! ");
												emitter.onNext("Emitter over");
											}
										});
									}
									XLog.d("ContactInfoUI   AddFriendactivity  over " + AddFriendHook.c);
									activity.finish();
								} else if (AddFriendHook.c == 4) {
									AddFriendHook.a = intent.getStringExtra("explain");
									try {
										activity.runOnUiThread(new Runnable() {
											public void run() {
												AddFriendHook.c = 2;
												Object objectField = XposedHelpers.getObjectField(activity, "qit");
												XLog.d("oHt class is " + objectField.getClass().getName());
												XposedHelpers.callMethod(objectField, "Lj", "contact_profile_add_contact");
												XLog.d("callMethod a success");
											}
										});
									} catch (Exception e) {
										XLog.e("ContactInfoUI  1error is " + Log.getStackTraceString(e));
									}
								}
							} catch (Exception e) {
								XLog.e("ContactInfoUI error is " + Log.getStackTraceString(e));
							}
						}
					}
				}, 3000, TimeUnit.MILLISECONDS);
			}
		});
	}
}
