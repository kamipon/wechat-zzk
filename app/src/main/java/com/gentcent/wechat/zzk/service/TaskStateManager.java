package com.gentcent.wechat.zzk.service;

import android.text.TextUtils;

import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.background.Api;
import com.gentcent.wechat.zzk.bean.DelFriendBean;
import com.gentcent.wechat.zzk.bean.JPTimeBean;
import com.gentcent.wechat.zzk.bean.TaskStateBean;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.MediaType;

public class TaskStateManager {
	
	public static void a(int i) {
		if (i != -1) {
			a(i, 2);
		}
	}
	
	public static void b(int i) {
		if (i != -1) {
			a(i, 3);
		}
	}
	
	public static void a(String str) {
		a(Api.blank, GsonUtils.GsonString(new JPTimeBean(PhoneUtils.getIMEI(), str, null)), "taskJpush");
	}
	
	private static void a(int i, int i2) {
		a(Api.blank, GsonUtils.GsonString(new TaskStateBean(i, PhoneUtils.getIMEI(), i2)), "doTaskState");
	}
	
	public static void a(String str, String str2) {
		if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
			a(Api.blank, GsonUtils.GsonString(new DelFriendBean(PhoneUtils.getIMEI(), str, str2)), "delFiend");
		}
	}
	
	private static void a(final String str, final String json, final String error) {
		Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(final ObservableEmitter<String> emitter) {
				try {
					OkHttpUtils.postString().url(str).content(json).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
						public void onError(Call call, Exception exc, int i) {
							XLog.d("postGson error " + error + " is " + exc.getMessage() + "  json " + json);
							emitter.onError(exc);
						}
						
						public void onResponse(String str, int i) {
							String sb = "postGson success " + error + " is " + str + "  json " + json;
							XLog.d(sb);
							emitter.onNext(str);
							emitter.onComplete();
						}
					});
				} catch (Exception e) {
					XLog.d("postGson exception " + error + " is " + e.getMessage() + "  json " + json);
					emitter.onError(e);
				}
			}
		}).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
			
			public int mRetryCount;
			
			int getnum() {
				int i = mRetryCount + 1;
				mRetryCount = i;
				return i;
			}
			
			@Override
			public ObservableSource<?> apply(Observable<Object> objectObservable) {
				return objectObservable.just(new Function<Throwable, ObservableSource<?>>() {
					@Override
					public ObservableSource<?> apply(Throwable throwable) {
						String message = throwable.getMessage();
						if (getnum() > 2) {
							return Observable.error(throwable);
						}
						XLog.d("postGson error " + message + ", mRetryCount = " + mRetryCount);
						return Observable.interval(2, TimeUnit.SECONDS);
					}
				});
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
	}
}
