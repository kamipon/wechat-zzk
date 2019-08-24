package com.gentcent.wechat.zzk.model.wallet;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.background.MessageConvert;
import com.gentcent.wechat.zzk.background.UploadUtil;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.model.wallet.bean.Info;
import com.gentcent.wechat.zzk.model.wallet.bean.TransferBean;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.io.IOException;

import cn.jpush.android.service.WakedResultReceiver;

public class ReceivableManger {
	public static Activity activity = null;
	public static boolean isMyTask = false;
	public static String sender_name = "";
	public static boolean isReceiver = true;
	public static boolean isClick = false;
	private static String transfer_id = "";
	
	public static void identityVerify() {
		ToastUtils.showShort("请实名认证，绑定银行卡");
		if (ObjectUtils.isNotEmpty(sender_name) && ObjectUtils.isNotEmpty(transfer_id)) {
//			QNUploadUtil.b(sender_name, "请实名认证，绑定银行卡");
//			QNUploadUtil.a(transfer_id, QNUploadUtil.e, 6, sender_name, "请实名认证，绑定银行卡", "", "", 0, 3);
			sender_name = "";
			transfer_id = "";
		}
	}
	
	public static void hook(final LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.wallet_core.id_verify.SwitchRealnameVerifyModeUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					if (isMyTask) {
						XLog.d("ReceivableManger Hook_RemittanceDetailUI SwitchRealnameVerifyModeUI onCreate");
						identityVerify();
						((Activity) methodHookParam.thisObject).finish();
						isMyTask = false;
						if (activity != null) {
							activity.finish();
						}
					}
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					activity = (Activity) methodHookParam.thisObject;
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI$18", loadPackageParam.classLoader, "onClick", View.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					isClick = true;
				}
			});
			XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI", loadPackageParam.classLoader, "onSceneEnd", Integer.TYPE, Integer.TYPE, String.class, loadPackageParam.classLoader.loadClass("com.tencent.mm.ak.m"), new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					try {
						Activity activity = (Activity) methodHookParam.thisObject;
						if (methodHookParam.args[3] != null) {
							XLog.d("ReceivableManger Hook_RemittanceDetailUI RemittanceDetailUI d");
							String name = methodHookParam.args[3].getClass().getName();
							XLog.d("ReceivableManger  parm4classname:" + name);
							int code = (Integer) methodHookParam.args[0];
							int result = (Integer) methodHookParam.args[1];
							XLog.d("ReceivableManger receive code:" + code + ",result:" + result);
							if ("com.tencent.mm.plugin.remittance.model.x".equals(name) && code == 0 && result == 0) {
								int status = XposedHelpers.getIntField(methodHookParam.args[3], "status");
								XLog.d("ReceivableManger receive status:" + status);
								if (status == 2000) {
									XLog.d("Receivableing   " + isMyTask);
									if (isMyTask) {
										Button button = (Button) XposedHelpers.getObjectField(methodHookParam.thisObject, "qZH");
										if (button != null) {
											XLog.d("ReceivableManger receive 4");
											OnClickListener onClickListener = (OnClickListener) XposedHelpers.newInstance(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI$18"), new Object[]{methodHookParam.thisObject});
											if (onClickListener != null) {
												onClickListener.onClick(button);
											}
										}
									}
								} else if (status == 2001) {
									if (XposedHelpers.getObjectField(methodHookParam.thisObject, "qZk") != null && isClick) {
										isClick = false;
										isReceiver = true;
										Intent intent = activity.getIntent();
										String transfer_id = intent.getStringExtra("transfer_id");
										String transaction_id = intent.getStringExtra("transaction_id");
										XLog.d("transfer_id" + transfer_id + "transaction_id" + transaction_id);
										String sql1 = "select * from message where message.content like '%" + transfer_id + "%' and message.content like '%" + transaction_id + "%'and message.isSend = 1";
										Cursor a2 = WcdbHolder.excute(sql1, "EnMicroMsg.db");
										if (a2.moveToNext()) {
											XLog.d(" has transfer_id");
											int msgId = a2.getInt(a2.getColumnIndex("msgId"));
											XLog.d("message msgId " + msgId);
											String sql2 = "select * from Appmessage\nwhere Appmessage.msgId = " + msgId;
											a2.close();
											Cursor a3 = WcdbHolder.excute(sql2, "EnMicroMsg.db");
											if (!a3.moveToNext()) {
												isReceiver = false;
											}
											a3.close();
										}
										XLog.d(" receiverManager isReceiver  ::" + isReceiver);
										if (isReceiver) {
											((Activity) methodHookParam.thisObject).finish();
											activity = null;
											isMyTask = false;
										}
									}
								} else if (status == 2004) {
									((Activity) methodHookParam.thisObject).finish();
									activity = null;
									isMyTask = false;
								}
							}
						}
					} catch (Throwable th) {
						XLog.d("ReceivableManger handle e:" + Log.getStackTraceString(th));
					}
				}
			});
		} catch (Throwable th) {
			XLog.d("ReceivableManger Hook_RemittanceDetailUI e: " + Log.getStackTraceString(th));
		}
	}
	
	/**
	 * 收款
	 *
	 * @param msgId msgId
	 */
	public static void recieve(long msgId) {
		LoadPackageParam lpparam = MainManager.wxLpparam;
		XLog.d("ReceivableManger ReceivableManger dohandle:" + msgId);
		Info info = getInfo(lpparam, msgId);
		if (info != null && info.field_isSend != 1) {
			XLog.d("ReceivableManger ReceivableManger info:" + info.toString());
			if (MainManager.activity != null) {
				if (verify(MainManager.activity.getPackageName(), "com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI", MainManager.activity)) {
					XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  is active ");
					if (activity != null) {
						XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  finish  start");
						activity.finish();
						activity = null;
						XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  finish  end");
					} else {
						XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  mActivity  start");
						try {
							Runtime.getRuntime().exec("input keyevent 4");
						} catch (IOException e) {
							XLog.e("error:" + Log.getStackTraceString(e));
						}
						XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  mActivity  end");
					}
				}
				try {
					goActivity(lpparam, info);
				} catch (ClassNotFoundException e) {
					XLog.e("error:" + Log.getStackTraceString(e));
				}
			}
		}
	}
	
	/**
	 * 立即退款
	 *
	 * @param messageId msgId
	 */
	public static void refund(long messageId) {
		try {
			LoadPackageParam lpparam = MainManager.wxLpparam;
			Info info = getInfo(lpparam, messageId);
			if (info != null) {
				if (info.field_isSend != 1) {
					if (verify(MainManager.activity.getPackageName(), "com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI", MainManager.activity)) {
						XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  is active ");
						if (activity != null) {
							XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  finish  start");
							activity.finish();
							activity = null;
							XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  finish  end");
						} else {
							XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  mActivity  start");
							try {
								Runtime.getRuntime().exec("input keyevent 4");
							} catch (IOException e) {
								XLog.e("error:" + Log.getStackTraceString(e));
							}
							XLog.d("ReceivableManger ReceivableManger g_RemittanceDetailUI  mActivity  end");
						}
					}
					XLog.d("getInfo info is " + GsonUtils.GsonString(info));
					Intent intent = new Intent(MainManager.activity, lpparam.classLoader.loadClass("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI"));
					intent.putExtra("transaction_id", info.transaction_id);
					intent.putExtra("transfer_id", info.transfer_id);
					intent.putExtra("total_fee", info.total_fee);
					intent.putExtra("sender_name", info.field_talker);
					intent.putExtra("invalid_time", info.invalid_time);
					intent.putExtra("messageId", messageId);
					intent.putExtra("shen_shou", true);
					MainManager.activity.startActivity(intent);
					XLog.d("getInfo success callMethod");
				}
			}
		} catch (Exception e) {
			XLog.d("getInfo error is " + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 退款
	 */
	public static void hook2(LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI"), "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					Activity activity = (Activity) methodHookParam.thisObject;
					if (activity.getIntent().getBooleanExtra("shen_shou", false)) {
						XposedHelpers.callMethod(activity, "coK");
					}
				}
			});
		} catch (Exception e2) {
			XLog.d("refund error is " + Log.getStackTraceString(e2));
		}
	}
	
	/**
	 * 跳转页面
	 */
	private static void goActivity(LoadPackageParam loadPackageParam, Info info) throws ClassNotFoundException {
		Intent intent = new Intent(MainManager.activity, loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI"));
		intent.putExtra("sender_name", info.field_talker);
		intent.putExtra("invalid_time", info.invalid_time);
		intent.putExtra("is_sender", false);
		intent.putExtra("appmsg_type", info.appmsg_type);
		intent.putExtra("transfer_id", info.transfer_id);
		intent.putExtra("transaction_id", info.transaction_id);
		intent.putExtra("effective_date", info.effective_date);
		intent.putExtra("total_fee", info.total_fee);
		intent.putExtra("fee_type", info.fee_type);
		transfer_id = info.transfer_id;
		sender_name = info.field_talker;
		isMyTask = true;
		MainManager.activity.startActivity(intent);
	}
	
	public static boolean verify(String packageName, String packageName2, Context context) {
		boolean result = true;
		RunningTaskInfo runningTaskInfo = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0);
		if (runningTaskInfo == null) {
			return false;
		}
		if (!TextUtils.equals(runningTaskInfo.topActivity.getPackageName(), packageName) || !TextUtils.equals(runningTaskInfo.topActivity.getClassName(), packageName2)) {
			result = false;
		}
		return result;
	}
	
	public static Info getInfo(LoadPackageParam loadPackageParam, long msgId) {
		try {
			Object callMethod = XposedHelpers.callMethod(XposedHelpers.callMethod(ZzkUtil.getWxCore(loadPackageParam), "bXb"), "kP", msgId);
			String field_content = (String) XposedHelpers.getObjectField(callMethod, "field_content");
			String field_reserved = (String) XposedHelpers.getObjectField(callMethod, "field_reserved");
			String field_talker = (String) XposedHelpers.getObjectField(callMethod, "field_talker");
			int field_isSend = XposedHelpers.getIntField(callMethod, "field_isSend");
			
			Object callStaticMethod = XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.ah.j$b"), "af", field_content, field_reserved);
			if (callStaticMethod == null) {
				return null;
			}
			int paysubtype = XposedHelpers.getIntField(callStaticMethod, "fCk");
			XLog.d("ReceivableManger  handle paysubtype:" + paysubtype);
			if (paysubtype != 1 && paysubtype != 7) {
				return null;
			}
			
			Info info = Info.init(callStaticMethod, String.valueOf(msgId));
			info.field_content = field_content;
			info.field_reserved = field_reserved;
			info.field_talker = field_talker;
			info.field_isSend = field_isSend;
			return info;
		} catch (Exception e) {
			XLog.d("fromMsgidGetInfo error " + Log.getStackTraceString(e));
			return null;
		}
	}
	
	/**
	 * 处理消息回调
	 */
	public static void handel(ContentValues contentValues) {
		XLog.d("ReceivableManger activeTransferMessageHandle");
		int isSend = contentValues.getAsInteger("isSend");
		long msgId = contentValues.getAsLong("msgId");
		String talker = contentValues.getAsString("talker");
		String content = ZzkUtil.b(contentValues.getAsString("content"));
		XLog.d("ReceivableManger isSend" + isSend + " ReceivableManger 转账他人  ::::::::" + content);
		TransferBean transferBean = GsonUtils.GsonToBean(content, TransferBean.class);
		XLog.d("transferBean.msg.appmsg.wcpayinfo.paysubtype" + transferBean.msg.appmsg.wcpayinfo.paysubtype);
		
		String transferid = transferBean.msg.appmsg.wcpayinfo.transferid;
		String pay_memo = transferBean.msg.appmsg.wcpayinfo.pay_memo;
		String feedesc = transferBean.msg.appmsg.wcpayinfo.feedesc.replace("￥", "");
		MessageBean messageBean = null;
		if (isSend == 1) {
			if ("3".equals(transferBean.msg.appmsg.wcpayinfo.paysubtype) || "5".equals(transferBean.msg.appmsg.wcpayinfo.paysubtype)) {
				if (Integer.valueOf(transferBean.msg.appmsg.type) == 2000) {
					messageBean = MessageBean.buldeMoneyMessageBean(transferid, isSend, talker, pay_memo, feedesc, String.valueOf(msgId), 1);
					XLog.d("ReceivableManger 收成功B转账" + GsonUtils.GsonString(messageBean));
				}
			} else if (WakedResultReceiver.CONTEXT_KEY.equals(transferBean.msg.appmsg.wcpayinfo.paysubtype)) {
				if (Integer.valueOf(transferBean.msg.appmsg.type) == 2000) {
					messageBean = MessageBean.buldeMoneyMessageBean(transferid, isSend, talker, pay_memo, feedesc, String.valueOf(msgId), 0);
					XLog.d("ReceivableManger A给B转账 " + GsonUtils.GsonString(messageBean));
				}
			} else if ("4".equals(transferBean.msg.appmsg.wcpayinfo.paysubtype)) {
				XLog.d("ReceivableManger" + "send 1         paysubtype  4");
				messageBean = MessageBean.buldeMoneyMessageBean(transferid, isSend, talker, pay_memo, feedesc, String.valueOf(msgId), 2);
				XLog.d("ReceivableManger A退还B转账  " + GsonUtils.GsonString(messageBean));
			}
		} else if (isSend == 0) {
			XLog.d(" issend == 0 ");
			if ("3".equals(transferBean.msg.appmsg.wcpayinfo.paysubtype)) {
				XLog.d("ReceivableManger" + "b收到a的转账 3");
				if (Integer.valueOf(transferBean.msg.appmsg.type) == 2000) {
					messageBean = MessageBean.buldeMoneyMessageBean(transferid, isSend, talker, pay_memo, feedesc, String.valueOf(msgId), 1);
					XLog.d("ReceivableManger b收到a的转账 " + messageBean);
				}
			} else if (WakedResultReceiver.CONTEXT_KEY.equals(transferBean.msg.appmsg.wcpayinfo.paysubtype) || "7".equals(transferBean.msg.appmsg.wcpayinfo.paysubtype)) {
				if (Integer.valueOf(transferBean.msg.appmsg.type) == 2000) {
//					String is_money_reply = MyHelper.readLine("is_money_reply");
//					XLog.d("ReceivableManger is_money_reply is " + TextUtils.equals(is_money_reply, "true"));
					XLog.d("ReceivableManger activeTransferMessageHandle transferid \" + transferBean.msg.appmsg.wcpayinfo.transferid");
					messageBean = MessageBean.buldeMoneyMessageBean(transferid, isSend, talker, pay_memo, feedesc, String.valueOf(msgId), 0);
					XLog.d("ReceivableManger B给A发 转账  " + GsonUtils.GsonString(messageBean));
//					if (TextUtils.equals(is_money_reply, "true")) {
//						XLog.d("ReceivableManger" + " dohandle 收转账了 ");
//						recieve(msgId);
//					}
				}
			} else if ("4".equals(transferBean.msg.appmsg.wcpayinfo.paysubtype)) {
				XLog.d("ReceivableManger" + "send  paysubtype  4");
				messageBean = MessageBean.buldeMoneyMessageBean(transferid, isSend, talker, pay_memo, feedesc, String.valueOf(msgId), 2);
				XLog.d("ReceivableManger B退还A转账  " + GsonUtils.GsonString(messageBean));
			}
		}
		messageBean.setMyWxId(UserDao.getMyWxid());
		UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
		MessageConvert.a(uploadBean, talker);
		if (messageBean != null) UploadUtil.sendToBack(uploadBean);
	}
}
