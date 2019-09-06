package com.gentcent.wechat.zzk.model.friend;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.model.friend.bean.PowderAddBean;
import com.gentcent.wechat.zzk.service.TaskManager;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.google.gson.reflect.TypeToken;
import com.mindorks.scheduler.RxPS;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public class AddFriendDispatchJob {
	static Observable<String> waitAddFriendOverObservable;
	static Observable<String> addFriendDispatchObservable;
	String Taskjson;
	String type;
	int Task_id;
	
	public AddFriendDispatchJob(String Taskjson, String doTaskType, int Task_id) {
		this.Taskjson = Taskjson;
		this.type = doTaskType;
		this.Task_id = Task_id;
	}
	
	public static synchronized Observable<String> getWaitAddFriendOverObservable() {
		Observable<String> Obse;
		synchronized (AddFriendDispatchJob.class) {
			if (waitAddFriendOverObservable == null) {
				waitAddFriendOverObservable = createObservable("waitAddFriendOverObservable");
			}
			Obse = waitAddFriendOverObservable;
		}
		return Obse;
	}
	
	private static Observable<String> createObservable(final String str) {
		return Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> emitter) throws Exception {
				Thread.sleep(3000);
				emitter.onNext(str);
			}
		});
	}
	
	public static synchronized Observable<String> getObservable() {
		Observable<String> Obse;
		synchronized (AddFriendDispatchJob.class) {
			if (addFriendDispatchObservable == null) {
				addFriendDispatchObservable = createObservable("addFriendDispatchObservable");
			}
			Obse = addFriendDispatchObservable;
		}
		return Obse;
	}
	
	private Observer<String> createObserver() {
		return new Observer<String>() {
			@Override
			public void onComplete() {
			}
			
			@Override
			public void onError(Throwable e) {
			}
			
			@Override
			public void onSubscribe(Disposable d) {
			}
			
			@Override
			public void onNext(String s) {
				try {
					synchronized (AddFriendDispatchJob.class) {
						XLog.d("AddFriendDispatchJob synchronized onNext");
						int i = 0;
						if (AddFriendHook.observableSession == null) {
							XLog.d("AddFriendDispatchJob synchronized onNext .sendAddFriendFinishObservable == null");
							AddFriendDispatchJob.this.e();
							do {
								i++;
								Thread.sleep(1000);
								XLog.d("AddFriendDispatchJob addNewFriends sleep 1000 wait sendAddFriendFinishObservable  ");
								if (i > 5) {
									break;
								}
							} while (AddFriendHook.observableSession == null);
							XLog.d("AddFriendDispatchJob sendAddFriendFinishObservable is null" + AddFriendHook.observableSession);
						} else {
							XLog.d("AddFriendDispatchJob synchronized onNext dispatchToAddFriend");
							AddFriendDispatchJob.this.e();
						}
					}
				} catch (Exception e) {
					XLog.d("AddFriendDispatchJob onNext : error " + Log.getStackTraceString(e) + s);
				}
			}
		};
	}
	
	public void run() {
		try {
			XLog.d("AddFriendDispatchJob onRun AddFriendDispatch  ");
			getObservable().subscribeOn(RxPS.high()).subscribe(createObserver());
		} catch (Exception e) {
			XLog.d("AddFriendDispatchJob onRun AddFriendDispatch  error" + Log.getStackTraceString(e));
		}
	}
	
	public void e() {
		XLog.d("dispatchToAddFriend " + this.type);
		switch (this.type) {
			case "AddFriendsPowder":
				XLog.d("dispatchToAddFriend AddFriendsPowder");
				ArrayList arrayList = GsonUtils.GsonToType(this.Taskjson, new TypeToken<List<PowderAddBean>>() {
				}.getType());
				XLog.d("AddFriendDispatchJob dispatchToAddFriend type" + this.type);
				addPowder(arrayList, this.Task_id, this.type);
				break;
			case "AddNewFriends":
				if (AddFriendHook.observableSession != null) {
					XLog.d("waitAddFinish  ");
					addNewFriends(this.Taskjson, 0, -1, this.type);
					break;
				} else {
					TaskManager.getInstance().getJobManager().addJob(new AddChatroomFriendJob(this.Taskjson));
					break;
				}
			case "add_oldwx":
				if (!TextUtils.isEmpty(this.Taskjson)) {
					ArrayList<String> arrayList2 = GsonUtils.GsonToType(this.Taskjson, new TypeToken<List<String>>() {
					}.getType());
					for (String s : arrayList2) {
						AddFriendHelper.getInstance().writeRemarkMap(s, "SetStar");
					}
					PowderAddBean powderAddBean = new PowderAddBean();
					powderAddBean.DeviceIMEI = PhoneUtils.getIMEI();
					powderAddBean.ImportNumber = arrayList2;
					powderAddBean.MinimumTime = 60;
					ArrayList arrayList3 = new ArrayList();
					arrayList3.add(powderAddBean);
					addPowder(arrayList3, this.Task_id, "AddFriendsPowder");
					break;
				} else {
					return;
				}
			default:
				XLog.d("dispatchToAddFriend default");
				break;
		}
	}
	
	private void addPowder(ArrayList<PowderAddBean> list, int taskId, String type) {
		XLog.d("AddFriendDispatchJob addPowder ");
		int times = 0;
		XLog.d("powderAddBean.ImportNumber:" + list.get(0).ImportNumber);
		try {
			for (PowderAddBean powderAddBean : list) {
				XLog.d("AddFriendDispatchJob addPowder start size is " + powderAddBean.ImportNumber.size());
				int minTime = powderAddBean.MinimumTime > 0 ? powderAddBean.MinimumTime : 60;
				XLog.e("powderAddBean.MinimumTime:" + powderAddBean.MinimumTime);
				if (AddFriendHook.observableSession == null) {
					XLog.d("AddFriendDispatchJob addPowder sleep 1000 waitsendAddFriendFinishObservable == null");
					FriendManager.addPowder(MainManager.wxLpparam, powderAddBean.ImportNumber, powderAddBean.SayHello, minTime, taskId);
					if (ObjectUtils.isNotEmpty(powderAddBean.NumberTags)) {
						MyHelper.writeLine("AddFriendTag", GsonUtils.GsonString(powderAddBean.NumberTags));
					}
					do {
						times++;
						Thread.sleep(1000);
						XLog.d("AddFriendDispatchJob addPowder sleep 1000 waitsendAddFriendFinishObservable  times" + times);
						if (times > 3) {
							return;
						}
					} while (AddFriendHook.observableSession == null);
					return;
				}
				addNewFriends(GsonUtils.GsonString(powderAddBean), minTime, taskId, type);
				return;
			}
		} catch (Exception e) {
			XLog.d("AddFriendDispatchJob addPowder error" + e.getMessage());
		}
	}
	
	@SuppressLint("CheckResult")
	private void addNewFriends(final String powderAddBeanStr, final int minTime, final int i2, final String type) {
		Observable observer = Observable.combineLatest(getWaitAddFriendOverObservable(), AddFriendHook.observableSession, new BiFunction<String, String, String>() {
			@Override
			public String apply(String s, String s2) {
				return powderAddBeanStr + type;
			}
		});
		observer.subscribe(new Consumer<String>() {
			@Override
			public void accept(String s) throws Exception {
				XLog.d("AddFriendDispatchJob waitAddFinish accept: 成功：");
				switch (type) {
					case "AddFriendsPowder":
						Thread.sleep(8000);
						PowderAddBean powderAddBean = GsonUtils.GsonToBean(s, PowderAddBean.class);
						FriendManager.addPowder(MainManager.wxLpparam, powderAddBean.ImportNumber, powderAddBean.SayHello, minTime, i2);
						Thread.sleep(4000);
						return;
					case "AddNewFriends":
						Thread.sleep(5000);
						TaskManager.getInstance().getJobManager().addJob(new AddChatroomFriendJob(s));
						return;
					default:
						return;
				}
			}
		}, new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) {
				XLog.e("AddFriendDispatchJob waitAddFinish accept: 失败：" + Log.getStackTraceString(throwable));
			}
		});
	}
}
