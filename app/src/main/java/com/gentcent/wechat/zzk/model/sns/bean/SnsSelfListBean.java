package com.gentcent.wechat.zzk.model.sns.bean;

import java.util.List;

public class SnsSelfListBean {
	private String Imei;
	private List<SnsContentItemBean> MySnsList;
	private String MyWxid;
	
	public SnsSelfListBean(String imei, String myWxid, List<SnsContentItemBean> mySnsList) {
		Imei = imei;
		MySnsList = mySnsList;
		MyWxid = myWxid;
	}
	
	@Override
	public String toString() {
		return "SnsSelfListBean{" +
				"Imei='" + Imei + '\'' +
				", MySnsList=" + MySnsList +
				", MyWxid='" + MyWxid + '\'' +
				'}';
	}
	
	public String getImei() {
		return Imei;
	}
	
	public void setImei(String imei) {
		Imei = imei;
	}
	
	public List<SnsContentItemBean> getMySnsList() {
		return MySnsList;
	}
	
	public void setMySnsList(List<SnsContentItemBean> mySnsList) {
		MySnsList = mySnsList;
	}
	
	public String getMyWxid() {
		return MyWxid;
	}
	
	public void setMyWxid(String myWxid) {
		MyWxid = myWxid;
	}
}
