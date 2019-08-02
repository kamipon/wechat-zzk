package com.gentcent.wechat.zzk.model.sns;

import android.text.TextUtils;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.model.sns.bean.SnsContentItemBean;
import com.gentcent.wechat.zzk.model.sns.bean.SnsSelfListBean;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.plugin.IPlugin;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class SnsHook{
	private static List<SnsContentItemBean> mList = new ArrayList<>();
	private static long mSnsStartTime = (System.currentTimeMillis() / 1000);
	
	public static void hook(final LoadPackageParam lpparam) {
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.model.aw", lpparam.classLoader, "t", XposedHelpers.findClass("com.tencent.mm.plugin.sns.storage.n", lpparam.classLoader), new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				SnsHook.mSnsStartTime = System.currentTimeMillis() / 1000;
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.storage.o", lpparam.classLoader, "jW", Long.TYPE, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				Object result = methodHookParam.getResult();
				if (result != null) {
					long intValue = (long) (Integer) XposedHelpers.getObjectField(XposedHelpers.callMethod(result, "cmi"), "ozl");
					long j = intValue - SnsHook.mSnsStartTime;
					if (j >= 0) {
						final SnsContentItemBean snsContentItemBean = SnsHandler.getSnsContentItemBean(lpparam, result, "");
						if (snsContentItemBean == null) {
							XLog.d("hookNewSns bean is null");
						} else if (TextUtils.equals(snsContentItemBean.getSnsWxid(), UserDao.getMyWxid())) {
							XLog.d("hookNewSns intervalTime is " + j);
							if (snsContentItemBean.getType() == 0 && TextUtils.isEmpty(snsContentItemBean.getContent())) {
								XLog.d("text sns Content isEmpty");
							} else if (snsContentItemBean.getType() != 1 || !ObjectUtils.isEmpty((Collection) snsContentItemBean.getImages())) {
								SnsHook.mSnsStartTime = intValue + 1;
								if (SnsHook.mList == null) {
									SnsHook.mList = new ArrayList<>();
								}
								if (SnsHook.mList.size() == 0) {
									ThreadPoolUtils.getInstance().a(new Runnable() {
										public void run() {
											SnsHook.mList.add(snsContentItemBean);
											SnsSelfListBean snsSelfListBean = new SnsSelfListBean(PhoneUtils.getIMEI(), UserDao.getMyWxid(), SnsHook.mList);
											XLog.d("hookNewSns is start");
											XLog.d("snsSelfListBean:" + snsSelfListBean.toString());
											selfCommend(SnsHook.mList.get(0).getSnsID());
//											SnsHook.postSingleUpdateSns(new GsonBuilder().disableHtmlEscaping().create().toJson((Object) snsSelfListBean), SnsHook.mList);
//											if (snsContentItemBean.getType() == 1 && ObjectUtils.isNotEmpty((Collection) snsContentItemBean.getImages()) && ObjectUtils.isNotEmpty((CharSequence) snsContentItemBean.getSnsID())) {
//												CopySnsStrategy.postSnsStrategy(snsContentItemBean.getSnsID(), snsContentItemBean.getImages(), snsContentItemBean.getContent());
//											}
											SnsHook.mList.clear();
										}
									}, 1000, TimeUnit.MILLISECONDS);
								}
							} else {
								XLog.d("image sns Images isEmpty");
							}
						}
					}
				}
			}
		});
		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.model.am$a", lpparam.classLoader, "WL", String.class, new XC_MethodHook() {
			public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
				super.afterHookedMethod(methodHookParam);
				String str = (String) methodHookParam.args[0];
				XLog.d("hookCancelPrise is " + str);
				if (!TextUtils.equals(str, "sns_table_0")) {
					XLog.d("!TextUtils.equals(str, \"sns_table_0\")");
//					b(str, true);
				}
			}
		});
		
	}
	
	public static void selfCommend(String snsId) {
		XLog.d("SelfCommend is " + SnsHandler.SelfCommend + " snsId is " + snsId);
		if (!TextUtils.isEmpty(SnsHandler.SelfCommend) && !TextUtils.isEmpty(snsId)) {
			XLog.d("开始首次评论：" + SnsHandler.SelfCommend);
			SnsHandler.makeComment_Append_By_SnsID(MainManager.wxLpparam, snsId, SnsHandler.SelfCommend);
			SnsHandler.SelfCommend = "";
		}
	}

