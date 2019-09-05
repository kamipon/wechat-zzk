package com.gentcent.wechat.zzk.model.friend.bean;

import android.content.Intent;
import android.util.Log;

import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

public class WxInfo {
	public String key_add_contact_custom_detail;
	public String key_add_contact_desc_wording_id;
	public String key_add_contact_desc_icon;
	public int type;
	public String Contact_User;
	public String Contact_Nick;
	public String Contact_PyInitial;
	public String Contact_QuanPin;
	public String Contact_Alias;
	public int Contact_Sex;
	public String Contact_VUser_Info;
	public int Contact_VUser_Info_Flag;
	public int Contact_KWeibo_flag;
	public String Contact_KWeibo;
	public String Contact_KWeiboNick;
	public int Contact_Scene;
	public boolean Contact_KHideExpose;
	public String Contact_RegionCode;
	public String Contact_Signature;
	public String Contact_BrandList;
	public int Contact_KSnsIFlag;
	public long Contact_KSnsBgId;
	public String Contact_KSnsBgUrl;
	public String Contact_BIZ_KF_WORKER_ID;
	public String Contact_BIZ_PopupInfoMsg;
	public String imgurl;
	public String bigimgurl;
	public String key_add_contact_openim_appid;
	public int key_add_contact_match_type;
	public int key_add_contact_custom_detail_visible;
	
	@Override
	public String toString() {
		return "WxInfo{" +
				"key_add_contact_custom_detail='" + key_add_contact_custom_detail + '\'' +
				", key_add_contact_desc_wording_id='" + key_add_contact_desc_wording_id + '\'' +
				", key_add_contact_desc_icon='" + key_add_contact_desc_icon + '\'' +
				", type=" + type +
				", Contact_User='" + Contact_User + '\'' +
				", Contact_Nick='" + Contact_Nick + '\'' +
				", Contact_PyInitial='" + Contact_PyInitial + '\'' +
				", Contact_QuanPin='" + Contact_QuanPin + '\'' +
				", Contact_Alias='" + Contact_Alias + '\'' +
				", Contact_Sex=" + Contact_Sex +
				", Contact_VUser_Info='" + Contact_VUser_Info + '\'' +
				", Contact_VUser_Info_Flag=" + Contact_VUser_Info_Flag +
				", Contact_KWeibo_flag=" + Contact_KWeibo_flag +
				", Contact_KWeibo='" + Contact_KWeibo + '\'' +
				", Contact_KWeiboNick='" + Contact_KWeiboNick + '\'' +
				", Contact_Scene=" + Contact_Scene +
				", Contact_KHideExpose=" + Contact_KHideExpose +
				", Contact_RegionCode='" + Contact_RegionCode + '\'' +
				", Contact_Signature='" + Contact_Signature + '\'' +
				", Contact_BrandList='" + Contact_BrandList + '\'' +
				", Contact_KSnsIFlag=" + Contact_KSnsIFlag +
				", Contact_KSnsBgId=" + Contact_KSnsBgId +
				", Contact_KSnsBgUrl='" + Contact_KSnsBgUrl + '\'' +
				", Contact_BIZ_KF_WORKER_ID='" + Contact_BIZ_KF_WORKER_ID + '\'' +
				", Contact_BIZ_PopupInfoMsg='" + Contact_BIZ_PopupInfoMsg + '\'' +
				", imgurl='" + imgurl + '\'' +
				", bigimgurl='" + bigimgurl + '\'' +
				", key_add_contact_openim_appid='" + key_add_contact_openim_appid + '\'' +
				", key_add_contact_match_type=" + key_add_contact_match_type +
				", key_add_contact_custom_detail_visible=" + key_add_contact_custom_detail_visible +
				'}';
	}
	
	public static WxInfo a(XC_LoadPackage.LoadPackageParam loadPackageParam, Object obj) {
		WxInfo wxInfo = new WxInfo();
		Intent intent = new Intent();
		try {
			wxInfo.imgurl = (String) XposedHelpers.getObjectField(obj, "xsO");
			wxInfo.bigimgurl = (String) XposedHelpers.getObjectField(obj, "xsN");
			int intField = XposedHelpers.getIntField(obj, "ytF");
			if (2 == intField) {
				wxInfo.type = 15;
			} else if (1 == intField) {
				wxInfo.type = 1;
			} else {
				wxInfo.type = 3;
			}
			XposedHelpers.callMethod(XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.kernel.g"), "ab", loadPackageParam.classLoader.loadClass("com.tencent.mm.api.j")), "a", intent, obj, wxInfo.type);
			wxInfo.Contact_User = intent.getStringExtra("Contact_User");
			wxInfo.Contact_Nick = intent.getStringExtra("Contact_Nick");
			wxInfo.Contact_PyInitial = intent.getStringExtra("Contact_PyInitial");
			wxInfo.Contact_QuanPin = intent.getStringExtra("Contact_QuanPin");
			wxInfo.Contact_Alias = intent.getStringExtra("Contact_Alias");
			wxInfo.Contact_Sex = intent.getIntExtra("Contact_Sex", 0);
			wxInfo.Contact_VUser_Info = intent.getStringExtra("Contact_VUser_Info");
			wxInfo.Contact_VUser_Info_Flag = intent.getIntExtra("Contact_VUser_Info_Flag", 0);
			wxInfo.Contact_KWeibo_flag = intent.getIntExtra("Contact_KWeibo_flag", 0);
			wxInfo.Contact_KWeibo = intent.getStringExtra("Contact_KWeibo");
			wxInfo.Contact_KWeiboNick = intent.getStringExtra("Contact_KWeiboNick");
			wxInfo.Contact_Scene = intent.getIntExtra("Contact_Scene", 0);
			wxInfo.Contact_KHideExpose = intent.getBooleanExtra("Contact_KHideExpose", false);
			wxInfo.Contact_RegionCode = intent.getStringExtra("Contact_RegionCode");
			wxInfo.Contact_Signature = intent.getStringExtra("Contact_Signature");
			wxInfo.Contact_BrandList = intent.getStringExtra("Contact_BrandList");
			wxInfo.Contact_KSnsIFlag = intent.getIntExtra("Contact_KSnsIFlag", 0);
			wxInfo.Contact_KSnsBgId = intent.getLongExtra("Contact_KSnsBgId", 0);
			wxInfo.Contact_KSnsBgUrl = intent.getStringExtra("Contact_KSnsBgUrl");
			wxInfo.Contact_BIZ_KF_WORKER_ID = intent.getStringExtra("Contact_BIZ_KF_WORKER_ID");
			wxInfo.Contact_BIZ_PopupInfoMsg = intent.getStringExtra("Contact_BIZ_PopupInfoMsg");
			wxInfo.key_add_contact_openim_appid = intent.getStringExtra("key_add_contact_openim_appid");
			wxInfo.key_add_contact_match_type = intent.getIntExtra("key_add_contact_match_type", 0);
			wxInfo.key_add_contact_custom_detail_visible = intent.getIntExtra("key_add_contact_custom_detail_visible", 0);
			wxInfo.key_add_contact_custom_detail = intent.getStringExtra("key_add_contact_custom_detail");
			wxInfo.key_add_contact_desc_wording_id = intent.getStringExtra("key_add_contact_desc_wording_id");
			wxInfo.key_add_contact_desc_icon = intent.getStringExtra("key_add_contact_desc_icon");
		} catch (Exception e) {
			XLog.e("error:" + Log.getStackTraceString(e));
		}
		return wxInfo;
	}
}
