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
	private static AddFriendHelper instance;
	private Map<String, String> mRemarkMap;
	
	public static AddFriendHelper getInstance() {
		if (instance == null) {
			synchronized (AddFriendHelper.class) {
				if (instance == null) {
					instance = new AddFriendHelper();
				}
			}
		}
		return instance;
	}
	
	public void writeRemarkMap(String key, String value) {
		try {
			if (this.mRemarkMap == null) {
				this.mRemarkMap = new HashMap();
			}
			if (!this.mRemarkMap.containsKey(key)) {
				this.mRemarkMap.put(key, value);
				MyHelper.writeLine("idTel_json", GsonUtils.GsonString(this.mRemarkMap));
				XLog.d("jjdd add mRemarkMap size is " + this.mRemarkMap.size());
			}
		} catch (Exception e) {
			XLog.e("addRemark error is " + Log.getStackTraceString(e));
		}
	}
	
	public boolean isContainKey(String key) {
		Map<String, String> map = this.mRemarkMap;
		if (map == null || !map.containsKey(key)) {
			return false;
		}
		this.mRemarkMap.remove(key);
		return true;
	}
	
	public String get(String rowKey) {
		String val = "";
		String key = rowKey.replace("_", "");
		try {
			XLog.e("getRemark key is " + key);
			if (ObjectUtils.isNotEmpty(this.mRemarkMap) && this.mRemarkMap.containsKey(key)) {
				val = this.mRemarkMap.get(key);
			}
			if (TextUtils.isEmpty(val)) {
				if (TextUtils.isEmpty(MyHelper.readLine("idTel_json"))) {
					return val;
				}
				this.mRemarkMap = GsonUtils.GsonToType(MyHelper.readLine("idTel_json"), new TypeToken<Map<String, String>>() {
				}.getType());
				XLog.d("jjdd getRemark mRemarkMap size is " + this.mRemarkMap.size());
				if (ObjectUtils.isNotEmpty(this.mRemarkMap) && this.mRemarkMap.containsKey(key)) {
					val = this.mRemarkMap.get(key);
				}
			}
			if (ObjectUtils.isNotEmpty(val)) {
				this.mRemarkMap.remove(key);
			}
		} catch (Exception e) {
			XLog.e("getRemark error is " + Log.getStackTraceString(e));
		}
		return val;
	}
	
	public void remove(String key) {
		if (ObjectUtils.isNotEmpty(this.mRemarkMap) && this.mRemarkMap.containsKey(key)) {
			this.mRemarkMap.remove(key);
			MyHelper.writeLine("idTel_json", GsonUtils.GsonString(this.mRemarkMap));
		}
	}
}
