package com.gentcent.wechat.zzk.background;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.AppUtils.AppInfo;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.bean.PhoneInfoBean;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.util.HookParams;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;

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
	 * 接受纯文本消息
	 */
	public static void receiveTextMessage(int status, int isSend, String talker, String content, int createTime) {
		XLog.d("doText content " + content);
		if (!TextUtils.isEmpty(content)) {
			XLog.d("发送/接收 文字消息 ");
			UploadUtil.sendToBack("", status, "", isSend, 0, talker, content,"", createTime,0,"");
		}
	}
	
	public static void receiveImgMessage(String path, String talker, Object o) {
	
	}
}
