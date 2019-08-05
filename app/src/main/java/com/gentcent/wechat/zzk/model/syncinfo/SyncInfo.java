package com.gentcent.wechat.zzk.model.syncinfo;

import android.database.Cursor;
import android.util.Log;

import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.StringUtils;
import com.gentcent.wechat.zzk.bean.UserBean;
import com.gentcent.wechat.zzk.model.sns.bean.SnsContentItemBean;
import com.gentcent.wechat.zzk.MainManager;
import com.gentcent.wechat.zzk.model.sns.SnsHandler;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.wechat.zzk.wcdb.DecryptPasw;
import com.gentcent.wechat.zzk.wcdb.UserDao;
import com.gentcent.wechat.zzk.wcdb.WcdbHolder;
import com.gentcent.zzk.xped.XposedHelpers;

import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class SyncInfo {
	/**
	 * 微信好友信息
	 */
	public static void syncInfo() {
		ThreadPoolUtils.getInstance().run(new Runnable() {
			@Override
			public void run() {
				DecryptPasw.initDbPassword();
				getRcontact();
//				getChatRoom();
//				getMessage();
//				getSnsData();
			}
		});
	}
	
	
	
	/**
	 * 获取好友列表
	 */
	private static void getRcontact() {
		Cursor c1 = null;
		try {
			//查询所有联系人（verifyFlag!=0:公众号等类型，群里面非好友的类型为4，未知类型2）
			String findFriendheadandID = "SELECT r.username as username, r.alias as alias , r.nickname as nickname ,  r.conRemark as conRemark ,  r.pyInitial as pyInitial ,  r.quanPin as quanPin, i.reserved1 as reserved1,  i.reserved2 as reserved2 ,r.showHead as showHead,CASE  WHEN length(r.conRemarkPYFull) > 0  THEN UPPER(r.conRemarkPYFull) ELSE UPPER(r.quanPin) END as PY, CASE   WHEN length(r.conRemark) > 0  THEN UPPER(r.conRemark) ELSE UPPER(r.quanPin) end as byremark,c.memberlist as memberlist,  c.displayname as displayname FROM rcontact r LEFT JOIN img_flag i ON r.username = i.username LEFT JOIN chatroom c ON r.username = c.chatroomname WHERE(type&1 != 0) AND r.type&32 = 0 AND r.type&8 = 0 AND r.verifyFlag&8 = 0   AND (r.username NOT LIKE '%@%' OR (((r.type&1 != 0)  AND r.type&8 = 0 AND r.username LIKE '%@talkroom')) OR (r.type&8 = 0  AND r.username LIKE '%@openim')) AND r.username != 'tmessage' AND r.username != 'officialaccounts' AND r.username != 'helper_entry' AND r.username != 'blogapp' AND r.username != 'weixin' union all SELECT r.username , r.alias as alias , r.nickname , r.conRemark , r.pyInitial, r.quanPin , i.reserved1 , i.reserved2 ,r.showHead ,CASE  WHEN length(r.conRemarkPYFull) > 0  THEN UPPER(r.conRemarkPYFull) ELSE UPPER(r.quanPin) END as PY,CASE   WHEN length(r.conRemark) > 0  THEN UPPER(r.conRemark) ELSE UPPER(r.quanPin) end as byremark, c.memberlist, c.displayname FROM rcontact r  LEFT JOIN img_flag i ON r.username = i.username LEFT JOIN chatroom c ON r.username = c.chatroomname where r.username like '%@chatroom%' ORDER BY r.showHead ASC,PY asc,  byremark asc, quanPin ASC,r.quanPin asc,r.nickname asc,r.username asc";
			
			c1 = WcdbHolder.excute(findFriendheadandID);
			XLog.d("openWxDb:  " + "好友列表分割线=====================================================================================");
			while (c1.moveToNext()) {
				String username = c1.getString(c1.getColumnIndex("username"));
				String alias = c1.getString(c1.getColumnIndex("alias"));
				String nickname = c1.getString(c1.getColumnIndex("nickname"));
				String reserved1 = c1.getString(c1.getColumnIndex("reserved1"));
				String reserved2 = c1.getString(c1.getColumnIndex("reserved2"));
				String conRemark = c1.getString(c1.getColumnIndex("conRemark"));
				String memberlist = c1.getString(c1.getColumnIndex("memberlist"));
				String displayname = c1.getString(c1.getColumnIndex("displayname"));
				String pyInitial = c1.getString(c1.getColumnIndex("pyInitial"));
				String quanPin = c1.getString(c1.getColumnIndex("quanPin"));
				UserBean userBean = new UserBean(username, alias, nickname, reserved1, reserved2, conRemark, memberlist, displayname, pyInitial, quanPin);
				
				//是我自己
				if (StringUtils.equals(username, UserDao.getMyWxid())) {
				
				}
				
				
				XLog.d(userBean.toString());
			}
			c1.close();
		} catch (Exception e) {
			c1.close();
			XLog.e("openWxDb:  " + "读取数据库信息失败" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 获取群聊成员列表
	 */
	private static void getChatRoom() {
		Cursor c1 = null;
		try {
			c1 = WcdbHolder.excute("select * from chatroom ");
			XLog.d("openWxDb:  " + "群组信息记录分割线=====================================================================================");
			while (c1.moveToNext()) {
				String roomowner = c1.getString(c1.getColumnIndex("roomowner"));
				String chatroomname = c1.getString(c1.getColumnIndex("chatroomname"));
				String memberlist = c1.getString(c1.getColumnIndex("memberlist"));
				XLog.e("openWxDb:  " + "群主====" + roomowner + "    群组成员id=====" + memberlist + "    群id=====" + chatroomname);
			}
			c1.close();
		} catch (Exception e) {
			c1.close();
			XLog.e("openWxDb:  " + "读取数据库信息失败" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 查询聊天信息
	 * 这里查出的聊天信息包含用户主动删除的信息
	 * 微信的聊天信息删除不是物理删除，所哟只要不卸载仍然可以查到聊天记录
	 */
	private static void getMessage() {
		Cursor c1 = null;
		try {
			//这里只查询文本消息，type=1  图片消息是47，具体信息可以自己测试  http://emoji.qpic.cn/wx_emoji/gV159fHh6rYfCMejCAU1wIoP6eywxFMYjaJiaBzPbSjoc6XlTLoMyKQEh4nswfrX5/ （发送表情连接可以拼接的）
			c1 = WcdbHolder.excute("select * from message where type = 1 ");
			XLog.d("openWxDb:  " + "聊天记录分割线==================================================================================");
			while (c1.moveToNext()) {
				String talker = c1.getString(c1.getColumnIndex("talker"));
				String content = c1.getString(c1.getColumnIndex("content"));
				String createTime = c1.getString(c1.getColumnIndex("createTime"));
				XLog.e("openWxDb:  " + "聊天对象微信号====" + talker + "    内容=====" + content + "    时间=====" + createTime);
			}
			c1.close();
		} catch (Exception e) {
			c1.close();
			XLog.e("openWxDb:  " + "读取数据库信息失败" + Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 获取朋友圈数据
	 */
	private static void getSnsData() {
		List<SnsContentItemBean> selfAllDatas = SnsHandler.getSelfAllDatas(MainManager.wxLpparam);
		XLog.d("openWxDb:  " + "自己的朋友圈数据=====================================================================================");
		for (SnsContentItemBean selfAllData : selfAllDatas) {
			XLog.e(selfAllData.toString());
		}
		List<SnsContentItemBean> allDatas = SnsHandler.getAllDatas(MainManager.wxLpparam);
		XLog.d("openWxDb:  " + "所有的朋友圈数据=====================================================================================");
		for (SnsContentItemBean allData : allDatas) {
			XLog.e(allData.toString());
		}
	}
}
