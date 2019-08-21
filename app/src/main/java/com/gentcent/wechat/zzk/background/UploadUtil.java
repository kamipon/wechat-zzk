package com.gentcent.wechat.zzk.background;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gentcent.wechat.zzk.bean.PhoneInfoBean;
import com.gentcent.wechat.zzk.bean.SystemInfoBean;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.model.message.bean.MessageBean;
import com.gentcent.wechat.zzk.model.wallet.bean.BackMoneyResult;
import com.gentcent.wechat.zzk.model.wallet.bean.EnWalletBean;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 请求服务器上传资料
 *
 * @author zuozhi
 * @since 2019-08-01
 */
public class UploadUtil {
	//判断是否绑定设备
	private static boolean isbinded() {
		String sysInfo = MyHelper.readLine("sys-info");
		if (StringUtils.isEmpty(sysInfo)) {
			ToastUtils.showShort("信息有误，请重新绑定设备");
			XLog.e("信息有误，请重新绑定设备");
			return false;
		} else {
			try {
				GsonUtils.GsonToBean(sysInfo, SystemInfoBean.class);
			} catch (Exception e) {
				ToastUtils.showShort("信息有误，请重新绑定设备");
				XLog.e("信息有误，请重新绑定设备");
				return false;
			}
		}
		String phoneID = MyHelper.readLine("phone-id");
		if (StringUtils.isEmpty(phoneID)) {
			ToastUtils.showShort("信息有误，请重新绑定设备");
			XLog.e("信息有误，请重新绑定设备");
			return false;
		}
		return true;
	}
	
	/**
	 * 绑定设备
	 */
	public static void bindDevice(final PhoneInfoBean p) {
		ThreadPoolUtils.getInstance().a(new Runnable() {
			public void run() {
				OkHttpUtils.post().url(Api.appPhone)
						.addParams("IMEI", p.IMEI)
						.addParams("phoneBrand", p.phoneBrand)
						.addParams("wxVersion", p.wxVersion)
						.addParams("phonemodel", p.phonemodel)
						.addParams("acId", p.acId)
						.addParams("isroot", String.valueOf(p.isroot))
						.addParams("isxPosed", String.valueOf(p.isxPosed))
						.addParams("softwareVersion", p.softwareVersion)
						.addParams("electric", p.electric)
						.build().execute(
						new StringCallback() {
							@Override
							public void onError(Call call, Exception e, int id) {
								XLog.e("error: " + Log.getStackTraceString(e));
								ToastUtils.showShort("绑定失败，请检查网络后重新再试");
							}
							
							@Override
							public void onResponse(String response, int id) {
								XLog.d("response: " + response);
								Map<String, Object> map = GsonUtils.GsonToMaps(response);
								ToastUtils.showShort((String) Objects.requireNonNull(map.get("msg")));
								if ((boolean) map.get("flag")) {
									SystemInfoBean systemInfoBean = new SystemInfoBean();
									systemInfoBean.phoneId = (String) map.get("phoneId");
									systemInfoBean.actId = (String) map.get("actId");
									systemInfoBean.actName = (String) map.get("actName");
									systemInfoBean.keepMan = (String) map.get("keepMan");
									systemInfoBean.picUrl = (String) map.get("picUrl");
									MyHelper.writeLine("sys-info", GsonUtils.GsonString(systemInfoBean));
									MyHelper.writeLine("phone-id", (String) map.get("phoneId"));
								}
							}
						});
			}
		}, 500, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 绑定微信
	 */
	public static void bindWeixin(final UserBean u) {
		if (!isbinded()) return;
		XLog.e("bindWeixin: " + u.toString());
		XLog.e("phone-id: " + MyHelper.readLine("phone-id"));
		OkHttpUtils.post().url(Api.addweixin + MyHelper.readLine("phone-id"))
				.addParams("weixinID", u.username)
				.addParams("weixin", u.alias)
				.addParams("pic", u.reserved2 == null ? "" : u.reserved2)
				.addParams("province", u.province == null ? "" : u.province)
				.addParams("city", u.region == null ? "" : u.region)
				.addParams("sex", String.valueOf(u.sex))
				.addParams("nick", String.valueOf(u.nickname))
				.addParams("signature", String.valueOf(u.signature))
				.build().execute(
				new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.e("error: " + Log.getStackTraceString(e));
						ToastUtils.showShort("同步失败，请检查网络后重新再试");
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("response: " + response);
						Map<String, Object> map = GsonUtils.GsonToMaps(response);
						if ((boolean) map.get("flag")) {
							MyHelper.writeLine("self-wx", u.toString());
						} else {
							ToastUtils.showShort((String) Objects.requireNonNull(map.get("msg")));
						}
					}
				});
	}
	
