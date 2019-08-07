package com.gentcent.wechat.zzk.model.syncinfo;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.HookSQL;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage.LoadPackageParam;

import java.util.HashMap;

/**
 * @author zuozhi
 * @since 2019-08-05
 */
public class SyncInfoManager {
	
	/**
	 * user信息补全
	 */
	public static void userCompletion(UserBean userBean) {
		try {
			if (!ObjectUtils.isEmpty(userBean.username)) {
				if (userBean.username.endsWith("@chatroom")) {
					setChatroom(userBean);
				} else {
					XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.model.av"), "XE");
					Object user = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.model.c"), "VI"), "anm", userBean.username);
					setSex(user, userBean);
					setAddress(user, userBean);
					setSignature(user, userBean);
					setSource(user, userBean);
				}
			}
		} catch (Exception e) {
			XLog.e("setUserAttribute is " + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 获得群聊成员的群昵称
	 *
	 * @param username   群聊id
	 * @param memberlist 成员id列表
	 */
	public static HashMap<String, String> getGroupUserName(String username, String memberlist) {
		HashMap<String, String> hashMap = new HashMap<>();
		if (TextUtils.isEmpty(memberlist)) {
			return hashMap;
		}
		String[] split = memberlist.split(";");
		Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.model.av", MainManager.wxLpparam.classLoader), "XE"), "VI");
		Object a2 = initChatRoom(username, MainManager.wxLpparam);
		if (a2 != null) {
			for (String str3 : split) {
				String a3 = (String) XposedHelpers.callMethod(a2, "mk", str3);
				if (!TextUtils.isEmpty(a3)) {
					hashMap.put(str3, a3);
				} else {
					hashMap.put(str3, (String) XposedHelpers.getObjectField(XposedHelpers.callMethod(callMethod, "anl", str3), "field_nickname"));
				}
			}
		} else {
			for (String str4 : split) {
				hashMap.put(str4, (String) XposedHelpers.getObjectField(XposedHelpers.callMethod(callMethod, "anl", str4), "field_nickname"));
			}
		}
		return hashMap;
	}
	
	/**
	 * 初始化群聊
	 *
	 * @param username 群聊ID
	 */
	private static Object initChatRoom(String username, LoadPackageParam loadPackageParam) {
		try {
			Object[] objArr = {username};
			return XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.kernel.g"), "L", loadPackageParam.classLoader.loadClass("com.tencent.mm.plugin.chatroom.a.c")), "VR"), "nB", objArr);
		} catch (Exception unused) {
			XLog.d(" initChatRoom error ");
			return null;
		}
	}
	
	/**
	 * 聊天室信息补全
	 */
	private static void setChatroom(UserBean userBean) {
		try {
			userBean.notice = findNotice(userBean.username);
			userBean.isAddAddressBook = findIsAddAddressBook(userBean.username);
			userBean.roomOwner = findChatRoomOwner(userBean.username);
			userBean.NameMap = getGroupUserName(userBean.username, userBean.memberlist);
		} catch (Exception unused) {
			XLog.e("User setChatroom error");
		}
	}
	
	/**
	 * 消息免打扰
	 *
	 * @param roomId roomId
	 * @return
	 */
	public static String findNotice(String roomId) {
		try {
			return (String) XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.model.m"), "md", roomId);
		} catch (Exception e) {
			XLog.e("room_notice error is " + Log.getStackTraceString(e));
			return "";
		}
	}
	
	/**
	 * 保存到通讯录
	 *
	 * @param roomId roomId
	 * @return
	 */
	public static boolean findIsAddAddressBook(String roomId) {
		try {
			Class loadClass = MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.plugin.messenger.foundation.a.j");
			Object callMethod = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.kernel.g"), "L", loadClass), "VI"), "anm", roomId);
			if (callMethod == null) {
				return false;
			}
			return (Boolean) XposedHelpers.callStaticMethod(MainManager.wxLpparam.classLoader.loadClass("com.tencent.mm.m.a"), "iv", new Object[]{XposedHelpers.getObjectField(callMethod, "field_type")});
		} catch (Exception unused) {
			return false;
		}
	}
	
	/**
	 * 找房主
	 *
	 * @param roomId 微信ID
	 */
	public static String findChatRoomOwner(String roomId) {
		Cursor a = WcdbHolder.excute(HookSQL.findChatRoomOwner(roomId));
		String owner = "";
		if (ObjectUtils.isNotEmpty((Object) a)) {
			while (a.moveToNext()) {
				owner = a.getString(a.getColumnIndex("roomowner"));
			}
		}
		a.close();
		return owner;
	}
	
	//获取性别
	public static void setSex(Object user, UserBean userBean) {
		try {
			userBean.sex = (Integer) XposedHelpers.getObjectField(user, "sex");
		} catch (Exception unused) {
			XLog.e("User get sex error");
		}
	}
	
	//获取地区
	public static void setAddress(Object user, UserBean userBean) {
		try {
			userBean.province = (String) XposedHelpers.getObjectField(user, "dhK");
			userBean.region = (String) XposedHelpers.getObjectField(user, "dhL");
		} catch (Exception unused) {
			XLog.e("User setAddress error");
		}
	}
	
	//获取签名S
	public static void setSignature(Object user, UserBean userBean) {
		try {
			userBean.signature = (String) XposedHelpers.getObjectField(user, "signature");
		} catch (Exception unused) {
			XLog.e("User setSignature error");
		}
	}
	
	//获取添加来源
	private static void setSource(Object user, UserBean userBean) {
		try {
			int intValue = (Integer) XposedHelpers.callMethod(user, "getSource");
			int intValue2 = (Integer) XposedHelpers.callMethod(user, "MD");
			if (intValue != 8) {
				if (intValue != 10) {
					if (intValue == 48) {
						userBean.sourceType = 1;
						userBean.sourceText = "雷达";
						return;
					} else if (intValue != 76) {
						switch (intValue) {
							case 0:
								break;
							case 1:
								if (intValue2 <= 1000000) {
									userBean.sourceType = 1;
									userBean.sourceText = "通过搜索qq号添加";
									return;
								}
								userBean.sourceType = 2;
								userBean.sourceText = "对方通过搜索qq号添加";
								return;
							default:
								switch (intValue) {
									case 3:
										if (intValue2 <= 1000000) {
											userBean.sourceType = 1;
											userBean.sourceText = "通过搜索微信号添加";
											return;
										}
										userBean.sourceType = 2;
										userBean.sourceText = "对方通过搜索微信号添加";
										return;
									case 4:
										break;
									case 5:
										break;
									case 6:
										break;
									default:
										switch (intValue) {
											case 12:
												break;
											case 13:
												break;
											case 14:
												break;
											case 15:
												if (intValue2 <= 1000000) {
													userBean.sourceType = 1;
													userBean.sourceText = "通过手机号添加";
													return;
												}
												userBean.sourceType = 2;
												userBean.sourceText = "对方通过手机号添加";
												return;
											case 16:
												break;
											case 17:
												if (intValue2 <= 1000000) {
													userBean.sourceType = 1;
													userBean.sourceText = "通过名片分享添加";
													return;
												}
												userBean.sourceType = 2;
												userBean.sourceText = "对方通过名片分享添加";
												return;
											case 18:
												if (intValue2 <= 1000000) {
													userBean.sourceType = 1;
													userBean.sourceText = "通过附近人添加";
													return;
												}
												userBean.sourceType = 2;
												userBean.sourceText = "对方通过附近人添加";
												return;
											case 19:
												break;
											default:
												switch (intValue) {
													case 22:
													case 23:
													case 24:
													case 26:
													case 27:
													case 28:
													case 29:
														if (intValue2 <= 1000000) {
															userBean.sourceType = 1;
															userBean.sourceText = "通过摇一摇添加";
															return;
														}
														userBean.sourceType = 2;
														userBean.sourceText = "对方通过摇一摇添加";
														return;
													case 25:
														if (intValue2 <= 1000000) {
															userBean.sourceType = 1;
															userBean.sourceText = "通过漂流瓶添加";
															return;
														}
														userBean.sourceType = 2;
														userBean.sourceText = "对方通过漂流瓶添加";
														return;
													case 30:
														if (intValue2 <= 1000000) {
															userBean.sourceType = 1;
															userBean.sourceText = "通过扫一扫添加";
															return;
														}
														userBean.sourceType = 2;
														userBean.sourceText = "对方通过扫一扫添加";
														return;
													default:
														switch (intValue) {
															case 33:
																break;
															case 34:
																userBean.sourceType = 1;
																userBean.sourceText = "公众号";
																return;
															default:
																switch (intValue) {
																	case 58:
																	case 59:
																	case 60:
																		userBean.sourceType = 1;
																		userBean.sourceText = "通过Google联系人添加";
																		return;
																	default:
																		userBean.sourceType = 0;
																		userBean.sourceText = "未知来源，微信没有";
																		return;
																}
														}
												}
										}
								}
						}
					} else {
						userBean.sourceType = 1;
						userBean.sourceText = "通过LinkedIn添加";
						return;
					}
				}
				if (intValue2 <= 1000000) {
					userBean.sourceType = 1;
					userBean.sourceText = "通过手机通讯录添加";
					return;
				}
				userBean.sourceType = 2;
				userBean.sourceText = "对方通过手机通讯录添加";
				return;
			}
			if (intValue2 <= 1000000) {
				userBean.sourceType = 1;
				userBean.sourceText = "通过群聊添加";
				return;
			}
			userBean.sourceType = 2;
			userBean.sourceText = "对方通过群聊添加";
		} catch (Exception unused) {
			XLog.e("User get source error");
		}
	}
	
}
