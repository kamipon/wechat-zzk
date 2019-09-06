package com.gentcent.wechat.zzk.model.wallet.bean;

public class RedPageBean {
    public MsgBean msg;

    public static class MsgBean {
        public AppmsgBean appmsg;
        public String fromusername;

        public static class AppmsgBean {
            public String appid;
            public String des;
            public String sdkver;
            public String thumburl;
            public String title;
            public String type;
            public String url;
            public WcpayinfoBean wcpayinfo;

            public static class WcpayinfoBean {
                private BroadenBean broaden;
                private String corpname;
                private String detailshowsourcemd5;
                private String detailshowsourceurl;
                private String iconurl;
                private String imageaeskey;
                private String imageid;
                private String imagelength;
                private String innertype;
                private String invalidtime;
                private String locallogoicon;
                public String nativeurl;
                private String paymsgid;
                private String receiverc2cshowsourcemd5;
                private String receiverc2cshowsourceurl;
                private String receiverdes;
                private String receivertitle;
                private String recshowsourcemd5;
                private String recshowsourceurl;
                private String sceneid;
                private String scenetext;
                private String senderc2cshowsourcemd5;
                private String senderc2cshowsourceurl;
                private String senderdes;
                public String sendertitle;
                private String subtype;
                private String templateid;
                private String url;

                public static class BroadenBean {
                    private String androidversion;
                    private String iosversion;
                    private String nativeurl;
                    private String typeid;
                    private String url;

                    public String getTypeid() {
                        return this.typeid;
                    }

                    public void setTypeid(String str) {
                        this.typeid = str;
                    }

                    public String getIosversion() {
                        return this.iosversion;
                    }

                    public void setIosversion(String str) {
                        this.iosversion = str;
                    }

                    public String getAndroidversion() {
                        return this.androidversion;
                    }

                    public void setAndroidversion(String str) {
                        this.androidversion = str;
                    }

                    public String getNativeurl() {
                        return this.nativeurl;
                    }

                    public void setNativeurl(String str) {
                        this.nativeurl = str;
                    }

                    public String getUrl() {
                        return this.url;
                    }

                    public void setUrl(String str) {
                        this.url = str;
                    }
                }

                public BroadenBean getBroaden() {
                    return this.broaden;
                }

                public void setBroaden(BroadenBean broadenBean) {
                    this.broaden = broadenBean;
                }

                public String getSubtype() {
                    return this.subtype;
                }

                public void setSubtype(String str) {
                    this.subtype = str;
                }

                public String getRecshowsourcemd5() {
                    return this.recshowsourcemd5;
                }

                public void setRecshowsourcemd5(String str) {
                    this.recshowsourcemd5 = str;
                }

                public String getReceiverc2cshowsourceurl() {
                    return this.receiverc2cshowsourceurl;
                }

                public void setReceiverc2cshowsourceurl(String str) {
                    this.receiverc2cshowsourceurl = str;
                }

                public String getInvalidtime() {
                    return this.invalidtime;
                }

                public void setInvalidtime(String str) {
                    this.invalidtime = str;
                }

                public String getNativeurl() {
                    return this.nativeurl;
                }

                public void setNativeurl(String str) {
                    this.nativeurl = str;
                }

                public String getUrl() {
                    return this.url;
                }

                public void setUrl(String str) {
                    this.url = str;
                }

                public String getImageid() {
                    return this.imageid;
                }

                public void setImageid(String str) {
                    this.imageid = str;
                }

                public String getPaymsgid() {
                    return this.paymsgid;
                }

                public void setPaymsgid(String str) {
                    this.paymsgid = str;
                }

                public String getImagelength() {
                    return this.imagelength;
                }

                public void setImagelength(String str) {
                    this.imagelength = str;
                }

                public String getLocallogoicon() {
                    return this.locallogoicon;
                }

                public void setLocallogoicon(String str) {
                    this.locallogoicon = str;
                }

                public String getReceiverdes() {
                    return this.receiverdes;
                }

                public void setReceiverdes(String str) {
                    this.receiverdes = str;
                }

                public String getIconurl() {
                    return this.iconurl;
                }

                public void setIconurl(String str) {
                    this.iconurl = str;
                }

                public String getSenderc2cshowsourceurl() {
                    return this.senderc2cshowsourceurl;
                }

                public void setSenderc2cshowsourceurl(String str) {
                    this.senderc2cshowsourceurl = str;
                }

                public String getRecshowsourceurl() {
                    return this.recshowsourceurl;
                }

                public void setRecshowsourceurl(String str) {
                    this.recshowsourceurl = str;
                }

                public String getReceiverc2cshowsourcemd5() {
                    return this.receiverc2cshowsourcemd5;
                }

                public void setReceiverc2cshowsourcemd5(String str) {
                    this.receiverc2cshowsourcemd5 = str;
                }

                public String getSceneid() {
                    return this.sceneid;
                }

                public void setSceneid(String str) {
                    this.sceneid = str;
                }

                public String getSendertitle() {
                    return this.sendertitle;
                }

                public void setSendertitle(String str) {
                    this.sendertitle = str;
                }

                public String getDetailshowsourceurl() {
                    return this.detailshowsourceurl;
                }

                public void setDetailshowsourceurl(String str) {
                    this.detailshowsourceurl = str;
                }

                public String getSenderc2cshowsourcemd5() {
                    return this.senderc2cshowsourcemd5;
                }

                public void setSenderc2cshowsourcemd5(String str) {
                    this.senderc2cshowsourcemd5 = str;
                }

                public String getImageaeskey() {
                    return this.imageaeskey;
                }

                public void setImageaeskey(String str) {
                    this.imageaeskey = str;
                }

                public String getInnertype() {
                    return this.innertype;
                }

                public void setInnertype(String str) {
                    this.innertype = str;
                }

                public String getSenderdes() {
                    return this.senderdes;
                }

                public void setSenderdes(String str) {
                    this.senderdes = str;
                }

                public String getReceivertitle() {
                    return this.receivertitle;
                }

                public void setReceivertitle(String str) {
                    this.receivertitle = str;
                }

                public String getDetailshowsourcemd5() {
                    return this.detailshowsourcemd5;
                }

                public void setDetailshowsourcemd5(String str) {
                    this.detailshowsourcemd5 = str;
                }

                public String getTemplateid() {
                    return this.templateid;
                }

                public void setTemplateid(String str) {
                    this.templateid = str;
                }

                public String getScenetext() {
                    return this.scenetext;
                }

                public void setScenetext(String str) {
                    this.scenetext = str;
                }

                public String getCorpname() {
                    return this.corpname;
                }

                public void setCorpname(String str) {
                    this.corpname = str;
                }
            }
        }
    }
}