	/**
	 * TODO:绑定群聊
	 */
	public static void bindGroup(final UserBean u) {
		if (!isbinded()) return;
		OkHttpUtils.post().url(Api.addgroup)
				.addParams("chartRoomFriendsList", GsonUtils.GsonString(u.chartRoomFriendsList))
				.addParams("isAddAddressBook", String.valueOf(u.isAddAddressBook))
				.addParams("pic", u.reserved2 == null ? "" : u.reserved2)
				.addParams("nickname", u.nickname)
				.addParams("notice", u.notice)
				.addParams("roomOwner", u.roomOwner)
				.addParams("username", u.username)
				.build().execute(
				new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.e("error: " + Log.getStackTraceString(e));
						ToastUtils.showShort("同步失败，请检查网络后重新再试");
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("response: " + response);
						Map<String, Object> map = GsonUtils.GsonToMaps(response);
						if ((boolean) map.get("flag")) {
							MyHelper.writeLine("self-wx", u.toString());
						} else {
							ToastUtils.showShort((String) Objects.requireNonNull(map.get("msg")));
						}
					}
				});
	}
	
	/**
	 * 同步好友
	 */
	public static void bindFriend(final UserBean u) {
		if (!isbinded()) return;
		OkHttpUtils.post().url(Api.appfriend)
				.addParams("weixinID", UserDao.getMyWxid())
				.addParams("friendWeixinID", u.username)
				.addParams("friendWeixin", u.alias)
				.addParams("pic", u.reserved2 == null ? "" : u.reserved2)
				.addParams("province", u.province == null ? "" : u.province)
				.addParams("city", u.region == null ? "" : u.region)
				.addParams("nick", u.nickname)
				.addParams("sex", String.valueOf(u.sex))
				.addParams("desc", u.conRemark)
				.addParams("fromType", String.valueOf(u.sourceType))
				.addParams("from", u.sourceText)
				.addParams("signature", u.signature)
				.build().execute(
				new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.e("error: " + Log.getStackTraceString(e));
						ToastUtils.showShort("同步失败，请检查网络后重新再试");
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("response: " + response);
						Map<String, Object> map = GsonUtils.GsonToMaps(response);
						if ((boolean) map.get("flag")) {
						} else {
							ToastUtils.showShort((String) Objects.requireNonNull(map.get("msg")));
						}
					}
				});
	}
	
	/**
	 * 上传收到的消息
	 */
	public static void sendToBack(String serviceGuid, int status, String msgId, int isSend, int type, final String talker, String content, String money, long createTime, int fileSize, String fileName) {
		if (!isbinded()) return;
		XLog.d("sendToBackend serviceGuid " + serviceGuid);
		if (!verify(talker, type, content)) {
			UploadBean uploadBean = new UploadBean(new UserBean(), new MessageBean(), MyHelper.readLine("phone-id"));
			uploadBean.messageBean.setMyWxId(UserDao.getMyWxid());
			uploadBean.messageBean.setFriendWxId(talker);
			uploadBean.messageBean.setContent(content);
			uploadBean.messageBean.setType(type);
			uploadBean.messageBean.setIsSend(isSend);
			uploadBean.messageBean.setServiceGuid("");
			uploadBean.messageBean.setMsgId(msgId);
			uploadBean.messageBean.setAddTime(createTime);
			uploadBean.messageBean.setStatus(status);
			uploadBean.messageBean.setMoney(money);
			if (fileSize != 0) {
				uploadBean.messageBean.setFileName(fileName);
				uploadBean.messageBean.setFileSize(fileSize);
			}
			if (uploadBean.messageBean.getContent() == null) {
				uploadBean.messageBean.setContent("");
			}
			uploadBean = MessageConvert.a(uploadBean, talker);
			final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			final UploadBean finalUploadBean = uploadBean;
			ThreadPoolUtils.getInstance().a(new Runnable() {
				public void run() {
					XLog.d("sendToBackend param is " + gson.toJson(finalUploadBean));
					OkHttpUtils.post().url(Api.addWchat)
							.addParams("wxmessage", gson.toJson(finalUploadBean.messageBean))
							.addParams("phoneID", finalUploadBean.phoneID)
							.addParams("wxuser", gson.toJson(finalUploadBean.userBean))
							.build().execute(new StringCallback() {
						public void onError(Call call, Exception exc, int i) {
							XLog.e("error: " + Log.getStackTraceString(exc));
						}
						
						public void onResponse(String response, int i) {
							XLog.d("sendToBackend success " + response);
						}
					});
				}
			}, 500, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * 上传收到的消息
	 */
	public static void sendToBack(UploadBean uploadBean) {
		if (!isbinded()) return;
		final MessageBean messageBean = uploadBean.messageBean;
		final UserBean userBean = uploadBean.userBean;
		final String phoneID = uploadBean.phoneID;
		final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final UploadBean finalUploadBean = uploadBean;
		ThreadPoolUtils.getInstance().a(new Runnable() {
			public void run() {
				XLog.d("sendToBackend param is " + gson.toJson(finalUploadBean));
				OkHttpUtils.post().url(Api.addWchat)
						.addParams("wxmessage", gson.toJson(messageBean))
						.addParams("phoneID", phoneID)
						.addParams("wxuser", gson.toJson(userBean))
						.build().execute(new StringCallback() {
					public void onError(Call call, Exception exc, int i) {
						XLog.e("error: " + Log.getStackTraceString(exc));
					}
					
					public void onResponse(String response, int i) {
						XLog.d("sendToBackend success " + response);
					}
				});
			}
		}, 500, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 带文件的消息信息，先上传文件
	 *
	 * @param file       需要上传的文件
	 * @param uploadBean 消息信息
	 */
	public static void uploadFileToBack(final File file, final UploadBean uploadBean, final boolean reRun) {
		if (!isbinded()) return;
		XLog.d(file.toString());
		XLog.d(GsonUtils.GsonString(uploadBean));
		
		ThreadPoolUtils.getInstance().a(new Runnable() {
			public void run() {
				OkHttpUtils.post()
						.addFile(file.getName(), file.getName(), file)
						.url(Api.fileUploadsWchat)
						.addHeader("Content-Type", "multipart/form-data")
						.build()
						.execute(new StringCallback() {
							@Override
							public void onError(Call call, Exception e, int id) {
								XLog.d("uploadFileToBack error: " + Log.getStackTraceString(e));
								try {
									if (reRun) {
										Thread.sleep(2000);
										uploadFileToBack(file, uploadBean, false);
									}
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
							
							@Override
							public void onResponse(String response, int id) {
								XLog.d("uploadFileToBack success: " + response);
								Map<String, Object> map = GsonUtils.GsonToMaps(response);
								if ((boolean) map.get("flag")) {
									String src = (String) map.get("src");
									if (uploadBean.messageBean.getType() == 7) {
										uploadBean.messageBean.setLinkImg(src);
									} else {
										uploadBean.messageBean.setContent(src);
									}
									sendToBack(uploadBean);
								} else {
									ToastUtils.showShort((String) Objects.requireNonNull(map.get("msg")));
								}
							}
						});
			}
		}, 500, TimeUnit.MILLISECONDS);
	}
	
	private static boolean verify(String talker, int type, String content) {
		return TextUtils.isEmpty(content) || (!TextUtils.isEmpty(talker) && TextUtils.equals(talker, "weixin")) || (type == 0 && (content.startsWith("[B@") || content.contains("~SEMI_XML~")));
	}
	
	/**
	 * 上传钱包信息
	 */
	public static void sendToBack(final EnWalletBean enWalletBean) {
		if (!isbinded()) return;
		ThreadPoolUtils.getInstance().a(new Runnable() {
			public void run() {
				XLog.d("sendWalletNotice url is " + Api.getWallet);
				String json = new GsonBuilder().disableHtmlEscaping().create().toJson(enWalletBean);
				XLog.d("sendWalletNotice json is " + json);
				OkHttpUtils.postString().url(Api.getWallet).content(json).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.d("sendWalletNotice error " + e.getMessage());
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("sendWalletNotice success " + response);
					}
				});
			}
		}, 500, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 上传发红包的结果
	 */
	public static void sendToBack(final BackMoneyResult backMoneyResult) {
		if (!isbinded()) return;
		final String json = new GsonBuilder().disableHtmlEscaping().create().toJson(backMoneyResult);
		XLog.d("backRedResult url is " + Api.sendMoneyResult);
		XLog.d("backRedResult json is " + json);
		ThreadPoolUtils.getInstance().a(new Runnable() {
			public void run() {
				OkHttpUtils.postString().url(Api.sendMoneyResult).content(json).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						XLog.d("sendWalletNotice error " + e.getMessage());
					}
					
					@Override
					public void onResponse(String response, int id) {
						XLog.d("sendWalletNotice success " + response);
					}
				});
			}
		}, 500, TimeUnit.MILLISECONDS);
	}
	
}
