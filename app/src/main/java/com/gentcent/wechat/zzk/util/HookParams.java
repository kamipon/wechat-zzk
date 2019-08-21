package com.gentcent.wechat.zzk.util;

public class HookParams {
	public static final String SAVE_WECHAT_ENHANCEMENT_CONFIG = "wechat.intent.action.SAVE_WECHAT_ENHANCEMENT_CONFIG";
	public static final String WECHAT_ENHANCEMENT_CONFIG_NAME = "wechat_enhancement_config";
	public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
	public static final String MY_PACKAGE_NAME = "com.gentcent.wechat.zzk";
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
	
	
	public static String method_a = "a";
	public static String method_b = "b";
	public static String method_c = "c";
	//微信内核
	public static String kernel_g = "com.tencent.mm.kernel.g";
	public static String kernel_g_method1 = "N";
	
	//消息
	public static String modelmulti_h = "com.tencent.mm.modelmulti.h";
	//图片
	public static String send_img_class1 = "com.tencent.mm.av.n";
	public static String send_img_class1_method = "alL";
	public static String send_img_class2_method1 = method_a;
	public static String send_img_class2_method2 = method_a;
	public static String send_img_class2_ConcurrentHashMap = "gaG";
	public static String send_img_ConcurrentHashMap_attribute1 = "gaU";
	public static String send_img_ConcurrentHashMap_attribute2 = "fYs";
	public static String send_img_ConcurrentHashMap_attribute3 = "cIQ";
	public static String send_img_ConcurrentHashMap_attribute4 = "cRP";
	public static String send_img_class3 = "com.tencent.mm.pointers.PString";
	public static String send_img_class4 = "com.tencent.mm.pointers.PInt";
	public static String send_img_class5 = "com.tencent.mm.pointers.PInt";
	public static String send_img_ConcurrentHashMap_attribute5 = "gaV";
	public static String send_img_ConcurrentHashMap_attribute6 = "gaW";
	public static String send_img_ConcurrentHashMap_attribute7 = "cJn";
	public static String send_img_ConcurrentHashMap_attribute8 = "gbb";
	public static String send_img_ConcurrentHashMap_attribute9 = "gbc";
	public static String send_img_ConcurrentHashMap_attribute10 = "gbd";
	public static String send_img_class6 = "com.tencent.mm.av.o";
	public static String send_img_class6_method = "alV";
	public static String send_img_class6_return_method = method_a;
	public static String send_img_class7 = "com.tencent.mm.av.i";
	public static String send_img_class7_method = method_a;
	
	//语音
	public static String send_voice_class1 = "com.tencent.mm.modelvoice.f";
	public static String send_voice_class2 = "com.tencent.mm.modelvoice.s";
	public static String send_voice_class2_method1 = "wD";
	public static String send_voice_class2_method2 = "V";
	public static String send_voice_class2_method3 = "w";
	public static String send_voice_class3 = "com.tencent.mm.ak.p";
	public static String send_voice_class3_attribute = "fOd";
	public static String send_voice_class3_method = "b";
	//视频
	public static String model_j = "com.tencent.mm.pluginsdk.model.j";
	public static String model_j_a = "com.tencent.mm.pluginsdk.model.j$a";
	//链接
	public static String a_pr = "com.tencent.mm.g.a.pr";
	public static String a_pr_field = "cAT";
	public static String a_pr_field_attribute1 = "appId";
	public static String a_pr_field_attribute2 = "appName";
	public static String a_pr_field_attribute3 = "cAU";
	public static String a_pr_field_attribute4 = "cAY";
	public static String a_pr_field_attribute5 = "cAZ";
	public static String a_pr_field_attribute6 = "toUser";
	public static String WXMediaMessage = "com.tencent.mm.opensdk.modelmsg.WXMediaMessage";
	public static String WXMediaMessage_attribute1 = "description";
	public static String WXMediaMessage_attribute2 = "mediaTagName";
	public static String WXMediaMessage_attribute3 = "messageAction";
	public static String WXMediaMessage_attribute4 = "messageExt";
	public static String WXMediaMessage_attribute5 = "sdkVer";
	public static String WXMediaMessage_attribute6 = "title";
	public static String WXMediaMessage_method1 = "setThumbImage";
	public static String WXMediaMessage_method1_return_filed1 = "mediaObject";
	public static String WXMediaMessage_method1_return_filed2 = "ctb";
	public static String sdk_b_a = "com.tencent.mm.sdk.b.a";
	public static String sdk_b_a_filed = "wKm";
	public static String WXWebpageObject = "com.tencent.mm.opensdk.modelmsg.WXWebpageObject";
	//文件
	public static String WXFileObject = "com.tencent.mm.opensdk.modelmsg.WXFileObject";
	public static String WXFileObject_method1 = "setFilePath";
	public static String platformtools_bp = "com.tencent.mm.sdk.platformtools.bp";
	public static String platformtools_bp_method = "fx";
	public static String app_l = "com.tencent.mm.pluginsdk.model.app.l";
	//@好友
	public static String model_av = "com.tencent.mm.model.aw";
	public static String model_av_method1 = "Pw";
	//发送GIF
	public static String WXEmojiObject = "com.tencent.mm.opensdk.modelmsg.WXEmojiObject";
	public static String emoji_b_d = "com.tencent.mm.plugin.emoji.b.d";
	public static String emoji_b_d_method1 = "getEmojiMgr";
	public static String emoji_b_d_method1_return_method = "HO";
	public static String model_av_method2 = "XE";
	public static String model_c = "com.tencent.mm.model.c";
	public static String model_c_method = "VX";
	public static String emoji_b_d_method1_return_method_return_method = "ze";
	public static String emoji_b_d_method1_return_method_return_method_return_file = "field_app_id";
	public static String vfs_e = "com.tencent.mm.vfs.e";
	public static String vfs_e_method1 = "cl";
	public static String vfs_e_method2 = "e";
	public static String vfs_e_method3 = "ars";
	public static String vfs_e_method4 = "openRead";
	public static String WXMediaMessage_file = "thumbData";
	public static String BackwardSupportUtil_b = "com.tencent.mm.sdk.platformtools.BackwardSupportUtil$b";
	
