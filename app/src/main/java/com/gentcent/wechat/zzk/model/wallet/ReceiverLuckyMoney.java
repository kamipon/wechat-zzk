package com.gentcent.wechat.zzk.model.wallet;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.background.MessageConvert;
import com.gentcent.wechat.zzk.background.UploadUtil;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.model.chatroom.bean.ChatRoomRedPocketMemberBean;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;
import com.gentcent.zzk.xped.XC_MethodHook;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReceiverLuckyMoney {
	static String TAG = "ReceiverLuckyMoney";
	static boolean b = false;
	static boolean isautomate = false;
	int onResume = 0;
	int groupflag = 0;
	
	static {
		HookLuckMoney.init_7_0_3();
	}
	
	/**
	 * 自动接收转账
	 */
	public static void autoReceive(final LoadPackageParam lpparam, final String talker, final String linkUrl) {
		String is_money_reply = MyHelper.readLine("is_money_reply");
		XLog.d(TAG + "is_money_reply is " + TextUtils.equals(is_money_reply, "true"));
		if (TextUtils.equals(is_money_reply, "true")) {
			new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
						try {
							ReceiverLuckyMoney.personalRecevice(talker, linkUrl);
						} catch (Throwable th) {
							XLog.e("error: " + Log.getStackTraceString(th));
						}
					} catch (InterruptedException e) {
						XLog.e("error: " + Log.getStackTraceString(e));
					}
				}
			}.run();
		}
	}
	
	public void hook(LoadPackageParam loadPackageParam) {
		try {
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyDetailUI, loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					Activity activity = (Activity) methodHookParam.thisObject;
					if (activity.getIntent().getBooleanExtra("shouquGroup", false)) {
						ReceiverLuckyMoney.this.groupflag = 1;
						XLog.d("LuckyMoneyDetailUI Groupflag " + ReceiverLuckyMoney.this.groupflag);
					}
					if (activity.getIntent().getBooleanExtra("send_red_package", false)) {
						ReceiverLuckyMoney.isautomate = false;
					}
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyDetailUI, loadPackageParam.classLoader, HookLuckMoney.Runmethod, Integer.TYPE, Integer.TYPE, String.class, loadPackageParam.classLoader.loadClass(HookLuckMoney.RunmethodNeedClass), new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					String str;
					MessageBean messageBean;
					XLog.d("LuckyMoneyDetailUI com.tencent.mm.af.m groupflag" + ReceiverLuckyMoney.this.groupflag);
					Activity activity = (Activity) methodHookParam.thisObject;
					int i = 1;
					if ((activity.getIntent().getBooleanExtra("send_red_package", false) || ReceiverLuckyMoney.this.groupflag == 1) && !ReceiverLuckyMoney.isautomate) {
						ReceiverLuckyMoney.this.groupflag = 1;
						ReceiverLuckyMoney.isautomate = true;
						String linkUrl = (String) XposedHelpers.getObjectField(activity, "oVh");
						Cursor a2 = WcdbHolder.excute(findLuckyMoneyisChatroom(linkUrl));
						String friendwxid = "";
						while (a2.moveToNext()) {
							friendwxid = a2.getString(a2.getColumnIndex("talker"));
						}
						a2.close();
						XLog.d("LuckyMoneyDetailUI com.tencent.mm.af.m:: username" + friendwxid);
						List list = (List) XposedHelpers.getObjectField(activity, HookLuckMoney.Listnpr);
						LinkedList memberInfo = new LinkedList();
						if (!list.isEmpty()) {
							for (Object next : list) {
								XLog.d("b.f  listifq:: " + ZzkUtil.GetClassFieldAndValue(next));
								String userNick = (String) XposedHelpers.getObjectField(next, "oRZ");
								String time = (String) XposedHelpers.getObjectField(next, "oRN");
								String userName = (String) XposedHelpers.getObjectField(next, "userName");
								String note = (String) XposedHelpers.getObjectField(next, "oSb");
								double moneyNumber = ZzkUtil.arith((double) (Long) XposedHelpers.getObjectField(next, "oRM"), 100.0d, 2);
								ChatRoomRedPocketMemberBean chatRoomRedPocketMemberBean = new ChatRoomRedPocketMemberBean(userName, note, userNick, String.valueOf(moneyNumber), time);
								memberInfo.add(chatRoomRedPocketMemberBean);
							}
						}
						int sendusername = linkUrl.indexOf("sendusername=");
						int sendusername2 = linkUrl.indexOf("&", sendusername);
						if (sendusername2 > 0) {
							str = linkUrl.substring(sendusername + 13, sendusername2);
						} else {
							str = linkUrl.substring(sendusername + 13);
						}
						XLog.d(" LuckyMoneyDetailUI c Method:: " + str + "    ::::::username " + friendwxid);
						int sendid = linkUrl.indexOf("sendid=");
						String msgId = linkUrl.substring(sendid + 7, linkUrl.indexOf("&", sendid));
						XLog.d("LuckyMoneyDetailUI c Method:: id " + msgId);
						TextView textView = (TextView) XposedHelpers.getObjectField(activity, "lsg");
						String desc = textView.getText().toString();
						XLog.d("LuckyMoneyDetailUI c Method::  LinkDescription" + textView.getText());
						String content = ((TextView) XposedHelpers.getObjectField(activity, "omt")).getText().toString();
						XLog.d("LuckyMoneyDetailUI c Method::  note " + content);
						TextView textView2 = (TextView) XposedHelpers.getObjectField(activity, "oME");
						XLog.d("LuckyMoneyDetailUI c Method::  IFW " + textView2.getText());
						String money2 = textView2.getText().toString();
						if (!friendwxid.endsWith("@chatroom")) {
							if (money2.isEmpty()) {
								String replace = desc.replace("红包金额", "");
								String money = replace.substring(0, replace.indexOf("元，"));
								XLog.d("LuckyMoneyDetailUI c Method:: moeny " + money);
								XLog.d("send money friendwxid   " + friendwxid);
								messageBean = MessageBean.builderGroupMoneyMessageBean(msgId, 0, friendwxid, "", content, money, desc, linkUrl, 1, 0, memberInfo);
							} else {
								XLog.d("send money friendwxid   " + friendwxid);
								messageBean = MessageBean.builderGroupMoneyMessageBean(msgId, 0, friendwxid, "", content, money2, desc, linkUrl, 1, 1, memberInfo);
							}
						} else if (money2.isEmpty()) {
							if (desc.contains("已领取") && desc.contains("共")) {
								money2 = desc.substring(desc.indexOf("共0.00/") + 6, desc.indexOf("元"));
							}
							XLog.d("LuckyMoneyDetailUI c Method::moeny " + money2);
							XLog.d("send money friendwxid   " + friendwxid);
							messageBean = MessageBean.builderGroupMoneyMessageBean(msgId, 0, friendwxid, str, content, money2, desc, linkUrl, 1, 0, memberInfo);
						} else {
							XLog.d("send money friendwxid   " + friendwxid);
							messageBean = MessageBean.builderGroupMoneyMessageBean(msgId, 0, friendwxid, str, content, money2, desc, linkUrl, 1, 1, memberInfo);
						}
						if (((ImageView) XposedHelpers.getObjectField(activity, "oMP")).getVisibility() != View.VISIBLE) {
							i = 0;
						}
						messageBean.setLinkTitle(String.valueOf(i));
						XLog.d(TAG + "QNUploadUtil LuckyMoneyDetailUI c Method  build: " + GsonUtils.GsonString(messageBean));
						messageBean.setMyWxId(UserDao.getMyWxid());
						UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
						MessageConvert.a(uploadBean, friendwxid);
						UploadUtil.sendToBack(uploadBean);
						onResume = 0;
						groupflag = 0;
						activity.finish();
					}
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyDetailUI, loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					Activity activity = (Activity) methodHookParam.thisObject;
					if (activity.getIntent().getBooleanExtra("shen_shou", false)) {
						XLog.d("LuckyMoneyDetailUI onResume " + ReceiverLuckyMoney.this.onResume + "Groupflag  " + ReceiverLuckyMoney.this.groupflag);
						if (ReceiverLuckyMoney.this.onResume == 1 && ReceiverLuckyMoney.this.groupflag == 0) {
							ReceiverLuckyMoney.this.onResume = 0;
							activity.finish();
						}
						XLog.d("isautomate ::" + ReceiverLuckyMoney.isautomate);
						if (!ReceiverLuckyMoney.isautomate) {
							XposedHelpers.callMethod(activity, "d", activity);
						}
					}
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyDetailUI, loadPackageParam.classLoader, "a", loadPackageParam.classLoader.loadClass(HookLuckMoney.FJ), new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					String sessionname;
					String str2;
					super.afterHookedMethod(methodHookParam);
					Activity activity = (Activity) methodHookParam.thisObject;
					XLog.d(TAG + "LuckyMoneyDetailUI luckymoney.b.f " + ReceiverLuckyMoney.this.onResume);
					XLog.d(TAG + "LuckyMoneyDetailUI isautomate a method " + ReceiverLuckyMoney.isautomate);
					Activity activity2 = (Activity) methodHookParam.thisObject;
					if (ReceiverLuckyMoney.this.onResume == 1 || ReceiverLuckyMoney.this.groupflag == 1 || (activity.getIntent().getBooleanExtra("send_red_package", false) && !ReceiverLuckyMoney.isautomate)) {
						String sql = (String) XposedHelpers.getObjectField(activity2, "oVh");
						XLog.d(" luckyMoney oml ::" + sql);
						Cursor a2 = WcdbHolder.excute(findLuckyMoneyisChatroom(sql));
						String talker = "";
						while (a2.moveToNext()) {
							talker = a2.getString(a2.getColumnIndex("talker"));
						}
						a2.close();
						int indexOf = sql.indexOf("sendusername=");
						int indexOf2 = sql.indexOf("&", indexOf);
						if (indexOf2 > 0) {
							sessionname = sql.substring(indexOf + 13, indexOf2);
						} else {
							sessionname = sql.substring(indexOf + 13);
						}
						XLog.d(TAG + "luckyMoney sessionname " + sessionname + "    ::::::username " + talker);
						int sendidIndex = sql.indexOf("sendid=");
						String substring = sql.substring(sendidIndex + 7, sql.indexOf("&", sendidIndex));
						String charSequence = ((TextView) XposedHelpers.getObjectField(activity2, HookLuckMoney.lucky_money_detail_wishing)).getText().toString();
						TextView textView = (TextView) XposedHelpers.getObjectField(activity2, HookLuckMoney.lucky_money_detail_desc);
						TextView textView2 = (TextView) XposedHelpers.getObjectField(activity2, HookLuckMoney.lucky_money_detail_amount);
						List list = (List) XposedHelpers.getObjectField(activity2, HookLuckMoney.Listnpr);
						XLog.d(TAG + " get listifq" + list.size());
						LinkedList linkedList = new LinkedList();
						if (!list.isEmpty()) {
							for (Object next : list) {
								XLog.d("b.f  listifq:: " + ZzkUtil.GetClassFieldAndValue(next));
								String str9 = (String) XposedHelpers.getObjectField(next, "oRZ");
								String str10 = (String) XposedHelpers.getObjectField(next, "oRN");
								String userName = (String) XposedHelpers.getObjectField(next, "userName");
								String str12 = (String) XposedHelpers.getObjectField(next, "oSb");
								String str13 = sessionname;
								double a3 = ZzkUtil.arith((double) (Long) XposedHelpers.getObjectField(next, "oRM"), 100.0d, 2);
								ChatRoomRedPocketMemberBean chatRoomRedPocketMemberBean = new ChatRoomRedPocketMemberBean(userName, str12, str9, String.valueOf(a3), str10);
								linkedList.add(chatRoomRedPocketMemberBean);
								sessionname = str13;
							}
							str2 = sessionname;
						} else {
							str2 = sessionname;
						}
						ReceiverLuckyMoney.this.a(activity, substring, str2, talker, charSequence, textView2.getText().toString(), textView.getText().toString(), linkedList, ((ImageView) XposedHelpers.getObjectField(activity2, HookLuckMoney.lucky_money_detail_group_icon)).getVisibility() == View.VISIBLE ? 1 : 0);
						XLog.d(" Groupflag enddd" + ReceiverLuckyMoney.this.groupflag);
					}
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyReceiveUI, loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					Activity activity = (Activity) methodHookParam.thisObject;
					String stringExtra = activity.getIntent().getStringExtra(HookLuckMoney.key_username);
					try {
						if (ReceiverLuckyMoney.this.onResume == 3) {
							ReceiverLuckyMoney.this.onResume = 0;
							String key_native_url = activity.getIntent().getStringExtra(HookLuckMoney.key_native_url);
							XLog.d("LuckyMoneyReceiveUI key_native_url is " + key_native_url);
							String str = "sendid=";
							if (ObjectUtils.isNotEmpty(key_native_url) && key_native_url.contains(str)) {
								String substring = key_native_url.substring(key_native_url.indexOf(str) + 7);
								XLog.d("LuckyMoneyReceiveUI tempStr is " + substring);
								String substring2 = substring.substring(0, substring.indexOf("&"));
								XLog.d(TAG + "QNUploadUtil LuckyMoneyReceiveUI sendId is " + substring2);
//								QNUploadUtil.a(substring2, QNUploadUtil.f, 5, stringExtra, "请实名认证，绑定银行卡", "", "", 0, 3);
							}
							ToastUtils.showShort("请实名认证，绑定银行卡");
//							QNUploadUtil.b(stringExtra, "请实名认证，绑定银行卡");
							activity.finish();
						}
					} catch (Exception e) {
						XLog.d("error " + Log.getStackTraceString(e));
					}
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyReceiveUI, loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
				public void afterHookedMethod(final MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					ThreadPoolUtils.getInstance().a(new Runnable() {
						public void run() {
							Activity activity = (Activity) methodHookParam.thisObject;
							TextView textView = (TextView) XposedHelpers.getObjectField(activity, HookLuckMoney.lucky_money_receive_wishing);
							if (textView.getVisibility() == View.VISIBLE) {
								String text = String.valueOf(textView.getText());
								XLog.d("LuckyMoneyReceiveUI text " + activity.getIntent().getBooleanExtra("shen_shou", false) + textView.getText());
								if (activity.getIntent().getBooleanExtra("shen_shou", false)) {
									if (text.equals("该红包已被领取")) {
										XLog.d("群普通 该红包已被领取 ");
										String linkUrl = activity.getIntent().getStringExtra("key_native_url");
										if (!linkUrl.isEmpty()) {
											int indexOf = linkUrl.indexOf("sendid=");
											String msgId = linkUrl.substring(indexOf + 7, linkUrl.indexOf("&", indexOf));
											XLog.d("LuckyMoneyReceiveUI  " + msgId);
											MessageBean messageBean = MessageBean.builderGroupMoneyMessageBean(msgId, 0, activity.getIntent().getStringExtra("key_username"), "", "", "", "", linkUrl, 0, 6, null);
											XLog.d(TAG + "QNUploadUtil LuckyMoneyReceiveUI moneystatus ==6 onresume:: " + GsonUtils.GsonString(messageBean));
											
											messageBean.setMyWxId(UserDao.getMyWxid());
											UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
											MessageConvert.a(uploadBean, activity.getIntent().getStringExtra("key_username"));
											UploadUtil.sendToBack(uploadBean);
											activity.finish();
										}
									} else if (text.equals("该红包已超过24小时。如已领取，可在“我的红包”中查看。")) {
										XLog.d("该红包已超过24小时 finish ");
										activity.finish();
									}
								}
								XLog.d("lgY  is performClick 已被领完");
							}
						}
					}, 3000, TimeUnit.MILLISECONDS);
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyReceiveUI, loadPackageParam.classLoader, HookLuckMoney.Runmethod, Integer.TYPE, Integer.TYPE, String.class, loadPackageParam.classLoader.loadClass(HookLuckMoney.RunmethodNeedClass), new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					ReceiverLuckyMoney.isautomate = false;
					final Activity activity = (Activity) methodHookParam.thisObject;
					ThreadPoolUtils.getInstance().a(new Runnable() {
						public void run() {
							Object objectField = XposedHelpers.getObjectField(activity, "oYP");
							if (objectField != null) {
								long longValue = (Long) XposedHelpers.getObjectField(objectField, "field_receiveTime");
								XLog.d(" field_receiveTime " + longValue);
								if (longValue > 86400000) {
									XLog.d("Luckymoney is out of date" + longValue);
									ReceiverLuckyMoney.this.onResume = 0;
								}
							} else {
								XLog.d("lrK is null");
								ReceiverLuckyMoney.this.onResume = 0;
							}
							try {
								if ((ReceiverLuckyMoney.this.onResume == 1 && ReceiverLuckyMoney.b) || ReceiverLuckyMoney.this.groupflag == 1) {
									XLog.d("LuckyMoneyReceiveUI flag" + ReceiverLuckyMoney.this.onResume);
									final Button button = (Button) XposedHelpers.getObjectField(activity, HookLuckMoney.lucky_money_recieve_open);
									if (button.getVisibility() == View.VISIBLE) {
										activity.runOnUiThread(new Runnable() {
											public void run() {
												button.performClick();
											}
										});
										XLog.d("button is performClick 打大红包");
									}
									final View view = (View) XposedHelpers.getObjectField(activity, HookLuckMoney.lucky_money_recieve_check_detail_ll);
									if (view.getVisibility() == View.VISIBLE) {
										activity.runOnUiThread(new Runnable() {
											public void run() {
												view.performClick();
											}
										});
										XLog.d("viewDetail is performClick 过期查看详情");
									}
									final View view2 = (View) XposedHelpers.getObjectField(activity, HookLuckMoney.lucky_money_recieve_check_detail_ll);
									if (view2.getVisibility() == View.VISIBLE) {
										activity.runOnUiThread(new Runnable() {
											public void run() {
												view2.performClick();
												XLog.d("lgY  is performClick 已被领完");
											}
										});
									}
									ReceiverLuckyMoney.this.onResume = 1;
									ReceiverLuckyMoney.b = false;
								}
							} catch (Exception e) {
								XLog.d("error " + Log.getStackTraceString(e));
							}
						}
					}, 2000, TimeUnit.MILLISECONDS);
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.VerifyModeUI, loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					Activity activity = (Activity) methodHookParam.thisObject;
					ReceiverLuckyMoney.this.onResume = 3;
					activity.finish();
				}
			});
			XposedHelpers.findAndHookMethod(HookLuckMoney.LuckyMoneyReceiveUI, loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
					super.afterHookedMethod(methodHookParam);
					Activity activity = (Activity) methodHookParam.thisObject;
					XLog.d(TAG + " LuckyMoneyReceiveUI class   " + ZzkUtil.GetClassFieldAndValue(activity));
					if (activity.getIntent().getBooleanExtra("shen_shou", false)) {
						Cursor a2 = WcdbHolder.excute(findLuckyMoneynformation(activity.getIntent().getStringExtra("key_native_url")), "EnMicroMsg.db");
						if (a2.moveToNext()) {
							long receiveAmount = a2.getLong(a2.getColumnIndex("receiveAmount"));
							int receiveStatus = a2.getInt(a2.getColumnIndex("receiveStatus"));
							int hbStatus = a2.getInt(a2.getColumnIndex("hbStatus"));
							XLog.d("hbStatus is " + hbStatus);
							if (!(receiveAmount == 0 && receiveStatus == 0 && hbStatus == 0) && !activity.getIntent().getBooleanExtra("shen_shou", false)) {
								XLog.d(" not receiverMoney");
								ReceiverLuckyMoney.b = false;
								ReceiverLuckyMoney.this.onResume = 0;
							} else {
								XLog.d("can receiverMoney");
								ReceiverLuckyMoney.b = true;
								ReceiverLuckyMoney.this.onResume = 1;
							}
						} else {
							XLog.d(" can not receiverMoney ");
							ReceiverLuckyMoney.b = false;
							ReceiverLuckyMoney.this.onResume = 0;
						}
						a2.close();
						if (activity.getIntent().getBooleanExtra("shouquGroup", false)) {
							XLog.d(" greup");
							ReceiverLuckyMoney.this.groupflag = 1;
							return;
						}
						ReceiverLuckyMoney.this.groupflag = 0;
					}
				}
			});
		} catch (Throwable th) {
			XLog.e(TAG + " needHook e:" + Log.getStackTraceString(th));
		}
	}
	
	public void a(final Activity activity) {
		ThreadPoolUtils.getInstance().a(new Runnable() {
			public void run() {
				XLog.d("finishLuckyDetailAct start activity is " + activity);
				if (activity != null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							ReceiverLuckyMoney.this.groupflag = 0;
							ReceiverLuckyMoney.this.onResume = 0;
							activity.finish();
							XLog.d("finishLuckyDetailAct finish activity is " + activity);
						}
					});
				}
			}
		}, 3000, TimeUnit.MILLISECONDS);
	}
	
	public void a(Activity activity, String msgId, String chatroomMemberWxId, String friendWxId, String conent, String money, String desc, List<ChatRoomRedPocketMemberBean> memberInfo, int i) {
		MessageBean messageBean;
		XLog.d(TAG + "get bean    ::vis" + i + "sendid" + msgId + "sendname " + chatroomMemberWxId + "ChatroomMemberWxId " + friendWxId + "note " + conent
				+ "Money " + money + "LinkDescription" + desc + "  memberBeans  " + GsonUtils.GsonString(memberInfo));
		if (!friendWxId.endsWith("@chatroom")) {
			messageBean = MessageBean.builderGroupMoneyMessageBean(msgId, 0, friendWxId, "", conent, money, desc, "", 0, 1, memberInfo);
		} else {
			messageBean = MessageBean.builderGroupMoneyMessageBean(msgId, 0, friendWxId, chatroomMemberWxId, conent, money, desc, "", 0, 1, memberInfo);
		}
		if (friendWxId.endsWith("@chatroom")) {
			if (chatroomMemberWxId.equals(UserDao.getMyWxid())) {
				if (this.groupflag != 1) {
					return;
				}
			}
			messageBean.setLinkTitle(String.valueOf(i));
			if (messageBean.getMoney().isEmpty() && i == 1) {
				messageBean.setStatus(7);
				XLog.d("get bean  money is 0 change" + GsonUtils.GsonString(messageBean));
			}
			isautomate = true;
			XLog.d(TAG + "QNUploadUtil receiver redpacket getbean  isf and chatroom ::" + GsonUtils.GsonString(messageBean));
			messageBean.setMyWxId(UserDao.getMyWxid());
			UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
			MessageConvert.a(uploadBean, friendWxId);
			UploadUtil.sendToBack(uploadBean);
			a(activity);
			return;
		}
		if (!chatroomMemberWxId.equals(UserDao.getMyWxid())) {
			messageBean.setLinkTitle(String.valueOf(i));
			XLog.d(TAG + "QNUploadUtil receiver redpacket getbean  isf and ChatroomMemberWxId ::" + GsonUtils.GsonString(messageBean));
			messageBean.setMyWxId(UserDao.getMyWxid());
			UploadBean uploadBean = new UploadBean(messageBean, MyHelper.readLine("phone-id"));
			MessageConvert.a(uploadBean, friendWxId);
			UploadUtil.sendToBack(uploadBean);
			a(activity);
		}
	}
	
	/**
	 * 个人接受转账
	 */
	public static void personalRecevice(String talker, String linkUrl) {
		try {
			LoadPackageParam lpparam = MainManager.wxLpparam;
			Intent intent = new Intent(MainManager.activity, lpparam.classLoader.loadClass(HookLuckMoney.LuckyMoneyReceiveUI));
			intent.putExtra(HookLuckMoney.key_native_url, linkUrl);
			intent.putExtra(HookLuckMoney.key_username, talker);
			intent.putExtra("shen_shou", true);
			XLog.d("luckyMoney sessionname" + talker);
			intent.putExtra("shouquGroup", true);
			MainManager.activity.startActivity(intent);
		} catch (Exception e) {
			XLog.d(TAG + "luckyMoney error: " + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 群聊接收转账
	 */
	public static void groupRecevice(String talker, String linkUrl) {
		try {
			LoadPackageParam lpparam = MainManager.wxLpparam;
			Intent intent = new Intent(MainManager.activity, lpparam.classLoader.loadClass(HookLuckMoney.LuckyMoneyReceiveUI));
			intent.putExtra(HookLuckMoney.key_native_url, linkUrl);
			intent.putExtra(HookLuckMoney.key_username, talker);
			intent.putExtra("shen_shou", true);
			intent.putExtra("shouquGroup", true);
			MainManager.activity.startActivity(intent);
		} catch (Exception e) {
			XLog.d(TAG + "luckyMoney error: " + Log.getStackTraceString(e));
		}
	}
	
	public static String findLuckyMoneyisChatroom(String str) {
		return "select * from message where content like'%" + str + "%'";
	}
	
	public static String findLuckyMoneynformation(String str) {
		return "select * from WalletLuckyMoney  where mNativeUrl ='" +
				str +
				"'";
	}
	
	public static String findLuckyMoneyContent(String str) {
		return "select * from message where content like'%" + str + "%' and type = 436207665";
	}
}
