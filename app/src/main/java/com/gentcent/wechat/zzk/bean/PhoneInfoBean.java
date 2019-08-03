package com.gentcent.wechat.zzk.bean;

/**
 * @author zuozhi
 * @since 2019-08-03
 */
public class PhoneInfoBean {
	//IMEI号
	public String IMEI;
	//商家ID
	public String acId;
	//微信版本
	public String wxVersion;
	//软件版本
	public String softwareVersion;
	//手机型号
	public String phonemodel;
	//手机品牌
	public String phoneBrand;
	//设备保管人
	public String keepMan;
	//当前电量
	public String electric;
	//是否root
	public Integer isroot;
	//是否安装框架
	public Integer isxPosed;
	
	public PhoneInfoBean() {
	}
	
	public PhoneInfoBean(String IMEI, String acId, String wxVersion, String softwareVersion, String phonemodel, String phoneBrand, String keepMan, String electric, Integer isroot, Integer isxPosed) {
		this.IMEI = IMEI;
		this.acId = acId;
		this.wxVersion = wxVersion;
		this.softwareVersion = softwareVersion;
		this.phonemodel = phonemodel;
		this.phoneBrand = phoneBrand;
		this.keepMan = keepMan;
		this.electric = electric;
		this.isroot = isroot;
		this.isxPosed = isxPosed;
	}
}