	//好友
	public static String sayHiWithSnsPermissionUI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
	public static String sayHiWithSnsPermissionUI_6 = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI$6";
	public static String sayHiWithSnsPermissionUI_field = "oLC";
	public static String ContactInfoUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
	public static String ContactInfoUI_file = "oHt";
	public static String ContactInfoUI_method = "EF";
	public static String ContactInfoUI_method_param = "contact_profile_add_contact";
	public static String FTSMainUI = "com.tencent.mm.plugin.fts.ui.FTSMainUI";
	public static String FTSMainUI2 = "com.tencent.mm.ui.widget.a.c";
	public static String FTSMainUI3 = "com.tencent.mm.protocal.protobuf.bsk";
	public static String FTSMainUI4 = "com.tencent.mm.model.j";
	
	//朋友圈
	public static String SnsUploadUI = "com.tencent.mm.plugin.sns.ui.SnsUploadUI";
	public static String VideoCompressUI = "com.tencent.mm.plugin.mmsight.segment.VideoCompressUI";
	public static String SnsTimeLineUI = "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI";
	
	
	//接受图片消息
	public static String receive_imgFile_class1 = "com.tencent.mm.av.o";
	public static String receive_imgFile_class1_method = "alV";
	public static String receive_imgFile_class2 = "com.tencent.mm.a.g";
	public static String receive_imgFile_class2_method = "C";
	public static String receive_imgFile_method = "q";
	public static String receive_fileKey_class = "com.tencent.mm.an.c";
	public static String receive_fileKey_class_method = method_a;
	public static String receive_imgDownload_parameter = "com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest";
	public static String receive_imgDownload_class = "com.tencent.mars.cdn.CdnLogic";
	public static String receive_imgDownload_class_method = "startC2CDownload";
	
	static {
		init706();
		initvoice706();
	}
	
	public static void init706() {
		send_img_class1 = "com.tencent.mm.av.n";
		send_img_class1_method = "alL";
		send_img_class2_method1 = method_a;
		send_img_class2_method2 = method_a;
		send_img_class2_ConcurrentHashMap = "gaG";
		send_img_ConcurrentHashMap_attribute1 = "gaU";
		send_img_ConcurrentHashMap_attribute2 = "fYs";
		send_img_ConcurrentHashMap_attribute3 = "cIQ";
		send_img_ConcurrentHashMap_attribute4 = "cRP";
		send_img_class3 = "com.tencent.mm.pointers.PString";
		send_img_class4 = "com.tencent.mm.pointers.PInt";
		send_img_class5 = "com.tencent.mm.pointers.PInt";
		send_img_ConcurrentHashMap_attribute5 = "gaV";
		send_img_ConcurrentHashMap_attribute6 = "gaW";
		send_img_ConcurrentHashMap_attribute7 = "cJn";
		send_img_ConcurrentHashMap_attribute8 = "gbb";
		send_img_ConcurrentHashMap_attribute9 = "gbc";
		send_img_ConcurrentHashMap_attribute10 = "gbd";
		send_img_class6 = "com.tencent.mm.av.o";
		send_img_class6_method = "alV";
		send_img_class6_return_method = method_a;
		send_img_class7 = "com.tencent.mm.av.i";
		send_img_class7_method = method_a;
	}
	
	public static void initvoice706() {
		send_voice_class1 = "com.tencent.mm.modelvoice.f";
		send_voice_class2 = "com.tencent.mm.modelvoice.s";
		send_voice_class2_method1 = "wD";
		send_voice_class2_method2 = "V";
		send_voice_class2_method3 = "w";
		send_voice_class3 = "com.tencent.mm.ak.p";
		send_voice_class3_attribute = "fOd";
		send_voice_class3_method = "b";
	}
	
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
