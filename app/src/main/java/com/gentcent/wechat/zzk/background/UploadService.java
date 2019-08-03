package com.gentcent.wechat.zzk.background;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.AppUtils.AppInfo;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.gentcent.wechat.zzk.bean.PhoneInfoBean;
import com.gentcent.wechat.zzk.util.HookParams;

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
		UploadUtil.bindDevice(phoneInfoBean);
	}
	
	public static boolean isXposed() {
		return false;
	}
}