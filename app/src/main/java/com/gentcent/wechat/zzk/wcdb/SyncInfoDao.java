package com.gentcent.wechat.zzk.wcdb;

import android.database.Cursor;

import com.gentcent.wechat.zzk.bean.SnsContentItemBean;
import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.manager.SnsManager;
import com.gentcent.wechat.zzk.util.ThreadPoolUtils;
import com.gentcent.wechat.zzk.util.XLog;

import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class SyncInfoDao {
	/**
	 * 微信好友信息
	 */
	public static void syncInfo() {
		ThreadPoolUtils.getInstance().run(new Runnable() {
			@Override
			public void run() {
				getRcontact();
				getChatRoom();
				getMessage();
				DecryptPasw.initDbPassword();
				getSnsData();
			}
		});
	}
	
	/**
	 * 获取朋友圈数据
	 */
	private static void getSnsData() {
		List<SnsContentItemBean> selfAllDatas = SnsManager.getSelfAllDatas(MainManager.wxLpparam);
		XLog.d("openWxDb:  " + "自己的朋友圈数据=====================================================================================");
		for (SnsContentItemBean selfAllData : selfAllDatas) {
			XLog.e(selfAllData.toString());
		}
		List<SnsContentItemBean> allDatas = SnsManager.getAllDatas(MainManager.wxLpparam);
		XLog.d("openWxDb:  " + "所有的朋友圈数据=====================================================================================");
		for (SnsContentItemBean allData : allDatas) {
			XLog.e(allData.toString());
		}
	}
	
	/**
	 * 获取好友列表
	 */
	private static void getRcontact() {
		Cursor c1 = null;
		try {
			//查询所有联系人（verifyFlag!=0:公众号等类型，群里面非好友的类型为4，未知类型2）
			c1 = WcdbHolder.excute("select * from rcontact where verifyFlag = 0 and type != 4 and type != 2 and nickname != ''");
			XLog.d("openWxDb:  " + "好友列表分割线=====================================================================================");
			while (c1.moveToNext()) {
				String userName = c1.getString(c1.getColumnIndex("username"));
				String nickName = c1.getString(c1.getColumnIndex("nickname"));
				XLog.e("openWxDb:  " + "userName====" + userName + "    nickName=====" + nickName);
			}
			c1.close();
		} catch (Exception e) {
			c1.close();
			XLog.e("openWxDb:  " + "读取数据库信息失败" + e.toString());
		}
	}
	
	/**
	 * 获取群聊成员列表
	 */
	private static void getChatRoom() {
		Cursor c1 = null;
		try {
			c1 =  WcdbHolder.excute("select * from chatroom ");
			XLog.d("openWxDb:  " + "群组信息记录分割线=====================================================================================");
			while (c1.moveToNext()) {
				String roomowner = c1.getString(c1.getColumnIndex("roomowner"));
				String chatroomname = c1.getString(c1.getColumnIndex("chatroomname"));
				String memberlist = c1.getString(c1.getColumnIndex("memberlist"));
				XLog.e("openWxDb:  " + "群主====" + roomowner + "    群组成员id=====" + memberlist+ "    群id=====" + chatroomname);
			}
			c1.close();
		} catch (Exception e) {
			c1.close();
			XLog.e("openWxDb:  " +  "读取数据库信息失败" + e.toString());
		}
	}
	
	/**
	 * 查询聊天信息
	 *  这里查出的聊天信息包含用户主动删除的信息
	 *  微信的聊天信息删除不是物理删除，所哟只要不卸载仍然可以查到聊天记录
	 */
	private static void getMessage() {
		Cursor c1 = null;
		try {
			//这里只查询文本消息，type=1  图片消息是47，具体信息可以自己测试  http://emoji.qpic.cn/wx_emoji/gV159fHh6rYfCMejCAU1wIoP6eywxFMYjaJiaBzPbSjoc6XlTLoMyKQEh4nswfrX5/ （发送表情连接可以拼接的）
			c1 = WcdbHolder.excute("select * from message where type = 1 ");
			XLog.d("openWxDb:  " +  "聊天记录分割线==================================================================================");
			while (c1.moveToNext()) {
				String talker = c1.getString(c1.getColumnIndex("talker"));
				String content = c1.getString(c1.getColumnIndex("content"));
				String createTime = c1.getString(c1.getColumnIndex("createTime"));
				XLog.e("openWxDb:  " + "聊天对象微信号====" + talker + "    内容=====" + content+ "    时间=====" + createTime);
			}
			c1.close();
		} catch (Exception e) {
			c1.close();
			XLog.e("openWxDb:  " +  "读取数据库信息失败" + e.toString());
		}
	}
}
