package com.gentcent.wechat.zzk.background;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.AppUtils.AppInfo;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.bean.PhoneInfoBean;
import com.gentcent.wechat.zzk.bean.UploadBean;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.util.ZzkUtil;

import java.io.File;

/**
 * @author zuozhi
 * @since 2019-08-03
 */
public class UploadService {
	/**
	 * 绑定设备
	 */
	public static void bindDevice(String acId) {
		PhoneInfoBean phoneInfoBean = new PhoneInfoBean();
		phoneInfoBean.IMEI = PhoneUtils.getIMEI();
		phoneInfoBean.acId = acId;
		
		String wxVersion;
		if (!AppUtils.isAppInstalled(HookParams.WECHAT_PACKAGE_NAME)) {
			wxVersion = "未安装微信";
		} else {
			AppInfo appInfo = AppUtils.getAppInfo(HookParams.WECHAT_PACKAGE_NAME);
			wxVersion = appInfo.getVersionName();
		}
		
		phoneInfoBean.wxVersion = wxVersion;
		phoneInfoBean.softwareVersion = AppUtils.getAppInfo(HookParams.MY_PACKAGE_NAME).getVersionName();
		phoneInfoBean.phonemodel = DeviceUtils.getModel();
		phoneInfoBean.phoneBrand = DeviceUtils.getManufacturer();
		phoneInfoBean.isroot = DeviceUtils.isDeviceRooted() ? 1 : 0;
		phoneInfoBean.isxPosed = isXposed() ? 1 : 0;
		phoneInfoBean.electric = "";
		XLog.d("当前的sys-info：" + MyHelper.readLine("sys-info", "未绑定设备信息"));
		UploadUtil.bindDevice(phoneInfoBean);
	}
	
	/**
	 * 绑定微信
	 */
	public static void bindWeixin(UserBean userBean) {
		XLog.d("上传自己的微信信息");
		UploadUtil.bindWeixin(userBean);
	}
	
	/**
	 * 同步好友
	 */
	public static void bindFriend(UserBean userBean) {
		UploadUtil.bindFriend(userBean);
	}
	
	/**
	 * 同步群聊
	 */
	public static void bindGroup(UserBean userBean) {
		UploadUtil.bindGroup(userBean);
	}
	
	/**
	 * 是否安装框架
	 */
	public static boolean isXposed() {
		return false;
	}
	
	/**
	 * 接收纯文本消息
	 */
	public static void receiveTextMessage(int status, int isSend, String talker, String content, int createTime, long msgId) {
		XLog.d("doText content " + content);
		if (!TextUtils.isEmpty(content)) {
			XLog.d("发送/接收 文字消息 ");
			UploadUtil.sendToBack("", status, String.valueOf(msgId), isSend, 0, talker, content, "", createTime, 0, "");
		}
	}
	
	/**
	 * 接收图片消息
	 */
	public static void receiveAnimationMessage(int status, int isSend, String talker, String content, int createTime, long msgId) {
		XLog.d("doAnimation content " + content);
		if (!TextUtils.isEmpty(content)) {
			XLog.d("发送/接收 表情包 ");
			String cdnurl = ZzkUtil.a(content, "cdnurl").replace("*#*", ":");
			
			UploadUtil.sendToBack("", status, String.valueOf(msgId), isSend, 1, talker, cdnurl, "", createTime, 0, "");
		}
	}
	
	/**
	 * 上传文件到后台
	 *
	 * @param file 上传的文件
	 */
	public static void uploadFileToBack(File file, UploadBean uploadBean) {
		UploadUtil.uploadFileToBack(file, uploadBean, true);
	}
	
	/**
	 * 微信消息类型和后台消息类型对应
	 *
	 * @param type 微信消息类型
	 *             1：纯文本消息√
	 *             3：图片√
	 *             34：语音√
	 *             42：名片
	 *             43：视频√
	 *             47：表情√
	 *             48：指定定位
	 *             49：文件、链接信息
	 *             50：视屏通话 content：voip_content_video
	 *             50：语音通话 content：voip_content_voice
	 *             436207665：红包
	 *             419430449：转账
	 *             -1879048186：共享实时位置
	 *             10000：提示字体
	 *             <p>
	 *             消息类型
	 *             0:文本√
	 *             1：图片（.gif和其他）√
	 *             2：语音√
	 *             3：视频√
	 *             7：链接√
	 *             8：文件
	 *             9：群聊
	 *             -1:UNKNOW
	 */
	public static int mappingType(int type) {
		if (type == 1) {
			return 0;
		} else if (type == 3) {
			return 1;
		} else if (type == 47) {
			return 1;
		} else if (type == 34) {
			return 2;
		} else if (type == 43) {
			return 3;
		} else if (type == 49) {
			return 7;
		}
		return -1;
	}
	
}