//	public static boolean b(String str, boolean z) {
//		if (!str.startsWith("sns_table_")) {
//			StringBuilder sb = new StringBuilder();
//			sb.append("sns_table_");
//			sb.append(str);
//			str = sb.toString();
//		}
//		SnsContentItemBean snsContentItemBean = getSnsContentItemBean(MainManager.wxLpparam, str);
//		snsContentItemBean.DelayLike = z;
//		long j = 0;
//		if (snsContentItemBean.Timestamp == 0) {
//			return true;
//		}
//		if (ObjectUtils.isNotEmpty((Collection) snsContentItemBean.Likelist)) {
//			j = ((SnsLikeBean) snsContentItemBean.Likelist.get(snsContentItemBean.Likelist.size() - 1)).Timestamp;
//		}
//		if (ObjectUtils.isNotEmpty((Collection) snsContentItemBean.Commentlist)) {
//			SnsCommentBean snsCommentBean = (SnsCommentBean) snsContentItemBean.Commentlist.get(snsContentItemBean.Commentlist.size() - 1);
//			if (snsCommentBean.Timestamp > j) {
//				j = snsCommentBean.Timestamp;
//			}
//		}
//		boolean z2 = (System.currentTimeMillis() / 1000) - j > 5;
//		if (z) {
//			z2 = false;
//		}
//		StringBuilder sb2 = new StringBuilder();
//		sb2.append("isTimeReturn is ");
//		sb2.append(z2);
//		XLog.d(sb2.toString());
//		if (z2 || !TextUtils.equals(snsContentItemBean.SnsWxid, bh.f())) {
//			return true;
//		}
//		if (a == null) {
//			a = new ArrayList();
//		}
//		StringBuilder sb3 = new StringBuilder();
//		sb3.append("mList size is ");
//		sb3.append(a.size());
//		XLog.d(sb3.toString());
//		if (a.size() == 0) {
//			a.add(snsContentItemBean);
//			SnsSelfListBean snsSelfListBean = new SnsSelfListBean(SSPhoneUtils.a(), bh.f(), a);
//			StringBuilder sb4 = new StringBuilder();
//			sb4.append("start post mList size is ");
//			sb4.append(a.size());
//			XLog.d(sb4.toString());
//			SnsInfoTools.postSingleUpdateSns(new GsonBuilder().disableHtmlEscaping().create().toJson((Object) snsSelfListBean), a);
//		}
//		return false;
//	}
//
//
//	public static void postSingleUpdateSns(final String str, final List<SnsContentItemBean> list) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("postSingleUpdateSns json is ");
//		sb.append(str);
//		XLog.d(sb.toString());
//		((PostStringBuilder) OkHttpUtils.postString().url(Api.z)).content(str).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
//			public void onError(Call call, Exception exc, int i) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("error postSingleUpdateSns is ");
//				sb.append(QNUploadUtil.a(exc));
//				XLog.d("Xposed", sb.toString());
//				if (ObjectUtils.isNotEmpty((Collection) list)) {
//					SnsInfoTools.selfCommend(((SnsContentItemBean) list.get(0)).SnsID);
//					list.clear();
//				}
//				new Timer().schedule(new TimerTask() {
//					public void run() {
//						SnsInfoTools.msgPostRetry(0, str);
//					}
//				}, 5000);
//			}
//
//			public void onResponse(String str, int i) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("success postSingleUpdateSns is ");
//				sb.append(str);
//				XLog.d("Xposed", sb.toString());
//				if (ObjectUtils.isNotEmpty((Collection) list)) {
//					SnsInfoTools.selfCommend(((SnsContentItemBean) list.get(0)).SnsID);
//					list.clear();
//				}
//			}
//		});
//	}
//
//	public static void msgPostRetry(final int i, final String str) {
//		if (i < 2) {
//			((PostStringBuilder) OkHttpUtils.postString().url(Api.z)).content(str).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
//				public void onError(Call call, Exception exc, int i) {
//					StringBuilder sb = new StringBuilder();
//					sb.append("error postSingleUpdateSns is ");
//					sb.append(QNUploadUtil.a(exc));
//					XLog.d("Xposed", sb.toString());
//					new Timer().schedule(new TimerTask() {
//						public void run() {
//							SnsInfoTools.msgPostRetry(i + 1, str);
//						}
//					}, 5000);
//				}
//
//				public void onResponse(String str, int i) {
//					StringBuilder sb = new StringBuilder();
//					sb.append("success postSingleUpdateSns is ");
//					sb.append(str);
//					XLog.d("Xposed", sb.toString());
//				}
//			});
//		}
//	}
//
//	public static SnsContentItemBean getSnsContentItemBean(LoadPackageParam lpparam, String str) {
//		return SnsManager.getSnsContentItemBean(lpparam, SnsManager.GetWSnsContentBean_By_snsID(lpparam, str), "");
//	}
//
//	private static void postSelfSns(String str) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("postSelfSns json is ");
//		sb.append(str);
//		XLog.d(sb.toString());
//		((PostStringBuilder) OkHttpUtils.postString().url(Api.y)).content(str).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
//			public void onError(Call call, Exception exc, int i) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("error postSelfSns is ");
//				sb.append(exc.getMessage());
//				LogUtils.eTag("Xposed", sb.toString());
//				MyHelper.b("sync_sns_over", "true");
//			}
//
//			public void onResponse(String str, int i) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("success postSelfSns is ");
//				sb.append(str);
//				LogUtils.eTag("Xposed", sb.toString());
//				try {
//					BaseBean baseBean = (BaseBean) GsonUtils.a(str, BaseBean.class);
//					if (baseBean.Success) {
//						boolean isNotEmpty = ObjectUtils.isNotEmpty((CharSequence) baseBean.Context);
//					} else {
//						ObjectUtils.isNotEmpty((CharSequence) baseBean.ErrContext);
//					}
//				} catch (Exception unused) {
//					MyHelper.b("sync_sns_over", "true");
//				}
//				MyHelper.b("sync_sns_over", "true");
//			}
//		});
//	}
}
