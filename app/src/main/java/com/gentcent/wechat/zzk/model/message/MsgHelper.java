package com.gentcent.wechat.zzk.model.message;

import com.gentcent.wechat.zzk.bean.UploadBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zuozhi
 * @since 2019-08-15
 */
public class MsgHelper {
	private static MsgHelper instance;
	private Map<Long, UploadBean> upMap;
	
	
	/**
	 * 单例
	 */
	public static MsgHelper getInstance() {
		if (instance == null) {
			synchronized (MsgHelper.class) {
				if (instance == null) {
					instance = new MsgHelper();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 保存信息到Map
	 *
	 * @param msgId      消息ID
	 * @param uploadBean 消息信息
	 */
	public void putMsg(Long msgId, UploadBean uploadBean) {
		if (upMap == null) {
			upMap = new HashMap();
		}
		if (upMap.size() > 100) {
			upMap.clear();
		}
		upMap.put(msgId, uploadBean);
	}
	
	/**
	 * 根据msgId获取保存的信息
	 *
	 * @param msgId 消息ID
	 * @return 保存的信息记录
	 */
	public UploadBean getMsg(Long msgId) {
		if (upMap == null) {
			upMap = new HashMap();
		}
		return upMap.get(msgId);
	}
	
	
}
