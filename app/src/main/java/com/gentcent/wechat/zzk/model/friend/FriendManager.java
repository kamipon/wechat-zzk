package com.gentcent.wechat.zzk.model.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.background.Api;
import com.gentcent.wechat.zzk.model.friend.bean.PowderListBean;
import com.gentcent.wechat.zzk.model.friend.bean.WxInfo;
import com.gentcent.wechat.zzk.service.TaskManager;
import com.gentcent.wechat.zzk.service.TaskStateManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.service.WakedResultReceiver;
import okhttp3.Call;
import okhttp3.MediaType;

public class FriendManager {
	public static int taskId = -1;
	public static String SayHello;
	public static boolean mIsAddFriend;
	public static Object mFTSMainUI;
	private static int index;
	
	static int getIndex() {
		int i = index;
		index = i + 1;
		return i;
	}
	
	public static void del(final LoadPackageParam lpparam, String username, int delay) {
		try {
			index = 0;
			XLog.d("FriendManagerdelFriends start username is " + username + "  delay is " + delay);
			final String[] split = username.split("\\|");
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					int i = FriendManager.getIndex();
					if (i >= split.length) {
						timer.cancel();
					} else {
						FriendManager.a(lpparam, split[i]);
					}
				}
			}, 1000, (long) delay);
		} catch (Exception e) {
			XLog.e("FriendManagerdelFriends error is " + Log.getStackTraceString(e));
		}
	}
	
	public static void addPowder(LoadPackageParam lpparam, final ArrayList<String> ImportNumber, String SayHello, final int minTime, final int taskId) {
		try {
			XLog.d("addFriends every delay is " + minTime);
			FriendManager.SayHello = SayHello;
			mIsAddFriend = true;
			MyHelper.writeLine("FriendManager.mIsAddFriend", mIsAddFriend + "");
			if (mFTSMainUI == null) {
				FTSMainUI.openUI();
				ThreadPoolUtils.getInstance().a(new Runnable() {
					public void run() {
						if (FriendManager.mFTSMainUI != null) {
							FriendManager.b(ImportNumber, minTime, taskId);
						} else {
							XLog.e("error addFriends mFTSMainUI is null task be cancel");
						}
					}
				}, 3000, TimeUnit.MILLISECONDS);
				return;
			}
			b(ImportNumber, minTime, taskId);
		} catch (Exception e) {
			XLog.d("addFriends error is " + Log.getStackTraceString(e));
		}
	}
	
	public static void b(ArrayList<String> ImportNumber, int minTime, int taskId) {
		XLog.d("FriendManager addQueue ");
		if (minTime > 0 && minTime < 1900) {
			minTime *= 1000;
		}
		for (int i = 0; i < ImportNumber.size(); i++) {
			String wxid = ImportNumber.get(i);
			JobManager jobManager = TaskManager.getInstance().getJobManager();
			AddFriendJob addFriendJob = new AddFriendJob(ImportNumber, i, wxid, taskId, minTime);
			jobManager.addJob(addFriendJob);
		}
	}
	
	public static void a(LoadPackageParam loadPackageParam, Object obj, String str) {
		try {
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.fts.ui.FTSMainUI"), "c", obj, str);
		} catch (Exception e) {
			XLog.d("searchFriend error is " + Log.getStackTraceString(e));
		}
	}
	
	public static void a(LoadPackageParam loadPackageParam, String str) {
		try {
			XLog.d("delfriend start is " + str);
			XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.report.service.h"), "rdc"), "e", 14553, new Object[]{2, 2, str});
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.aw"), "aeU");
			XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.c"), "acT"), "c", XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.bb.c"), str));
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.aw"), "aeU");
			XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.c"), "acU"), "atd", str);
			XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.aw"), "aeU");
			XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.c"), "ade"), "pS", str);
			XLog.d("delfriend over success is " + str);
		} catch (Throwable th) {
			XLog.d("FriendManagerdelfriend over error is " + Log.getStackTraceString(th));
		}
	}
	
	public static void hook3(LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.report.service.e"), "a", Long.TYPE, String.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					long longValue = (Long) methodHookParam.args[0];
					boolean booleanValue = (Boolean) methodHookParam.args[2];
					boolean booleanValue2 = (Boolean) methodHookParam.args[3];
					if (longValue == 14553 && !booleanValue && !booleanValue2) {
						String str = (String) methodHookParam.args[1];
						if (ObjectUtils.isNotEmpty(str) && str.indexOf(",") > 0) {
							String[] split = str.split(",");
							if (split.length == 3 && TextUtils.equals(split[0], WakedResultReceiver.WAKE_TYPE_KEY) && TextUtils.equals(split[1], WakedResultReceiver.WAKE_TYPE_KEY)) {
								String friendWxId = split[2];
								XLog.d("delFriendHook del friend is " + friendWxId);
								TaskStateManager.a(UserDao.getMyName(), friendWxId);
							}
						}
					}
				}
			});
		} catch (Exception e) {
			XLog.d("delFriendHook error is " + Log.getStackTraceString(e));
		}
	}
	
	public static void a(int sate, String number, WxInfo wxInfo) {
		if (sate == -1) {
			XLog.d("FriendManager findFriendCallback number :" + number + "  fail! -1");
			b(4, number, null);
		} else if (sate == 0) {
			XLog.d("FriendManager findFriendCallback number :" + number + "  WxInfo:" + wxInfo.toString());
			if (MainManager.wxLpparam != null) {
				a(MainManager.wxLpparam, wxInfo.Contact_User, SayHello, wxInfo.type);
				b(1, number, wxInfo);
			}
		} else if (sate == -2) {
			XLog.d("FriendManager findFriendCallback number :" + number + ", sate=" + sate + "  fail -2 !");
			b(5, number, null);
		} else if (sate == -3) {
			XLog.d("FriendManager findFriendCallback number :" + number + ", sate=" + sate + "  fail -3!");
			b(4, number, null);
		}
	}
	
	private static void b(int status, String context, WxInfo wxInfo) {
		XLog.d("postPowder status " + status);
		PowderListBean powderListBean = new PowderListBean();
		powderListBean.TaskId = taskId;
		powderListBean.Imei = PhoneUtils.getIMEI();
		powderListBean.DeviceMemo = DeviceUtils.getModel();
		powderListBean.MyWxid = UserDao.getMyWxid();
		powderListBean.MyNickName = UserDao.getMyName();
		powderListBean.Context = context;
		powderListBean.Status = status;
		if (status == 4 || status == 5) {
			String json = GsonUtils.GsonString(powderListBean);
			XLog.d("1 postPowder json is " + json);
			OkHttpUtils.postString().url(Api.blank).content(json).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
				public void onError(Call call, Exception exc, int i) {
					XLog.d("error postPowder is " + exc.getMessage());
				}
				
				public void onResponse(String str, int i) {
					XLog.d("success postPowder is " + str);
				}
			});
			return;
		}
		if (wxInfo != null && status == 1) {
			powderListBean.FriendId = wxInfo.Contact_User;
			powderListBean.FriendNickName = wxInfo.Contact_Nick;
			powderListBean.HeadImg = wxInfo.bigimgurl;
			XLog.d("OkHttpUtils start ");
			String json2 = GsonUtils.GsonString(powderListBean);
			XLog.d("2 postPowder json is " + json2);
			OkHttpUtils.postString().url(Api.blank).content(json2).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
				public void onError(Call call, Exception exc, int i) {
					XLog.e("error postPowder is " + Log.getStackTraceString(exc));
				}
				
				public void onResponse(String str, int i) {
					XLog.d("success postPowder is " + str);
				}
			});
		}
	}
	
	public static void a(LoadPackageParam loadPackageParam, String Wxid, String str2, int type) {
		try {
			XLog.d("FriendManageraddfriend start type " + type + "  Wxid is " + Wxid);
			LinkedList linkedList = new LinkedList();
			linkedList.add(Wxid);
			LinkedList linkedList2 = new LinkedList();
			linkedList2.add(type);
			HashMap hashMap = new HashMap();
			hashMap.put(Wxid, 0);
			Object newInstance = XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.pluginsdk.model.m"), new Class[]{List.class, List.class, String.class, String.class, Map.class, String.class}, linkedList, linkedList2, str2, "", hashMap, null);
			XLog.d("FriendManager addfriend info ");
			if (newInstance != null) {
				XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.kernel.g"), "Vs"), "a", newInstance, 0);
				XLog.d("FriendManager addfriend info over");
				return;
			}
			XLog.d("FriendManager addfriend info =null");
		} catch (Throwable th) {
			XLog.d("FriendManager  addfriend e:" + Log.getStackTraceString(th));
		}
	}
	
	public static void hook(final LoadPackageParam loadPackageParam) {
		try {
			hook2(loadPackageParam);
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.fts.ui.FTSMainUI$5", loadPackageParam.classLoader, "onSceneEnd", Integer.TYPE, Integer.TYPE, String.class, loadPackageParam.classLoader.loadClass("com.tencent.mm.ak.m"), new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					XLog.d("FTSMainUI$5 onSceneEnd mIsAddFriend is " + FriendManager.mIsAddFriend);
					if (FriendManager.mIsAddFriend) {
						String number = (String) XposedHelpers.getObjectField(methodHookParam.thisObject, "Cu");
						XLog.d("FriendManager hookTargetClass number:" + number);
						int intValue = (Integer) methodHookParam.args[0];
						int intValue2 = (Integer) methodHookParam.args[1];
						Object obj = methodHookParam.args[3];
						if (intValue == 4 && intValue2 == -4) {
							FriendManager.a(-1, number, null);
						} else if (intValue == 0 && intValue2 == 0) {
							WxInfo a2 = WxInfo.a(loadPackageParam, XposedHelpers.callMethod(obj, "bWT"));
							XLog.d("FriendManager hookTargetClass wxInfo:");
							FriendManager.a(0, number, a2);
						} else if (intValue == 4 && intValue2 == -24) {
							FriendManager.a(-2, number, null);
						} else {
							FriendManager.a(-3, number, null);
						}
					}
				}
			});
		} catch (Exception e) {
			XLog.d("FriendManager hookTargetClass e:" + Log.getStackTraceString(e));
		} catch (Throwable th) {
			XLog.d("FriendManager hookTargetClass th:" + Log.getStackTraceString(th));
		}
	}
	
	private static void hook2(LoadPackageParam loadPackageParam) throws Throwable {
		XposedHelpers.findAndHookMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.fts.ui.FTSMainUI"), "onCreate", Bundle.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				XLog.d("FriendManager.mIsAddFriend is " + FriendManager.mIsAddFriend + "  FriendManager.mFTSMainUI is " + FriendManager.mFTSMainUI);
				if (FriendManager.mIsAddFriend && FriendManager.mFTSMainUI == null) {
					Activity activity = (Activity) methodHookParam.thisObject;
					FriendManager.mFTSMainUI = activity;
					XLog.d("FTSMainUI is onCreate finish");
					activity.finish();
				}
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.ui.widget.b.c", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				if (FriendManager.mIsAddFriend) {
					String msg = ((TextView) XposedHelpers.getObjectField(methodHookParam.thisObject, "zKf")).getText().toString();
					XLog.d("2textView msg is " + msg);
					if (ObjectUtils.isNotEmpty(msg) && msg.startsWith("操作过于频繁")) {
						final Button button = (Button) XposedHelpers.getObjectField(methodHookParam.thisObject, "vcM");
						button.postDelayed(new Runnable() {
							public void run() {
								button.performClick();
								XLog.d("2textView msg is performClick");
							}
						}, 200);
					}
				}
			}
		});
		Class loadClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.protocal.protobuf.bxf");
		XposedHelpers.findAndHookMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.model.j"), "a", Intent.class, loadClass, Integer.TYPE, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				if (FriendManager.mIsAddFriend) {
					((Intent) methodHookParam.args[0]).putExtra("shen_shou", true);
					AddFriendHook.c = 4;
				}
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				XLog.d("FriendManager.mIsAddFriend is " + FriendManager.mIsAddFriend + "  FriendManager.mSayHello is " + FriendManager.SayHello);
				if (FriendManager.mIsAddFriend) {
					final EditText editText = (EditText) XposedHelpers.getObjectField(methodHookParam.thisObject, "qmH");
					XLog.d("SayHiWithSnsPermissionUI editText is " + editText);
					if (editText != null && !TextUtils.isEmpty(FriendManager.SayHello)) {
						editText.postDelayed(new Runnable() {
							public void run() {
								editText.setText(FriendManager.SayHello);
							}
						}, 200);
					}
					final Activity activity = (Activity) methodHookParam.thisObject;
					if (editText != null && !TextUtils.isEmpty(FriendManager.SayHello)) {
						editText.postDelayed(new Runnable() {
							public void run() {
								if (activity != null && !activity.isDestroyed()) {
									activity.finish();
								}
							}
						}, 12000);
					}
				}
			}
		});
	}
}
