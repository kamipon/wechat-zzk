package com.gentcent.wechat.zzk.wcdb;

import android.database.Cursor;

import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.callbacks.XC_LoadPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class SnsDao {
	private final static String TAG = "SnsDao:  ";
	
	/**
	 * 获取朋友圈Rowids
	 */
	public static List<String> getSnsRowids() {
		Cursor a = WcdbHolder.excute("select rowid  from SnsInfo order by SnsInfo.createTime desc ,snsId desc", "SnsMicroMsg.db");
		List<String> list = new ArrayList<>();
		if (a == null) {
			XLog.d(TAG + "getFriendGroupRowids cur is null");
			return list;
		}
		a.moveToFirst();
		while (!a.isAfterLast()) {
			list.add(a.getString(a.getColumnIndex("rowid")));
			a.moveToNext();
		}
		a.close();
		XLog.d(TAG + "getFriendGroupRowids size:" + list.size());
		return list;
	}
	
	/**
	 * 获取自己的朋友圈的RowId
	 */
	public static List<String> getMyFriendGroupRowids() {
		String sql = "select rowid  from SnsInfo  where userName=\"" + UserDao.getMyWxidByWXMethod(MainManager.wxLpparam) + "\"";
		Cursor c1 = WcdbHolder.excute(sql, "SnsMicroMsg.db");
		List<String> list = new ArrayList<>();
		if (c1 == null) {
			XLog.d(TAG + "getMyFriendGroupRowids cur is null");
			return list;
		}
		c1.moveToFirst();
		while (!c1.isAfterLast()) {
			list.add(c1.getString(c1.getColumnIndex("rowid")));
			c1.moveToNext();
		}
		c1.close();
		XLog.d(TAG + "getMyFriendGroupRowids size:" + list.size());
		return list;
	}
	
}
