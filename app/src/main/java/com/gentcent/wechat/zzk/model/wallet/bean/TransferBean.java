package com.gentcent.wechat.zzk.model.wallet.bean;

/**
 * 转账
 */
public class TransferBean {
	public MsgBean msg;
	
	public static class MsgBean {
		public AppmsgBean appmsg;
		
		public static class AppmsgBean {
			public String action;
			public String appid;
			public String content;
			public String des;
			public String extinfo;
			public String lowurl;
			public String sdkver;
			public String thumburl;
			public String title;
			public String type;
			public String url;
			public WcpayinfoBean wcpayinfo;
			
			public static class WcpayinfoBean {
				public String begintransfertime;
				public String effectivedate;
				public String feedesc;
				public String invalidtime;
				public String pay_memo;
				public String paymsgid;
				public String paysubtype;
				public String transcationid;
				public String transferid;
			}
		}
	}
}
