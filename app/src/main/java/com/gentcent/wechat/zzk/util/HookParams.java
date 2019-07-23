package com.gentcent.wechat.zzk.util;

public class HookParams {
    public static final String SAVE_WECHAT_ENHANCEMENT_CONFIG = "wechat.intent.action.SAVE_WECHAT_ENHANCEMENT_CONFIG";
    public static final String WECHAT_ENHANCEMENT_CONFIG_NAME = "wechat_enhancement_config";
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    public static int SEND_TIME_INTERVAL = 1000; //发送消息的时间间隔
    public static final int VERSION_CODE = 46; //大版本变动时候才需要修改

    public String SQLiteDatabaseClassName = "com.tencent.wcdb.database.SQLiteDatabase";
    public String SQLiteDatabaseUpdateMethod = "updateWithOnConflict";
    public String SQLiteDatabaseInsertWithOnConflictMethod = "insertWithOnConflict";
    public String SQLiteDatabaseInsertMethod = "insert";
    public String SQLiteDatabaseDeleteMethod = "delete";
    public String ContactInfoUIClassName = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";

    public String ContactInfoClassName;
    public String ChatroomInfoUIClassName = "com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI";
    public String WebWXLoginUIClassName = "com.tencent.mm.plugin.webwx.ui.ExtDeviceWXLoginUI";
    public String AlbumPreviewUIClassName = "com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI";
    public String SelectContactUIClassName = "com.tencent.mm.ui.contact.SelectContactUI";
    public String MMActivityClassName = "com.tencent.mm.ui.MMActivity";
    public String SelectConversationUIClassName = "com.tencent.mm.ui.transmit.SelectConversationUI";
    public String SelectConversationUICheckLimitMethod;
    public String LuckyMoneyReceiveUIClassName = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    public String XMLParserClassName;
    public String XMLParserMethod;
    public String MsgInfoClassName;
    public String MsgInfoStorageClassName;
    public String MsgInfoStorageInsertMethod;
    public String ReceiveUIParamNameClassName;
    public String ReceiveUIMethod;
    public String NetworkRequestClassName;
    public String RequestCallerClassName;
    public String RequestCallerMethod;
    public String GetNetworkByModelMethod;
    public String ReceiveLuckyMoneyRequestClassName;
    public String ReceiveLuckyMoneyRequestMethod;
    public String LuckyMoneyRequestClassName;
    public String GetTransferRequestClassName;
    public boolean hasTimingIdentifier = true;
    public String versionName;
    public int versionCode;
    
    //消息
    public static String sendMessageClassName = "com.tencent.mm.ah.p";
    public static String sendMessageStaticObject = "fej";
    public static String sendMessageParamClassName = "com.tencent.mm.modelmulti.h";
    public static String sendMessageMethodName = "d";
    
    //好友
    public static String sayHiWithSnsPermissionUI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
    public static String sayHiWithSnsPermissionUIInnerClass6 = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI$6";
    public static String sayHiWithSnsPermissionUIFieldName = "oLC";
    public static String ContactInfoUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    public static String ContactInfoUIFieldName = "oHt";
    public static String ContactInfoUIMethodName = "EF";
    public static String ContactInfoUIMethodArgs = "contact_profile_add_contact";
    public static String FTSMainUI = "com.tencent.mm.plugin.fts.ui.FTSMainUI";
    public static String FTSMainUI2 = "com.tencent.mm.ui.widget.a.c";
    public static String FTSMainUI3 = "com.tencent.mm.protocal.protobuf.bsk";
    public static String FTSMainUI4 = "com.tencent.mm.model.j";
    
    
    //朋友圈

    private static HookParams instance = null;

    private HookParams() {
    }

    public static HookParams getInstance() {
        if (instance == null)
            instance = new HookParams();
        return instance;
    }

    public static void setInstance(HookParams i) {
        instance = i;
    }

    public static boolean hasInstance() {
        return instance != null;
    }

}
