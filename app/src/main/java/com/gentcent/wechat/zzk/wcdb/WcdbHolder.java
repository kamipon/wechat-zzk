package com.gentcent.wechat.zzk.wcdb;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.gentcent.wechat.zzk.manager.MainManager;
import com.gentcent.wechat.zzk.util.XLog;
import com.gentcent.zzk.xped.XposedHelpers;

import java.util.ArrayList;

/**
 * @author zuozhi
 * @since 2019-07-25
 */
public class WcdbHolder {
	
	public static Cursor excute(String sql) {
		return excute(sql, "EnMicroMsg.db");
	}
	
	public static Cursor excute(String sql, String databaseName) {
		Object obj;
		Class cls = null;
		try {
			cls = MainManager.wxLpparam.classLoader.loadClass("com.tencent.wcdb.database.SQLiteDatabase");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			XLog.e("WcdbHolder:  "+ Log.getStackTraceString(e));
		}
		if (cls == null) {
			return null;
		}
		ArrayList arrayList = (ArrayList) XposedHelpers.callStaticMethod(cls, "getActiveDatabases");
		int i = 0;
		while (true) {
			if (i >= arrayList.size()) {
				obj = null;
				break;
			} else if (XposedHelpers.callMethod(arrayList.get(i), "getPath").toString().contains(databaseName)) {
				obj = arrayList.get(i);
				break;
			} else {
				i++;
			}
		}
		return (Cursor) XposedHelpers.callMethod(obj, "rawQuery", sql, null);
	}
	
	public static void excute(String str, ContentValues contentValues, String str2, String[] strArr) {
		Class cls = null;
		try {
			cls = MainManager.wxLpparam.classLoader.loadClass("com.tencent.wcdb.database.SQLiteDatabase");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			XLog.e("WcdbHolder:  "+ Log.getStackTraceString(e));
		}
		if (cls == null) {
			Toast.makeText(MainManager.activity, "类库没发现", Toast.LENGTH_SHORT).show();
		}
		ArrayList arrayList = (ArrayList) XposedHelpers.callStaticMethod(cls, "getActiveDatabases", new Object[0]);
		Object obj = null;
		int i = 0;
		while (true) {
			if (i >= arrayList.size()) {
				break;
			} else if (XposedHelpers.callMethod(arrayList.get(i), "getPath").toString().contains("EnMicroMsg.db")) {
				obj = arrayList.get(i);
				break;
			} else {
				i++;
			}
		}
		XposedHelpers.callMethod(obj, "update", str, contentValues, str2, strArr);
	}

}
