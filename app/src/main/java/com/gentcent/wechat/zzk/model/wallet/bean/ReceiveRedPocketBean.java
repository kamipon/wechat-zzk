package com.gentcent.wechat.zzk.model.wallet.bean;

import java.util.List;

public class ReceiveRedPocketBean {
    public MsgBean msg;

    public static class MsgBean {
        public AppmsgBean appmsg;
        private String content;
        public String fromusername;

        public static class AppmsgBean {
            private String appid;
            public String content;
            private String des;
            private String sdkver;
            private String thumburl;
            public String title;
            public String type;
            private String url;
            public WcpayinfoBean wcpayinfo;

            public static class WcpayinfoBean {
                private String broaden;
                public String content;
                private String iconurl;
                private String innertype;
                public String invalidtime;
                private String locallogoicon;
                public String nativeurl;
                private String paymsgid;
                public String receiverdes;
                public String receivertitle;
                private String sceneid;
                private List<ScenetextBean> scenetext;
                private String senderdes;
                private String sendertitle;
                private String templateid;
                private String url;

                public static class ScenetextBean {
                    private String content;

                    public String getContent() {
                        return this.content;
                    }

                    public void setContent(String str) {
                        this.content = str;
                    }
                }

                public String getContent() {
                    return this.content;
                }

                public void setContent(String str) {
                    this.content = str;
                }

                public String getBroaden() {
                    return this.broaden;
                }

                public void setBroaden(String str) {
                    this.broaden = str;
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

                public String getSendertitle() {
                    return this.sendertitle;
                }

                public void setSendertitle(String str) {
                    this.sendertitle = str;
                }

                public String getUrl() {
                    return this.url;
                }

                public void setUrl(String str) {
                    this.url = str;
                }

                public String getPaymsgid() {
                    return this.paymsgid;
                }

                public void setPaymsgid(String str) {
                    this.paymsgid = str;
                }

                public String getLocallogoicon() {
                    return this.locallogoicon;
                }

                public void setLocallogoicon(String str) {
                    this.locallogoicon = str;
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

                public String getTemplateid() {
                    return this.templateid;
                }

                public void setTemplateid(String str) {
                    this.templateid = str;
                }

                public String getSceneid() {
                    return this.sceneid;
                }

                public void setSceneid(String str) {
                    this.sceneid = str;
                }

                public List<ScenetextBean> getScenetext() {
                    return this.scenetext;
                }

                public void setScenetext(List<ScenetextBean> list) {
                    this.scenetext = list;
                }
            }

            public String getContent() {
                return this.content;
            }

            public void setContent(String str) {
                this.content = str;
            }

            public WcpayinfoBean getWcpayinfo() {
                return this.wcpayinfo;
            }

            public void setWcpayinfo(WcpayinfoBean wcpayinfoBean) {
                this.wcpayinfo = wcpayinfoBean;
            }

            public String getTitle() {
                return this.title;
            }

            public void setTitle(String str) {
                this.title = str;
            }

            public String getUrl() {
                return this.url;
            }

            public void setUrl(String str) {
                this.url = str;
            }

            public String getThumburl() {
                return this.thumburl;
            }

            public void setThumburl(String str) {
                this.thumburl = str;
            }

            public String getAppid() {
                return this.appid;
            }

            public void setAppid(String str) {
                this.appid = str;
            }

            public String getSdkver() {
                return this.sdkver;
            }

            public void setSdkver(String str) {
                this.sdkver = str;
            }

            public String getDes() {
                return this.des;
            }

            public void setDes(String str) {
                this.des = str;
            }

            public String getType() {
                return this.type;
            }

            public void setType(String str) {
                this.type = str;
            }
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String str) {
            this.content = str;
        }

        public String getFromusername() {
            return this.fromusername;
        }

        public void setFromusername(String str) {
            this.fromusername = str;
        }

        public AppmsgBean getAppmsg() {
            return this.appmsg;
        }

        public void setAppmsg(AppmsgBean appmsgBean) {
            this.appmsg = appmsgBean;
        }
    }

    public MsgBean getMsg() {
        return this.msg;
    }

    public void setMsg(MsgBean msgBean) {
        this.msg = msgBean;
    }
}
