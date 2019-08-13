package com.gentcent.wechat.zzk.wcdb;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.background.UploadService;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.model.syncinfo.SyncInfoManager;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class UserDao {
	private static final String TAG = "UserDao:  ";
	
	/**
	 * 获取自己的微信昵称
	 */
	public static String getMyName() {
		Cursor c1 = WcdbHolder.excute("select value from userinfo where id='4'");
		String myName = "";
		while (c1.moveToNext()) {
			myName = c1.getString(c1.getColumnIndex("value"));
		}
		c1.close();
		return myName;
	}
	
	/**
	 * 获取自己的微信ID
	 */
	public static String getMyWxid() {
		String myWxid = "";
		String result = "";
		Cursor c1 = WcdbHolder.excute("select value from userinfo where id='2'");
		if (ObjectUtils.isNotEmpty((Object) c1)) {
			while (c1.moveToNext()) {
				myWxid = c1.getString(c1.getColumnIndex("value"));
			}
		} else {
			XLog.d("getSelfWxid user_ID is empty");
		}
		if (c1 != null) {
			c1.close();
		}
		if (ObjectUtils.isNotEmpty(myWxid)) {
			MyHelper.writeLine("myWechatID", myWxid);
			result = myWxid;
		}
		if (TextUtils.isEmpty(result)) {
			result = MyHelper.readLine("myWechatID", "");
		}
		return result;
	}
	
	/**
	 * 获取自己微信ID（通过微信内部调用）
	 */
	public static String getMyWxidByWXMethod(XC_LoadPackage.LoadPackageParam lpparam) {
		String wxid = "";
		try {
			wxid = (String) XposedHelpers.callStaticMethod(lpparam.classLoader.loadClass("com.tencent.mm.model.q"), "Wt");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return wxid;
	}
	
	/**
	 * 根据wxid获取昵称(有备注则获取备注)
	 *
	 * @param wxid 微信ID
	 * @return 昵称/备注
	 */
	public static String getNickByWxid(String wxid) {
		if (TextUtils.equals(wxid, getMyWxid())) {
			return getMyName();
		}
		String nickName = "";
		String conRemark = "";
		String sql = "select nickname,conRemark from rcontact where username='" + wxid + "'";
		Cursor c1 = WcdbHolder.excute(sql);
		while (c1.moveToNext()) {
			nickName = c1.getString(c1.getColumnIndex("nickname"));
			conRemark = c1.getString(c1.getColumnIndex("conRemark"));
		}
		if (!TextUtils.isEmpty(conRemark)) {
			nickName = conRemark;
		}
		return nickName;
	}
	
	/**
	 * 根据微信ID获取用户信息
	 */
	public static UserBean getUserBeanByWxId(String wxId) {
		Cursor c1 = null;
		UserBean userBean = new UserBean();
		try {
			//查询所有联系人（verifyFlag!=0:公众号等类型，群里面非好友的类型为4，未知类型2）
			String findFriendheadandID = "SELECT * FROM(SELECT r.username as username, r.alias as alias , r.nickname as nickname ,  r.conRemark as conRemark ,  r.pyInitial as pyInitial ,  r.quanPin as quanPin, i.reserved1 as reserved1,  i.reserved2 as reserved2 ,r.showHead as showHead,CASE  WHEN length(r.conRemarkPYFull) > 0  THEN UPPER(r.conRemarkPYFull) ELSE UPPER(r.quanPin) END as PY, CASE   WHEN length(r.conRemark) > 0  THEN UPPER(r.conRemark) ELSE UPPER(r.quanPin) end as byremark,c.memberlist as memberlist,  c.displayname as displayname FROM rcontact r LEFT JOIN img_flag i ON r.username = i.username LEFT JOIN chatroom c ON r.username = c.chatroomname WHERE(type&1 != 0) AND r.type&32 = 0 AND r.type&8 = 0 AND r.verifyFlag&8 = 0   AND (r.username NOT LIKE '%@%' OR (((r.type&1 != 0)  AND r.type&8 = 0 AND r.username LIKE '%@talkroom')) OR (r.type&8 = 0  AND r.username LIKE '%@openim')) AND r.username != 'tmessage' AND r.username != 'officialaccounts' AND r.username != 'helper_entry' AND r.username != 'blogapp' AND r.username != 'weixin' union all SELECT r.username , r.alias as alias , r.nickname , r.conRemark , r.pyInitial, r.quanPin , i.reserved1 , i.reserved2 ,r.showHead ,CASE  WHEN length(r.conRemarkPYFull) > 0  THEN UPPER(r.conRemarkPYFull) ELSE UPPER(r.quanPin) END as PY,CASE   WHEN length(r.conRemark) > 0  THEN UPPER(r.conRemark) ELSE UPPER(r.quanPin) end as byremark, c.memberlist, c.displayname FROM rcontact r  LEFT JOIN img_flag i ON r.username = i.username LEFT JOIN chatroom c ON r.username = c.chatroomname where r.username like '%@chatroom%' ORDER BY r.showHead ASC,PY asc,  byremark asc, quanPin ASC,r.quanPin asc,r.nickname asc,r.username asc) WHERE username = '" + wxId + "'";
			
			c1 = WcdbHolder.excute(findFriendheadandID);
			c1.moveToFirst();
			String username = c1.getString(c1.getColumnIndex("username"));
			String alias = c1.getString(c1.getColumnIndex("alias"));
			String nickname = c1.getString(c1.getColumnIndex("nickname"));
			String reserved2 = c1.getString(c1.getColumnIndex("reserved1"));
			String conRemark = c1.getString(c1.getColumnIndex("conRemark"));
			String memberlist = c1.getString(c1.getColumnIndex("memberlist"));
			String displayname = c1.getString(c1.getColumnIndex("displayname"));
			String pyInitial = c1.getString(c1.getColumnIndex("pyInitial"));
			String quanPin = c1.getString(c1.getColumnIndex("quanPin"));
			userBean = new UserBean(username, alias, nickname, reserved2, conRemark, memberlist, displayname, pyInitial, quanPin);
			//补全信息
			SyncInfoManager.userCompletion(userBean);
			XLog.d(userBean.toString());
			c1.close();
			return userBean;
		} catch (Exception e) {
			c1.close();
			return null;
		}
	}
}
