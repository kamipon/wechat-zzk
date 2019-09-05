package com.gentcent.wechat.zzk.model.friend;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.gentcent.wechat.zzk.util.GsonUtils;
import com.gentcent.wechat.zzk.util.MyHelper;
import com.gentcent.wechat.zzk.util.XLog;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class AddFriendHelper {
	private static AddFriendHelper b;
	private Map<String, String> a;
	
	public static AddFriendHelper a() {
		if (b == null) {
			synchronized (AddFriendHelper.class) {
				if (b == null) {
					b = new AddFriendHelper();
				}
			}
		}
		return b;
	}
	
	public void a(String str, String str2) {
		try {
			if (this.a == null) {
				this.a = new HashMap();
			}
			if (!this.a.containsKey(str)) {
				this.a.put(str, str2);
				MyHelper.writeLine("idTel_json", GsonUtils.GsonString(this.a));
				XLog.d("jjdd add mRemarkMap size is " + this.a.size());
			}
		} catch (Exception e) {
			XLog.e("addRemark error is " + Log.getStackTraceString(e));
		}
	}
	
	public boolean a(String str) {
		Map<String, String> map = this.a;
		if (map == null || !map.containsKey(str)) {
			return false;
		}
		this.a.remove(str);
		return true;
	}
	
	public String b(String str) {
		String str2 = "";
		String key = str.replace("_", "");
		try {
			XLog.e("getRemark key is " + key);
			if (ObjectUtils.isNotEmpty(this.a) && this.a.containsKey(key)) {
				str2 = this.a.get(key);
			}
			if (TextUtils.isEmpty(str2)) {
				if (TextUtils.isEmpty(MyHelper.readLine("idTel_json"))) {
					return str2;
				}
				this.a = GsonUtils.GsonToType(MyHelper.readLine("idTel_json"), new TypeToken<Map<String, String>>() {
				}.getType());
				XLog.d("jjdd getRemark mRemarkMap size is " + this.a.size());
				if (ObjectUtils.isNotEmpty(this.a) && this.a.containsKey(key)) {
					str2 = this.a.get(key);
				}
			}
			if (ObjectUtils.isNotEmpty(str2)) {
				this.a.remove(key);
			}
		} catch (Exception e) {
			XLog.e("getRemark error is " + Log.getStackTraceString(e));
		}
		return str2;
	}
	
	public void c(String str) {
		if (ObjectUtils.isNotEmpty(this.a) && this.a.containsKey(str)) {
			this.a.remove(str);
			MyHelper.writeLine("idTel_json", GsonUtils.GsonString(this.a));
		}
	}
}
